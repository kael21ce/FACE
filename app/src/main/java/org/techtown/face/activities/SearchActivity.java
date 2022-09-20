package org.techtown.face.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.adapters.SearchAdapter;
import org.techtown.face.databinding.ActivitySearchBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;
import org.techtown.face.utilites.SearchItem;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SearchAdapter adapter = new SearchAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.searchRecyclerView.setLayoutManager(layoutManager);
        binding.button.setOnClickListener(v -> {
            String searchMobile = binding.searchEditText.getText().toString();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        String path = queryDocumentSnapshot.getString(Constants.KEY_PATH);
                        String name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                        String mobile = queryDocumentSnapshot.getString(Constants.KEY_MOBILE);
                        adapter.addItem(new SearchItem(path, name, mobile));
                        binding.searchRecyclerView.setAdapter(adapter);
                    }
                } else {
                    Log.e("This", "is Fuck");
                }
            });
        });
    }
}