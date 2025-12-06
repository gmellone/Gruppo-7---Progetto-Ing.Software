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
    /** Percorso del file che contiene l’archivio utenti. */
    private final Path filePath;

    /** Componente per operazioni di lettura e scrittura su file. */
    private final FileRepository fileRepository;

    /**
     * Costruttore del repository utenti.
     * @param fileName nome del file dell'archivio (es: "utenti.txt")
     */
    public UtenteRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        this.fileRepository = new FileRepository();
    }

    // 1. CARICAMENTO DATI
    /**
     * Restituisce la lista completa degli utenti registrati.
     */
    public List<Utente> findAll() {
        // TODO
        return new ArrayList<>();
    }

    // 2. METODI DI RICERCA
    /**
     * Cerca un utente tramite matricola.
     */
    public Optional<Utente> findByMatricola(String matricola) {
        // TODO
        return Optional.empty();
    }

    /**
     * Cerca utenti tramite cognome (case-insensitive).
     */
    public List<Utente> findByCognome(String cognome) {
        // TODO
        return new ArrayList<>();
    }


    // 3. INSERIMENTO UTENTE

    /**
     * Esiti possibili dell'inserimento di un nuovo utente.
     */
    public enum EsitoInserimento {
        INSERITO,
        GIA_PRESENTE
    }

    /**
     * Inserisce un nuovo utente nell'archivio.
     * 
     * @return INSERITO se registrato correttamente;
     *         GIA_PRESENTE se esiste già un utente con la stessa matricola.
     */
    public EsitoInserimento addUtente(Utente nuovo) {
        // TODO
        return EsitoInserimento.INSERITO;
    }

    // 4. RIMOZIONE UTENTE

    /**
     * Rimuove un utente tramite matricola.
     * (La logica "non rimuovere se l’utente ha prestiti attivi"
     *  viene gestita nel Controller.)
     */
    public boolean removeUtente(String matricola) {
        // TODO
        return false;
    }

    
    // 5. MODIFICA DATI UTENTE
    /**
     * Aggiorna i dati di un utente già registrato.
     *
     * @return true se il record esiste ed è stato modificato
     */
    public boolean updateUtente(Utente modificato) {
        // TODO
        return false;
    }

    // 6. SUPPORTO: parsing e salvataggio

    /**
     * Salva tutti gli utenti nel file.
     */
    private void saveAll(List<Utente> utenti) {
        // TODO
    }

    /**
     * Converte un utente in una riga del file.
     */
    private String format(Utente u) {
        // TODO
        return "";
    }

    /**
     * Converte una riga del file in un oggetto Utente.
     */
    private Utente parse(String riga) {
        // TODO
        return null;
    }
}
