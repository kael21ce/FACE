package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.databinding.ActivityFamilyAddBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;

public class FamilyAddActivity extends AppCompatActivity {

    ActivityFamilyAddBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();

        String name = intent.getStringExtra(Constants.KEY_NAME);
        String mobile = intent.getStringExtra(Constants.KEY_MOBILE);
        String userId = intent.getStringExtra(Constants.KEY_USER_ID);
        String path = intent.getStringExtra(Constants.KEY_PATH);

        StorageReference reference = FirebaseStorage.getInstance().getReference().child(path);
        reference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(binding.image));
        binding.mobile.setText(mobile);

        binding.add.setOnClickListener(v -> {
            String idealContact = binding.idealContact.getText().toString();
            String minContact = binding.minContact.getText().toString();
            String like = binding.themeLike.getText().toString();
            String dislike = binding.themeDislike.getText().toString();
            String familyName = binding.nameTxt.getText().toString();

            long now = System.currentTimeMillis();
            //나의 상대방에게 추가할 내용
            HashMap<String,Object> user = new HashMap<>();
            user.put(Constants.KEY_NAME, familyName);
            user.put(Constants.KEY_USER, userId);
            user.put(Constants.KEY_WINDOW, now);
            user.put(Constants.KEY_EXPRESSION, 5);
            user.put(Constants.KEY_IDEAL_CONTACT, "0");
            user.put(Constants.KEY_MIN_CONTACT, "0");
            user.put(Constants.KEY_THEME_LIKE, "");
            user.put(Constants.KEY_THEME_DISLIKE, "");

            //상대방의 나에게 추가할 내용
            HashMap<String, Object> myUser = new HashMap<>();
            myUser.put(Constants.KEY_USER, preferenceManager.getString(Constants.KEY_USER_ID));
            myUser.put(Constants.KEY_WINDOW, now);
            myUser.put(Constants.KEY_EXPRESSION, 5);
            myUser.put(Constants.KEY_IDEAL_CONTACT, idealContact);
            myUser.put(Constants.KEY_MIN_CONTACT, minContact);
            myUser.put(Constants.KEY_THEME_LIKE, like);
            myUser.put(Constants.KEY_THEME_DISLIKE, dislike);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .add(user);

            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId)
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .add(myUser);

            Intent intent1 = new Intent(FamilyAddActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });
    }
}