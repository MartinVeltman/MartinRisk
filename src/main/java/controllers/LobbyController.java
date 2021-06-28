package controllers;

import application.State;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import observers.LobbyObservable;
import observers.LobbyObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class LobbyController implements LobbyObserver, UpdatableController{

    private Scene scene;
    private Parent root;
    boolean isInGame = false;




    static LoginController loginController = new LoginController();
    @FXML
    private Label username1; //TODO: probeer de username imports meer dry te maken
    @FXML
    private Label username2;
    @FXML
    private Label username3;
    @FXML
    private Label username4;
    @FXML
    private Label lobbyCode;



    public boolean checkIfInGame() {
        if (!isInGame) {
            isInGame = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean checkGameIsRunning() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        return (boolean) Objects.requireNonNull(document.getData()).get("gameIsRunning");

    }

    public void attachlistener() {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {

            try {
                if (checkGameIsRunning()) {  //Om een of andere redenen als je if(checkGameIsRunning() && checkIfInGame()) werkt observer gamemap laden niet
                    if (checkIfInGame()) {
                        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("FXML/GameMap.fxml")));
                        scene = new Scene(root);

                        Platform.runLater(() -> State.stage.setScene(scene));

                        SpelbordController.getGameModelInstance();
                    }
                }

            } catch (ExecutionException | InterruptedException | IOException executionException) {
                executionException.printStackTrace();//TODO: met logger
            }

        });


    }


    public LobbyController() {

    }

    public void startGame(ActionEvent event) throws IOException, ExecutionException, InterruptedException {

        if (loginController.enoughPlayers()) {
            loginController.gameRunning();
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("FXML/GameMap.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            SpelbordController.getSpelbordControllerInstance().setArmyAndCountryInFirebase();
        }

    }


    @FXML
    public void initialize() throws ExecutionException, InterruptedException {

        GameController gameController = new GameController();
        gameController.listen("players", this);

        registerObserver(this);
        attachlistener();

//        lobbyView = new LobbyView();
        lobbyCode.setText(State.lobbycode);

        if (getFirebaseUsernames(State.lobbycode).size() == 1) {
            username1.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(0)));
        } else if (getFirebaseUsernames(State.lobbycode).size() == 2) {
            username1.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(0)));
            username2.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(1)));
        } else if (getFirebaseUsernames(State.lobbycode).size() == 3) {
            username1.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(0)));
            username2.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(1)));
            username3.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(2)));
        } else if (getFirebaseUsernames(State.lobbycode).size() == 4) {
            username1.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(0)));
            username2.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(1)));
            username3.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(2)));
            username4.setText(String.valueOf(getFirebaseUsernames(State.lobbycode).get(3)));
        }
    }


    public ArrayList<String> getFirebaseUsernames(String lobbyCode) throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(lobbyCode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        ArrayList<String> myUsernameList = new ArrayList<>();
        if (document.exists()) {
            ArrayList<HashMap> arrayPlayerData = (ArrayList<HashMap>) document.get("players"); //zet alle data van 'players' in array wat hashmaps bevatten

            assert arrayPlayerData != null;
            for (HashMap playerData : arrayPlayerData) {
                myUsernameList.add((String) playerData.get("username"));//loopt door de arrays van firestore zodat je ze apart kan zien van elke player
            }
        }

        return myUsernameList;
    }



    @Override
    public void update(LobbyObservable lb) {

    }


    @Override
    public void updateDocument(DocumentSnapshot lb) {     //hier word de list voor elke speler geupdate
        ArrayList<HashMap> arrayPlayerData = (ArrayList<HashMap>) lb.get("players"); //zet alle data van 'players' in array wat hashmaps bevatten
        List<String> mijnUsernamesList = new ArrayList<>();

        assert arrayPlayerData != null;
        for (HashMap playerData : arrayPlayerData) {
            mijnUsernamesList.add((String) playerData.get("username"));//loopt door de arrays van firestore zodat je ze apart kan zien van elke player
        }


        if (username1 == null) {
            return;
        }
        Platform.runLater(() -> {  //hier word de nieuwe view voor elke speler verzonden
            if (mijnUsernamesList.size() == 1) {
                username1.setText(String.valueOf(mijnUsernamesList.get(0)));
            } else if (mijnUsernamesList.size() == 2) {
                username1.setText(String.valueOf(mijnUsernamesList.get(0)));
                username2.setText(String.valueOf(mijnUsernamesList.get(1)));
            } else if (mijnUsernamesList.size() == 3) {
                username1.setText(String.valueOf(mijnUsernamesList.get(0)));
                username2.setText(String.valueOf(mijnUsernamesList.get(1)));
                username3.setText(String.valueOf(mijnUsernamesList.get(2)));
            } else if (mijnUsernamesList.size() == 4) {
                username1.setText(String.valueOf(mijnUsernamesList.get(0)));
                username2.setText(String.valueOf(mijnUsernamesList.get(1)));
                username3.setText(String.valueOf(mijnUsernamesList.get(2)));
                username4.setText(String.valueOf(mijnUsernamesList.get(3)));
            } });
        }

    //    //TODO: Initialize DRY maken
//
public static void registerObserver(LobbyObserver sbv) {
    loginController.register(sbv);
}

    @Override
    public void update(DocumentSnapshot documentSnapshot) {

    }
}




