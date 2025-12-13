
package com.mycompany.gestionebiblioteca.repository;

import com.mycompany.gestionebiblioteca.model.Loan;
import com.mycompany.gestionebiblioteca.persistence.FileManager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


/**
 * @class FileBackedLoanRepository
 * @brief Implementazione di PrestitoRepository con persistenza su file.
 *
 * Questa classe funge da decoratore per un repository di prestiti (delegate),
 * aggiungendo la responsabilità di mantenere sincronizzati i dati con un file
 * tramite l'oggetto FileManager.
 *
 * Ogni modifica ai dati (salvataggio, cancellazione) viene automaticamente
 * persistita su file. Al momento della creazione, il repository viene
 * inizializzato caricando tutti i prestiti esistenti dal file specificato.
 *
 * @see PrestitoRepository
 * @see FileManager
 * @see com.mycompany.gestionebiblioteca.model.Prestito
 */

/**
 *
 * @author valerialupo
 */


// chi usa LoanRepository non deve sapere se i dati sono salvati in memoria, su file o su database
public class FileBackedLoanRepository implements LoanRepository { 
    
    /**
     * @brief Repository interno che gestisce i dati in memoria.
     *
     * L’implementazione concreta può essere una lista, una mappa o qualsiasi
     * altra struttura dati. Questo repository viene arricchito con la
     * persistenza su file da questa classe decoratrice.
     */
    private final LoanRepository delegate;
    
     /**
     * @brief Oggetto responsabile della lettura e scrittura dei prestiti su file.
     */
    private final FileManager fileManager;
    
