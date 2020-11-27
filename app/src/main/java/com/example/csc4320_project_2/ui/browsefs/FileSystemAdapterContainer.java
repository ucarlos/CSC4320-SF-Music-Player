package com.example.csc4320_project_2.ui.browsefs;

/*
 * -----------------------------------------------------------------------------
 * Created by Ulysses Carlos on 11/25/2020 at 10:33 PM
 *
 * FileSystemAdapterContainer.java
 * Contains a reference to a FileSystemAdapter and makes sure that its items
 * are recreated when the user switches directories.
 * -----------------------------------------------------------------------------
 */



public class FileSystemAdapterContainer {
    public static FileSystemAdapter getFileSystemAdapter() {
        return fileSystemAdapter;
    }

    public static void setFileSystemAdapter(FileSystemAdapter fileSystemAdapter) {
        FileSystemAdapterContainer.fileSystemAdapter = fileSystemAdapter;
    }

    private static FileSystemAdapter fileSystemAdapter;

    public final FileSystemAdapterRunnable getRunnable() {
        return runnable;
    }



    private FileSystemAdapterRunnable runnable;


    public FileSystemAdapterContainer(FileSystemAdapter adapter){
        fileSystemAdapter = adapter;
        runnable = new FileSystemAdapterRunnable();
    }

    public static class FileSystemAdapterRunnable implements Runnable {
        private boolean do_stop = false;
        private FileSystemAdapter localadapter;

        public synchronized void stop_thread(){
            do_stop = true;
        }

        public synchronized boolean continue_running_thread(){
            return !this.do_stop;
        }

        public synchronized void resume_thread(){
            do_stop = false;
        }

        public void run(){
            // While user does not change directory: sleep

            // User has changed directory, so wake up
            // Remove all items from list corresponding to old directory
            // Add all items from list corresponding to new directory
            // Set variable back to false and sleep.

            while (continue_running_thread()) {

                System.out.println("Checking if User has Clicked.");
                if (FileSystemAdapter.UserHasClicked()) {
                    try {
                        repopulate_items();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }
        }

        public FileSystemAdapterRunnable(){
            super();
            localadapter = getFileSystemAdapter();

        }

        public synchronized void repopulate_items() throws InterruptedException {
            // Do nothing util needed.
            System.out.println("Thread: Sleeping util Directory Status changes from INITIAL.");
            System.out.println(fileSystemAdapter.get_directory_status().toString());
            while (!fileSystemAdapter.directory_is_initial())
                wait();

            System.out.println("Thread: Waking up since Directory Status is not INITAL.");
            System.out.println("Thread: Notifying that all the items in the database have been removed.");
            // Now notify that the items have been removed.
            fileSystemAdapter.notifyItemRangeRemoved(0, fileSystemAdapter.getItemCount());
            fileSystemAdapter.set_directory_status(FileSystemAdapter.Directory_Status.REGENERATING_NEW_DATASET);

            // Wake up the Adapter
            notifyAll();
            System.out.println("Thread: Waiting util the New Database has finished regenerating.");
            while (fileSystemAdapter.get_directory_status() == FileSystemAdapter.Directory_Status.REGENERATING_NEW_DATASET)
                wait();

            // Now notify that new items have been added to the adapter.
            System.out.println("Thread: Inserting new Database items into adapter and resetting Directory Staus to INITIAL.");
            fileSystemAdapter.notifyItemRangeInserted(0, fileSystemAdapter.getItemCount());
            fileSystemAdapter.set_directory_status(FileSystemAdapter.Directory_Status.INITIAL);

            // Now wake up the Adapter again.
            notifyAll();
            // Done!
            System.out.println("Thread: Complete!");


        }

    }
}
