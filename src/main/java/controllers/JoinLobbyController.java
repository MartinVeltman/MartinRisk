package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.PlayerModel;
import java.io.IOException;


public class JoinLobbyController {

    LoginController loginController = new LoginController();
    PlayerModel playerModel;
    PreLobbyController preLobbyController = PreLobbyController.getPreLobbyControllerInstance();

    @FXML
    TextField codeField;

    public JoinLobbyController(){
        playerModel = PlayerModel.getPlayerModelInstance();
    }

    public void switchToInsertLobbycode(ActionEvent event) throws IOException{
        if (loginController.validateLobby(codeField.getText())) {
            if (loginController.checkJoin(playerModel.getUsername(), codeField.getText())){
                preLobbyController.sceneLoader("FXML/Lobby.fxml", event);
            }
        } else {
            codeField.setText("Code ongeldig");

        }
    }


}
