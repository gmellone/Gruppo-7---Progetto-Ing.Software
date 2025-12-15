/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.gestionebiblioteca.model.Loan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryLoanRepositoryTest {

    private InMemoryLoanRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLoanRepository();
    }

    private Loan createLoan(String matricola, String isbn,
                            LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        return new Loan(matricola, isbn, loanDate, dueDate, returnDate);
    }

    private String idFor(Loan loan) {
        return loan.getUserMatricola() + ":" + loan.getBookIsbn() + ":" + loan.getLoanDate();
    }

    @Test
    void saveAndFindByIdShouldStoreAndReturnLoan() {
        Loan loan = createLoan("1234567890", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15), null);

        repository.save(loan);
        Optional<Loan> result = repository.findById(idFor(loan));

        assertTrue(result.isPresent());
        assertEquals(loan, result.get());
    }

    @Test
    void saveNullLoanShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void existsByIdAndCountShouldReflectStoredLoans() {
        assertEquals(0, repository.count());
        assertFalse(repository.existsById("some:id"));

        Loan loan1 = createLoan("1234567890", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15), null);
        Loan loan2 = createLoan("1234567891", "9781234567891",
                LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 16), null);
        repository.save(loan1);
        repository.save(loan2);

        assertEquals(2, repository.count());
        assertTrue(repository.existsById(idFor(loan1)));
        assertTrue(repository.existsById(idFor(loan2)));
        assertFalse(repository.existsById("unknown:id"));
    }

    @Test
    void deleteByIdShouldRemoveLoan() {
        Loan loan = createLoan("1234567890", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15), null);
        repository.save(loan);
        String id = idFor(loan);
        assertTrue(repository.existsById(id));

        repository.deleteById(id);

        assertFalse(repository.existsById(id));
        assertTrue(!repository.findById(id).isPresent());
    }

    @Test
    void deleteAllShouldClearRepository() {
        repository.save(createLoan("1234567890", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15), null));
        repository.save(createLoan("1234567891", "9781234567891",
                LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 16), null));
        assertEquals(2, repository.count());

        repository.deleteAll();

        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findByUserMatricolaShouldReturnLoansForUser() {
        Loan loan1 = createLoan("user1", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15), null);
        Loan loan2 = createLoan("user1", "9781234567891",
                LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 16), null);
        Loan loan3 = createLoan("user2", "9781234567892",
                LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 17), null);

        repository.save(loan1);
        repository.save(loan2);
        repository.save(loan3);

        List<Loan> result = repository.findByUserMatricola("user1");

        assertEquals(2, result.size());
        assertTrue(result.contains(loan1));
        assertTrue(result.contains(loan2));
    }

    @Test
    void findByBookIsbnShouldReturnLoansForBook() {
        Loan loan1 = createLoan("user1", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15), null);
        Loan loan2 = createLoan("user2", "9781234567890",
                LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 16), null);
        Loan loan3 = createLoan("user3", "9781234567891",
                LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 17), null);

        repository.save(loan1);
        repository.save(loan2);
        repository.save(loan3);

        List<Loan> result = repository.findByBookIsbn("9781234567890");

        assertEquals(2, result.size());
        assertTrue(result.contains(loan1));
        assertTrue(result.contains(loan2));
    }

    @Test
    void findActiveLoansOrderByDueDateShouldReturnOnlyActiveSorted() {
        Loan active1 = createLoan("user1", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 10), null);
        Loan active2 = createLoan("user2", "9781234567891",
                LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 5), null);
        Loan returned = createLoan("user3", "9781234567892",
                LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 15));

        repository.save(active1);
        repository.save(active2);
        repository.save(returned);

        List<Loan> result = repository.findActiveLoansOrderByDueDate();

        assertEquals(2, result.size());
        assertEquals(active2, result.get(0));
        assertEquals(active1, result.get(1));
    }

    @Test
    void findActiveLoansByUserShouldReturnOnlyActiveLoansForUserSortedByDueDate() {
        Loan active1 = createLoan("user1", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 10), null);
        Loan returned = createLoan("user1", "9781234567891",
                LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 5), LocalDate.of(2024, 3, 4));
        Loan active2 = createLoan("user1", "9781234567892",
                LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 8), null);
        Loan otherUser = createLoan("user2", "9781234567893",
                LocalDate.of(2024, 3, 4), LocalDate.of(2024, 3, 12), null);

        repository.save(active1);
        repository.save(returned);
        repository.save(active2);
        repository.save(otherUser);

        List<Loan> result = repository.findActiveLoansByUser("user1");

        assertEquals(2, result.size());
        assertEquals(active2, result.get(0));
        assertEquals(active1, result.get(1));
    }

    @Test
    void findActiveLoansByUserWithBlankMatricolaShouldReturnEmptyList() {
        repository.save(createLoan("user1", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 10), null));

        List<Loan> result = repository.findActiveLoansByUser("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
