/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @file FileManager.java
 * @brief Gestisce la persistenza dei dati dell'applicazione tramite file di
 * testo.
 *
 * La classe FileManager fornisce operazioni di caricamento e salvataggio per
 * Libri, Utenti e Prestiti, usando file di testo delimitati da un separatore. È
 * lo strato di persistenza "basso", usato dai repository dell'applicazione.
 */
package com.mycompany.gestionebiblioteca.persistence;

import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.model.User;
import com.mycompany.gestionebiblioteca.model.Loan;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author valerialupo
 */
/**
 * @class FileManager
 * @brief Classe responsabile della gestione dei file di persistenza.
 *
 * Questa classe contiene metodi per leggere e scrivere su file testuali
 * contenenti liste di libri, utenti e prestiti. I file utilizzano un formato
 * semplice basato su righe di testo con campi separati da '|'.
 */
public class FileManager {

    // header e separatore dei file di testo
    /**
     * @brief Header del file dei libri.
     */
    private static final String BOOKS_HEADER = "ISBN|Titolo|Autori|Anno|CopieTotali|CopieDisponibili";

    /**
     * @brief Header del file degli utenti.
     */
    private static final String USERS_HEADER = "Matricola|Nome|Cognome|Email";

    /**
     * @brief Header del file dei prestiti.
     */
    private static final String LOANS_HEADER = "Matricola|ISBN|DataPrestito|DataRestituzionePrevista|DataRestituzioneEffettiva";

    /**
     * @brief Separatore utilizzato nei file di testo.
     */
    private static final String SEPARATOR = "|";

    // operazioni sui libri 
    /**
     * @brief Carica l'elenco dei libri da un file.
     *
     * Il metodo legge tutte le righe del file, salta l'header e converte
     * ciascuna riga in un oggetto Libro.
     *
     * @param file Percorso del file da cui leggere.
     * @return Lista dei libri contenuti nel file.
     * @throws IOException Se si verificano errori di I/O o formati non validi.
     */
    public List<Book> loadBooks(Path file) throws IOException { // legge un file di testo che contiene i libri, trasforma ogni riga valida in un oggetto Book, restituisce una lista di libri
        // qui si verifica che il percorso del file non sia null
        if (file == null) {
            throw new IllegalArgumentException("il file non deve essere null"); // si usa IllegalArgumentException  perche è un errore di uso del metodo, non di i/o 
        }

        // gestione del caso in cui il file non esiste
        if (!Files.exists(file)) {
            //crea le cartelle se servono
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // crea il file solo con l'header
            Files.write(file, Arrays.asList(BOOKS_HEADER), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            // restituisce una lista vuota
            return new ArrayList<>(); // se il file non esiste non è errore, semplicemente non ci sono libri
        }

        // lettura completa del file
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8); // tutte le righe vengono caricate in memoria
        List<Book> result = new ArrayList<>(); // result: lista finale di Book

        // ciclo sulle righe del file
        for (int i = 0; i < lines.size(); i++) {
            // salto le righe nulle
            String rawLine = lines.get(i);
            if (rawLine == null) {
                continue;
            }
            // eliimina spazi + salta righe vuote
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            // se la prima riga corrisponde all'header (non è un libro) viene ignorata
            if (i == 0 && line.equals(BOOKS_HEADER)) {
                continue;
            }

            // divide la riga usando |
            String[] parts = line.split("\\|", -1);

            // il formato del file è: ISBN | titolo | autore | anno | copie totali | copie disponibili -> se il numero dei campi è sbagliato -> eccezione
            if (parts.length != 6) {
                throw new IOException("Riga del file dei libri non valida " + rawLine);
            }

            // lettura dei campi
            String isbn = parts[0]; // isbn è obbligatorio 
            String title = emptyToNull(parts[1]); // title e author possono essere null-> nel caso vengono trasformati in null
            String authorsStr = emptyToNull(parts[2]);
            
            // parsing degli autori separati da punto e virgola
            List<String> authors = new ArrayList<>();
            if (authorsStr != null && !authorsStr.isEmpty()) {
                String[] authorArray = authorsStr.split(";");
                for (String author : authorArray) {
                    String trimmed = author.trim();
                    if (!trimmed.isEmpty()) {
                        authors.add(trimmed);
                    }
                }
            }
            
            int year;
            int totalCopies;
            int availableCopies;

            // converte stringhe in interi
            try {
                year = Integer.parseInt(parts[3]);
                totalCopies = Integer.parseInt(parts[4]);
                availableCopies = Integer.parseInt(parts[5]);
            } catch (NumberFormatException e) {
                throw new IOException("Valore numerico non valido nella riga del file dei libri: " + rawLine, e); // se il file contiene valori non numerici -> errore -> che viene incapsulato in una IOexception
            }

            // creazione dell'oggetto Book
            Book book = new Book(isbn, title, authors, year, totalCopies, availableCopies);
            result.add(book);
        }

        return result;
    }

