/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.service;

import com.mycompany.gestionebiblioteca.exceptions.LimiteLibriRaggiuntoException;
import com.mycompany.gestionebiblioteca.exceptions.UtenteGiaRegistratoException;
import com.mycompany.gestionebiblioteca.model.Utente;
import com.mycompany.gestionebiblioteca.persistence.UtenteRepository;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author david
 */
public class UserService {
    
    

    private final UtenteRepository utenteRepository;

    public UserService(UtenteRepository utenteRepository) {
       
        this.utenteRepository = utenteRepository;
    }

    public Utente addUser(String matricola, String Nome, String cognome, String email) {
       return null;
    }

    public Utente  updateUser(String matricola, String Nome, String Cognome, String email) {
        return null;
    }

    public void deleteUser(String matricola) {
       
    }

    public Optional<Utente> getUserByMatricola(String matricola) {
       
        return null;
    }

    public List<Utente> searchByLastName(String keyword) {
        return null;
    }

    public List<Utente> getAllUsersOrderedByLastNameAndFirstName() {
        return null;
    }

    private void validateMatricola(String matricola) {
       
    }

    private void validateEmail(String email) {
       
    }

    private boolean emailInUse(String email, String currentMatricola) {
      return false;
    }


    
       
    
}
