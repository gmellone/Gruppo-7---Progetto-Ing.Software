package com.mycompany.gestionebiblioteca.repository;

import java.util.List;
import java.util.Optional;

/**
 * @interface CrudRepository
 * @brief Interfaccia generica per le operazioni CRUD di base.
 *
 * Questa interfaccia definisce il contratto fondamentale per la gestione
 * di entità di qualsiasi tipo, identificabili tramite una chiave di tipo ID.
 * 
 * Le implementazioni possono essere basate su memoria, file system o database.
 *
 * @param <ID> Tipo dell'identificatore univoco dell'entità.
 * @param <T> Tipo dell'entità gestita dal repository.
 */

/**
 *
 * @author valerialupo
 */

public interface CrudRepository<ID, T> {
    
    
    /**
     * @brief Salva un'entità nel repository.
     *
     * Se l'entità esiste già, viene aggiornata; altrimenti viene creata.
     *
     * @param entity L'entità da salvare.
     * @return L'entità salvata, eventualmente modificata.
     */
    T save(T entity); 

    
    
    /**
     * @brief Recupera un'entità tramite il suo ID.
     *
     * @param id Identificatore dell'entità da cercare.
     * @return Un Optional contenente l'entità trovata, o empty() se non esiste.
     */
    Optional<T> findById(ID id); 
    
    
    
    /**
     * @brief Restituisce tutte le entità presenti nel repository.
     *
     * @return Lista completa delle entità.
     */
    List<T> findAll();
    
    
    
    /**
     * @brief Elimina l'entità associata all'ID specificato.
     *
     * Se l'entità non esiste, l'operazione non ha effetto.
     *
     * @param id Identificatore dell'entità da rimuovere.
     */
    void deleteById(ID id); 
    
    
    /**
     * @brief Elimina tutte le entità presenti nel repository.
     */
    void deleteAll();
    
    
    /**
     * @brief Verifica se un'entità esiste nel repository tramite ID.
     *
     * @param id Identificatore da controllare.
     * @return true se l'entità esiste, false altrimenti.
     */
    boolean existsById(ID id); 
    
    
    /**
     * @brief Restituisce il numero totale di entità presenti.
     *
     * @return Quantità delle entità gestite.
     */
    long count();
    
    
}