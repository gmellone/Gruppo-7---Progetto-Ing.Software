/*
 * Test di unità per la classe {@link FileManager}.
 *
 * Questa classe verifica il corretto funzionamento dei metodi di caricamento e
 * salvataggio dei libri su file di testo.
 *
 * In particolare, i test coprono: 
 * 1)la creazione automatica del file se assente
 * 2)la corretta scrittura dell'intestazione (header) 
 * 3)la serializzazione e deserializzazione degli oggetti {@link Book} 
 * 4)la gestione di righe vuote o non valide 
 * 5)la validazione dei dati in ingresso
 *
 * I test utilizzano directory temporanee per evitare effetti collaterali sul
 * filesystem reale.
 */
package com.mycompany.gestionebiblioteca.persistence;

import com.mycompany.gestionebiblioteca.model.Book;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author valerialupo
 */
class FileManagerTest {

    private FileManager fileManager;

    // Viene creata una nuova istanza di FileManager prima di ogni test
    // per garantire l'indipendenza tra i test
    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
    }

    /*
     * verifica che il metodo loadBooks:
     * crei automaticamente il file se non esiste +
     * scriva l'header corretto +
     * restituisca una lista vuota di libri
     */
    @Test
    void loadBooksShouldCreateFileIfMissingAndReturnEmptyList(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        // il file non deve esistere prima dell'invocazione
        assertFalse(Files.exists(booksFile));

        // caricamento dei libri da un file inesistente
        List<Book> books = fileManager.loadBooks(booksFile);

        // il file deve essere stato creato automaticamente
        assertTrue(Files.exists(booksFile));

        // nessun libro presente
        assertTrue(books.isEmpty());

        // verifica che il file contenga solo l'intestazione
        List<String> lines = Files.readAllLines(booksFile, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
        assertEquals("ISBN|Titolo|Autori|Anno|CopieTotali|CopieDisponibili", lines.get(0));
    }

    /*
     * Verifica che il metodo saveBooks scriva correttamente: 
     * l'intestazione del file,
     * i libri nel formato previsto, usando il separatore | e ; per la lista degli autori.
     */
    @Test
    void saveBooksShouldWriteHeaderAndBooksInCorrectFormat(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        Book book1 = new Book("9781234567890", "Il nome della rosa", Arrays.asList("Eco"), 1980, 5, 3);
        Book book2 = new Book("9781234567891", "Clean Code", Arrays.asList("Martin", "Fowler"), 2008, 10, 7);

        // salvataggio dei libri su file
        fileManager.saveBooks(booksFile, Arrays.asList(book1, book2));

        // lettura del file per verificarne il contenuto
        List<String> lines = Files.readAllLines(booksFile, StandardCharsets.UTF_8);

        assertEquals(3, lines.size());
        assertEquals("ISBN|Titolo|Autori|Anno|CopieTotali|CopieDisponibili", lines.get(0));
        assertEquals("9781234567890|Il nome della rosa|Eco|1980|5|3", lines.get(1));
        assertEquals("9781234567891|Clean Code|Martin;Fowler|2008|10|7", lines.get(2));
    }

    /*
    * verifica che loadBooks legga correttamente un file valido
    * e ricostruisca correttamente gli oggetti Book
     */
    @Test
    void loadBooksShouldReadBooksFromValidFile(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");

        // file di input simulato
        List<String> lines = Arrays.asList(
                "ISBN|Titolo|Autori|Anno|CopieTotali|CopieDisponibili",
                "9781234567890|Il nome della rosa|Eco|1980|5|3",
                "9781234567891|Clean Code|Martin;Fowler|2008|10|7"
        );

        Files.write(booksFile, lines, StandardCharsets.UTF_8);

        List<Book> books = fileManager.loadBooks(booksFile);

        assertEquals(2, books.size());

        // verifica completa del primo libro
        Book first = books.get(0);
        assertEquals("9781234567890", first.getIsbn());
        assertEquals("Il nome della rosa", first.getTitle());
        assertEquals(Arrays.asList("Eco"), first.getAuthors());
        assertEquals(1980, first.getYear());
        assertEquals(5, first.getTotalCopies());
        assertEquals(3, first.getAvailableCopies());

        // verifica completa del secondo libro
        Book second = books.get(1);
        assertEquals("9781234567891", second.getIsbn());
        assertEquals("Clean Code", second.getTitle());
        assertEquals(Arrays.asList("Martin", "Fowler"), second.getAuthors());
        assertEquals(2008, second.getYear());
        assertEquals(10, second.getTotalCopies());
        assertEquals(7, second.getAvailableCopies());
    }

    /*
    * Verifica che loadBooks:
    * ignori l'intestazione +
    * ignori righe vuote o contenenti solo spazi +
    * carichi correttamente solo le righe valide
     */
    @Test
    void loadBooksShouldIgnoreBlankLinesAndHeader(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        List<String> lines = Arrays.asList(
                "ISBN|Titolo|Autori|Anno|CopieTotali|CopieDisponibili",
                "",
                "9781234567890|Il nome della rosa|Eco|1980|5|3",
                "   ",
                "9781234567891|Clean Code|Martin|2008|10|7"
        );
        Files.write(booksFile, lines, StandardCharsets.UTF_8);

        List<Book> books = fileManager.loadBooks(booksFile);

        // devono essere caricati solo i due libri validi
        assertEquals(2, books.size());
    }

    /*
    * verifica che loadBooks lanci un'eccezione
    * quando una riga del file non rispetta il formato atteso
     */
    @Test
    void loadBooksShouldThrowOnInvalidLineFormat(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        List<String> lines = Arrays.asList(
                "ISBN|Titolo|Autori|Anno|CopieTotali|CopieDisponibili",
                "riga non valida con un numero insufficiente di campi"
        );
        Files.write(booksFile, lines, StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> fileManager.loadBooks(booksFile));
    }

    /*
    * Verifica che saveBooks rifiuti dati non validi,
     * in particolare campi che contengono il separatore '|',
    * per evitare la corruzione del formato del file.
     */
    
    @Test
    void saveBooksShouldRejectFieldsContainingSeparator(@TempDir Path tempDir) {
        Path booksFile = tempDir.resolve("books.txt");
        Book invalidBook = new Book("9781234567890", "Title|WithSeparator", Arrays.asList("Author"), 2000, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> fileManager.saveBooks(booksFile, Arrays.asList(invalidBook)));
    }

}
