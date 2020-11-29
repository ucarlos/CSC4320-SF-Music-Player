package com.example.csc4320_project_2.ui.browsefs;

import android.app.Activity;
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

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;


public class FileSystemFragment extends Fragment {

    // The File Manager will only display files with the following file extentions,
    // alongwith directories.
    // Basically, if you click a directory, it opens the contents of said directory.
    // There should be a option to go back (through .. on UNIX/Linux)


    private FileSystemViewModel fileSystemViewModel;
    private RecyclerView recyclerView;
    private final String root_path = "/";
    private Thread filesystem_adapter_thread;
    private static FileSystemAdapterContainer adapterContainer;
    private static Activity parent_activity;
    private LinkedList<File> populate_root_directory(){
        LinkedList<File> temp = new LinkedList<File>();

        File directory = new File(root_path);
        if (!directory.isDirectory()){
            System.out.println("This is NOT a directory!");
            return null;
        }

        if (!directory.canRead()){
            System.out.println("Can't read that!");
        }

        if (!directory.exists())
            System.out.println("Also this file doesn't exist.");

        File[] list = directory.listFiles();

        if (list == null) return null;

        if (list.length < 1)
            return temp;
        else {
            for (File f: list)
                if (f.canRead() && !f.getName().contains("."))
                    temp.add(f);

        }

        Collections.sort(temp);

        return temp;

    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fileSystemViewModel =
                new ViewModelProvider(this).get(FileSystemViewModel.class);
        View root = inflater.inflate(R.layout.fragment_filesystem, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        parent_activity = getActivity();
        // Set up RecyclerView:

        LinkedList<File> list = populate_root_directory();
        File new_file = new File(root_path);
        FileSystemAdapter filesystem_adapter = new FileSystemAdapter(list, new_file);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Set up container thread:
        adapterContainer = new FileSystemAdapterContainer(filesystem_adapter);

        recyclerView = root.findViewById(R.id.FileSystemView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(filesystem_adapter);


        // Now call the container into a new thread.
        //filesystem_adapter_thread = new Thread(adapterContainer.getRunnable());
        //filesystem_adapter_thread.start();
        filesystem_adapter_thread = new Thread(){
            private boolean thread_run_status = false;
            public void enable_thread() { thread_run_status = true; }
            public void disable_thread() { thread_run_status = false; }
            public final boolean thread_is_running() { return thread_run_status; }

            @Override
            public void run(){
                // Do nothing util the directory is initial.


                while (true) {

                    // Horrible spinlock solution
                    System.out.println("FS Adapter Thread: Checking if Directory is initial.");
                    while (adapterContainer.getFileSystemAdapter_const().directory_is_initial())
                        ;

                    System.out.println("FS Adapter Thread: Attempt to run the UI Thread.");
                    // Now run on the UI Thread.

                    //requireActivity().runOnUiThread(adapterContainer.getRunnable());
                    //requireActivity().runOnUiThread(new FileSystemAdapterContainer.FileSystemAdapterRunnable(filesystem_adapter));

                    adapterContainer.getRunnable().run();

                    System.out.println("FS Adapter Thread: Blocking util the Directory is ready again.");
                    // Now block until the directory is ready again.
                    while (!adapterContainer.getFileSystemAdapter_const().directory_is_initial())
                        ;
                    System.out.println("Fs Adapter Thread: Continuing into another loop.");
                }
            }
        };
        filesystem_adapter_thread.start();



        fileSystemViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }


}