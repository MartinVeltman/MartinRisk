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

    private Stage stage;
    private Scene scene;
    private Parent root;
    LoginController loginController = new LoginController();
    PlayerModel playerModel;

    @FXML
    TextField usernameField;

    public PreLobbyController(){
        playerModel = PlayerModel.getPlayerModelInstance();
    }

    public void switchLobby(String name, ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(name)));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
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
            switchLobby("FXML/Lobby.fxml", event);
        }
    }

    public void switchToJoinedLobby(ActionEvent event) throws IOException {
        playerModel.setUsername(usernameField.getText());
        if (loginController.emptyUsername(usernameField.getText())) {
            displayUsernameEmpty();
        } else {
            playerModel.setUsername(usernameField.getText());
            switchLobby("FXML/JoinLobby.fxml", event);
        }
    }
}
