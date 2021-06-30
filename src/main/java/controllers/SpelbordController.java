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


    static GameModel gameModel;
    static SpelbordModel spelbordModel;
    private SpelbordModel map;
    private boolean canEnd;
    private int turnID;
    private boolean gameOver;
    static SpelbordController spelbordController;
    public boolean canAttack = false;
//    SpelbordViewController spelbordViewController;
//
//    public SpelbordViewController getSpelbordViewController() {
//        return spelbordViewController;
//    }
    // SpelbordViewController spelbordViewController = SpelbordViewController.getSpelbordViewControllerInstance();
    //gameModel = loginController.getGameModelInstance();
//    LoginController loginController = new LoginController();

    ArrayList<Integer> worp1 = new DiceModel().roll(3);
    ArrayList<Integer> worp2 = new DiceModel().roll(3);
    private SpelbordView view;


    public static SpelbordController getSpelbordControllerInstance() {
        if (spelbordController == null) {
            spelbordController = new SpelbordController();
            System.out.println("nieuwe instantie van SpelbordController is aangemaakt");
        }
        return spelbordController;
    }



    public static GameModel getGameModelInstance() {
        if (gameModel == null) {
            gameModel = new GameModel(1);
            System.out.println("nieuwe instantie van GameModel is aangemaakt");
        }
        return gameModel;
    }

    EventHandler<MouseEvent> eventHandler = e -> System.out.println("ER is geklikt");

    public void attachlistener() {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                int firebaseTurnID = Integer.valueOf(documentSnapshot.getData().get("gamestateTurnID").toString());
                gameModel.setTurnID(firebaseTurnID);
                try {
                    startMainLoop();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
    }

    public void startMainLoop() throws ExecutionException, InterruptedException {

        if (gameModel.getTurnID() == State.TurnID) {
            System.out.println("Jij bent aan de beurt " + gameModel.getTurnID());
            State.stage.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
            canEnd = true;
            //TODO hier komt de zetten en aanvallen van de game. Als laatst nextTurn()


            //ToDo zorg ervoor dat hier een mouse event listeren

            //functie viewer.garrison(current playerID)


        } else {
            System.out.println("Je bent niet aan de beurt, TurnID " + gameModel.getTurnID() + " is aan de beurt");
            canEnd = false;
        }
        System.out.println("main loop gestart");
        this.getArmyAndCountryFromFirebase();


    }

    public SpelbordController() {
        gameModel = getGameModelInstance();
        spelbordModel = spelbordModel.getSpelbordModelInstance();
        attachlistener();
        attachStateListener();
    }

    public void setArmyAndCountryInFirebase() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.get("countries") != null) {
            System.out.println("this shit is already made");
        } else {
            spelbordModel.CountriesAndIdMap();
            Map<String, Object> data = new HashMap<>();
            data.put("countries", spelbordModel.getCountries());

            //    CountryModel countryModel1 = new CountryModel("NA1");

            ApiFuture<WriteResult> result = docRef.update(data);

            System.out.println("iets gedaan met countries naar database");
        }
    }

    public void getArmyAndCountryFromFirebase() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {

            ArrayList<HashMap> arrayCountryData = (ArrayList<HashMap>) document.get("countries");
            for (HashMap armyAndCountryID : arrayCountryData) {
                //   ApiFuture<WriteResult> result = docRef.update("countries", armyAndCountryID);
                //   System.out.println("pernoot:" +result.get());

            }
        } else {
            System.out.println("No document found! aids");
        }


    }

    //if the player turnID matches the gamestate turnID. then he can start his turn
    public void getPlayersFirebaseTurnID() throws ExecutionException, InterruptedException {

        //get benodigde stuff van firestore
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        //if lobbycode/collection van players bestaat ->
        if (document.exists()) {

            ArrayList<HashMap> arrayPlayerData = (ArrayList<HashMap>) document.get("players"); //zet alle data van 'players' in array wat hashmaps bevatten

            for (HashMap playerData : arrayPlayerData) {
                System.out.println(playerData);  //loopt door de arrays van firestore zodat je ze apart kan zien van elke player

                Map.Entry<String, Long> entry = (Map.Entry<String, Long>) playerData.entrySet().iterator().next(); //elke
                String turnIdKey = entry.getKey(); //pakt de key van elke 1e Key-Value combo
                Long turnIdValue = entry.getValue(); //pakt de bijbehorende value van die 1e key
                System.out.println(turnIdKey + " = " + turnIdValue); //print beide key en value
            }
        } else {
            System.out.println("No document found!");

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
            Object stringID = document.get("gamestateTurnID").toString();

            // maak toUpdate een int die gelijk staat aan de turnID uit firebase
            toUpdate = Integer.parseInt(stringID.toString());

            // als de stringID gelijk is aan 4 dan wordt de value naar 1 gezet. anders wordt toUpdate + 1 gebruikt
            if (stringID.equals("4")) {
                ApiFuture<WriteResult> GamestateID = docRef.update("gamestateTurnID", 1);
            } else {
                ApiFuture<WriteResult> GamestateID = docRef.update("gamestateTurnID", toUpdate + 1);
            }
        }
    }


