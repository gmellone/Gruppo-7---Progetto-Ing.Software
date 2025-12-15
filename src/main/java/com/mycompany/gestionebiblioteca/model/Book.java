/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.model;

/**
 *
 * @author Giovanni
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief Rappresenta un libro all'interno del sistema bibliotecario.
 *
 * La classe Book funge da entità nel modello dati (Model).
 * Contiene le informazioni anagrafiche del libro (ISBN, titolo, autori, anno)
 * e gestisce il conteggio delle copie totali e disponibili.
 *
 * L'uguaglianza tra due libri è determinata esclusivamente dal codice ISBN.
 */
public class Book {

    /**
     * @brief Il codice ISBN univoco del libro.
     */
    private String isbn;

    /**
     * @brief Il titolo del libro.
     */
    private String title;

    /**
     * @brief Lista degli autori del libro.
     */
    private List<String> authors;

    /**
     * @brief L'anno di pubblicazione del libro.
     */
    private int year;

    /**
     * @brief Il numero totale di copie possedute dalla biblioteca.
     */
    private int totalCopies;

    /**
     * @brief Il numero di copie attualmente disponibili per il prestito.
     */
    private int availableCopies;

    /**
     * @brief Costruttore della classe Book.
     *
     * Inizializza un nuovo oggetto Book.
     * Gestisce la lista degli autori creando una nuova istanza (copia difensiva)
     * per evitare riferimenti esterni o liste nulle.
     *
     * @param isbn Il codice univoco ISBN del libro (non può essere null).
     * @param title Il titolo del libro.
     * @param authors Lista degli autori. Se null, viene inizializzata una lista vuota.
     * @param year L'anno di pubblicazione.
     * @param totalCopies Il numero totale di copie fisiche.
     * @param availableCopies Il numero di copie disponibili al momento della creazione.
     * @throws NullPointerException Se l'ISBN passato è null.
     */
    public Book(String isbn, String title, List<String> authors, int year, int totalCopies, int availableCopies) {
        this.isbn = Objects.requireNonNull(isbn);
        this.title = title;
        // Evitiamo NullPointerException e riferimenti esterni diretti
        this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
        this.year = year;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // --- GETTER ---

    /**
     * @brief Restituisce il codice ISBN.
     * @return Una stringa rappresentante l'ISBN del libro.
     */
    public String getIsbn() { return isbn; }

    /**
     * @brief Restituisce il titolo del libro.
     * @return Una stringa contenente il titolo.
     */
    public String getTitle() { return title; }

    /**
     * @brief Restituisce la lista degli autori.
     * @return Una lista di stringhe contenente i nomi degli autori.
     */
    public List<String> getAuthors() { return authors; }

    /**
     * @brief Restituisce gli autori formattati come singola stringa.
     *
     * @return Una stringa con i nomi degli autori separati da virgola,
     * oppure una stringa vuota se non ci sono autori.
     */
    public String getAuthorsAsString() {
        return authors != null && !authors.isEmpty() ? String.join(", ", authors) : "";
    }

    /**
     * @brief Restituisce l'anno di pubblicazione.
     * @return Un intero rappresentante l'anno.
     */
    public int getYear() { return year; }

    /**
     * @brief Restituisce il numero totale di copie.
     * @return Il numero totale di copie in possesso della biblioteca.
     */
    public int getTotalCopies() { return totalCopies; }

    /**
     * @brief Restituisce il numero di copie disponibili.
     * @return Il numero di copie attualmente disponibili per il prestito.
     */
    public int getAvailableCopies() { return availableCopies; }

    // --- SETTER ---

    /**
     * @brief Imposta un nuovo codice ISBN.
     * @param isbn Il nuovo ISBN da assegnare.
     */
    public void setIsbn(String isbn){ this.isbn = isbn; }

    /**
     * @brief Imposta il titolo del libro.
     * @param title Il nuovo titolo da assegnare.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * @brief Imposta la lista degli autori.
     * @param authors La nuova lista di autori da assegnare.
     */
    public void setAuthors(List<String> authors) { this.authors = authors; }

    /**
     * @brief Imposta l'anno di pubblicazione.
     * @param year Il nuovo anno da assegnare.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @brief Imposta il numero totale di copie.
     * @param totalCopies Il nuovo numero totale di copie.
     */
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    /**
     * @brief Imposta il numero di copie disponibili.
     * @param availableCopies Il nuovo numero di copie disponibili.
     */
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    /**
     * @brief Verifica l'uguaglianza tra questo libro e un altro oggetto.
     *
     * Due libri sono considerati uguali se hanno lo stesso codice ISBN.
     *
     * @param o L'oggetto da confrontare con l'istanza corrente.
     * @return true se gli oggetti sono uguali (stesso ISBN), false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    /**
     * @brief Calcola l'hash code del libro.
     *
     * L'hash code è generato basandosi esclusivamente sul codice ISBN
     * per mantenere coerenza con il metodo equals().
     *
     * @return Un intero rappresentante l'hash code dell'oggetto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}