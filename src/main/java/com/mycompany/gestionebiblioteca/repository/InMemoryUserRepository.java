/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.mycompany.gestionebiblioteca.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author david
 */
/**
 * @brief Implementazione del repository degli utenti.
 */
public class InMemoryUserRepository implements UserRepository {

    /**
     * @brief Struttura dati per memorizzare gli utenti (Chiave: Matricola, Valore: User).
     */
    private final Map<String, User> storage = new HashMap<>();

    /**
     * @brief Salva o aggiorna un utente nel repository.
     *
     * Se un utente con la stessa matricola esiste già, viene sovrascritto.
     *
     * @param entity L'oggetto User da salvare.
     * @return L'entità salvata (identica a quella passata in input).
     * @throws IllegalArgumentException Se l'entità o la matricola sono null.
     */
    @Override
    public User save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        String matricola = entity.getMatricola();
        if (matricola == null) {
            throw new IllegalArgumentException("matricola must not be null");
        }
        storage.put(matricola, entity);
        return entity;
    }

    /**
     * @brief Cerca un utente tramite il suo identificativo (matricola).
     *
     * @param id La matricola dell'utente da cercare.
     * @return Un Optional contenente l'utente se trovato, altrimenti vuoto.
     */
    @Override
    public Optional<User> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * @brief Restituisce tutti gli utenti presenti nel repository.
     *
     * @return Una lista contenente tutti gli utenti memorizzati (senza un ordine garantito).
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * @brief Rimuove un utente specificando il suo ID.
     *
     * @param id La matricola dell'utente da rimuovere.
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
     *
     * Rimuove tutte le entità memorizzate nella mappa.
     */
    @Override
    public void deleteAll() {
        storage.clear();
    }

    /**
     * @brief Verifica l'esistenza di un utente nel repository.
     *
     * @param id La matricola da verificare.
     * @return true se l'utente esiste, false altrimenti (o se l'id è null).
     */
    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    /**
     * @brief Conta il numero totale di utenti nel repository.
     *
     * @return Il numero di entità salvate.
     */
    @Override
    public long count() {
        return storage.size();
    }

    /**
     * @brief Alias per findById specifico per la semantica del dominio User.
     *
     * @param matricola La matricola da cercare.
     * @return Optional contenente l'utente se trovato.
     */
    @Override
    public Optional<User> findByMatricola(String matricola) {
        return findById(matricola);
    }

    /**
     * @brief Cerca utenti il cui cognome contiene la parola chiave specificata.
     *
     * La ricerca è case-insensitive (non distingue maiuscole/minuscole).
     * I risultati vengono ordinati alfabeticamente per Cognome e poi per Nome.
     *
     * @param keyword Parte del cognome da cercare.
     * @return Lista ordinata di utenti che soddisfano il criterio.
     */
    @Override
    public List<User> findByLastNameContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        List<User> result = new ArrayList<>();
        for (User user : storage.values()) {
            String lastName = user.getLastName();
            if (lastName != null && lastName.toLowerCase().contains(lowerKeyword)) {
                result.add(user);
            }
        }
        // Ordinamento dei risultati
        result.sort(Comparator
                .comparing(User::getLastName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(User::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }

    /**
     * @brief Restituisce tutti gli utenti con ordinamento specifico.
     *
     * Questo metodo garantisce che la lista restituita
     * sia ordinata per Cognome e poi per Nome.
     *
     * @return Lista ordinata di tutti gli utenti.
     */
    @Override
    public List<User> findAllOrderByLastNameAndFirstName() {
        List<User> result = new ArrayList<>(storage.values());
        result.sort(Comparator
                .comparing(User::getLastName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(User::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }
}