package configuration;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Database {

    private final static Logger logger = Logger.getLogger(Database.class.getName());
    private static final String PRIVATEKEYLOCATION = "src/main/resources/json/niewe-f7dc6-firebase-adminsdk-afalt-d50b0555bd.json";
    private static final String DATABASEURL = "niewe-f7dc6.iam.gserviceaccount.com";
    private Firestore db;

    private static Database INSTANCE;
    public static Database getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    private Database() {

        try {
            FileInputStream serviceAccount =
                    new FileInputStream(PRIVATEKEYLOCATION);


            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASEURL)
                    .build();

            FirebaseApp.initializeApp(options);
            this.db = FirestoreClient.getFirestore();

        } catch (IOException e) {
            logger.log(Level.INFO, "gooit exption: ", e);
        }
    }

    public Firestore getFirestoreDatabase() {
        return this.db;
    }


}