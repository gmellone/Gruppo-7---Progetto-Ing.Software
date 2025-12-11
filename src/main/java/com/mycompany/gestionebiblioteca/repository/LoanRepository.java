
package com.mycompany.gestionebiblioteca.repository;
        
import com.mycompany.gestionebiblioteca.model.Loan;
import java.util.List;
        
/**
 *
 * @author valerialupo
 */


/**
 * @interface PrestitoRepository
 * @brief Interfaccia dedicata alla gestione dei prestiti nel sistema.
 *
 * Questa interfaccia estende {@link CrudRepository} fornendo operazioni
 * avanzate di ricerca specifiche per la gestione dei prestiti, come
 * individuare prestiti attivi, prestiti per utente e prestiti per libro.
 *
 * Le implementazioni concrete (come FileBackedPrestitoRepository)
 * devono garantire che i metodi definiti rispettino tale contratto.
 *
 * @see CrudRepository
 * @see com.mycompany.gestionebiblioteca.model.Loan
 */


public interface LoanRepository extends CrudRepository<String, Loan> {
    
    
     /**
     * @brief Restituisce tutti i prestiti associati a uno specifico utente.
     *
     * Utilizzato per mostrare la situazione dei prestiti di un singolo utente,
     * sia attivi che storici.
     *
     * @param matricola Matricola dell’utente.
     * @return Lista dei prestiti associati a quell’utente.
     */
    List<Loan> findByUserMatricola(String matricola);

    
    
     /**
     * @brief Restituisce tutti i prestiti relativi a un determinato libro tramite il suo ISBN.
     *
     * Utile per controllare quante copie sono attualmente in prestito
     * o visualizzare lo storico dei prestiti di un libro.
     *
     * @param isbn ISBN del libro.
     * @return Lista dei prestiti riferiti a quel libro.
     */
    List<Loan> findByBookIsbn(String isbn);
    
    
    
     /**
     * @brief Restituisce i prestiti attivi ordinati per data di scadenza.
     *
     * Serve per identificare rapidamente i prestiti in scadenza o già oltre la data prevista.
     *
     * @return Lista dei prestiti attivi, ordinati per data di restituzione prevista.
     */
    List<Loan> findActiveLoansOrderByDueDate();

    
    
     /**
     * @brief Restituisce tutti i prestiti attivi associati a uno specifico utente.
     *
     * Utile per controllare quali libri un utente deve ancora restituire.
     *
     * @param matricola Matricola dell’utente.
     * @return Lista dei prestiti attualmente attivi dell’utente.
     */
    List<Loan> findActiveLoansByUser(String matricola);

}








