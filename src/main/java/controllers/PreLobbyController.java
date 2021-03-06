package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.PlayerModel;
import java.io.IOException;
import java.util.Objects;

public class PreLobbyController {

    LoginController loginController = new LoginController();
    PlayerModel playerModel;
    static PreLobbyController preLobbyController;

    @FXML
    TextField usernameField;

    public PreLobbyController(){
        playerModel = PlayerModel.getPlayerModelInstance();
    }

    public static PreLobbyController getPreLobbyControllerInstance() {
        if (preLobbyController == null) {
            preLobbyController = new PreLobbyController();
        }
        return preLobbyController;
    }

    public void sceneLoader(String name, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(name)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void displayUsernameEmpty(){
        usernameField.setText("Give name !");
    }


    public void switchToCreatedLobby(ActionEvent event) throws IOException {
        playerModel.setUsername(usernameField.getText());
        if (loginController.emptyUsername(usernameField.getText())) {
           displayUsernameEmpty();
        } else {
            loginController.checkCreate(usernameField.getText());
            sceneLoader("FXML/Lobby.fxml", event);
        }
    }

    public void switchToJoinedLobby(ActionEvent event) throws IOException {
        playerModel.setUsername(usernameField.getText());
        if (loginController.emptyUsername(usernameField.getText())) {
            displayUsernameEmpty();
        } else {
            playerModel.setUsername(usernameField.getText());
            sceneLoader("FXML/JoinLobby.fxml", event);
        }
    }
}