//    TODO zorg ervoor dat de lokale playerID wordt aangesproken hier als playerLocalID, maybe met final String?

    public long comparePlayerIDtoTurnIDFirebase(String playerLocalID) throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (playerLocalID.equals(document.get("gamestateTurnID").toString())) {
            System.out.println("this is your turn!");
        } else {
            System.out.println("nah fam, not your turn");
        }

        return (long) document.getData().get("gamestateTurnID");
    }

    //TODO matchen met code hierboven
    public void nextTurn() throws ExecutionException, InterruptedException {
        if (gameModel.isGameOver() == true) {
            //end game. this should be called by an observer?
        } else if (gameModel.getTurnID() < 4) {
            gameModel.setTurnID(gameModel.getTurnID() + 1);
            nextTurnIDFirebase();
        } else if (gameModel.getTurnID() == 4) {
            gameModel.setTurnID(1);
            nextTurnIDFirebase();
        }
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<WriteResult> future = docRef.update("State",
                FieldValue.arrayUnion("Speler: " + gameModel.getTurnID() + " is nu aan de beurt"));

    }


    //Todo zorg ervoor dat via de map de 2 countryID's worden meegegeven
    public void attackPlayer(String countryCodeAttacker, String countryCodeDefender) {
    }


    public void attachStateListener() {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (view == null) {

                return;
            }
            if (documentSnapshot != null) {
                ArrayList<String> stateList = (ArrayList<String>) documentSnapshot.getData().get("State");
                String latestStateText = stateList.get(stateList.size()-1);
                view.setStateText(latestStateText);



//                System.out.println(State.TurnID);
//                int firebaseTurnID = Integer.valueOf(documentSnapshot.getData().get("gamestateTurnID").toString());
//                System.out.println("hij is niet gezet");
//                gameModel.setTurnID(firebaseTurnID);
//                System.out.println("hij is gezet");
//                try {
//                    startMainLoop();
//                } catch (ExecutionException executionException) {
//                    executionException.printStackTrace();
//                } catch (InterruptedException interruptedException) {
//                    interruptedException.printStackTrace();
//                }
            }
        });
    }


    public void getButtonID(SpelbordView spelbordView, ActionEvent event) throws ExecutionException, InterruptedException{
        Button buttonid = (Button) event.getSource();
        validCountryCheck(buttonid.getId());
        if (gameModel.getTurnID() == State.TurnID) {
            if (validCountryCheck(buttonid.getId())) {
                DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
                ApiFuture<WriteResult> future = docRef.update("State",
                        FieldValue.arrayUnion(buttonid.getId()+" word aangevallen door speler: " + gameModel.getTurnID())); //stuurt de gameState naar firebase voor observers
                WriteResult result = future.get();
                spelbordView.displayAttack(buttonid.getId());


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

            for (HashMap armyAndCountryID : arrayCountryData) {

                if (armyAndCountryID.containsValue(country)) {
                    int firebasePlayerID = Integer.valueOf(armyAndCountryID.get("playerID").toString());
                    if (firebasePlayerID == State.TurnID) {
                        return false;
                    } else {
                        return true;
                    }

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
        if (gameModel.getTurnID() == State.TurnID) {
            spelbordView.dobbelen();
            try {

                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
            ApiFuture<WriteResult> future8 = docRef.update("attackThrow" , worp1);
            ApiFuture<WriteResult> future2 = docRef.update("defendThrow", worp2);


            int attackThrow1 = worp1.get(0);
            int defendThrow1 = worp2.get(0);
            int attackThrow2 = worp1.get(1);
            int defendThrow2 = worp2.get(1);

            if (attackThrow1 > defendThrow1 && attackThrow2 > defendThrow2) {
                //hier iets van spelers.get(1).setSoldaten(soldaten-2)
                spelbordView.attackerWins(attackThrow1, attackThrow2);
                 ApiFuture<WriteResult> future3 = docRef.update("State",
                    FieldValue.arrayUnion("De aanvaller wint met een " + attackThrow1 + " en een " + attackThrow2));


            } else if (defendThrow1 >= attackThrow1 && defendThrow2 >= attackThrow2) {
                spelbordView.defenderWins(defendThrow1, defendThrow2);
                ApiFuture<WriteResult> future4 = docRef.update("State",
                    FieldValue.arrayUnion("De verdediger wint met een " + defendThrow1 + " en een " + defendThrow2));

            } else {
                spelbordView.draw();
                ApiFuture<WriteResult> future4 = docRef.update("State",
                     FieldValue.arrayUnion("Het is gelijkspel, niemand wint"));

            }
        }else {
            spelbordView.notYourTurn();
        }
        try {
            setWin();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    public void setWin() throws ExecutionException, InterruptedException {
        DocumentReference docRef = State.database.getFirestoreDatabase().collection(State.lobbycode).document("players");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        Long wins = ((Number) document.get("wins" + gameModel.getTurnID())).longValue();
        ApiFuture<WriteResult> win = docRef.update("wins" + gameModel.getTurnID(),  wins + 1);

    }

    public void registerObserver(SpelbordObserver sbv) {
        spelbordModel.register(sbv);
    }

    @Override
    public void update(SpelbordObservable sb) {

    }

    @Override
    public void updateStatefield(DocumentSnapshot sb) {

        System.out.println("update statefield observerss");
        ArrayList<HashMap> arrayStateData = (ArrayList<HashMap>) sb.get("State");


    }

    @Override
    public void update(DocumentSnapshot documentSnapshot) {

    }

    public void setView(SpelbordView view) {
        this.view = view;
    }


//    //TODO NIET AAN DEZE 4 METHODS KOMEN
//
//    public void setArmyFirebase() throws ExecutionException, InterruptedException {
//        DocumentReference docRef = State.database.getFirestoreDatabase().collection("791967").document("players");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        DocumentSnapshot document = future.get();
//
//        if (document.exists()) {
//            ArrayList<HashMap> arrayCountryData = (ArrayList<HashMap>) document.get("countries");
//            System.out.println("dit is arraycountrydata: " + arrayCountryData);
//
//            arrayCountryData.
//
//
//
//
//                    HashMap newData = new HashMap();
//            newData.put("army", 4);
//
//            ArrayList<HashMap> testArray = new ArrayList<>();
//            testArray.add(newData);
////            long newValue = (long) oldValue.put("army", 4);
//
//
//            System.out.println("test new value: " + newValue);
//            docRef.update("countries", newValue);
//        }
//        else {
//            System.out.println("No document found!");
//        }
//    }
//
//    public void setArmyFirebase(int arrayNumber, int newArmies) throws ExecutionException, InterruptedException {
//        DocumentReference docRef = State.database.getFirestoreDatabase().collection("791967").document("players");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        DocumentSnapshot document = future.get();
//
//        if (document.exists()) {
//            ArrayList<HashMap> arrayCountryData = (ArrayList<HashMap>) document.get("countries");
//            System.out.println("dit is arraycountrydata: " + arrayCountryData);
//
////            ArrayList<HashMap> data = new ArrayList<HashMap>();
////            data.add(0, ;
//
//            Map<String, Integer> dataMap = new HashMap<>();
//            dataMap.put("army", newArmies);
////
////            ArrayList<String> countries = new ArrayList<>();
////            countries.add(arrayNumber, "test");
//
//
//            docRef.update("countries", dataMap);
//        }
//        else {
//            System.out.println("No document found!");
//        }
//    }
//
//    public void getArmyFirebase(int arrayNumber) throws ExecutionException, InterruptedException {
//        DocumentReference docRef = State.database.getFirestoreDatabase().collection("791967").document("players");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        DocumentSnapshot document = future.get();
//        if (document.exists()) {
//
//            ArrayList<HashMap> arrayCountryData = (ArrayList<HashMap>) document.get("countries");
//            System.out.println("dit is arraycountrydata:    "+arrayCountryData);
//            System.out.println(arrayCountryData.get(arrayNumber).get("army"));
//
//        } else {
//            System.out.println("No document found!");
//        }
//    }
//
//    public void setPlayerIDtoCountry() throws ExecutionException, InterruptedException {
//        DocumentReference docRef = State.database.getFirestoreDatabase().collection("791967").document("players");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        DocumentSnapshot document = future.get();
//        if (document.exists()) {
//
//            ArrayList<HashMap> arrayCountryData = (ArrayList<HashMap>) document.get("countries");
//            System.out.println("dit is arraycountrydata:    "+arrayCountryData);
//            for (HashMap armyAndCountryID : arrayCountryData) {
//                System.out.println(armyAndCountryID);
//
//            }
//        } else {
//            System.out.println("No document found!");
//        }
//    }
//
//    public void getPlayerIDtoCountry() throws ExecutionException, InterruptedException {
//        DocumentReference docRef = State.database.getFirestoreDatabase().collection("791967").document("players");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        DocumentSnapshot document = future.get();
//        if (document.exists()) {
//
//            ArrayList<HashMap> arrayCountryData = (ArrayList<HashMap>) document.get("countries");
//            System.out.println("dit is arraycountrydata:    "+arrayCountryData);
//            for (HashMap armyAndCountryID : arrayCountryData) {
//                System.out.println(armyAndCountryID);
//
//            }
//        } else {
//            System.out.println("No document found!");
//        }
//    }


//    private List<PlayerModel> spelers = new ArrayList<>();
//
////
////    public void onClick() {
////
////        ArrayList<Integer> worp1 = new DiceController().roll(3);
////        ArrayList<Integer> worp2 = new DiceController().roll(3);
////
////        PlayerModel speler1 = new PlayerModel("Petra", worp1);
////        PlayerModel speler2 = new PlayerModel("Erik", worp2);
////
////
////        spelers.add(speler1);
////        spelers.add(speler2);
////
////    }
////    public void aanval() {
//////
//////        System.out.println("worp speler 1: "+ spelers.get(0).getLastThrow());
//////        System.out.println("worp speler 2: "+ spelers.get(1).getLastThrow());
//////
////        // (1) spelers gooien dobbelstenen
//////        int attackThrow1 = spelers.get(0).getLastThrow().get(0);
//////        int defendThrow1 = spelers.get(1).getLastThrow().get(0);
//////        int attackThrow2= spelers.get(0).getLastThrow().get(1);
//////        int defendThrow2 = spelers.get(1).getLastThrow().get(1);
////
////
////        // (2) er wordt een winnaar bepaald
////        PlayerModel winnaar = null;
////        if (attackThrow1 > defendThrow1 && attackThrow2 > defendThrow2){
////            System.out.println("speler 1 wint");//hier iets van spelers.get(1).setSoldaten(soldaten-2)
////            winnaar = spelers.get(0);
////        } else if(defendThrow1 >= attackThrow1 && defendThrow2 >= attackThrow2 ) {
////            System.out.println("2 wint");
////            winnaar = spelers.get(1);
////
////        }else {
////            System.out.println("gelijkspel allebij een pion weg");
////            System.out.println("STOP");
////            return;
////        }
//
//        // (3) er wordt een willekeurige kaart gekozen
//
//        //Integer willekeurigeKaart = new Random().nextInt(3)+1;
////        Integer willekeurigeKaart = new Integer(1);
//
//
////        // (4) de winnaar krijgt de willekerugei kaart
////        winnaar.getCards().add(willekeurigeKaart);
////        winnaar.getCards().add(willekeurigeKaart);
////        winnaar.getCards().add(willekeurigeKaart);
////
////        winnaar.getCards().add(2);
////
////        System.out.println(winnaar.getCards());
//
//
//
//        // (?) voeg aan een speler een aantal soldaten toe
//        //             (?.1) bepalen hoeveel soldaten
//        //             (?.2) bepalen welke soldaten
//        //              (?.3) bepalen welke speler
//        //          x   (?.4) toevoegen aan speler's soldaten
//        //                 x      (?.4.1) lijst van soldaten van de speler ophalen
//        //                 x      (?.4.2) soldaten uit stap 2 toevoegen aan de lijst
//
////
//        // soldaten toevoegen
//
//   }
//    public void startGame(){
//        spelers.get(0).setAantalLegers(20);
//        spelers.get(0).setColor(Color.blue);
//
//    }
//public void removeCard (int cardNumber){
//    spelers.get(0).getCards().remove(new Integer(cardNumber));
//    spelers.get(0).getCards().remove(new Integer(cardNumber));
//    spelers.get(0).getCards().remove(new Integer(cardNumber));
//}
//
//    public void handInCard(){
//        int Paarden= Collections.frequency(spelers.get(0).getCards(), 2);
//        int Kannonen= Collections.frequency(spelers.get(0).getCards(), 1);
//        int Ridders= Collections.frequency(spelers.get(0).getCards(), 3);
//        if(Kannonen >= 3){ ///moet nog kaarten verwijderen
//            System.out.println("Kaarten ingeleverd:Kanon");
//            spelers.get(0).setAantalLegers(spelers.get(0).getAantalLegers()+8);
//            removeCard(1);
//        }
//        else if(Paarden>= 3){
//            System.out.println("Kaarten ingeleverd(Paard");
//            spelers.get(0).setAantalLegers(spelers.get(0).getAantalLegers()+10);
//            removeCard(2);
//
//
//        }else if (Ridders >= 3){
//            System.out.println("Kaarten ingeleverd");
//            spelers.get(0).setAantalLegers(spelers.get(0).getAantalLegers()+14);
//            removeCard(3);
//
//
//        }else{
//            System.out.println("Je hebt geen geldige combinatie van kaarten");
//        }
//    }
//
//    public void showPlayers() {
//    } //method voor de buttons in de UI/Interface - show players is een simpele popup met de namen van de players en kleur
//
//    public void rollDice() {
//    } //method voor de buttons in de UI/Interface - roll dice daar moet je met martin ff overleggen
//
//    public void showCards() {
//    } //method voor de buttons in de UI/Interface - show cards wordt een kleine nieuwe interface waar chiel en ryan aan gaan werken
//


    //    IK WEET BTW NIET OF DIT IN DE CONTROLLER MOET OF IN DE MODEL!!!



    //maak aantal spelers gelijk aan hoeveel mensen in lobby, dus 4 nieuwe spelers.
    // de usernames kan je met .getUserName fzo pakken, kijk ff in de rest van de classes van jansen.

    // de code kiest een random kleur en assigned die tot de speler OFFFFFF we kunnen 4 standaard kleuren kiezen bijvoorbeeld, rood blauw groen oranje.


    //als het spel is gestart heeft elke player een kleur en speler attributes, zoals regions cards etc...
    //ik maak nog een knopje met END TURN, dan als je erop klikt dat de beurt dan overgaat naar de volgende,


    // deze knop staat in de map Images en die mag alleen visible worden als degene heeft aangevallen/reinforced. maar dat komt later wel


    //if player has all regions, set hasWon = true, stop de turn loop >>> go to results screen


    //als een speler die 3 cards heeft dan schakelt de canExchageCards naar = true en dan verschijnt er een knopje die we nog moeten maken en die hele interface nog met, 'Trade in' bijvoorbeeld
    //dan verschijnt er een scherm met het aantal troepen dat diegene krijgt en dan klikt ie op "OK" en dan ontvangt hij de troepen en gaan die kaarten weg.

//    //TODO NIET AANKOMEN IS MINE ^^^^^^^^


    //    IK WEET BTW NIET OF DIT IN DE CONTROLLER MOET OF IN DE MODEL!!!


    //maak aantal spelers gelijk aan hoeveel mensen in lobby, dus 4 nieuwe spelers.
    // de usernames kan je met .getUserName fzo pakken, kijk ff in de rest van de classes van jansen.

    // de code kiest een random kleur en assigned die tot de speler OFFFFFF we kunnen 4 standaard kleuren kiezen bijvoorbeeld, rood blauw groen oranje.


    //als het spel is gestart heeft elke player een kleur en speler attributes, zoals regions cards etc...
    //ik maak nog een knopje met END TURN, dan als je erop klikt dat de beurt dan overgaat naar de volgende,


    // deze knop staat in de map Images en die mag alleen visible worden als degene heeft aangevallen/reinforced. maar dat komt later wel


    //if player has all regions, set hasWon = true, stop de turn loop >>> go to results screen


    //als een speler die 3 cards heeft dan schakelt de canExchageCards naar = true en dan verschijnt er een knopje die we nog moeten maken en die hele interface nog met, 'Trade in' bijvoorbeeld
    //dan verschijnt er een scherm met het aantal troepen dat diegene krijgt en dan klikt ie op "OK" en dan ontvangt hij de troepen en gaan die kaarten weg.


}