package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.techtown.face.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //계정 설정
        LinearLayout setAccount = findViewById(R.id.setAccount);
        setAccount.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        //가족 정원 설정
        LinearLayout setGarden = findViewById(R.id.setGarden);
        setGarden.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, GardenActivity.class);
            startActivity(intent);
        });

    }
}