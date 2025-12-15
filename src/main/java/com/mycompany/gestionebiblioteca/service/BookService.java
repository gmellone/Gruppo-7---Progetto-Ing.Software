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
import java.util.stream.Collectors;

/**
 *
 * @author david
 */
/**
 * @brief Service che gestisce la logica di business relativa ai libri.
 *
 * Questa classe agisce come livello intermedio tra l'interfaccia utente (o
 * controller) e il repository. Si occupa delle validazioni formali e
 * sostanziali, della gestione dei duplicati semantici e garantisce la coerenza
 * tra il numero totale di copie e quelle effettivamente disponibili in base ai
 * prestiti attivi.
 */
public class BookService {

    private final BookRepository bookRepository;
    private final LoanService loanService;

    /**
     * @brief Costruttore con iniezione delle dipendenze.
     *
     * @param bookRepository Repository per l'accesso ai dati dei libri.
     * @param loanService Service per verificare lo stato dei prestiti correlati
     * ai libri.
     * @throws IllegalArgumentException Se uno dei parametri è null.
     */
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

    /**
     * @brief Aggiunge un nuovo libro al catalogo.
     *
     * Esegue la validazione dei campi e verifica che non esista già un libro
     * con lo stesso ISBN. Effettua inoltre un controllo sui "duplicati
     * semantici": impedisce l'inserimento se esiste già un libro con identico
     * Titolo, Autore e Anno di pubblicazione. All'inserimento, le copie
     * disponibili vengono inizializzate pari alle copie totali.
     *
     * @param isbn Codice ISBN univoco (deve essere di 13 cifre).
     * @param title Titolo del libro.
     * @param authors Autori/Autore del libro.
     * @param year Anno di pubblicazione (1 - 2025).
     * @param totalCopies Numero totale di copie possedute dalla biblioteca.
     * @return L'istanza del libro salvata nel database.
     * @throws ValidationException Se i dati non sono validi o se il libro è un
     * duplicato.
     */
    public Book addBook(String isbn, String title, List<String> authors, int year, int totalCopies) {
        // Validazione formale
        validateIsbn(isbn);
        validateTotalCopies(totalCopies);
        validateYear(year);
        validateAuthors(authors);

        // Controllo univocità ISBN (File)
        if (bookRepository.existsById(isbn)) {
            throw new ValidationException("Libro con ISBN " + isbn + " esiste già");
        }
        String newTitle = title.trim();

        // Pulisco la lista autori in input (rimuovo spazi e vuoti)
        List<String> newAuthors = authors.stream()
                .map(String::trim)
                .filter(a -> !a.isEmpty())
                .collect(Collectors.toList());

        //CONTROLLO DUPLICATI SEMANTICI
        List<Book> allBooks = bookRepository.findAll();

        for (Book existingBook : allBooks) {

            // Confronto Titolo (Case Insensitive)
            boolean sameTitle = existingBook.getTitle().equalsIgnoreCase(newTitle);

            // Confronto Autori (Uso un metodo helper per gestire liste e case-insensitive)
            boolean sameAuthors = areAuthorsEqual(existingBook.getAuthors(), newAuthors);

            // Confronto Anno
            boolean sameYear = existingBook.getYear() == year;

            // Se Titolo e Autori coincidono e anche l'anno coincide, è un duplicato
            if (sameTitle && sameAuthors) {
                if (sameYear) {
                    throw new ValidationException(
                            "Esiste già questo libro (stesso Titolo, Autori e Anno: " + year + ")."
                    );
                }
                // Se l'anno è diverso, il ciclo continua (è una nuova edizione del libro).
            }
        }
        // creazione e salvataggio
        // Quando creo un libro nuovo, disponibili = totali
        int initialAvailable = totalCopies;

        // Passo 'newAuthors' (la lista pulita) al costruttore
        Book book = new Book(isbn, newTitle, newAuthors, year, totalCopies, initialAvailable);

        return bookRepository.save(book);
    }

    //METODO HELPER 
    // Serve per confrontare due liste di autori ignorando maiuscole/minuscole
    private boolean areAuthorsEqual(List<String> list1, List<String> list2) {
        if (list1 == null || list2 == null) {
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }

        // Converto entrambe le liste in minuscolo e le ordino per essere sicuro del confronto
        List<String> l1Sorted = list1.stream().map(String::toLowerCase).sorted().collect(Collectors.toList());
        List<String> l2Sorted = list2.stream().map(String::toLowerCase).sorted().collect(Collectors.toList());

        return l1Sorted.equals(l2Sorted);
    }

