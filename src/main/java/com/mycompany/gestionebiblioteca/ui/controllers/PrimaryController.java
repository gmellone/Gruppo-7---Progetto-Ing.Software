package com.mycompany.gestionebiblioteca.ui.controllers;

import com.mycompany.gestionebiblioteca.ui.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
 