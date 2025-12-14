/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.service;

import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.model.User;
import com.mycompany.gestionebiblioteca.repository.UserRepository;
import com.mycompany.gestionebiblioteca.service.LoanService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author david
 */
public class UserService {

    private final UserRepository userRepository;
    private final LoanService loanService;

    public UserService(UserRepository userRepository, LoanService loanService) {
        if (userRepository == null) {
            throw new IllegalArgumentException("userRepository non deve essere null");
        }
        this.userRepository = userRepository;
        if (loanService == null) {
            throw new IllegalArgumentException("loanService non deve essere null");
        }
        this.loanService = loanService;
    }

    public User addUser(String matricola, String firstName, String lastName, String email) {
        validateMatricola(matricola);
        validateEmail(email);

        if (userRepository.existsById(matricola)) {
            throw new ValidationException("L'utente con matricola " + matricola + " già esiste");
        }
        if (emailInUse(email, null)) {
            throw new ValidationException("Email " + email + " è già in uso");
        }

        User user = new User(matricola, firstName, lastName, email);
        return userRepository.save(user);
    }

    public User updateUser(String oldMatricola, String newMatricola, String firstName, String lastName, String email) {

        //  Validazioni dei campi
        validateMatricola(newMatricola);
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("Il nome non può essere vuoto");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Il cognome non può essere vuoto");
        }

        // Validazione Email specifica
        validateEmail(email);

        // Cerco l'utente esistente
        User existing = userRepository.findById(oldMatricola)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con matricola " + oldMatricola));

        // Controllo duplicati Email (escludendo l'utente stesso)
        // Nota: Ho integrato il controllo qui per mantenere lo stile compatto
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email.trim()) && !u.getMatricola().equals(oldMatricola));
        if (emailExists) {
            throw new ValidationException("Email " + email + " già in uso da un altro utente");
        }

        //  Controllo se la Matricola sta cambiando
        boolean matricolaChanged = !oldMatricola.equals(newMatricola);

        if (matricolaChanged) {
            // --- SCENARIO A: LA MATRICOLA CAMBIA (Delete & Re-Insert) ---

            // Verifico che la NUOVA matricola non sia già occupata
            if (userRepository.existsById(newMatricola)) {
                throw new ValidationException("Utente con matricola " + newMatricola + " esiste già.");
            }

            // Creo un nuovo oggetto con TUTTI i dati aggiornati
            User newUser = new User(newMatricola, firstName, lastName, email);

            // Cancello il vecchio e salvo il nuovo
            // ATTENZIONE: Se l'utente ha prestiti attivi, questo potrebbe fallire (come per i libri)
            long currentlyLoan = loanService.countActiveLoansForUser(oldMatricola);
            if (currentlyLoan > 0) {
                throw new ValidationException("L'utente con matricola " + oldMatricola + "ha dei prestiti attivi,impossibile modificare");
            }
            userRepository.deleteById(existing.getMatricola());
            return userRepository.save(newUser);

        } else {
            // --- SCENARIO B: LA MATRICOLA NON CAMBIA (Update Standard) ---

            // Aggiorno i campi sull'oggetto esistente
            existing.setFirstName(firstName);
            existing.setLastName(lastName);
            existing.setEmail(email);

            return userRepository.save(existing);
        }
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsersOrderedByLastNameAndFirstName();
        }

        String term = keyword.trim();

        // Controllo se è una matricola 
        if (term.matches("\\d{10}")) {
            // Cerco per matricola. 
            return userRepository.findByMatricola(term).map(Arrays::asList).orElse(Collections.emptyList()); // Se non c'è, lista vuota
        } else {
            // Se non sembra una matricola, cerco per cognome
            return userRepository.findByLastNameContaining(term);
        }
    }

    public void deleteUser(String matricola) {
        validateMatricola(matricola);

        if (!userRepository.existsById(matricola)) {
            throw new NotFoundException("Utente con matricola " + matricola + "\nnon trovato");
        }
        long currentlyLoan = loanService.countActiveLoansForUser(matricola);
        if (currentlyLoan > 0) {
            throw new ValidationException("L'utente con matricola " + matricola + "ha dei prestiti attivi,impossibile rimuovere");
        }
        userRepository.deleteById(matricola);
    }

    public Optional<User> getUserByMatricola(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByMatricola(matricola);
    }

    public List<User> searchByLastName(String keyword) {
        return userRepository.findByLastNameContaining(keyword);
    }

    public List<User> getAllUsersOrderedByLastNameAndFirstName() {
        return userRepository.findAllOrderByLastNameAndFirstName();
    }

    private void validateMatricola(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            throw new ValidationException("La matricola non deve essere vuota");
        }
        if (!matricola.matches("\\d{10}")) {
            throw new ValidationException("La matricola deve essere 10 cifre");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("L'email non deve essere vuota");
        }
        String trimmed = email.trim().toLowerCase();

        if (!trimmed.contains("@")) {
            throw new ValidationException("Formato email non valido");
        }

        if (!trimmed.endsWith("@studenti.unisa.it")) {
            throw new ValidationException("L'email deve essere istituzionale (@studenti.unisa.it)");
        }
    }

    private boolean emailInUse(String email, String currentMatricola) {
        String target = email.trim();
        for (User user : userRepository.findAll()) {
            if (user.getEmail() != null && user.getEmail().trim().equalsIgnoreCase(target)) {
                if (currentMatricola == null || !user.getMatricola().equals(currentMatricola)) {
                    return true;
                }
            }
        }
        return false;
    }

}
