/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.exceptions;

/**
 *
 * @author Carmine
 */

/**
 * @brief Eccezione controllata di base per le operazioni di gestione della biblioteca 
 * 
 */
public class BibliotecaException extends Exception{
    /**
     * Costruttore per l'eccezione con un solo messaggio
     * @param msg Messaggio descrittivo dell'errore
     */
    public BibliotecaException(String msg){
        super(msg);
    }
}
