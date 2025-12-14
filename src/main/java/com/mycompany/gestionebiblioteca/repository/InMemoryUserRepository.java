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
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> storage = new HashMap<>();

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

    @Override
    public Optional<User> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(String id) {
        if (id == null) {
            return;
        }
        storage.remove(id);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public Optional<User> findByMatricola(String matricola) {
        return findById(matricola);
    }

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
        result.sort(Comparator
                .comparing(User::getLastName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(User::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }

    @Override
    public List<User> findAllOrderByLastNameAndFirstName() {
        List<User> result = new ArrayList<>(storage.values());
        result.sort(Comparator
                .comparing(User::getLastName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(User::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }
}
