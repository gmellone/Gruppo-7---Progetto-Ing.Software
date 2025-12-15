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

/**
 * @brief Implementazione in memoria del repository per la gestione dei prestiti (Loan).
 *
 * Questa classe gestisce la persistenza volatile delle transazioni di prestito.
 *
 * Aspetti architetturali chiave:
 * - **Chiave Primaria Composta**: Poiché l'oggetto Loan è identificato da una terna (Utente, Libro, Data),
 * questo repository genera una chiave sintetica (Stringa) concatenando questi valori per l'inserimento nella HashMap.
 */
public class InMemoryLoanRepository implements LoanRepository {

    /**
     * @brief Storage interno.
     * Mappa l'ID sintetico (String) all'oggetto Loan.
     */
    private final Map<String, Loan> storage = new HashMap<>();

    /**
     * @brief Genera l'ID univoco per la memorizzazione nella mappa.
     *
     * Combina matricola, ISBN e data di inizio per creare una chiave univoca.
     * Formato: "MATRICOLA:ISBN:YYYY-MM-DD"
     *
     * @param loan Il prestito da cui estrarre le informazioni.
     * @return Una stringa univoca che rappresenta la chiave primaria del prestito.
     */
    private String buildId(Loan loan) {
        return loan.getUserMatricola() + ":" + loan.getBookIsbn() + ":" + loan.getLoanDate();
    }

    /**
     * @brief Salva o aggiorna un prestito.
     *
     * @param entity Il prestito da salvare.
     * @return L'istanza salvata.
     * @throws IllegalArgumentException Se l'entità passata è null.
     */
    @Override
    public Loan save(Loan entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Prestito non deve essere null");
        }
        String id = buildId(entity);
        storage.put(id, entity);
        return entity;
    }

    /**
     * @brief Cerca un prestito tramite la sua chiave sintetica.
     *
     * @param id La stringa identificativa (formato "Matricola:ISBN:Data").
     * @return Optional contenente il prestito se trovato.
     */
    @Override
    public Optional<Loan> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * @brief Restituisce tutti i prestiti (storico completo).
     * @return Lista di tutti i prestiti presenti in memoria.
     */
    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * @brief Rimuove un prestito specifico.
     * @param id La chiave identificativa del prestito.
     */
    @Override
    public void deleteById(String id) {
        if (id == null) {
            return;
        }
        storage.remove(id);
    }

    /**
     * @brief Svuota l'intero archivio prestiti.
     */
    @Override
    public void deleteAll() {
        storage.clear();
    }

    /**
     * @brief Verifica l'esistenza di un prestito tramite ID.
     * @param id La chiave da cercare.
     * @return true se esiste, false altrimenti.
     */
    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    /**
     * @brief Conta il numero totale di transazioni (attive e chiuse).
     * @return Il totale dei record.
     */
    @Override
    public long count() {
        return storage.size();
    }

    /**
     * @brief Trova tutto lo storico dei prestiti di un determinato utente.
     *
     * Include sia i libri attualmente in prestito che quelli già restituiti.
     *
     * @param matricola La matricola dell'utente.
     * @return Lista dei prestiti associati all'utente.
     */
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

    /**
     * @brief Trova tutto lo storico dei prestiti di un determinato libro.
     *
     * @param isbn L'ISBN del libro.
     * @return Lista dei prestiti che hanno coinvolto quel libro.
     */
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

    /**
     * @brief Recupera tutti i prestiti attualmente attivi nel sistema.
     *
     * Filtra i prestiti che non hanno ancora una data di restituzione (isActive == true).
     *
     * Criterio di ordinamento:
     * 1. Data di Scadenza (i più urgenti prima).
     * 2. Matricola Utente.
     * 3. ISBN Libro.
     *
     * @return Lista ordinata dei prestiti attivi.
     */
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

    /**
     * @brief Recupera i prestiti attivi di un utente specifico.
     *
     * Utile per verificare se l'utente ha raggiunto il limite di libri o ha ritardi.
     *
     * Criterio di ordinamento:
     * 1. Data di Scadenza.
     * 2. ISBN Libro.
     *
     * @param matricola La matricola dell'utente.
     * @return Lista dei prestiti in corso per quell'utente.
     */
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