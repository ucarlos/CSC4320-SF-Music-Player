package com.example.csc4320_project_2.sqlite;
/*
 * -----------------------------------------------------------------------------
 * Created by Ulysses Carlos on 11/02/2020 at 11:38 PM
 *
 * DatabaseContract.java
 * Defines the database schema used for the project.
 * -----------------------------------------------------------------------------
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() { }

    /* Inner class that defines the table contents */
    public static class TrackEntry implements BaseColumns {
        public static final String TABLE_NAME = "track_entry";

        public static final String COLUMN_TRACK_NAME = "title";
        public static final String COLUMN_TRACK_NUMBER = "number";
        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_ALBUM_ARTIST_NAME = "album_artist_name";
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_FILE_PATH = "file_path";
        public static final String COLUMN_IS_INVALID_TRACK = "is_invalid_track";
    }

    public static String getSqlCreateEntries() { return SQL_CREATE_ENTRIES; }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TrackEntry.TABLE_NAME + " ("
            + TrackEntry._ID + ","
            + TrackEntry.COLUMN_TRACK_NAME + " TEXT,"
            + TrackEntry.COLUMN_TRACK_NUMBER + " INTEGER,"
            + TrackEntry.COLUMN_ARTIST_NAME + " TEXT, "
            + TrackEntry.COLUMN_ALBUM_ARTIST_NAME + " TEXT, "
            + TrackEntry.COLUMN_ALBUM_NAME + " TEXT, "
            + TrackEntry.COLUMN_YEAR + " INTEGER, "
            + TrackEntry.COLUMN_FILE_PATH + " TEXT UNIQUE,"
            + TrackEntry.COLUMN_IS_INVALID_TRACK + " INTEGER,"
            + "PRIMARY KEY (" + TrackEntry._ID + ", " + TrackEntry.COLUMN_FILE_PATH + "))";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME;

    public static class TrackEntryDBHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "TrackEntry.db";

        public TrackEntryDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }


    public static class ArtistTable implements BaseColumns {

    }

}
