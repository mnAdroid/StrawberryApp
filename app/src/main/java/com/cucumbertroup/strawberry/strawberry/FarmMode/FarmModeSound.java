package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;


import com.cucumbertroup.strawberry.strawberry.GlobalVariables;
import com.cucumbertroup.strawberry.strawberry.R;

import java.io.IOException;
import java.util.Random;

class FarmModeSound {
    //Singleton
    private static FarmModeSound instance;
    private GlobalVariables globalVariables;

    //Musik initialisieren
    private SoundPool soundPool;
    private int click1 = -1;
    private int clock1 = -1;
    private int clock2 = -1;
    private int dirt1 = -1;
    private int dirt2 = -1;
    private int dirt3 = -1;
    private int dirt4 = -1;
    private int dirt5 = -1;
    private int dirt6 = -1;
    private int dirt7 = -1;
    private int dirt8 = -1;
    private int dirt9 = -1;
    private int plop1 = -1;
    private int plop2 = -1;
    private int plop3 = -1;
    private int plop4 = -1;
    private int plop5 = -1;
    private int plop6 = -1;
    private int plop7 = -1;
    private int plop8 = -1;
    private int plop9 = -1;
    private int gold1 = -1;
    private MediaPlayer backgroundloop1; //Farmmusik
    private boolean ticktack = true; //für den Uhr Sound
    private boolean loaded = false; //ist der soundPool fertig

    private FarmModeSound() {
        globalVariables = GlobalVariables.getInstance();
    }
    static synchronized FarmModeSound getInstance() {
        if (FarmModeSound.instance == null) {
            FarmModeSound.instance = new FarmModeSound();
        }
        return FarmModeSound.instance;
    }

    //Musik einlesen
    private void initialiseSound(Context fullContext) {
        //In neuen Versionen soll man das halt jetzt so machen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .build();
        } else {
            //Aber ich will die alten Versionen trz nicht verlieren deshalb lassen wir das mal drin
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }

