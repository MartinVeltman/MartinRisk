package controllers;

import application.State;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import models.DiceModel;
import models.GameModel;
import models.SpelbordModel;
import observers.SpelbordObservable;
import observers.SpelbordObserver;
import views.SpelbordView;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpelbordController implements SpelbordObserver, UpdatableController {

    private int dataBaseInt = 0;
    static GameModel gameModel;
    static SpelbordModel spelbordModel;
    private boolean canEnd;
    static SpelbordController spelbordController;
    private SpelbordView view;
    private int selectCountry = 0;
    private int winInt = 0;
    private final static Logger logger = Logger.getLogger(SpelbordController.class.getName());


    public static SpelbordController getSpelbordControllerInstance() {
        if (spelbordController == null) {
            spelbordController = new SpelbordController();
        }
        return spelbordController;
    }



    public static GameModel getGameModelInstance() {
        if (gameModel == null) {
            gameModel = new GameModel(1);
        }
        return gameModel;
    }

    EventHandler<MouseEvent> eventHandler = e -> System.out.println("ER is geklikt");

    public void attachlistener() {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                int firebaseTurnID = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getData()).get("gamestateTurnID").toString());
                gameModel.setTurnID(firebaseTurnID);
                try {
                    startMainLoop();
                } catch (ExecutionException | InterruptedException executionException) {
                    logger.log(Level.INFO, "gooit exption: ", executionException);
                }
            }
        });
    }

    public void startMainLoop() throws ExecutionException, InterruptedException {

        if (gameModel.getTurnID() == State.TurnID) {
            State.stage.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
            canEnd = true;
        } else {
            canEnd = false;
        }
    }

    public SpelbordController() {
        gameModel = getGameModelInstance();
        spelbordModel = SpelbordModel.getSpelbordModelInstance();
        attachlistener();
        attachStateListener();
        attachWinListener();
    }

    public void setArmyAndCountryInFirebase() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.get("countries") != null) {
            logger.log(Level.INFO, "Countries zijn al gemaakt");
        } else {
            spelbordModel.CountriesAndIdMap();
            Map<String, Object> data = new HashMap<>();
            data.put("countries", spelbordModel.getCountries());

            docRef.update(data);
            docRef.update(data);

        }
    }

    public void nextTurnIDFirebase() throws ExecutionException, InterruptedException {

        if (canEnd) {
            int toUpdate;
            DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
            // haal de info van doc players op
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            // haal de info van gamestateTurnID op
            Object stringID = Objects.requireNonNull(document.get("gamestateTurnID")).toString();
            // maak toUpdate een int die gelijk staat aan de turnID uit firebase
            toUpdate = Integer.parseInt(stringID.toString());
            // als de stringID gelijk is aan 4 dan wordt de value naar 1 gezet. anders wordt toUpdate + 1 gebruikt
            if (stringID.equals("4")) {
                docRef.update("gamestateTurnID", 1);
            } else {
                docRef.update("gamestateTurnID", toUpdate + 1);
            }
        }
    }

    public void nextTurn() throws ExecutionException, InterruptedException {
        final int maxPlayers = 4;
        if (gameModel.isGameOver()) {
            return;
        }

        if (gameModel.getTurnID() < maxPlayers) {
            gameModel.setTurnID(gameModel.getTurnID() + 1);
            nextTurnIDFirebase();
        } else if (gameModel.getTurnID() == maxPlayers) {
            gameModel.setTurnID(1);
            nextTurnIDFirebase();
        }

        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.update("State",
                FieldValue.arrayUnion("Speler: " + gameModel.getTurnID() + " is nu aan de beurt#" +State.TurnID+ dataBaseInt));
        dataBaseInt += 1;

    }

    public void attachStateListener() {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (view == null) {
                return;
            }
            if (documentSnapshot != null) {
                ArrayList<String> stateList = (ArrayList<String>) Objects.requireNonNull(documentSnapshot.getData()).get("State");
                String latestStateText = stateList.get(stateList.size()-1);
                String displayedText = latestStateText.substring(0, latestStateText.indexOf("#"));
                view.setStateText(displayedText);
            }
        });
    }

    public void attachWinListener() {
        final int amountToWin= 10;
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (view == null) {
                return;
            }
            if (documentSnapshot != null) {
                long winAmount = ((Number) Objects.requireNonNull(documentSnapshot.getData()).get("wins"+gameModel.getTurnID())).longValue();
                if (winAmount >= amountToWin) {
                    displayWinner();
                    try {
                        TimeUnit.SECONDS.sleep(4);
                        System.exit(0);
                    } catch (InterruptedException interruptedException) {
                        logger.log(Level.INFO, "gooit exption: ", interruptedException);
                    }
                }
            }
        });
    }
    public void displayWinner(){
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.update("State", FieldValue.arrayUnion("Speler " + gameModel.getTurnID() + " heeft het spel gewonnen!#"));
    }


    public void attackCountry(SpelbordView spelbordView, ActionEvent event) throws ExecutionException, InterruptedException{
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");

        Button buttonid = (Button) event.getSource();
        validCountryCheck(buttonid.getId());

        if (gameModel.getTurnID() == State.TurnID) {
            if (validCountryCheck(buttonid.getId())) {
                docRef.update("State",
                        FieldValue.arrayUnion(buttonid.getId()+" word aangevallen door speler: " + gameModel.getTurnID()+"#"+State.TurnID+ dataBaseInt));
                dataBaseInt += 1;
                System.out.println("komt hieerrrrrrr");
                selectCountry = 1;
            } else if (!validCountryCheck(buttonid.getId())) {
                spelbordView.cantAttack();
            }
        }else {
            spelbordView.notYourTurn();
        }
    }




    public boolean validCountryCheck(String country) throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            ArrayList<HashMap> arrayCountryData = (ArrayList<HashMap>) document.get("countries");

            assert arrayCountryData != null;
            for (HashMap armyAndCountryID : arrayCountryData) {

                if (armyAndCountryID.containsValue(country)) {
                    int firebasePlayerID = Integer.parseInt(armyAndCountryID.get("playerID").toString());
                    return firebasePlayerID != State.TurnID;

                }
            }
        }
        return false;
    }


    public void showCards() {
        logger.log(Level.INFO, "showCards aangeroepen");
    }

    public void showPlayers() {
        logger.log(Level.INFO, "showPlayers aangeroepen");
    }

    public void endTurn() throws ExecutionException, InterruptedException {
        selectCountry = 0;
        winInt = 0;
        nextTurn();
    }

    public void rollDiceAttack(SpelbordView spelbordView) throws ExecutionException, InterruptedException {
        if (selectCountry == 1) {

            ArrayList<Integer> worp1 = new DiceModel().roll(3); //nieuwe worp aanmaken
            ArrayList<Integer> worp2 = new DiceModel().roll(3);
            if (gameModel.getTurnID() == State.TurnID) {  //Kijkt of het jou turn is
                spelbordView.dobbelen();  // dan dobbelen
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    logger.log(Level.INFO, "gooit exption: ", e);
                }
                DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
                docRef.update("attackThrow", worp1);
                docRef.update("defendThrow", worp2);

                int attackThrow1 = worp1.get(0); //Is onnodig maar handig voor leesbaarheid
                int defendThrow1 = worp2.get(0);
                int attackThrow2 = worp1.get(1);
                int defendThrow2 = worp2.get(1);

                if (attackThrow1 > defendThrow1 && attackThrow2 > defendThrow2) {
                    docRef.update("State", FieldValue.arrayUnion
                            ("De aanvaller wint met een " + attackThrow1 + " en een " + attackThrow2 + "#" + State.TurnID + dataBaseInt));
                    try {
                        setWin();
                        TimeUnit.SECONDS.sleep(4);
                    } catch (InterruptedException e) {
                        logger.log(Level.INFO, "gooit exption: ", e);
                    }
                    spelbordView.attackAgain();
                    winInt = 1;

                } else if (defendThrow1 >= attackThrow1 && defendThrow2 >= attackThrow2) {

                    docRef.update("State", FieldValue.arrayUnion
                            ("De verdediger wint met een " + defendThrow1 + " en een " + defendThrow2 + "#" + State.TurnID + dataBaseInt));
                    TimeUnit.SECONDS.sleep(4);
                    spelbordView.turnEnds();
                    TimeUnit.SECONDS.sleep(2);
                    if (winInt == 1) {
                        lowerWin();
                    }
                    endTurn();

                } else {
                    docRef.update("State",
                            FieldValue.arrayUnion("Het is gelijkspel, niemand wint#" + State.TurnID + dataBaseInt));
                    TimeUnit.SECONDS.sleep(4);
                    spelbordView.turnEnds();
                    TimeUnit.SECONDS.sleep(2);
                    endTurn();
                }
                dataBaseInt += 1;
            } else {
                spelbordView.notYourTurn();
            }
        }else {
            spelbordView.noCountrySelected();
        }
    }



    public void setWin() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        long wins = ((Number) Objects.requireNonNull(document.get("wins" + gameModel.getTurnID()))).longValue();
        docRef.update("wins" + gameModel.getTurnID(),  wins + 1);

    }

    public void lowerWin() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        long wins = ((Number) Objects.requireNonNull(document.get("wins" + gameModel.getTurnID()))).longValue();
        docRef.update("wins" + gameModel.getTurnID(),  wins - 0.5);

    }



    @Override
    public void update(SpelbordObservable sb) {

    }

    @Override
    public void updateStatefield(DocumentSnapshot sb) {

    }

    @Override
    public void update(DocumentSnapshot documentSnapshot) {

    }

    public void setView(SpelbordView view) {
        this.view = view;
    }

}