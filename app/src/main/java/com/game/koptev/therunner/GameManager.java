package com.game.koptev.therunner;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Koptev Denis on 03.04.2016.
 */

public class GameManager extends Thread {
    private SurfaceHolder holder;
    private boolean terminated;
    private Canvas canvas;
    GameView view;

    public GameManager(SurfaceHolder holder, GameView view) {
        this.holder = holder;
        terminated = false;
        this.view = view;
        Log.e("GameManager: ", "GameManager");
    }

    public void terminate()
    {
        terminated = true;
        Log.e("GameManager: ", "terminate");
    }

    @Override
    public void run() {

        while (!terminated) {
            canvas = null;
            try {
                canvas = holder.lockCanvas(null);
                synchronized (holder) {
                    /*Calculations*/

                    view.counter++;
                    if (view.counter > 20 - view.speed / 2) view.counter = 0;

                    view.bld_xpos -= view.speed;
                    if (view.bld_xpos <= -view.dm.widthPixels) {
                        view.bld_xpos = 0;
                    }

                    /*Start drawing*/
                    view.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            Log.e("GameManager: ", "run");
        }
    }
}
