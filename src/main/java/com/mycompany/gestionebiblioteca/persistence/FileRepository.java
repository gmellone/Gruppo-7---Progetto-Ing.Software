package com.mycompany.gestionebiblioteca.persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;   //IMPORT NECESSARIO PER Paths.get()
import java.util.Collections;
import java.util.List;
import com.mycompany.exceptions.FileRepositoryException;

/**
 * Classe di utilità per la lettura e scrittura sicura di file di testo.
 * 
 * Offre due operazioni fondamentali:
 *  - writeAll: scrive tutte le righe su un file, usando un file temporaneo 
 *              per evitare corruzione dei dati.
 *  - readAll: legge tutte le righe da un file, restituendo una lista vuota
 *              se il file non esiste.
 *
 * Questa classe rappresenta lo strato di "persistenza bassa" del progetto:
 * i repository di libro/utente/prestito useranno questo componente per 
 * interagire con il filesystem.
 */
public class FileRepository {

    /**
     * Scrive tutte le righe nel file specificato.
     * La scrittura avviene in modo SICURO tramite file temporaneo (.tmp) 
     * che viene rinominato solo dopo il completamento della scrittura.
     *
     * @param path percorso del file da scrivere
     * @param lines lista di righe da salvare
     */
    public void writeAll(String path, List<String> lines) {
        try {
            // Percorso del file finale (es: data/books.txt) — compatibile con Java 8
            Path filePath = Paths.get(path);

            // Percorso del file temporaneo (es: data/books.txt.tmp)
            Path tempPath = Paths.get(path + ".tmp");

            // Se la cartella non esiste, la creiamo (evita errori su nuove directory)
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            // 1. Scrittura del contenuto nel file temporaneo
            // Se qualcosa va storto, NON roviniamo il file originale
            Files.write(tempPath, lines);

            // 2. Rinomina (move) del file temporaneo sul file definitivo
            // L'opzione REPLACE_EXISTING sostituisce il file vecchio in modo atomico
            Files.move(tempPath, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            // Incapsuliamo l'errore tecnico in un'eccezione applicativa più chiara
            throw new FileRepositoryException("Errore durante la scrittura del file: " + path, e);
        }
    }

    /**
     * Legge tutte le righe dal file specificato.
     * Se il file NON esiste, restituisce semplicemente una lista vuota 
     * senza generare errori.
     *
     * @param path percorso del file da leggere
     * @return lista delle righe contenute nel file
     */
    public List<String> readAll(String path) {
        try {
            // Path compatibile con Java 8
            Path filePath = Paths.get(path);

            // Se il file non esiste → archivio vuoto (comportamento desiderato)
            if (!Files.exists(filePath)) {
                return Collections.emptyList();
            }

            // Lettura di tutte le righe
            return Files.readAllLines(filePath);

        } catch (Exception e) {
            // Un problema di I/O viene trasformato in eccezione applicativa
            throw new FileRepositoryException("Errore durante la lettura del file: " + path, e);
        }
    }
}