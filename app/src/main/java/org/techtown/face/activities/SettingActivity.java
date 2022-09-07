package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.techtown.face.R;

public class SettingActivity extends AppCompatActivity {

    private LinearLayout setAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setAccount = findViewById(R.id.setAccount);
        setAccount.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
            startActivity(intent);
        });
    }
}