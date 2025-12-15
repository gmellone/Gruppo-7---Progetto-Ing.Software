package com.mycompany.gestionebiblioteca.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.gestionebiblioteca.model.Book;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryBookRepositoryTest {

    private InMemoryBookRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBookRepository();
    }

    private Book createBook(String isbn, String title, String author) {
        return new Book(isbn, title, Arrays.asList(author), 2020, 5, 5);
    }

    @Test
    void saveAndFindByIdShouldStoreAndReturnBook() {
        Book book = createBook("9781234567890", "Title", "Author");

        repository.save(book);
        Optional<Book> result = repository.findById("9781234567890");

        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    void saveNullBookShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void existsByIdAndCountShouldReflectStoredBooks() {
        assertEquals(0, repository.count());
        assertFalse(repository.existsById("9781234567890"));

        Book book1 = createBook("9781234567890", "Title1", "Author1");
        Book book2 = createBook("9781234567891", "Title2", "Author2");
        repository.save(book1);
        repository.save(book2);

        assertEquals(2, repository.count());
        assertTrue(repository.existsById("9781234567890"));
        assertTrue(repository.existsById("9781234567891"));
        assertFalse(repository.existsById("9780000000000"));
    }

    @Test
    void deleteByIdShouldRemoveBook() {
        Book book = createBook("9781234567890", "Title", "Author");
        repository.save(book);
        assertTrue(repository.existsById("9781234567890"));

        repository.deleteById("9781234567890");

        assertFalse(repository.existsById("9781234567890"));
        assertTrue(!repository.findById("9781234567890").isPresent());
    }

    @Test
    void deleteAllShouldClearRepository() {
        repository.save(createBook("9781234567890", "Title1", "Author1"));
        repository.save(createBook("9781234567891", "Title2", "Author2"));
        assertEquals(2, repository.count());

        repository.deleteAll();

        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findByIsbnShouldReturnMatchingBook() {
        Book book = createBook("9781234567890", "Title", "Author");
        repository.save(book);

        Optional<Book> result = repository.findByIsbn("9781234567890");

        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    void findByIsbnOnEmptyRepositoryShouldReturnEmptyOptional() {
        Optional<Book> result = repository.findByIsbn("9781234567890");
        assertTrue(!result.isPresent());
    }

    @Test
    void findByTitleContainingShouldBeCaseInsensitiveAndOrderedByTitle() {
        Book book1 = createBook("9781234567890", "Java Basics", "Author1");
        Book book2 = createBook("9781234567891", "Advanced Java", "Author2");
        Book book3 = createBook("9781234567892", "Python Intro", "Author3");
        repository.save(book1);
        repository.save(book2);
        repository.save(book3);

        List<Book> result = repository.findByTitleContaining("java");

        assertEquals(2, result.size());
        assertEquals("Advanced Java", result.get(0).getTitle());
        assertEquals("Java Basics", result.get(1).getTitle());
    }

    @Test
    void findByTitleContainingWithBlankKeywordShouldReturnEmptyList() {
        repository.save(createBook("9781234567890", "Java Basics", "Author1"));

        List<Book> result = repository.findByTitleContaining("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByAuthorContainingShouldBeCaseInsensitiveAndOrdered() {
        Book book1 = createBook("9781234567890", "C Title", "Alice");
        Book book2 = createBook("9781234567891", "A Title", "Alice");
        repository.save(book1);
        repository.save(book2);

        List<Book> result = repository.findByAuthorContaining("alice");

        assertEquals(2, result.size());
        assertEquals("A Title", result.get(0).getTitle());
        assertEquals("C Title", result.get(1).getTitle());
    }

    @Test
    void findAllOrderByTitleShouldReturnAllBooksSortedByTitle() {
        Book book1 = createBook("9781234567890", "C Title", "Author1");
        Book book2 = createBook("9781234567891", "A Title", "Author2");
        Book book3 = createBook("9781234567892", "B Title", "Author3");
        repository.save(book1);
        repository.save(book2);
        repository.save(book3);

        List<Book> result = repository.findAllOrderByTitle();

        assertEquals(3, result.size());
        assertEquals("A Title", result.get(0).getTitle());
        assertEquals("B Title", result.get(1).getTitle());
        assertEquals("C Title", result.get(2).getTitle());
    }
}
