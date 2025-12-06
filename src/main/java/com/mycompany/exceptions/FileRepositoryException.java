package com.mycompany.exceptions;

/**
 *
 * @author Valeria
 */


public class FileRepositoryException extends RuntimeException {

    public FileRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

/*
- estende RuntimeException → non serve scrivere throws ovunque
- prende un message e una cause (la IOException originale) così non perdiamo il dettaglio tecnico.

*/