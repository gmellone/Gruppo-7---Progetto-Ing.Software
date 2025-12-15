/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.service;

import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.model.Loan;
import com.mycompany.gestionebiblioteca.model.User;
import com.mycompany.gestionebiblioteca.repository.BookRepository;
import com.mycompany.gestionebiblioteca.repository.LoanRepository;
import com.mycompany.gestionebiblioteca.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 * @author david
 */
/**
 * @brief Service per la gestione del ciclo di vita dei prestiti.
 *
 * Questa classe coordina le interazioni tra Utenti, Libri e Prestiti. Gestisce
 * la logica di assegnazione e restituzione, garantendo il rispetto delle regole
 * di business (es. limite massimo di prestiti per utente, verifica
 * disponibilità copie) e mantenendo aggiornato lo stato dell'inventario.
 */
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * @brief Costruttore con iniezione delle dipendenze.
     *
     * @param loanRepository Repository per la gestione dei prestiti.
     * @param bookRepository Repository per l'aggiornamento dello stato dei
     * libri.
     * @param userRepository Repository per la verifica degli utenti.
     * @throws IllegalArgumentException Se uno dei repository passati è null.
     */
    public LoanService(LoanRepository loanRepository,
            BookRepository bookRepository,
            UserRepository userRepository) {
        if (loanRepository == null) {
            throw new IllegalArgumentException("loanRepository non deve essere null");
        }
        if (bookRepository == null) {
            throw new IllegalArgumentException("bookRepository non deve essere null");
        }
        if (userRepository == null) {
            throw new IllegalArgumentException("userRepository non deve essere null");
        }
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * @brief Ricerca Utenti per la TableView della GUI Prestiti. Logica
     * "Google-style": - Se la stringa è vuota -> Restituisce tutti (o lista
     * vuota a seconda delle preferenze). - Se è una Matricola (10 cifre) ->
     * Cerca per matricola esatta. - Altrimenti -> Cerca per Cognome (parziale,
     * case-insensitive).
     */
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(); // Mostra tutti gli utenti all'inizio
        }

        String cleanQuery = keyword.trim();

        // Rileva se è una matricola (esattamente 10 cifre)
        if (cleanQuery.matches("\\d{10}")) {
            return userRepository.findByMatricola(cleanQuery)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
        }

        // Altrimenti cerca per cognome
        return userRepository.findByLastNameContaining(cleanQuery);
    }

    /**
     * @brief Ricerca Libri per la TableView della GUI Prestiti. Logica
     * "Google-style": - Cerca contemporaneamente in: ISBN (contiene) OR Titolo
     * (contiene) OR Autori (contiene). - Supporta la nuova lista di autori
     * @param keyword la Stringa da cercare.
     */
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll(); // Mostra tutti i libri all'inizio
        }

        String lowerQuery = keyword.trim().toLowerCase();

        // Nota: Eseguiamo il filtro in memoria stream() per garantire la logica "OR" complessa
        // senza dover modificare l'interfaccia del Repository con query custom.
        return bookRepository.findAll().stream()
                .filter(book -> {
                    // 1. Match ISBN (parziale o esatto)
                    boolean matchIsbn = book.getIsbn().toLowerCase().contains(lowerQuery);

                    // 2. Match Titolo
                    boolean matchTitle = book.getTitle().toLowerCase().contains(lowerQuery);

                    // 3. Match Autori (Itera sulla lista List<String> authors)
                    boolean matchAuthor = false;
                    if (book.getAuthors() != null) {
                        matchAuthor = book.getAuthors().stream()
                                .anyMatch(authorName -> authorName.toLowerCase().contains(lowerQuery));
                    }

                    // Logica OR: basta che uno dei campi corrisponda
                    return matchIsbn || matchTitle || matchAuthor;
                })
                .collect(Collectors.toList());
    }

    /**
     * @brief Registra un nuovo prestito nel sistema.
     *
     * Esegue una serie di controlli bloccanti prima di procedere: 1.
     * Validazione formale degli input. 2. Esistenza di Utente e Libro. 3.
     * Disponibilità effettiva di copie del libro. 4. Limite massimo di prestiti
     * attivi per utente (Max 3). 5. Verifica che l'utente non stia prendendo in
     * prestito una seconda copia dello stesso libro.
     *
     * Se tutti i controlli passano, crea il prestito e decrementa di 1 le copie
     * disponibili del libro.
     *
     * @param matricola Identificativo dell'utente che richiede il prestito.
     * @param isbn Identificativo del libro richiesto.
     * @param loanDate Data di inizio prestito.
     * @param dueDate Data di scadenza prevista.
     * @return L'oggetto Loan salvato nel database.
     * @throws NotFoundException Se l'utente o il libro non esistono.
     * @throws ValidationException Se non ci sono copie, se il limite prestiti è
     * raggiunto o se i dati non sono validi.
     */
    public Loan registerLoan(String matricola, String isbn,
            LocalDate loanDate, LocalDate dueDate) {

        validateMatricola(matricola);
        validateIsbn(isbn);
        validateLoanDates(loanDate, dueDate);

        if (!userRepository.existsById(matricola)) {
            throw new NotFoundException("Utente non trovato con matricola " + matricola);
        }

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new NotFoundException("Libro non trovato con ISBN " + isbn));

        if (book.getAvailableCopies() <= 0) {
            throw new ValidationException("Non ci sono copie disponibili del libro con ISBN " + isbn);
        }

        List<Loan> activeLoansForUser = loanRepository.findActiveLoansByUser(matricola);

        if (activeLoansForUser.size() >= 3) {
            throw new ValidationException("L'utente con matricola " + matricola + " ha già 3 prestiti attivi (limite raggiunto)");
        }

        // Controllo se l'utente ha già UNA copia di QUESTO libro in prestito
        activeLoansForUser.stream()
                .filter(activeLoan -> (activeLoan.getBookIsbn().equals(isbn)))
                .forEachOrdered(_item -> {
                    throw new ValidationException("L'utente ha già in prestito una copia del libro: " + book.getTitle());
                });

        Loan loan = new Loan(matricola, isbn, loanDate, dueDate, null);
        Loan saved = loanRepository.save(loan);

        // Side effect: aggiorno l'inventario
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return saved;
    }

    /**
     * @brief Registra la restituzione di un libro.
     *
     * Chiude il prestito impostando la data di restituzione effettiva e
     * incrementa di 1 il numero di copie disponibili del libro associato.
     *
     * @param loan L'oggetto prestito da chiudere.
     * @param returnDate La data effettiva di restituzione.
     * @return Il prestito aggiornato con la data di restituzione impostata.
     * @throws ValidationException Se il prestito è già chiuso o se la data non
     * è valida.
     * @throws IllegalArgumentException Se il prestito passato è null.
     */
    public Loan registerReturn(Loan loan, LocalDate returnDate) {
        if (loan == null) {
            throw new IllegalArgumentException("il prestito non deve essere null");
        }
        if (returnDate == null) {
            throw new ValidationException("la data di restituzione non deve essere null");
        }
        if (!loan.isActive()) {
            throw new ValidationException("il prestito è stato già restituito");
        }
        if (returnDate.isBefore(loan.getLoanDate())) {
            throw new ValidationException("la data di ritorno non può essere inferiore alla data di inizio prestito");
        }

        loan.setReturnDate(returnDate);
        Loan updated = loanRepository.save(loan);

        Book book = bookRepository.findById(loan.getBookIsbn())
                .orElseThrow(() -> new NotFoundException("Libro non trovato con ISBN " + loan.getBookIsbn()));

        // ripristino la disponibilità
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return updated;
    }

    /**
     * @brief Recupera tutti i prestiti attivi nel sistema ordinati per
     * scadenza.
     * @return Lista di prestiti attivi ordinata per DueDate.
     */
    public List<Loan> getActiveLoansOrderedByDueDate() {
        return loanRepository.findActiveLoansOrderByDueDate();
    }

    /**
     * @brief Recupera i prestiti attivi di uno specifico utente.
     * @param matricola La matricola dell'utente.
     * @return Lista dei prestiti non ancora restituiti dall'utente.
     */
    public List<Loan> getActiveLoansByUser(String matricola) {
        return loanRepository.findActiveLoansByUser(matricola);
    }

    /**
     * @brief Conta il numero di prestiti attivi per un utente.
     * @param matricola La matricola dell'utente.
     * @return Il numero di libri attualmente in possesso dell'utente.
     */
    public long countActiveLoansForUser(String matricola) {
        return loanRepository.findActiveLoansByUser(matricola).size();
    }

    /**
     * @brief Valida il formato della matricola.
     * @details Controlla che la stringa non sia vuota e corrisponda esattamente
     * a 10 cifre.
     * @param matricola Stringa da validare.
     * @throws ValidationException Se il formato non è corretto.
     */
    private void validateMatricola(String matricola) {
        if (matricola == null || matricola.trim().isEmpty()) {
            throw new ValidationException("La matricola non deve essere vuota");
        }
        if (!matricola.matches("^\\d{10}$")) {
            throw new ValidationException("La matricola deve essere di dieci cifre");
        }
    }

    /**
     * @brief Valida il formato dell'ISBN.
     * @param isbn Stringa da validare (13 cifre).
     * @throws ValidationException Se il formato non è corretto.
     */
    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN non deve essere vuoto");
        }
        if (!isbn.matches("^\\d{13}$")) {
            throw new ValidationException("ISBN deve essere esattamente di 13 cifre");
        }
    }

    /**
     * @brief Valida la coerenza cronologica delle date del prestito.
     * @param loanDate Data di inizio.
     * @param dueDate Data di fine prevista.
     * @throws ValidationException Se le date sono nulle o se la scadenza è
     * antecedente all'inizio.
     */
    private void validateLoanDates(LocalDate loanDate, LocalDate dueDate) {
        if (loanDate == null) {
            throw new ValidationException("La data di inizio del prestito non deve essere nulla");
        }
        if (dueDate == null) {
            throw new ValidationException("La data di restituzione non deve essere nulla");
        }
        if (dueDate.isBefore(loanDate)) {
            throw new ValidationException("La data di restituzione non può essere inserita prima della data di inizio del prestito");
        }
        if (loanDate.isAfter(LocalDate.now())){
            throw new ValidationException("Il prestito non può essere registrato in data successiva alla data Odierna");
        }
    }

    /**
     * @brief Conta quante copie di un libro specifico sono attualmente in
     * prestito. * Metodo utilizzato principalmente dal BookService per
     * calcolare la disponibilità durante le operazioni di aggiornamento
     * catalogo.
     * * @param isbn L'ISBN del libro da verificare.
     * @return Il numero di prestiti attivi per quell'ISBN.
     */
    public int countActiveLoansByIsbn(String isbn) {
        if (isbn == null) {
            return 0;
        }
        // Recupera tutti i prestiti di quel libro e conta quelli senza data di restituzione
        return (int) loanRepository.findByBookIsbn(isbn).stream()
                .filter(loan -> loan.getReturnDate() == null) // Solo quelli attivi
                .count();
    }
}
