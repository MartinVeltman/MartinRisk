package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainMenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    PreLobbyController preLobbyController = PreLobbyController.getPreLobbyControllerInstance();

    public void switchToLobby(ActionEvent event) throws IOException {
        preLobbyController.sceneLoader("FXML/Pre-Lobby.fxml", event);
    }


    public void openRuleSet(ActionEvent event) throws IOException {
        preLobbyController.sceneLoader("FXML/rules1.fxml", event);
    }
}
