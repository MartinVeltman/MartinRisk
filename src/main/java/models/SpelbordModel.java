package models;

import observers.SpelbordObservable;
import observers.SpelbordObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SpelbordModel implements SpelbordObservable {

    public Logger logger;
    private ArrayList<CountryModel> countries;
    private final List<SpelbordObserver> observers = new ArrayList<>();
    static SpelbordModel spelbordModel;

    public static SpelbordModel getSpelbordModelInstance(){
        if (spelbordModel == null) {
            spelbordModel = new SpelbordModel();
        }
        return spelbordModel;
    }

    public SpelbordModel(){

    }

    public void CountriesAndIdMap() {
        ArrayList<CountryModel> countriesAndID = new ArrayList<>();
        int count = 1;
        try {
            File myObj = new File("src/main/resources/text/countries.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String countryCode = data.split(":")[1];

                CountryModel newCountry = new CountryModel(countryCode);
                if (count == 5) {
                    count = 1;
                }
                newCountry.setPlayerID(count);
                countriesAndID.add(newCountry);
                count++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            logger.log(Level.INFO, "gooit exception: ", e);
        }
        Collections.shuffle(countriesAndID);
        this.countries = countriesAndID;

    }



    public ArrayList<CountryModel> getCountries(){
        return this.countries;
    }

    @Override
    public void register(SpelbordObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notifyAllObservers() {
        for (SpelbordObserver s : observers) {
            s.update(this);
        }
    }


}