    /**
     * @brief Scrive su file l'elenco dei libri.
     *
     * Tutti i libri vengono salvati come righe di testo, precedute dall'header
     * BOOKS_HEADER. Ogni campo è separato dal carattere '|'.
     *
     * @param file Percorso del file da scrivere.
     * @param books Collezione dei libri da salvare.
     * @throws IOException Se si verificano errori di I/O.
     */
    public void saveBooks(Path file, Collection<Book> books) throws IOException { // 
        // condizioni limite: se file o books fossero null, il metodo non potrebbe funzionare
        // l'errore è di uso del metodo quindi si usa IllegalArgumentException
        if (file == null) { // si verifica che il percorso del file esista
            throw new IllegalArgumentException("il file non deve essere null");
        }
        if (books == null) { // si verifica che la collezione dei libri non sia nulla
            throw new IllegalArgumentException("il file non deve essere null");
        }

        List<String> lines = new ArrayList<>(); // lines contiene tutte le righe da scrivere nel file
        lines.add(BOOKS_HEADER); // la prima riga è sempre l'header

        // si scorre la collezione dei libri che sono stati forniti in input
        for (Book book : books) {
            if (book == null) { // se un elemento è null viene ignorato
                continue;
            }

            // estrazione: prendo i dati dall'oggetto Book e li metto in variabili locali
            // + porto i dati in una forma standard per evitare che ci siano valori nulli o caratteri che potrebbero compromettere il formato del file
            String isbn = requireNoSeparator(book.getIsbn()); // isbn è obbligatorio (requireNoSeparator verifica che non contenga il carattere separatore)
            String title = requireNoSeparator(nullToEmpty(book.getTitle())); // se title o author sono null -> diventano stringhe vuote
            
            List<String> authors = book.getAuthors();
            String authorsStr = "";
            
            if (authors != null && !authors.isEmpty()) {
                // Validate that no author contains separator or semicolon
                for (String author : authors) {
                    requireNoSeparator(author);
                    if (author != null && author.contains(";")) {
                        throw new IllegalArgumentException("Author name must not contain ';': " + author);
                    }
                }
                authorsStr = String.join(";", authors);
            }
            
            
            
            String year = String.valueOf(book.getYear());  // i valori numerici vengono convertiti in stringhe -> cosi il file contiene solo testo
            String totalCopies = String.valueOf(book.getTotalCopies());
            String availableCopies = String.valueOf(book.getAvailableCopies());

            // costruzione della riga del file 
            String line = String.join(SEPARATOR, isbn,title,authorsStr, year, totalCopies, availableCopies);
            
            lines.add(line);
        }

        // creazione delle directory se necessarie -> se il file si trova in una cartella che ancora non esiste-> viene creata
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // scrittura sul file -> crea il file se non esiste + sovrascrive il contenuto precedente
        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    }

