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

import com.mycompany.model.Prestito;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;



/**
 * Repository responsabile della gestione dell’archivio dei prestiti.
 *
 * Si occupa di:
 * - leggere la lista dei prestiti da file
 * - salvare modifiche
 * - registrare un nuovo prestito
 * - chiudere (rimuovere) un prestito restituito
 * - ricercare prestiti per utente o per libro
 *
 * NON gestisce:
 * - controllo di copie disponibili
 * - limite di 3 prestiti per utente
 * - logiche di data
 * 
 * Queste logiche appartengono al Controller / classe Biblioteca.
 */
public class PrestitoRepository {
    /** Percorso del file che contiene l’archivio prestiti. */
    private final Path filePath;

    /** Modulo di basso livello per leggere/scrivere file. */
    private final FileRepository fileRepository;

    /**
     * Costruttore del repository prestiti.
     * @param fileName nome del file dell’archivio (es. "prestiti.txt")
     */
    public PrestitoRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        this.fileRepository = new FileRepository();
    }

    // 1. CARICAMENTO DATI

    /**
     * Restituisce la lista completa dei prestiti attivi.
     */
    public List<Prestito> findAll() {
        // TODO
        return new ArrayList<>();
    }

    // 2. RICERCA PRESTITI

    /**
     * Cerca tutti i prestiti effettuati da un dato utente.
     *
     * @param matricola matricola dell'utente
     */
    public List<Prestito> findByMatricola(String matricola) {
        // TODO
        return new ArrayList<>();
    }

    /**
     * Cerca tutti i prestiti associati a un determinato libro.
     *
     * @param isbn codice ISBN del libro
     */
    public List<Prestito> findByIsbnLibro(String isbn) {
        // TODO
        return new ArrayList<>();
    }

    /**
     * Cerca un prestito specifico (utente + libro).
     * Usato durante la restituzione.
     */
    public Optional<Prestito> findPrestito(String matricola, String isbn) {
        // TODO
        return Optional.empty();
    }

    // 3. REGISTRAZIONE DI UN NUOVO PRESTITO

    /**
     * Registra un nuovo prestito nell’archivio.
     * 
     * L’oggetto Prestito include:
     * - matricola dello studente
     * - isbn del libro
     * - data restituzione prevista (come String o LocalDate)
     */
    public void addPrestito(Prestito p) {
        // TODO
    }

    // 4. RESTITUZIONE (CHIUSURA PRESTITO)


    /**
     * Rimuove un prestito dall’archivio (restituzione libro).
     *
     * @return true se il prestito è stato trovato e rimosso
     */
    public boolean removePrestito(String matricola, String isbn) {
        // TODO
        return false;
    }

    // 5. SUPPORTO: salvataggio e parsing

    /**
     * Salva tutti i prestiti nel file.
     */
    private void saveAll(List<Prestito> prestiti) {
        // TODO
    }

    /**
     * Converte un prestito in una riga del file.
     */
    private String format(Prestito p) {
        // TODO
        return "";
    }

    /**
     * Converte una riga del file in un oggetto Prestito.
     */
    private Prestito parse(String riga) {
        // TODO
        return null;
    }
}
