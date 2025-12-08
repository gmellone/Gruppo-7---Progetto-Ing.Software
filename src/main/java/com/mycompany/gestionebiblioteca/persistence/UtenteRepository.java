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
import com.mycompany.gestionebiblioteca.model.Utente;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;


/**
 * Repository responsabile ESCLUSIVAMENTE della persistenza degli utenti.
 *
 * Questa classe si occupa di:
 *  - leggere l’archivio degli utenti da file di testo
 *  - salvare l’intera lista degli utenti sul file
 *  - convertire le righe del file in oggetti Utente
 *  - convertire gli oggetti Utente in righe pronte per essere salvate
 *
 * NON gestisce alcuna logica di business:
 *  - non controlla se l’utente è già registrato
 *  - non valida i dati (matricola, e-mail, ecc.)
 *  - non gestisce prestiti o vincoli applicativi
 *
 * Tutta la logica applicativa appartiene al Controller o alla classe Biblioteca,
 * mentre il repository rappresenta il livello di "persistenza bassa" basato su file.
 */
public class UtenteRepository {
    
    private final Path filePath;
    private final FileRepository fileRepository;

    public UtenteRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        this.fileRepository = new FileRepository();
    }

    // Carica tutti gli utenti dal file
    public List<Utente> loadAll() {
        // TODO
        return new ArrayList<>();
    }

    // Salva tutti gli utenti nel file
    public void saveAll(List<Utente> utenti) {
        // TODO
    }

    // Converte Utente → riga
    private String format(Utente utente) {
        // TODO
        return "";
    }

    // Converte riga → Utente
    private Utente parse(String riga) {
        // TODO
        return null;
    }
}
