/*
 * Test di unità per il repository dei prestiti.
 *
 * Questa classe verifica il corretto comportamento delle operazioni
 * CRUD e dei metodi di ricerca specifici sui prestiti ({@link Loan}),
 * utilizzando l'implementazione {@link InMemoryLoanRepository}.
 *
 * i test servono a verificare
 * 1) che ogni prestito venga identificato correttamente all'interno del repository
 * 2) che le operazioni principali di salvataggio, ricerca e cancellazione funzionino correttamente
 * 3) che le ricerche dei prestiti per utente, per libro e dei prestiti funzionino correttamente
 * 4) che i prestiti vengano ordinati in base alla data di scadenza
 *
 */
package com.mycompany.gestionebiblioteca.repository;

import com.mycompany.gestionebiblioteca.model.Loan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author valerialupo
 */
class FileBackedLoanRepositoryTest {
    
    private InMemoryLoanRepository repository;

    // prima di ogni test viene inizializzato un repository vuoto
    // garantendo indipendenza tra i casi di test
    @BeforeEach
    void setUp() {
        repository = new InMemoryLoanRepository();
    }

    
    /*
    * metodo di utilità per creare rapidamente un oggetto Loan
    * con i parametri desiderati, evitando duplicazioni di codice nei test
    */
    private Loan createLoan(String matricola, String isbn,
                            LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        return new Loan(matricola, isbn, loanDate, dueDate, returnDate);
    }

    
    /*
    * costruisce l'identificativo logico di un prestito.
    *
    * l'ID è composto da:
    * matricola dell'utente, 
    * isbn del libro,
    * data di inizio del prestito.
     *
    * Questo garantisce l'univocità del prestito nel repository
    */
    private String idFor(Loan loan) {
        return loan.getUserMatricola() + ":" + loan.getBookIsbn() + ":" + loan.getLoanDate();
    }

    
    
    
    
    /*
    * verifica che un prestito salvato nel repository
    * possa essere correttamente recuperato tramite il suo id
    */
    @Test
    void saveAndFindByIdShouldStoreAndReturnLoan() {
        Loan loan = createLoan("1234567890", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15), null);

        repository.save(loan);
        Optional<Loan> result = repository.findById(idFor(loan));

        assertTrue(result.isPresent());
        assertEquals(loan, result.get());
    }

    
    
    /*
    * verifica che il repository rifiuti il salvataggio
    * di un prestito nullo, garantendo la validità dei dati
    */
    @Test
    void saveNullLoanShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    
    /*
    * verifica la coerenza dei metodi existsById e count
    * rispetto ai prestiti presenti nel repository.
    */
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

    
    
    /*
    * verifica che deleteById rimuova correttamente
    * un prestito precedentemente salvato.
    */
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

    
    /*
    * verifica che deleteAll svuoti completamente il repository
    */
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

    
    /*
    * Verifica che findByUserMatricola restituisca
     * tutti e soli i prestiti associati a un determinato utente
    */
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

    
    
    /*
    *  verifica che findByBookIsbn restituisca
    * tutti i prestiti associati a un determinato libro
    */
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

    
    
    
    /*
    * Verifica che findActiveLoansOrderByDueDate:
    * restituisca solo i prestiti attivi +
     * ordini i risultati per data di scadenza crescente
 */
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

    
    /*
    * Verifica che findActiveLoansByUser restituisca
    * solo i prestiti attivi di un utente, ordinati per scadenza.
    */
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

    
    
    /*
    * Verifica che una matricola vuota o contenente solo spazi
    * produca una lista vuota di risultati.
    */
    @Test
    void findActiveLoansByUserWithBlankMatricolaShouldReturnEmptyList() {
        repository.save(createLoan("user1", "9781234567890",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 10), null));

        List<Loan> result = repository.findActiveLoansByUser("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
}
