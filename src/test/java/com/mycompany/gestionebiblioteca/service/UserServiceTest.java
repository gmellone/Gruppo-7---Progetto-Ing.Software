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
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UserServiceTest {

    @Test
    void addUserWithValidDataShouldStoreInRepositoryAndFile(@TempDir Path tempDir) throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService userService = new UserService(userRepository, loanService);

        BookService service = new BookService(bookRepository, loanService);

        User result = userService.addUser("1234567890", "Mario", "Rossi", "mario@studenti.unisa.it");

        assertEquals("1234567890", result.getMatricola());
        assertEquals(1, userRepository.count());
        Optional<User> stored = userRepository.findById("1234567890");
        assertTrue(stored.isPresent());
        assertEquals("mario@studenti.unisa.it", stored.get().getEmail());

        List<String> lines = Files.readAllLines(usersFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("Matricola|Nome|Cognome|Email", lines.get(0));
        assertEquals("1234567890|Mario|Rossi|mario@studenti.unisa.it", lines.get(1));
    }

    @Test
    void addUserWithInvalidMatricolaShouldThrowValidationException(@TempDir Path tempDir) {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        assertThrows(ValidationException.class,
                () -> service.addUser("123", "Mario", "Rossi", "mario@studenti.unisa.it"));
    }

    @Test
    void addUserWithInvalidEmailShouldThrowValidationException(@TempDir Path tempDir) {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        assertThrows(ValidationException.class,
                () -> service.addUser("1234567890", "Mario", "Mele", "invalid-email"));
    }

    @Test
    void addUserWithDuplicateMatricolaShouldThrowValidationException(@TempDir Path tempDir) throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        service.addUser("1234567890", "Mario", "Rossi", "mario@studenti.unisa.it");

        assertThrows(ValidationException.class,
                () -> service.addUser("1234567890", "Luigi", "Bianchi", "luigi@studenti.unisa.it"));
    }

    @Test
    void addUserWithDuplicateEmailShouldThrowValidationException(@TempDir Path tempDir) throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        service.addUser("1234567890", "Mario", "Rossi", "mario@studenti.unisa.it");

        assertThrows(ValidationException.class,
                () -> service.addUser("1234567891", "Luigi", "Bianchi", "mario@studenti.unisa.it"));
    }

    @Test
    void updateUserShouldModifyExistingUserAndPersist(@TempDir Path tempDir) throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        service.addUser("1234567890", "Mario", "Rossi", "mario@studenti.unisa.it");

        User updated = service.updateUser("1234567890", "1234567890", "Mario", "Verdi", "mario.verdi@studenti.unisa.it");

        assertEquals("Verdi", updated.getLastName());
        assertEquals("mario.verdi@studenti.unisa.it", updated.getEmail());

        List<String> lines = Files.readAllLines(usersFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("1234567890|Mario|Verdi|mario.verdi@studenti.unisa.it", lines.get(1));
    }

    @Test
    void updateNonExistingUserShouldThrowNotFoundException(@TempDir Path tempDir) {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        assertThrows(NotFoundException.class,
                () -> service.updateUser("1234567890", "1234567890", "Mario", "Rossi", "mario@studenti.unisa.it"));
    }

    @Test
    void deleteUserShouldRemoveFromRepositoryAndFile(@TempDir Path tempDir) throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        service.addUser("1234567890", "Mario", "Rossi", "mario@studenti.unisa.it");
        service.addUser("1234567891", "Luigi", "Bianchi", "luigi@studenti.unisa.it");

        service.deleteUser("1234567890");

        assertEquals(1, userRepository.count());
        assertFalse(userRepository.existsById("1234567890"));

        List<String> lines = Files.readAllLines(usersFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("1234567891|Luigi|Bianchi|luigi@studenti.unisa.it", lines.get(1));
    }

    @Test
    void deleteNonExistingUserShouldThrowNotFoundException(@TempDir Path tempDir) {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        assertThrows(NotFoundException.class,
                () -> service.deleteUser("1234567890"));
    }

    @Test
    void searchAndListingMethodsShouldDelegateToRepository(@TempDir Path tempDir) throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        FileManager fileManager = new FileManager();
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(),
                fileManager,
                usersFile
        );
        Path booksFile = tempDir.resolve("books.txt");

        BookRepository bookRepository = new FileBackedBookRepository(new InMemoryBookRepository(),
                fileManager,
                booksFile
        );
        Path loansFile = tempDir.resolve("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(new InMemoryLoanRepository(),
                fileManager,
                loansFile);
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);
        UserService service = new UserService(userRepository, loanService);

        service.addUser("1234567890", "Mario", "Rossi", "mario@studenti.unisa.it");
        service.addUser("1234567891", "Luigi", "Bianchi", "luigi@studenti.unisa.it");

        Optional<User> byMatricola = service.getUserByMatricola("1234567890");
        assertTrue(byMatricola.isPresent());

        List<User> byLastName = service.searchByLastName("rossi");
        assertEquals(1, byLastName.size());

        List<User> allOrdered = service.getAllUsersOrderedByLastNameAndFirstName();
        assertEquals(2, allOrdered.size());
        assertEquals("Bianchi", allOrdered.get(0).getLastName());
        assertEquals("Rossi", allOrdered.get(1).getLastName());
    }
}
