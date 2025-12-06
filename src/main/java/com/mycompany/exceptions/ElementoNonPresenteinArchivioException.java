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
 * @brief Eccezione controllata che viene lanciata quando un elemento non viene trovato in archivio.
 * 
 *  In caso di tentata ricerca, rimozione o modifica di un libro o di un utente non presente nellʼarchivio deve 
 *  mandare un messaggio di errore notificando lʼassenza di tale libro o di tale utente.
 */
public class ElementoNonPresenteinArchivioException extends BibliotecaException {
    /**
     * Costruttore per l'eccezione con il solo messaggio
     * @param msg Messaggio descrittivo dell'errore
     */
    public ElementoNonPresenteinArchivioException(String msg){
        super(msg);
    }
}
