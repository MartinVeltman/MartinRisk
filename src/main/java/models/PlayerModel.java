package models;


public class PlayerModel{

    private String username;
    private int turnID;
    static PlayerModel playerModel;


    public static PlayerModel getPlayerModelInstance() {
        if (playerModel == null) {
            playerModel = new PlayerModel();
        }
        return playerModel;
    }


    public PlayerModel() {

    }

    public PlayerModel(String username, int turnID, int wins) {
        this.username = username;
        this.turnID = turnID;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public void setTurnID(int turnID) {
        this.turnID = turnID;
    }

}
