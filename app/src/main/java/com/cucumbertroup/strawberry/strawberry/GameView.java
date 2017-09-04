package com.cucumbertroup.strawberry.strawberry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Created by Max on 28.08.2017.
 */
//Die Implementierung von GameView, inkl. SurfaceView (unter anderem um zu malen, vor allem aber um "onTouchEvent" overriden zu können)
//und Runnable (um den GameLoop einfach hier mit reinzupacken)
//#Kp ob das mit dem Loop echt intelligent ist, aber die Alternative lief damals ja auch scheiße
class GameView extends SurfaceView implements Runnable {

    //der Thread, der mehr oder weniger der GameLoop sein wird
    Thread gameThread = null;

    //Surfaceholder braucht man um im Thread zu malen
    SurfaceHolder ourHolder;

    //flüchtiger Boolean um den Fehler meiner letzten App (hoffentlich) zu verhindern
    volatile boolean isPlaying;

    //Standard Canvas und Paint Objekte (um halt zu malen (du dummes Zukunftsich)
    Canvas canvas;
    Paint paint;

    //FPS Berechnung damit jede Animation (falls je vorhanden :D) auf jeder Hardware gleich schnell geschieht
    private long fps;
    private long timeThisFrame;

    //Die Größe des Bildschirms damit alles auf jedem Handy gleich aussieht
    private int screenX;
    private int screenY;

    //Klickzähler
    private int clickCount = 0;

    //Zustand (0 = Sähen, 1 = Wachsen, 2 = Ernten)
    private int zustand = 0;

    //Ort der letzten Berührung auf dem Bildschirm
    private float touchX1, touchX2;
    private float deltaX;
    //Wie "stark" der Wisch sein muss, damit ich handel
    static final int MIN_DISTANCE = 200;

    //Erdbeeren Array (bis 240)
    Strawberry[] strawberries;
    private int numStrawberries = 0;
    private int numAecker;

    //Gold initialisieren
    private int gold;

    //Bilder initialisieren
    Bitmap bitmapMainButtons;

    //Musik einlesen
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
    private int gold1 = -1;
    private int plop1 = -1;
    private int plop2 = -1;
    private int plop3 = -1;
    private int plop4 = -1;
    private int plop5 = -1;
    private int plop6 = -1;
    private int plop7 = -1;
    private int plop8 = -1;
    private int plop9 = -1;
    private MediaPlayer backgroundloop1;

    //Musiksettings
    private boolean musicOn;
    private boolean soundOn;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können
    public GameView(Context context, int screenX, int screenY) {
        super(context);

        //Initialisierung der "Zeichen" Objekte
        ourHolder = getHolder();
        paint = new Paint();

        //Um die Größe des Bildschirms auch hier zu kennen
        this.screenX = screenX;
        this.screenY = screenY;

        //Die Buttons Shop und Einstellungen anzeigen
        bitmapMainButtons = BitmapFactory.decodeResource(this.getResources(), R.drawable.mainbuttons);
        //Bild an Größe des Bildschirms anpassen
        bitmapMainButtons = Bitmap.createScaledBitmap(bitmapMainButtons, screenX/3, screenY/9, false);

        //Musik einlesen
        initialiseSound(context);

    }
    //Der vermutlich wichtigste (und in der letzten App fehleranfälligste) Teil der Gameview
    //run() ist quasi eine Endlosschleife (solange das Game läuft) in dem alles passiert
    @Override
    public void run() {
        //Um Fehler abzufangen die auftreten wenn das hier aufgerufen wird bevor alles geladen hat
        while (isPlaying) {
            //Derzeitige Zeit (für FPS Berechnung)
            long startFrameTime = System.currentTimeMillis();

            //update ist quasi das DENKEN in der App
            update();

            //draw ist das ZEICHNEN in der App
            draw();

            //FPS Berechung (Da Millisekunden -> 1000)
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            //Standardfehlerabfangen
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }

    }
    //update ist quasi das DENKEN in der App
    private void update() {
        //Erdbeeren wachsen hier automatisch durch Zeit
        for(int i = 0; i < numStrawberries; i++) {
            strawberries[i].update();
        }
    }

