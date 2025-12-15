/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.model;

/**
 *
 * @author Giovanni
 */
import java.util.Objects;

/**
 * @brief Entity che rappresenta un utente registrato (User) nel sistema bibliotecario.
 *
 * Questa classe contiene le informazioni anagrafiche degli utenti della biblioteca.
 *
 * N.B.: L'identità dell'utente è definita univocamente dalla sua **matricola**.
 * Due istanze di User con la stessa matricola sono considerate lo stesso oggetto logico,
 * indipendentemente da eventuali variazioni nel nome o email.
 * N.B.: La matricola è un attributo obbligatorio e non può essere nullo.
 */
public class User {

    /**
     * @brief Identificativo univoco dell'utente (es. Matricola Studente/Docente).
     * Rappresenta la chiave primaria naturale dell'entità. Non può essere null.
     */
    private String matricola;

    /**
     * @brief Nome di battesimo dell'utente.
     */
    private String firstName;

    /**
     * @brief Cognome dell'utente.
     */
    private String lastName;

    /**
     * @brief Indirizzo email per le comunicazioni (es. solleciti, ricevute).
     */
    private String email;

    /**
     * @brief Costruttore per la creazione di un nuovo utente.
     *
     * Inizializza l'anagrafica imponendo il vincolo di non nullità sulla matricola.
     *
     * @param matricola Il codice identificativo univoco (Obbligatorio).
     * @param firstName Il nome dell'utente.
     * @param lastName Il cognome dell'utente.
     * @param email L'indirizzo di posta elettronica.
     * @throws NullPointerException Se la matricola passata è null.
     */
    public User(String matricola, String firstName, String lastName, String email) {
        this.matricola = Objects.requireNonNull(matricola, "La matricola è obbligatoria e non può essere null");
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // --- SEZIONE GETTER ---

    /**
     * @brief Restituisce la matricola dell'utente.
     * @return Stringa contenente l'identificativo univoco.
     */
    public String getMatricola() { return matricola; }

    /**
     * @brief Restituisce il nome dell'utente.
     * @return Il nome di battesimo.
     */
    public String getFirstName() { return firstName; }

    /**
     * @brief Restituisce il cognome dell'utente.
     * @return Il cognome.
     */
    public String getLastName() { return lastName; }

    /**
     * @brief Restituisce l'email dell'utente.
     * @return L'indirizzo email registrato.
     */
    public String getEmail() { return email; }

    // --- SEZIONE SETTER ---

    /**
     * @brief Imposta o aggiorna la matricola dell'utente.
     *
     * @warning **Attenzione**: La modifica della matricola altera l'identità dell'oggetto (hashcode/equals).
     * Se l'oggetto è contenuto in strutture dati basate su hash (es. HashMap, HashSet),
     * rimuoverlo prima di modificare questo campo e reinserirlo successivamente.
     *
     * @param matricola La nuova matricola da assegnare.
     */
    public void setMatricola(String matricola) { this.matricola = matricola; }

    /**
     * @brief Aggiorna il nome dell'utente.
     * @param firstName Il nuovo nome.
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * @brief Aggiorna il cognome dell'utente.
     * @param lastName Il nuovo cognome.
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * @brief Aggiorna l'indirizzo email.
     * @param email La nuova email.
     */
    public void setEmail(String email) { this.email = email; }

    // --- METODI DI IDENTITÀ ---

    /**
     * @brief Verifica l'uguaglianza logica tra due utenti.
     *
     * Implementa il concetto di Entity Equality basato sulla **matricola**.
     *
     * @param o L'oggetto con cui effettuare il confronto.
     * @return true se gli utenti hanno la stessa matricola, false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return matricola.equals(user.matricola);
    }

    /**
     * @brief Genera l'hash code dell'utente.
     *
     * L'hash è calcolato esclusivamente sulla matricola per garantire la coerenza
     * con il metodo equals().
     *
     * @return Un intero che rappresenta l'hash della matricola.
     */
    @Override
    public int hashCode() {
        return Objects.hash(matricola);
    }
}