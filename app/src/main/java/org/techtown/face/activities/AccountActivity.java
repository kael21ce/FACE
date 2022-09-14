package org.techtown.face.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.face.databinding.ActivityAccountBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;

public class AccountActivity extends BaseActivity {

    PreferenceManager preferenceManager;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.SignOut.setOnClickListener(view -> signOut());
        binding.phoneNumber.setText(preferenceManager.getString(Constants.KEY_MOBILE));
        binding.imageView.setImageBitmap(getUserImage(preferenceManager.getString(Constants.KEY_IMAGE)));
        binding.like.setText(preferenceManager.getString(Constants.KEY_THEME_LIKE));
        binding.dislike.setText(preferenceManager.getString(Constants.KEY_THEME_DISLIKE));

    }

    private void signOut() {
        showToast("Signing out...........");
        database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(String.valueOf(encodedImage),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }



}
