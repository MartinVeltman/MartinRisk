package controllers;

import application.State;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import models.PlayerModel;
import observers.LobbyObservable;
import observers.LobbyObserver;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements LobbyObservable {

    private final static Logger logger = Logger.getLogger(LoginController.class.getName());

    private static final List<LobbyObserver> observers = new ArrayList<>();

    public LoginController() {
    }

    public String createLobbyCode() {
        int min = 100000;
        int max = 999999;
        int lobbycode = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return Integer.toString(lobbycode);
    }

    public void createLobby(String username, String lobbycode) throws ExecutionException, InterruptedException {
        PlayerModel playerModel1 = new PlayerModel(username, 1, 0);
        State.TurnID = 1;
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(lobbycode).document("players");

        Map<String, Object> data = new HashMap<>();
        data.put("players", Collections.singletonList(playerModel1));
        data.put("gameIsRunning", false);
        data.put("gamestateTurnID", 1);
        data.put("State", Arrays.asList("Spel gestart", "Speler 1 is aan de beurt"));
        data.put("attackThrow", Collections.emptyList());
        data.put("defendThrow", Collections.emptyList());
        data.put("wins1",0);
        data.put("wins2",0);
        data.put("wins3",0);
        data.put("wins4",0);
        docRef.set(data);

    }

    public void checkCreate(String username) {
        try {
            if (username.equals("")) {
                logger.log(Level.INFO, "Usernamefield is leeg");
            } else {
                String lobbycode = createLobbyCode();
                createLobby(username, lobbycode);
                State.lobbycode = lobbycode;
                logger.log(Level.INFO, "Lobbycode is: " + State.lobbycode);
            }
        } catch (ExecutionException | InterruptedException e) {
                logger.log(Level.INFO, "gooit exption: ", e);
        }
    }


    public boolean readLobby(String lobbycode) throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            List<String> arrayValue = (List<String>) document.get("players");
            assert arrayValue != null;
            if (arrayValue.size() < 4) {
                return true;
            } else {
                logger.log(Level.INFO, "Lobby is al vol");
                return false;
            }
        } else {
                logger.log(Level.INFO, "Lobby bestaat niet");
                return false;
        }
    }

    public PlayerModel generateInstance(String username, String lobbycode) throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        List<String> arrayValue = (List<String>) document.get("players");
        assert arrayValue != null;
        PlayerModel playerModel2 = new PlayerModel(username, arrayValue.size() + 1, 1);
        playerModel2.setTurnID(arrayValue.size() + 1);
        State.TurnID = arrayValue.size() + 1;

        return playerModel2;
    }

    public void joinLobby(String lobbycode, String username) throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        future.get();
        State.lobbycode = lobbycode;
        PlayerModel playerModel2 = generateInstance(username, lobbycode);

        docRef.update("players", FieldValue.arrayUnion(playerModel2));
        notifyAllObservers();

    }

    public boolean checkJoin(String username, String code) {
        if (username.equals("")) {
            return false;
        } else {
            try {
                if (readLobby(code)) {
                    joinLobby(code, username);
                    return true;
                }
            } catch (InterruptedException e) {
                logger.log(Level.INFO, "gooit exption: ", e);
            } catch (ExecutionException e) {

                return false;
            }
        }
        return false;
    }

    public boolean emptyUsername(String textfield) {
        return textfield.equals("");
    }

    public boolean validateLobby(String code) {
        if (code.equals("")) {
            return false;
        } else {
            code = code.toLowerCase();
            char[] charArray = code.toCharArray();
            for (char ch : charArray) {
                if (ch >= 'a' && ch <= 'z') {
                    logger.log(Level.INFO, "Ingevulde lobbycode bevat letters");
                    return false;
                }
            }
            return true;
        }
    }

    public void gameRunning(){
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.update("gameIsRunning", true);
    }

    public boolean enoughPlayers() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        List<String> arrayValue = (List<String>) document.get("players");

        assert arrayValue != null;
        if (arrayValue.size() == 1) {
            return true;
        } else {
            logger.log(Level.INFO, "Er zijn teweing mensen in de lobby");
            return false;
        }
    }


    @Override
    public void register(LobbyObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notifyAllObservers() {
        for (LobbyObserver b : observers) {
            b.update(this);
        }
    }

}
