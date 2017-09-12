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
import android.os.Build;
import android.os.Vibrator;
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
    private float touchX1, touchX1down, touchX2, touchY1, touchXPointer;
    private long touchTimer;
    private boolean touchPointer, actionDown, noButtonKlicked;
    //Abstand der letzten Bewegung auf dem Bildschirm
    private float deltaXmove, deltaXclick;

    //Ort des Hintergrundbildes
    private float backgroundColorsX1;
    private int backgroundFightX1;

    //Erdbeeren Array
    Strawberry[] strawberries;
    private int numStrawberries = 0;
    //Anzahl und Preis der Farmfläche
    private int numAecker;
    private int priceAecker;
    private final int AECKER_MAX = 32;
    //Anzahl und Preis der arbeitenden Gurken
    private int numGurken;
    private int priceGurken;

    //Gold initialisieren
    private int gold;
    //Erdbeerkosten
    private final int STRAWBERRY_PRICE = 1;

    //Bilder initialisieren
    Bitmap bitmapBackgroundColors;
    Bitmap bitmapBackgroundFights;

    //Größe der Bilder
    private int imageHeight;
    private int imageWidth;

    //Testbuttons FARM initialisieren
    Bitmap bitmapAckerKaufenButton;
    Bitmap bitmapFightButton;
    Bitmap bitmapGurkeKaufenButton;
    Bitmap bitmapMusikAnAusButton;
    Bitmap bitmapResetButton;

    //Testbuttons FIGHT initialisieren
    Bitmap bitmapSpawnDiebButton;
    Bitmap bitmapSpawnGoblinButton;
    Bitmap bitmapSpawnOrkButton;
    Bitmap bitmapSpawnLevelUp;
    Bitmap bitmapLevelUpDamage;
    Bitmap bitmapLevelUpDefense;
    Bitmap bitmapLevelUpLife;
    Bitmap bitmapWaffeHackeButton;
    Bitmap bitmapWaffeHolzschildButton;
    Bitmap bitmapWaffeKnueppelButton;
    Bitmap bitmapWaffeRiesenschwertButton;
    Bitmap bitmapWaffeSchwertButton;
    Bitmap bitmapWaffeWechselnButton;
    Bitmap bitmapSpawnRiese;
    Bitmap bitmapLevelUpAttackspeed;

    //Feste Größen speichern
    private int textSize, textSizeBig, textX, textY;
    private int bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY;
    private int bitmapFightButtonX, bitmapFightButtonY;
    private int bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY;
    private int bitmapMusikAnAusButtonX, bitmapMusikAnAusButtonY;
    private int bitmapResetButtonX, bitmapResetButtonY;
    private int bitmapSpawnDiebButtonX, bitmapSpawnDiebButtonY;
    private int bitmapSpawnGoblinButtonX, bitmapSpawnGoblinButtonY;
    private int bitmapSpawnOrkButtonX, bitmapSpawnOrkButtonY;
    private int bitmapSpawnLevelUpX, bitmapSpawnLevelUpY;
    private int bitmapLevelUpDamageX, bitmapLevelUpDamageY;
    private int bitmapLevelUpDefenseX, bitmapLevelUpDefenseY;
    private int bitmapLevelUpLifeX, bitmapLevelUpLifeY;
    private int bitmapWaffeHackeButtonX, bitmapWaffeHackeButtonY;
    private int bitmapWaffeHolzschildButtonX, bitmapWaffeHolzschildButtonY;
    private int bitmapWaffeKnueppelButtonX, bitmapWaffeKnueppelButtonY;
    private int bitmapWaffeRiesenschwertButtonX, bitmapWaffeRiesenschwertButtonY;
    private int bitmapWaffeSchwertButtonX, bitmapWaffeSchwertButtonY;
    private int bitmapWaffeWechselnButtonX, bitmapWaffeWechselnButtonY;
    private int bitmapSpawnRieseX, bitmapSpawnRieseY;
    private int bitmapLevelUpAttackspeedX, bitmapLevelUpAttackspeedY;

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

    //Kampfessteuerung
    private boolean fightmode1 = true;
    private int screenMitte;
    private int enemieSpawnLevel;
    private boolean levelUpPossible;
    private boolean chooseWeapon;

    //Alles was ich für den Fightmode brauche
    private Enemie enemie;
    private Character character;
    private StatusEffect characterStatusEffect;
    private Weapon equippedWeapon;
    private StatusEffect enemieStatusEffect;

    //Muss ich als haptisches Feedback dank Ability vibrieren?
    private boolean alreadyVibrating;

    //Verteidige dich gegen die Gegner
    private boolean defendNecessary;

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
        textSizeBig = getScaledBitmapSize(screenX, 1080, 100);
        //Textgröße setzen (möglicherweise muss das wieder zurück in drawFarm nach Hintergrundmalen)
        paint.setTextSize(textSize);
        //Textposition setzen
        textX = getScaledCoordinates(screenX, 1080, 20);
        textY = getScaledCoordinates(screenY, 1920, 50);
        //Font einstellen
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/caladea-bold.ttf");
        paint.setTypeface(customFont);

        screenMitte = screenX/2;
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
                updateFight();
                drawFight();
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

    //update ist quasi das DENKEN in der App
    private void updateFight() {
        if (enemie != null) {
            defendNecessary = enemie.attackUpdate();
            if (defendNecessary)
                gotAttacked();
            statusEffectUpdate();
            abilityFeedbackUpdate();
        }
    }

    //draw ist das ZEICHNEN in der App
    private void drawFarm() {
        //Standardfehlerabfangen
        if(ourHolder.getSurface().isValid()) {
            //canvas wird das Zeichenobjekt
            canvas = ourHolder.lockCanvas();

            try {
                //Hintergrund malen
                if (bitmapBackgroundColors != null)
                    canvas.drawBitmap(bitmapBackgroundColors, backgroundColorsX1, 0, paint);


                //Pinselfarbe wählen(bisher nur für den Text)
                paint.setColor(Color.argb(255, 249, 129, 0));

                //Derzeitigen FPS malen
                canvas.drawText("FPS: " + fps, textX, textY, paint);

                //Klickcounter malen
                canvas.drawText("Clicks: " + clickCount, textX, 2 * textY, paint);

                //Zustand als Text ausgeben
                switch (zustand) {
                    case 0:
                        canvas.drawText("Zustand: Aussähen", textX, 3 * textY, paint);
                        break;
                    case 1:
                        canvas.drawText("Zustand: Wachsen", textX, 3 * textY, paint);
                        break;
                    case 2:
                        canvas.drawText("Zustand: Ernten", textX, 3 * textY, paint);
                        break;
                    default:
                        canvas.drawText("Something went wrong :D", textX, 3 * textY, paint);
                        break;
                }

                //Anzahl der Erdbeeren
                canvas.drawText("Erdbeeren: " + numStrawberries, textX, 4 * textY, paint);

                //Anzahl Gurken
                canvas.drawText("Gurken: " + numGurken + " | Kosten: " + priceGurken + " Gold", textX, 5 * textY, paint);

                //Anzahl Aecker
                canvas.drawText("Äcker: " + numAecker + " | Kosten: " + priceAecker + " Gold", textX, 6 * textY, paint);

                //Wie viel Gold haben wir eigentlich?
                canvas.drawText("Gold: " + gold, textX, 7 * textY, paint);

                //Test Wachsstatus Erdbeere 1
                if (numStrawberries > 0)
                    canvas.drawText("Wachsstatus Erdbeere 1: " + strawberries[0].getWachsStatus(), textX, 8 * textY, paint);

                //Test Button malen
                if (bitmapAckerKaufenButton != null)
                    canvas.drawBitmap(bitmapAckerKaufenButton, bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY, paint);
                if (bitmapFightButton != null)
                    canvas.drawBitmap(bitmapFightButton, bitmapFightButtonX, bitmapFightButtonY, paint);
                if (bitmapGurkeKaufenButton != null)
                    canvas.drawBitmap(bitmapGurkeKaufenButton, bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY, paint);
                if (bitmapMusikAnAusButton != null)
                    canvas.drawBitmap(bitmapMusikAnAusButton, bitmapMusikAnAusButtonX, bitmapMusikAnAusButtonY, paint);
                if (bitmapResetButton != null)
                    canvas.drawBitmap(bitmapResetButton, bitmapResetButtonX, bitmapResetButtonY, paint);

            } catch (NullPointerException e) {
            setSharedPreferences();
            return;
        }

            //Alles auf den Bildschirm malen
            //Und Canvas wieder freilassen (um Fehler zu minimieren(das könnte sogar der Fehler meiner ersten App gewesen sein))
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    //draw ist das ZEICHNEN in der App
    private void drawFight() {
        //Standardfehlerabfangen
        if(ourHolder.getSurface().isValid()) {
            //canvas wird das Zeichenobjekt
            canvas = ourHolder.lockCanvas();

            try {
                //Hintergrund malen
                paint.setColor(Color.argb(255, 255, 255, 0));

                //Hintergrund malen
                if (bitmapBackgroundFights != null)
                    canvas.drawBitmap(bitmapBackgroundFights, backgroundFightX1, 0, paint);

                //Gegner Text malen
                if (enemie != null)
                    canvas.drawText("Gegner: " + enemie.getName(), textX, textY, paint);
                if (enemie != null)
                    canvas.drawText("Leben: " + enemie.getLife(), textX, 2 * textY, paint);
                //GegnerSpawnLevel
                if (enemieSpawnLevel >= 1) {
                    canvas.drawText("Gegnerisches Spawnlevel: " + enemieSpawnLevel, textX, 4 * textY, paint);
                }

                //Character Stats malen:
                if (character != null) {
                    canvas.drawText("Erfahrung: " + character.getExperience(), screenMitte, textY, paint);
                    canvas.drawText("Level: " + character.getLevel(), screenMitte, 2 * textY, paint);
                    canvas.drawText("Leben: " + character.getLife(), screenMitte, 3*textY, paint);
                }

                //Du kannst Levelupn
                if (levelUpPossible && !defendNecessary) {
                    paint.setTextSize(textSizeBig);
                    canvas.drawText("LEVEL UP AVAILABLE!!", textX, 14 * textY, paint);
                    paint.setTextSize(textSize);
                }

                //Du wirst angegriffen
                if (defendNecessary) {
                    paint.setTextSize(textSizeBig);
                    canvas.drawText("VERTEIDIGE DICH!!", textX, 14 * textY, paint);
                    paint.setTextSize(textSize);
                }

                //SpawnButtons malen
                if (bitmapSpawnLevelUp != null)
                    canvas.drawBitmap(bitmapSpawnLevelUp, bitmapSpawnLevelUpX, bitmapSpawnLevelUpY, paint);
                if (bitmapSpawnGoblinButton != null)
                    canvas.drawBitmap(bitmapSpawnGoblinButton, bitmapSpawnGoblinButtonX, bitmapSpawnGoblinButtonY, paint);
                if (bitmapSpawnOrkButton != null)
                    canvas.drawBitmap(bitmapSpawnOrkButton, bitmapSpawnOrkButtonX, bitmapSpawnOrkButtonY, paint);
                if (bitmapSpawnDiebButton != null)
                    canvas.drawBitmap(bitmapSpawnDiebButton, bitmapSpawnDiebButtonX, bitmapSpawnDiebButtonY, paint);
                if (bitmapSpawnRiese != null)
                    canvas.drawBitmap(bitmapSpawnRiese, bitmapSpawnRieseX, bitmapSpawnRieseY, paint);

                //Levelup Buttons
                if (levelUpPossible && !defendNecessary) {
                    if (bitmapLevelUpDamage != null)
                        canvas.drawBitmap(bitmapLevelUpDamage, bitmapLevelUpDamageX, bitmapLevelUpDamageY, paint);
                    if (bitmapLevelUpDefense != null)
                        canvas.drawBitmap(bitmapLevelUpDefense, bitmapLevelUpDefenseX, bitmapLevelUpDefenseY, paint);
                    if (bitmapLevelUpLife != null)
                        canvas.drawBitmap(bitmapLevelUpLife, bitmapLevelUpLifeX, bitmapLevelUpLifeY, paint);
                    if (bitmapLevelUpAttackspeed != null)
                        canvas.drawBitmap(bitmapLevelUpAttackspeed, bitmapLevelUpAttackspeedX, bitmapLevelUpAttackspeedY, paint);
                }

                //Chooseweapon Buttons
                if (bitmapWaffeWechselnButton != null)
                    canvas.drawBitmap(bitmapWaffeWechselnButton, bitmapWaffeWechselnButtonX, bitmapWaffeWechselnButtonY, paint);
                if (chooseWeapon) {
                    if (bitmapWaffeHackeButton != null)
                        canvas.drawBitmap(bitmapWaffeHackeButton, bitmapWaffeHackeButtonX, bitmapWaffeHackeButtonY, paint);
                    if (bitmapWaffeHolzschildButton != null)
                        canvas.drawBitmap(bitmapWaffeHolzschildButton, bitmapWaffeHolzschildButtonX, bitmapWaffeHolzschildButtonY, paint);
                    if (bitmapWaffeKnueppelButton != null)
                        canvas.drawBitmap(bitmapWaffeKnueppelButton, bitmapWaffeKnueppelButtonX, bitmapWaffeKnueppelButtonY, paint);
                    if (bitmapWaffeRiesenschwertButton != null)
                        canvas.drawBitmap(bitmapWaffeRiesenschwertButton, bitmapWaffeRiesenschwertButtonX, bitmapWaffeRiesenschwertButtonY, paint);
                    if (bitmapWaffeSchwertButton != null)
                        canvas.drawBitmap(bitmapWaffeSchwertButton, bitmapWaffeSchwertButtonX, bitmapWaffeSchwertButtonY, paint);
                }
            } catch (NullPointerException e) {
                setSharedPreferences();
                return;
            }
            //Alles auf den Bildschirm malen
            //Und Canvas wieder freilassen (um Fehler zu minimieren(das könnte sogar der Fehler meiner ersten App gewesen sein))
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    //SharedPreferences auslesen
    private void getSharedPreferences() {
        if (gameMode == 0) {
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

            strawberries = new Strawberry[numAecker * AECKER_MAX];

            //um keine IndexoutofBoundException zu bekommen
            if (!(strawberryStatus.equals(""))) {
                //Initialisierung der gespeicherten Erdbeeren: 1. String auseinander nehmen, 2. aus den Daten auslesen
                //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
                String[] strawberryStatusStrings = strawberryStatus.split("a");
                int stringsCounter = 0;
                for (int i = 0; i < (numAecker * 16); i++) {
                    strawberries[i] = new Strawberry(Integer.parseInt(strawberryStatusStrings[stringsCounter]), Integer.parseInt(strawberryStatusStrings[stringsCounter + 1]), Long.parseLong(strawberryStatusStrings[stringsCounter + 2]));
                    stringsCounter += 3;
                }
            } else {
                for (int i = 0; i < (numAecker * 16); i++) {
                    strawberries[i] = new Strawberry(((int) i / 16) + 1);
                }
            }
        }
        if (gameMode == 1) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("StrawberryFight", 0);
            characterStatusEffect = new StatusEffect(sharedPreferences.getString("characterStatusEffect", "default"));
        }
    }
    //SharedPreferences wieder sicher verwahren
    private void setSharedPreferences() {
        if (gameMode == 0) {

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
            for (int i = 0; i < (numAecker * 16); i++) {
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
        if (gameMode == 1) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("StrawberryFight", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (character != null) {
                editor.putInt("characterExperience", character.getExperience());
                editor.putString("characterEquippedWeapon", character.getEquipedWeapon().getName());
                editor.putInt("characterBaseDamage",character.getBaseDamage());
                editor.putInt("characterLife", character.getLife());
                editor.putInt("characterDefense", character.getBaseDefense());
                editor.putInt("characterExperience", character.getExperience());
                editor.putInt("characterLevel", character.getLevel());
                editor.putInt("characterBaseAttackspeed", character.getBaseAttackspeed());
                if (characterStatusEffect != null)
                    editor.putString("characterStatusEffect", characterStatusEffect.getName());
            }

            editor.commit();
        }
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

        if (enemie != null)
            enemie.attackRightNowReset();

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
                touchPointer = false;

                //War da ein Button?
                //acker kaufen Button
                if (touchX1 >= bitmapAckerKaufenButtonX && touchX1 < (bitmapAckerKaufenButtonX + bitmapAckerKaufenButton.getWidth())
                        && touchY1 >= bitmapAckerKaufenButtonY && touchY1 < (bitmapAckerKaufenButtonY + bitmapAckerKaufenButton.getHeight())) {
                    playSound(4);
                    if (gold >= (priceAecker + STRAWBERRY_PRICE) && numAecker < AECKER_MAX) {
                        ackerGekauft();
                    }
                    break;
                }
                //fight button
                if (touchX1 >= bitmapFightButtonX && touchX1 < (bitmapFightButtonX + bitmapFightButton.getWidth())
                        && touchY1 >= bitmapFightButtonY && touchY1 < (bitmapFightButtonY + bitmapFightButton.getHeight())) {
                    playSound(4);
                    if (gameMode == 0) {
                        initialiseFightMode();
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
                    strawberries = new Strawberry[numAecker*AECKER_MAX];
                    for (int i = 0; i < (numAecker * 16); i++) {
                        strawberries[i] = new Strawberry(((int)i/16) + 1);
                    }

                    //Fight Mode resetten
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("StrawberryFight", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (character != null) {
                        editor.putInt("characterExperience", 0);
                        editor.putString("characterEquippedWeapon", "Hacke");
                        editor.putInt("characterBaseDamage", 1);
                        editor.putInt("characterLife", 25);
                        editor.putInt("characterDefense", 1);
                        editor.putInt("characterExperience", 0);
                        editor.putInt("characterLevel", 1);
                        editor.putInt("characterMaxLife", 25);
                        editor.putInt("characterBaseAttackspeed", 1000);
                        editor.putString("characterStatusEffect", "default");
                    }

                    editor.commit();
                    break;
                }

                //Wir haben geklickt, in einem Klickergame müssen wir doch mit der Info irgendwas machen oder? :D
                gotClickedFarm();

                break;

            //Spieler bewegt den Finger auf dem Bildschirm
            case MotionEvent.ACTION_MOVE:
                if (!touchPointer) {
                    //In welche Richtung hat sich der Finger bewegt?
                    touchX2 = motionEvent.getX();
                    //Differenz der beiden Werte
                    deltaXmove = touchX2 - touchX1;
                    //wo befinden wir uns in diesem Schritt
                    touchX1 = motionEvent.getX();

                    //Bedingung für die Äußeren Grenzen
                    if (((backgroundColorsX1 + deltaXmove) < 0) && ((backgroundColorsX1 + deltaXmove) > (-2 * screenX))) {
                        //Standardmovement (Folge dem Finger)
                        backgroundColorsX1 += deltaXmove;
                    }
                }
                break;
            //Zweiter Finger kommt dazu:
            case MotionEvent.ACTION_POINTER_DOWN:
                touchPointer = true;
                break;
            case MotionEvent.ACTION_UP:
                //Wie weit hat sich der Finger insgesamt bewegt?
                touchX2 = motionEvent.getX();
                //Differenz der beiden Werte
                deltaXclick = touchX2 - touchX1down;

                //reset der Hintergrundbildposition

                //Wären wir über den Rand gekommen?
                if (((backgroundColorsX1 + deltaXclick) > 0) || ((backgroundColorsX1 + deltaXclick) < (-2 * screenX))) {
                    //Rechts
                    if ((backgroundColorsX1 + deltaXclick) < 0) {
                        backgroundColorsX1 = -2 * screenX;
                        zustand = 2;
                        break;
                    }
                    //Links
                    if ((backgroundColorsX1 + deltaXclick) > (-2 * screenX)) {
                        backgroundColorsX1 = 0;
                        zustand = 0;
                        break;
                    }
                }
                //Zurückswapen nach Links, Rechts, Mitte wenn nach Stillstand Bedingungen zutreffen

                //Wenn wir von links nach links kommen
                if (backgroundColorsX1 + deltaXmove > (-0.3 * screenX) && zustand == 0) {
                    backgroundColorsX1 = 0;
                    zustand = 0;
                    break;
                }
                //wenn wir von der mitte nach links kommen
                if (backgroundColorsX1 + deltaXmove > (-0.7 * screenX) && zustand == 1) {
                    backgroundColorsX1 = 0;
                    zustand = 0;
                    break;
                }
                //wenn wir von rechts nach rechts kommen
                if (backgroundColorsX1 + deltaXmove < (-1.7 * screenX) && zustand == 2) {
                    backgroundColorsX1 = -2 * screenX;
                    zustand = 2;
                    break;
                }
                //von rechts zur mitte kommen
                if (backgroundColorsX1 + deltaXmove < (-1.3 * screenX) && zustand == 1) {
                    backgroundColorsX1 = -2 * screenX;
                    zustand = 2;
                    break;
                }
                //Mitte
                backgroundColorsX1 = (-1 * screenX);
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
        //Alle Arten von Bewegung (auf dem Screen) die man bearbeiten will
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchX1 = motionEvent.getX();
                touchY1 = motionEvent.getY();
                touchTimer = System.currentTimeMillis();
                alreadyVibrating = false;
                actionDown = true;
                noButtonKlicked = false;

                //War da ein Button?
                if (touchX1 >= bitmapSpawnLevelUpX && touchX1 < (bitmapSpawnLevelUpX + bitmapSpawnLevelUp.getWidth())
                    && touchY1 >= bitmapSpawnLevelUpY && touchY1 < (bitmapSpawnLevelUpY + bitmapSpawnLevelUp.getHeight())) {
                    playSound(4);
                    enemieSpawnLevel++;
                    break;
                }
                if (touchX1 >= bitmapSpawnGoblinButtonX && touchX1 < (bitmapSpawnGoblinButtonX + bitmapSpawnGoblinButton.getWidth())
                        && touchY1 >= bitmapSpawnGoblinButtonY && touchY1 < (bitmapSpawnGoblinButtonY + bitmapSpawnGoblinButton.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Goblin");
                    }
                    break;
                }
                if (touchX1 >= bitmapSpawnDiebButtonX && touchX1 < (bitmapSpawnDiebButtonX + bitmapSpawnDiebButton.getWidth())
                        && touchY1 >= bitmapSpawnDiebButtonY && touchY1 < (bitmapSpawnDiebButtonY + bitmapSpawnDiebButton.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Dieb");
                    }
                    break;
                }
                if (touchX1 >= bitmapSpawnOrkButtonX && touchX1 < (bitmapSpawnOrkButtonX + bitmapSpawnOrkButton.getWidth())
                        && touchY1 >= bitmapSpawnOrkButtonY && touchY1 < (bitmapSpawnOrkButtonY + bitmapSpawnOrkButton.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Ork");
                    }
                    break;
                }
                if (touchX1 >= bitmapSpawnRieseX && touchX1 < (bitmapSpawnRieseX + bitmapSpawnRiese.getWidth())
                        && touchY1 >= bitmapSpawnRieseY && touchY1 < (bitmapSpawnRieseY + bitmapSpawnRiese.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Riese");
                    }
                    break;
                }
                //Level Up
                if (touchX1 >= bitmapLevelUpDamageX && touchX1 < (bitmapLevelUpDamageX + bitmapLevelUpDamage.getWidth())
                        && touchY1 >= bitmapLevelUpDamageY && touchY1 < (bitmapLevelUpDamageY + bitmapLevelUpDamage.getHeight())) {
                    playSound(4);
                    character.levelUp(1, 0, 0, 0);
                    if (!character.canLevelUp())
                        levelUpPossible = false;
                    break;
                }
                if (touchX1 >= bitmapLevelUpDefenseX && touchX1 < (bitmapLevelUpDefenseX + bitmapLevelUpDefense.getWidth())
                        && touchY1 >= bitmapLevelUpDefenseY && touchY1 < (bitmapLevelUpDefenseY + bitmapLevelUpDefense.getHeight())) {
                    playSound(4);
                    character.levelUp(0, 0, 1, 0);
                    if (!character.canLevelUp())
                        levelUpPossible = false;
                    break;
                }
                if (touchX1 >= bitmapLevelUpLifeX && touchX1 < (bitmapLevelUpLifeX + bitmapLevelUpLife.getWidth())
                        && touchY1 >= bitmapLevelUpLifeY && touchY1 < (bitmapLevelUpLifeY + bitmapLevelUpLife.getHeight())) {
                    playSound(4);
                    character.levelUp(0, 1, 0, 0);
                    if (!character.canLevelUp())
                        levelUpPossible = false;
                    break;
                }
                if (touchX1 >= bitmapLevelUpAttackspeedX && touchX1 < (bitmapLevelUpAttackspeedX + bitmapLevelUpAttackspeed.getWidth())
                        && touchY1 >= bitmapLevelUpAttackspeedY && touchY1 < (bitmapLevelUpAttackspeedY + bitmapLevelUpAttackspeed.getHeight())) {
                    playSound(4);
                    character.levelUp(0, 0, 0, 100);
                    if (!character.canLevelUp())
                        levelUpPossible = false;
                    break;
                }

                //Weapon Choose Buttons
                if (touchX1 >= bitmapWaffeWechselnButtonX && touchX1 < (bitmapWaffeWechselnButtonX + bitmapWaffeWechselnButton.getWidth())
                        && touchY1 >= bitmapWaffeWechselnButtonY && touchY1 < (bitmapWaffeWechselnButtonY + bitmapWaffeWechselnButton.getHeight())) {
                    playSound(4);
                    if (chooseWeapon)
                        chooseWeapon = false;
                    else
                        chooseWeapon = true;
                    break;
                }
                if (chooseWeapon) {
                    if (touchX1 >= bitmapWaffeHackeButtonX && touchX1 < (bitmapWaffeHackeButtonX + bitmapWaffeHackeButton.getWidth())
                            && touchY1 >= bitmapWaffeHackeButtonY && touchY1 < (bitmapWaffeHackeButtonY + bitmapWaffeHackeButton.getHeight())) {
                        playSound(4);
                        equippedWeapon = new Weapon("Hacke");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeHolzschildButtonX && touchX1 < (bitmapWaffeHolzschildButtonX + bitmapWaffeHolzschildButton.getWidth())
                            && touchY1 >= bitmapWaffeHolzschildButtonY && touchY1 < (bitmapWaffeHolzschildButtonY + bitmapWaffeHolzschildButton.getHeight())) {
                        playSound(4);
                        equippedWeapon = new Weapon("Holzschild");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeKnueppelButtonX && touchX1 < (bitmapWaffeKnueppelButtonX + bitmapWaffeKnueppelButton.getWidth())
                            && touchY1 >= bitmapWaffeKnueppelButtonY && touchY1 < (bitmapWaffeKnueppelButtonY + bitmapWaffeKnueppelButton.getHeight())) {
                        playSound(4);
                        equippedWeapon = new Weapon("Knüppel");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeRiesenschwertButtonX && touchX1 < (bitmapWaffeRiesenschwertButtonX + bitmapWaffeRiesenschwertButton.getWidth())
                            && touchY1 >= bitmapWaffeRiesenschwertButtonY && touchY1 < (bitmapWaffeRiesenschwertButtonY + bitmapWaffeRiesenschwertButton.getHeight())) {
                        playSound(4);
                        equippedWeapon = new Weapon("Riesenschwert");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeSchwertButtonX && touchX1 < (bitmapWaffeSchwertButtonX + bitmapWaffeSchwertButton.getWidth())
                            && touchY1 >= bitmapWaffeSchwertButtonY && touchY1 < (bitmapWaffeSchwertButtonY + bitmapWaffeSchwertButton.getHeight())) {
                        playSound(4);
                        equippedWeapon = new Weapon("Schwert");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                }
                noButtonKlicked = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int index = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                touchXPointer = motionEvent.getX(index);
                float test = touchXPointer - touchX1;
                //Auf den linken Teil des Bildschirms wird geklickt
                if (touchXPointer < screenMitte) {
                    defend();
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
                actionDown = false;
                //Fähigkeiten Spells
                if (noButtonKlicked) { //Solange kein Button benutzt wurde
                    if (System.currentTimeMillis() - touchTimer >= 1500) {
                        fireball();
                        alreadyVibrating = false;
                        break;
                    }
                    //Auf den rechten Teil des Bildschirms wird geklickt
                    if (touchX1 >= screenMitte) {
                        attack();
                        break;
                    }
                    //Auf den linken Teil des Bildschirms wird geklickt
                    if (touchX1 < screenMitte) {
                        defend();
                        break;
                    }
                }
                break;
        }
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
        //In neuen Versionen soll man das halt jetzt so machen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            //Aber ich will die alten Versionen trz nicht verlieren deshalb lassen wir das mal drin
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

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
    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
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
    private static int calculateInSampleSize(
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
        if (gameMode == 0) {
            //Position des Hintergrundbildes festlegen
            backgroundColorsX1 = 0;

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
        else {
            //Hintergrundbildposition
            backgroundFightX1 = getScaledCoordinates(screenX, 1080, -540);

            //Hinntergrundbildscaling
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapBackgroundFights = BitmapFactory.decodeResource(this.getResources(), R.drawable.fightbackground2, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
            bitmapBackgroundFights = decodeSampledBitmapFromResource(this.getResources(), R.drawable.fightbackground2, 200, 200);
            bitmapBackgroundFights = Bitmap.createScaledBitmap(bitmapBackgroundFights, screenX * 2, screenY, false);

            //Buttons
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapSpawnDiebButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.spawndieb_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapSpawnDiebButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.spawndieb_button, 100, 100);
            bitmapSpawnDiebButton = Bitmap.createScaledBitmap(bitmapSpawnDiebButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapSpawnGoblinButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.spawngoblin_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapSpawnGoblinButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.spawngoblin_button, 100, 100);
            bitmapSpawnGoblinButton = Bitmap.createScaledBitmap(bitmapSpawnGoblinButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapSpawnOrkButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.spawnork_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapSpawnOrkButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.spawnork_button, 100, 100);
            bitmapSpawnOrkButton = Bitmap.createScaledBitmap(bitmapSpawnOrkButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapSpawnLevelUp = BitmapFactory.decodeResource(this.getResources(), R.drawable.spawnlevelplus_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapSpawnLevelUp = decodeSampledBitmapFromResource(this.getResources(), R.drawable.spawnlevelplus_button, 100, 100);
            bitmapSpawnLevelUp = Bitmap.createScaledBitmap(bitmapSpawnLevelUp, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapSpawnRiese = BitmapFactory.decodeResource(this.getResources(), R.drawable.spawnriese_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapSpawnRiese = decodeSampledBitmapFromResource(this.getResources(), R.drawable.spawnriese_button, 100, 100);
            bitmapSpawnRiese = Bitmap.createScaledBitmap(bitmapSpawnRiese, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            //Feste Werte setzen
            bitmapSpawnLevelUpX = getScaledCoordinates(screenX, 1080, 20);
            bitmapSpawnLevelUpY = getScaledCoordinates(screenY, 1920, 250);
            bitmapSpawnGoblinButtonX = getScaledCoordinates(screenX, 1080, 20);
            bitmapSpawnGoblinButtonY = getScaledCoordinates(screenY, 1920, 400);
            bitmapSpawnOrkButtonX = getScaledCoordinates(screenX, 1080, 270);
            bitmapSpawnOrkButtonY = bitmapSpawnGoblinButtonY;
            bitmapSpawnDiebButtonX = getScaledCoordinates(screenX, 1080, 520);
            bitmapSpawnDiebButtonY = bitmapSpawnGoblinButtonY;
            bitmapSpawnRieseX = getScaledCoordinates(screenX, 1080, 770);
            bitmapSpawnRieseY = bitmapSpawnGoblinButtonY;

            //Level UP Buttons
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapLevelUpDamage = BitmapFactory.decodeResource(this.getResources(), R.drawable.levelupdamage_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapLevelUpDamage = decodeSampledBitmapFromResource(this.getResources(), R.drawable.levelupdamage_button, 100, 100);
            bitmapLevelUpDamage = Bitmap.createScaledBitmap(bitmapLevelUpDamage, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapLevelUpDefense = BitmapFactory.decodeResource(this.getResources(), R.drawable.levelupdefense_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapLevelUpDefense = decodeSampledBitmapFromResource(this.getResources(), R.drawable.levelupdefense_button, 100, 100);
            bitmapLevelUpDefense = Bitmap.createScaledBitmap(bitmapLevelUpDefense, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapLevelUpLife = BitmapFactory.decodeResource(this.getResources(), R.drawable.leveluplife_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapLevelUpLife = decodeSampledBitmapFromResource(this.getResources(), R.drawable.leveluplife_button, 100, 100);
            bitmapLevelUpLife = Bitmap.createScaledBitmap(bitmapLevelUpLife, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapLevelUpAttackspeed = BitmapFactory.decodeResource(this.getResources(), R.drawable.levelupattackspeed_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapLevelUpAttackspeed = decodeSampledBitmapFromResource(this.getResources(), R.drawable.levelupattackspeed_button, 100, 100);
            bitmapLevelUpAttackspeed = Bitmap.createScaledBitmap(bitmapLevelUpAttackspeed, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            //bitmapLevelUpAttackspeed
            bitmapLevelUpDamageX = getScaledCoordinates(screenX, 1080, 50);
            bitmapLevelUpDamageY = getScaledCoordinates(screenY, 1920, 800);
            bitmapLevelUpDefenseX = getScaledCoordinates(screenX, 1080, 300);
            bitmapLevelUpDefenseY = getScaledCoordinates(screenY, 1920, 800);
            bitmapLevelUpLifeX = getScaledCoordinates(screenX, 1080, 550);
            bitmapLevelUpLifeY = getScaledCoordinates(screenY, 1920, 800);
            bitmapLevelUpAttackspeedX = getScaledCoordinates(screenX, 1080, 800);
            bitmapLevelUpAttackspeedY = getScaledCoordinates(screenY, 1920, 800);

            //Weaponchoose Buttons
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapWaffeHackeButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.waffehacke_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapWaffeHackeButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.waffehacke_button, 100, 100);
            bitmapWaffeHackeButton = Bitmap.createScaledBitmap(bitmapWaffeHackeButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapWaffeHolzschildButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.waffeholzschild_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapWaffeHolzschildButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.waffeholzschild_button, 100, 100);
            bitmapWaffeHolzschildButton = Bitmap.createScaledBitmap(bitmapWaffeHolzschildButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapWaffeKnueppelButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.waffeknueppel_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapWaffeKnueppelButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.waffeknueppel_button, 100, 100);
            bitmapWaffeKnueppelButton = Bitmap.createScaledBitmap(bitmapWaffeKnueppelButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapWaffeRiesenschwertButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.wafferiesenschwert_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapWaffeRiesenschwertButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.wafferiesenschwert_button, 100, 100);
            bitmapWaffeRiesenschwertButton = Bitmap.createScaledBitmap(bitmapWaffeRiesenschwertButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapWaffeSchwertButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.waffeschwert_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapWaffeSchwertButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.waffeschwert_button, 100, 100);
            bitmapWaffeSchwertButton = Bitmap.createScaledBitmap(bitmapWaffeSchwertButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmapWaffeWechselnButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.waffewechseln_button, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            bitmapWaffeWechselnButton = decodeSampledBitmapFromResource(this.getResources(), R.drawable.waffewechseln_button, 100, 100);
            bitmapWaffeWechselnButton = Bitmap.createScaledBitmap(bitmapWaffeWechselnButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

            //Feste Werte
            bitmapWaffeWechselnButtonX = getScaledCoordinates(screenX, 1080, 50);
            bitmapWaffeWechselnButtonY = getScaledCoordinates(screenY, 1920, 1620);
            bitmapWaffeSchwertButtonX = getScaledCoordinates(screenX, 1080, 300);
            bitmapWaffeSchwertButtonY = getScaledCoordinates(screenY, 1920, 1620);
            bitmapWaffeRiesenschwertButtonX = getScaledCoordinates(screenX, 1080, 550);
            bitmapWaffeRiesenschwertButtonY = getScaledCoordinates(screenY, 1920, 1620);
            bitmapWaffeKnueppelButtonX = getScaledCoordinates(screenX, 1080, 550);
            bitmapWaffeKnueppelButtonY = getScaledCoordinates(screenY, 1920, 1770);
            bitmapWaffeHolzschildButtonX = getScaledCoordinates(screenX, 1080, 50);
            bitmapWaffeHolzschildButtonY = getScaledCoordinates(screenY, 1920, 1770);
            bitmapWaffeHackeButtonX = getScaledCoordinates(screenX, 1080, 300);
            bitmapWaffeHackeButtonY = getScaledCoordinates(screenY, 1920, 1770);
        }
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

    //Gegner erstellen
    private void spawnEnemie(String name) {
        enemie = new Enemie(name, enemieSpawnLevel);
        enemieStatusEffect = new StatusEffect("default");
        character.resetLastAttackTime();
    }

    //AUF ZUM ANGRIFF!! AAAAHHH
    private void attack() {
        if (enemie != null && character != null) {
            if (System.currentTimeMillis() - character.getLastAttackTime() > character.getAttackspeed()) {
                enemie.defend(character.getMeleeDamage());
                character.setLastAttackTime();
                if (!enemie.getLifeStatus()) {
                    if (character.setExperience(enemie.getExperience())) {
                        levelUpPossible = true;
                    }
                    enemie = null;
                    defendNecessary = false;
                    if (character != null)
                        character.setLife();
                    enemieStatusEffect.resetStatusEffect();
                }
            }
        }
    }

    //VERTEIDIGE DICH GEGEN DAS SCHWEIN!
    private void defend() {
        //Solange der Gegner auch existiert
        if (enemie != null) {
            //falls der Gegner gerade angreift
            if (enemie.getAttackRightNow()) {
                enemie.attackRightNowReset();
                defendNecessary = false;
            }
        }
    }

    //wurden wir / wir wurden angegriffen
    private void gotAttacked() {
        if (enemie != null && character != null) {
            //man hat eine halbe Sekunde Zeit um auf einen Angriff zu reagieren
            if (System.currentTimeMillis() - enemie.getLastAttackTime() > 500 && defendNecessary) {
                //Man wird tatsächlich angegriffen
                if (character.getLife() > 0) {
                    //Die ersten 10 Defense Punkte zehlen 1 zu 1 als Abwehr. Danach nur noch zur Hälfte
                    if (character.getBaseDefense() < 10) {
                        if (enemie.getDamage() >= character.getBaseDefense()) //Damit man beim Angreifen nicht heilt
                            character.gotAttacked(Math.abs(character.getBaseDefense() - enemie.getDamage()));
                    } else {
                        if (enemie.getDamage() - 10 >= ((character.getBaseDefense() - 10) / 2))
                            character.gotAttacked(Math.abs(((character.getBaseDefense() - 10) / 2) - (enemie.getDamage() - 10)));
                    }
                }
                enemie.attackRightNowReset();
                defendNecessary = false;
            }
        }
    }

    //Wenn der Zurückbutton gedrücckt wurde
    public void onBackPressed() {
        if (gameMode == 0) {

        }
        if (gameMode == 1) {
            //Fightmode Infos sichern
            setSharedPreferences();
            //alles leeren
            enemie = null;
            gameMode = 0;

            initialiseBitmaps();

            bitmapBackgroundFights.recycle();
            bitmapBackgroundFights = null;
            bitmapSpawnDiebButton.recycle();
            bitmapSpawnDiebButton = null;
            bitmapSpawnGoblinButton.recycle();
            bitmapSpawnGoblinButton = null;
            bitmapSpawnOrkButton.recycle();
            bitmapSpawnOrkButton = null;
            bitmapSpawnLevelUp.recycle();
            bitmapSpawnLevelUp = null;
            bitmapLevelUpDamage.recycle();
            bitmapLevelUpDamage = null;
            bitmapLevelUpDefense.recycle();
            bitmapLevelUpDefense = null;
            bitmapLevelUpLife.recycle();
            bitmapLevelUpLife = null;
            bitmapWaffeHackeButton.recycle();
            bitmapWaffeHackeButton = null;
            bitmapWaffeHolzschildButton.recycle();
            bitmapWaffeHolzschildButton = null;
            bitmapWaffeKnueppelButton.recycle();
            bitmapWaffeKnueppelButton = null;
            bitmapWaffeRiesenschwertButton.recycle();
            bitmapWaffeRiesenschwertButton = null;
            bitmapWaffeSchwertButton.recycle();
            bitmapWaffeSchwertButton = null;
            bitmapWaffeWechselnButton.recycle();
            bitmapWaffeWechselnButton = null;
            bitmapSpawnRiese.recycle();
            bitmapSpawnRiese = null;
            bitmapLevelUpAttackspeed.recycle();
            bitmapLevelUpAttackspeed = null;

            chooseWeapon = false;

            if(enemieStatusEffect != null)
                enemieStatusEffect.resetStatusEffect();
            enemieStatusEffect = null;
            if (characterStatusEffect != null)
                characterStatusEffect.resetStatusEffect();
            characterStatusEffect = null;
        }
    }

    //Fireball
    public void fireball() {
        if (enemie != null) {
            enemie.defend(30);
            enemieStatusEffect.setStatusEffect("ignite");
            if (!enemie.getLifeStatus()) {
                if (character.setExperience(enemie.getExperience())) {
                    levelUpPossible = true;
                }
                enemie = null;
                defendNecessary = false;
                if (character != null)
                    character.setLife();
                enemieStatusEffect.resetStatusEffect();
            }
        }
    }

    //Fight Mode initialisieren
    private void initialiseFightMode() {
        //Zur Sicherheit mal alles wichtige abspeichern
        setSharedPreferences();
        gameMode = 1;
        //Bitmaps Recyclen
        bitmapBackgroundColors.recycle();
        bitmapBackgroundColors = null;
        bitmapAckerKaufenButton.recycle();
        bitmapAckerKaufenButton = null;
        bitmapFightButton.recycle();
        bitmapFightButton = null;
        bitmapGurkeKaufenButton.recycle();
        bitmapGurkeKaufenButton = null;
        bitmapMusikAnAusButton.recycle();
        bitmapMusikAnAusButton = null;
        bitmapResetButton.recycle();
        bitmapResetButton = null;

        touchTimer = System.currentTimeMillis();

        initialiseBitmaps();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("StrawberryFight", 0);
        character = new Character(new Weapon(sharedPreferences.getString("characterEquippedWeapon", "Hacke")), sharedPreferences.getInt("characterBaseDamage", 1), sharedPreferences.getInt("characterLife", 25), sharedPreferences.getInt("characterMaxLife", 25), sharedPreferences.getInt("characterDefense", 1), sharedPreferences.getInt("characterExperience", 0), sharedPreferences.getInt("characterLevel", 1), sharedPreferences.getInt("characterBaseAttackspeed", 1000));
        enemieSpawnLevel = 1;
        spawnEnemie("Goblin");
        getSharedPreferences();
        levelUpPossible = false;
        if (character.canLevelUp() == true) {
            levelUpPossible = true;
        }
        levelUpPossible = character.canLevelUp();
        chooseWeapon = false;
        alreadyVibrating = false;
        noButtonKlicked = true;
    }

    //Statuseffekte abgeben?
    private void statusEffectUpdate() {
        //enemie status effect
        if (enemie != null && enemieStatusEffect != null) {
            if (enemieStatusEffect.getStatusEffectNumber() == 0) { //ignite
                if ((System.currentTimeMillis() - enemieStatusEffect.getLastTick()) >= 1000) {
                    enemie.trueDamage(1);
                    enemieStatusEffect.setLastTick();
                    if ((System.currentTimeMillis() - enemieStatusEffect.getTimeGotEffect()) >= 5000) {
                        enemieStatusEffect.resetStatusEffect();
                    }
                }
            }
        }
    }

    //Haptisches Feedback abgeben?
    private void abilityFeedbackUpdate() {
        if (enemie != null) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (actionDown && System.currentTimeMillis() - touchTimer >= 1500 && !alreadyVibrating) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(100);
                    alreadyVibrating = true;
                }
            }
        }
    }
}