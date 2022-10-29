package org.techtown.face.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.adapters.MeetAdapter;
import org.techtown.face.models.MeetItem;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class AddActivity extends AppCompatActivity {
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(AddActivity.this);
        setContentView(R.layout.activity_add);
        RecyclerView addRecyclerView = findViewById(R.id.addRecyclerView);
        FloatingActionButton addButton = findViewById(R.id.addFloatingActionButton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddActivity.this,
                LinearLayoutManager.VERTICAL, false);
        addRecyclerView.setLayoutManager(layoutManager);
        MeetAdapter meetAdapter = new MeetAdapter();

        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        String myName = preferenceManager.getString(Constants.KEY_NAME);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(myId)
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            String userId = queryDocumentSnapshot.getString(Constants.KEY_NOTIFICATION);
                            String docId = queryDocumentSnapshot.getId();
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection(Constants.KEY_COLLECTION_USERS).document(userId).get().addOnCompleteListener(task1 -> {
                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                    if (task1.isSuccessful()) {
                                        String path = documentSnapshot.getString(Constants.KEY_PATH);
                                        String name = documentSnapshot.getString(Constants.KEY_NAME);
                                        String mobile = documentSnapshot.getString(Constants.KEY_MOBILE);
                                        meetAdapter.addItem(new MeetItem(path, name, mobile, myId, myName, userId, docId));
                                        addRecyclerView.setAdapter(meetAdapter);
                                    }
                                });
                            }
                        }
                     });
        addButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(AddActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }
}
