package com.example.csc4320_project_2.ui.browsefs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.csc4320_project_2.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileSystemAdapter extends RecyclerView.Adapter<FileSystemAdapter.ViewHolder> {

    private static List<String> localDataSet;
    private static File parent_file = null;
    private static File current_file = null;

    // Represents the parent directory reference .. in a directory.
    private static final int PARENT_DIRECTORY_INDEX = 2147483647;

    // Represenets the current directory reference . in a directory.
    private static final int CURRENT_DIRECTORY_INDEX = 2147483646;

    private static final String CURRENT_DIRECTORY_STRING = ".";
    private static final String PARENT_DIRECTORY_STRING = "..";


    // Determines whether the adapter must have all of its items regenerated.
    // This only occurs when the user clicks an item that represents a directory.
    public static boolean directory_status = false;

    public final boolean regenerate_directory() { return directory_status; }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Clicking on a item will cause it to move down the directory.
                    int element_value = getAdapterPosition();
                    /*
                        TODO: PARENT_DATASET INDEX is set to the maxiumium value of an int32_t. I am working
                        on the asusmption that no user will have that amount of files in a given directory.

                     */

                    // If you're at the root directoy, there is no parent file that be shown.
                    // Instead, clicking on each item will move you to that directoy.

                    // If for some reason, the root directory is empty, then the constructor
                    // guarrenees that there is at least one item that represents the current directory.
                    // Clicking on it will do nothing.
                    System.out.println("You clicked on the element with string " +
                            localDataSet.get(element_value));
                    if (parent_file == null){
                        // Move down the root directory if it is not empty.
                        if (!localDataSet.get(element_value).equalsIgnoreCase(CURRENT_DIRECTORY_STRING)){
                            move_directory(new File(localDataSet.get(element_value)));
                        }
                    }
                    else if (localDataSet.get(element_value).equalsIgnoreCase(PARENT_DIRECTORY_STRING)){
                        move_directory(parent_file);
                    }
                    else {
                        move_directory(new File(localDataSet.get(element_value)));

                    }

                    // Search the data set for that particular index:

                    //System.out.println("Mission failed. We'll get them next time.");
                }
            });
            textView =  view.findViewById(R.id.fs_item_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     *
     * @param passed_file File representing the current directory.
     */
    public FileSystemAdapter(List<String> dataSet, File passed_file) throws NullPointerException{
        passed_file.isFile();

        localDataSet = dataSet;

        if (localDataSet.isEmpty()){
            // Add a single item stating that the filesystem is empty, represented by
            // the character '.' which is the same as in linux.
            // There will always be a single item in the dataset, representing the current directoy.
            localDataSet.add(CURRENT_DIRECTORY_STRING);
        }

        // Add a item


        current_file = passed_file;
        parent_file = passed_file.getParentFile();

        // Add a string that represents the parent file (if it exists) to the dataset.
        // It will be represented as .., the same as in Linux.
        if (parent_file != null)
            localDataSet.add(PARENT_DIRECTORY_INDEX, PARENT_DIRECTORY_STRING);
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.filesystem_row_item, viewGroup, false);



        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Repopulate the dataset to include all files in the directory located in new_file.
     *
     * @param new_file File containing the file that will replace current file.
     * by RecyclerView.
     */
    private static void move_directory(File new_file){
        if (!new_file.exists())
            return;

        // Assume the new_file is a child file of current_file, so set current_file as parrent_file.
        parent_file = current_file;
        current_file = new_file;

        LinkedList<String> temp_list = populate_directory();
        if (temp_list.isEmpty()){
            // If file cannot be read, set a link back to parent_directory.
            temp_list.add(PARENT_DIRECTORY_STRING);
        }

        // Clear the localdataset and then populate it again with temp_list.
        localDataSet.clear();
        localDataSet.addAll(temp_list);

        //localDataSet = temp_list;

    }

    private static LinkedList<String> populate_directory(){
        LinkedList<String> temp = new LinkedList<String>();

        File directory = current_file;
        if (!directory.canRead()){
            return temp;
        }
        String[] list = directory.list();
        if (list != null){
            for (String file: list)
                if (!file.contains("."))
                    temp.add(file);

        }
        Collections.sort(temp);

        return temp;

    }
}
