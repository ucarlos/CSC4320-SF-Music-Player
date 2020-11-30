package com.example.csc4320_project_2.ui.browsefs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.csc4320_project_2.R;
import com.example.csc4320_project_2.sqlite.DatabaseTrack;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileSystemAdapter extends RecyclerView.Adapter<FileSystemAdapter.ViewHolder> {

    private static List<File> localDataSet;
    private static File parent_file = null;
    private static File current_file = null;

    private static final String CURRENT_DIRECTORY_STRING = ".";
    private static final String PARENT_DIRECTORY_STRING = "..";
    private TextView textView;

    private final String[] file_extention_list = {".mp3", ".m4a", ".aac",
            ".flac", ".ogg", ".opus"};

    public static boolean UserHasClicked() {
        return hasClicked;
    }

    public static void setHasClicked(boolean hasClicked) {
        FileSystemAdapter.hasClicked = hasClicked;
    }

    private static boolean hasClicked = false;
    private static String bop;
    public final String get_bop() { return bop; }
    public void set_bop(String str) { bop = str; }

    private Bitmap music_icon;
    private Bitmap directory_icon;
    private Bitmap error_icon;



    private ImageView fs_item_icon;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public enum Directory_Status {
        INITIAL,
        REMOVING_OLD_DATASET,
        REGENERATING_NEW_DATASET,
        ADDING_NEW_DATASET
    }

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
                    //setHasClicked(true);
                    // If you're at the root directoy, there is no parent file that be shown.
                    // Instead, clicking on each item will move you to that directory.

                    // If for some reason, the root directory is empty, then the constructor
                    // guarrenees that there is at least one item that represents the current directory.
                    // Clicking on it will do nothing.
                    System.out.println("You clicked on the element with string " +
                            localDataSet.get(element_value).getAbsolutePath());

                    try {
                        onClickCheckItem(element_value, v);
                    } catch (InterruptedException | ReadOnlyFileException | CannotReadException | TagException | InvalidAudioFrameException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            textView =  view.findViewById(R.id.fs_item_name);

            music_icon = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.music_note_24px);
            directory_icon = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.folder_24px);
            fs_item_icon = view.findViewById(R.id.fs_item_icon);
            error_icon = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.ic_baseline_error_24);

        }


        public TextView getTextView() {
            return textView;
        }

        public ImageView getItemIcon() {
            return fs_item_icon;
        }

        /**
         * Determine the behavior when the user clicks on an item. The directory list will be
         * populated if the user clicks on a directory, and the function handles empty directories.
         * This function assumes that the file actually exists.
         *
         * @param  adapter_position int  Contains the position of the item in the dataset.
         * by RecyclerView.
         * @param view
         *
         */
        private void onClickCheckItem(int adapter_position, View view) throws InterruptedException, ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
            File file = container.dataset.get(adapter_position);

            // This also handles . as well as other files that are being displayed.
            if (!file.exists() || file.isHidden()) // Do nothing if the file doesn't exist or is hidden.
                return;


            String item_name = container.dataset.get(adapter_position).getName();
            // Do nothing if item is "."
            if (item_name.equalsIgnoreCase(CURRENT_DIRECTORY_STRING))
                return;

            //get_directory_list(file);


            // Now handle if the directory is at the top (parent_file is null)
            if (file.isDirectory()) {
                move_directory(file, container);
            }
            else if (is_audio_file(item_name)){
                insert_file_into_database(view, file);
            }
            // If the file is .., then you are moving to the parent directory, and
            // You should make sure to add its parent directory if it has one.
            // Y
        }
    }

    /**
     * Insert the given file into the database by constructing an DatabaseTrack object
     * and inserting it into the database.
     */
    public void insert_file_into_database(View v, File passed_file) throws ReadOnlyFileException,
            IOException, TagException, InvalidAudioFrameException, CannotReadException {
        DatabaseTrack databaseTrack = new DatabaseTrack(v.getContext(), passed_file);
        databaseTrack.database_insert();
    }

    /**
     * Determine if the passed file_name is an accepted audio file.
     * @param file_name Name of the file that has been clicked
     * @return a boolean determining whether it is an audio_file or not.
     */
    public Boolean is_audio_file(String file_name){
        int extension_index = file_name.lastIndexOf('.');
        if (extension_index == -1) return false;


        String file_name_extension = file_name.substring(extension_index);
        for (String i : file_extention_list)
            if (file_name_extension.equals(i))
                return true;

        return false;
    }
    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     *
     * @param passed_file File representing the current directory.
     */
    public FileSystemAdapter(List<File> dataSet, File passed_file) throws NullPointerException{
        passed_file.isFile();

        localDataSet = dataSet;
        // If filesystem cannot be accessed, simply do this.
        if (localDataSet == null) {
            localDataSet = new LinkedList<File>();
            localDataSet.add(new File ("DUMMY FILE DOESNT EXIST"));
            localDataSet.add(new File(CURRENT_DIRECTORY_STRING));

            // Do not attempt to pass a parent file if the file cannot be acessed.
            current_file = passed_file;
            parent_file = passed_file.getParentFile();

            return;
        }


        if (localDataSet.isEmpty()) {
            // Add a single item stating that the filesystem is empty, represented by
            // the character '.' which is the same as in linux.
            // There will always be a single item in the dataset, representing the current directory.
            localDataSet.add(new File(CURRENT_DIRECTORY_STRING));
        }

        // Set boolean to hasClicked:
        setHasClicked(false);


        current_file = passed_file;
        parent_file = passed_file.getParentFile();

        // Add a string that represents the parent file (if it exists) to the dataset.
        // It will be represented as .., the same as in Linux.
        if (parent_file != null)
            localDataSet.add(parent_file);
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
        File f = localDataSet.get(position);

        // If the file is the parent of current file, then replace text with ..
        if (f.equals(current_file.getParentFile()))
            viewHolder.getTextView().setText(PARENT_DIRECTORY_STRING);
        else if (f.getName() == CURRENT_DIRECTORY_STRING) {
            viewHolder.getTextView().setText("[FILE CANNOT BE READ.]");
            viewHolder.getItemIcon().setImageBitmap(error_icon);
        }
        else {// Otherwise set it to the name of the file.
            viewHolder.getTextView().setText(localDataSet.get(position).getName());

            // Set bitmap depending on if it is a directory or not:
            if (f.isDirectory())
                viewHolder.getItemIcon().setImageBitmap(directory_icon);
            else
                viewHolder.getItemIcon().setImageBitmap(music_icon);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    private void get_directory_list(File new_file) {
        if (!new_file.exists())
            return;

        LinkedList<File> temp_list = populate_directory(new_file);

        if (!temp_list.isEmpty()) {
            System.out.println("Printing list of files in " + new_file.getAbsolutePath());
            for (File fi : temp_list) {
                System.out.printf("File Name: %s\tFilePath: %s\n", fi.getName(), fi.getAbsolutePath());
            }
        }
        else {
            System.out.println("File Path " + new_file.getAbsolutePath() +
                    " is either empty or inaccessible.");
        }

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

        // If the new_file is the parent_file of current_file, simply assign parent_file to current_file,
        // and then set the parent of parent_file as the new parent_file.
        if (new_file.equals(container.container_parent_file)){
            container.container_current_file = new_file;
            container.container_parent_file = new_file.getParentFile();
        }
        else {
            // Assume the new_file is a child file of current_file, so set current_file as parrent_file.
            container.container_parent_file = container.container_current_file;
            container.container_current_file = new_file;

        }

        LinkedList<File> temp_list = populate_directory(new_file);

        // If the new file has a parent file that is valid, or if the file cannot be read,
        // Make sure to add the parent directory string.

        if (temp_list.isEmpty() || (new_file.getParentFile() != null)){
            temp_list.add(new_file.getParentFile());
        }

        // Clear the localdataset and then populate it again with temp_list.
        for (File f : temp_list){
            System.out.println("File Path: " + f.getAbsolutePath());
        }

        //localDataSet = temp_list;


        //regenerate_dataset(container.dataset, temp_list);
        regenerate_dataset_spinlock(container.dataset, temp_list);

        // Now set false.
        //setHasClicked(false);

    }

    private synchronized void regenerate_dataset(List<File> dataset, List<File> new_list) throws InterruptedException {

        set_directory_status(Directory_Status.REMOVING_OLD_DATASET);
        //System.out.println("Adapter: Setting Directory Status to \"REMOVING OLD DATASET.");
        //System.out.println(get_directory_status().toString());
        // Wake up the Container:
        notifyAll();

        // Do nothing until the adapter is notified that the dataset has been cleared.
        //System.out.println("Adapter: Sleeping until Thread has removed the old dataset from the adapter.");
        while (get_directory_status() == Directory_Status.REMOVING_OLD_DATASET) {
            //System.out.println("Adapter Directory Status: " + get_directory_status());
            wait();
        }

        //System.out.println("Adapter: Waking up and clearing the old database to make room for the new database.");
        // Now clear the database.
        dataset.clear();
        dataset.addAll(new_list);
        set_directory_status(Directory_Status.ADDING_NEW_DATASET);
        notifyAll();

        //System.out.println("Adapter: Sleeping until the Thread finishes adding the new dataset into the database.");
        while (get_directory_status() == Directory_Status.ADDING_NEW_DATASET)
            wait();

        // Now complete!
        //System.out.println("Adapter: Complete!");
    }

    /**
     * A version of regenerate_dataset using nothing but spinlocks. Horribly inefficient; It is to
     * be used if nothing else works.
     * @param dataset
     * @param new_list
     */
    public void regenerate_dataset_spinlock(List<File> dataset, List<File> new_list){
        set_directory_status(Directory_Status.REMOVING_OLD_DATASET);
        System.out.println("Adapter: Setting Directory Status to \"REMOVING OLD DATASET.");
        System.out.println(get_directory_status().toString());
        set_bop("SONICBOOM!");
        while (get_directory_status() == Directory_Status.REMOVING_OLD_DATASET) {
            continue;
        }

        System.out.println("Adapter: Waking up and clearing the old database to make room for the new database.");
        dataset.clear();
        dataset.addAll(new_list);
        set_directory_status(Directory_Status.ADDING_NEW_DATASET);

        System.out.println("Adapter: Sleeping until the Thread finishes adding the new dataset into the database.");
        while (get_directory_status() == Directory_Status.ADDING_NEW_DATASET)
            continue;

        // Now complete.
    }


    private static LinkedList<File> populate_directory(File file){
        LinkedList<File> temp = new LinkedList<File>();

        File directory = file;
        if (!directory.canRead()){
            return temp;
        }

        File [] list = directory.listFiles();

        if (list != null){
            for (File f: list)
                if (!f.getName().contains(CURRENT_DIRECTORY_STRING))
                    temp.add(f);

        }
        Collections.sort(temp);

        return temp;

    }

    //
    // Simply contains a structure containing references to
    // localdataset, current_file, and parent_file. This is done to avoid having to make
    // ViewHolder a static class.
    private static class ViewHolderArgumentContainer {
        private List<File> dataset = localDataSet;
        private File container_current_file = current_file;
        private File container_parent_file = parent_file;
        // Default constructor

        public ViewHolderArgumentContainer(){ }

        public ViewHolderArgumentContainer(List<File> dataset, File c_current_file,
                                           File c_parent_file){
            this.dataset = dataset;
            this.container_current_file = current_file;
            this.container_parent_file = parent_file;
        }
    }
}
