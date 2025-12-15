/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.ui.controllers;

import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.model.*;
import com.mycompany.gestionebiblioteca.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableCell;

/**
 * @file LoanController.java
 * @brief Controller JavaFX per la gestione dei prestiti (Loan).
 *
 * Questa classe gestisce l'interfaccia utente (GUI) relativa alla scheda
 * "Prestiti" per le operazioni di prestito. Coordina l'interazione tra tre
 * servizi: UserService: per selezionare l'utente a cui prestare il libro,
 * BookService: per selezionare il libro da prestare, LoanService: per
 * registrare il prestito o la restituzione.
 *
 * Gestisce inoltre la visualizzazione completa della tabella prestiti.
 *
 * @author Carmine
 */
public class LoanController {

    private LoanService loanService;
    private UserService userService;
    private BookService bookService;

    @FXML
    private Pane loanRootElement;

    //Tabella Utenti
    @FXML
    private TableView<User> userSelectionTable;
    @FXML
    private TableColumn<User, String> userMatricolaCol;
    @FXML
    private TableColumn<User, String> userNomeCol;
    @FXML
    private TableColumn<User, String> userCognomeCol;
    @FXML
    private TextField searchUserField;

    //Tabella Libri
    @FXML
    private TableView<Book> bookSelectionTable;
    @FXML
    private TableColumn<Book, String> bookIsbnCol;
    @FXML
    private TableColumn<Book, String> bookTitleCol;
    @FXML
    private TableColumn<Book, String> bookAuthorCol;
    @FXML
    private TextField searchBookField;

    @FXML
    private DatePicker loanDatePicker;
    @FXML
    private DatePicker dueDatePicker;

    //Tabella prestiti attivi
    @FXML
    private TableView<Loan> activeLoansTable;
    @FXML
    private TableColumn<Loan, String> loanMatricolaCol;
    @FXML
    private TableColumn<Loan, String> loanIsbnCol;
    @FXML
    private TableColumn<Loan, LocalDate> loanDueDateCol;

    // Colonne per la tabella dei prestiti attivi 
    @FXML
    private TableColumn<Loan, String> loanUserNameCol;
    @FXML
    private TableColumn<Loan, String> loanUserSurnameCol;
    @FXML
    private TableColumn<Loan, String> loanBookTitleCol;
    @FXML
    private TableColumn<Loan, String> loanBookAuthorCol;

    /**
     * @brief Inietta i servizi necessari (Dependency Injection). Metodo
     * essenziale chiamato da App.java. Appena i servizi sono disponibili, viene
     * fatto un refresh completo dei dati.
     *
     * @param loanService Servizio prestiti.
     * @param userService Servizio utenti.
     * @param bookService Servizio libri.
     */
    public void setServices(LoanService loanService, UserService userService, BookService bookService) {
        this.loanService = loanService;
        this.userService = userService;
        this.bookService = bookService;

        // Refresh dei dati appena i service vengono inizializzati
        refreshAllData();
    }

    /**
     * @brief Setta solo il LoanService (metodo di utilità).
     * @param loanService Il servizio prestiti.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * @brief Inizializzazione della vista. Configura le colonne delle tre
     * tabelle (Utenti, Libri, Prestiti), imposta le date di default (oggi e tra
     * sette giorni), attiva i listener per la ricerca in tempo reale.
     */
    @FXML
    public void initialize() {
        setupUserTable();
        setupBookTable();
        setupLoanTable();

        // Imposto date di default per i DatePicker dei prestiti (oggi e tra 7 giorni)
        loanDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        // Listener per la ricerca dinamica
        searchUserField.textProperty().addListener((obs, old, val) -> filterUsers(val));
        searchBookField.textProperty().addListener((obs, old, val) -> filterBooks(val));
    }