    //draw ist das ZEICHNEN in der App
    private void draw() {
        //Standardfehlerabfangen
        if(ourHolder.getSurface().isValid()) {
            //canvas wird das Zeichenobjekt
            canvas = ourHolder.lockCanvas();

            //Hintergrund malen
            switch (zustand){
                case 0:
                    canvas.drawColor(Color.argb(255, 104, 211, 86));
                    break;
                case 1:
                    canvas.drawColor(Color.argb(255, 26, 128, 182));
                    break;
                case 2:
                    canvas.drawColor(Color.argb(255, 241, 238, 0));
                    break;
            }


            //Pinselfarbe wählen(bisher nur für den Text)
            paint.setColor(Color.argb(255, 249, 129, 0));

            //Textgröße
            paint.setTextSize(50);

            //Derzeitigen FPS malen
            canvas.drawText("FPS: " + fps, 20, 40, paint);

            //Klickcounter malen
            canvas.drawText("Clicks: " + clickCount, 20, 90, paint);

            //Zustand als Text ausgeben
            switch(zustand) {
                case 0:
                    canvas.drawText("Zustand: Aussähen", 20, 140, paint);
                    break;
                case 1:
                    canvas.drawText("Zustand: Wachsen", 20, 140, paint);
                    break;
                case 2:
                    canvas.drawText("Zustand: Ernten", 20, 140, paint);
                    break;
                default:
                    canvas.drawText("Something went wrong :D", 20, 140, paint);
                    break;
            }
            //Bewegungsrichtung als Text ausgeben
            canvas.drawText("Delta X: " + deltaX, 20, 190, paint);

            //Anzahl der Erdbeeren
            canvas.drawText("Erdbeeren: " + numStrawberries, 20, 240, paint);

            //Anzahl Aecker
            canvas.drawText("Äcker: " + numAecker, 20, 290, paint);

            //Wie viel Gold haben wir eigentlich?
            canvas.drawText("Gold: " + gold, 20, 340, paint);

            //Test Wachsstatus Erdbeere 1
            if(numStrawberries > 0)
                canvas.drawText("Wachsstatus Erdbeere 1: " + strawberries[0].getWachsStatus(), 20, 390, paint);

            //Bilder malen
            canvas.drawBitmap(bitmapMainButtons, (int) (screenX/1.6), screenY/96, paint); //noch ohne Anpassung für jede Screengröße

            //Alles auf den Bildschirm malen
            //Und Canvas wieder freilassen (um Fehler zu minimieren(das könnte sogar der Fehler meiner ersten App gewesen sein))
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }
    //SharedPreferences wieder sicher verwahren
    private void setSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("StrawberrySettings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numStrawberries", numStrawberries);
        editor.putInt("numAecker", numAecker);
        editor.putInt("gold", gold);
        editor.putInt("clicks", clickCount);
        editor.putBoolean("musicOn", musicOn);
        editor.putBoolean("soundOn", soundOn);

        //Hier kommen alle derzeitigen Erdbeeren rein um gespeichert zu werden
        String strawberryStatus = "";
        //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
        for(int i = 0; i < (numAecker*16); i++) {
            strawberryStatus += strawberries[i].getWachsStatus();
            strawberryStatus += "a";
            strawberryStatus += strawberries[i].getAcker();
            strawberryStatus += "a";
            strawberryStatus += strawberries[i].getTimeThisFruit();
            strawberryStatus += "a";
        }
        editor.putString("strawberryStatus", strawberryStatus);

        editor.commit();
    }

