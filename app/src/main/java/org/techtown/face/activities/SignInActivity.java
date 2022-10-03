package org.techtown.face.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.face.databinding.ActivitySignInBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //앱체크, 스토리지 사용을 위해서 필요함
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

        //위험 권한 묻기
        ActivityResultLauncher<String> permissionLauncher
                = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d("MainActivity", "권한이 허용되었습니다.");
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("MainActivity", "앱 사용에 차질이 발생할 수 있습니다.");
                Toast.makeText(this, "앱 사용에 차질이 발생할 수 있습니다."
                        , Toast.LENGTH_SHORT).show();
            }
        });

        String[] permissionList = {
                Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.READ_PHONE_STATE
        };

        for (String p : permissionList) {
            permissionLauncher.launch(p);
        }
        //
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(view -> {
            if (isValidSignInDetails()){
                auth(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString());
            }
        });

        binding.forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this,PasswordResetActivity.class);
            startActivity(intent);
        });
    }

    private void auth(String email, String password){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,task -> {
           if(task.isSuccessful()){
               signIn();
           } else {
               showToast("authentication failed");
           }
        });
    }

    private void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&& task.getResult()!= null
                            &&task.getResult().getDocumentChanges().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_MOBILE,documentSnapshot.getString(Constants.KEY_MOBILE));
                        preferenceManager.putString(Constants.KEY_BIRTHDAY,documentSnapshot.getString(Constants.KEY_BIRTHDAY));
                        preferenceManager.putString(Constants.KEY_THEME_LIKE,documentSnapshot.getString(Constants.KEY_THEME_LIKE));
                        preferenceManager.putString(Constants.KEY_THEME_DISLIKE,documentSnapshot.getString(Constants.KEY_THEME_DISLIKE));
                        preferenceManager.putString(Constants.KEY_PATH,documentSnapshot.getString(Constants.KEY_PATH));
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });


    }
    private void loading(boolean isLoading){
        if (isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressSignIn.setVisibility(View.VISIBLE);
        }else {
            binding.progressSignIn.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails(){
        if (binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else {
            return true;
        }

    }
}