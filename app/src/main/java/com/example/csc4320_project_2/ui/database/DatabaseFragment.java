package com.example.csc4320_project_2.ui.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csc4320_project_2.R;
import com.example.csc4320_project_2.sqlite.DatabaseContract;

import java.util.LinkedList;
import java.util.Vector;

public class DatabaseFragment extends Fragment {

    private DatabaseViewModel databaseViewModel;
    private RecyclerView database_recycler_view;
    private LinkedList<DatabaseItem> DatabaseItem_List;

    public LinkedList<DatabaseItem> populate_list(){
        // Populate from the database.
        LinkedList<DatabaseItem> list = new LinkedList<>();
        DatabaseContract.TrackEntryDBHelper dbHelper = new DatabaseContract.TrackEntryDBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String selection = DatabaseContract.TrackEntry.COLUMN_TRACK_NAME + " = ?";
        String selection_args[] = { "*"};

        String projection[] = { BaseColumns._ID, DatabaseContract.TrackEntry.COLUMN_TRACK_NAME, DatabaseContract.TrackEntry.COLUMN_FILE_PATH};
        String SortOrder = DatabaseContract.TrackEntry.COLUMN_TRACK_NAME + " DESC";
        Cursor cursor = database.query(
                DatabaseContract.TrackEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selection_args,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                SortOrder              // The sort order
        );

        while (cursor.moveToNext()) {
            Vector<String> temp_list = new Vector<>();
            temp_list.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.TrackEntry._ID)));
            temp_list.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.TrackEntry.COLUMN_TRACK_NAME)));
            temp_list.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.TrackEntry.COLUMN_FILE_PATH)));

            DatabaseItem item = new DatabaseItem((String[]) temp_list.toArray());
            list.add(item);
        }
        return list;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        databaseViewModel =
                new ViewModelProvider(this).get(DatabaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_database, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        DatabaseItem_List = populate_list();
        /*
        databaseViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        return root;
    }
}