    //Spiel wird geschlossen / pausiert
    public void pause() {
        isPlaying = false;
        //Speichern der unabhängigen Werte
        setSharedPreferences();
        //Musik pausieren
        backgroundloop1.pause();

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

        //Auslesen der Daten vom letzten Game
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("StrawberrySettings", 0);
        numStrawberries = sharedPreferences.getInt("numStrawberries", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        gold = sharedPreferences.getInt("gold", 0);
        clickCount = sharedPreferences.getInt("clicks", 0);
        String strawberryStatus = sharedPreferences.getString("strawberryStatus", "");
        musicOn = sharedPreferences.getBoolean("musicOn", true);
        soundOn = sharedPreferences.getBoolean("soundOn", true);

        strawberries = new Strawberry[numAecker*16];

        //um keine IndexoutofBoundException zu bekommen
        if(!(strawberryStatus.equals(""))) {
            //Initialisierung der gespeicherten Erdbeeren: 1. String auseinander nehmen, 2. aus den Daten auslesen
            //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
            String[] strawberryStatusStrings = strawberryStatus.split("a");
            int stringsCounter = 0;
            for (int i = 0; i < (numAecker * 16); i++) {
                strawberries[i] = new Strawberry(Integer.parseInt(strawberryStatusStrings[stringsCounter]), Integer.parseInt(strawberryStatusStrings[stringsCounter + 1]), Long.parseLong(strawberryStatusStrings[stringsCounter + 2]));
                stringsCounter += 3;
            }
        }
        else {
            for (int i = 0; i < (numAecker * 16); i++) {
                strawberries[i] = new Strawberry(1);
            }
        }

        //Hintergrundmusik anschalten
        playSound(0);

        gameThread = new Thread(this);
        gameThread.start();
    }

    //Was passiert wenn man den Touchscreen berührt?
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //Alle Arten von Bewegung (auf dem Screen) die man bearbeiten will
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //Spieler berührt den Bildschirm
            case MotionEvent.ACTION_DOWN:
                //Wo befanden wir uns am Anfang?
                touchX1 = motionEvent.getX();
                //Wir haben geklickt, in einem Klickergame müssen wir doch mit der Info irgendwas machen oder? :D
                gotClicked();
                break;

            //Spieler "verlässt" den Bildschirm
            case MotionEvent.ACTION_UP:
                //In welche Richtung hat sich der Finger bewegt?
                touchX2 = motionEvent.getX();
                //Differenz der beiden Werte
                deltaX = touchX2 - touchX1;
                //Abfrage in welche Richtung es sich bewegt hat #deltaX < 0: nach rechts | deltaX > 0: nach links
                if(deltaX < 0) {
                    if (Math.abs(deltaX) > MIN_DISTANCE && zustand!= 2) {
                        zustand++;
                    }
                }
                if(deltaX > 0) {
                    if (Math.abs(deltaX) > MIN_DISTANCE && zustand!= 0) {
                        zustand--;
                    }
                }
                break;
        }
        return true;

    }

    //Was passiert wenn der Spieler klickt?
    private void gotClicked() {
        clickCount++;
        switch(zustand) {
            case 0:
                //Aussähen: Prüfen ob noch Platz ist, wenn ja: Aussähen.
                if(numStrawberries < (numAecker*16)) {
                    for(int i = 0; i < numAecker*16; i++) {
                        if (strawberries[i].getWachsStatus() <= -1) {
                            strawberries[i].setStrawberry();
                            numStrawberries++;
                            playSound(1);
                            break;
                        }
                    }
                }
                break;
            case 1:
                //Wachsen: Alles wächst viel schneller, aber es wächst auch schon so langsam.
                for(int i = 0; i < numAecker*16; i++) {
                    strawberries[i].incrWachsStatus(1);
                    playSound(2);
                }
                break;
            case 2:
                //Ernten: Prüfen ob Erdbeeren fertig, wenn ja: Gold bekommen und Platz machen zum Aussähen
                for(int i = 0; i < numAecker*16; i++) {
                    if (strawberries[i].getWachsStatus() >= 5) {
                        strawberries[i].resetStrawberry();
                        numStrawberries--;
                        gold += 10;
                        playSound(3);
                        break;
                    }
                }
                break;
        }
    }

    private void playSound(int whichOne) {
        boolean ticktack = true; //für den Uhr Sound
        //whichone Legende: 0 -> Hintergrundmusik; 1 -> Sähsound; 2 -> Uhr Sound; 3 -> Erntesound; 4 -> Buttonklick; 5 -> Geld

        //Zufallszahl generieren um die aussäh und erntegeräusche abwechslungsreicher zu machen
        Random random = new Random();
        int randomInt = 1;
        for (int i = 1; i <= 9; i++) {
            randomInt = random.nextInt(10);
        }
        switch (whichOne) {
            case 0:
                if (musicOn)
                    backgroundloop1.start();
                break;
            case 1:
                if (soundOn) {
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
                if (soundOn) {
                    if (ticktack) {
                        soundPool.play(clock1, 1, 1, 0, 0, 1);
                        ticktack = false;
                    } else {
                        soundPool.play(clock2, 1, 1, 0, 0, 1);
                        ticktack = true;
                    }
                }
                break;
            case 3:
                if (soundOn) {
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
                if (soundOn)
                    soundPool.play(click1, 1, 1, 0, 0, 1);
                break;
            case 5:
                if (soundOn)
                    soundPool.play(gold1, 1, 1, 0, 0, 1);
                break;
        }
    }

    //Musik einlesen
    private void initialiseSound(Context context) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //Musik tatsächlich einladen
            descriptor = assetManager.openFd("click1.wav");
            click1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("clock1.wav");
            clock1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("clock2.wav");
            clock2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt1.wav");
            dirt1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt2.wav");
            dirt2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt3.wav");
            dirt3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt4.wav");
            dirt4 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt5.wav");
            dirt5 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt6.wav");
            dirt6 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt7.wav");
            dirt7 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt8.wav");
            dirt8 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt9.wav");
            dirt9 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("gold1.wav");
            gold1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop1.wav");
            plop1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop2.wav");
            plop2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop3.wav");
            plop3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop4.wav");
            plop4 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop5.wav");
            plop5 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop6.wav");
            plop6 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt5.wav");
            dirt5 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("dirt6.wav");
            dirt6 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop7.wav");
            plop7 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop8.wav");
            plop8 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop9.wav");
            plop9 = soundPool.load(descriptor, 0);

            //Man muss MediaPlayer benutzen um vernünftig Hintergrundmusik abzuspielen...
            backgroundloop1 = MediaPlayer.create(context, R.raw.gameloop1);
            //Hintergrundmusik auf Loop stellen
            backgroundloop1.setLooping(true);
        } catch (IOException e) {
            //Errormessage
            Log.e("error", "failed to load sound files");
        }
    }

}