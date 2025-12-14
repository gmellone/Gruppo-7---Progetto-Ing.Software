/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.exceptions;

/**
 * @file ValidationException.java
 * 
 * @author Carmine
 *
 * @brief Eccezione controllata di validazione per i campi delle entity.
 *
 * L'eccezione ValidationException viene lanciata quando un campo
 * (ISBN per Book, email o matricola per User) non rispetta gli
 * standard di formato previsti. 
 * Ad esempio: ISBN a 13 cifre, matricola a 10 cifre, email senza '@'.
 */

public class ValidationException extends RuntimeException {

    /**
     * Crea una nuova eccezione di validazione con il messaggio specificato.
     * @param message Descrizione del problema di validazione riscontrato.
     */
    
    public ValidationException(String message) {
        super(message);
    }
}

