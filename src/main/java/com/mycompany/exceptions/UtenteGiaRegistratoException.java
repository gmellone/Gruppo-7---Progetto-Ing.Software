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
 * @brief Eccezione controllata che segnala che l'utente che si vuole registrare è già presente in archivio.
 * 
 *  In caso di tentata registrazione di un utente già registrato il sistema deve inviare un 
 *  messaggio che informa il bibliotecario della presenza dellʼutente nel sistema e non permettere 
 *  lʼinserimento dellʼutente già registrato.
 */
public class UtenteGiaRegistratoException extends BibliotecaException{
    /**
     * Costruttore per l'eccezione con il solo messaggio
     * @param msg Messaggio descrittivo dell'errore
     */
    public UtenteGiaRegistratoException(String msg){
        super(msg);
    }
}
