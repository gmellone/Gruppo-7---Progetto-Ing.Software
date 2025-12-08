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

import com.mycompany.gestionebiblioteca.model.Libro;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;



/**
 * Repository responsabile ESCLUSIVAMENTE della persistenza degli utenti.
 *
 * Questa classe si occupa di:
 *  - leggere l’archivio degli utenti da file di testo
 *  - salvare l’intera lista degli utenti sul file
 *  - convertire ogni riga del file in un oggetto Utente
 *  - convertire ogni oggetto Utente in una riga testuale per il salvataggio
 *
 * NON gestisce alcuna logica di business:
 *  - non controlla se un utente è già registrato
 *  - non valida i dati inseriti (matricola, e-mail, ecc.)
 *  - non gestisce prestiti o vincoli applicativi sugli utenti
 *
 * Tutta la logica relativa ai casi d’uso (registrazione utente,
 * rimozione utente, modifiche, controlli) è demandata al Controller
 * o alla classe Biblioteca. Il repository rappresenta un livello di
 * "persistenza bassa" che interagisce esclusivamente con il filesystem.
 */
public class LibroRepository {
        private final Path filePath;
    private final FileRepository fileRepository;

    public LibroRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        this.fileRepository = new FileRepository();
    }

    // Carica tutti i libri dal file
    public List<Libro> loadAll() {
        // TODO
        return new ArrayList<>();
    }

    // Salva tutti i libri nel file
    public void saveAll(List<Libro> libri) {
        // TODO
    }

    // Converte Libro → riga
    private String format(Libro libro) {
        // TODO
        return "";
    }

    // Converte riga → Libro
    private Libro parse(String riga) {
        // TODO
        return null;
    }
}
    