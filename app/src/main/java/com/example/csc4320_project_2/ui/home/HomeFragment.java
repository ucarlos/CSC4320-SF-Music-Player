package com.example.csc4320_project_2.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.csc4320_project_2.R;
import com.example.csc4320_project_2.sqlite.DatabaseTrack;
import com.example.csc4320_project_2.ui.database.FileParcel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.IOException;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MediaPlayer media_player = null;
    private ImageView track_artwork_view;
    private Bitmap track_bitmap;
    private static FileParcel database_parcel;
    // Enum to handle playback
    public enum PlaybackStatus {INITIAL, IS_PLAYING, IS_PAUSED, STOPPED }
    PlaybackStatus audio_button_status = PlaybackStatus.INITIAL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_audio_track);
        track_artwork_view = root.findViewById(R.id.audio_track_artwork);

        // Retrieve Bundle from DatabaseFragment
        Bundle bundle = this.getArguments();
        /*
        if (bundle != null){
            database_parcel = bundle.getParcelable("audio_path");
        }
        else {
            database_parcel = null;
        }
        */

        // Set the default album art from a bitmap in drawable:
        track_bitmap = BitmapFactory.decodeResource(root.getResources(), R.drawable.test);

        // Set the parcel to null or not depending on whether database_fragment sent a audio path or not.
        database_parcel = (bundle != null) ? bundle.getParcelable("audio_path") : null;

        // Prevent the user from playing a file that is removed or deleted by setting bundle to null.
        // This will cause the application to default to the default track. Ideally, I'd like to write
        // a notification stating that the file could not be read, but I've run out of time

        // Default Bitmap for audio file.
        //BitmapFactory.Options options = new BitmapFactory.Options();
        track_bitmap = BitmapFactory.decodeResource(root.getResources(), R.drawable.test);


        // Does not work

        if (database_parcel != null){
            File audio_file = new File(database_parcel.get_file_path());
            if (audio_file.exists()) {
                try {
                    track_bitmap = set_track_artwork(root, audio_file);
                } catch (TagException | ReadOnlyFileException | CannotReadException |
                        IOException | InvalidAudioFrameException e) {
                    track_bitmap = BitmapFactory.decodeResource(root.getResources(), R.drawable.test);
                }
            }
            else database_parcel = null;
        }


        // Now apply the track artwork:
        track_artwork_view.setImageBitmap(Bitmap.createScaledBitmap(track_bitmap, 400, 400, false));


        // TextViews
        TextView track_name = root.findViewById(R.id.text_audio_track);
        track_name.setText(R.string.default_audio_track);
        TextView audio_length_text = root.findViewById(R.id.text_audio_duration);
        audio_length_text.setVisibility(View.VISIBLE);
        TextView artist_text = root.findViewById(R.id.text_audio_artist);

        //audio_length_text.setGravity(View.TEXT_ALIGNMENT_CENTER);

        // Thread to monitor audio playback:
        // It basically waits loops until Audio playback starts, and
        new Thread(new Runnable() {

            @SuppressLint("DefaultLocale")
            public String create_audio_duration(long current_audio_in_sec,
                                                long max_audio_in_sec) {

                return String.format("%02d:%02d / %02d:%02d",
                        current_audio_in_sec / 60,
                        current_audio_in_sec % 60,
                        max_audio_in_sec / 60,
                        max_audio_in_sec % 60);

            }


            @Override
            public void run() {
                long max_audio_duration_milisec = -1;
                // Current audio position in miliseconds and seconds respectively.
                long current_audio_position_milisec = -1;
                while (true) {
                    while (audio_button_status == PlaybackStatus.INITIAL)
                        continue; // Do nothing

                    max_audio_duration_milisec = media_player.getDuration();
                    //audio_length_text.setVisibility(View.VISIBLE);
                    String audio_position;
                    // Print the audio elapsed time
                    do {
                        if (audio_button_status == PlaybackStatus.IS_PAUSED)
                            continue;
                        else if (audio_button_status == PlaybackStatus.STOPPED)
                            break;
                        else {
                            current_audio_position_milisec = media_player.getCurrentPosition();
                            // Wait a second
                            String text = create_audio_duration(current_audio_position_milisec / 1000,
                                    max_audio_duration_milisec / 1000);
                            audio_length_text.setText(text);
                        }
                    } while (current_audio_position_milisec < max_audio_duration_milisec);


                    // Now release the media_player and set everything back to normal.
                    // Set audio to stopped if it hasn't already
                    audio_button_status = PlaybackStatus.STOPPED;
                    media_player.reset();
                    media_player.release();
                    media_player = null;
                    audio_button_status = PlaybackStatus.INITIAL;

                    //audio_length_text.setVisibility(View.INVISIBLE);
                }
            }
        }).start();


        // Button Code
        Button audio_button = root.findViewById(R.id.audio_play_button);

        audio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audio_button_status == PlaybackStatus.INITIAL) {
                    System.out.println("INITIAL STATE");
                    // Prepare a thread to play audio.
                    media_player = new MediaPlayer();
                    media_player.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );

                    DatabaseTrack track = null;
                    // Play an audio file if database fragment passed one; Otherwise resort to default.
                    if (database_parcel != null) {
                        try {
                            track = new DatabaseTrack(getContext(), database_parcel.get_file_path());
                        } catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            track = new DatabaseTrack(getContext());
                        } catch (IOException | TagException | ReadOnlyFileException | CannotReadException | InvalidAudioFrameException e) {
                            e.printStackTrace();
                        }
                    }



                    assert track != null;
                    Uri audio_uri = Uri.fromFile(track.get_file());

                    try {
                        media_player.setDataSource(getContext(), audio_uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Prepare Asynchronously by creating a worker thread.
                    try {
                        media_player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Change the textview containing track name:

                    // Now play audio:
                    audio_button_status = PlaybackStatus.IS_PLAYING;
                    String title = track.get_audio_file().getTag().getFirst(FieldKey.TITLE);
                    audio_button.setText(getString(R.string.audio_pause));
                    artist_text.setText(track.get_audio_file().getTag().getFirst(FieldKey.ARTIST));
                    track_name.setText(title);
                    System.out.println("NOW PLAYING");
                    media_player.start();

                }
                else if (audio_button_status == PlaybackStatus.IS_PLAYING){
                    // Pause audio if it is playing.
                    audio_button.setText(getString(R.string.audio_play));
                    audio_button_status = PlaybackStatus.IS_PAUSED;
                    media_player.pause();

                }
                else if (audio_button_status == PlaybackStatus.IS_PAUSED){
                    // Resume audio if currently paused.
                    audio_button.setText(getString(R.string.audio_pause));

                    audio_button_status = PlaybackStatus.IS_PLAYING;
                    media_player.start();
                }

                // Deallocate all media_player details if playback has stopped.

            }

        });

        // Handles Stopping Audioplayback if the user clicks and holds for three seconds:
        audio_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Stop Playback Immediately.
                if (audio_button_status != PlaybackStatus.INITIAL){
                    media_player.stop();
                    audio_button_status = PlaybackStatus.STOPPED;
                    audio_button.setText(R.string.audio_play);
                    return true;
                }
                return false;
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;


    }

    /**
     * Sets the artwork of the audio track to the artwork found in the metadata. If none is found,
     * Simply display the default artwork.
     * @param audio_file: The audio file constructed from the string sent by the FileParcel
     */
    public Bitmap set_track_artwork(View view, File audio_file) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {

        if (audio_file == null || !audio_file.exists())
            return BitmapFactory.decodeResource(view.getResources(), R.drawable.test);

        AudioFile audioFile = AudioFileIO.read(audio_file);
        Tag tag = audioFile.getTag();
        if (tag.hasField(FieldKey.COVER_ART)){
            byte[] byte_array = tag.getFirstArtwork().getBinaryData();
            return BitmapFactory.decodeByteArray(byte_array, 0, byte_array.length);
        }
        else
            return BitmapFactory.decodeResource(view.getResources(), R.drawable.test);

    }

}