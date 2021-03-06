package controllers;

import application.State;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import observers.LobbyObservable;
import observers.LobbyObserver;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LobbyController implements LobbyObserver, UpdatableController{

    PreLobbyController preLobbyController = PreLobbyController.getPreLobbyControllerInstance();
    private Scene scene;
    private Parent root;
    boolean isInGame = false;
    private final static Logger logger = Logger.getLogger(LobbyController.class.getName());




    static LoginController loginController = new LoginController();
    @FXML
    private Label username1;
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
                logger.log(Level.INFO, (Supplier<String>) executionException);
            }
        });
    }




    public void startGame(ActionEvent event) throws IOException, ExecutionException, InterruptedException {

        if (loginController.enoughPlayers()) {
            loginController.gameRunning();
            preLobbyController.sceneLoader("FXML/GameMap.fxml", event);
            SpelbordController.getSpelbordControllerInstance().setArmyAndCountryInFirebase();
        }

    }



    @FXML
    public void initialize(){
        GameController gameController = new GameController();
        gameController.listen("players", this);
        registerObserver(this);
        attachlistener();

        lobbyCode.setText(State.lobbycode);

    }


    @Override
    public void update(LobbyObservable lb) {

    }


    @Override
    public void updateDocument(DocumentSnapshot lb) {     //hier word de list voor elke speler geupdate
        ArrayList<HashMap<String, String>> arrayPlayerData = (ArrayList<HashMap<String, String>>) lb.get("players"); //zet alle data van 'players' in array wat hashmaps bevatten
        List<String> mijnUsernamesList = new ArrayList<>();

        assert arrayPlayerData != null;
        for (HashMap<String, String> playerData : arrayPlayerData) {
            mijnUsernamesList.add(playerData.get("username"));//loopt door de arrays van firestore zodat je ze apart kan zien van elke player
        }
        if (username1 == null) {
            return;
        }
        Platform.runLater(() -> {  //hier word de nieuwe view voor elke speler verzonden
            username1.setText(String.valueOf(mijnUsernamesList.get(0)));
           if (mijnUsernamesList.size() >= 2) {
                username2.setText(String.valueOf(mijnUsernamesList.get(1)));
            }
           if (mijnUsernamesList.size() >= 3) {
                username3.setText(String.valueOf(mijnUsernamesList.get(2)));
            }
           if (mijnUsernamesList.size() == 4) {
                username4.setText(String.valueOf(mijnUsernamesList.get(3)));
            } });
        }


public static void registerObserver(LobbyObserver sbv) {
    loginController.register(sbv);
}

    @Override
    public void update(DocumentSnapshot documentSnapshot) {

    }
}




