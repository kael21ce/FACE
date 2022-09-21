package org.techtown.face.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.activities.SearchActivity;
import org.techtown.face.adapters.AddAdapter;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.SearchItem;

public class AddFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add, container, false);
        RecyclerView addRecyclerView = v.findViewById(R.id.addRecyclerView);
        FloatingActionButton addButton = v.findViewById(R.id.addFloatingActionButton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        addRecyclerView.setLayoutManager(layoutManager);
        AddAdapter addAdapter = new AddAdapter();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                    String path = queryDocumentSnapshot.getString(Constants.KEY_PATH);
                    String name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                    String mobile = queryDocumentSnapshot.getString(Constants.KEY_MOBILE);
                    addAdapter.addItem(new SearchItem(path, name, mobile));
                    addRecyclerView.setAdapter(addAdapter);
                }
            }
        });
        addButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        return v;
    }


}
