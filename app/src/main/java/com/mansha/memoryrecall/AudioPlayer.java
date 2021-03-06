package com.mansha.memoryrecall;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class AudioPlayer {
    private String audioFile;
    private View view;
    MediaPlayer mp;


    public AudioPlayer(View view){
        this.view = view;
    }

    public void setAudioFile(String audioFile){
        this.audioFile = audioFile;

    }

    public void playAudio(){
        try {
            //mp = MediaPlayer.create(this.view.getContext(), R.raw.cat_meow);
            MediaPlayer mPlayer = new MediaPlayer();
            mPlayer.setDataSource(audioFile);
            mPlayer.prepare();
            mPlayer.start();

            //mp.setLooping(true);

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mp = null;
                }
            });
        } catch (IOException e){
        }

    }

    public void releaseAudioPlayer(){
        if (mp != null) {
//            mp.stop();
            mp.release();
            mp = null;
        }
    }




}