    /**
     * @brief Configura la tabella dei prestiti attivi con colonne calcolate.
     * Oltre ai dati del prestito come Matricola e ISBN, configura colonne che
     * mostrano il nome dell'utente e il titolo del libro recuperandoli
     * dinamicamente dai service tramite i metodi getter.
     */
    private void setupLoanTable() {

        loanMatricolaCol.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getUserMatricola()));

        loanDueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        loanDueDateCol.setCellFactory(column -> {
            return new TableCell<Loan, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        // Qui trasformiamo la data nel formato giorno/mese/anno
                        setText(italianFormatter.format(item));
                    }
                }
            };
        });

        loanUserNameCol.setCellValueFactory(cellData -> {
            String matricola = cellData.getValue().getUserMatricola();
            return userService.getUserByMatricola(matricola)
                    .map(user -> new SimpleStringProperty(user.getFirstName()))
                    .orElse(new SimpleStringProperty(""));
        });

        loanUserSurnameCol.setCellValueFactory(cellData -> {
            String matricola = cellData.getValue().getUserMatricola();
            return userService.getUserByMatricola(matricola)
                    .map(user -> new SimpleStringProperty(user.getLastName()))
                    .orElse(new SimpleStringProperty(""));
        });

        loanBookTitleCol.setCellValueFactory(cellData -> {
            String isbn = cellData.getValue().getBookIsbn();
            return bookService.getBookByIsbn(isbn)
                    .map(book -> new SimpleStringProperty(book.getTitle()))
                    .orElse(new SimpleStringProperty("Libro Rimosso"));
        });

        loanBookAuthorCol.setCellValueFactory(cellData -> {
            String isbn = cellData.getValue().getBookIsbn();
            return bookService.getBookByIsbn(isbn)
                    .map(book -> new SimpleStringProperty(book.getAuthorsAsString()))
                    .orElse(new SimpleStringProperty(""));
        });
    }

    /**
     * @brief Gestisce l'evento di registrazione di un nuovo prestito: Verifica
     * che un utente e un libro siano selezionati nelle tabelle, recupera le
     * date dai datePicker, invoca "loanService.registerLoan()" per la logica di
     * business, in caso di successo, aggiorna tutte le tabelle decrementando le
     * copie disponibili.
     */
    @FXML
    private void onRegisterLoan() {
        try {
            // Recupero Utente Selezionato
            User selectedUser = userSelectionTable.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                showError("Seleziona un utente dalla tabella di sinistra");
                return;
            }

            Book selectedBook = bookSelectionTable.getSelectionModel().getSelectedItem();
            if (selectedBook == null) {
                showError("Seleziona un libro dalla tabella di destra");
                return;
            }

            LocalDate loanDate = loanDatePicker.getValue();
            LocalDate dueDate = dueDatePicker.getValue();

            // Delega al service
            loanService.registerLoan(
                    selectedUser.getMatricola(),
                    selectedBook.getIsbn(),
                    loanDate,
                    dueDate
            );

            showInfo("Successo", "Prestito registrato per " + selectedUser.getFirstName() + " " + selectedUser.getLastName());
            refreshAllData(); // Ricarica tutto
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Errore imprevisto: " + e.getMessage());
        }
    }

    /**
     * @brief Gestisce la restituzione di un libro (chiusura prestito): verifica
     * se la data di restituzione effettiva (oggi) è successiva alla scadenza
     * prevista e mostra un avviso in caso di ritardo, aggiorna lo stato del
     * prestito tramite "loanService.registerReturn()".
     */
    @FXML
    private void onReturnLoan() {

        Loan selectedLoan = activeLoansTable.getSelectionModel().getSelectedItem();

        if (selectedLoan == null) {
            showError("Seleziona un prestito dalla tabella in basso per restituirlo");
            return;
        }

        // La data di restituzione effettiva coincide con la data odierna
        LocalDate actualReturnDate = LocalDate.now();
        //formattazione della data di restituzione (per i messaggi di alert)
        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 2. Controllo Ritardo (con date formattate)
        if (actualReturnDate.isAfter(selectedLoan.getDueDate())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Prestito Scaduto");
            alert.setHeaderText("Attenzione: Restituzione in Ritardo!");

            // Uso .format(italianFormatter) per visualizzare le date correttamente
            alert.setContentText(
                    "Il prestito è scaduto.\n"
                    + "Data Scadenza Prevista: " + selectedLoan.getDueDate().format(italianFormatter) + "\n"
                    + "Data Restituzione Effettiva: " + actualReturnDate.format(italianFormatter) + "\n\n"
                    + "Verificare se è necessario applicare sanzioni."
            );

            alert.showAndWait();
        }

        try {
            loanService.registerReturn(selectedLoan, actualReturnDate);
            showInfo("Successo", "Libro restituito correttamente in data: "
                    + actualReturnDate.format(italianFormatter));

            refreshAllData();
        } catch (Exception e) {
            showError("Errore durante la restituzione: " + e.getMessage());
        }
    }

    /**
     * @brief Configura le colonne della tabella di selezione Utenti. Collega le
     * colonne della TableView alle proprietà dell'oggetto User. I nomi passati
     * al PropertyValueFactory devono corrispondere esattamente ai nomi dei
     * campi nella classe User.
     */
    private void setupUserTable() {
        userMatricolaCol.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        userNomeCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        userCognomeCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    }

    /**
     * @brief Configura le colonne della tabella di selezione Libri. * Collega
     * le colonne della TableView alle proprietà dell'oggetto Book. Serve per
     * visualizzare i libri disponibili per il prestito.
     */
    private void setupBookTable() {
        bookIsbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
    }

    /**
     * @brief Ricarica i dati di tutte le tabelle (Utenti, Libri, Prestiti). *
     * Fondamentale per mantenere la consistenza visuale: se registro un
     * prestito, la tabella Libri deve aggiornarsi per mostrare una copia
     * disponibile in meno.
     */
    public void refreshAllData() {
        if (userService == null || bookService == null || loanService == null) {
            return;
        }
        // Ricarico Utenti
        List<User> users = userService.getAllUsersOrderedByLastNameAndFirstName();
        userSelectionTable.setItems(FXCollections.observableArrayList(users));
        userSelectionTable.refresh();

        // Ricarico Libri
        List<Book> books = bookService.getAllBooksOrderedByTitle();
        bookSelectionTable.setItems(FXCollections.observableArrayList(books));
        bookSelectionTable.refresh();

        // Ricarico Prestiti Attivi
        List<Loan> loans = loanService.getActiveLoansOrderedByDueDate();
        activeLoansTable.setItems(FXCollections.observableArrayList(loans));
        activeLoansTable.refresh();
    }

    /**
     * @brief Aggiorna solo la tabella dei prestiti.
     */
    public void refreshLoans() {
        if (loanService == null) {
            return;
        }
        List<Loan> activeLoans = loanService.getActiveLoansOrderedByDueDate();
        activeLoansTable.getItems().setAll(activeLoans);
        activeLoansTable.refresh();
    }

    /**
     * @brief Filtra la tabella utenti in base alla barra di ricerca.
     * @param keyword Stringa di ricerca (cognome o matricola).
     */
    private void filterUsers(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            refreshAllData();
        } else {
            userSelectionTable.setItems(FXCollections.observableArrayList(userService.searchUsers(keyword)));
        }
    }

    /**
     * @brief Filtra la tabella libri in base alla barra di ricerca.
     * @param keyword Titolo del libro o ISBN.
     */
    private void filterBooks(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            refreshAllData();
        } else {
            bookSelectionTable.setItems(FXCollections.observableArrayList(bookService.searchByTitle(keyword)));
        }
    }

    /**
     * @brief Metodo ausiliario per visualizzare messaggi di errore critici. (ad
     * esempio impossibile registrare prestito, se l'utente ha già 3 prestiti
     * attivi)
     * @param message Il testo dell'errore.
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * @brief Metodo ausiliario per visualizzare messaggi di informazione (ad
     * esempio: prestito restituito con successo).
     * @param message Il testo del messaggio di informazione.
     */
    private void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
