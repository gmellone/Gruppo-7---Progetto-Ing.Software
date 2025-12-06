package com.mycompany.gestionebiblioteca.model;

/**
 * Classe del modello di dominio usata per l'esercizio.
 * 
 * Rappresenta un libro con alcune semplici regole interne di validazione:
 * - l'ISBN deve contenere esattamente 13 cifre
 * - un libro ha copie disponibili se copies > 0
 *
 * Questa classe è indipendente dal resto del progetto
 * (non sostituisce la classe Libro esistente).
 */
public class Book {

    private String title;
    private String author;
    private String isbn;   // deve avere 13 cifre
    private int year;
    private int copies;    // numero di copie disponibili

    public Book(String title, String author, String isbn, int year, int copies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.year = year;
        this.copies = copies;
    }

    /**
     * Controlla se l'ISBN è valido.
     * Deve essere lungo 13 caratteri e tutti numerici.
     */
    public boolean isValidIsbn() {
        // Controlla lunghezza
        if (isbn == null || isbn.length() != 13) {
            return false;
        }

        // Controlla che ogni carattere sia una cifra
        for (char c : isbn.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Ritorna true se il libro ha almeno una copia disponibile.
     */
    public boolean hasAvailableCopies() {
        return copies > 0;
    }

    // Getters (utili nei test)
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getYear() { return year; }
    public int getCopies() { return copies; }
}