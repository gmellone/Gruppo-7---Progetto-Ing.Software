/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.exceptions;

/**
 *
 * @author Carmine
 */

/**
 * @brief Eccezione controllata che segnala che un libro non ha copie in archivio
 * 
 * In caso lʼutente abbia richiesto il prestito su un libro che non ha copie in biblioteca, il sistema 
 * deve mandare un messaggio che informa il bibliotecario che il libro richiesto non ha copie disponibili.
 * 
 */
public class CopieTerminateException extends BibliotecaException {
    /**
     * Costruttore per l'eccezione con il solo messaggio
     * @param msg Messaggio descrittivo dell'errore
     */
    public CopieTerminateException(String msg){
        super(msg);
    }
}
