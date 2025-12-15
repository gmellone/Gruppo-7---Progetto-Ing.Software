package com.mycompany.gestionebiblioteca.ui.controllers;

import com.mycompany.gestionebiblioteca.exceptions.NotFoundException;
import com.mycompany.gestionebiblioteca.exceptions.ValidationException;
import com.mycompany.gestionebiblioteca.model.User;
import com.mycompany.gestionebiblioteca.service.UserService;
import java.util.ArrayList;
import java.util.List;
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
 * @file UserController.java
 * @brief Controller JavaFX per la gestione dell'anagrafica Utenti.
 *
 * Questa classe gestisce l'interfaccia utente (GUI) relativa alla scheda
 * "Utenti" e le operazioni CRUD (Create, Read, Update, Delete) per gli utenti
 * della biblioteca. Interagisce con l'UserService per la logica di business e
 * controlla la validazione degli input e la visualizzazione degli errori.
 *
 * @author Carmine
 */
public class UserController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> matricolaColumn;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TextField matricolaField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField searchField;

    /**
     * @brief Lista osservabile che contiene i dati visualizzati nella tabella.
     */
    private final ObservableList<User> usersData = FXCollections.observableArrayList();

    /**
     * @brief Riferimento al servizio di business per gli utenti.
     */
    private UserService userService;

    /**
     * @brief Inietta il servizio UserService e carica i dati iniziali.
     * Metodo chiamato da App.java. 
     * Appena il servizio è disponibile, viene popolata la tabella con l'elenco completo degli utenti.
     * @param userService L'istanza del servizio utenti.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
        refreshUsers();
    }

    /**
     * @brief Inizializza la vista e configura i binding.
     * Configura le colonne della TableView usando PropertyValueFactory.
     * Configura inoltre i listener per la selezione delle righe (popolamento form)
     * e per il reset della ricerca quando il campo viene svuotato.
     */
    @FXML
    private void initialize() {
        matricolaColumn.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        userTable.setItems(usersData);

        // Listener per reset automatico della ricerca se il campo viene pulito (cleanForm)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                refreshUsers();
            }
        });

        // Listener selezione tabella: popola i campi di testo quando clicco su una riga
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateForm(newValue);
                    } else {
                        clearForm();
                    }
                }
        );
    }

    /**
     * @brief Esegue una ricerca avanzata sugli utenti. 
     * Logica di filtraggio:
     * se il campo è vuoto mostro tutti gli utenti, 
     * se l'input è numerico cerco per Matricola (con validazione), 
     * infine se l'input è testuale cerco corrispondenze parziali per nome o cognome
     */
    @FXML
    public void onSearchUser() {
        if (userService == null) {
            return;
        }

        String query = searchField.getText().trim();

        // Se vuoto, ricarica tutto
        if (query.isEmpty()) {
            refreshUsers();
            return;
        }

        // Controllo Matricola
        // Se l'input contiene solo numeri
        if (query.matches("\\d+")) {
            if (query.length() > 10) {
                usersData.clear();
                showInfo("Nessun risultato: La matricola non può superare le 10 cifre.");
                return;
            }
        }
        // Scarichiamo la lista completa e filtriamo qui nel controller
        List<User> allUsers = userService.getAllUsersOrderedByLastNameAndFirstName();
        List<User> filteredResults = new ArrayList<>();
        String lowerQuery = query.trim().toLowerCase();

        for (User user : allUsers) {
            // Check Matricola Parziale (es. cerco "06127" trova "06127...")
            boolean matchMatricola = user.getMatricola() != null && user.getMatricola().contains(query);

            // Check Cognome (Case Insensitive)
            boolean matchLastName = user.getLastName() != null && user.getLastName().toLowerCase().contains(lowerQuery);

            // Check Nome (Case Insensitive)
            boolean matchFirstName = user.getFirstName() != null && user.getFirstName().toLowerCase().contains(lowerQuery);

            // Se uno qualsiasi dei criteri combacia
            if (matchMatricola || matchLastName || matchFirstName) {
                filteredResults.add(user);
            }
        }

        if (filteredResults.isEmpty()) {
            showInfo("Nessun utente trovato per: " + query);
            usersData.clear();
        } else {
            usersData.setAll(filteredResults);
        }
    }

    /**
     * @brief Gestisce l'aggiunta di un nuovo utente.
     * Recupera i dati dai campi di testo e invoca il metodo addUser del servizio UserService. 
     * Gestisce, inoltre, le eccezioni di validazione (es. campi vuoti, email non valida).
     */
    @FXML
    private void onAddUser() {
        if (userService == null) {
            return;
        }

        String matricola = matricolaField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        try {
            userService.addUser(matricola, firstName, lastName, email);
            refreshUsers();
            clearForm();
            showInfo("Utente aggiunto con successo!");
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError("Errore imprevisto: " + e.getMessage());
        }
    }

    /**
     * @brief Gestisce la modifica di un utente esistente. 
     * Permette di modificare i dati, inclusa la matricola (gestita dall'UserService),
     * come per BookController, elimina l'utente con la matricola originale,
     * successivamente crea un nuovo utente con la nuova matricola inserita
     * Richiede che una riga sia selezionata nella tabella.
     */
    @FXML
    private void onUpdateUser() {
        if (userService == null) {
            return;
        }

        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un utente da modificare");
            return;
        }

        String oldMatricola = selected.getMatricola(); // matricola originale per identificare l'utente
        String newMatricola = matricolaField.getText(); // nuova matricola (potrebbe essere cambiata)

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        try {
            userService.updateUser(oldMatricola, newMatricola, firstName, lastName, email);
            refreshUsers();
        } catch (ValidationException | NotFoundException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError("Errore durante l'aggiornamento: " + e.getMessage());
        }
    }

    /**
     * @brief Gestisce l'eliminazione di un utente.
     * Mostra una finestra di conferma prima di procedere. 
     * Se l'utente ha prestiti attivi, 
     * l'UserService lancerà un'eccezione che verrà mostrata a video 
     * (impedendo la cancellazione).
     */
    @FXML
    private void onDeleteUser() {
        if (userService == null) {
            return;
        }

        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un utente da eliminare");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Sei sicuro di voler eliminare l'utente " + selected.getFirstName() + " " + selected.getLastName() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    userService.deleteUser(selected.getMatricola());
                    refreshUsers();
                    clearForm();
                } catch (NotFoundException e) {
                    showError(e.getMessage());
                } catch (RuntimeException e) {
                    // Questa catch intercetta errori come "Impossibile eliminare utente con prestiti attivi"
                    showError("Impossibile eliminare: " + e.getMessage());
                }
            }
        });
    }
     /**
     * @brief Aggiorna la tabella degli utenti.
     */
    @FXML
    private void onRefreshUsers() {
        refreshUsers();
    }
     /**
     * @brief Pulisce i campi del form di inserimento.
     */
    @FXML
    private void onClearForm() {
        clearForm();
    }

    /**
     * @brief Ricarica la lista completa degli utenti dal servizio.
     */
    public void refreshUsers() {
        if (userService != null) {
            List<User> allUsers = userService.getAllUsersOrderedByLastNameAndFirstName();
            usersData.setAll(allUsers);
            userTable.refresh();
        }
    }

    /**
     * @brief Compila i campi del form con i dati dell'utente selezionato.
     * @param user L'utente i cui dati devono essere mostrati.
     */
    private void populateForm(User user) {
        matricolaField.setText(user.getMatricola());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
    }

    /**
     * @brief Pulisce i campi di input e deseleziona la tabella.
     */
    private void clearForm() {
        matricolaField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        userTable.getSelectionModel().clearSelection();
    }
     /**
     * @brief Metodo ausiliario per visualizzare messaggi di errore critici.
     * (ad esempio impossibile eliminare l'utente, se l'utente ha prestiti attivi)
     * @param message Il testo dell'errore.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Errore");
        alert.showAndWait();
    }
    /**
     * @brief Metodo ausiliario per visualizzare messaggi di informazione 
     * (ad esempio: utente registrato con successo).
     * @param message Il testo del messaggio di informazione.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.showAndWait();
    }
}
