package models;

import observers.GameObservable;
import observers.GameObserver;
import java.util.*;

public class GameModel implements GameObservable {

    private int turnID;
    private boolean gameOver;
    private final List<GameObserver> observers = new ArrayList<>();

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


