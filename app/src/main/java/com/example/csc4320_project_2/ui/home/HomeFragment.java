package com.example.csc4320_project_2.ui.home;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.csc4320_project_2.R;
import com.example.csc4320_project_2.sqlite.DatabaseTrack;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MediaPlayer media_player = null;

    // Enum to handle playback
    public enum PlaybackStatus {INITIAL, IS_PLAYING, IS_PAUSED, STOPPED }
    PlaybackStatus audio_button_status = PlaybackStatus.INITIAL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_audio_track);
        TextView track_name = root.findViewById(R.id.text_audio_track);
        TextView audio_length_text = root.findViewById(R.id.text_audio_duration);
        audio_length_text.setVisibility(View.VISIBLE);

        // Thread to monitor audio playback:
        // It basically waits loops until Audio playback starts, and
        new Thread(new Runnable() {
            @Override
            public void run() {
                long max_audio_duration = -1;
                long current_audio_position = -1;
                while (true) {
                    while (audio_button_status == PlaybackStatus.INITIAL)
                        continue; // Do nothing

                    max_audio_duration = media_player.getDuration();
                    //audio_length_text.setVisibility(View.VISIBLE);
                    String audio_position;
                    // Print the audio elapsed time
                    do {
                        if (audio_button_status == PlaybackStatus.IS_PAUSED)
                            continue;
                        else if (audio_button_status == PlaybackStatus.STOPPED)
                            break;
                        else {
                            current_audio_position = media_player.getCurrentPosition();
                            audio_position = (current_audio_position / 1000) + " / " + (max_audio_duration / 1000);
                            audio_length_text.setText(audio_position);
                        }
                    } while (current_audio_position < max_audio_duration);

                    // Now release the media_player and set everyting back to normal.
                    // Set audio to stopped if it hasn't already
                    audio_button_status = PlaybackStatus.STOPPED;
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
                if (audio_button_status == PlaybackStatus.INITIAL){
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
                    try {
                        track = new DatabaseTrack(getContext());
                    } catch (IOException | TagException | ReadOnlyFileException | CannotReadException | InvalidAudioFrameException e) {
                        e.printStackTrace();
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
                    audio_button.setText(getString(R.string.audio_play));

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

}