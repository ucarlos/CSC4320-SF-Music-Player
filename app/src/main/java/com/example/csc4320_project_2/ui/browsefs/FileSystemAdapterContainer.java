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
    private static FileSystemAdapter fileSystemAdapter;

    public FileSystemAdapterContainer(FileSystemAdapter adapter){
        fileSystemAdapter = adapter;
    }

    static class FileSystemAdapterRunnable implements Runnable {
        public void run(){
            // While user does not change directory: sleep

            // User has changed directory, so wake up
            // Remove all items from list corresponding to old directory
            // Add all items from list corresponding to new directory
            // Set variable back to false and sleep.

        }

        public synchronized void repopulate_items() throws InterruptedException {
            // Do nothing util needed.
            while (!fileSystemAdapter.regenerate_directory())
                wait();

            // Now notify that the items have been removed.
        }

    }
}
