/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author Giovanni
 */

public class Loan {

    private final String userMatricola;
    private final String bookIsbn;
    private final LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String userMatricola, String bookIsbn, LocalDate loanDate,
        LocalDate dueDate, LocalDate returnDate) {
        this.userMatricola = Objects.requireNonNull(userMatricola);
        this.bookIsbn = Objects.requireNonNull(bookIsbn);
        this.loanDate = Objects.requireNonNull(loanDate);
        this.dueDate = Objects.requireNonNull(dueDate);
        this.returnDate = returnDate;
    }

    //GETTER
    public String getUserMatricola() { return userMatricola; }
    public String getBookIsbn() { return bookIsbn; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    //SETTER
    public void setDueDate(LocalDate dueDate) { this.dueDate = Objects.requireNonNull(dueDate); }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isActive() {
        return returnDate == null;
    }

    public boolean isOverdue(LocalDate today) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Loan loan = (Loan) o;
        return userMatricola.equals(loan.userMatricola)
                && bookIsbn.equals(loan.bookIsbn)
                && loanDate.equals(loan.loanDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userMatricola, bookIsbn, loanDate);
    }
}
