package org.techtown.face.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.databinding.ActivityAccountBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;
import java.util.Objects;

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
        binding.deleteAccount.setOnClickListener(view -> deleteUser());
        binding.changeInfo.setOnClickListener(view -> changeInformation());
        binding.phoneNumber.setText(preferenceManager.getString(Constants.KEY_MOBILE));
        binding.imageView.setImageBitmap(getUserImage(preferenceManager.getString(Constants.KEY_IMAGE)));
        binding.like.setText(preferenceManager.getString(Constants.KEY_THEME_LIKE));
        binding.dislike.setText(preferenceManager.getString(Constants.KEY_THEME_DISLIKE));

    }

    private void changeInformation(){
        Intent intent = new Intent(AccountActivity.this,ChangeInfoActivity.class);
        startActivity(intent);
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

    private void deleteUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(task -> {if(task.isSuccessful()){showToast("Delete Successful");}});

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(Constants.KEY_COLLECTION_CHAT).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                    String chatId = queryDocumentSnapshot.getId();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String senderId = queryDocumentSnapshot.getString(Constants.KEY_SENDER_ID);
                    String userId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(Objects.equals(senderId, userId)){
                        db.collection(Constants.KEY_COLLECTION_CHAT)
                                .document(chatId)
                                .delete()
                                .addOnSuccessListener(unused->showToast("delete chat succeed"));
                    }

                }
            }
        });

        firestore.collection(Constants.KEY_COLLECTION_CONVERSIONS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                    String conversionId = queryDocumentSnapshot.getId();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String senderId = queryDocumentSnapshot.getString(Constants.KEY_SENDER_ID);
                    String userId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(Objects.equals(senderId,userId)){
                        db.collection(Constants.KEY_COLLECTION_CONVERSIONS)
                                .document(conversionId)
                                .delete()
                                .addOnSuccessListener(unused -> showToast("delete conversion succeed"));
                    }
                }
            }
        });

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .delete()
                .addOnSuccessListener(unused -> {
                    showToast("Deleted User Success");
                    preferenceManager.clear();
                    Intent intent = new Intent(AccountActivity.this,SignInActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(runnable -> showToast("Failed delete"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(String.valueOf(encodedImage),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

}
