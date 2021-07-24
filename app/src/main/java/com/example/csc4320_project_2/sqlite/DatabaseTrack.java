package com.example.csc4320_project_2.sqlite;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class DatabaseTrack {
    // Public Section

    public DatabaseTrack(Context context) throws IOException, TagException, ReadOnlyFileException,
            CannotReadException, InvalidAudioFrameException {
        // Access Information in assets/
        passed_context = context;
        AssetManager am = passed_context.getAssets();
        InputStream is = am.open("default_audio_file.ogg");
        // Now create a file out of it (Inefficiently)

        byte temp_byte[] = new byte[1];

        ArrayList<Byte> read_buffer = new ArrayList<>();
        int bytes_read;
        while (is.read(temp_byte) != -1){
            read_buffer.add(temp_byte[0]);
        }

        byte output_buffer[] = new byte[read_buffer.size()];
        for (int i = 0; i < output_buffer.length; i++) {
            output_buffer[i] = read_buffer.get(i);
        }

        // Now store into temp file:

        file = File.createTempFile("Temp_File", ".ogg");
        OutputStream os = new FileOutputStream(file);
        os.write(output_buffer);
        os.close();
        is.close();
        file_path = file.getAbsolutePath();


        // This is obviously a invalid file.
        is_invalid = true;
        audio_file = AudioFileIO.read(file);

        // Setup the database:
        database_helper = new DatabaseContract.TrackEntryDBHelper(passed_context);



    }

    public DatabaseTrack(Context context, String file_path) throws TagException,
            ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        this.passed_context = context;
        database_helper = new DatabaseContract.TrackEntryDBHelper(passed_context);
        this.file_path = file_path;

        // Assume that the file is not a test or temp file. That's why the first constructor
        // exists.
        is_invalid = false;
        file = new File(file_path);
        audio_file = AudioFileIO.read(file);
    }

    public DatabaseTrack(Context context, File passed_file) throws TagException, ReadOnlyFileException, CannotReadException,
            InvalidAudioFrameException, IOException {
        this.passed_context = context;
        database_helper = new DatabaseContract.TrackEntryDBHelper(passed_context);

        this.file = passed_file;
        is_invalid = false;
        this.file_path = passed_file.getAbsolutePath();
        audio_file = AudioFileIO.read(passed_file);

        // Now extract the artwork file if possible:

    }



    public final AudioFile get_audio_file() { return audio_file; }
    public final String get_file_path() { return file_path; }
    public final boolean is_temp_file() { return is_invalid; }
    public final File get_file() { return file; }

    public void print_tags() {
        Tag tag = audio_file.getTag();

        System.out.println("Title: " + tag.getFirst(FieldKey.TITLE));
        System.out.println("Track Number: " + tag.getFirst(FieldKey.TRACK));
        System.out.println("Disc Number: " + tag.getFirst(FieldKey.DISC_NO));
        System.out.println("Artist: " + tag.getFirst(FieldKey.ARTIST));
        System.out.println("Album Artist: " + tag.getFirst(FieldKey.ALBUM_ARTIST));
        System.out.println("Album: " + tag.getFirst(FieldKey.ALBUM));
        System.out.println("Year: " + tag.getFirst(FieldKey.YEAR));
        System.out.println("Genre: " + tag.getFirst(FieldKey.GENRE));
        System.out.println("");


    }


    /**
     * Insert all applicable tags from AudioFile into the SQLite Database.
     */
    public void database_insert() {
        SQLiteDatabase database = database_helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Tag tag = audio_file.getTag();
        values.put(DatabaseContract.TrackEntry.COLUMN_TRACK_NAME, tag.getFirst(FieldKey.TITLE));
        values.put(DatabaseContract.TrackEntry.COLUMN_TRACK_NUMBER, tag.getFirst(FieldKey.TRACK));
        values.put(DatabaseContract.TrackEntry.COLUMN_ARTIST_NAME, tag.getFirst(FieldKey.ARTIST));
        values.put(DatabaseContract.TrackEntry.COLUMN_ALBUM_ARTIST_NAME,
                tag.getFirst(FieldKey.ALBUM_ARTIST));
        values.put(DatabaseContract.TrackEntry.COLUMN_ALBUM_NAME, tag.getFirst(FieldKey.ALBUM));
        values.put(DatabaseContract.TrackEntry.COLUMN_YEAR, tag.getFirst(FieldKey.YEAR));
        values.put(DatabaseContract.TrackEntry.COLUMN_FILE_PATH, file_path);
        values.put(DatabaseContract.TrackEntry.COLUMN_IS_INVALID_TRACK, is_invalid);

        long new_row_id = database.insert(DatabaseContract.TrackEntry.TABLE_NAME,
                null, values);


    }


    public void database_remove() {
        SQLiteDatabase database = database_helper.getWritableDatabase();

    }

    public void delete_temp_file() {
        if (is_invalid) {

            SQLiteDatabase database = database_helper.getWritableDatabase();

            String Selection = DatabaseContract.TrackEntry.COLUMN_TRACK_NAME + " LIKE ?";
            String arguments[] = {"Default Audio Track"};

            int deleted_rows = database.delete(DatabaseContract.TrackEntry.TABLE_NAME,
                    Selection, arguments);

            System.out.println("Number of Deleted Rows: " + deleted_rows);

            // Now delete the file as well.
            file.delete();

        }
    }


    // Private Section
    private DatabaseContract.TrackEntryDBHelper database_helper = null;
    private final String file_path;
    private final AudioFile audio_file;
    private final File file;

    private final boolean is_invalid;
    private final Context passed_context;
}
