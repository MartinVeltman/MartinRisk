package services;

import com.google.cloud.firestore.*;
import com.google.firebase.database.annotations.Nullable;
import configuration.Database;
import controllers.UpdatableController;

public class FirebaseService {

    private Firestore firestore;
    private static final String GAMES_PATH = "games";//kan miss weg
    private CollectionReference colRef;



    public FirebaseService() {
        Database db = Database.getInstance();
        this.firestore = db.getFirestoreDatabase();

        this.colRef = this.firestore.collection(GAMES_PATH);		// Een generieke referentie naar de games documents.
    }

    /**
     * Geeft een update naar de meegeleverde controller
     * op het moment dat er een wijziging in het firebase document plaatsvindt.
     * @param documentId
     */
    public void listen(String documentId, final UpdatableController controller) {

        DocumentReference docRef = this.colRef.document(documentId);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    controller.update(snapshot);

                    System.out.println("Current data: " + snapshot.getData());
                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }
}
