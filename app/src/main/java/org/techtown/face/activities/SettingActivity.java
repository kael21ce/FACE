package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        
        //계정 설정
        binding.setAccount.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        binding.setFamily.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, FamilySettingActivity.class);
            startActivity(intent);
        });



    }
}