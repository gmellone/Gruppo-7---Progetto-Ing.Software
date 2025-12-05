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
 * @brief Eccezione controllata che segnala all'utente che ha raggiunto il limite massimo per i prestiti 
 * 
 * Lʼutente ha un limite di 3 libri in prestito contemporaneamente, in caso richieda un 
 * ulteriore libro in prestito il programma deve informare il bibliotecario con un messaggio che sottolinea 
 * lʼimpossibilità di richiedere un quarto libro da parte dellʼutente.
 */
public class LimiteLibriRaggiuntoException extends BibliotecaException{
    /**
     * Costruttore per l'eccezione con un solo messaggio
     * @param msg Messaggio descrittivo dell'errore
     */
    public LimiteLibriRaggiuntoException(String msg){
        super(msg);
    }
}
