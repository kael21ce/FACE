package org.techtown.face.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityMainBinding;
import org.techtown.face.fragments.FrameFragment;
import org.techtown.face.fragments.GardenFragment;
import org.techtown.face.fragments.MomentFragment;
import org.techtown.face.fragments.ScaleFragment;
import org.techtown.face.network.BluetoothService;
import org.techtown.face.network.ContactService;
import org.techtown.face.network.FCMService;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
        //FCMService
        Intent fcm = new Intent(getApplicationContext(), FCMService.class);
        startService(fcm);
    }

    FrameFragment frameFragment;
    ScaleFragment scaleFragment;
    MomentFragment momentFragment;
    GardenFragment gardenFragment;
    UploadTask uploadTask;
    private FirebaseFirestore db;
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    String TAG = "MainActivity";
    String CHANNEL_ID = "test";
    String DESCRIPTION = "For the test push notification";

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
            case R.id.add_family:
                Intent addIntent = new Intent(this, AddActivity.class);
                startActivity(addIntent);
                break;
            case R.id.setting:
                Intent nIntent = new Intent(this, SettingActivity.class);
                nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent,
                        PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.user_icon)
                        .setContentTitle("FACE")
                        .setContentText("설정 창이 열렸습니다.").setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(101, mBuilder.build());

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
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        getToken();

        createNotificationChannel();

        preferenceManager = new PreferenceManager(MainActivity.this);

        frameFragment = new FrameFragment();
        scaleFragment = new ScaleFragment();
        momentFragment = new MomentFragment();
        gardenFragment = new GardenFragment();

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
                        case R.id.gardenTab:
                            abar.setTitle("FACE: 가족 정원");
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    gardenFragment).commit();

                            return true;
                    }
                    return false;
                }
        );

        //Get Token for Test Message
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.w(TAG, token);
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