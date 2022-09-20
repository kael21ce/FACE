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

import org.techtown.face.R;
import org.techtown.face.activities.SearchActivity;
import org.techtown.face.adapters.AddAdapter;

public class AddFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add, container, false);
        RecyclerView addRecyclerView = v.findViewById(R.id.addRecyclerView);
        FloatingActionButton addButton = v.findViewById(R.id.addFloatingActionButton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        addRecyclerView.setLayoutManager(layoutManager);
        AddAdapter addAdapter = new AddAdapter();


        addButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        return v;
    }


}
