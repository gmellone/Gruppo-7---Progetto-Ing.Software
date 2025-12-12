/**
/**
 * @file FileBackedLibroRepository.java
 * @brief Implementazione di LibroRepository con persistenza su file.
 *
 * Questa classe funge da adattatore tra un repository in memoria (delegate)
 * e un sistema di persistenza basato su file gestito da FileManager.
 * Ogni modifica (inserimento, cancellazione o aggiornamento) viene salvata
 * automaticamente sul file tramite il metodo persistAll().
 * Al momento dell’istanziazione, il repository viene inizializzato leggendo
 * tutti i libri presenti nel file specificato.
 */
package com.mycompany.gestionebiblioteca.repository;

import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.persistence.FileManager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author valerialupo
 */

/**
 * @class FileBackedLibroRepository
 * @brief Repository di libri che mantiene i dati sincronizzati con un file.
 * Questa classe implementa il pattern Decorator: avvolge un altro
 * LibroRepository (delegate) aggiungendo la persistenza su file senza
 * modificarne l’implementazione interna.
 */
public class FileBackedBookRepository implements BookRepository {
    
    /**
     * @brief Repository interno che gestisce i dati in memoria.
     */
    private final BookRepository delegate;
    
    /**
     * @brief Gestore della lettura e scrittura dei dati su file.
     */
    private final FileManager fileManager;
    
     /**
     * @brief Percorso del file in cui vengono memorizzati i libri.
     */
    private final Path booksFile;

    
    /**
     * @brief Costruttore.
     * Inizializza il repository e carica dal file tutti i libri esistenti,
     * popolando il repository interno.
     *
     * @param delegate Repository che gestisce i dati in memoria.
     * @param fileManager Gestore della persistenza su file.
     * @param booksFile Percorso del file contenente i libri.
     * @throws IllegalArgumentException Se uno dei parametri è null.
     */
    
    // FileBackedrepository non salva i dati direttamente in strutture proprie 
    // fa da adattatore tra repository in memoria -> delegate 
    // e persistenza su file -> FileManager
    
    // il file non è la fonte diretta dei dati a runtime
    // la fonte reale è il repository in memoria, che viene inizializzato dal file solo all'avvio
    
    // questa classe dipende da altre componenti : non crea nulla da sola , riceve tutto dall'esterno -> dependency injection)
    public FileBackedBookRepository(BookRepository delegate, FileManager fileManager, Path booksFile) {
        
        if (delegate == null) {
            throw new IllegalArgumentException("delegate non deve essere null");
        }
        if (fileManager == null) {
            throw new IllegalArgumentException("fileManager non deve essere null");
        }
        if (booksFile == null) {
            throw new IllegalArgumentException("booksFile non deve essere null");
        }
        this.delegate = delegate; // chi gestisce i dati
        this.fileManager = fileManager; // come si salvano
        this.booksFile = booksFile; // dove si salvano
        
        loadFromFile(); // caricamento inziale dal file

    }
    
    
    /**
     * @brief Carica i libri dal file e popola il repository interno.
     *
     * Questo metodo legge tutti i record tramite FileManager
     * e li inserisce nel repository in memoria (delegate).
     */
    
    // è private perche viene chiamato solo dal costruttore 
    private void loadFromFile() { // sincronizza il repository in memoria (delegate) con il contenuto del file dei libri
         try { 
            // FileManager apre il file, legge riga per riga, converte ogni riga in un oggetto Book 
            // e poi restituisce una lista di libri
            List<Book> books = fileManager.loadBooks(booksFile); 
            
            // pulizia del repository in memoria
            // serve a garantire che il repository in memoria non contenga dati vecchi 
            // e che quindi la memoria rifletta esattamente il file
            delegate.deleteAll();
            
            // itero su ogni libro letto dal file , delegando la gestione dei dati al repository in memoria 
            for (Book book : books) {
                delegate.save(book);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Errore durante il caricamento dei libri dal file " + booksFile, e);
        }

    }
    
    
    /**
     * @brief Salva sul file tutti i libri presenti nel repository.
     *
     * Questo metodo garantisce che ogni modifica sia immediatamente
     * persistita per evitare perdita di dati.
     */
    private void persistAll() { // salvare su file lo stato completo e corrente del repository in memoria
        try { 
            // recupero dello stato corrente
            List<Book> allBooks = delegate.findAll(); 
            // scrittura sul file
            fileManager.saveBooks(booksFile, allBooks);
        } catch (IOException e) {
            throw new UncheckedIOException("Errore durante il salvataggio dei libri sul file " + booksFile, e);
        }
    }
    
    
    /**
     * @brief Salva un libro e aggiorna il file di persistenza.
     *
     * @param entity Libro da salvare.
     * @return Il libro salvato.
     */
    @Override
    public Book save(Book entity) {
        Book saved = delegate.save(entity);
        persistAll();
        return saved;
    }
    
    
     /**
     * @brief Restituisce un libro tramite il suo ID.
     */
    @Override
    public Optional<Book> findById(String id) {
        return delegate.findById(id);
    }
    
    
    /**
     * @brief Restituisce tutti i libri presenti nel repository.
     */
    @Override
    public List<Book> findAll() {
        return delegate.findAll();
    }
    
    
    /**
     * @brief Elimina un libro tramite ID e aggiorna il file.
     */
    @Override
    public void deleteById(String id) {
        delegate.deleteById(id);
        persistAll();
    }

    
    /**
     * @brief Elimina tutti i libri e sincronizza il file.
     */
    @Override
    public void deleteAll() {
        delegate.deleteAll();
        persistAll();
    }

    
    /**
     * @brief Verifica se un libro esiste tramite ID.
     */
    @Override
    public boolean existsById(String id) {
        return delegate.existsById(id);
    }

    
    /**
     * @brief Restituisce il numero totale di libri.
     */
    @Override
    public long count() {
        return delegate.count();
    }

    
    /**
     * @brief Cerca un libro tramite ISBN.
     */
    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return delegate.findByIsbn(isbn);
    }

    
    /**
     * @brief Cerca libri il cui titolo contiene una data parola chiave.
     */
    @Override
    public List<Book> findByTitleContaining(String keyword) {
        return delegate.findByTitleContaining(keyword);
    }
    
    
     /**
     * @brief Cerca libri il cui autore contiene una data parola chiave.
     */
    @Override
    public List<Book> findByAuthorContaining(String keyword) {
        return delegate.findByAuthorContaining(keyword);
    }

    /**
     * @brief Restituisce tutti i libri ordinati per titolo.
     */
    @Override
    public List<Book> findAllOrderByTitle() {
        return delegate.findAllOrderByTitle();
    }
}
