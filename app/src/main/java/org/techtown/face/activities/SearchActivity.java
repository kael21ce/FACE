package org.techtown.face.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.adapters.SearchAdapter;
import org.techtown.face.databinding.ActivitySearchBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;
import org.techtown.face.models.SearchItem;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    PreferenceManager preferenceManager;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SearchAdapter adapter = new SearchAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.searchRecyclerView.setLayoutManager(layoutManager);
        preferenceManager = new PreferenceManager(getApplicationContext());

        String myId = preferenceManager.getString(Constants.KEY_USER_ID);


        binding.button.setOnClickListener(v -> {
            int c = adapter.getItemCount();
            for(i=0;i<c;i++){
                adapter.deleteItem(0);
            }
            adapter.notifyDataSetChanged();
            String searchNameOrMobile = binding.searchEditText.getText().toString();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        String name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                        String mobile = queryDocumentSnapshot.getString(Constants.KEY_MOBILE);
                        String userId = queryDocumentSnapshot.getId();
                        if(searchNameOrMobile.equals(name) || searchNameOrMobile.equals(mobile)) {
                            String path = queryDocumentSnapshot.getString(Constants.KEY_PATH);
                            adapter.addItem(new SearchItem(path, name, mobile, userId, myId));
                            binding.searchRecyclerView.setAdapter(adapter);
                        }
                    }
                }
            });

        });
    }
}