    // operazioni sui prestiti 
    /**
     * @brief carica l'elenco dei prestiti da un file.
     *
     * Ogni riga (escluso l'header) viene convertita in un oggetto Prestito.
     *
     * @param file Percorso del file da cui leggere.
     * @return Lista dei prestiti caricati.
     * @throws IOException Se il formato è errato o il file non è leggibile.
     */
    public List<Loan> loadLoans(Path file) throws IOException {

        // verifico che il percorso del file non sia nullo
        if (file == null) {
            throw new IllegalArgumentException("il percorso del file non deve essere null");
        }

        // se il file ancora non esiste:
        if (!Files.exists(file)) {
            Path parent = file.getParent(); // creao la directory 
            if (parent != null) {
                Files.createDirectories(parent);
            }
            /**
             * asList restituisce una lista di dimensione fissa, non puoi
             * aggiungere/rimuovere elementi, ma puoi modificare quelli
             * esistenti
             *
             */
            // creo il file solo con l'header -> scrivo solo l'intestazione 
            Files.write(file, Arrays.asList(LOANS_HEADER), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            // restituisce una lista vuota -> se il file non esiste -> non ci sono ancora prestiti registrati
            return new ArrayList<>();
        }

        // lettura del contenuto del file
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8); // tutte le righe vengono caricate in memoria
        List<Loan> result = new ArrayList<>(); // result contiene i prestiti validi trovati 

        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            if (rawLine == null) { // salto le righe nulle
                continue;
            }
            String line = rawLine.trim();
            if (line.isEmpty()) { // rimuovo spazi inutili e ignoro righe vuote
                continue;
            }
            if (i == 0 && line.equals(LOANS_HEADER)) { // la prima riga è quella dell'header, non rappresenta un prestito
                continue;
            }

            String[] parts = line.split("\\|", -1); // divide la riga usando |

            // ogni riga deve contenere esattamente 5 campi -> se il formato non è rispettato -> file non valido
            if (parts.length != 5) {
                throw new IOException("Riga non valida nel file dei prestiti: " + rawLine);
            }

            // estrazione dei valori testuali
            String matricola = parts[0];
            String isbn = parts[1];
            String loanDateStr = parts[2];
            String dueDateStr = parts[3];
            String returnDateStr = parts[4];

            // se una data è scritta male -> il file dei prestiti non è valido
            try {

                /*
                prende la stringa loanDateStr -> la coverte in un oggetto LocalDate -> 
                se la stringa non è una data valida, Java lancia DateTimeParseException
                 */
                java.time.LocalDate loanDate = java.time.LocalDate.parse(loanDateStr);
                java.time.LocalDate dueDate = java.time.LocalDate.parse(dueDateStr); // stessa cpsa per la data di scadenza

                java.time.LocalDate returnDate = null; // inzializzo la data di restituizione a null -> prestito non restituito

                // se returnDateStr è null -> uso "" -> altrimenti fa trim() quindi rimuovo spazi all'inizio e alla fine
                String trimmedReturn = returnDateStr == null ? "" : returnDateStr.trim();

                // controllo se la data di restituizione è davvero presente
                // entra nell'if solo se 1) trimmedReturn non è vuota (quindi non è "") 2) trimmedreturn non è la stringa null
                if (!trimmedReturn.isEmpty() && !"null".equalsIgnoreCase(trimmedReturn)) {
                    returnDate = java.time.LocalDate.parse(trimmedReturn); // converte trimmedretunr in localdate (se non è una data valida -> eccezione e si va nel catch)
                }

                // creo oggetto Loan 
                Loan loan = new Loan(matricola, isbn, loanDate, dueDate, returnDate);
                result.add(loan); // aggiungo il prestito appena creato alla lista result che poi viene restituita dal metodo
            } catch (java.time.format.DateTimeParseException e) {
                /*
                trasforma l'errore di parsinh in un errore di file (ioexception)
                 */
                throw new IOException("Valore di data non valido nella riga del file dei prestiti: " + rawLine, e);
            }
        }

