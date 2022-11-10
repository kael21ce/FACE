package org.techtown.face.activities;


import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityMainBinding;
import org.techtown.face.fragments.FrameFragment;
import org.techtown.face.fragments.MomentFragment;
import org.techtown.face.fragments.ScaleFragment;
import org.techtown.face.network.BluetoothService;
import org.techtown.face.network.ContactService;
import org.techtown.face.network.NotificationService;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        //ContactService 바인딩
        Log.w(TAG, "onResume() 호출됨.");
        Intent bIntent = new Intent(MainActivity.this, ContactService.class);
        bIntent.setAction(Constants.ACTION_CALCULATE_EXPRESSION);
        bindService(bIntent, connection, Context.BIND_AUTO_CREATE);
        //BluetoothService
        Intent btIntent = new Intent(MainActivity.this, BluetoothService.class);
        bindService(btIntent, connection, Context.BIND_AUTO_CREATE);
    }

    FrameFragment frameFragment;
    ScaleFragment scaleFragment;
    MomentFragment momentFragment;
    GardenActivity gardenFragment;
    private FirebaseFirestore db;
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    String TAG = "MainActivity";
    String CHANNEL_ID = "test";
    String DESCRIPTION = "For the test push notification";
    DrawerLayout drawerLayout;
    View drawerView;

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
            case R.id.family_garden:
                Intent gardenIntent = new Intent(this, GardenActivity.class);
                startActivity(gardenIntent);
                break;
            case R.id.add_family:
                Intent addIntent = new Intent(this, AddActivity.class);
                startActivity(addIntent);
                break;
            case R.id.setting:
                drawerLayout.openDrawer(drawerView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //ContactService를 위한 Interface
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d("ContactService", service + "연결됨.");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer_setting);
        ImageButton close_button = findViewById(R.id.close_button);
        close_button.setOnClickListener(v -> drawerLayout.closeDrawers());
        //설정창 기능
        LinearLayout setLocation = findViewById(R.id.setLocation);
        setLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, GeoSettingActivity.class);
            startActivity(intent);
        });
        LinearLayout setAccount = findViewById(R.id.setAccount);
        setAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        });
        LinearLayout setFamily = findViewById(R.id.setFamily);
        setFamily.setOnClickListener(v -> {
            Intent intent = new Intent(this, FamilySettingActivity.class);
            startActivity(intent);
        });
        //

        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        getToken();

        createNotificationChannel();
        Intent notiIntent = new Intent(this, NotificationService.class);
        startService(notiIntent);

        preferenceManager = new PreferenceManager(MainActivity.this);

        frameFragment = new FrameFragment();
        scaleFragment = new ScaleFragment();
        momentFragment = new MomentFragment();
        gardenFragment = new GardenActivity();

        abar = getSupportActionBar();

        //위험 권한 묻기
        String[] permissions = {
                Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.POST_NOTIFICATIONS
        };
        checkPermissions(permissions);
        //

        getToken();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, frameFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Animator anim = new ValueAnimator();
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.frameTab:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    frameFragment).commit();
                            anim.setDuration(3000);
                            //anim.start();

                            return true;
                        case R.id.scaleTab:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    scaleFragment).commit();

                            return true;
                        case R.id.momentTab:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    momentFragment).commit();

                            return true;
                    }
                    return false;
                });

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            if(Objects.equals(snapshot.getString(Constants.KEY_NOTIFICATION), Constants.KEY_MEET)){
                                String docId = snapshot.getId();
                                String userId = snapshot.getString(Constants.KEY_USER);
                                String name = snapshot.getString(Constants.KEY_NAME);
                                dialogClick(docId, userId, name);
                            }
                        }
                    }
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

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

    public void checkPermissions(String[] permissions) {
        ArrayList<String> targetList = new ArrayList<String>();

        for (int i = 0; i < permissions.length; i++) {
            String curPermission = permissions[i];
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, curPermission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, curPermission + " 권한 있음");
            } else {
                Log.w(TAG, curPermission + " 권한 없음");
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, curPermission)) {
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
            ActivityCompat.requestPermissions(MainActivity.this, targets, 101);
        }
    }

    public void dialogClick(String docId, String userId, String name) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("대면 만남").setMessage(name+"님의 대면 만남을 수락하시겠습니까?");

        builder.setPositiveButton("예", (dialog, which) -> {
            long now = System.currentTimeMillis();
            HashMap<String, Object> window = new HashMap<>();
            window.put(Constants.KEY_WINDOW, now);
            Log.e("This ", "is->"+userId);
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_USER, userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                db.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                        .collection(Constants.KEY_COLLECTION_USERS)
                                        .document(snapshot.getId())
                                        .update(window);
                            }
                        }
                    });
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId)
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_USER, preferenceManager.getString(Constants.KEY_USER_ID))
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                db.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(userId)
                                        .collection(Constants.KEY_COLLECTION_USERS)
                                        .document(snapshot.getId())
                                        .update(window);
                            }
                        }
                    });

            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                    .document(docId)
                    .delete();
        });
        builder.setNegativeButton("아니요", (dialog, which) -> db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .document(docId)
                .delete());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}