package com.game.koptev.therunner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity  {

    private Intent game_intent;
    private Intent info_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game_intent = new Intent(this, GameActivity.class);
        info_intent = new Intent(this, InfoActivity.class);

        Button start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(game_intent);
                finish();
            }
        });

        Button info_button = (Button) findViewById(R.id.info_button);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(info_intent);
                finish();
            }
        });

        Button exit_button = (Button) findViewById(R.id.exit_button);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        VideoView video_view = (VideoView) findViewById(R.id.videoView);
        video_view.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        video_view.setLeft(0);
        video_view.setRight(getResources().getDisplayMetrics().widthPixels);
        video_view.setTop(0);
        video_view.setBottom(getResources().getDisplayMetrics().heightPixels);
        video_view.start();

        Log.e("MainActivity: ", "onCreate");

        String s_score = new String("0");
        try {
            FileInputStream fIn = openFileInput("scores.txt");
            fIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                FileOutputStream fOut = openFileOutput("scores.txt", MODE_WORLD_READABLE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(s_score);
                osw.flush();
                osw.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
