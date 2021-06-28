package observers;

import com.google.cloud.firestore.DocumentSnapshot;

public interface LobbyObserver {
    void update(LobbyObservable lb);
    void updateDocument(DocumentSnapshot lb); //alleen de updateDocument word gerund zou dus naar update kunnen worden veranderd
}
