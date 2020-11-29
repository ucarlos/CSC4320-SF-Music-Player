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
    public static final FileSystemAdapter getFileSystemAdapter() {
        return adapter;
    }


    public final FileSystemAdapter getFileSystemAdapter_const() { return adapter; }

    private static FileSystemAdapter adapter;

    public final FileSystemAdapterRunnable getRunnable() {
        return runnable;
    }



    private FileSystemAdapterRunnable runnable;


    public FileSystemAdapterContainer(FileSystemAdapter adapter){
        FileSystemAdapterContainer.adapter = adapter;
        runnable = new FileSystemAdapterRunnable();
    }

    public static class FileSystemAdapterRunnable implements Runnable {
        private boolean do_stop = false;
        private FileSystemAdapter fsadapter;

        public final FileSystemAdapter getLocaladapter() { return fsadapter; }
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
                System.out.println("FSAdapterContainer: Will the user ever click?");
                try {
                    repopulate_items();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //repopulate_items_spinlock();

            }

        public FileSystemAdapterRunnable(){
            super();
            fsadapter = getFileSystemAdapter();

        }

        public FileSystemAdapterRunnable(FileSystemAdapter adapter){
            super();
            fsadapter = adapter;
        }

        public synchronized void repopulate_items() throws InterruptedException {
            // Do nothing util needed.
            FileSystemAdapter adapter = getLocaladapter();
            System.out.println("Thread: Sleeping util Directory Status changes from INITIAL.");
            System.out.println(adapter.get_directory_status().toString());
            while (adapter.directory_is_initial()) {
                System.out.println("Thread Directory Status: " + adapter.get_directory_status());
                wait();
            }

            System.out.println("Thread: Waking up since Directory Status is not INITIAL.");
            System.out.println("Thread: Notifying that all the items in the database have been removed.");
            // Now notify that the items have been removed.
            adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
            adapter.set_directory_status(FileSystemAdapter.Directory_Status.REGENERATING_NEW_DATASET);

            // Wake up the Adapter
            notifyAll();
            System.out.println("Thread: Waiting util the New Database has finished regenerating.");
            while (adapter.get_directory_status() == FileSystemAdapter.Directory_Status.REGENERATING_NEW_DATASET)
                wait();

            // Now notify that new items have been added to the adapter.
            System.out.println("Thread: Inserting new Database items into adapter and resetting Directory Status to INITIAL.");
            adapter.notifyItemRangeInserted(0, adapter.getItemCount());
            adapter.set_directory_status(FileSystemAdapter.Directory_Status.INITIAL);

            // Now wake up the Adapter again.
            notifyAll();
            // Done!
            System.out.println("Thread: Complete!");
        }

        /**
         * Spinlock version of repopulate_items(). Horribly Ineffiecent.
         */
        public void repopulate_items_spinlock() {
            FileSystemAdapter adapter = getLocaladapter();
            System.out.println("Thread: Sleeping until Directory Status changes from INITIAL.");
            String ss = adapter.get_bop();
            adapter.set_bop("NOOOOOOOOOOOO!");
            while (adapter.directory_is_initial()) {
                continue;
            }

            System.out.println("Thread: Waking up since Directory Status is not INITIAL.");
            System.out.println("Thread: Notifying that all items in the database have been removed.");
            // Now notify that the items have been removed.
            adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
            adapter.set_directory_status(FileSystemAdapter.Directory_Status.REGENERATING_NEW_DATASET);
            while (adapter.get_directory_status() == FileSystemAdapter.Directory_Status.REGENERATING_NEW_DATASET)
                continue;

            System.out.println("Thread: Inserting new Database items into adapter and resetting Directory Status to INITIAL.");
            adapter.notifyItemRangeInserted(0, adapter.getItemCount());
            adapter.set_directory_status(FileSystemAdapter.Directory_Status.INITIAL);

            System.out.println("Thread: Complete!");
        }
    }
}
