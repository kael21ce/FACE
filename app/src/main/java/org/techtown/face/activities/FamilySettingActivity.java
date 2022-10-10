package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.techtown.face.databinding.ActivityFamilyAddBinding;
import org.techtown.face.databinding.ActivitySettingBinding;

public class FamilySettingActivity extends AppCompatActivity {
    ActivityFamilyAddBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}