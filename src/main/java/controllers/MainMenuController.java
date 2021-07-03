package controllers;

import javafx.event.ActionEvent;
import java.io.IOException;


public class MainMenuController {

    PreLobbyController preLobbyController = PreLobbyController.getPreLobbyControllerInstance();

    public void switchToLobby(ActionEvent event) throws IOException {
        preLobbyController.sceneLoader("FXML/Pre-Lobby.fxml", event);
    }


    public void openRuleSet(ActionEvent event) throws IOException {
        preLobbyController.sceneLoader("FXML/rules1.fxml", event);
    }
}
