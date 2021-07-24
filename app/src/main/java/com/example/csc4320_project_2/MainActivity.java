package com.example.csc4320_project_2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.Menu;

import com.example.csc4320_project_2.sqlite.DatabaseContract;
import com.example.csc4320_project_2.sqlite.DatabaseTrack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void testDatabaseTag() throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        System.out.println("****************************************************************");
        System.out.println("TESTING DATABASE TAG!");
        System.out.println("****************************************************************");
        // Test Taglib Here:
        DatabaseTrack dt = new DatabaseTrack(getApplicationContext());
        dt.print_tags();

        System.out.println("****************************************************************");
        System.out.println("INSERT TAG INTO DATABASE!");
        System.out.println("****************************************************************");
        dt.database_insert();
        // Now check if it exists:
        DatabaseContract.TrackEntryDBHelper dbHelper =
                new DatabaseContract.TrackEntryDBHelper(getApplicationContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String selection = DatabaseContract.TrackEntry.COLUMN_TRACK_NAME + " = ?";
        String selection_args[] = { "Default Audio Track"};

        String projection[] = { BaseColumns._ID, DatabaseContract.TrackEntry.COLUMN_TRACK_NAME };
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

            System.out.println((cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.TrackEntry._ID))));
            //temp_list.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.TrackEntry.COLUMN_TRACK_NAME)));
            //temp_list.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.TrackEntry.COLUMN_FILE_PATH)));

            //System.out.println("Results : " + temp);
        }


        System.out.println("****************************************************************");
        System.out.println("TESTING AUDIO PLAYBACK!");
        System.out.println("****************************************************************");

        dt.delete_temp_file();

        System.out.println("****************************************************************");
        System.out.println("NOW CARRY ON!");
        System.out.println("****************************************************************");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        check_storage_permissions(this)
        ;

        FloatingActionButton fab = findViewById(R.id.fab);
        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void check_storage_permissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}