package configuration;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Database {

    private static final String PRIVATEKEYLOCATION = "C:\\Users\\Martin Veltman\\Downloads\\niewe-f7dc6-firebase-adminsdk-afalt-d50b0555bd.json";
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
            e.printStackTrace();
        }
    }

    public Firestore getFirestoreDatabase() {
        return this.db;
    }


}