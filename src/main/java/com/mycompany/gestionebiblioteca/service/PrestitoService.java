/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.service;

import com.mycompany.gestionebiblioteca.exceptions.LimiteLibriRaggiuntoException;
import com.mycompany.gestionebiblioteca.model.Prestito;
import com.mycompany.gestionebiblioteca.persistence.PrestitoRepository;
import com.mycompany.gestionebiblioteca.persistence.PrestitoRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * 
 *
 * @author david
 */
public class PrestitoService {
    
    private final PrestitoRepository prestitoRepository;
    private final LibroRepository libroRepository;
    private final UserRepository userRepository;
    
     public PrestitoService(PrestitoRepository prestitoRepository,
            LibroRepository libroRepository,
            UserRepository userRepository) {
       
         
    }

    public Prestito registerLoan(String matricola, String isbn,
            LocalDate loanDate, LocalDate dueDate) {
        validateMatricola(matricola);
        validateIsbn(isbn);
        validateLoanDates(loanDate, dueDate);

       return null;
    }

    public Prestito registerReturn(Prestito loan, LocalDate returnDate) {
        
        return null;
            }

    public List<Prestito> getActiveLoansOrderedByDueDate() {
        return prestitoRepository.findActiveLoansOrderByDueDate();
    }

    public List<Prestito> getActiveLoansByUser(String matricola) {
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
