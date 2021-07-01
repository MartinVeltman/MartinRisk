package views;


import application.State;
import controllers.LoginController;
import controllers.SpelbordController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import models.GameModel;
import models.SpelbordModel;
import java.util.concurrent.ExecutionException;

public class SpelbordView {

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



    SpelbordController spelbordController = SpelbordController.getSpelbordControllerInstance();
    static SpelbordModel spelbordModel;
    GameModel gameModel = new GameModel();
    //GameModel gameModel;
    //static SpelbordViewController spelbordViewController;
    LoginController loginController = new LoginController();

    @FXML
    public void initialize() {
        spelbordController.setView(this);
    }

//    public static SpelbordViewController getSpelbordViewControllerInstance() {
//        if (spelbordViewController == null) {
//            spelbordViewController = new SpelbordViewController();
//            System.out.println("nieuwe instantie van spelBordViewController is aangemaakt");
//        }
//        return spelbordViewController;
//    }

//    public SpelbordViewController() {
//        spelbordController.registerObserver(this);
//    }


    public void getButtonID(ActionEvent event) throws ExecutionException, InterruptedException{
        spelbordController.getButtonID(this, event);
    }

    public void handleClicky() {
        spelbordController.handleClicky();
    }

    public void showCards() {
        spelbordController.showCards();
    }

    public void showPlayers() {
        spelbordController.showPlayers();
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0.2);
        cNA1.setEffect(colorAdjust);
    }//TODO op deze manier proberen kleur aan country te geven

    public void rollDice() {
        new Thread(() -> {
            try {
                spelbordController.rollDice(this);
            } catch (ExecutionException e) {
                e.printStackTrace();
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







    //TODO FIX HUD
//    public void hideHUD() {
//        cardIcon.setVisible(false);
//        diceIcon.setVisible(false);
//        playerIcon.setVisible(false);
//    }
//
//    public void showHUD() {
//        cardIcon.setVisible(true);
//        diceIcon.setVisible(true);
//        playerIcon.setVisible(true);
//    }
//
//    public void HUD() throws ExecutionException, InterruptedException {
//        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        DocumentSnapshot document = future.get();
//
//        if (State.TurnID == Integer.parseInt(document.get("gamestateTurnID").toString())) {
//            showHUD();
//        } else {
//            hideHUD();
//        }
//    }



}
