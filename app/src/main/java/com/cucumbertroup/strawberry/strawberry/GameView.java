package com.cucumbertroup.strawberry.strawberry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Arrays;
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

    //In welchem Modus befinden wir uns gerade? (0: Farm, 1: Fight)
    private int gameMode = 0;

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
    private float touchX1, touchX1down, touchX2, touchY1;
    //Abstand der letzten Bewegung auf dem Bildschirm
    private float deltaXmove, deltaXclick;

    //Ort des Hintergrundbildes
    private float backgroundX1;

    //Erdbeeren Array
    Strawberry[] strawberries;
    private int numStrawberries = 0;
    //Anzahl und Preis der Farmfläche
    private int numAecker;
    private int priceAecker;
    //Anzahl und Preis der arbeitenden Gurken
    private int numGurken;
    private int priceGurken;

    //Gold initialisieren
    private int gold;
    //Erdbeerkosten
    private final int STRAWBERRY_PRICE = 1;

    //Bilder initialisieren
    Bitmap bitmapBackgroundColors;

    //Größe der Bilder
    private int imageHeight;
    private int imageWidth;

    //Testbuttons initialisieren
    Bitmap bitmapAckerKaufenButton;
    Bitmap bitmapFightButton;
    Bitmap bitmapGurkeKaufenButton;
    Bitmap bitmapMusikAnAusButton;
    Bitmap bitmapResetButton;

    //Feste Größen speichern
    private int textSize, textX, textY;
    private int bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY;
    private int bitmapFightButtonX, bitmapFightButtonY;
    private int bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY;
    private int bitmapMusikAnAusButtonX, bitmapMusikAnAusButtonY;
    private int bitmapResetButtonX, bitmapResetButtonY;

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
    private MediaPlayer backgroundloop1; //Farmmusik

    //Musiksettings
    private boolean musicOn;
    private boolean soundOn;

    //Ein paar Spaßbooleans
    private boolean alphaTester;
    private boolean betaTester;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    public GameView(Context context, int screenX, int screenY) {
        super(context);

        //Initialisierung der "Zeichen" Objekte
        ourHolder = getHolder();
        paint = new Paint();

        //Um die Größe des Bildschirms auch hier zu kennen
        this.screenX = screenX;
        this.screenY = screenY;

        //Alle Bilder einlesen
        initialiseBitmaps();

        //Musik einlesen
        initialiseSound(context);

        //Textgröße errechnen
        textSize = getScaledBitmapSize(screenX, 1080, 50);
        //Textgröße setzen (möglicherweise muss das wieder zurück in drawFarm nach Hintergrundmalen)
        paint.setTextSize(textSize);
        //Textposition setzen
        textX = getScaledCoordinates(screenX, 1080, 20);
        textY = getScaledCoordinates(screenY, 1920, 50);
        //Font einstellen
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/caladea-bold.ttf");
        paint.setTypeface(customFont);

    }
    //Der vermutlich wichtigste (und in der letzten App fehleranfälligste) Teil der Gameview
    //run() ist quasi eine Endlosschleife (solange das Game läuft) in dem alles passiert
    @Override
    public void run() {
        //Um Fehler abzufangen die auftreten wenn das hier aufgerufen wird bevor alles geladen hat
        while (isPlaying) {
            //Derzeitige Zeit (für FPS Berechnung)
            long startFrameTime = System.currentTimeMillis();

            //Das hier machen wir während des FARMens
            if (gameMode == 0) {

                //update ist quasi das DENKEN in der App
                updateFarm();

                //draw ist das ZEICHNEN in der App
                drawFarm();
            }
            //Das hier machen wir während des FIGHTens
            else {

            }

            //FPS Berechung (Da Millisekunden -> 1000)
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            //Standardfehlerabfangen
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }

    }
    //update ist quasi das DENKEN in der App
    private void updateFarm() {
        //Erdbeeren wachsen hier automatisch durch Zeit
        for(int i = 0; i < numStrawberries; i++) {
            strawberries[i].update();
        }
    }

    //draw ist das ZEICHNEN in der App
    private void drawFarm() {
        //Standardfehlerabfangen
        if(ourHolder.getSurface().isValid()) {
            //canvas wird das Zeichenobjekt
            canvas = ourHolder.lockCanvas();

            //Hintergrund malen
            canvas.drawBitmap(bitmapBackgroundColors, backgroundX1, 0, paint);


            //Pinselfarbe wählen(bisher nur für den Text)
            paint.setColor(Color.argb(255, 249, 129, 0));

            //Derzeitigen FPS malen
            canvas.drawText("FPS: " + fps, textX, textY, paint);

            //Klickcounter malen
            canvas.drawText("Clicks: " + clickCount, textX, 2*textY, paint);

            //Zustand als Text ausgeben
            switch(zustand) {
                case 0:
                    canvas.drawText("Zustand: Aussähen", textX, 3*textY, paint);
                    break;
                case 1:
                    canvas.drawText("Zustand: Wachsen", textX, 3*textY, paint);
                    break;
                case 2:
                    canvas.drawText("Zustand: Ernten", textX, 3*textY, paint);
                    break;
                default:
                    canvas.drawText("Something went wrong :D", textX, 3*textY, paint);
                    break;
            }

            //Anzahl der Erdbeeren
            canvas.drawText("Erdbeeren: " + numStrawberries, textX, 4*textY, paint);

            //Anzahl Gurken
            canvas.drawText("Gurken: " + numGurken + " | Kosten: " + priceGurken + " Gold", textX, 5*textY, paint);

            //Anzahl Aecker
            canvas.drawText("Äcker: " + numAecker + " | Kosten: " + priceAecker + " Gold", textX, 6*textY, paint);

            //Wie viel Gold haben wir eigentlich?
            canvas.drawText("Gold: " + gold, textX, 7*textY, paint);

            //Test Wachsstatus Erdbeere 1
            if(numStrawberries > 0)
                canvas.drawText("Wachsstatus Erdbeere 1: " + strawberries[0].getWachsStatus(), textX, 8*textY, paint);

            //Test Button malen
            canvas.drawBitmap(bitmapAckerKaufenButton, bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY, paint);
            canvas.drawBitmap(bitmapFightButton, bitmapFightButtonX, bitmapFightButtonY, paint);
            canvas.drawBitmap(bitmapGurkeKaufenButton, bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY, paint);
            canvas.drawBitmap(bitmapMusikAnAusButton, bitmapMusikAnAusButtonX, bitmapMusikAnAusButtonY, paint);
            canvas.drawBitmap(bitmapResetButton, bitmapResetButtonX, bitmapResetButtonY, paint);

            //Alles auf den Bildschirm malen
            //Und Canvas wieder freilassen (um Fehler zu minimieren(das könnte sogar der Fehler meiner ersten App gewesen sein))
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    //SharedPreferences auslesen
    private void getSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("StrawberrySettings", 0);
        numStrawberries = sharedPreferences.getInt("numStrawberries", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        gold = sharedPreferences.getInt("gold", 5);
        clickCount = sharedPreferences.getInt("clicks", 0);
        String strawberryStatus = sharedPreferences.getString("strawberryStatus", "");
        musicOn = sharedPreferences.getBoolean("musicOn", true);
        soundOn = sharedPreferences.getBoolean("soundOn", true);
        numGurken = sharedPreferences.getInt("numGurken", 1);
        alphaTester = sharedPreferences.getBoolean("alphaTester", true);
        betaTester = sharedPreferences.getBoolean("betaTester", false);

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
                strawberries[i] = new Strawberry(((int)i/16) + 1);
            }
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
        editor.putInt("numGurken", numGurken);
        editor.putBoolean("alphaTester", alphaTester);
        editor.putBoolean("betaTester", betaTester);

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
        getSharedPreferences();

        //Preise initialisieren
        priceAecker = getPrice(0);
        priceGurken = getPrice(1);

        //Hintergrundmusik anschalten
        playSound(0);

        gameThread = new Thread(this);
        gameThread.start();
    }

    //Was passiert wenn man den Touchscreen berührt?
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //Wenn wir im Farmmodus sind
        if (gameMode == 0) {
            return onTouchFarm(motionEvent);
        }
        //Wenn wir im Fightmodus sind
        else {
            return onTouchFight(motionEvent);
        }

    }

    //Was passiert wenn man den Touchscreen im FARM Modus berührt
    private boolean onTouchFarm(MotionEvent motionEvent) {
        //Alle Arten von Bewegung (auf dem Screen) die man bearbeiten will
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //Spieler berührt den Bildschirm
            case MotionEvent.ACTION_DOWN:
                //Wo befanden wir uns am Anfang?
                touchX1down = motionEvent.getX();
                touchX1 = motionEvent.getX();
                touchY1 = motionEvent.getY();

                //War da ein Button?
                //acker kaufen Button
                if (touchX1 >= bitmapAckerKaufenButtonX && touchX1 < (bitmapAckerKaufenButtonX + bitmapAckerKaufenButton.getWidth())
                        && touchY1 >= bitmapAckerKaufenButtonY && touchY1 < (bitmapAckerKaufenButtonY + bitmapAckerKaufenButton.getHeight())) {
                    playSound(4);
                    if (gold >= (priceAecker + STRAWBERRY_PRICE) && numAecker < 16) {
                        ackerGekauft();
                    }
                    break;
                }
                //fight button
                if (touchX1 >= bitmapFightButtonX && touchX1 < (bitmapFightButtonX + bitmapFightButton.getWidth())
                        && touchY1 >= bitmapFightButtonY && touchY1 < (bitmapFightButtonY + bitmapFightButton.getHeight())) {
                    playSound(4);
                    if (gameMode == 0) {
                        //gameMode = 1;
                    }
                    break;
                }
                //gurke kaufen button
                if (touchX1 >= bitmapGurkeKaufenButtonX && touchX1 < (bitmapGurkeKaufenButtonX + bitmapGurkeKaufenButton.getWidth())
                        && touchY1 >= bitmapGurkeKaufenButtonY && touchY1 < (bitmapGurkeKaufenButtonY + bitmapGurkeKaufenButton.getHeight())) {
                    playSound(4);
                    if (gold >= (priceGurken + 1)) {
                        numGurken++;
                        gold -= priceGurken;
                        priceGurken = getPrice(1);
                    }
                    break;
                }
                //musik an aus button
                if (touchX1 >= bitmapMusikAnAusButtonX && touchX1 < (bitmapMusikAnAusButtonX + bitmapMusikAnAusButton.getWidth())
                        && touchY1 >= bitmapMusikAnAusButtonY && touchY1 < (bitmapMusikAnAusButtonY + bitmapMusikAnAusButton.getHeight())) {
                    playSound(4);
                    if(soundOn == true) {
                        musicOn = false;
                        backgroundloop1.pause();
                        soundOn = false;
                    }
                    else {
                        musicOn = true;
                        playSound(0);
                        soundOn = true;
                    }
                    break;
                }
                //reset Button
                if (touchX1 >= bitmapResetButtonX && touchX1 < (bitmapResetButtonX + bitmapMusikAnAusButton.getWidth())
                        && touchY1 >= bitmapResetButtonY && touchY1 < (bitmapResetButtonY + bitmapMusikAnAusButton.getHeight())) {
                    //Buttonsound
                    playSound(4);

                    //Allgemeine Werte resetten
                    gold = 5;
                    numAecker = 1;
                    numStrawberries = 0;
                    numGurken = 1;
                    priceAecker = getPrice(0);
                    priceGurken = getPrice(1);

                    //Erdbeeren tatsächlich resetten
                    strawberries = new Strawberry[numAecker*16];
                    for (int i = 0; i < (numAecker * 16); i++) {
                        strawberries[i] = new Strawberry(((int)i/16) + 1);
                    }
                }


                //Wir haben geklickt, in einem Klickergame müssen wir doch mit der Info irgendwas machen oder? :D
                gotClickedFarm();

                break;

            //Spieler bewegt den Finger auf dem Bildschirm
            case MotionEvent.ACTION_MOVE:
                //In welche Richtung hat sich der Finger bewegt?
                touchX2 = motionEvent.getX();
                //Differenz der beiden Werte
                deltaXmove = touchX2 - touchX1;
                //wo befinden wir uns in diesem Schritt
                touchX1 = motionEvent.getX();

                //Bedingung für die Äußeren Grenzen
                if (((backgroundX1 + deltaXmove) < 0) && ((backgroundX1 + deltaXmove) > (-2 * screenX))) {
                    //Standardmovement (Folge dem Finger)
                    backgroundX1 += deltaXmove;
                }
                break;
            case MotionEvent.ACTION_UP:
                //Wie weit hat sich der Finger insgesamt bewegt?
                touchX2 = motionEvent.getX();
                //Differenz der beiden Werte
                deltaXclick = touchX2 - touchX1down;

                //reset der Hintergrundbildposition

                //Wären wir über den Rand gekommen?
                if (((backgroundX1 + deltaXclick) > 0) || ((backgroundX1 + deltaXclick) < (-2 * screenX))) {
                    //Rechts
                    if ((backgroundX1 + deltaXclick) < 0) {
                        backgroundX1 = -2 * screenX;
                        zustand = 2;
                        break;
                    }
                    //Links
                    if ((backgroundX1 + deltaXclick) > (-2 * screenX)) {
                        backgroundX1 = 0;
                        zustand = 0;
                        break;
                    }
                }
                //Zurückswapen nach Links, Rechts, Mitte wenn nach Stillstand Bedingungen zutreffen

                //Wenn wir von links nach links kommen
                if (backgroundX1 + deltaXmove > (-0.3 * screenX) && zustand == 0) {
                    backgroundX1 = 0;
                    zustand = 0;
                    break;
                }
                //wenn wir von der mitte nach links kommen
                if (backgroundX1 + deltaXmove > (-0.7 * screenX) && zustand == 1) {
                    backgroundX1 = 0;
                    zustand = 0;
                    break;
                }
                //wenn wir von rechts nach rechts kommen
                if (backgroundX1 + deltaXmove < (-1.7 * screenX) && zustand == 2) {
                    backgroundX1 = -2 * screenX;
                    zustand = 2;
                    break;
                }
                //von rechts zur mitte kommen
                if (backgroundX1 + deltaXmove < (-1.3 * screenX) && zustand == 1) {
                    backgroundX1 = -2 * screenX;
                    zustand = 2;
                    break;
                }
                //Mitte
                backgroundX1 = (-1 * screenX);
                zustand = 1;
                break;
        }
        return true;
    }

    //Was passiert wenn der Spieler im FARM Modus klickt?
    private void gotClickedFarm() {
        clickCount++;
        switch(zustand) {
            case 0:
                //Aussähen: Prüfen ob noch Platz ist, wenn ja: Aussähen.
                for(int j = 1; j <= numGurken; j++) {
                    if (numStrawberries < (numAecker * 16)) {
                        for (int i = 0; i < numAecker * 16; i++) {
                            if (gold >= STRAWBERRY_PRICE && strawberries[i].getWachsStatus() <= -1) {
                                strawberries[i].setStrawberry();
                                numStrawberries++;
                                gold -= STRAWBERRY_PRICE;
                                if(j == 1) {
                                    playSound(1);
                                }
                                break;
                            }
                        }
                    }
                }
                break;
            case 1:
                //Wachsen: Alles wächst viel schneller, aber es wächst auch schon so langsam.
                if (numStrawberries > 0) {
                    for (int i = 0; i < numAecker * 16; i++) {
                        strawberries[i].incrWachsStatus(1);
                    }
                }
                playSound(2);
                break;
            case 2:
                //Ernten: Prüfen ob Erdbeeren fertig, wenn ja: Gold bekommen und Platz machen zum Aussähen
                for(int j = 1; j <= numGurken; j++) {
                    for (int i = 0; i < numAecker * 16; i++) {
                        if (strawberries[i].getWachsStatus() >= 5) {
                            strawberries[i].resetStrawberry();
                            numStrawberries--;
                            gold += 10;
                            if (j == 1) {
                                playSound(3);
                            }
                            break;
                        }
                    }
                }
                break;
        }
    }

    //Was passiert wenn man den Touchscreen im FIGHT Modus berührt
    private boolean onTouchFight(MotionEvent motionEvent) {
        return true;
    }

    //Jede Art von Sound abspielen
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

    //Bitmaps rescalen
    private int getScaledBitmapSize(int targetScreenSize, int defaultScreenSize, int bitmapSize) {
        return (bitmapSize * targetScreenSize) / defaultScreenSize;
        //(((targetScreenSize)/(defaultScreenSize))*bitmapSize))
    }

    //X-Y Koordinaten rescalen
    private int getScaledCoordinates(int targetScreenSize, int defaultScreenSize, int defaultCoordinate) {
        return (defaultCoordinate * targetScreenSize) / defaultScreenSize;
    }

    //Wenn ein Acker gekauft wurde
    private void ackerGekauft() {
        //Anzahl hochzählen und Gold abbuchen
        numAecker++;
        gold -= priceAecker;
        priceAecker = getPrice(0);

        //Neues Strawberry Array erstellen
        Strawberry[] strawberriesTemp = Arrays.copyOf(strawberries, numAecker*16);
        for (int i = ((numAecker-1) * 16); i < (numAecker * 16); i++) {
            strawberriesTemp[i] = new Strawberry(((int)i/16) + 1);
        }

        strawberries = strawberriesTemp;
    }

    //Einlesen der minimalen Bitmaps (vong Google perösnlich der Algorithmus)
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                              int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    //Annäherung an eine minimale Größe der Bitmaps vor dem Einlesen
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //Alle Bilder einlesen
    private void initialiseBitmaps() {
        //Position des Hintergrundbildes festlegen
        backgroundX1 = 0;

        //Hintergrundbild:
        //Um das ganze effizient einzufügen müssen wir hier mit den Bitmapfactory Otions spielen
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Nur die Ränder werden eingefügt
        //Hintergrundbild einfügen
        bitmapBackgroundColors = BitmapFactory.decodeResource(this.getResources(), R.drawable.background_colors, options);
        //Dadurch, dass wir nur die Ränder haben wird nicht so viel RAM verbraucht und wir können trz die Größe erfahren und rescalen
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapBackgroundColors = decodeSampledBitmapFromResource(this.getResources(), R.drawable.background_colors, 250, 250);
        bitmapBackgroundColors = Bitmap.createScaledBitmap(bitmapBackgroundColors, screenX * 3, screenY, false);

        //Buttons initialisieren

        //Acker Kaufen Button
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapAckerKaufenButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.ackerkaufen_button, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapAckerKaufenButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.ackerkaufen_button, 100, 100);
        bitmapAckerKaufenButton = Bitmap.createScaledBitmap(bitmapAckerKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapFightButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.fight_button, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
        bitmapFightButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.fight_button, 100, 100);
        bitmapFightButton = Bitmap.createScaledBitmap(bitmapFightButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapGurkeKaufenButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.gurkekaufen_button, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
        bitmapGurkeKaufenButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.gurkekaufen_button, 100, 100);
        bitmapGurkeKaufenButton = Bitmap.createScaledBitmap(bitmapGurkeKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapMusikAnAusButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.musikanaus_button, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
        bitmapMusikAnAusButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.musikanaus_button, 100, 100);
        bitmapMusikAnAusButton = Bitmap.createScaledBitmap(bitmapMusikAnAusButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapResetButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.reset_button, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
        bitmapResetButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.reset_button, 100, 100);
        bitmapResetButton = Bitmap.createScaledBitmap(bitmapResetButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Feste Werte setzen
        bitmapAckerKaufenButtonX = getScaledCoordinates(screenX, 1080, 20);
        bitmapAckerKaufenButtonY = getScaledCoordinates(screenY, 1920, 490);
        bitmapFightButtonX = getScaledCoordinates(screenX, 1080, 270);
        bitmapFightButtonY = bitmapAckerKaufenButtonY;
        bitmapGurkeKaufenButtonX = getScaledCoordinates(screenX, 1080, 520);
        bitmapGurkeKaufenButtonY = bitmapAckerKaufenButtonY;
        bitmapMusikAnAusButtonX = getScaledCoordinates(screenX, 1080, 770);
        bitmapMusikAnAusButtonY = bitmapAckerKaufenButtonY;
        bitmapResetButtonX = getScaledCoordinates(screenX, 1080, 20);
        bitmapResetButtonY = getScaledCoordinates(screenY, 1920, 640);
    }

    //Gibt den Preis der Elemente aus dem Shop aus
    private int getPrice(int whichOne) {
        //whichOne Legende: 0: Acker, 1: Gurke, 2: Werkzeug
        switch (whichOne) {
            case 0:
                return (int) (50*Math.pow((double) numAecker, 1.7));
            case 1:
                return (int) (500*Math.pow((double) numGurken, 1.5));
            case 2:
                return 500;
        }
        return -1;
    }
}