        //Ist der SoundPool fertig geladen
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                loaded = true;
            }
        });

        try {
            AssetManager assetManager = fullContext.getAssets();
            AssetFileDescriptor descriptor;

            //Musik tatsächlich einlesen
            descriptor = assetManager.openFd("click1.wav");
            click1 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("clock1.wav");
            clock1 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("clock2.wav");
            clock2 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt1.wav");
            dirt1 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt2.wav");
            dirt2 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt3.wav");
            dirt3 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt4.wav");
            dirt4 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt5.wav");
            dirt5 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt6.wav");
            dirt6 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt7.wav");
            dirt7 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt8.wav");
            dirt8 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("dirt9.wav");
            dirt9 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop1.wav");
            plop1 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop2.wav");
            plop2 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop3.wav");
            plop3 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop4.wav");
            plop4 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop5.wav");
            plop5 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop6.wav");
            plop6 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop7.wav");
            plop7 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop8.wav");
            plop8 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("plop9.wav");
            plop9 = soundPool.load(descriptor, 1);

            descriptor = assetManager.openFd("gold1.wav");
            gold1 = soundPool.load(descriptor, 1);
        } catch (IOException e) {
            //Errormessage
            Log.e("FarmSoundError", "failed to load sound files");
        }
    }

    //Hintergrundmusikverwaltung
    private synchronized void backgroundMusicPlayer(Context fullContext) {
        try {
            //Beim ersten Start der Farmmusik
            if (backgroundloop1 == null && globalVariables.getMusicOn()) {
                backgroundloop1 = MediaPlayer.create(fullContext, R.raw.gameloop1);
                backgroundloop1.setLooping(true);
                backgroundloop1.start();
            }
            //Wenn wir nachträglich wieder in den Farmmodus wechseln
            if (backgroundloop1 != null && !backgroundloop1.isPlaying() && globalVariables.getMusicOn()) {
                backgroundloop1.start();
            }
        } catch (IllegalStateException e) {
            Log.d("gamemode1 Error", e.toString());
        }
    }

    //Jede Art von Sound abspielen
    void playSound(int whichOne, Context context) {
        //whichone Legende: 0 -> Hintergrundmusik; 1 -> Sähsound; 2 -> Uhr Sound; 3 -> Erntesound; 4 -> Buttonklick; 5 -> Geld

        if (soundPool == null || !loaded) {
            initialiseSound(context);
        }
        //Zufallszahl generieren um die aussäh und erntegeräusche abwechslungsreicher zu machen
        Random random = new Random();
        int randomInt = 1;

        for (int i = 1; i <= 9; i++) {
            randomInt = random.nextInt(10);
        }
        switch (whichOne) {
            case 0:
                if (globalVariables.getMusicOn())
                    backgroundMusicPlayer(context);
                break;
            case 1:
                if (globalVariables.getSoundOn()) {
                    switch (randomInt) {
                        case 1:
                            soundPool.play(dirt1, 1, 1, 0, 0, 1);
                            break;
                        case 2:
                            soundPool.play(dirt2, 1, 1, 0, 0, 1);
                            break;
                        case 3:
                            soundPool.play(dirt3, 1, 1, 0, 0, 1);
                            break;
                        case 4:
                            soundPool.play(dirt4, 1, 1, 0, 0, 1);
                            break;
                        case 5:
                            soundPool.play(dirt5, 1, 1, 0, 0, 1);
                            break;
                        case 6:
                            soundPool.play(dirt6, 1, 1, 0, 0, 1);
                            break;
                        case 7:
                            soundPool.play(dirt7, 1, 1, 0, 0, 1);
                            break;
                        case 8:
                            soundPool.play(dirt8, 1, 1, 0, 0, 1);
                            break;
                        case 9:
                            soundPool.play(dirt9, 1, 1, 0, 0, 1);
                            break;
                    }
                }
                break;
            case 2:
                if (globalVariables.getSoundOn()) {
                    if (ticktack) {
                        Log.d("instance", "" + instance);
                        Log.d("clock1", "" + clock1);
                        soundPool.play(clock1, 1, 1, 0, 0, 1);
                        ticktack = false;
                    } else {
                        Log.d("clock2", "" + clock2);
                        soundPool.play(clock2, 1, 1, 0, 0, 1);
                        ticktack = true;
                    }
                }
                break;
            case 3:
                if (globalVariables.getSoundOn()) {
                    switch (randomInt) {
                        case 1:
                            soundPool.play(plop1, 1, 1, 0, 0, 1);
                            break;
                        case 2:
                            soundPool.play(plop2, 1, 1, 0, 0, 1);
                            break;
                        case 3:
                            soundPool.play(plop3, 1, 1, 0, 0, 1);
                            break;
                        case 4:
                            soundPool.play(plop4, 1, 1, 0, 0, 1);
                            break;
                        case 5:
                            soundPool.play(plop5, 1, 1, 0, 0, 1);
                            break;
                        case 6:
                            soundPool.play(plop6, 1, 1, 0, 0, 1);
                            break;
                        case 7:
                            soundPool.play(plop7, 1, 1, 0, 0, 1);
                            break;
                        case 8:
                            soundPool.play(plop8, 1, 1, 0, 0, 1);
                            break;
                        case 9:
                            soundPool.play(plop9, 1, 1, 0, 0, 1);
                            break;
                    }
                }
                break;
            case 4:
                if (globalVariables.getSoundOn())
                    soundPool.play(click1, 1, 1, 0, 0, 1);
                break;
            case 5:
                if (globalVariables.getSoundOn())
                    soundPool.play(gold1, 1, 1, 0, 0, 1);
                break;
        }
    }

    void recycle() {
        //Audio releasen
        if (backgroundloop1 != null)
            backgroundloop1.pause();
        if (soundPool != null) {
            soundPool.autoPause();
            soundPool.release();
            soundPool = null;
        }
        loaded = false;
        instance = null;
    }

    void pauseMusic() {
        if (backgroundloop1 != null && backgroundloop1.isPlaying() && !globalVariables.getMusicOn()) {
            backgroundloop1.pause();
        }
    }
}
