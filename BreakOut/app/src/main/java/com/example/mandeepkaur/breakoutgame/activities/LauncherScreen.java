package com.example.mandeepkaur.breakoutgame.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.mandeepkaur.breakoutgame.R;

import pl.droidsonroids.gif.GifTextView;

public class LauncherScreen extends AppCompatActivity {

    GifTextView start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_screen);

        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.theme);
        mediaPlayer.start();

        start = (GifTextView)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherScreen.this, BreakoutGame.class);
                mediaPlayer.stop();
                startActivity(intent);
            }
        });
    }
}
