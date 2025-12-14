/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.service;

import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.model.Loan;
import java.util.List;
import java.util.Optional;
import com.mycompany.gestionebiblioteca.repository.BookRepository;

/**
 *
 * @author david
 */
public class BookService {

    private final BookRepository bookRepository;
    private final LoanService loanService;

    public BookService(BookRepository bookRepository, LoanService loanService) {
        if (bookRepository == null) {
            throw new IllegalArgumentException("BookRepository must not be null");
        }
        if (loanService == null) {
            throw new IllegalArgumentException("LoanService must not be null");
        }
        this.bookRepository = bookRepository;
        this.loanService = loanService;
    }

    public Book addBook(String isbn, String title, String author, int year, int totalCopies) {
        // validazione formale (solo sui campi)
        validateIsbn(isbn);
        validateTotalCopies(totalCopies);
        validateYear(year);

        /*
            logica di validazione sostanziale,
            coinvolge altri domini/entità oppure
            letture sul file: non si può inserire
            un libro con isbn esistente
         */
        if (bookRepository.existsById(isbn)) {
            throw new ValidationException("Libro con  ISBN " + isbn + " esiste già");
        }
        // CONTROLLO DUPLICATI 
        // Recupero tutti i libri attuali
        List<Book> allBooks = bookRepository.findAll();

        // Normalizzo le stringhe in input per il confronto (rimuovo spazi extra)
        String newTitle = title.trim();
        String newAuthor = author.trim();

        // Ciclo classico "for-each"
        for (Book existingBook : allBooks) {

            //  Confronto Titolo (Case Insensitive)
            boolean sameTitle = existingBook.getTitle().equalsIgnoreCase(newTitle);

            //  Confronto Autore (Case Insensitive)
            boolean sameAuthor = existingBook.getAuthor().equalsIgnoreCase(newAuthor);

            //  Confronto Anno
            boolean sameYear = existingBook.getYear() == year;

            //   Controllo per evitare che vengano creati due libri uguali semanticamente
            // Se Titolo e Autore coincidono, controlliamo l'anno
            if (sameTitle && sameAuthor) {

                int existingYear = existingBook.getYear();

                if (existingYear == year) {
                    //  Anno identico -> È un duplicato vero e proprio. BLOCCA.
                    throw new ValidationException(
                            "Esiste già questo libro (stesso Titolo, Autore e Anno: " + existingYear + ")."
                    );
                }

                //  Anno diverso (es. Nuova Edizione) -> il sistema accetta.
                // Il ciclo continua, non lanciamo eccezioni.
                // Esempio: "Manuale Java" (2020) esiste. Inserisco "Manuale Java" (2025).
                // 2020 == 2025 è false -> OK.
            }
        }

        // LOGICA:
        // Quando creo un libro NUOVO, nessuno l'ha ancora preso in prestito.
        // Quindi: Totale = Disponibili.
        int initialTotal = totalCopies;
        int initialAvailable = totalCopies;

        Book book = new Book(isbn, title, author, year, initialTotal, initialAvailable);
        return bookRepository.save(book);
    }

    public Book updateBook(String oldIsbn, String newIsbn, String title, String author, Integer publicationYear, Integer totalCopies) {

        validateIsbn(newIsbn);
        validateTotalCopies(totalCopies);
        validateYear(publicationYear);

        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Il titolo non può essere vuoto");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new ValidationException("L'autore non può essere vuoto");
        }
        if (publicationYear == null) {
            throw new ValidationException("Anno non valido");
        }

        Book existing = bookRepository.findByIsbn(oldIsbn)
                .orElseThrow(() -> new NotFoundException("Libro non trovato con ISBN " + oldIsbn));

        // conteggio dei libri in prestito attualmente
        // Recupero quanti sono fuori in questo momento
        int currentlyLoaned = loanService.countActiveLoansByIsbn(oldIsbn);

        // Calcolo le nuove disponibili: Totale inserito - Quelli fuori
        int newAvailableCopies = totalCopies - currentlyLoaned;

        validateTotalCopies(newAvailableCopies); //validazione delle copie totali (maggiori di 0)

        // se cambia ISBN devo creare un nuovo oggetto Book e cancellare il precedente (Delete & Insert)
        boolean isbnChanged = !oldIsbn.equals(newIsbn);

        if (isbnChanged) {
            // Se ci sono prestiti attivi, NON permetto di cambiare l'ISBN 
            // romperebbe il collegamento con i prestiti esistenti nell'archivio

            if (currentlyLoaned > 0) {
                throw new ValidationException("Impossibile modificare l'ISBN: ci sono " + currentlyLoaned + " copie ancora in prestito.");
            }

            if (bookRepository.existsById(newIsbn)) {
                throw new ValidationException("Un libro con ISBN " + newIsbn + " esiste già.");
            }

            // Creo nuovo libro con le nuove specifiche
            Book newBook = new Book(newIsbn, title, author, publicationYear, totalCopies, newAvailableCopies);

            bookRepository.deleteById(existing.getIsbn());
            return bookRepository.save(newBook);

        } else {
            // Update Standard (ISBN invariato)
            existing.setTitle(title);
            existing.setAuthor(author);
            existing.setYear(publicationYear);

            // Aggiorno i contatori calcolati
            existing.setAvailableCopies(newAvailableCopies);
            existing.setTotalCopies(totalCopies);

            return bookRepository.save(existing);
        }
    }

    public void deleteBook(String isbn) {
        //Validazione input
        validateIsbn(isbn);

        if (!bookRepository.existsById(isbn)) {
            throw new NotFoundException("Libro non trovato con ISBN " + isbn);
        }

        //verifico che non ci sia attualmente in prestito una copia del libro da rimuovere dal sistema
        List<Loan> activeLoans = loanService.getActiveLoansOrderedByDueDate();

        //controllo se nella lista c'è un prestito che riguarda questo ISBN
        boolean isBorrowed = activeLoans.stream()
                .anyMatch(loan -> loan.getBookIsbn().equals(isbn));

        if (isBorrowed) {
            throw new ValidationException("Impossibile eliminare il libro (" + isbn
                    + "): risulta attualmente in prestito.");
        }

        bookRepository.deleteById(isbn);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return Optional.empty();
        }
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> searchByTitle(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    public List<Book> searchByAuthor(String keyword) {
        return bookRepository.findByAuthorContaining(keyword);
    }

    public List<Book> getAllBooksOrderedByTitle() {
        return bookRepository.findAllOrderByTitle();
    }

    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN must not be blank");
        }
        if (!isbn.matches("\\d{13}")) {
            throw new ValidationException("ISBN must be exactly 13 digits");
        }
    }

    private void validateTotalCopies(int totalCopies) {
        if (totalCopies < 1) {
            throw new ValidationException("totalCopies must be at least 1");
        }
    }

    private void validateYear(int year) {
        if (year <= 0 || year > 2025) {
            throw new ValidationException("L'anno di pubblicazione deve essere compreso fra 1 e 2025");
        }
    }

}
