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

import java.io.File;
import java.io.IOException;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MediaPlayer mediaPlayer = null;
    private ImageView trackArtworkView;
    private Bitmap trackBitmap;
    private static FileParcel databaseParcel;
    private TextView audioLengthText;
    private Thread audioThread;
    private Button audioButton;
    private TextView artistText;
    private TextView trackName;
    // Enum to handle playback
    public enum PlaybackStatus {INITIAL, IS_PLAYING, IS_PAUSED, STOPPED }

    private PlaybackStatus audioButtonStatus = PlaybackStatus.INITIAL;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_audio_track);
        trackArtworkView = root.findViewById(R.id.audio_track_artwork);

        // Retrieve Bundle from DatabaseFragment
        Bundle bundle = this.getArguments();

        // Set the default album art from a bitmap in drawable:
        trackBitmap = BitmapFactory.decodeResource(root.getResources(), R.drawable.test);

        // Set the parcel to null or not depending on whether database_fragment sent a audio path or not.
        databaseParcel = (bundle != null) ? bundle.getParcelable("audio_path") : null;

        // Prevent the user from playing a file that is removed or deleted by setting bundle to null.
        // This will cause the application to default to the default track. Ideally, I'd like to write
        // a notification stating that the file could not be read, but I've run out of time

        // Default Bitmap for audio file.
        //BitmapFactory.Options options = new BitmapFactory.Options();
        trackBitmap = BitmapFactory.decodeResource(root.getResources(), R.drawable.test);


        // Does not work

        if (databaseParcel != null){
            File audio_file = new File(databaseParcel.get_file_path());
            if (audio_file.exists()) {
                try {
                    trackBitmap = SetTrackArtwork(root, audio_file);
                } catch (TagException | ReadOnlyFileException | CannotReadException |
                        IOException | InvalidAudioFrameException e) {
                    trackBitmap = BitmapFactory.decodeResource(root.getResources(), R.drawable.test);
                }
            }
            else databaseParcel = null;
        }


        // Now apply the track artwork:
        trackArtworkView.setImageBitmap(Bitmap.createScaledBitmap(trackBitmap,
                400, 400, false));


        // TextViews
        trackName = root.findViewById(R.id.text_audio_track);
        trackName.setText(R.string.default_audio_track);
        audioLengthText = root.findViewById(R.id.text_audio_duration);
        audioLengthText.setVisibility(View.VISIBLE);
        artistText = root.findViewById(R.id.text_audio_artist);

        //audio_length_text.setGravity(View.TEXT_ALIGNMENT_CENTER);

        // Thread to monitor audio playback:
        // It basically waits loops until Audio playback starts, and
        audioThread = new Thread(new AudioPlayback());
        audioThread.start();


        // Button Code
        audioButton = root.findViewById(R.id.audio_play_button);

        audioButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                if (audioButtonStatus == PlaybackStatus.INITIAL) {
                    runInitialAudioBehavior();
                }
                else if (audioButtonStatus == PlaybackStatus.IS_PLAYING){
                    // Pause audio if it is playing.
                    audioButton.setText(getString(R.string.audio_play));
                    audioButtonStatus = PlaybackStatus.IS_PAUSED;
                    mediaPlayer.pause();

                }
                else if (audioButtonStatus == PlaybackStatus.IS_PAUSED){
                    // Resume audio if currently paused.
                    audioButton.setText(getString(R.string.audio_pause));

                    audioButtonStatus = PlaybackStatus.IS_PLAYING;
                    mediaPlayer.start();
                }

                // Deallocate all media_player details if playback has stopped.
            }

        });

        // Handles Stopping Audio playback if the user clicks and holds for three seconds:
        audioButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Stop Playback Immediately.
                if (audioButtonStatus != PlaybackStatus.INITIAL){
                    mediaPlayer.stop();
                    audioButtonStatus = PlaybackStatus.STOPPED;
                    audioButton.setText(R.string.audio_play);
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
     * Function for Default Audio Behavior
     */
    private void runInitialAudioBehavior() {
        System.out.println("INITIAL STATE");
        // Prepare a thread to play audio.
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        DatabaseTrack track = null;
        // Play an audio file if database fragment passed one; Otherwise resort to default.
        if (databaseParcel != null) {
            try {
                track = new DatabaseTrack(getContext(), databaseParcel.get_file_path());
            }
            catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                track = new DatabaseTrack(getContext());
            }
            catch (IOException | TagException | ReadOnlyFileException | CannotReadException | InvalidAudioFrameException e) {
                e.printStackTrace();
            }
        }

        assert track != null;
        Uri audioUri = Uri.fromFile(track.get_file());

        try {
            mediaPlayer.setDataSource(getContext(), audioUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Prepare Asynchronously by creating a worker thread.
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Change the textview containing track name:

        // Now play audio:
        audioButtonStatus = PlaybackStatus.IS_PLAYING;
        String title = track.get_audio_file().getTag().getFirst(FieldKey.TITLE);
        audioButton.setText(getString(R.string.audio_pause));
        artistText.setText(track.get_audio_file().getTag().getFirst(FieldKey.ARTIST));
        trackName.setText(title);
        System.out.println("NOW PLAYING");
        mediaPlayer.start();

    }

    /**
     * Sets the artwork of the audio track to the artwork found in the metadata. If none is found,
     * Simply display the default artwork.
     * @param audio_file: The audio file constructed from the string sent by the FileParcel
     */
    public Bitmap SetTrackArtwork(View view, File audio_file) throws TagException,
            ReadOnlyFileException,
            CannotReadException,
            InvalidAudioFrameException,
            IOException {

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


    /**
     * Inner Class that handles Audio Playback and Displaying the Current
     * Time Duration.
     */
    private class AudioPlayback implements Runnable {
            @SuppressLint("DefaultLocale")
            public String createAudioDuration(long CurrentAudioTimeInSec,
                                              long MaxAudioTimeInSec) {

                return String.format("%02d:%02d / %02d:%02d",
                        CurrentAudioTimeInSec / 60,
                        CurrentAudioTimeInSec % 60,
                        MaxAudioTimeInSec / 60,
                        MaxAudioTimeInSec % 60);
            }

        @Override public void run() {
            // Wait time:
            long waitTimeinMiliseconds = 600;
            long MaxAudioTimeInMiliseconds = -1;
            // Current audio position in miliseconds and seconds respectively.
            long CurrentAudioTimeInMiliSeconds = -1;

            while (true) {
                while (audioButtonStatus == PlaybackStatus.INITIAL)
                    continue; // Do nothing

                MaxAudioTimeInMiliseconds = mediaPlayer.getDuration();
                //audio_length_text.setVisibility(View.VISIBLE);
                // Print the audio elapsed time
                do {
                    if (audioButtonStatus == PlaybackStatus.IS_PAUSED)
                        continue;
                    else if (audioButtonStatus == PlaybackStatus.STOPPED)
                        break;
                    else {
                        CurrentAudioTimeInMiliSeconds = mediaPlayer.getCurrentPosition();

                        String text = createAudioDuration(CurrentAudioTimeInMiliSeconds / 1000,
                                MaxAudioTimeInMiliseconds / 1000);
                        audioLengthText.setText(text);

                        // Now wait half a second in order to prevent hogging the CPU.
                        try {
                            Thread.sleep(waitTimeinMiliseconds);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } while (CurrentAudioTimeInMiliSeconds < MaxAudioTimeInMiliseconds);


                // Now release the media_player and set everything back to normal.
                // Set audio to stopped if it hasn't already
                audioButtonStatus = PlaybackStatus.STOPPED;
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                audioButtonStatus = PlaybackStatus.INITIAL;
            }

        }

    }

}