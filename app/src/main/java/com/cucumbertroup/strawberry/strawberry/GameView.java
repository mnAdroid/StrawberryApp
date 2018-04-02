package com.cucumbertroup.strawberry.strawberry;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.cucumbertroup.strawberry.strawberry.FarmMode.FarmMode;
import com.cucumbertroup.strawberry.strawberry.FightMode.FightMode;

//Die Implementierung von GameView, inkl. SurfaceView (unter anderem um zu malen, vor allem aber um "onTouchEvent" overriden zu können)
//und Runnable (um den GameLoop einfach hier mit reinzupacken)
//#Kp ob das mit dem Loop echt intelligent ist, aber die Alternative lief damals ja auch scheiße
class GameView extends SurfaceView implements Runnable {

    //der Thread, der mehr oder weniger der GameLoop sein wird
    Thread gameThread = null;

    //FPS
    private long fps = 0;

    //Surfaceholder braucht man um im Thread zu malen
    SurfaceHolder ourHolder;

    //flüchtiger Boolean um den Fehler meiner letzten App (hoffentlich) zu verhindern
    volatile boolean isPlaying;

    //In welchem Modus befinden wir uns gerade? (0: Farm, 1: Fight)
    private int gameMode = 0;

    //Standard Canvas und Paint Objekte (um halt zu malen (du dummes Zukunftsich)
    Canvas canvas;
    Paint paint;

    //Context abspeicher, vielleicht klappt es ja
    Context fullContext;

    //Die Größe des Bildschirms damit alles auf jedem Handy gleich aussieht
    private int screenX;
    private int screenY;

    //Klickzähler
    private int clickCount = 0;

    //Gold initialisieren
    private int gold;

    //Musiksettings
    private boolean musicOn;
    private boolean soundOn;

    //Ein paar Spaßbooleans
    private boolean alphaTester;
    private boolean betaTester;

    //Modusvariablen
    FarmMode farmMode;
    FightMode fightMode;
    GlobalVariables globalVariables;

    //Damit nicht ausversehen nicht mehr zwischen Modi gewechselt werden kann
    private boolean modeChanging;

    //Ist das der Start der App oder nur ein resume
    private boolean start;

    //alle 90 Sekunden wird autosafed
    private long autosafeTimer;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    public GameView(Context context, int screenX, int screenY) {
        super(context);

        //Context für GameView abspeichern und zugänglich machen
        fullContext = context;

        //erster start
        start = true;

        //Bildschirmkoordinaten
        this.screenX = screenX;
        this.screenY = screenY;

        //Initialisierung der "Zeichen" Objekte
        ourHolder = getHolder();
        paint = new Paint();

        //Fonteinstellen
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/caladea-bold.ttf");
        paint.setTypeface(customFont);

        modeChanging = false;

        getSharedPreferences();

        globalVariables = GlobalVariables.getInstance(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
        farmMode = new FarmMode(context, screenX, screenY);

        autosafeTimer = System.currentTimeMillis();
    }
    //Der vermutlich wichtigste (und in der letzten App fehleranfälligste) Teil der Gameview
    //run() ist quasi eine Endlosschleife (solange das Game läuft) in dem alles passiert
    @Override
    public void run() {
        //Um Fehler abzufangen die auftreten wenn das hier aufgerufen wird bevor alles geladen hat
        while (isPlaying) {
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();
            //Farmmodus ist an
            if (gameMode == 0) {
                //Dauerhaft gucken ob sich der Gamemodus wechselt
                if (globalVariables != null) {
                    if (gameMode != globalVariables.getGameMode() && gameMode != -1)
                        changeGamemode();
                }

                //update ist quasi das DENKEN in der App
                if (farmMode != null)
                    farmMode.updateFarm(fps);

                //draw ist das ZEICHNEN in der App
                if (farmMode != null)
                    farmMode.drawFarm(ourHolder, canvas, paint);
            }

            //Fightmodus ist an
            if (gameMode == 1) {
                //Dauerhaft gucken ob sich der Gamemodus wechselt
                if (globalVariables != null) {
                    if (globalVariables.getGameMode() == 101) {
                        restartGamemode();
                    }
                    if (gameMode != globalVariables.getGameMode() && gameMode != -1)
                        changeGamemode();

                    if (fightMode != null)
                        fightMode.updateFight();
                    if (fightMode != null)
                        fightMode.drawFight(ourHolder, canvas, paint);
                }
            }
            //FPS Berechnung
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }

            //Autosafe alle 90 Sekunden
            if (System.currentTimeMillis() - autosafeTimer > 90000) {
                setSharedPreferences();
                autosafeTimer = System.currentTimeMillis();
                showToast("Autosave");
            }
        }
    }

