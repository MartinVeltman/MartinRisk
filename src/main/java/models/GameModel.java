package models;

import observers.GameObservable;
import observers.GameObserver;
import java.util.*;

public class GameModel implements GameObservable {

    // spelbord model moet niet map zijn maar bijv private SpelbordModel map = firebase.get(currentMap)
    private SpelbordModel map;
    private int turnID;
    private boolean gameOver;
    private PlayerModel players;
    private final List<GameObserver> observers = new ArrayList<>();


//    public GameModel getGameModelInstance() {
//        if (gameModel == null) {
//            gameModel = new GameModel(1);
//            System.out.println("nieuwe instantie van GameModel is aangemaakt");
//        }
//        return gameModel;
//    }



    public GameModel(int TurnID) {
        this.turnID = 1;
        this.gameOver = false;
    }

    public GameModel() {

    }

    public int getTurnID() {
        return this.turnID;
    }

    public void setTurnID(int turnID) {
        this.turnID = turnID;
    }



    public boolean isGameOver() {
        return gameOver;
    }




    @Override
    public void register(GameObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notifyAllObservers() {
        for (GameObserver g : observers) {
            g.update(this);
        }
    }
}


