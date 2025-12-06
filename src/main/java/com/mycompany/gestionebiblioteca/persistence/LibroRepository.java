/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.persistence;

/**
 *
 * @author valerialupo
 */

import com.mycompany.model.Libro;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Repository responsabile della gestione dell’archivio dei libri.
 * 
 * Questa classe incapsula tutta la logica di:
 * - lettura dei libri dal file di testo
 * - scrittura dell'archivio aggiornato
 * - ricerca (ISBN, titolo, autore)
 * - inserimento, modifica e rimozione di libri
 * 
 * Il repository NON si occupa di validare i dati (compito della GUI o del Controller)
 * ma solo di leggere/scrivere in modo coerente con il formato definito:
 * 
 *     isbn;titolo;autore1,autore2;annoPubblicazione;copieDisponibili
 */

public class LibroRepository {
     /** Percorso del file che contiene l’archivio dei libri. */
    private final Path filePath;

    /** Modulo di basso livello per leggere/scrivere file */
    private final FileRepository fileRepository;

    /**
     * Costruttore del repository.
     * @param fileName nome del file dell'archivio (es: "libri.txt")
     */
    public LibroRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        this.fileRepository = new FileRepository();
    }

    
    // 1. CARICAMENTO DATI
    
    public List<Libro> findAll() {
        // TODO: leggere tutto il file e convertire in oggetti Libro
        return new ArrayList<>();
    }


    // 2. METODI DI RICERCA

    public Optional<Libro> findByIsbn(String isbn) {
        // TODO: ricerca per ISBN
        return Optional.empty();
    }

    public List<Libro> findByTitolo(String titolo) {
        // TODO: ricerca per titolo
        return new ArrayList<>();
    }

    public List<Libro> findByAutore(String autore) {
        // TODO: ricerca per autore
        return new ArrayList<>();
    }

    
    // 3. INSERIMENTO LIBRO
   

    public enum EsitoInserimento {
        INSERITO,
        GIA_PRESENTE
    }

    public EsitoInserimento addLibro(Libro nuovo) {
        // TODO: controllare se presente e aggiungere
        return EsitoInserimento.INSERITO;
    }

    // 4. INCREMENTARE COPIE


    public boolean incrementaCopie(String isbn, int quanteCopie) {
        // TODO: incrementare copie disponibili
        return false;
    }

    // 5. RIMOZIONE LIBRO


    public boolean removeLibro(String isbn) {
        // TODO: rimuovere libro per ISBN
        return false;
    }

   
    // 6. MODIFICA LIBRO

    public boolean updateLibro(Libro modificato) {
        // TODO: aggiornare i dati del libro
        return false;
    }

    // 7. SUPPORTO: parsing e salvataggio

    private void saveAll(List<Libro> libri) {
        // TODO: scrivere tutti i libri nel file
    }

    private String format(Libro l) {
        // TODO: convertire Libro → riga di file
        return "";
    }

    private Libro parse(String riga) {
        // TODO: convertire riga file → Libro
        return null;
    }
}