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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        //유저 아이디가 들어간 채팅 전부 삭제
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

        //유저 아이디가 들어간 대화 모두 삭제
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

        //유저 이미지 스토리지에서 삭제
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String image = documentSnapshot.getString(Constants.KEY_PATH);
                    StorageReference reference = FirebaseStorage.getInstance().getReference().child(image);
                    reference.delete();
                });

        //순간 이미지 스토리지에서 삭제
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_IMAGES)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            String image = snapshot.getString(Constants.KEY_IMAGE);
                            StorageReference reference = FirebaseStorage.getInstance().getReference().child(image);
                            reference.delete();
                        }
                    }
                });

        //다른 유저에서 '나' 삭제
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            String userId = snapshot.getId();
                            firestore.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(userId)
                                    .collection(Constants.KEY_COLLECTION_USERS)
                                    .whereEqualTo(Constants.KEY_USER,preferenceManager.getString(Constants.KEY_USER_ID))
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            for(QueryDocumentSnapshot documentSnapshot : task1.getResult()){
                                                String docId = documentSnapshot.getId();
                                                firestore.collection(Constants.KEY_COLLECTION_USERS)
                                                        .document(userId).collection(Constants.KEY_COLLECTION_USERS)
                                                        .document(docId)
                                                        .delete();
                                            }
                                        }else{
                                            Log.e("Got", "Fucked");
                                        }
                                    });
                        }
                    }else{
                        Log.e("Goot", "Fucked");
                    }
                });


        //하위 컬렉션 유저 문서들 삭제
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            String docId = snapshot.getId();
                            firestore.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                    .collection(Constants.KEY_COLLECTION_USERS)
                                    .document(docId)
                                    .delete();
                        }
                    } else {
                        Log.e("THis", "IS FUCK");
                    }
                });

        //유저 관련 문서 삭제
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_IMAGES)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        //하위 컬렉션 이미지 문서들 삭제
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            String docId = snapshot.getId();
                            firestore.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                    .collection(Constants.KEY_COLLECTION_IMAGES)
                                    .document(docId)
                                    .delete();
                        }
                        //유저 문서 삭제
                        firestore.collection(Constants.KEY_COLLECTION_USERS)
                                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                .delete();

                        //인증에서 유저 삭제
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnCompleteListener(task1 -> {if(task1.isSuccessful()){showToast("Delete Successful");}});

                        preferenceManager.clear();
                        Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Log.e("THis", "IS FUCK");
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(String.valueOf(encodedImage),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

}
