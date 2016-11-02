package com.game.koptev.therunner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Koptev Denis on 03.04.2016.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    /* Class for pair of x,y for barrels' positions */
    class Position {
        public int x, y;
        Position() {
            x = 0; y = 0;
        }
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /* ELEMENTS */
    private Bitmap actor1;
    private Bitmap actor2;
    private Bitmap bg;
    private Bitmap buildings;
    private Bitmap ground;
    private Bitmap barrel;
    private Bitmap press_back;

    MediaPlayer mp;

    Paint p;

   // private Position[] barrels; /*positions of barrels*/

    private Vector<Position> barrels = new Vector<Position>();

    private GameManager gameThread;
    DisplayMetrics dm;

    /* FUNCTIONAL COUNTERS AND FLAGS */
    public boolean jump = false; /*jump signal*/
    public boolean up = false; /*jump direction*/
    public byte speed = 5;
    public byte counter = 0; /*for player animation*/
    public int score = 0;

    /* ELEMENTS POSITIONS */
    public int actor_xpos = 0;
    public int actor_ypos = 0;
    public int bld_xpos = 0;
    public int bld_ypos = 0;
    public int bg_xpos = 0;
    public int bg_ypos = 0;
    public int grnd_xpos = 0;
    public int grnd_ypos = 0;
    public int brl_ypos;
    public int offset = (20 + speed*10);
    public int offs_time = 0;

    public Random rand; /*for barrels' appearing*/

    /******* METHODS *******/

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        rand = new Random();

        /* GET RESOURCES */
        p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextSize(55f);
        //barrels = new Position[10];
        barrel = BitmapFactory.decodeResource(getResources(), R.drawable.barrel);
        actor1 = BitmapFactory.decodeResource(getResources(), R.drawable.actor_1);
        actor2 = BitmapFactory.decodeResource(getResources(), R.drawable.actor_2);
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg_light);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.grnd_light);
        buildings = BitmapFactory.decodeResource(getResources(), R.drawable.buildings);
        press_back = BitmapFactory.decodeResource(getResources(), R.drawable.pressback_light);

        /* SIZING */
        dm = getResources().getDisplayMetrics();
        bg = Bitmap.createScaledBitmap(bg, dm.widthPixels, dm.heightPixels, false);
        ground = Bitmap.createScaledBitmap(ground, dm.widthPixels,
                (int) (1.5 * ground.getHeight() * dm.heightPixels / ground.getWidth()), false);
        buildings = Bitmap.createScaledBitmap(buildings, dm.widthPixels,
                2 * buildings.getHeight() * dm.heightPixels / buildings.getWidth(), false);
        barrel = Bitmap.createScaledBitmap(barrel, (int)(0.5f*actor1.getWidth()),
                (int)(0.65f*actor1.getHeight()), false);
        press_back = Bitmap.createScaledBitmap(press_back, dm.widthPixels, dm.heightPixels, false);

        /* POSITIONING */
        actor_xpos = dm.widthPixels/2 - actor1.getWidth()/2;
        actor_ypos = dm.heightPixels/2;
        bld_xpos = 0;
        bld_ypos = dm.heightPixels- ground.getHeight()- buildings.getHeight();
        grnd_xpos = 0;
        grnd_ypos = dm.heightPixels- ground.getHeight();
        offset *= dm.heightPixels/1080;
        brl_ypos = actor_ypos + Math.abs(actor1.getHeight() - barrel.getHeight());

        mp = MediaPlayer.create(context, R.raw.sound);
        mp.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.e("GameView: ", "surfaceDestroyed");
        gameThread.terminate();
        while (true)
        {
            Log.e("GameView: ", "surfaceDestroyed");
            try {
                gameThread.join();
                break;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.e("GameView: ", "surfaceCreated");
        if (gameThread != null)
        {
            gameThread.interrupt();
        }
        gameThread = new GameManager(getHolder(), this);
        gameThread.start();
        Log.e("GameView: ", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.e("GameView: ", "surfaceChanged");
    }

    @Override
    public void draw(Canvas canvas) {
        Log.e("GameView: ", "draw");

        /* DO NOT DRAW BEFORE IT! */
        if (canvas != null) {
            super.draw(canvas);

            /* DRAWING ACTIONS */
            /* BACKGROUND */
            canvas.drawBitmap(bg, bg_xpos, bg_ypos, null);

            /* GROUND */
            canvas.drawBitmap(ground, grnd_xpos, grnd_ypos, null);
            /*canvas.drawBitmap(ground, grnd_xpos + dm.widthPixels, grnd_ypos, null);
            grnd_xpos -= 4 * speed;
            if (grnd_xpos <= -dm.widthPixels) {
                grnd_xpos = 0;
            }*/

            /* BUILDINGS */
            canvas.drawBitmap(buildings, bld_xpos, bld_ypos, null);
            canvas.drawBitmap(buildings, bld_xpos + dm.widthPixels, bld_ypos, null);

            /* BARRELS */
            for (int i = 0; i < barrels.size(); ++i) {
                if (i < 4) {
                    if (collision_case(canvas, i)) {
                        return; // If actor collided with barrel
                    }
                }
                canvas.drawBitmap(barrel, barrels.elementAt(i).x -= 4 * speed, brl_ypos, null);
                if (barrels.elementAt(i).x < -barrel.getWidth()) { // We don't need this barrel anymore
                    delete_barrel(i);
                    i--;
                }
            }
            request_barrel(); // Ask for new barrel. Will be built if it's good time for it

            /* ACTOR */
            jump_processing(); // Make jump if it is activated
            /* animation */
            if (counter < 10 - speed / 2 || jump) {
                canvas.drawBitmap(actor1, actor_xpos, actor_ypos, null);
            } else {
                canvas.drawBitmap(actor2, actor_xpos, actor_ypos, null);
            }

            /* SCORE */
            canvas.drawText("SCORE: " + Integer.toString(score), 100, 100, p);

            /* MUSIC */
            if (!mp.isPlaying()) {
                mp.start();
            }
        }
    }

    public void make_jump() {
        jump = true;
        up = true;
    }

    private void save_score() {
        char[] buff = new char[1];
        String s_score;
        try {
            FileInputStream fIn = getContext().openFileInput("scores.txt");
            InputStreamReader isr = new InputStreamReader(fIn);
            isr.read(buff);
            isr.close();
            s_score = new String(buff);
        } catch (FileNotFoundException e) {
            s_score = new String("Not found");
            e.printStackTrace();
        } catch (IOException e) {
            s_score = new String("Error");
            e.printStackTrace();
        }

        try {
            if (Integer.parseInt(s_score) < score) {
                try {
                    FileOutputStream fOut = getContext().openFileOutput("scores.txt", 1);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write(Integer.toString(score));
                    osw.flush();
                    osw.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) {
        }
    }

    private boolean collision_case(Canvas canvas, int brl_ind) {
        if (barrels.elementAt(brl_ind).x - 100 > actor_xpos - barrel.getWidth() &&
                barrels.elementAt(brl_ind).x + 130 < actor_xpos + actor1.getWidth() &&
                actor_ypos + actor1.getHeight() - 80 > brl_ypos) {
            for (int w = 0; w < 100000; ++w) {
                System.out.println("Hello");
            }
            canvas.drawBitmap(press_back, 0, 0, null);
            p.setColor(Color.WHITE);
            p.setTextSize(100f);
            canvas.drawText("SCORE: " + Integer.toString(score), 100, 130, p);
            save_score(); // write to file if it is highscore
            gameThread.terminate();
            score = 0;
            return true;
        }
        return false;
    }

    private void request_barrel() {
        if (rand.nextInt() % 25 == 0 && barrels.size() < 10) {
            if (barrels.size() != 0) {
                if (barrels.lastElement().x < dm.widthPixels - barrel.getWidth() * 5) {
                    barrels.add(new Position(dm.widthPixels, brl_ypos));
                }
            } else {
                barrels.add(new Position(dm.widthPixels, brl_ypos));
            }
        }
    }

    private void delete_barrel(int brl_ind) {
        barrels.removeElementAt(brl_ind);
        score++;
        if (score == 20 || score == 50 || score == 100) speed++;
    }

    private void jump_processing() {
        if (jump == true && counter % 3 != 0) {
            if (up == true) {
                offset -= speed - 2 + speed / 2;
                offs_time++;
                actor_ypos -= offset;
                if (offset <= 0) {
                    up = false;
                    offset = 0;
                }
            } else {
                offset += speed;
                offs_time--;
                if (offs_time == 0) {
                    jump = false;
                    actor_ypos = dm.heightPixels / 2;
                    offset = (20 + speed * 10) * dm.heightPixels / 1080;
                } else if (offset < 20 + speed * 10)
                    actor_ypos += offset;
            }
        }
    }

}
