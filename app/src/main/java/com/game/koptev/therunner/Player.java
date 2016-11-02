package com.game.koptev.therunner;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by Koptev Denis on 10.04.2016.
 */

public class Player {

    public static final int id1 = R.raw.sound;
    private static SoundPool sound_pool;
    private static HashMap<Integer, Integer> sound_pool_map;
    private static Context context;

    Player(Context context) {
        this.context = context;
    }

    public static void init() {
        sound_pool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        sound_pool_map = new HashMap<Integer, Integer>(1);
        sound_pool_map.put(id1, sound_pool.load(context, R.raw.sound, 1));
    }

    public static void play(int id) {
        if (sound_pool == null || sound_pool_map == null) {
            return;
        }
        float volume = 1;
        sound_pool.play(R.raw.sound, volume, volume, 1, 0, 1f);
    }

    public static void stop(int id) {
        if (sound_pool != null && sound_pool_map != null) {
            sound_pool.stop(sound_pool_map.get(id));
        }
    }
}
