package views;

import controllers.PreLobbyController;
import javafx.event.ActionEvent;

import java.io.IOException;

public class RulesViewController {
    PreLobbyController preLobbyController = PreLobbyController.getPreLobbyControllerInstance();

    public void backToMenu(ActionEvent event) throws IOException {
        preLobbyController.sceneLoader("FXML/mainMenuScreen.fxml", event);
    }

    public void openRuleSet(ActionEvent event) throws IOException {
        preLobbyController.sceneLoader("FXML/rules1.fxml", event);
    }

    public void openRuleSet2(ActionEvent event) throws IOException {
       preLobbyController.sceneLoader("FXML/rules2.fxml", event);
    }

    public void openRuleSet3(ActionEvent event) throws IOException {
        preLobbyController.sceneLoader("FXML/rules3.fxml", event);
    }

}
