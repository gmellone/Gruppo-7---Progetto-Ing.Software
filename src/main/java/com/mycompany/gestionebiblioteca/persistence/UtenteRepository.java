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
import com.mycompany.model.Utente;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;


/**
 * Repository responsabile della gestione dell’archivio degli utenti.
 *
 * Si occupa di:
 * - leggere l'archivio da file
 * - salvare modifiche
 * - eseguire ricerche (matricola, cognome)
 * - gestire inserimento, modifica, rimozione utenti
 *
 * Il repository NON contiene logica di business né controlli della GUI.
 * Si limita alla persistenza (lettura/scrittura file).
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
