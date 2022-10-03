package org.techtown.face.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityMainBinding;
import org.techtown.face.fragments.AddFragment;
import org.techtown.face.fragments.FrameFragment;
import org.techtown.face.fragments.MomentFragment;
import org.techtown.face.fragments.ScaleFragment;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    FrameFragment frameFragment;
    ScaleFragment scaleFragment;
    MomentFragment momentFragment;
    AddFragment addFragment;
    UploadTask uploadTask;
    private FirebaseFirestore db;
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    long now = System.currentTimeMillis();

    ActionBar abar;

    //액션바를 위한 메서드
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
            case R.id.setting:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.add_moment:
                Intent momentIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                momentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(momentIntent);
                break;
            case R.id.delete_moment:
                Intent deleteIntent = new Intent(this, MomentCheckActivity.class);
                startActivity(deleteIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        getToken();
        Handler mHandler = new Handler();

        preferenceManager = new PreferenceManager(MainActivity.this);

        frameFragment = new FrameFragment();
        scaleFragment = new ScaleFragment();
        momentFragment = new MomentFragment();
        addFragment = new AddFragment();

        abar = getSupportActionBar();

        getToken();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, frameFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.frameTab:
                            abar.setTitle("FACE: 가족");
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    frameFragment).commit();

                            return true;
                        case R.id.scaleTab:
                            abar.setTitle("FACE: 연락저울");
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    scaleFragment).commit();

                            return true;
                        case R.id.momentTab:
                            abar.setTitle("FACE: 순간");
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    momentFragment).commit();

                            return true;
                        case R.id.addTab:
                            abar.setTitle("FACE: 가족 추가");
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    addFragment).commit();

                            return true;
                    }
                    return false;
                }
        );

        //위험 권한 묻기
        ActivityResultLauncher<String> permissionLauncher
                = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d("MainActivity", "권한이 허용되었습니다.");
            } else {
                Log.d("MainActivity", "앱 사용에 차질이 발생할 수 있습니다.");
            }
        });

        String[] permissionListS = {
                Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
        };

        String[] permissionList = {
                Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
        };

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
            for (String p : permissionListS) {
                permissionLauncher.launch(p);
            }
        } else {
            for (String p : permissionList) {
                permissionLauncher.launch(p);
            }
        }
        //

        //표정 계산하기
        final long[] lastNow = {0};
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserId)
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    AtomicInteger idealContact = new AtomicInteger();
                    AtomicInteger minContact = new AtomicInteger();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //상대방의 window 가져오기
                            lastNow[0] = (long) document.get(Constants.KEY_WINDOW);
                            String userId = document.get(Constants.KEY_USER).toString();
                            preferenceManager.putInt("ideal" + userId, 0);
                            preferenceManager.putInt("min" + userId, 0);
                            //
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                if (userId.equals(document1.getId())) {
                                                    idealContact.set(Integer.parseInt(Objects.requireNonNull(document1.get("ideal_contact"))
                                                            .toString()) * 86400000);
                                                    preferenceManager.putInt("ideal" + userId, idealContact.get());
                                                    minContact.set(Integer.parseInt(Objects.requireNonNull(document1.get("min_contact"))
                                                            .toString()) * 86400000);
                                                    preferenceManager.putInt("min" + userId, minContact.get());
                                                }
                                            }
                                        }
                                    });
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("MainActivity", userId + "-" + "idealContact: "
                                            + preferenceManager.getInt("ideal" + userId) +
                                            " | " + "minContact: "
                                            + preferenceManager.getInt("min" + userId));
                                    long term = (minContact.get() - idealContact.get()) / 3;
                                    long diff = now - lastNow[0];
                                    Log.e("MainActivity", "연락 간격 차이: " + diff);
                                    if (now - lastNow[0] < idealContact.get()) {
                                        //나의 데이터베이스에 상대방 expression 업데이트
                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(currentUserId)
                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                .document(document.getId())
                                                .update(Constants.KEY_EXPRESSION, 5);
                                        //상대방 데이터베이스에 내 expression 업데이트
                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                                    .document(document.get(Constants.KEY_USER).toString())
                                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                                    .document(documentSnapshot.getId())
                                                                    .update(Constants.KEY_EXPRESSION, 5);
                                                        }
                                                    }
                                                });
                                    }
                                    if (now - lastNow[0] >= idealContact.get()) {
                                        if (now - lastNow[0] < idealContact.get() + term) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 4);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(document.get(Constants.KEY_USER).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 4);
                                                            }
                                                        }
                                                    });
                                        } else if (now - lastNow[0] < idealContact.get() + term*2) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 3);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(document.get(Constants.KEY_USER).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 3);
                                                            }
                                                        }
                                                    });
                                        } else if (now - lastNow[0] < idealContact.get() + term*3) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 2);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(document.get(Constants.KEY_USER).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 2);
                                                            }
                                                        }
                                                    });
                                        } else {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 1);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(document.get(Constants.KEY_USER).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 1);
                                                            }
                                                        }
                                                    });
                                            }
                                        }
                                }
                            }, 1000);
                        }
                    }
                });
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData()!= null){
                        Log.e(TAG,result.getData().toString());
                        Uri imageUri = result.getData().getData();
                        String path = preferenceManager.getString(Constants.KEY_USER_ID)+"/"+imageUri.getLastPathSegment();

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference imageRef = storageReference.child(path);
                        uploadTask = imageRef.putFile(imageUri);
                        uploadTask.addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                showToast("We did it!");
                            } else {
                                showToast("Failed");
                            }
                        });

                        HashMap<String,Object> images = new HashMap<>();

                        String name = preferenceManager.getString(Constants.KEY_NAME);
                        String image = path;
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dates = sdf.format(date);

                        images.put(Constants.KEY_NAME,name);
                        images.put(Constants.KEY_IMAGE,image);
                        images.put(Constants.KEY_TIMESTAMP,dates);

                        db.collection(Constants.KEY_COLLECTION_USERS)
                                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                .collection(Constants.KEY_COLLECTION_IMAGES)
                                .add(images)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        showToast("horray");
                                    } else {
                                        showToast("fuck");
                                    }
                                });
                    }
                }
            });

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                db.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token."));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}