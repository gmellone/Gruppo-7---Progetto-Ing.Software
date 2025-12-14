/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.exceptions;

/**
 * @file NotFoundException.java
 * 
 * @author Carmine
 *
 * @brief Eccezione controllata lanciata quando una entity non viene trovata.
 *
 * L'eccezione NotFoundException viene lanciata quando una entity
 * (ad esempio Book o User) non viene trovata durante
 * un'operazione di ricerca.
 */

public class NotFoundException extends RuntimeException {

    /**
     * Crea una nuova eccezione di tipo NOT FOUND con il messaggio specificato.
     * @param message Descrizione della risorsa non trovata.
     */
    
    public NotFoundException(String message) {
        super(message);
    }
}

