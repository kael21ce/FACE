package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityFamilyDeleteBinding;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class FamilyDeleteActivity extends AppCompatActivity {
    ActivityFamilyDeleteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyDeleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(Constants.KEY_USER);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);

        binding.back.setOnClickListener(v -> {
            Intent intentBack = new Intent(FamilyDeleteActivity.this, FamilyViewActivity.class);
            startActivity(intentBack);
            finish();
        });

        binding.deleteReal.setOnClickListener(v -> {
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(myId)
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_USER, user.id)
                    .get()
                    .addOnCompleteListener(task-> {
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            String docId = snapshot.getId();
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(myId)
                                    .collection(Constants.KEY_COLLECTION_USERS)
                                    .document(docId)
                                    .delete();
                        }
                    });

            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(user.id)
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .document(user.docId)
                    .delete();

            Intent intent1 = new Intent(FamilyDeleteActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });
    }
}