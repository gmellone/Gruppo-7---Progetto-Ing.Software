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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Libro implements Serializable {
    private String titolo;
    private List<String> autori;
    private int annoPubblicazione;
    private String isbn;
    private int copieDisponibili;

    public Libro(String titolo, List<String> autori, int annoPubblicazione, String isbn, int copieDisponibili) {
        this.titolo = titolo;
        this.autori = autori;
        this.annoPubblicazione = annoPubblicazione;
        this.isbn = isbn;
        this.copieDisponibili = copieDisponibili;
    }

    // Metodi per gestire le copie
    public void decrementaCopie() {
        if (this.copieDisponibili > 0) {
            this.copieDisponibili--;
        }
    }

    public void incrementaCopie() {
        this.copieDisponibili++;
    }

    // Getters e Setters
    public String getTitolo() { return titolo; }
    public String getIsbn() { return isbn; }
    public int getCopieDisponibili() { return copieDisponibili; }
    public List<String> getAutori() { return autori; }
    public int getAnnoPubblicazione() { return annoPubblicazione; }

    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setAutori(List<String> autori) { this.autori = autori; }
    public void setAnnoPubblicazione(int annoPubblicazione) { this.annoPubblicazione = annoPubblicazione; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setCopieDisponibili(int copieDisponibili) { this.copieDisponibili = copieDisponibili; }
    
    @Override
    public String toString() {
        return titolo + " (" + isbn + ")";
    }
}
