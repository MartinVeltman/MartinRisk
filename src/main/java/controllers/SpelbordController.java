package controllers;

import application.State;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.WriteResult;
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

public class SpelbordController implements SpelbordObserver, UpdatableController {

    private int dataBaseInt = 0;
    static GameModel gameModel;
    static SpelbordModel spelbordModel;
    private boolean canEnd;
    static SpelbordController spelbordController;
    private SpelbordView view;


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
                    executionException.printStackTrace();
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
            System.out.println("this shit is already made");//TODO; log dit
        } else {
            spelbordModel.CountriesAndIdMap();
            Map<String, Object> data = new HashMap<>();
            data.put("countries", spelbordModel.getCountries());

            docRef.update(data);
            ApiFuture<WriteResult> result = docRef.update(data);

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
        if (gameModel.isGameOver()) {
            return;
        }

        if (gameModel.getTurnID() < 4) {
            gameModel.setTurnID(gameModel.getTurnID() + 1);
            nextTurnIDFirebase();
        } else if (gameModel.getTurnID() == 4) {
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
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (view == null) {
                return;
            }
            if (documentSnapshot != null) {
                long winAmount = ((Number) Objects.requireNonNull(documentSnapshot.getData()).get("wins"+gameModel.getTurnID())).longValue();
                if (winAmount == 10) {
                    displayWinaar();
                }
            }
        });
    }
    public void displayWinaar(){
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.update("State", FieldValue.arrayUnion("Speler " + gameModel.getTurnID() + " heeft het spel gewonnen!#"));
    }


    public void getButtonID(SpelbordView spelbordView, ActionEvent event) throws ExecutionException, InterruptedException{
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");

        Button buttonid = (Button) event.getSource();
        validCountryCheck(buttonid.getId());

        if (gameModel.getTurnID() == State.TurnID) {
            if (validCountryCheck(buttonid.getId())) {
                docRef.update("State",
                        FieldValue.arrayUnion(buttonid.getId()+" word aangevallen door speler: " + gameModel.getTurnID()+"#"+State.TurnID+ dataBaseInt));
                dataBaseInt += 1;
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

    public void handleClicky() {
        System.out.println("CLICKYYY MOFO");
    }

    public void showCards() {
        System.out.println("showcard");
    }

    public void showPlayers() {
        System.out.println("showplayer");
    }
    public void endTurn() throws ExecutionException, InterruptedException {
        nextTurn();
    }

    public void rollDice(SpelbordView spelbordView) throws ExecutionException{
        ArrayList<Integer> worp1 = new DiceModel().roll(3);
        ArrayList<Integer> worp2 = new DiceModel().roll(3);
        if (gameModel.getTurnID() == State.TurnID) {
            spelbordView.dobbelen();
            try {

                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
            docRef.update("attackThrow" , worp1);
            docRef.update("defendThrow", worp2);

            int attackThrow1 = worp1.get(0);
            int defendThrow1 = worp2.get(0);
            int attackThrow2 = worp1.get(1);
            int defendThrow2 = worp2.get(1);

            if (attackThrow1 > defendThrow1 && attackThrow2 > defendThrow2) {
                docRef.update("State", FieldValue.arrayUnion
                        ("De aanvaller wint met een " + attackThrow1 + " en een " + attackThrow2 + "#" + State.TurnID + dataBaseInt));
                try {
                    setWin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (defendThrow1 >= attackThrow1 && defendThrow2 >= attackThrow2) {
                docRef.update("State", FieldValue.arrayUnion
                        ("De verdediger wint met een " + defendThrow1 + " en een " + defendThrow2 + "#" + State.TurnID + dataBaseInt));
            } else {
                docRef.update("State",
                        FieldValue.arrayUnion("Het is gelijkspel, niemand wint#" +State.TurnID+ dataBaseInt));
            }
            dataBaseInt += 1;
        } else {
            spelbordView.notYourTurn();
        }
    }

    public void setWin() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        long wins = ((Number) Objects.requireNonNull(document.get("wins" + gameModel.getTurnID()))).longValue();
        docRef.update("wins" + gameModel.getTurnID(),  wins + 1);

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