/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.repository;

/**
 *
 * @author Giovanni
 */
import com.mycompany.gestionebiblioteca.model.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @brief Implementazione in memoria del repository per la gestione dei libri.
 *
 * Questa classe fornisce un'implementazione concreta dell'interfaccia BookRepository
 * utilizzando una struttura dati in memoria (HashMap).
 */
public class InMemoryBookRepository implements BookRepository {

    /**
     * @brief Storage interno basato su HashMap.
     * Mappa l'ISBN (chiave univoca) all'oggetto Book (valore).
     */
    private final Map<String, Book> storage = new HashMap<>();

    /**
     * @brief Salva o aggiorna un libro nel repository.
     *
     * - Se l'ISBN non esiste, il libro viene aggiunto.
     * - Se l'ISBN esiste già, il record precedente viene sovrascritto.
     *
     * @param entity L'entità libro da salvare.
     * @return L'entità appena salvata.
     * @throws IllegalArgumentException Se l'entità è null o se l'ISBN è null.
     */
    @Override
    public Book save(Book entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Libro non deve essere null");
        }
        String isbn = entity.getIsbn();
        if (isbn == null) {
            throw new IllegalArgumentException("isbn non deve essere null");
        }
        storage.put(isbn, entity);
        return entity;
    }

    /**
     * @brief Recupera un libro tramite il suo identificativo (ISBN).
     *
     * @param id L'ISBN del libro da cercare.
     * @return Un Optional contenente il libro se presente, altrimenti vuoto.
     */
    @Override
    public Optional<Book> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * @brief Restituisce l'intera collezione di libri.
     *
     * Crea una nuova ArrayList per evitare di esporre la struttura interna del repository.
     *
     * @return Lista di tutti i libri presenti.
     */
    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * @brief Rimuove un libro dal repository.
     *
     * @param id L'ISBN del libro da rimuovere. Se null, l'operazione viene ignorata.
     */
    @Override
    public void deleteById(String id) {
        if (id == null) {
            return;
        }
        storage.remove(id);
    }

    /**
     * @brief Svuota completamente il repository.
     * Rimuove tutti i libri memorizzati.
     */
    @Override
    public void deleteAll() {
        storage.clear();
    }

    /**
     * @brief Verifica l'esistenza di un libro.
     *
     * @param id L'ISBN da controllare.
     * @return true se il libro esiste, false altrimenti.
     */
    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    /**
     * @brief Conta il numero totale di libri nel repository.
     * @return Il numero di elementi presenti.
     */
    @Override
    public long count() {
        return storage.size();
    }

    /**
     * @brief Alias per findById specifico per il dominio (ricerca per ISBN).
     * @param isbn Il codice ISBN.
     * @return Optional con il libro trovato.
     */
    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return findById(isbn);
    }

    /**
     * @brief Cerca libri il cui titolo contiene la parola chiave specificata.
     *
     * N.B.: La ricerca è **case-insensitive**.
     *
     * @param keyword La stringa da cercare nel titolo.
     * @return Lista di libri corrispondenti, ordinata alfabeticamente per titolo.
     */
    @Override
    public List<Book> findByTitleContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book book : storage.values()) {
            String title = book.getTitle();
            if (title != null && title.toLowerCase().contains(lowerKeyword)) {
                result.add(book);
            }
        }
        result.sort(Comparator.comparing(Book::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }

    /**
     * @brief Cerca libri in base agli autori.
     *
     * Verifica se almeno uno degli autori del libro contiene la parola chiave specificata.
     * La ricerca è case-insensitive.
     *
     * Criterio di ordinamento:
     * 1. Alfabetico per il **primo autore** della lista (Primary Sort).
     * 2. Alfabetico per titolo (Secondary Sort).
     *
     * @param keyword La stringa da cercare nei nomi degli autori.
     * @return Lista di libri corrispondenti.
     */
    @Override
    public List<Book> findByAuthorContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Book> result = new ArrayList<>();
        
        // Iterazione su tutti i libri
        for (Book book : storage.values()) {
            List<String> authors = book.getAuthors();
            if (authors != null) {
                // Controllo se ALMENO UN autore matcha la keyword
                for (String author : authors) {
                    if (author != null && author.toLowerCase().contains(lowerKeyword)) {
                        result.add(book);
                        break; // Trovato un match, aggiungo il libro ed esco dal loop autori
                    }
                }
            }
        }
        
        // Ordinamento complesso: usa il primo autore della lista, se esiste
        result.sort(Comparator.comparing(
                (Book b) -> b.getAuthors().isEmpty() ? "" : b.getAuthors().get(0),
                Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(Book::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }

    /**
     * @brief Restituisce tutti i libri ordinati per titolo.
     *
     * @return Lista completa dei libri ordinata alfabeticamente.
     */
    @Override
    public List<Book> findAllOrderByTitle() {
        List<Book> result = new ArrayList<>(storage.values());
        result.sort(Comparator.comparing(Book::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }
}