    /**
     * @brief Percorso del file dove sono memorizzati tutti i prestiti.
     */
    private final Path loansFile;
    
    
    
    
    /**
     * @brief Costruttore principale.
     *
     * Esegue controlli di validazione sui parametri, inizializza i campi e
     * carica dal file tutti i prestiti disponibili.
     *
     * @param delegate Repository che gestisce i prestiti in memoria.
     * @param fileManager Gestore della persistenza su file.
     * @param loansFile Percorso del file contenente i prestiti.
     * @throws IllegalArgumentException Se uno dei parametri è null.
     */
    
    
    public FileBackedLoanRepository(LoanRepository delegate, FileManager fileManager, Path loansFile) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate non deve essere null");
        }
        if (fileManager == null) {
            throw new IllegalArgumentException("fileManager non deve essere null");
        }
        if (loansFile == null) {
            throw new IllegalArgumentException("loansFile non deve essere null");
        }
        
        this.delegate = delegate;
        this.fileManager = fileManager;
        this.loansFile = loansFile;
        
        // caricamento iniziale dei dati
        /*
        quando questa rep nasce, deve rappresentare esattamente lo stato salvato nel file
        cio implica che il repository non nasce vuoto, parte sincronizzato con la persitenza
        
        dopo il costruttore : delegate contiene tutti i file presenti nel file + la memoria riflette il file
        */
        loadFromFile();
    }
    
    
    
    /**
     * @brief Carica dal file tutti i prestiti esistenti e li inserisce nel repository interno.
     *
     * Questo metodo viene invocato automaticamente nel costruttore.
     * In caso di errore di lettura, l’eccezione può essere convertita in
     * UncheckedIOException nell’implementazione completa.
     */
    private void loadFromFile() {
        try {
            // cosa succede : fileManager apre il file + legge le righe + le converte in oggetti Loan
            // il repository non sa nulla del formato del file
            List<Loan> loans = fileManager.loadLoans(loansFile); 
            
            // pulizia del repository -> il delegate potrebbe non essere vuoto -> voglio che la memoria rifletta solo il contenuto del file
            delegate.deleteAll();
            
            // inserimento dei prestiti in memoria 
            for (Loan loan : loans) {
                delegate.save(loan);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Errore durante il caricamento dei prestiti dal file " + loansFile, e);
        }
        
    }
    
    
    
    /**
     * @brief Salva su file tutti i prestiti attualmente presenti nel repository interno.
     *
     * Questo metodo è invocato dopo ogni modifica (salvataggio o cancellazione)
     * per mantenere consistenza tra memoria e file.
     */
    private void persistAll() { // private perchè chi usa il repository non deve sapere quando o come avviene la persitenza
        try {
            // lettura dello stato corrente in memoria
            List<Loan> allLoans = delegate.findAll();
            // cosa succede: il fileManager apre il file + serializza ogni Loan + scrive il contenuto
            fileManager.saveLoans(loansFile, allLoans);
        } catch (IOException e) {
            throw new UncheckedIOException("Errore durante il salvataggio dei prestiti sul file " + loansFile, e);
        }
      
    }

    
    
    /**
     * @brief Salva o aggiorna un prestito e aggiorna la persistenza su file.
     *
     * @param entity Il prestito da salvare.
     * @return Il prestito salvato.
     */
    @Override
    public Loan save(Loan entity) {
        Loan saved = delegate.save(entity);
        persistAll();
        return saved;
    }

    
    
    /**
     * @brief Recupera un prestito tramite il suo ID.
     *
     * @param id Identificativo del prestito.
     * @return Optional contenente il prestito, oppure empty().
     */
    @Override
    public Optional<Loan> findById(String id) {
        return delegate.findById(id);
    }

    
    
    /**
     * @brief Restituisce tutti i prestiti presenti nel repository.
     *
     * @return Lista completa dei prestiti.
     */
    @Override
    public List<Loan> findAll() {
        return delegate.findAll();
    }

    
    
    /**
     * @brief Elimina un prestito tramite ID e aggiorna la persistenza su file.
     *
     * @param id Identificativo del prestito da eliminare.
     */
    @Override
    public void deleteById(String id) {
        delegate.deleteById(id);
        persistAll();
    }

    
    
    /**
     * @brief Elimina tutti i prestiti e sincronizza il file.
     */
    @Override
    public void deleteAll() {
        delegate.deleteAll();
        persistAll();
    }

    
    
    /**
     * @brief Verifica se un prestito esiste tramite ID.
     *
     * @param id Identificativo del prestito.
     * @return true se il prestito esiste, false altrimenti.
     */
    @Override
    public boolean existsById(String id) {
        return delegate.existsById(id);
    }

    
    
    /**
     * @brief Restituisce il numero totale di prestiti.
     */
    @Override
    public long count() {
        return delegate.count();
    }

    
    
    /**
     * @brief Restituisce tutti i prestiti associati a una specifica matricola utente.
     *
     * @param matricola Matricola dell'utente.
     * @return Lista dei prestiti attivi o storici dell’utente.
     */
    @Override
    public List<Loan> findByUserMatricola(String matricola) {
        return delegate.findByUserMatricola(matricola);
    }

    
    
    /**
     * @brief Restituisce tutti i prestiti relativi a un determinato ISBN.
     *
     * @param isbn Codice ISBN del libro prestato.
     * @return Lista dei prestiti corrispondenti.
     */
    @Override
    public List<Loan> findByBookIsbn(String isbn) {
        return delegate.findByBookIsbn(isbn);
    }

    
    
    /**
     * @brief Restituisce tutti i prestiti ancora attivi, ordinati per data di scadenza.
     *
     * @return Lista dei prestiti ordinati dalla data più prossima alla scadenza.
     */
    @Override
    public List<Loan> findActiveLoansOrderByDueDate() {
        return delegate.findActiveLoansOrderByDueDate();
    }

    
    
    /**
     * @brief Restituisce tutti i prestiti attivi associati a uno specifico utente.
     *
     * @param matricola Matricola dell’utente.
     * @return Lista dei prestiti attivi.
     */
    @Override
    public List<Loan> findActiveLoansByUser(String matricola) {
        return delegate.findActiveLoansByUser(matricola);
    }
    
    
}
