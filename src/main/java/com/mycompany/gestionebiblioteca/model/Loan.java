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

/**
 * @brief Rappresenta l'entità "Prestito" (Loan) nel sistema bibliotecario.
 *
 * Questa classe funge da "Classe di Associazione" tra un Utente e un Libro.
 * Gestisce il ciclo di vita del prestito, dalla creazione (data inizio) alla chiusura (data restituzione),
 * monitorando le scadenze.
 *
 * N.B.: Un prestito è identificato univocamente dalla terna
 * {Matricola Utente, ISBN Libro, Data Inizio}.
 * N.B.: Lo stato (Attivo/Concluso) è determinato dalla presenza della data di restituzione.
 */
public class Loan {

    /**
     * @brief Matricola dell'utente che ha effettuato il prestito.
     */
    private final String userMatricola;

    /**
     * @brief ISBN del libro oggetto del prestito.
     */
    private final String bookIsbn;

    /**
     * @brief Data di inizio del prestito.
     */
    private final LocalDate loanDate;

    /**
     * @brief Data prevista per la restituzione (Scadenza).
     */
    private LocalDate dueDate;

    /**
     * @brief Data di effettiva restituzione del libro.
     */
    private LocalDate returnDate;

    /**
     * @brief Costruttore completo per la creazione di un prestito.
     *
     * Inizializza l'oggetto validando che i campi obbligatori non siano nulli.
     *
     * @param userMatricola Matricola dell'utente (Non null).
     * @param bookIsbn ISBN del libro (Non null).
     * @param loanDate Data di registrazione del prestito (Non null).
     * @param dueDate Data di scadenza prevista (Non null).
     * @param returnDate Data di restituzione (può essere null se il prestito è appena iniziato).
     * @throws NullPointerException Se userMatricola, bookIsbn, loanDate o dueDate sono null.
     */
    public Loan(String userMatricola, String bookIsbn, LocalDate loanDate,
        LocalDate dueDate, LocalDate returnDate) {
        this.userMatricola = Objects.requireNonNull(userMatricola, "La matricola utente è obbligatoria");
        this.bookIsbn = Objects.requireNonNull(bookIsbn, "L'ISBN del libro è obbligatorio");
        this.loanDate = Objects.requireNonNull(loanDate, "La data di inizio è obbligatoria");
        this.dueDate = Objects.requireNonNull(dueDate, "La data di scadenza è obbligatoria");
        this.returnDate = returnDate;
    }

    //GETTR

    /**
     * @brief Restituisce la matricola dell'utente.
     * @return Stringa identificativa dell'utente.
     */
    public String getUserMatricola() { return userMatricola; }

    /**
     * @brief Restituisce l'ISBN del libro.
     * @return Stringa identificativa del libro.
     */
    public String getBookIsbn() { return bookIsbn; }

    /**
     * @brief Restituisce la data di inizio prestito.
     * @return Oggetto LocalDate.
     */
    public LocalDate getLoanDate() { return loanDate; }

    /**
     * @brief Restituisce la data di scadenza prevista.
     * @return Oggetto LocalDate.
     */
    public LocalDate getDueDate() { return dueDate; }

    /**
     * @brief Restituisce la data di effettiva restituzione.
     * @return Oggetto LocalDate o null se non ancora restituito.
     */
    public LocalDate getReturnDate() { return returnDate; }

    //SETTER

    /**
     * @brief Aggiorna la data di scadenza.
     * @param dueDate La nuova data di scadenza (Non null).
     */
    public void setDueDate(LocalDate dueDate) { this.dueDate = Objects.requireNonNull(dueDate); }

    /**
     * @brief Registra la data di restituzione.
     * @param returnDate La data di restituzione (può essere null per riaprire il prestito, se necessario).
     */
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    /**
     * @brief Verifica se il prestito è attualmente attivo.
     *
     * Un prestito è attivo se il libro non è ancora stato restituito (returnDate è null).
     *
     * @return true se il prestito è in corso, false se è concluso.
     */
    public boolean isActive() {
        return returnDate == null;
    }

    /**
     * @brief Verifica se il prestito è in ritardo (Overdue).
     *
     * Un prestito è in ritardo se:
     * - È ancora attivo (non restituito).
     * - La data di riferimento (oggi) è successiva alla data di scadenza (dueDate).
     *
     * @param today La data rispetto alla quale verificare il ritardo (LocalDate.now()).
     * @return true se il prestito è scaduto, false altrimenti.
     * @throws NullPointerException Se il parametro today è null.
     */
    public boolean isOverdue(LocalDate today) {
        Objects.requireNonNull(today, "La data di riferimento non può essere null");
        return isActive() && today.isAfter(dueDate);
    }

    // --- METODI DI IDENTITÀ ---

    /**
     * @brief Confronta questo prestito con un altro oggetto.
     *
     * L'uguaglianza è strutturale e basata sulla chiave composta:
     * **Matricola + ISBN + Data Inizio**.
     * Due prestiti sono uguali se riguardano le stesse entità nello stesso giorno.
     *
     * @param o L'oggetto da confrontare.
     * @return true se identici per chiave, false altrimenti.
     */
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

    /**
     * @brief Genera l'hash code del prestito.
     * Calcolato sulla base dei campi chiave (userMatricola, bookIsbn, loanDate).
     *
     * @return Intero hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userMatricola, bookIsbn, loanDate);
    }
}