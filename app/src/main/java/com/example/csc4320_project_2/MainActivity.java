package com.example.csc4320_project_2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
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
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    protected void test_audio(DatabaseTrack databaseTrack) throws IOException {
        Uri audio_uri = Uri.fromFile(databaseTrack.get_file());
        MediaPlayer media_player = new MediaPlayer();
        media_player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        media_player.setDataSource(getApplicationContext(), audio_uri);
        media_player.prepare();
        media_player.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void testDatabaseTag() throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
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
            String temp = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.TrackEntry.COLUMN_TRACK_NAME));
            System.out.println("Results : " + temp);
        }


        System.out.println("****************************************************************");
        System.out.println("TESTING AUDIO PLAYBACK!");
        System.out.println("****************************************************************");

        //test_audio(dt);

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

        // Set here:


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

        /*
        try {
            testDatabaseTag();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
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
}