/*
 * Test di unità per la classe FileBackedUserRepository.
 *
 * Questa classe serve a verificare che il repository file-backed
 * degli utenti carichi correttamente i dati presenti nel file
 * e li renda disponibili tramite il repository in-memory delegato
 *
 * il test controlla in particolare il comportamento del costruttore,
 * che deve leggere il file degli utenti e inizializzare correttamente
 * il repository
 */
package com.mycompany.gestionebiblioteca.repository;

import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.model.User;
import com.mycompany.gestionebiblioteca.persistence.FileManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author valerialupo
 */
public class FileBackedUserRepositoryTest {
    
    /*
    * Verifica che il costruttore di FileBackedUserRepository
    * carichi correttamente gli utenti da un file già esistente
    */
    @Test
    void constructorShouldLoadUsersFromExistingFile(@TempDir Path tempDir) throws IOException {
        
        // creazione di un file temporaneo che simula il file degli utenti
        Path usersFile = tempDir.resolve("users.txt");
        
        // contenuto del file: intestazione + due utenti validi
        List<String> lines = Arrays.asList(
                "Matricola|Nome|Cognome|Email",
                "1234567890|Mario|Rossi|mario@example.com",
                "1234567891|Luigi|Bianchi|luigi@example.com"
        );
        
        // scrittura del file temporaneo
        Files.write(usersFile, lines, StandardCharsets.UTF_8);

        
        // inizializzazione del FileManager per la gestione del file
        FileManager fileManager = new FileManager();
        
        
        // creazione del repository file-backed, che delega la gestione
        // degli utenti a un repository in-memory
        UserRepository repository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        
        // verifica che gli utenti siano stati caricati correttamente
        assertEquals(2, repository.count());
        assertTrue(repository.existsById("1234567890"));
        assertTrue(repository.existsById("1234567891"));

        // recupero di un utente per verificare i dati caricati
        User first = repository.findById("1234567890").orElseThrow(
        () -> new NotFoundException("User not found"));
        
        assertEquals("Mario", first.getFirstName());
        assertEquals("Rossi", first.getLastName());
    }
    
}
