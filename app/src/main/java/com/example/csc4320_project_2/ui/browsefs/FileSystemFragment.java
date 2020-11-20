package com.example.csc4320_project_2.ui.browsefs;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csc4320_project_2.R;

import java.util.ArrayList;


public class FileSystemFragment extends Fragment {

    // The File Manager will only display files with the following file extentions,
    // alongwith directories.
    // Basically, if you click a directory, it opens the contents of said directory.
    // There should be a option to go back (through .. on UNIX/Linux)
    private final String file_extention_list[] = {".mp3", ".m4a", ".aac",
            ".flac", ".ogg", ".opus"};

    private SlideshowViewModel slideshowViewModel;
    private RecyclerView recyclerView;
    private final String filesystem_root = "/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_filesystem, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);

        // Set up RecyclerView:

        ArrayList<String> list = new ArrayList<String>();
        list.add("Hello World!");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // WHY IS THIS NULL
        recyclerView = root.findViewById(R.id.FileSystemView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new FileSystemAdapter(list));



        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}