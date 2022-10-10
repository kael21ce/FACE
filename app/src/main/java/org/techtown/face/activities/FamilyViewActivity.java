package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityFamilyViewBinding;

public class FamilyViewActivity extends AppCompatActivity {

    ActivityFamilyViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}