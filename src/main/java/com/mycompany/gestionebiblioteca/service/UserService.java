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
/**
 * @brief Service per la gestione delle anagrafiche utenti.
 *
 * Questa classe gestisce il ciclo di vita degli utenti (studenti), occupandosi
 * della validazione dei dati (formato matricola, email istituzionale) e
 * garantendo l'unicità dei campi chiave. Interagisce con il LoanService per
 * impedire modifiche strutturali o cancellazioni su utenti con prestiti attivi.
 */
public class UserService {

    private final UserRepository userRepository;
    private final LoanService loanService;

    /**
     * @brief Costruttore con iniezione delle dipendenze.
     *
     * @param userRepository Repository per l'accesso ai dati degli utenti.
     * @param loanService Service per verificare lo stato dei prestiti associati
     * agli utenti.
     * @throws IllegalArgumentException Se uno dei parametri è null.
     */
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

    /**
     * @brief Crea e registra un nuovo utente nel sistema.
     *
     * Verifica che la matricola e l'email non siano già utilizzate da altri
     * utenti. Impone vincoli sul formato della matricola e sul dominio
     * dell'email.
     *
     * @param matricola Identificativo univoco (10 cifre).
     * @param firstName Nome dell'utente.
     * @param lastName Cognome dell'utente.
     * @param email Email istituzionale (@studenti.unisa.it).
     * @return L'utente appena creato e salvato.
     * @throws ValidationException Se i dati non sono validi o se
     * matricola/email esistono già.
     */
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

    /**
     * @brief Modifica i dati di un utente esistente.
     *
     * Gestisce due scenari principali: 1. **Cambio Matricola:** Richiede la
     * cancellazione del vecchio record e l'inserimento del nuovo. Questa
     * operazione è bloccata se l'utente ha prestiti attivi. 2. **Aggiornamento
     * Standard:** Aggiorna nome, cognome o email mantenendo la stessa chiave
     * primaria.
     *
     * Verifica inoltre che la nuova email non sia già in uso da un altro
     * utente.
     *
     * @param oldMatricola La matricola attuale dell'utente.
     * @param newMatricola La nuova matricola (può coincidere con oldMatricola).
     * @param firstName Nuovo nome.
     * @param lastName Nuovo cognome.
     * @param email Nuova email.
     * @return L'istanza dell'utente aggiornato.
     * @throws NotFoundException Se l'utente originale non viene trovato.
     * @throws ValidationException Se i nuovi dati non sono validi, se la nuova
     * matricola esiste già o se l'operazione è bloccata da prestiti attivi.
     */
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
            // PRIMO SCENARIO: LA MATRICOLA CAMBIA 

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
            // SECONDO SCENARIO : LA MATRICOLA NON CAMBIA 

            // Aggiorno i campi sull'oggetto esistente
            existing.setFirstName(firstName);
            existing.setLastName(lastName);
            existing.setEmail(email);

            return userRepository.save(existing);
        }
    }

    /**
     * @brief Ricerca utenti tramite una parola chiave intelligente.
     *
     * La logica di ricerca è ibrida: - Se la keyword è composta da 10 cifre,
     * viene interpretata come una matricola (ricerca esatta). - Altrimenti,
     * viene interpretata come una ricerca parziale sul cognome.
     *
     * @param keyword Parola chiave (matricola o cognome).
     * @return Lista di utenti corrispondenti. Restituisce tutti gli utenti se
     * la keyword è vuota.
     */
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

    /**
     * @brief Elimina un utente dal sistema.
     *
     * L'operazione viene bloccata se l'utente ha ancora libri in prestito non
     * restituiti.
     *
     * @param matricola La matricola dell'utente da eliminare.
     * @throws NotFoundException Se l'utente non esiste.
     * @throws ValidationException Se l'utente ha prestiti attivi.
     */
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

    /**
     * @brief Recupera un utente tramite matricola.
     * @param matricola L'identificativo da cercare.
     * @return Optional contenente l'utente se trovato.
     */
    public Optional<User> getUserByMatricola(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByMatricola(matricola);
    }

    /**
     * @brief Cerca utenti il cui cognome contiene la stringa specificata.
     * @param keyword Parte del cognome da cercare.
     * @return Lista di utenti trovati.
     */
    public List<User> searchByLastName(String keyword) {
        return userRepository.findByLastNameContaining(keyword);
    }

    /**
     * @brief Restituisce l'elenco completo degli utenti.
     * @return Lista ordinata per Cognome e poi per Nome.
     */
    public List<User> getAllUsersOrderedByLastNameAndFirstName() {
        return userRepository.findAllOrderByLastNameAndFirstName();
    }

    // 
    /**
     * @brief Valida il formato della matricola.
     * @details Deve essere non nulla e composta esattamente da 10 cifre.
     * @param matricola La stringa da verificare.
     */
    private void validateMatricola(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            throw new ValidationException("La matricola non deve essere vuota");
        }
        if (!matricola.matches("\\d{10}")) {
            throw new ValidationException("La matricola deve essere 10 cifre");
        }
    }

    /**
     * @brief Valida il formato e il dominio dell'email.
     * @details Richiede che l'email termini con il dominio istituzionale
     * "@studenti.unisa.it".
     * @param email L'indirizzo email da verificare.
     * @throws ValidationException Se il formato è errato o il dominio non
     * corrisponde.
     */
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

    /**
     * @brief Verifica se un indirizzo email è già utilizzato da un altro
     * utente.
     *
     * @param email L'email da controllare.
     * @param currentMatricola La matricola dell'utente corrente (opzionale). Se
     * specificata, ignora l'utente stesso durante il controllo (utile in fase
     * di aggiornamento).
     * @return true se l'email è già in uso da terzi, false altrimenti.
     */
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
