package com.example.csc4320_project_2.ui.database;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.csc4320_project_2.R;

public class DatabaseFragment extends Fragment {

    private DatabaseViewModel databaseViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        databaseViewModel =
                new ViewModelProvider(this).get(DatabaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_database, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        databaseViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}