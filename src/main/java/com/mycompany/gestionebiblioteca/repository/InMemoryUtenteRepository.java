/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.repository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.mycompany.gestionebiblioteca.model.Utente;

/**
 *
 * @author david
 */
public class InMemoryUtenteRepository implements UtenteRepository{
    
    private final Map<String, Utente> storage = new HashMap<>();

    @Override
    public Utente save(Utente entity) {
       return null;
    }

    @Override
    public Optional<Utente> findById(String id) {
        return null;
    }

    @Override
    public List<Utente> findAll() {
        return null;
    }

    @Override
    public void deleteById(String id) {
        return null;
    }

    @Override
    public void deleteAll() {
        return null;
    }

    @Override
    public boolean existsById(String id) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Optional<Utente> findByMatricola(String matricola) {
        return null;
    }

    @Override
    public List<Utente> findByLastNameContaining(String keyword) {
        return null;
    }

    @Override
    public List<Utente> findAllOrderByLastNameAndFirstName() {
        return null;
    }
    
}
