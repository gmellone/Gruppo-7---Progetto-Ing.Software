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

import com.mycompany.gestionebiblioteca.model.Prestito;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;



/**
 * Repository responsabile ESCLUSIVAMENTE della persistenza dei prestiti.
 *
 * Questa classe si occupa di:
 *  - leggere l’archivio dei prestiti da file di testo
 *  - salvare l’intera lista dei prestiti sul file
 *  - convertire le righe del file in oggetti Prestito
 *  - convertire gli oggetti Prestito in righe pronte per il salvataggio
 *
 * NON gestisce alcuna logica di business:
 *  - non controlla se un utente ha raggiunto il limite dei prestiti
 *  - non verifica la disponibilità delle copie dei libri
 *  - non gestisce la restituzione dal punto di vista logico
 *  - non valida le date o le condizioni dei prestiti
 *
 * Tutta la logica applicativa (registrazione prestito, restituzione libro,
 * aggiornamento copie, controlli sui vincoli) è responsabilità del Controller
 * o della classe Biblioteca. Questo repository rappresenta esclusivamente lo
 * strato di "persistenza bassa" basato su file.
 */
public class PrestitoRepository {
     private final Path filePath;
    private final FileRepository fileRepository;

    public PrestitoRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        this.fileRepository = new FileRepository();
    }

    // Carica tutti i prestiti dal file
    public List<Prestito> loadAll() {
        // TODO
        return new ArrayList<>();
    }

    // Salva tutti i prestiti nel file
    public void saveAll(List<Prestito> prestiti) {
        // TODO
    }

    // Converte Prestito → riga
    private String format(Prestito prestito) {
        // TODO
        return "";
    }

    // Converte riga → Prestito
    private Prestito parse(String riga) {
        // TODO
        return null;
    }
}
