/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.repository;

/**
 *
 * @author Giovanni
 */

import com.mycompany.gestionebiblioteca.model.Loan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryLoanRepository implements LoanRepository {

    private final Map<String, Loan> storage = new HashMap<>();

    private String buildId(Loan loan) {
        return loan.getUserMatricola() + ":" + loan.getBookIsbn() + ":" + loan.getLoanDate();
    }

    @Override
    public Loan save(Loan entity) {
        if (entity == null) {
            throw new IllegalArgumentException("loan must not be null");
        }
        String id = buildId(entity);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<Loan> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(String id) {
        if (id == null) {
            return;
        }
        storage.remove(id);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public List<Loan> findByUserMatricola(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Loan> result = new ArrayList<>();
        for (Loan loan : storage.values()) {
            if (matricola.equals(loan.getUserMatricola())) {
                result.add(loan);
            }
        }
        return result;
    }

    @Override
    public List<Loan> findByBookIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Loan> result = new ArrayList<>();
        for (Loan loan : storage.values()) {
            if (isbn.equals(loan.getBookIsbn())) {
                result.add(loan);
            }
        }
        return result;
    }

    @Override
    public List<Loan> findActiveLoansOrderByDueDate() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : storage.values()) {
            if (loan.isActive()) {
                result.add(loan);
            }
        }
        result.sort(Comparator
                .comparing(Loan::getDueDate, Comparator.nullsLast(LocalDate::compareTo))
                .thenComparing(Loan::getUserMatricola)
                .thenComparing(Loan::getBookIsbn));
        return result;
    }

    @Override
    public List<Loan> findActiveLoansByUser(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Loan> result = new ArrayList<>();
        for (Loan loan : storage.values()) {
            if (loan.isActive() && matricola.equals(loan.getUserMatricola())) {
                result.add(loan);
            }
        }
        result.sort(Comparator
                .comparing(Loan::getDueDate, Comparator.nullsLast(LocalDate::compareTo))
                .thenComparing(Loan::getBookIsbn));
        return result;
    }
}

