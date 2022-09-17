package org.techtown.face.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.adapters.SearchAdapter;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.Family;
import org.techtown.face.utilites.PreferenceManager;
import org.techtown.face.utilites.SearchItem;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<SearchItem> searchItemArrayList, filteredList;
    SearchAdapter searchAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EditText searchET;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerview);
        searchET = findViewById(R.id.searchFood);

        filteredList=new ArrayList<>();
        searchItemArrayList = new ArrayList<>();

        searchAdapter = new SearchAdapter(searchItemArrayList, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.get(Constants.KEY_NAME).toString();
                    String image = document.get(Constants.KEY_IMAGE).toString();
                    String mobile = document.get(Constants.KEY_MOBILE).toString();
                    searchAdapter.addItem(new SearchItem(name, mobile, image));
                }
                recyclerView.setAdapter(searchAdapter);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String searchText = searchET.getText().toString();
                searchFilter(searchText);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void searchFilter(String searchText) {
        filteredList.clear();

        for (int i = 0; i < searchItemArrayList.size(); i++) {
            if (searchItemArrayList.get(i).getUserName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(searchItemArrayList.get(i));
            } else if (searchItemArrayList.get(i).getUserMobile().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(searchItemArrayList.get(i));
            }
        }

        searchAdapter.filterList(filteredList);
    }
}