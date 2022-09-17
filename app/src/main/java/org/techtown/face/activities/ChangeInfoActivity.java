package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.techtown.face.databinding.ActivityChangeInfoBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class ChangeInfoActivity extends AppCompatActivity {

    ActivityChangeInfoBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.phoneNumber.setText(preferenceManager.getString(Constants.KEY_MOBILE));
        binding.birthDay.setText(preferenceManager.getString(Constants.KEY_BIRTHDAY));
        binding.like.setText(preferenceManager.getString(Constants.KEY_THEME_LIKE));
        binding.dislike.setText(preferenceManager.getString(Constants.KEY_THEME_DISLIKE));
        binding.buttonChange.setOnClickListener(view -> changeInfo());
    }

    private void changeInfo(){
        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
        preferenceManager.putString(Constants.KEY_MOBILE,binding.phoneNumber.getText().toString());
        preferenceManager.putString(Constants.KEY_BIRTHDAY,binding.birthDay.getText().toString());
        preferenceManager.putString(Constants.KEY_THEME_LIKE,binding.like.getText().toString());
        preferenceManager.putString(Constants.KEY_THEME_DISLIKE,binding.dislike.getText().toString());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(
                        Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE),
                        Constants.KEY_MOBILE, preferenceManager.getString(Constants.KEY_MOBILE),
                        Constants.KEY_BIRTHDAY, preferenceManager.getString(Constants.KEY_BIRTHDAY),
                        Constants.KEY_THEME_LIKE, preferenceManager.getString(Constants.KEY_THEME_LIKE),
                        Constants.KEY_THEME_DISLIKE, preferenceManager.getString(Constants.KEY_THEME_DISLIKE)
                ).addOnSuccessListener(unused -> showToast("Update Success"))
                .addOnFailureListener(runnable -> showToast("Update Failed"));

        Intent intent = new Intent(ChangeInfoActivity.this, AccountActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}