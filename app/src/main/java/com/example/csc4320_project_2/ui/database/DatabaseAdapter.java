package com.example.csc4320_project_2.ui.database;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csc4320_project_2.R;
import com.example.csc4320_project_2.sqlite.DatabaseContract;
import com.example.csc4320_project_2.ui.home.HomeFragment;

import java.util.List;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.ViewHolder> {

    private static List<DatabaseItem> local_data_set;
    private TextView database_row_name;
    private static Fragment current_fragment;

    public final Fragment getCurrent_fragment() { return current_fragment; }

    public final List<DatabaseItem>  get_local_data_set() {
        return local_data_set;
    }

    public static List<DatabaseItem> get_static_local_data_set(){
        return local_data_set;
    }


    // Constructor

    public DatabaseAdapter(List<DatabaseItem> data_set, Fragment frag){
        local_data_set = data_set;
        current_fragment = frag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.database_row_item, parent, false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String temp = local_data_set.get(position).get_item_name();
        holder.getTextView().setText(local_data_set.get(position).get_item_name());
    }

    @Override
    public int getItemCount() {
        return local_data_set.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view){
            super(view);

            database_row_name =  view.findViewById(R.id.database_row_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int element_value = getAdapterPosition();
                    System.out.println("You clicked on " + element_value);

                    // Clicking on a database item sets an intent to the
                    // player fragment to play the song with the selected track.

                    onClickAddItem(element_value);
                }
            });


        }

        /**
         * Given a DatabaseItem, automatically send the file to the home fragment and
         * play the song.
         */
        public void onClickAddItem(int element_index){
            DatasetContainer container = new DatasetContainer();
            DatabaseItem databaseItem = container.get_container_list().get(element_index);
            // Now send the file somehow?
            // Return any
            if (databaseItem.equals(new DatabaseItem()) || databaseItem.get_file_path().equals("")){
                return;
            }

            //
            System.out.println("----------------------------------------------------------------");
            System.out.println("Schema:");
            System.out.println(DatabaseContract.getSqlCreateEntries());
            System.out.println("----------------------------------------------------------------");

            // Prepare a fragment with a parcel containing the audio file path:
            String file_path = databaseItem.get_file_path();
            FileParcel fileParcel = new FileParcel(file_path);
            HomeFragment fragment = new HomeFragment();
            //fragment.getFragmentManager().beginTransaction().replace(fragment, R.id.nav_home)
            // Replace fragment with home fragment.


            Bundle bundle = new Bundle();
            bundle.putParcelable("audio_path", fileParcel);
            fragment.setArguments(bundle);

            // Now replace the current_fragment with one of home fragment.

            current_fragment.getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();

        }

        public TextView getTextView() {
            return database_row_name;
        }
    }

    private static class DatasetContainer {
        private List<DatabaseItem> list_pointer = get_static_local_data_set();

        private final List<DatabaseItem> get_container_list() { return list_pointer; }

        public DatasetContainer(){

        }
    }

}
