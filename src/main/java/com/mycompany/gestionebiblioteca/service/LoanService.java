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
import com.mycompany.gestionebiblioteca.repository.BookRepository;
import com.mycompany.gestionebiblioteca.repository.LoanRepository;
import com.mycompany.gestionebiblioteca.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

/**
 *
 *
 * @author david
 */
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository,
            BookRepository bookRepository,
            UserRepository userRepository) {
        if (loanRepository == null) {
            throw new IllegalArgumentException("loanRepository non deve essere null");
        }
        if (bookRepository == null) {
            throw new IllegalArgumentException("bookRepository non deve essere null");
        }
        if (userRepository == null) {
            throw new IllegalArgumentException("userRepository non deve essere null");
        }
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Loan registerLoan(String matricola, String isbn,
            LocalDate loanDate, LocalDate dueDate) {
        
        validateMatricola(matricola);
        validateIsbn(isbn);
        validateLoanDates(loanDate, dueDate);

        if (!userRepository.existsById(matricola)) {
            throw new NotFoundException("Utente non trovato con matricola " + matricola);
        }

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new NotFoundException("Libro non trovato con ISBN " + isbn));

        if (book.getAvailableCopies() <= 0) {
            throw new ValidationException("Non ci sono copie disponibili del libro con ISBN " + isbn);
        }

        List<Loan> activeLoansForUser = loanRepository.findActiveLoansByUser(matricola);

        if (activeLoansForUser.size() >= 3) {
            throw new ValidationException("L'utente con matricola " + matricola + " ha già 3 prestiti attivi (limite raggiunto)");
        }

        activeLoansForUser.stream().filter(activeLoan -> (activeLoan.getBookIsbn().equals(isbn))).forEachOrdered(_item -> {
            throw new ValidationException("L'utente ha già in prestito una copia del libro: " + book.getTitle());
        });

        Loan loan = new Loan(matricola, isbn, loanDate, dueDate, null);
        Loan saved = loanRepository.save(loan);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return saved;
    }
    
    public Loan registerReturn(Loan loan, LocalDate returnDate) {
        if (loan == null) {
            throw new IllegalArgumentException("il prestito deve essere null");
        }
        if (returnDate == null) {
            throw new ValidationException("la data di restituzione deve essere null");
        }
        if (!loan.isActive()) {
            throw new ValidationException("il prestito è stato già restituito");
        }
        if (returnDate.isBefore(loan.getLoanDate())) {
            throw new ValidationException("la data di ritorno non può essere inferiore alla data odierna");
        }

        loan.setReturnDate(returnDate);
        Loan updated = loanRepository.save(loan);

        Book book = bookRepository.findById(loan.getBookIsbn())
                .orElseThrow(() -> new NotFoundException("Libro non trovato con ISBN " + loan.getBookIsbn()));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return updated;
    }

    public List<Loan> getActiveLoansOrderedByDueDate() {
        return loanRepository.findActiveLoansOrderByDueDate();
    }

    public List<Loan> getActiveLoansByUser(String matricola) {
        return loanRepository.findActiveLoansByUser(matricola);
    }

    public long countActiveLoansForUser(String matricola) {
        return loanRepository.findActiveLoansByUser(matricola).size();
    }

    private void validateMatricola(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            throw new ValidationException("La matricola non deve essere vuota");
        }
        if (!matricola.matches("^\\d{10}$")) {
            throw new ValidationException("La matricola non deve essere di dieci cifre ");
        }
    }

    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN non deve essere vuoto");
        }
        if (!isbn.matches("^\\d{13}$")) {
            throw new ValidationException("ISBN deve essere esattamente di 13 cifre");
        }
    }

    private void validateLoanDates(LocalDate loanDate, LocalDate dueDate) {
        if (loanDate == null) {
            throw new ValidationException("loanDate non deve essere nulla");
        }
        if (dueDate == null) {
            throw new ValidationException("dueDate non deve essere nulla");
        }
        if (dueDate.isBefore(loanDate)) {
            throw new ValidationException("dueDate non può essere inserita prima  loanDate");
        }
    }
    
    public int countActiveLoansByIsbn(String isbn) {
        if (isbn == null) return 0;
        // Recupera tutti i prestiti di quel libro e conta quelli senza data di restituzione
         return (int) loanRepository.findByBookIsbn(isbn).stream()
            .filter(loan -> loan.getReturnDate() == null) // Solo quelli attivi
            .count();
    }
}
