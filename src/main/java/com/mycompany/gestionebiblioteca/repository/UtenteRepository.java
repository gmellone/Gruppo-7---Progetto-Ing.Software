package com.mycompany.gestionebiblioteca.repository;
/**
 * @interface UtenteRepository
 * @brief Interfaccia specializzata per la gestione degli utenti della biblioteca.
 *
 * estende l'interfaccia generica CrudRepository aggiungendo metodi specifici
 * per cercare e ordinare gli utenti in base ai loro dati anagrafici.
 * 
 * le implementazioni di questa interfaccia devono assicurare che:
 * 1) la matricola di ogni utente sia unica
 * 2) i dati restituiti siano sempre coerenti e aggiornati
 * 3) vengano rispettate tutte le operazioni previste dai metodi CRUD
 * 4) i metodi di ricerca e ordinamento producano risultati prevedibili 
 * 
 * questa interfaccia quindi definisce che ogni repository dedicato agli 
 * utenti deve seguire, indipendentemente da come i dati vengono salvati (in memoria, su file, 
 * o su un database)
 * 
 */
import com.mycompany.gestionebiblioteca.model.Utente;
import java.util.List;
import java.util.Optional;       

/**
 *
 * @author valerialupo
 */
public interface UtenteRepository extends CrudRepository<String, Utente> {
    
    
    
    /**
     * @brief Cerca un utente tramite la sua matricola.
     *
     * La matricola è considerata un identificatore univoco per l’utente.
     *
     * @param matricola Matricola dell’utente da cercare.
     * @return Optional contenente l’utente trovato; empty() se la matricola non esiste.
     */
    Optional<Utente> findByMatricola(String matricola);
    
    
    
    
    /**
     * @brief Restituisce gli utenti il cui cognome contiene la parola chiave specificata.
     *
     * Il confronto può essere implementato come case-sensitive o case-insensitive,
     * a seconda della scelta dell’implementazione concreta.
     *
     * @param keyword Frammento del cognome da cercare.
     * @return Lista degli utenti il cui cognome contiene la parola chiave.
     */
    List<Utente> findByLastNameContaining(String keyword);

    
    
    
    
    /**
     * @brief Restituisce tutti gli utenti ordinati per cognome e poi per nome.
     *
     * Il criterio di ordinamento è:
     * 1. Ordine alfabetico crescente del cognome
     * 2. Ordine alfabetico crescente del nome
     *
     * Questo metodo permette di ottenere un elenco strutturato di utenti,
     * tipicamente utile per visualizzazioni in GUI o stampe.
     *
     * @return Lista ordinata degli utenti.
     */
    List<Utente> findAllOrderByLastNameAndFirstName();
    
}




