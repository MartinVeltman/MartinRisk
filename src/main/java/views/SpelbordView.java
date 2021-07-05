package views;

import controllers.SpelbordController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import models.GameModel;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpelbordView {

    private final static Logger logger = Logger.getLogger(SpelbordView.class.getName());
    SpelbordController spelbordController = SpelbordController.getSpelbordControllerInstance();
    GameModel gameModel = new GameModel();

    @FXML
    public static ImageView endTurnIcon;
    @FXML
    public static ImageView cardIcon;
    @FXML
    public static ImageView diceIcon;
    @FXML
    public static ImageView playerIcon;
    @FXML
    public Label stateField;
    @FXML
    public ImageView cNA1;


    @FXML
    public void initialize() {
        spelbordController.setView(this);
    }


    public void getButtonID(ActionEvent event) throws ExecutionException, InterruptedException{
        spelbordController.attackCountry(this, event);
    }



    public void showCards() {
        spelbordController.showCards();
    }

    public void showPlayers() {
        spelbordController.showPlayers();
    }

    public void rollDice() {
        new Thread(() -> {
            try {
                spelbordController.rollDiceAttack(this);
            } catch (ExecutionException | InterruptedException e) {
                logger.log(Level.INFO, "gooit exption: ", e);
            }
        }).start();
    }

    public void endTurn() throws ExecutionException, InterruptedException {
        spelbordController.endTurn();
        stateField.setText("Speler: " + gameModel.getTurnID() + " Is nu aan de beurt");
    }

    public void setStateText(String text) {
        Platform.runLater(() -> {
            stateField.setText(text);
        });
    }

    public void noCountrySelected(){
        Platform.runLater(() -> {
            stateField.setText("Klik eerst op het land dat je wilt aanvallen");
        });
    }

    public void cantAttack(){
        stateField.setText("Je kan je eigen land niet aanvallen dat is zelfmoord");
    }

    public void dobbelen(){
        Platform.runLater(() -> {
            stateField.setText("Er word automatisch gedobbeld....");
        });

    }

    public void notYourTurn(){
        Platform.runLater(() -> {
            stateField.setText("Je bent niet aan de beurt");
        });
    }

    public void turnEnds(){
        Platform.runLater(() -> {
            stateField.setText("Je turn wordt beindigt loser");
        });
    }

    public void attackAgain(){
        Platform.runLater(() -> {
            stateField.setText("Je mag nog een keer als je wilt winnaar");
        });
    }

}
