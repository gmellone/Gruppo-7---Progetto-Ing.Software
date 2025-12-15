package com.mycompany.gestionebiblioteca.ui;

import com.mycompany.gestionebiblioteca.persistence.FileManager;
import com.mycompany.gestionebiblioteca.repository.*;
import com.mycompany.gestionebiblioteca.service.*;
import com.mycompany.gestionebiblioteca.ui.controllers.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * @file App.java
 * @brief Punto di ingresso principale dell'applicazione "Gestione Biblioteca"
 *
 * Questa classe è responsabile dell'inizializzazione dell'intero sistema delle
 * dipendenze. Configura i Repository (persistenza su file), i Service (logica
 * di business) e i Controller (interfaccia utente), collegandoli tra loro.
 *
 * @author Carmine
 *
 */
public class App extends Application {

    /**
     * @brief Metodo di avvio dell'applicazione JavaFX.
     *
     * Esegue le seguenti operazioni: 
     * inizializza i repository basati su file (User, Book, Loan); 
     * crea i servizi iniettando (Dependency Injection) i repository necessari; 
     * carica i file FXML e inietta i servizi nei controller;
     * configura il TabPane e i listener per il refresh automatico dei dati al cambio scheda; 
     * applica gli stili visivi (CSS).
     *
     * @param primaryStage Lo stage principale fornito dalla piattaforma JavaFX.
     * @throws IOException Se i file FXML non vengono trovati o non possono essere caricati.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        //SETUP REPOSITORY
        FileManager fileManager = new FileManager();

        Path loansFile = Paths.get("loans.txt");
        LoanRepository loanRepository = new FileBackedLoanRepository(
                new InMemoryLoanRepository(), fileManager, loansFile);

        Path booksFile = Paths.get("books.txt");
        BookRepository bookRepository = new FileBackedBookRepository(
                new InMemoryBookRepository(), fileManager, booksFile);

        Path usersFile = Paths.get("users.txt");
        UserRepository userRepository = new FileBackedUserRepository(
                new InMemoryUserRepository(), fileManager, usersFile);

        // SETUP SERVICE 
        // LoanService ha bisogno di User e Book repository
        LoanService loanService = new LoanService(loanRepository, bookRepository, userRepository);

        // UserService ha bisogno di LoanService per i controlli di cancellazione 
        // Se l'utente ha libri in prestito non può essere eliminato
        UserService userService = new UserService(userRepository, loanService);

        // BookService ha bisogno di LoanService per i controlli di cancellazione
        // Se il libro è attualmente in prestito non può essere eliminato
        BookService bookService = new BookService(bookRepository, loanService);

        // SETUP VIEW E CONTROLLERS 
        FXMLLoader bookLoader = new FXMLLoader(getClass().getResource("/com/mycompany/gestionebiblioteca/ui/view/BookView.fxml"));
        javafx.scene.Parent bookRoot = bookLoader.load();
        BookController bookController = bookLoader.getController();
        bookController.setBookService(bookService);

        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/com/mycompany/gestionebiblioteca/ui/view/UserView.fxml"));
        javafx.scene.Parent userRoot = userLoader.load();
        UserController userController = userLoader.getController();
        userController.setUserService(userService);

        FXMLLoader loanLoader = new FXMLLoader(getClass().getResource("/com/mycompany/gestionebiblioteca/ui/view/LoanView.fxml"));
        javafx.scene.Parent loanRoot = loanLoader.load();
        LoanController loanController = loanLoader.getController();

        loanController.setServices(loanService, userService, bookService);

        // SCENE E STAGE
        TabPane tabPane = new TabPane();
        Tab booksTab = new Tab("Libri", bookRoot);
        Tab usersTab = new Tab("Utenti", userRoot);
        Tab loansTab = new Tab("Prestiti", loanRoot);

        booksTab.setClosable(false);
        usersTab.setClosable(false);
        loansTab.setClosable(false);

        //Quando seleziono la scheda Utenti, aggiorno la tabella utenti
        usersTab.setOnSelectionChanged(event -> {
            if (usersTab.isSelected()) {
                userController.refreshUsers();
            }
        });

        //Quando seleziono la scheda Libri, aggiorno la tabella libri
        booksTab.setOnSelectionChanged(event -> {
            if (booksTab.isSelected()) {
                bookController.refreshBooks();
            }
        });

        // Quando seleziono la scheda Prestiti, aggiorno tutto (Utenti, Libri, Prestiti)
        loansTab.setOnSelectionChanged(event -> {
            if (loansTab.isSelected()) {
                loanController.refreshAllData();
            }
        });

        tabPane.getTabs().addAll(booksTab, usersTab, loansTab);

        Scene scene = new Scene(tabPane);
        primaryStage.setTitle("Library Management System");

        // CSS LOADING (Gestione errori se il file non esiste)
        loadCss(scene, "/styles/buttons.css");
        loadCss(scene, "/styles/textfields.css");
        loadCss(scene, "/styles/tables.css");

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * @brief Carica un foglio di stile CSS in modo sicuro.
     *
     * Tenta di caricare il file CSS specificato. 
     * Se il file non esiste, stampa un errore in console invece di far crashare l'applicazione.
     *
     * @param scene La scena a cui applicare il foglio di stile.
     * @param path Il percorso relativo del file CSS (es. "/styles/buttons.css").
     */
    private void loadCss(Scene scene, String path) {
        URL resource = getClass().getResource(path);
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.err.println("ATTENZIONE: File CSS non trovato: " + path);
        }
    }
    /**
     * @brief Punto di ingresso standard per le applicazioni Java.
     *
     * Lancia il runtime di JavaFX chiamando il metodo launch().
     *
     * @param args Argomenti da riga di comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
