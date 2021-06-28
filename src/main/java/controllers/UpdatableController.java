package controllers;

import com.google.cloud.firestore.DocumentSnapshot;

public interface UpdatableController {
    void update(DocumentSnapshot documentSnapshot);

}
