package com.example.csc4320_project_2.sqlite;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

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

public class DatabaseTag {
    // Public Section

    public DatabaseTag(Context context) throws IOException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException {
        // Acess Information in assets/
        my_context = context;
        AssetManager am = my_context.getAssets();
        InputStream is = am.open("default_audio_file.ogg");
        // Now create a file out of it:

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

        audio_file = AudioFileIO.read(file);


    }

    public DatabaseTag(String file_path) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        this.file_path = file_path;
        audio_file = AudioFileIO.read(new File(file_path));
    }

    public final AudioFile get_audio_file() { return audio_file; }
    public final String get_file_path() { return file_path; }

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

    // Private Section
    private String file_path;
    private AudioFile audio_file;
    private File file;
    private Context my_context;
}
