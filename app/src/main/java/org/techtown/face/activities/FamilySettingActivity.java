package org.techtown.face.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.adapters.FamilySettingAdapter;
import org.techtown.face.databinding.ActivityFamilySettingBinding;
import org.techtown.face.models.Family;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class FamilySettingActivity extends AppCompatActivity {
    ActivityFamilySettingBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        FamilySettingAdapter adapter = new FamilySettingAdapter();
        preferenceManager = new PreferenceManager(getApplicationContext());

        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(myId)
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = new User();
                            user.id = document.getString(Constants.KEY_USER);
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection(Constants.KEY_COLLECTION_USERS).document(user.id).get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    DocumentSnapshot snapshot = task1.getResult();
                                    user.name = snapshot.get(Constants.KEY_NAME).toString();
                                    user.mobile = snapshot.get(Constants.KEY_MOBILE).toString();
                                    user.path = snapshot.get(Constants.KEY_PATH).toString();

                                    firestore.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(user.id)
                                            .collection(Constants.KEY_COLLECTION_USERS)
                                            .whereEqualTo(Constants.KEY_USER, preferenceManager.getString(Constants.KEY_USER_ID))
                                            .get()
                                            .addOnCompleteListener(task2 -> {
                                                if(task2.isSuccessful()){
                                                    for(QueryDocumentSnapshot documentSnapshot : task2.getResult()){
                                                        user.ideal_contact = Integer.parseInt(documentSnapshot.getString(Constants.KEY_IDEAL_CONTACT));
                                                        user.min_contact = Integer.parseInt(documentSnapshot.getString(Constants.KEY_MIN_CONTACT));
                                                        user.like = documentSnapshot.getString(Constants.KEY_THEME_LIKE);
                                                        user.dislike = documentSnapshot.getString(Constants.KEY_THEME_DISLIKE);
                                                        user.docId = documentSnapshot.getId();
                                                        adapter.addItem(new Family(user));
                                                        binding.recyclerView.setAdapter(adapter);
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });

    }
}