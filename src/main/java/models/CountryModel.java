package models;


public class CountryModel {

    private int playerID;
    private final String countryID;
    private int army;
//    private String countryName;

//    private Map<String, String> CountriesAndId = new HashMap<>();



//    public CountryModel(String countryID,String countryName){
//        this.countryID = countryID;
//        this.countryName = countryName;
//    }

    public CountryModel(String countryID){
        this.countryID = countryID;
        this.army = 2;
        this.playerID = 0;
    }


//    public CountryModel(int ID,String countryID, int army){
//        this.playerID = ID;
//        this.countryID = countryID;
//        this.army = army;
//    }



    public void setArmy(int army){
        this.army = army;
    }
    public String getCountryID(){
        return this.countryID;
    }

//    public String getCountryName(){
//        return this.countryName;
//    }
    public void setPlayerID(int playerID){
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

}
