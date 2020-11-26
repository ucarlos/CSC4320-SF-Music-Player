package com.example.csc4320_project_2.ui.browsefs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.csc4320_project_2.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
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
    private TextView textView;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public enum Directory_Status { INITIAL, REMOVING_OLD_DATASET, REGENERATING_NEW_DATASET, ADDING_NEW_DATASET}

    // Determines whether the adapter must have all of its items regenerated.
    // This only occurs when the user clicks an item that represents a directory.
    public static Directory_Status directory_status = Directory_Status.INITIAL;

    public final boolean directory_is_initial() { return directory_status == Directory_Status.INITIAL;}
    public void set_directory_status(Directory_Status ds) { directory_status = ds; }
    public final Directory_Status get_directory_status() { return directory_status; }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolderArgumentContainer container;

        public ViewHolder(View view) {
            super(view);
            container = new ViewHolderArgumentContainer();
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
                    // Instead, clicking on each item will move you to that directory.

                    // If for some reason, the root directory is empty, then the constructor
                    // guarrenees that there is at least one item that represents the current directory.
                    // Clicking on it will do nothing.
                    System.out.println("You clicked on the element with string " +
                            localDataSet.get(element_value));
                    if (parent_file == null){
                        // Move down the root directory if it is not empty.
                        if (!localDataSet.get(element_value).equalsIgnoreCase(CURRENT_DIRECTORY_STRING)){

                            File new_file = new File(container.dataset.get(element_value));
                            try {
                                move_directory(new_file, container);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    else if (localDataSet.get(element_value).equalsIgnoreCase(PARENT_DIRECTORY_STRING)){
                        // If the user is moving up the file system
                        //move_directory(parent_file);
                        try {
                            move_directory(container.container_parent_file, container);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        // If the user is moving down the filesystem and the file has a non-null parent file.
                        //move_directory(new File(localDataSet.get(element_value)));
                        File new_file = new File(container.dataset.get(element_value));
                        try {
                            move_directory(new_file, container);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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


        /**
         * Determine the behavior when the user clicks on an item. The directory list will be
         * populated if the user clicks on a directory, and the function handles empty directories.
         * This function assumes that the file actually exists.
         *
         * @param  adapter_position int  Contains the position of the item in the dataset.
         * by RecyclerView.
         *
         */
        private void onClickCheckItem(int adapter_position){
            String item_name = container.dataset.get(adapter_position);
            // Do nothing if item is "."
            if (item_name.equalsIgnoreCase(CURRENT_DIRECTORY_STRING))
                return;

            // Now handle if the directory is at the top (parent_file is null)
            if (container.container_parent_file == null){

                // Assume that you don't need to handle a empty directoty.

            }

            // If the file is .., then you are moving to the parent directory, and
            // You should make sure to add its parent directory if it has one.
            // Y


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
            // There will always be a single item in the dataset, representing the current directory.
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
     * @param container
     */
    private void move_directory(File new_file,
                                ViewHolderArgumentContainer container) throws InterruptedException {
        if (!new_file.exists())
            return;

        // If the new_file is the parent_file of current_file, simply assign  parent_file to current_file,
        // and then set the parent of parent_file as the new parent_file.
        if (new_file.equals(container.container_parent_file)){
            container.container_current_file = new_file;
            container.container_parent_file = new_file.getParentFile();
        }


        // Assume the new_file is a child file of current_file, so set current_file as parrent_file.
        container.container_parent_file = container.container_current_file;
        container.container_current_file = new_file;
        LinkedList<String> temp_list = populate_directory(new_file);


        // If the new file has a parent file that is valid, or if the file cannot be read,
        // Make sure to add the parent directory string.

        if (temp_list.isEmpty() || (new_file.getParentFile() != null)){
            temp_list.add(PARENT_DIRECTORY_STRING);
        }

        // Clear the localdataset and then populate it again with temp_list.

        localDataSet.clear();
        localDataSet.addAll(temp_list);

        //localDataSet = temp_list;
        regenerate_dataset(container.dataset, temp_list);

    }

    private synchronized void regenerate_dataset(List<String> dataset, List<String> new_list) throws InterruptedException {

        set_directory_status(Directory_Status.REMOVING_OLD_DATASET);
        // Wake up the Container:
        notifyAll();

        // Do nothing until the adapter is notified that the dataset has been cleared.
        while (get_directory_status() == Directory_Status.REMOVING_OLD_DATASET)
            wait();

        // Now clear the database.
        dataset.clear();
        dataset.addAll(new_list);
        set_directory_status(Directory_Status.ADDING_NEW_DATASET);
        notifyAll();

        while (get_directory_status() == Directory_Status.ADDING_NEW_DATASET)
            wait();

        // Now complete!

    }


    private static LinkedList<String> populate_directory(File file){
        LinkedList<String> temp = new LinkedList<String>();

        File directory = file;
        if (!directory.canRead()){
            return temp;
        }
        String[] list = directory.list();
        if (list != null){
            for (String str: list)
                if (!str.contains("."))
                    temp.add(str);

        }
        Collections.sort(temp);

        return temp;

    }

    //
    // Simply contains a structure containing references to
    // localdataset, current_file, and parent_file. This is done to avoid having to make
    // ViewHolder a static class.
    private static class ViewHolderArgumentContainer {
        private List<String> dataset = localDataSet;
        private File container_current_file = current_file;
        private File container_parent_file = parent_file;
        // Default constructor

        public ViewHolderArgumentContainer(){

        }

        public ViewHolderArgumentContainer(List<String> dataset, File c_current_file,
                                           File c_parent_file){
            this.dataset = dataset;
            this.container_current_file = current_file;
            this.container_parent_file = parent_file;
        }
    }
}
