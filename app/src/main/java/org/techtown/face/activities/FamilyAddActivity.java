package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.databinding.ActivityFamilyAddBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;
import java.util.regex.Pattern;

public class FamilyAddActivity extends AppCompatActivity {

    ActivityFamilyAddBinding binding;
    PreferenceManager preferenceManager;
    private final Intent intent = getIntent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());


        String mobile = intent.getStringExtra(Constants.KEY_MOBILE);
        String path = intent.getStringExtra(Constants.KEY_PATH);

        StorageReference reference = FirebaseStorage.getInstance().getReference().child(path);
        reference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(binding.image));
        binding.mobile.setText(mobile);

        binding.add.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                familyAdd();
            }
        });
    }

    private void familyAdd(){
        String userId = intent.getStringExtra(Constants.KEY_USER_ID);

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

        HashMap<String,Object> notification = new HashMap<>();
        notification.put(Constants.KEY_NOTIFICATION, Constants.KEY_FAMILY_REQUEST);
        notification.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .add(notification);

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
    }

    private Boolean isValidSignUpDetails() {
        String pattern = "^[0-99]*$";
        if (binding.nameTxt.getText().toString().trim().isEmpty()) {
            showToast("이름을 입력해주세요");
            return false;
        } else if (binding.idealContact.getText().toString().trim().isEmpty()) {
            showToast("이상적인 연락 횟수를 입력해주세요");
            return false;
        } else if (!Pattern.matches(pattern, binding.idealContact.getText().toString())) {
            showToast("제대로 된 이상적인 연락 횟수를 입력해주세요");
            return false;
        } else if (binding.minContact.getText().toString().trim().isEmpty()) {
            showToast("최소 연락 횟수를 입력해주세요");
            return false;
        } else if (!Pattern.matches(pattern, binding.minContact.getText().toString())) {
            showToast("정상적인 최소 연락 횟수를 입력해주세요");
            return false;
        } else if (binding.themeLike.getText().toString().trim().isEmpty()) {
            showToast("선호 여부를 입력해주세요");
            return false;
        } else if (binding.themeDislike.getText().toString().trim().isEmpty()) {
            showToast("불호 여부를 입력해주세요");
            return false;
        } else {
            return true;
        }

    }
    private void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}