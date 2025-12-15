/*
 * Test di unità per la classe {@link FileBackedBookRepository}.
 *
 * Questa classe verifica il corretto comportamento del repository
 * file-backed dei libri, in particolare il caricamento iniziale dei dati
 * dal file di persistenza verso il repository in-memory delegato
 *
 * I test assicurano che:
 * 1) i dati presenti su file vengano caricati correttamente all'avvio
 * 2) il repository esponga correttamente i libri tramite le operazioni CRUD
 * 3) il meccanismo di delega verso {@link InMemoryBookRepository} funzioni come previsto
 *
 * Anche in questo caso vengono utilizzate directory temporanee
 * per evitare effetti collaterali sul filesystem reale.
 */
package com.mycompany.gestionebiblioteca.repository;

import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.model.Book;
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
public class FileBackedBookRepositoryTest {
    
    
    /*
    * Verifica che il costruttore di FileBackedBookRepository:
    * carichi correttamente i libri da un file già esistente +
    * popoli il repository in-memory delegato +
    * renda immediatamente disponibili i dati tramite i metodi del repository
    */
    @Test
    void constructorShouldLoadBooksFromExistingFile(@TempDir Path tempDir) throws IOException {
        
        // creazione di un file temporaneo che simula il file di persistenza dei libri
        Path booksFile = tempDir.resolve("books.txt");
        
        
        // contenuto del file: intestazione + due libri validi
        List<String> lines = Arrays.asList(
                "ISBN|Titolo|Autori|Anno|CopieTotali|CopieDisponibili",
                "9781234567890|Title1|Author1|2020|5|5",
                "9781234567891|Title2|Author2|2021|3|2"
        );
        
        // scrittura del file di input
        Files.write(booksFile, lines, StandardCharsets.UTF_8);

        
        // inizializzazione del filemanager, responsabile della lettura/scrittura su file
        FileManager fileManager = new FileManager();
        
        // Creazione del repository file-backed:
        // InMemoryBookRepository gestisce i dati in memoria
        // FileBackedBookRepository si occupa della persistenza
        BookRepository repository = new FileBackedBookRepository(
                new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        
        
        // verifica che i libri siano stati caricati correttamente
        assertEquals(2, repository.count());
       
        
        // verifica dell'esistenza dei libri tramite isbn
        assertTrue(repository.existsById("9781234567890"));
        assertTrue(repository.existsById("9781234567891"));

        // recupero di un libro per verificarne i campi
        Book first = repository.findById("9781234567890").orElseThrow(
                () -> new NotFoundException("Libro non trovato"));
        
        // verifica dei dati del libro caricato
        assertEquals("Title1", first.getTitle());
        assertEquals(Arrays.asList("Author1"), first.getAuthors());
    }
    
}
