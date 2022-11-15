package org.techtown.face.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.databinding.ActivitySignInBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "권한을 사용자가 승인함");
                } else {
                    Log.w(TAG, "권한이 거부됨.");
                }

                return;
        }
    }

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;
    String TAG = "SignInActivity";

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
        String[] permissions = {
                Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS
        };
        checkPermissions(permissions);
        //
    }

    public void checkPermissions(String[] permissions) {
        ArrayList<String> targetList = new ArrayList<String>();

        for (int i = 0; i < permissions.length; i++) {
            String curPermission = permissions[i];
            int permissionCheck = ContextCompat.checkSelfPermission(SignInActivity.this, curPermission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, curPermission + " 권한 있음");
            } else {
                Log.w(TAG, curPermission + " 권한 없음");
                if (ActivityCompat.shouldShowRequestPermissionRationale(SignInActivity.this, curPermission)) {
                    Log.w(TAG, curPermission + " 권한 설명 필요함.");
                } else {
                    targetList.add(curPermission);
                    Log.w(TAG, curPermission + "권한 요청이 추가됨");
                }
            }
        }
        String[] targets = new String[targetList.size()];
        targetList.toArray(targets);

        if (targets.length > 0) {
            ActivityCompat.requestPermissions(SignInActivity.this, targets, 101);
        }
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
                        preferenceManager.putString(Constants.KEY_PATH,documentSnapshot.getString(Constants.KEY_PATH));
                        database.collection(Constants.KEY_COLLECTION_USERS)
                                .document(documentSnapshot.getId())
                                .collection(Constants.KEY_COLLECTION_USERS)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        int i=0;
                                        for(QueryDocumentSnapshot snapshot : task1.getResult()){
                                            preferenceManager.putString(Constants.KEY_USER_ID+i,snapshot.getId());
                                            i++;
                                        }
                                        preferenceManager.putInt(Constants.KEY_COUNT, i-1);
                                    }
                                });
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