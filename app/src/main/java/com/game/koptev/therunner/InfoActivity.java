package com.game.koptev.therunner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InfoActivity extends Activity {

    private VideoView video_view;
    private TextView score_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        video_view = (VideoView) findViewById(R.id.videoView);
        video_view.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        video_view.setLeft(0);
        video_view.setRight(getResources().getDisplayMetrics().widthPixels);
        video_view.setTop(0);
        video_view.setBottom(getResources().getDisplayMetrics().heightPixels);
        video_view.start();

        score_text = (TextView)findViewById(R.id.score_text);

        int score = 0;
        char[] buff = new char[10];
        String s_score;
        try {
            FileInputStream fIn = openFileInput("scores.txt");
            InputStreamReader isr = new InputStreamReader(fIn);
            isr.read(buff);
            s_score = new String(buff);
        } catch (FileNotFoundException e) {
            s_score = "Not found";
            e.printStackTrace();
        } catch (IOException e) {
            s_score = "Exception";
            e.printStackTrace();
        }
        score_text.setText("HIGHSCORE: " + s_score);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
