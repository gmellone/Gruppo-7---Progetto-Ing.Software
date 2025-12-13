/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.service;

import com.mycompany.gestionebiblioteca.exceptions.LimiteLibriRaggiuntoException;
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
public class PrestitoService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public PrestitoService(LoanRepository prestitoRepository,
            BookRepository libroRepository,
            UserRepository userRepository) {

        this.loanRepository = prestitoRepository;
        this.bookRepository = libroRepository;
        this.userRepository = userRepository;

    }

    public Loan registerLoan(String matricola, String isbn,
            LocalDate loanDate, LocalDate dueDate) {
        validateMatricola(matricola);
        validateIsbn(isbn);
        validateLoanDates(loanDate, dueDate);

        return null;
    }

    public Loan registerReturn(Loan loan, LocalDate returnDate) {

        return null;
    }

    public List<Loan> getActiveLoansOrderedByDueDate() {
        return loanRepository.findActiveLoansOrderByDueDate();
    }

    public List<Loan> getActiveLoansByUser(String matricola) {
        return null;
    }

    public long countActiveLoansForUser(String matricola) {
        return 0;
    }

    private void validateMatricola(String matricola) {

    }

    private void validateIsbn(String isbn) {

    }

    private void validateLoanDates(LocalDate loanDate, LocalDate dueDate) {

    }

}
