package controllers;

import application.State;
import com.google.cloud.firestore.*;
import configuration.Database;
import observers.LobbyObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController {
    private final CollectionReference colRef;
    private final static Logger logger = Logger.getLogger(GameController.class.getName());


    public GameController() {
        Database db = Database.getInstance();
        Firestore firestore = db.getFirestoreDatabase();
        this.colRef = firestore.collection(State.lobbycode);

    }

    public void listen(String documentId, LobbyObserver observer) {            //luisterd of er een wijziging is binnen het firebasedocument als dat zo is geeft ie aan
        DocumentReference docRef = this.colRef.document(documentId);           //de observer een nieuw documentsnapshot mee
        docRef.addSnapshotListener((DocumentSnapshot snapshot, FirestoreException e) -> {
                if (e != null) {
                  logger.log(Level.INFO, "Gooit firestoreExption info", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    observer.updateDocument(snapshot);
                }
            });
    }


}
