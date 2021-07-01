package models;

public class CountryModel {

    private int playerID;
    private final String countryID;

    public CountryModel(String countryID){
        this.countryID = countryID;
        this.playerID = 0;
    }


    public String getCountryID(){
        return this.countryID;
    }

    public void setPlayerID(int playerID){
        this.playerID = playerID;
    }

    public int getPlayerID() {  //zegt dat deze nooit gebruikt word maar als je hem weghaald werkt het spel niet
        return playerID;
    }

}