    /**
     * @brief Aggiorna i dati di un libro esistente.
     *
     * Gestisce la logica complessa di aggiornamento: - Ricalcola le copie
     * disponibili sottraendo i prestiti attivi dalle nuove copie totali. -
     * Gestisce la modifica dell'ISBN: se l'ISBN cambia, viene eseguita una
     * cancellazione e un nuovo inserimento. Questa operazione è bloccata se ci
     * sono prestiti attivi per evitare inconsistenze storiche.
     *
     * @param oldIsbn L'ISBN attuale del libro da modificare.
     * @param newIsbn Il nuovo ISBN (può coincidere con oldIsbn).
     * @param title Nuovo titolo.
     * @param author Nuovo autore.
     * @param publicationYear Nuovo anno di pubblicazione.
     * @param totalCopies Nuovo numero totale di copie.
     * @return L'istanza del libro aggiornato.
     * @throws NotFoundException Se il libro con oldIsbn non esiste.
     * @throws ValidationException Se i nuovi dati non sono validi o se il
     * cambio ISBN è bloccato da prestiti attivi.
     */
    public Book updateBook(String oldIsbn, String newIsbn, String title, List<String> authors, Integer publicationYear, Integer totalCopies) {

        validateIsbn(newIsbn);
        validateTotalCopies(totalCopies);
        validateYear(publicationYear);
        validateAuthors(authors);

        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Il titolo non può essere vuoto");
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
            Book newBook = new Book(newIsbn, title, authors, publicationYear, totalCopies, newAvailableCopies);

            bookRepository.deleteById(existing.getIsbn());
            return bookRepository.save(newBook);

        } else {
            // Update Standard (ISBN invariato)
            existing.setTitle(title);
            existing.setAuthors(authors);
            existing.setYear(publicationYear);

            // Aggiorno i contatori calcolati
            existing.setAvailableCopies(newAvailableCopies);
            existing.setTotalCopies(totalCopies);

            return bookRepository.save(existing);
        }
    }

    /**
     * @brief Rimuove un libro dal sistema.
     *
     * L'eliminazione viene impedita se risulta esserci anche solo una copia del
     * libro attualmente in prestito, per preservare l'integrità referenziale
     * dei prestiti attivi.
     *
     * @param isbn Codice ISBN del libro da eliminare.
     * @throws NotFoundException Se il libro non viene trovato.
     * @throws ValidationException Se il libro è attualmente in prestito.
     */
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

    /**
     * @brief Recupera un libro tramite il suo ISBN.
     *
     * @param isbn L'ISBN da cercare.
     * @return Un Optional contenente il libro se trovato, altrimenti vuoto.
     */
    public Optional<Book> getBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return Optional.empty();
        }
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * @brief Cerca libri il cui titolo contiene la parola chiave specificata.
     *
     * @param keyword Parola chiave per la ricerca.
     * @return Lista di libri corrispondenti.
     */
    public List<Book> searchByTitle(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    /**
     * @brief Cerca libri il cui autore contiene la parola chiave specificata.
     *
     * @param keyword Parola chiave per la ricerca.
     * @return Lista di libri corrispondenti.
     */
    public List<Book> searchByAuthor(String keyword) {
        return bookRepository.findByAuthorContaining(keyword);
    }

    /**
     * @brief Restituisce tutti i libri presenti nel catalogo ordinati per
     * titolo.
     *
     * @return Lista completa dei libri ordinata alfabeticamente per titolo.
     */
    public List<Book> getAllBooksOrderedByTitle() {
        return bookRepository.findAllOrderByTitle();
    }

    // 
    /**
     * @brief Valida il formato dell'ISBN.
     * @details Controlla che non sia nullo, vuoto e che sia composto
     * esattamente da 13 cifre.
     * @param isbn La stringa ISBN da validare.
     * @throws ValidationException Se il formato non è corretto.
     */
    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN must not be blank");
        }
        if (!isbn.matches("\\d{13}")) {
            throw new ValidationException("ISBN must be exactly 13 digits");
        }
    }

    /**
     * @brief Valida il numero di copie totali.
     * @param totalCopies Numero di copie da verificare.
     * @throws ValidationException Se il numero è inferiore a 1.
     */
    private void validateTotalCopies(int totalCopies) {
        if (totalCopies < 1) {
            throw new ValidationException("totalCopies must be at least 1");
        }
    }

    /**
     * @brief Valida l'anno di pubblicazione.
     * @param year Anno da verificare.
     * @throws ValidationException Se l'anno non è compreso tra 1 e 2025.
     */
    private void validateYear(int year) {
        if (year <= 0 || year > 2025) {
            throw new ValidationException("L'anno di pubblicazione deve essere compreso fra 1 e 2025");
        }
    }

    private void validateAuthors(List<String> authors) {
        if (authors == null || authors.isEmpty()) {
            throw new ValidationException("Almeno un autore deve essere specificato");
        }
        for (String author : authors) {
            if (author == null || author.trim().isEmpty()) {
                throw new ValidationException("Il nome dell'autore non può essere vuoto");
            }
        }
    }
}
