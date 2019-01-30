package com.example.project.myapplication.music;
/*
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.example.project.myapplication.R;

import static com.example.project.myapplication.com.example.project.myapplication.MainActivity.mySound;


public class MusicActivity extends AppCompatActivity {
    public static boolean isMusicOn=false;
    public static Button playstopBtn;


    public void turnOnMusic(){
        try {
            if(mySound==null){
                mySound = MediaPlayer.create(this, R.raw.com.example.project.myapplication.music);
                mySound.start();
                isMusicOn = true;
                playstopBtn.setText("Stop");
            }

        }
        catch(Exception x){
            Toast.makeText(this, x.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void turnOffMusic() {
        try {
            mySound.release();
            mySound = null;
            isMusicOn = false;
            playstopBtn.setText("Play");
        } catch (Exception x) {
            Toast.makeText(this, x.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
*/