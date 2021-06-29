package views;


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

public class SpelbordViewController{

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


    public void displayAttack(String land) throws InterruptedException {
        stateField.setText(land+ " word aangevallen door speler: " + gameModel.getTurnID());
    }

    public void cantAttack(){
        stateField.setText(" speler: " + gameModel.getTurnID() + " probeert zijn eigen land aan te vallen HAHA");
    }
    public void dobbelen(){
        Platform.runLater(() -> {
            stateField.setText("Er word automatisch gedobbeld....");
        });

    }
    public void attackerWins(Integer steen1, Integer steen2){
        Platform.runLater(() -> {
            stateField.setText("De aanvaller wint met een " + steen1 + " en een " + steen2);
        });
    }

    public void defenderWins(Integer steen1, Integer steen2){
        Platform.runLater(() -> {
            stateField.setText("De verdediger wint met een " + steen1 + " en een " + steen2);


        });
    }

    public void draw(){
        Platform.runLater(() -> {
            stateField.setText("het is gelijkspel makkers");
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


    public void garrison(){
        //Todo catch mouseEvent
        // catch clickedCountry
        // Check if country has the same playerID as the player

    }


}
