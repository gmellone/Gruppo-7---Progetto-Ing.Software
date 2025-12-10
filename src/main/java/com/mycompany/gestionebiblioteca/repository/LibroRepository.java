
package com.mycompany.gestionebiblioteca.repository;

import com.mycompany.gestionebiblioteca.model.Book;
import java.util.List;
import java.util.Optional;



/**
 * @interface LibroRepository
 * @brief Interfaccia che definisce il contratto per la gestione dei libri nel sistema.
 *
 * Questa interfaccia estende {@link CrudRepository} e fornisce operazioni
 * di ricerca specifiche per i libri, come la ricerca per ISBN, titolo e autore.
 * 
 * Qualsiasi implementazione concreta (come FileBackedLibroRepository) deve
 * garantire che tutti questi metodi siano supportati.
 *
 * @see CrudRepository
 * @see com.mycompany.gestionebiblioteca.model.Libro
 */
/**
 *
 * @author valerialupo
 */
public interface LibroRepository extends CrudRepository<String, Book> {
    
    
    /**
     * @brief Cerca un libro tramite il suo codice ISBN.
     *
     * @param isbn Il codice ISBN del libro ricercato.
     * @return Un Optional contenente il libro trovato, oppure empty() se non esiste.
     */
    Optional<Book> findByIsbn(String isbn);
    
    
    
    /**
     * @brief Restituisce tutti i libri il cui titolo contiene la parola chiave specificata.
     *
     * @param keyword Parola o parte di parola da cercare nel titolo.
     * @return Lista dei libri che soddisfano il criterio di ricerca.
     */
    List<Book> findByTitleContaining(String keyword);

    
    
    /**
     * @brief Restituisce tutti i libri il cui autore contiene la parola chiave specificata.
     *
     * @param keyword Parola o parte di parola da cercare nel nome dell'autore.
     * @return Lista dei libri corrispondenti.
     */
    List<Book> findByAuthorContaining(String keyword);
    
    
    
    /**
     * @brief Restituisce tutti i libri ordinati alfabeticamente per titolo.
     *
     * @return Lista ordinata dei libri.
     */
    List<Book> findAllOrderByTitle();
    
}



