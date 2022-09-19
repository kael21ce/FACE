package org.techtown.face.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.techtown.face.R;
import org.techtown.face.adapters.RecentConversionsAdapter;
import org.techtown.face.databinding.ActivityMainBinding;
import org.techtown.face.fragments.AddFragment;
import org.techtown.face.fragments.FrameFragment;
import org.techtown.face.fragments.MomentFragment;
import org.techtown.face.fragments.ScaleFragment;
import org.techtown.face.models.ChatMessage;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FrameFragment frameFragment;
    ScaleFragment scaleFragment;
    MomentFragment momentFragment;
    AddFragment addFragment;
    private FirebaseFirestore db;
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

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
                //데이터베이스 구현 후 추가
                break;
            case R.id.delete_moment:
                //데이터베이스 구현 후 추가
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        getToken();

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


}