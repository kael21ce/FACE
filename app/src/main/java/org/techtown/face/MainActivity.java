package org.techtown.face;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FrameFragment frameFragment;
    ScaleFragment scaleFragment;
    MomentFragment momentFragment;

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
                break;
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
        setContentView(R.layout.activity_main);

        frameFragment = new FrameFragment();
        scaleFragment = new ScaleFragment();
        momentFragment = new MomentFragment();

        abar = getSupportActionBar();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, frameFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                        }
                        return false;
                    }
                }
        );
    }
}