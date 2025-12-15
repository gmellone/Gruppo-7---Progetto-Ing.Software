/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.gestionebiblioteca.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    private User createUser(String matricola, String firstName, String lastName, String email) {
        return new User(matricola, firstName, lastName, email);
    }

    @Test
    void saveAndFindByIdShouldStoreAndReturnUser() {
        User user = createUser("1234567890", "Mario", "Rossi", "mario@example.com");

        repository.save(user);
        Optional<User> result = repository.findById("1234567890");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void saveNullUserShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void existsByIdAndCountShouldReflectStoredUsers() {
        assertEquals(0, repository.count());
        assertFalse(repository.existsById("1234567890"));

        User user1 = createUser("1234567890", "Mario", "Rossi", "mario@example.com");
        User user2 = createUser("1234567891", "Luigi", "Bianchi", "luigi@example.com");
        repository.save(user1);
        repository.save(user2);

        assertEquals(2, repository.count());
        assertTrue(repository.existsById("1234567890"));
        assertTrue(repository.existsById("1234567891"));
        assertFalse(repository.existsById("0000000000"));
    }

    @Test
    void deleteByIdShouldRemoveUser() {
        User user = createUser("1234567890", "Mario", "Rossi", "mario@example.com");
        repository.save(user);
        assertTrue(repository.existsById("1234567890"));

        repository.deleteById("1234567890");

        assertFalse(repository.existsById("1234567890"));
        assertTrue(!repository.findById("1234567890").isPresent());
    }

    @Test
    void deleteAllShouldClearRepository() {
        repository.save(createUser("1234567890", "Mario", "Rossi", "mario@example.com"));
        repository.save(createUser("1234567891", "Luigi", "Bianchi", "luigi@example.com"));
        assertEquals(2, repository.count());

        repository.deleteAll();

        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findByLastNameContainingShouldBeCaseInsensitiveAndOrdered() {
        User user1 = createUser("1234567890", "Mario", "Bianchi", "mario@example.com");
        User user2 = createUser("1234567891", "Anna", "Bianchi", "anna@example.com");
        User user3 = createUser("1234567892", "Paolo", "Verdi", "paolo@example.com");
        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        List<User> result = repository.findByLastNameContaining("bianchi");

        assertEquals(2, result.size());
        assertEquals("Anna", result.get(0).getFirstName());
        assertEquals("Mario", result.get(1).getFirstName());
    }

    @Test
    void findByLastNameContainingWithBlankKeywordShouldReturnEmptyList() {
        repository.save(createUser("1234567890", "Mario", "Rossi", "mario@example.com"));

        List<User> result = repository.findByLastNameContaining("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllOrderByLastNameAndFirstNameShouldReturnAllUsersSorted() {
        User user1 = createUser("1234567890", "Paolo", "Verdi", "paolo@example.com");
        User user2 = createUser("1234567891", "Anna", "Bianchi", "anna@example.com");
        User user3 = createUser("1234567892", "Luca", "Bianchi", "luca@example.com");
        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        List<User> result = repository.findAllOrderByLastNameAndFirstName();

        assertEquals(3, result.size());
        assertEquals("Anna", result.get(0).getFirstName());
        assertEquals("Luca", result.get(1).getFirstName());
        assertEquals("Paolo", result.get(2).getFirstName());
    }
}
