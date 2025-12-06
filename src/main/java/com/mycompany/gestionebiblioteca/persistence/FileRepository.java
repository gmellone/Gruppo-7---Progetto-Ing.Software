package com.mycompany.gestionebiblioteca.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileRepository {

    public void writeAll(String path, List<String> lines) {
        try {
            Path filePath = Path.of(path);
            Path tempPath = Path.of(path + ".tmp");

            // Crea la cartella se non esiste
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            // Scrive sul file temporaneo
            Files.write(tempPath, lines);

            // Rinomina (sostituzione atomica)
            Files.move(tempPath, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new FileRepositoryException("Errore durante la scrittura del file: " + path, e);
        }
    }

    public List<String> readAll(String path) {
        try {
            Path filePath = Path.of(path);

            if (!Files.exists(filePath)) {
                return Collections.emptyList();
            }

            return Files.readAllLines(filePath);

        } catch (IOException e) {
            throw new FileRepositoryException("Errore durante la lettura del file: " + path, e);
        }
    }

    // Eccezione specifica
    public static class FileRepositoryException extends RuntimeException {
        public FileRepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}