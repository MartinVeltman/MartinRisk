//package views;
//
//import application.State;
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.DocumentSnapshot;
//import controllers.LobbyController;
//import controllers.LoginController;
//import javafx.fxml.FXML;
//import observers.LobbyObservable;
//import observers.LobbyObserver;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.concurrent.ExecutionException;
//
//public class LobbyView implements LobbyObserver {
//    LoginController loginController = new LoginController();
//    LobbyController lobbyController;
//
//    public LobbyView() {
//        lobbyController=lobbyController.getLobbyControllerInstance();// dit is niet goed -want deze maakt meerder oinsrtanties van deze controller. ///lobbycontrollerl.getinstance
//        lobbyController.registerObserver(this);
//    }
//
//
//    @Override
//    public void updateDocument(DocumentSnapshot lb) {
//        //
//    }
//
//    @FXML
//    public void displayLobbyCode() {
//    }
//
//
//    //TODO is nog niet af
//    public ArrayList<String> getFirebaseUsernames(String lobbyCode) throws ExecutionException, InterruptedException {
//        //get benodigde stuff van firestore
//        DocumentReference docRef = State.database.getFirestoreDatabase().collection(lobbyCode).document("players");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        DocumentSnapshot document = future.get();
//
//        ArrayList<String> mijnUsernamesList = new ArrayList<>();
//        if (document.exists()) {
//
//            ArrayList<HashMap> arrayPlayerData = (ArrayList<HashMap>) document.get("players"); //zet alle data van 'players' in array wat hashmaps bevatten
//
//            for (HashMap playerData : arrayPlayerData) {
//                System.out.println("playerdata player" + playerData);  //loopt door de arrays van firestore zodat je ze apart kan zien van elke player
//                mijnUsernamesList.add((String) playerData.get("username"));
//                System.out.println(mijnUsernamesList);
//
//
//            }
//        } else {
//            System.out.println("niks");
//        }
//
//
//        return mijnUsernamesList;
//    }
//
//    @Override
//    public void update(LobbyObservable lb) {
//
//        /// iets qua code dat het lijstje met namen wordt bijgewerkt
//
//
//        // HAAL SPELERS OP
//        System.out.println("SPELERS START");
//        try {
//            getFirebaseUsernames(State.lobbycode);
//            System.out.println("try catch proberen als t lukt");
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("SPELERS END");
//
//    }
//}
//
//
////   Stage lobbyStage;
////
////
////    public LobbyView(Stage lobbyStage){
////        this.lobbyStage = lobbyStage;
////
////    }
////
////   public void loadLobbyStage() throws IOException {
////
////       Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/Lobby.fxml"));
////       lobbyStage.setScene(new Scene(root,1280, 720));
////       lobbyStage.setResizable(false);
////       lobbyStage.show();
////   }
//
