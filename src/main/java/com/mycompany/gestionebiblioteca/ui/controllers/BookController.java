/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.ui.controllers;

import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.service.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @file BookController.java
 * @brief Controller JavaFX per la gestione del catalogo libri.
 *
 * Questa classe gestisce l'interfaccia utente (GUI) relativa alla scheda
 * "Libri". Si occupa di visualizzare la lista dei libri, gestire l'inserimento,
 * la modifica, la cancellazione e la ricerca, delegando la logica di business
 * al BookService.
 *
 * @author Carmine
 */
public class BookController {

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, String> isbnColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, Integer> yearColumn;

    @FXML
    private TableColumn<Book, Integer> totalCopiesColumn;

    @FXML
    private TableColumn<Book, Integer> availableCopiesColumn;

    @FXML
    private TextField isbnField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField yearField;

    @FXML
    private TextField totalCopiesField;

    @FXML
    private TextField searchField;

    /**
     * @brief Lista osservabile per il binding dei dati alla tabella
     */
    private final ObservableList<Book> booksData = FXCollections.observableArrayList();

    private BookService bookService;

    /**
     * @brief Inietta il BookService nel controller.
     *
     * Questo metodo viene chiamato da App.java per fornire al controller
     * l'istanza del servizio necessaria per operare sui dati. Appena il
     * servizio è settato, viene effettuato un refresh della tabella.
     *
     * @param bookService L'istanza di BookService.
     */
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
        refreshBooks();
    }

    /**
     * @brief Metodo di inizializzazione standard di JavaFX.
     *
     * Configura le colonne della TableView associandole alle proprietà della
     * classe Book. Imposta inoltre un listener sulla selezione della tabella
     * per popolare automaticamente i campi del form quando un utente clicca su
     * una riga.
     */
    @FXML
    private void initialize() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getAuthorsAsString()));

        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        totalCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        availableCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));

        bookTable.setItems(booksData);

        bookTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        populateForm(newVal);
                    } else {
                        clearForm();
                    }
                });
    }

    /**
     * @brief Gestisce l'evento di aggiunta di un nuovo libro.
     *
     * Legge i dati dai campi di testo, effettua la validazione di anno e copie
     * e chiama il metodo addBook del servizio BookService. In caso di errori
     * (campi vuoti, formato errato), mostra un alert.
     */
    @FXML
    private void onAddBook() {
        if (bookService == null) {
            showError("Servizio non inizializzato");
            return;
        }

        String isbn = isbnField.getText();
        String title = titleField.getText();
        String authorsInput = authorField.getText();
        List<String> authors = parseAuthors(authorsInput);
        int year;
        int totalCopies;
        try {
            year = Integer.parseInt(yearField.getText().trim());
            totalCopies = Integer.parseInt(totalCopiesField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Anno e copie totali devono essere numeri interi");
            return;
        }

        try {
            bookService.addBook(isbn, title, authors, year, totalCopies);
            refreshBooks();
            clearForm();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError("Errore durante il salvataggio del libro: " + e.getMessage());
        }
    }

    /**
     * @brief Gestisce la modifica dei dati di un libro esistente.
     *
     * Richiede che un libro sia selezionato nella tabella. Utilizza l'ISBN
     * originale del libro selezionato per identificarlo, permettendo di
     * modificare anche l'ISBN stesso, cancellando il libro con l'ISBN originale
     * e creandone uno nuovo con il nuovo ISBN da inserire (gestito dal
     * service).
     */
    @FXML
    private void onUpdateBook() {
        if (bookService == null) {
            showError("Servizio non inizializzato");
            return;
        }

        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un libro da modificare");
            return;
        }

        String oldIsbn = selected.getIsbn();

        String isbn = isbnField.getText();
        String title = titleField.getText();
        String authorsInput = authorField.getText();
        List<String> authors = parseAuthors(authorsInput);
        int year;
        int totalCopies;
        try {
            year = Integer.parseInt(yearField.getText().trim());
            totalCopies = Integer.parseInt(totalCopiesField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Anno e copie totali devono essere numeri interi");
            return;
        }

        try {
            bookService.updateBook(oldIsbn, isbn, title, authors, year, totalCopies);
            refreshBooks();
            clearForm();
        } catch (ValidationException | NotFoundException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError("Errore durante l'aggiornamento del libro: " + e.getMessage());
        }
    }

    /**
     * @brief Gestisce la rimozione di un libro.
     *
     * Chiede conferma all'utente tramite una finestra di dialogo prima di
     * procedere. Se il libro è attualmente in prestito, il BookService
     * solleverà un'eccezione che verrà catturata e mostrata qui.
     */
    @FXML
    private void onDeleteBook() {
        if (bookService == null) {
            showError("Servizio non inizializzato");
            return;
        }

        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un libro da eliminare");
            return;
        }
        // logica per confermare la rimozione di un libro con Alert di conferma
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Sei sicuro di voler eliminare il libro selezionato?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    bookService.deleteBook(selected.getIsbn());
                    refreshBooks();
                    clearForm();
                } catch (NotFoundException | ValidationException e) {
                    showError(e.getMessage());
                } catch (RuntimeException e) {
                    showError("Errore durante l'eliminazione del libro: " + e.getMessage());
                }
            }
        });
    }

    /**
     * @brief Ricarica manualmente la lista dei libri. Azionato dal bottone
     * "Ricarica" nella GUI
     */
    @FXML
    private void onRefreshBooks() {
        if (bookService == null) {
            showError("Servizio non inizializzato");
            return;
        }
        refreshBooks();
    }

    /**
     * @brief Pulisce i campi del form di inserimento.
     */
    @FXML
    private void onClearForm() {
        clearForm();
    }

    /**
     * @brief Esegue la ricerca dei libri.
     *
     * Implementa una logica di filtro sulla lista: 1. Se la query è vuota,
     * mostra tutti i libri. 2. Se la query è numerica, cerca per corrispondenza
     * parziale nell'ISBN. 3. Altrimenti, cerca per corrispondenza parziale su
     * Titolo o Autore (Case Insensitive).
     */
    @FXML
    private void onSearchBook() {
        if (bookService == null) {
            return;
        }

        String query = searchField.getText().trim();
        // Mostro la lista completa se il campo di ricerca è vuoto
        if (query.isEmpty()) {
            refreshBooks();
            return;
        }

        // Se la stringa contiene solo cifre: ricerca per ISBN
        if (query.matches("\\d+")) {
            // Se l'ISBN supera le 13 cifre, blocco tutto e mostro una lista vuota
            if (query.length() > 13) {
                booksData.clear();
                return;
            }
        }
        // Recupero una lista con tutti i libri per 
        List<Book> allBooks = bookService.getAllBooksOrderedByTitle();

        List<Book> filteredResults = new ArrayList<>();
        String lowerQuery = query.trim().toLowerCase(); //Per ignorare maiuscole/minuscole su titolo e autore

        for (Book book : allBooks) {
            // Check ISBN Parziale 
            boolean matchIsbn = book.getIsbn() != null && book.getIsbn().contains(query);

            // Check Titolo (Case Insensitive)
            boolean matchTitle = book.getTitle() != null && book.getTitle().toLowerCase().contains(lowerQuery);

            // Check Autore (Case Insensitive)
            boolean matchAuthor = book.getAuthors() != null && book.getAuthors().contains(lowerQuery);

            // Se almeno un di questi tre criteri combacia viene visualizzato il risultato sotto forma di lista
            if (matchIsbn || matchTitle || matchAuthor) {
                filteredResults.add(book);
            }
        }

        if (filteredResults.isEmpty()) {
            showInfo("Nessun libro trovato per: " + query);
            booksData.clear();
        } else {
            booksData.setAll(filteredResults);
        }
    }

    /**
     * @brief Metodo ausiliario per mostrare messaggi informativi.
     * @param message Il messaggio da visualizzare.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Ricerca");
        alert.showAndWait();
    }

    /**
     * @brief Recupera tutti i libri dal servizio e aggiorna la TableView.
     */
    public void refreshBooks() {
        if (bookService == null) {
            return;
        }
        List<Book> books = bookService.getAllBooksOrderedByTitle();
        booksData.setAll(books);
        bookTable.refresh();
    }

    /**
     * @brief Riempie i campi del form con i dati di un libro selezionato.
     * @param book Il libro da visualizzare nei campi di input.
     */
    private void populateForm(Book book) {
        isbnField.setText(book.getIsbn());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthorsAsString());
        yearField.setText(String.valueOf(book.getYear()));
        totalCopiesField.setText(String.valueOf(book.getTotalCopies()));
    }

    /**
     * @brief Pulisce tutti i campi di input e deseleziona l'oggetto nella
     * tabella.
     */
    private void clearForm() {
        isbnField.clear();
        titleField.clear();
        authorField.clear();
        yearField.clear();
        totalCopiesField.clear();
        bookTable.getSelectionModel().clearSelection();
    }

    /**
     * @brief Metodo ausiliario per visualizzare messaggi di errore critici.
     * @param message Il testo dell'errore.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private List<String> parseAuthors(String authorsInput) {
        if (authorsInput == null || authorsInput.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(authorsInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
