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
                            user.min_contact = Integer.parseInt(document.get(Constants.KEY_MIN_CONTACT).toString());
                            user.ideal_contact = Integer.parseInt(document.get(Constants.KEY_IDEAL_CONTACT).toString());
                            user.like = document.get(Constants.KEY_THEME_LIKE).toString();
                            user.dislike = document.get(Constants.KEY_THEME_DISLIKE).toString();
                            user.id = document.getString(Constants.KEY_USER);
                            int expression = Integer.parseInt(document.get(Constants.KEY_EXPRESSION).toString());
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection(Constants.KEY_COLLECTION_USERS).document(user.id).get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    DocumentSnapshot snapshot = task1.getResult();
                                    user.name = snapshot.get(Constants.KEY_NAME).toString();
                                    user.image= snapshot.get(Constants.KEY_IMAGE).toString();
                                    user.mobile = snapshot.get(Constants.KEY_MOBILE).toString();
                                    user.path = snapshot.get(Constants.KEY_PATH).toString();


                                    if (expression==5) {
                                        adapter.addItem(new Family(user));
                                    } else if (expression==4) {
                                        adapter.addItem(new Family(user));
                                    } else if (expression==3) {
                                        adapter.addItem(new Family(user));
                                    } else if (expression==2) {
                                        adapter.addItem(new Family(user));
                                    } else if (expression==1) {
                                        adapter.addItem(new Family(user));
                                    } else {
                                        adapter.addItem(new Family(user));
                                    }

                                    binding.recyclerView.setAdapter(adapter);


                                }
                            });
                        }
                        Log.w(TAG, "Successfully loaded");}
                });

    }
}