    //Gamemode wechseln
    private void changeGamemode() {
        if(gameMode == 0 && !modeChanging) {
            modeChanging = true;
            //run() Funktion stoppen
            gameMode = -1;
            //Farmmode aufräumen
            if (farmMode != null)
                farmMode.recycle();
            farmMode = null;
            //Die Allgemeinen Daten abspeichern
            setSharedPreferences();
            if (globalVariables != null)
                fightMode = new FightMode(fullContext, screenX, screenY);
            else {
                globalVariables = GlobalVariables.getInstance(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
                fightMode = new FightMode(fullContext, screenX, screenY);
            }
            //Auf Fightmode umstellen
            gameMode = 1;
            globalVariables.setGameMode(1);
            getSharedPreferences();
            modeChanging = false;
        }
         else if (gameMode == 1 && !modeChanging){
            modeChanging = true;
            //run() Funktion stoppen
            gameMode = -1;
            //Fightmode aufräumen
            if (fightMode != null)
                globalVariables = fightMode.recycle();
            fightMode = null;
            //Die Allgemeinen Daten abspeichern
            setSharedPreferences();
            if (globalVariables != null)
                farmMode = new FarmMode(fullContext, screenX, screenY);
            else {
                globalVariables = GlobalVariables.getInstance(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
                farmMode = new FarmMode(fullContext, screenX, screenY);
            }
            //Auf Farmmode umstellen
            gameMode = 0;
            globalVariables.setGameMode(0);
            getSharedPreferences();
            modeChanging = false;
        }
    }

    //Gamemode restarten (zb. nach Backupeinspielen)
    private void restartGamemode() {
        if (gameMode == 0) {
            modeChanging = true;
            getSharedPreferences();

            if (farmMode != null)
                farmMode.recycle();
            farmMode = null;
            getSharedPreferences();

            globalVariables = GlobalVariables.getInstance(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
            farmMode = new FarmMode(fullContext, screenX, screenY);

            gameMode = 0;
            globalVariables.setGameMode(0);
            modeChanging = false;
        }

        if (gameMode == 1) {
            modeChanging = true;
            getSharedPreferences();

            this.post(new Runnable() {
                @Override
                public void run() {
                    showToast("Alle Daten heruntergeladen");
                }
            });

            if (fightMode != null)
                fightMode.recycle();
            fightMode = null;
            getSharedPreferences();

            globalVariables = GlobalVariables.getInstance(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
            fightMode = new FightMode(fullContext, screenX, screenY);

            gameMode = 1;
            globalVariables.setGameMode(1);
            getSharedPreferences();
            modeChanging = false;
        }
    }

    //SharedPreferences auslesen
    private void getSharedPreferences() {
        //Allgemeine Daten die beide Modi brauchen können
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        gold = sharedPreferences.getInt("gold", 5);
        clickCount = sharedPreferences.getInt("clicks", 0);
        musicOn = sharedPreferences.getBoolean("musicOn", true);
        soundOn = sharedPreferences.getBoolean("soundOn", true);
        alphaTester = sharedPreferences.getBoolean("alphaTester", true);
        betaTester = sharedPreferences.getBoolean("betaTester", false);

        if (gameMode == 0 && farmMode != null) {
            farmMode.getSharedPreferences();
        }
        if (gameMode == 1 && fightMode != null) {
            fightMode.getSharedPreferences();
        }
    }

    //SharedPreferences wieder sicher verwahren
    private void setSharedPreferences() {
        //AutoSafeTimerReset
        autosafeTimer = System.currentTimeMillis();

        //Allgemeine Daten die beide Modi brauchen können
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("gold", globalVariables.getGold());
        int clicks = globalVariables.getClickCount();
        editor.putInt("clicks", clicks);
        editor.putBoolean("musicOn", globalVariables.getMusicOn());
        editor.putBoolean("soundOn", globalVariables.getSoundOn());
        editor.putBoolean("alphaTester", globalVariables.getAlphaTester());
        editor.putBoolean("betaTester", globalVariables.getBetaTester());

        editor.apply();

        if (gameMode == 0 && farmMode != null) {
            farmMode.setSharedPreferences();
        }
        if (gameMode == 1 && fightMode != null) {
            fightMode.setSharedPreferences();
        }
    }

    //Spiel wird geschlossen / pausiert
    public void pause() {
        isPlaying = false;
        if (farmMode != null) {
            farmMode.recycle();
            farmMode = null;
        }
        if (fightMode != null) {
            globalVariables = fightMode.recycle();
            fightMode = null;
        }
        //Speichern der unabhängigen Werte
        setSharedPreferences();
        try {
            //GameThread (Endlosloop) beenden
            gameThread.join();
        } catch(InterruptedException e) {
            Log.e("Error: ", "joining thread");
        }
    }

    //Spiel wird (wieder) gestartet
    public void resume() {
        isPlaying = true;
        modeChanging = true;
        //Auslesen der Daten vom letzten Game
        getSharedPreferences();
        //Modus starten
        if (gameMode == 0) {
            if (farmMode != null && !start)
                farmMode.getSharedPreferences();
            else {
                if (globalVariables == null)
                    globalVariables = GlobalVariables.getInstance(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
                else {
                    globalVariables.setGold(gold);
                    globalVariables.setClickCount(clickCount);
                    globalVariables.setMusicOn(musicOn);
                    globalVariables.setSoundOn(soundOn);
                    globalVariables.setGameMode(gameMode);
                }
                farmMode = new FarmMode(fullContext, screenX, screenY);
            }
        }
        if (gameMode == 1) {
            if (fightMode != null)
                fightMode.getSharedPreferences();
            else {
                if (globalVariables == null)
                    globalVariables = GlobalVariables.getInstance(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
                else {
                    globalVariables.setGold(gold);
                    globalVariables.setClickCount(clickCount);
                    globalVariables.setMusicOn(musicOn);
                    globalVariables.setSoundOn(soundOn);
                    globalVariables.setGameMode(gameMode);
                }
                fightMode = new FightMode(fullContext, screenX, screenY);
            }
        }

        //Auslesen der Modebezogenen Daten
        getSharedPreferences();

        modeChanging = false;
        start = true;
        //Gamethread starten
        gameThread = new Thread(this);
        gameThread.start();
    }

    //Was passiert wenn man den Touchscreen berührt?
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //Wenn wir im Farmmodus sind
        if (gameMode == 0 && farmMode != null) {
                return farmMode.onTouchFarm(motionEvent);
        }
        //Wenn wir im Fightmodus sind
        if (gameMode == 1 && fightMode != null) {
                return fightMode.onTouchFight(motionEvent);
        }
        return false;
    }

    public void onBackPressed()
    {
        if (globalVariables.getGameMode() == 1)
            globalVariables.setGameMode(0);
        if (globalVariables.getGameMode() == 0 && farmMode != null)
            farmMode.onBackPressed();
    }

    private void showToast(String text) {
        try {
            Toast.makeText(fullContext, text, Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            //unlucky
        }
    }

    //Was passiert bei Geste
    void onGesturePerformed(String name) {
        if (name.equals("circle")) {
            if (fightMode != null) {
                fightMode.fireball();
            }
        }
    }

}