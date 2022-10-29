package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityFamilyViewBinding;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;

public class FamilyViewActivity extends AppCompatActivity {

    ActivityFamilyViewBinding binding;
    PreferenceManager preferenceManager;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.KEY_USER);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .whereEqualTo(Constants.KEY_NOTIFICATION, Constants.KEY_FAMILY_REQUEST)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            String id = snapshot.getId();
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                    .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                                    .document(id)
                                    .delete();
                        }
                    }
                });

        binding.nameTxt.setText(user.name);
        binding.mobile.setText(user.mobile);
        binding.idealContact.setText(String.valueOf(user.ideal_contact));
        binding.minContact.setText(String.valueOf(user.min_contact));
        binding.themeLike.setText(user.like);
        binding.themeDislike.setText(user.dislike);

        binding.deleteFamily.setOnClickListener(v -> {
            Intent intent1 = new Intent(FamilyViewActivity.this, FamilyDeleteActivity.class);
            intent1.putExtra(Constants.KEY_USER, user);
            startActivity(intent1);
        });

        binding.add.setOnClickListener(v -> {
            String name = binding.nameTxt.getText().toString();
            String idealContact = binding.idealContact.getText().toString();
            String minContact = binding.minContact.getText().toString();
            String like = binding.themeLike.getText().toString();
            String dislike = binding.themeDislike.getText().toString();

            HashMap<String,Object> content = new HashMap<>();
            content.put(Constants.KEY_NAME,name);

            HashMap<String,Object> contents = new HashMap<>();
            contents.put(Constants.KEY_IDEAL_CONTACT, idealContact);
            contents.put(Constants.KEY_MIN_CONTACT, minContact);
            contents.put(Constants.KEY_THEME_LIKE, like);
            contents.put(Constants.KEY_THEME_DISLIKE, dislike);

            //나의 상대방에게 이름 변경하기

            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_USER, user.id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                String thisId = snapshot.getId();
                                db.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                        .collection(Constants.KEY_COLLECTION_USERS)
                                        .document(thisId)
                                        .update(content);
                            }
                        }
                    });

            //상대방의 나에게 이상, 최소, 선호, 불호 전달하기.
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(user.id)
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .document(user.docId)
                    .update(contents);

            Intent intent1 = new Intent(FamilyViewActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });
    }
}