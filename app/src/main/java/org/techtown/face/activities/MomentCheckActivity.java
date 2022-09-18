package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.adapters.MomentCheckAdapter;
import org.techtown.face.databinding.ActivityMomentCheckBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.MomentCheckItem;
import org.techtown.face.utilites.PreferenceManager;

public class MomentCheckActivity extends AppCompatActivity {

    ActivityMomentCheckBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMomentCheckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        MomentCheckAdapter adapter = new MomentCheckAdapter();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        binding.momentCheckRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_IMAGES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            String name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            String image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            String date = queryDocumentSnapshot.getString(Constants.KEY_TIMESTAMP);
                            String userId = preferenceManager.getString(Constants.KEY_USER_ID);
                            String imageId = queryDocumentSnapshot.getId();
                            Log.e("I", "did it!" + name);
                            adapter.addItem(new MomentCheckItem(name, image, date, userId, imageId));
                            binding.momentCheckRecyclerView.setAdapter(adapter);
                        }
                    } else {
                        Log.e("Error", "fuck");
                    }
                });
    }
}