        return result;
    }

    /**
     * @brief Salva sul file l'elenco dei prestiti.
     *
     * Scrive l'header LOANS_HEADER seguito da una riga per ogni prestito.
     *
     * @param file File di destinazione.
     * @param loans Collezione di prestiti da salvare.
     * @throws IOException In caso di errori di scrittura.
     */
    public void saveLoans(Path file, Collection<Loan> loans) throws IOException {
        // se il file è null -> IllegalArgumentException perchè il metodo non sa dove scrivere
        if (file == null) {
            throw new IllegalArgumentException("file non deve essere null");
        }
        // se loans è null -> nulla da salvare 
        if (loans == null) {
            throw new IllegalArgumentException("loans non deve essere null");
        }

        List<String> lines = new ArrayList<>(); // lines contiene tutte le righe del file
        lines.add(LOANS_HEADER); // la prima riga è sempre l'header

        for (Loan loan : loans) { // iterazione su tutti i prestiti 
            if (loan == null) { // se nella collezione dei prestiti c'è un null -> viene ignorato
                continue;
            }

            // estrazione e validazione dei campi 
            String matricola = requireNoSeparator(loan.getUserMatricola()); // controlla che non contenga il separatore
            String isbn = requireNoSeparator(loan.getBookIsbn());
            String loanDate = loan.getLoanDate().toString(); // conversione delle date in stringhe
            String dueDate = loan.getDueDate().toString();

            //se returnDate esiste -> scrivila come data 
            // se returnDate è null -> scrivi la stringa null ( null nel file rappresenta prestito non restituito)
            String returnDate = loan.getReturnDate() != null ? loan.getReturnDate().toString() : "null";

            // costruzione della riga di testo -> tutti i campi vengono uniti usando il seperatore | 
            String line = String.join(SEPARATOR, matricola, isbn, loanDate, dueDate, returnDate);
            lines.add(line); // la riga viene aggiunta alla lista delle righe da scrivere
        }

        // creazione della directory se non esiste ( se esiste gia -> nessun errore)
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // scrittura finale su file (crea il file se non esiste) 
        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    }

    // operazioni sugli utenti 
    /**
     * @brief Carica l'elenco degli utenti da file.
     *
     * Ogni riga viene interpretata come un record utente basato sull'header
     * USERS_HEADER.
     *
     * @param file Percorso del file dei dati.
     * @return Lista degli utenti contenuti nel file.
     * @throws IOException Se il file è formattato in modo errato.
     */
    public List<User> loadUsers(Path file) throws IOException {
        // verifico che il percorso del file non sia nullo
        if (file == null) {
            throw new IllegalArgumentException("file non deve essere null");
        }

        // se il file non è stato ancora cretao il metodo non fallisce ma:
        if (!Files.exists(file)) {
            // 1) se la directoru che deve contenere il file non esiste -> viene creata
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // 2) creazione del file con solo l'header 
            // viene creato il file + viene scritto solo l'header + il file viene inizializzato 
            Files.write(file, Arrays.asList(USERS_HEADER), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return new ArrayList<>(); // restituisco lista vuota ( se il file non esiste ->  non ci sono utenti registrati) 
        }

        // lettura del file esistente 
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8); // tutte le righe del file vengono lette in memoria
        List<User> result = new ArrayList<>(); // result contiene gli utenti validi 

        // iterazione riga per riga (uso un ciclo perche la prima riga è l'header e bisogna sapere quando i == 0)
        for (int i = 0; i < lines.size(); i++) {
            // ignoro righe nulle
            String rawLine = lines.get(i);
            if (rawLine == null) {
                continue;
            }

            // rimuovo spazi inutili + righe vuote vengono ignorate 
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            // la prima riga non rappresenta un utente perche è l'header
            if (i == 0 && line.equals(USERS_HEADER)) {
                continue;
            }

            // suddivisione della riga in campi
            String[] parts = line.split("\\|", -1);
            // ogni riga deve contenere esattamente 4 campi
            if (parts.length != 4) {
                throw new IOException("Riga non valida nel file degli utenti: " + rawLine);
            }

            // estrazione dei campi
            String matricola = parts[0]; // matricola è obbligatoria 
            String firstName = emptyToNull(parts[1]); // nome, cognome e mail se sono vuoti diventano null
            String lastName = emptyToNull(parts[2]);
            String email = emptyToNull(parts[3]);

            // creazione dell'oggetto User
            User user = new User(matricola, firstName, lastName, email);
            // l'utente valido viene salvato nella lista finale
            result.add(user);
        }

        return result; // alla fine del metodo la lista contiene solo utenti validi 
    }

    /**
     * @brief Scrive su file la lista degli utenti.
     *
     * Converte ciascun utente in una riga di testo separata da '|'.
     *
     * @param file Percorso del file di destinazione.
     * @param users Collezione di utenti da salvare.
     * @throws IOException In caso di errore di scrittura.
     */
    // questo metodo è speculare a saveBooks e saveLoans, cambia solo il tipo di dato (User) e il numero di campi 
    public void saveUsers(Path file, Collection<User> users) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file non deve essere null");
        }
        if (users == null) {
            throw new IllegalArgumentException("users non deve essere null");
        }

        List<String> lines = new ArrayList<>();
        lines.add(USERS_HEADER);

        for (User user : users) {
            if (user == null) {
                continue;
            }
            String matricola = requireNoSeparator(user.getMatricola());
            String firstName = requireNoSeparator(nullToEmpty(user.getFirstName()));
            String lastName = requireNoSeparator(nullToEmpty(user.getLastName()));
            String email = requireNoSeparator(nullToEmpty(user.getEmail()));

            String line = String.join(SEPARATOR,
                    matricola,
                    firstName,
                    lastName,
                    email);
            lines.add(line);
        }

        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    }

    // metodi di utilità 
    /**
     * @brief Converte un valore null in stringa vuota.
     *
     * @param value Valore da convertire.
     * @return Stringa vuota se il valore è null; altrimenti il valore stesso.
     */
    private static String nullToEmpty(String value) {
        return value == null ? "" : value;

    }

    /**
     * @brief Converte una stringa vuota in null.
     *
     * @param value Valore da convertire.
     * @return null se la stringa è vuota; altrimenti la stringa originale.
     */
    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    /**
     * @brief Verifica che un valore non contenga il separatore '|'.
     *
     * Questo metodo è utile per impedire che un campo corrompa la struttura del
     * file.
     *
     * @param value Valore da controllare.
     * @return Il valore stesso se valido.
     * @throws IllegalArgumentException Se contiene il separatore.
     */
    private static String requireNoSeparator(String value) {
        if (value != null && value.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Field value must not contain '" + SEPARATOR + "': " + value);
        }
        return value;
    }
}
