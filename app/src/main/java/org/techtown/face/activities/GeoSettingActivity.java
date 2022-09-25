package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.techtown.face.databinding.ActivityGeoSettingBinding;

public class GeoSettingActivity extends AppCompatActivity {
    ActivityGeoSettingBinding binding;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGeoSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}