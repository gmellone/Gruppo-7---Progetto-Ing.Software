package com.mycompany.gestionebiblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.mycompany.gestionebiblioteca.repository.FileBackedBookRepository;
import com.mycompany.gestionebiblioteca.repository.FileBackedUserRepository;
import com.mycompany.gestionebiblioteca.repository.FileBackedLoanRepository;
import com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository;
import com.mycompany.gestionebiblioteca.repository.InMemoryLoanRepository;
import com.mycompany.gestionebiblioteca.repository.InMemoryUserRepository;
import com.mycompany.gestionebiblioteca.repository.BookRepository;
import com.mycompany.gestionebiblioteca.repository.LoanRepository;
import com.mycompany.gestionebiblioteca.repository.UserRepository;
import com.mycompany.gestionebiblioteca.persistence.FileManager;
import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
/**
 *
 * @author david
 */

class BookServiceTest {

    @Test
    void addBookWithValidDataShouldStoreInRepositoryAndFile(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );

        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);

        List<String> autori = new ArrayList<>();
        autori.add("Author");
        Book result = service.addBook("9781234567890", "Title", autori, 2020, 5);

        assertEquals("9781234567890", result.getIsbn());
        assertEquals(1, bookRepository.count());
        Optional<Book> stored = bookRepository.findById("9781234567890");
        assertTrue(stored.isPresent());
        assertEquals(5, stored.get().getTotalCopies());
        assertEquals(5, stored.get().getAvailableCopies());

        List<String> lines = Files.readAllLines(booksFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("ISBN|Titolo|Autore|Anno|CopieTotali|CopieDisponibili", lines.get(0));
        assertEquals("9781234567890|Title|Author|2020|5|5", lines.get(1));
    }

    @Test
    void addBookWithInvalidIsbnShouldThrowValidationException(@TempDir Path tempDir) {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);

        List<String> autori = new ArrayList<>();
        autori.add("Author");

        assertThrows(ValidationException.class,
                () -> service.addBook("123", "Title", autori, 2020, 5));
    }

    @Test
    void addBookWithDuplicateIsbnShouldThrowValidationException(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );

        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);
        List<String> autori = new ArrayList<>();
        autori.add("Author");
        service.addBook("9781234567890", "Title", autori, 2020, 5);

        assertThrows(ValidationException.class,
                () -> service.addBook("9781234567890", "Other", autori, 2021, 3));
    }

    @Test
    void addBookWithInvalidTotalCopiesShouldThrowValidationException(@TempDir Path tempDir) {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);
        List<String> autori = new ArrayList<>();
        autori.add("Author");
        assertThrows(ValidationException.class,
                () -> service.addBook("9781234567890", "Title", autori, 2020, 0));
    }

    @Test
    void updateBookShouldModifyExistingBookAndPersist(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );

        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);

        List<String> autori = new ArrayList<>();
        autori.add("Author");

        service.addBook("9781234567890", "Old Title", autori, 2000, 5);

        Book updated = service.updateBook("9781234567890", "9781234567890", "New Title", autori, 2021, 10);

        assertEquals("New Title", updated.getTitle());
        assertEquals("New Author", updated.getAuthors());
        assertEquals(2021, updated.getYear());
        assertEquals(10, updated.getTotalCopies());

        List<String> lines = Files.readAllLines(booksFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("9781234567890|New Title|New Author|2021|10|10", lines.get(1));
    }

    @Test
    void updateNonExistingBookShouldThrowNotFoundException(@TempDir Path tempDir) {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );

        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);

        List<String> autori = new ArrayList<>();
        autori.add("Author");
        assertThrows(NotFoundException.class,
                () -> service.updateBook("9781234567890", "9781234567890", "Title", autori, 2020, 5));
    }

    @Test
    void deleteBookShouldRemoveFromRepositoryAndFile(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );

        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);

        List<String> autori = new ArrayList<>();
        autori.add("Author");

        service.addBook("9781234567890", "Title", autori, 2020, 5);
        service.addBook("9781234567891", "Other", autori, 2021, 3);

        service.deleteBook("9781234567890");

        assertEquals(1, bookRepository.count());
        assertFalse(bookRepository.existsById("9781234567890"));

        List<String> lines = Files.readAllLines(booksFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("9781234567891|Other|Other|2021|3|3", lines.get(1));
    }

    @Test
    void deleteNonExistingBookShouldThrowNotFoundException(@TempDir Path tempDir) {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );

        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);

        assertThrows(NotFoundException.class,
                () -> service.deleteBook("9781234567890"));
    }

    @Test
    void searchAndListingMethodsShouldDelegateToRepository(@TempDir Path tempDir) throws IOException {
        Path booksFile = tempDir.resolve("books.txt");
        FileManager fileManager = new FileManager();
        BookRepository bookRepository = new FileBackedBookRepository(
                new com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository(),
                fileManager,
                booksFile
        );

        Path usersFile = tempDir.resolve("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        // dependency injection di userRepository in UserService
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);
        List<String> autori = new ArrayList<>();
        autori.add("Author");

        service.addBook("9781234567890", "Java Basics", autori, 2020, 5);
        service.addBook("9781234567891", "Advanced Java", autori, 2021, 3);

        Optional<Book> byIsbn = service.getBookByIsbn("9781234567890");
        assertTrue(byIsbn.isPresent());

        List<Book> byTitle = service.searchByTitle("java");
        assertEquals(2, byTitle.size());

        List<Book> allOrdered = service.getAllBooksOrderedByTitle();
        assertEquals(2, allOrdered.size());
        assertEquals("Advanced Java", allOrdered.get(0).getTitle());
        assertEquals("Java Basics", allOrdered.get(1).getTitle());
    }
}
