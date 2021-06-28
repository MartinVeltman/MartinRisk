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
import models.GameModel;
import models.SpelbordModel;
import observers.LobbyObservable;
import observers.LobbyObserver;
import observers.SpelbordObserver;
import services.FirebaseService;
//import views.LobbyView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LobbyController implements LobbyObserver, UpdatableController{

    private Stage stage;
    private Scene scene;
    private Parent root;
    boolean isInGame = false;
//    LobbyView lobbyView;
    private static LobbyController lobbyController;


    SpelbordController spelbordController;
    static GameModel gameModel;
    static LoginController loginController = new LoginController();
    @FXML
    private Label username1; // voor elke privtae maken
    @FXML
    Label username2;
    @FXML
    Label username3;
    @FXML
    Label username4;
    @FXML
    Label lobbyCode;
    private LobbyController LobbyController;


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

        if ((boolean) document.getData().get("gameIsRunning")) {
            return true;
        } else {
            return false;
        }

    }

    public void attachlistener() {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {

            try {

                if (checkGameIsRunning()) {
                    System.out.println("2");
                    if (checkIfInGame()) {
                        System.out.println("3");
                        System.out.println("De game is running");
                        root = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/GameMap.fxml"));
                        scene = new Scene(root);

                        Platform.runLater(() -> {
                            State.stage.setScene(scene);
                        });

                        SpelbordController.getGameModelInstance();
                        System.out.println("De scene is uitgevoerd");
                    }
                }

            } catch (ExecutionException | InterruptedException | IOException executionException) {
                executionException.printStackTrace();//TODO: met logger
            }

        });


    }


    public LobbyController() {
//
//        GameController gameController = new GameController();
//        gameController.listen(State.lobbycode, this);
//
//        LobbyController.registerObserver(this);
//        attachlistener();


        // SINGLETON patroon worden.

        // als deze class niet bestaat =>> maak een nieuwe
        // als deze al wel bestaat , geef het huidige actieve instantie op.



//        System.out.println("run method");
//        LobbyView lobbyView = new LobbyView();
//        lobbyView.getFirebaseUsernames("110720");


        /////
    }

    public void startGame(ActionEvent event) throws IOException, ExecutionException, InterruptedException {

        if (loginController.enoughPlayers()) {
            loginController.gameRunning();
            root = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/GameMap.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            SpelbordController.getSpelbordControllerInstance().setArmyAndCountryInFirebase();
        }

    }

    //gamestate wordt init op 1
//    GameModel gameState = new GameModel(1);
    // update gamestate naar firebase
    //Todo Gamestate moet firebase

    // let the game init here
//    SpelbordModel hostedGame = new SpelbordModel();
    //Todo populate the hostedgame with a ArrayList<PlayerModel> and ArrayList<CountryModel>s

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
        } else {
           //TODO: System.out.println("er is iets fout gegaan met de namen aan het spel toevoegen");
        }
    }


    public ArrayList<String> getFirebaseUsernames(String lobbyCode) throws ExecutionException, InterruptedException {
        //get benodigde stuff van firestore
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(lobbyCode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        ArrayList<String> mijnUsernamesList = new ArrayList<>();
        if (document.exists()) {
            ArrayList<HashMap> arrayPlayerData = (ArrayList<HashMap>) document.get("players"); //zet alle data van 'players' in array wat hashmaps bevatten

            for (HashMap playerData : arrayPlayerData) {
                System.out.println("playerdata player" + playerData);  //loopt door de arrays van firestore zodat je ze apart kan zien van elke player
                mijnUsernamesList.add((String) playerData.get("username"));
                System.out.println(mijnUsernamesList);

            }
        } else {
            System.out.println("niks");
        }

        return mijnUsernamesList;
    }



    @Override
    public void update(LobbyObservable lb) {

    }


    @Override
    public void updateDocument(DocumentSnapshot lb) {     //hier word de list voor elke speler geupdate
        System.out.println("ASDFASDFADSFADF");
        System.out.println(1);


        ArrayList<HashMap> arrayPlayerData = (ArrayList<HashMap>) lb.get("players"); //zet alle data van 'players' in array wat hashmaps bevatten

        List<String> mijnUsernamesList = new ArrayList<>();

        for (HashMap playerData : arrayPlayerData) {
            System.out.println("playerdata player" + playerData);  //loopt door de arrays van firestore zodat je ze apart kan zien van elke player
            mijnUsernamesList.add((String) playerData.get("username"));
            System.out.println(mijnUsernamesList);


        }

        System.out.println("LobbyController - update methode aangeroepen");
        if (username1 == null) {
            return;
        }
        // LobbyView lobbyView = new LobbyView();

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
            } else {
                System.out.println("er is iets fout gegaan met de namen aan het spel toevoegen");
            }});
        }

    //    //TODO: Initialize DRY maken
//
public static void registerObserver(LobbyObserver sbv) {
    loginController.register(sbv);
}

    @Override
    public void update(DocumentSnapshot documentSnapshot) {
        System.out.println(documentSnapshot.getData());
    }
}




