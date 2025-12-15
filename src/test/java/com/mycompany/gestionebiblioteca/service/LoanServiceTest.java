
package com.mycompany.gestionebiblioteca.service;
import com.mycompany.gestionebiblioteca.repository.InMemoryBookRepository;
import com.mycompany.gestionebiblioteca.repository.InMemoryLoanRepository;
import com.mycompany.gestionebiblioteca.repository.InMemoryUserRepository;
import com.mycompany.gestionebiblioteca.repository.BookRepository;
import com.mycompany.gestionebiblioteca.repository.LoanRepository;
import com.mycompany.gestionebiblioteca.repository.UserRepository;
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.model.Loan;
import com.mycompany.gestionebiblioteca.model.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author david
 */
class LoanServiceTest {

    private LoanRepository loanRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanRepository = new InMemoryLoanRepository();
        bookRepository = new InMemoryBookRepository();
        userRepository = new InMemoryUserRepository();
        loanService = new LoanService(loanRepository, bookRepository, userRepository);
    }

    private void createUser(String matricola) {
        userRepository.save(new User(matricola, "Mario", "Rossi", "mario@example.com"));
    }

    private void createBook(String isbn, int totalCopies, int availableCopies) {
        bookRepository.save(new Book(isbn, "Title", java.util.Arrays.asList("Author"), 2020, totalCopies, availableCopies));
    }

    @Test
    void registerLoanShouldCreateLoanAndDecreaseAvailableCopies() {
        createUser("1234567890");
        createBook("9781234567890", 5, 5);

        LocalDate loanDate = LocalDate.of(2024, 3, 1);
        LocalDate dueDate = LocalDate.of(2024, 3, 15);

        Loan loan = loanService.registerLoan("1234567890", "9781234567890", loanDate, dueDate);

        assertEquals("1234567890", loan.getUserMatricola());
        assertEquals("9781234567890", loan.getBookIsbn());
        assertEquals(1, loanRepository.count());

        Book storedBook = bookRepository.findById("9781234567890").orElseThrow(
        () -> new NotFoundException("Book not found"));
        assertEquals(4, storedBook.getAvailableCopies());
    }

    @Test
    void registerLoanShouldFailIfUserDoesNotExist() {
        createBook("9781234567890", 5, 5);

        assertThrows(NotFoundException.class,
                () -> loanService.registerLoan("1234567890", "9781234567890",
                        LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15)));
    }

    @Test
    void registerLoanShouldFailIfBookDoesNotExist() {
        createUser("1234567890");

        assertThrows(NotFoundException.class,
                () -> loanService.registerLoan("1234567890", "9781234567890",
                        LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15)));
    }

    @Test
    void registerLoanShouldFailIfNoAvailableCopies() {
        createUser("1234567890");
        createBook("9781234567890", 5, 0);

        assertThrows(ValidationException.class,
                () -> loanService.registerLoan("1234567890", "9781234567890",
                        LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15)));
    }

    @Test
    void registerLoanShouldFailIfUserAlreadyHasThreeActiveLoans() {
        createUser("1234567890");
        createBook("9781234567890", 10, 10);
        createBook("9781234567891", 10, 10);
        createBook("9781234567892", 10, 10);
        createBook("9781234567893", 10, 10);

        loanService.registerLoan("1234567890", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15));
        loanService.registerLoan("1234567890", "9781234567891",
                LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 16));
        loanService.registerLoan("1234567890", "9781234567892",
                LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 17));

        assertThrows(ValidationException.class,
                () -> loanService.registerLoan("1234567890", "9781234567893",
                        LocalDate.of(2024, 3, 4), LocalDate.of(2024, 3, 18)));
    }

    @Test
    void registerReturnShouldSetReturnDateAndIncreaseAvailableCopies() {
        createUser("1234567890");
        createBook("9781234567890", 5, 5);

        LocalDate loanDate = LocalDate.of(2024, 3, 1);
        LocalDate dueDate = LocalDate.of(2024, 3, 15);
        Loan loan = loanService.registerLoan("1234567890", "9781234567890", loanDate, dueDate);

        LocalDate returnDate = LocalDate.of(2024, 3, 10);
        Loan returned = loanService.registerReturn(loan, returnDate);

        assertEquals(returnDate, returned.getReturnDate());

        Book storedBook = bookRepository.findById("9781234567890").orElseThrow(
        () -> new NotFoundException("Book not found"));
        assertEquals(5, storedBook.getAvailableCopies());
    }

    @Test
    void registerReturnShouldFailIfLoanAlreadyReturned() {
        createUser("1234567890");
        createBook("9781234567890", 5, 5);

        Loan loan = loanService.registerLoan("1234567890", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15));
        loanService.registerReturn(loan, LocalDate.of(2024, 3, 10));

        assertThrows(ValidationException.class,
                () -> loanService.registerReturn(loan, LocalDate.of(2024, 3, 11)));
    }

    @Test
    void getActiveLoansOrderedByDueDateShouldReturnOnlyActiveSorted() {
        createUser("0612709530");
        // Creiamo 3 libri
        createBook("9781234567890", 10, 10);
        createBook("9781234567891", 10, 10);
        createBook("9781234567892", 10, 10);

         // Loan 1: Scade il 10 Marzo (Attivo)
        Loan loan1 = loanService.registerLoan("0612709530", "9781234567890",
            LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 10));

        // Loan 2: Scade il 5 Marzo (Attivo) -> Dovrebbe essere PRIMA di loan1
        Loan loan2 = loanService.registerLoan("0612709530", "9781234567891",
            LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 5));

        // Loan 3: Scade il 20 Marzo (Ma viene RESTITUITO) -> Non deve esserci
        Loan loan3 = loanService.registerLoan("0612709530", "9781234567892",
            LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 20));

        // Restituiamo il loan3
        loanService.registerReturn(loan3, LocalDate.of(2024, 3, 4));

        // Eseguiamo il metodo
        List<Loan> active = loanService.getActiveLoansOrderedByDueDate();

        // Verifiche
        assertEquals(2, active.size(), "Dovrebbero esserci 2 prestiti attivi");


        assertEquals(loan2.getBookIsbn(), active.get(0).getBookIsbn());
        assertEquals(loan1.getBookIsbn(), active.get(1).getBookIsbn());
}
}