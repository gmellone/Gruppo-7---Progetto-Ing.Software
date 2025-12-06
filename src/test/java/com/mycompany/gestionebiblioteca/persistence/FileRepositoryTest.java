package com.mycompany.gestionebiblioteca.persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileRepositoryTest {

    private final FileRepository repo = new FileRepository();
    private final String testFilePath = "testdata/books_test.txt";

    @AfterEach
    public void cleanUp() throws Exception {
        Path file = Path.of(testFilePath);
        Path dir = file.getParent();

        if (Files.exists(file)) {
            Files.delete(file);
        }
        if (dir != null && Files.exists(dir)) {
            Files.delete(dir);
        }
    }

    @Test
    public void testWriteAndReadAll() {
        List<String> lines = Arrays.asList("Ciao", "Mondo");
        repo.writeAll(testFilePath, lines);

        List<String> result = repo.readAll(testFilePath);
        assertEquals(lines, result);
    }

    @Test
    public void testReadNonExistingFile() {
        List<String> result = repo.readAll("testdata/missing.txt");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testOverwriteFile() {
        repo.writeAll(testFilePath, List.of("Prima"));
        repo.writeAll(testFilePath, List.of("Nuova1", "Nuova2"));

        List<String> result = repo.readAll(testFilePath);
        assertEquals(List.of("Nuova1", "Nuova2"), result);
    }
}