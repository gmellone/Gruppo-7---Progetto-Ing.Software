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
import com.mycompany.gestionebiblioteca.model.User;

/**
 *
 * @author david
 */
public class InMemoryUtenteRepository implements UserRepository{
    
    private final Map<String, User> storage = new HashMap<>();

    @Override
    public User save(User entity) {
       return null;
    }

    @Override
    public Optional<User> findById(String id) {
        return null;
    }

    @Override
    public List<User> findAll() {
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
    public Optional<User> findByMatricola(String matricola) {
        return null;
    }

    @Override
    public List<User> findByLastNameContaining(String keyword) {
        return null;
    }

    @Override
    public List<User> findAllOrderByLastNameAndFirstName() {
        return null;
    }
    
}
