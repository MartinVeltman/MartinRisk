package observers;

import com.google.cloud.firestore.DocumentSnapshot;

public interface SpelbordObserver {
    void update(SpelbordObservable sb);
    void updateStatefield(DocumentSnapshot sb);
}
