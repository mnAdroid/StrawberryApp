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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

class FarmMode {
    //Der gespeicherte Context
    private Context fullContext;

    //Zustand (0 = Sähen, 1 = Wachsen, 2 = Ernten)
    private int zustand = 0;

    //Bildschirmkoordinaten
    private int screenX, screenY;

    //Ort der letzten Berührung auf dem Bildschirm
    private float touchX1;
    private float touchX1down;
    private boolean touchPointer = false;
    //Abstand der letzten Bewegung auf dem Bildschirm
    private float deltaXMove;

    //Ort des Hintergrundbildes
    private float backgroundOverlayX1;
    private float backgroundLandY1;

    //Erdbeeren Array
    private Strawberry[] strawberries;
    private int numStrawberries = 0;
    //Anzahl und Preis der Farmfläche
    private int numAecker;
    private int priceAecker;
    private final int AECKER_MAX = 32;
    //Anzahl und Preis der Länderein
    private int numLand;
    private int priceLand;
    private final int LAND_MAX = 8;
    //Anzahl und Preis der arbeitenden Gurken
    private int numGurken;
    private int priceGurken;

    //Erdbeerkosten
    private final int STRAWBERRY_PRICE = 1;

    //Bilder initialisieren
    private Bitmap bitmapBackgroundOverlay;
    private Bitmap bitmapBackgroundLoading;
    private Bitmap bitmapBackgroundLand;

    //Farm Buttons
    private Bitmap bitmapShopButton;
    private Bitmap bitmapSettingButton;

    //Wo kommen die Buttons hin?
    private int textSize, textX, textY;
    private int bitmapShopButtonX, bitmapShopButtonY;
    private int bitmapSettingButtonX, bitmapSettingButtonY;

    private FarmSettings farmSettings;
    private FarmShop farmShop;

    //Qualität: 250 - 1000
    private int bitmapMainQuality;

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
    private boolean ticktack = true; //für den Uhr Sound

    //Laden wir gerade
    private boolean loading;
    //Laden wir das erste mal
    private boolean initialsed;

    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    FarmMode(Context context, int screenX, int screenY, GlobalVariables globalVariables) {
        //Auf den Context können alle FarmMode Funktionen zugreifen
        fullContext = context;

        //Ladebildschirm anzeigen?
        loading = true;
        initialsed = false;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        //Globale Infos laden
        this.globalVariables = globalVariables;

        bitmapMainQuality = 500;

        //Alle Grafiken einlesen
        initialiseGrafics();

        //Musik einlesen
        initialiseSound(fullContext);

        //Daten einlesen
        getSharedPreferences();

        globalVariables.setSoundOn(true);
    }

    //update ist quasi das DENKEN in der App
    void updateFarm() {
        //Erdbeeren wachsen hier automatisch durch Zeit
        for(int i = 0; i < numStrawberries; i++) {
            strawberries[i].update();
        }
       playSound(0);
    }

    //ZEICHNEN
    void drawFarm(SurfaceHolder ourHolder, Canvas canvas, Paint paint) {
        //Standardfehlerabfangen
        if (ourHolder.getSurface().isValid()) {
            try {
                //canvas wird das Zeichenobjekt
                canvas = ourHolder.lockCanvas();
            } catch (IllegalArgumentException e) {
                ourHolder.unlockCanvasAndPost(canvas);
            }

            try {
                if (loading) {
                    //Loadingscreen
                    if (bitmapBackgroundLoading != null) {
                        canvas.drawBitmap(bitmapBackgroundLoading, 0, 0, paint);
                    }
                } else if (farmSettings != null) {
                    farmSettings.drawFarmSettings(ourHolder, canvas, paint);
                } else if (farmShop != null) {
                    farmShop.drawFarmShop(ourHolder, canvas, paint);
                } else {
                    if (bitmapBackgroundLand != null)
                        canvas.drawBitmap(bitmapBackgroundLand, 0, backgroundLandY1, paint);
                    //Hintergrund Overlay
                    if (bitmapBackgroundOverlay != null)
                        canvas.drawBitmap(bitmapBackgroundOverlay, backgroundOverlayX1, 0, paint);

                    //Pinselfarbe wählen (bisher nur für den Text)
                    paint.setColor(Color.argb(255, 249, 129, 0));
                    paint.setStyle(Paint.Style.FILL);
                    paint.setTextSize(textSize);

                    //Klickcounter malen
                    canvas.drawText("Clicks: " + globalVariables.getClickCount(), textX, textY, paint);

                    //Zustand als Text ausgeben
                    switch (zustand) {
                        case 0:
                            canvas.drawText("Zustand: Aussähen", textX, 2 * textY, paint);
                            break;
                        case 1:
                            canvas.drawText("Zustand: Wachsen", textX, 2 * textY, paint);
                            break;
                        case 2:
                            canvas.drawText("Zustand: Ernten", textX, 2 * textY, paint);
                            break;
                        default:
                            canvas.drawText("Something went wrong :D", textX, 2 * textY, paint);
                            break;
                    }

                    //Anzahl der Erdbeeren
                    canvas.drawText("Erdbeeren: " + numStrawberries, textX, 3 * textY, paint);

                    //Anzahl Gurken
                    canvas.drawText("Gurken: " + numGurken + " | Kosten: " + priceGurken + " Gold", textX, 4 * textY, paint);

                    //Anzahl Land
                    if ((numAecker / 4) >= numLand)
                        canvas.drawText("Land: " + numLand + " | Kosten: " + priceLand + " Gold", textX, 5 * textY, paint);
                        //Anzahl Aecker
                    else
                        canvas.drawText("Äcker: " + numAecker + " | Kosten: " + priceAecker + " Gold", textX, 5 * textY, paint);

                    //Wie viel Gold haben wir eigentlich?
                    canvas.drawText("Gold: " + globalVariables.getGold(), textX, 6 * textY, paint);

                    //Test Wachsstatus Erdbeere 1
                    if (numStrawberries > 0)
                        canvas.drawText("Alter Erdbeere 1: " + strawberries[0].getWachsStatus(), textX, 7 * textY, paint);

                    //Test Button malen
                    if (bitmapSettingButton != null)
                        canvas.drawBitmap(bitmapSettingButton, bitmapSettingButtonX, bitmapSettingButtonY, paint);
                    if (bitmapShopButton != null)
                        canvas.drawBitmap(bitmapShopButton, bitmapShopButtonX, bitmapShopButtonY, paint);
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
    void getSharedPreferences() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        numStrawberries = sharedPreferences.getInt("numStrawberries", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        numLand = sharedPreferences.getInt("numLand", 1);
        String strawberryStatus = sharedPreferences.getString("strawberryStatus", "");
        numGurken = sharedPreferences.getInt("numGurken", 1);
        //Initialisierung der gespeicherten Erdbeeren
        strawberries = new Strawberry[numAecker * AECKER_MAX];

        //um keine IndexoutofBoundException zu bekommen
        if (!(strawberryStatus.equals(""))) {
            //1. String auseinander nehmen, 2. aus den Daten auslesen
            //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
            String[] strawberryStatusStrings = strawberryStatus.split("a");
            int stringsCounter = 0;
            for (int i = 0; i < (numAecker * 16); i++) {
                strawberries[i] = new Strawberry(Integer.parseInt(strawberryStatusStrings[stringsCounter]), Integer.parseInt(strawberryStatusStrings[stringsCounter + 1]), Long.parseLong(strawberryStatusStrings[stringsCounter + 2]));
                stringsCounter += 3;
            }
        } else {
            for (int i = 0; i < (numAecker * 16); i++) {
                strawberries[i] = new Strawberry((i / 16) + 1);
            }
        }

        //Bei der ersten Initialisierung müssen wir noch die Preise einlesen
        if (!initialsed) {
            //Preise initialisieren
            priceAecker = getPrice(0);
            priceGurken = getPrice(1);
            priceLand = getPrice(2);
            initialsed = true;
        }
    }

    //SharedPreferences wieder sicher verwahren
    void setSharedPreferences() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numStrawberries", numStrawberries);
        editor.putInt("numAecker", numAecker);
        editor.putInt("numLand", numLand);
        editor.putInt("numGurken", numGurken);

        //Hier kommen alle derzeitigen Erdbeeren rein um gespeichert zu werden
        StringBuilder strawberryStatus = new StringBuilder();
        //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
        for (int i = 0; i < (numAecker * 16); i++) {
            strawberryStatus.append(strawberries[i].getWachsStatus());
            strawberryStatus.append("a");
            strawberryStatus.append(strawberries[i].getAcker());
            strawberryStatus.append("a");
            strawberryStatus.append(strawberries[i].getTimeThisFruit());
            strawberryStatus.append("a");
        }
        editor.putString("strawberryStatus", strawberryStatus.toString());

        editor.apply();
    }

    //Was passiert wenn man den Touchscreen im FARM Modus berührt
    boolean onTouchFarm(MotionEvent motionEvent) {
        //Alle Arten von Bewegung (auf dem Screen) die man bearbeiten will
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //Spieler berührt den Bildschirm
            case MotionEvent.ACTION_DOWN:
                //Wo befanden wir uns am Anfang?
                touchX1down = motionEvent.getX();
                touchX1 = motionEvent.getX();
                float touchY1 = motionEvent.getY();
                touchPointer = false;

                //Wir erlauben keine Buttonklicks wenn wir gerade laden
                if (!loading) {
                    if (farmShop != null) {
                        farmShop.onTouchFarm(motionEvent);
                        break;
                    }
                    if (farmSettings != null) {
                        farmSettings.onTouchFarmSettings(motionEvent);
                    }
                    //War da ein Button?
                    //fight button
                    if (touchX1 >= bitmapSettingButtonX && touchX1 < (bitmapSettingButtonX + bitmapSettingButton.getWidth())
                            && touchY1 >= bitmapSettingButtonY && touchY1 < (bitmapSettingButtonY + bitmapSettingButton.getHeight())) {
                        playSound(4);
                        farmSettings = new FarmSettings(fullContext, screenX, screenY, globalVariables);
                        break;
                    }
                    //gurke kaufen button
                    if (touchX1 >= bitmapShopButtonX && touchX1 < (bitmapShopButtonX + bitmapShopButton.getWidth())
                            && touchY1 >= bitmapShopButtonY && touchY1 < (bitmapShopButtonY + bitmapShopButton.getHeight())) {
                        playSound(4);
                        farmShop = new FarmShop(fullContext, screenX, screenY, globalVariables);
                        break;
                    }
                    //Wir haben geklickt, in einem Klickergame müssen wir doch mit der Info irgendwas machen oder? :D
                    gotClickedFarm();
                }
                break;

            //Spieler bewegt den Finger auf dem Bildschirm
            case MotionEvent.ACTION_MOVE:
                if (!touchPointer && !loading) {
                    //In welche Richtung hat sich der Finger bewegt? | Differenz der beiden Werte
                    deltaXMove = motionEvent.getX() - touchX1;
                    //wo befinden wir uns in diesem Schritt
                    touchX1 = motionEvent.getX();

                    //Bedingung für die Äußeren Grenzen
                    if (((backgroundOverlayX1 + deltaXMove) < 0) && ((backgroundOverlayX1 + deltaXMove) > (-2 * screenX))) {
                        //Standardmovement (Folge dem Finger)
                        backgroundOverlayX1 += deltaXMove;
                    }
                }
                break;
            //Zweiter Finger kommt dazu:
            case MotionEvent.ACTION_POINTER_DOWN:
                touchPointer = true;
                break;
            case MotionEvent.ACTION_UP:
                //Wie weit hat sich der Finger insgesamt bewegt? | Differenz der beiden Werte
                float deltaXClick = motionEvent.getX() - touchX1down;

                //reset der Hintergrundbildposition

                //Wären wir über den Rand gekommen?
                if (((backgroundOverlayX1 + deltaXClick) > 0) || ((backgroundOverlayX1 + deltaXClick) < (-2 * screenX))) {
                    //Rechts
                    if ((backgroundOverlayX1 + deltaXClick) < 0) {
                        backgroundOverlayX1 = -2 * screenX;
                        zustand = 2;
                        break;
                    }
                    //Links
                    if ((backgroundOverlayX1 + deltaXClick) > (-2 * screenX)) {
                        backgroundOverlayX1 = 0;
                        zustand = 0;
                        break;
                    }
                }
                //Zurückswapen nach Links, Rechts, Mitte wenn nach Stillstand Bedingungen zutreffen

                //Wenn wir von links nach links kommen
                if (backgroundOverlayX1 + deltaXMove > (-0.3 * screenX) && zustand == 0) {
                    backgroundOverlayX1 = 0;
                    zustand = 0;
                    break;
                }
                //wenn wir von der mitte nach links kommen
                if (backgroundOverlayX1 + deltaXMove > (-0.7 * screenX) && zustand == 1) {
                    backgroundOverlayX1 = 0;
                    zustand = 0;
                    break;
                }
                //wenn wir von rechts nach rechts kommen
                if (backgroundOverlayX1 + deltaXMove < (-1.7 * screenX) && zustand == 2) {
                    backgroundOverlayX1 = -2 * screenX;
                    zustand = 2;
                    break;
                }
                //von rechts zur mitte kommen
                if (backgroundOverlayX1 + deltaXMove < (-1.3 * screenX) && zustand == 1) {
                    backgroundOverlayX1 = -2 * screenX;
                    zustand = 2;
                    break;
                }
                //Mitte
                backgroundOverlayX1 = (-1 * screenX);
                zustand = 1;
                break;
        }
        return true;
    }

    //Was passiert wenn der Spieler im FARM Modus klickt?
    private void gotClickedFarm() {
        globalVariables.incrementClickCount();
        switch(zustand) {
            case 0:
                //Aussähen: Prüfen ob noch Platz ist, wenn ja: Aussähen.
                for(int j = 1; j <= numGurken; j++) {
                    if (numStrawberries < (numAecker * 16)) {
                        for (int i = 0; i < numAecker * 16; i++) {
                            if (globalVariables.getGold() >= STRAWBERRY_PRICE && strawberries[i].getWachsStatus() <= -1) {
                                strawberries[i].setStrawberry();
                                numStrawberries++;
                                globalVariables.setGold(globalVariables.getGold() - STRAWBERRY_PRICE);
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
                            globalVariables.setGold(globalVariables.getGold() + 10);
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

    //Jede Art von Sound abspielen
    private void playSound(int whichOne) {
        //whichone Legende: 0 -> Hintergrundmusik; 1 -> Sähsound; 2 -> Uhr Sound; 3 -> Erntesound; 4 -> Buttonklick; 5 -> Geld; 6 -> hit; 7 -> fireballsound; 8 -> knifesharpener

        //Zufallszahl generieren um die aussäh und erntegeräusche abwechslungsreicher zu machen
        Random random = new Random();
        int randomInt = 1;

        for (int i = 1; i <= 9; i++) {
            randomInt = random.nextInt(10);
        }
        switch (whichOne) {
            case 0:
                if (globalVariables.getMusicOn())
                    backgroundMusicPlayer();
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
                        soundPool.play(clock1, 1, 1, 0, 0, 1);
                        ticktack = false;
                    } else {
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

            descriptor = assetManager.openFd("plop7.wav");
            plop7 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop8.wav");
            plop8 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("plop9.wav");
            plop9 = soundPool.load(descriptor, 0);
        } catch (IOException e) {
            //Errormessage
            Log.e("error", "failed to load sound files");
        }
    }

    //Alle Bilder einlesen
    private void initialiseGrafics() {
        //Textposition setzen
        textX = getScaledCoordinates(screenX, 1080, 20);
        textY = getScaledCoordinates(screenY, 1920, 50);

        //Textgröße errechnen
        textSize = getScaledBitmapSize(screenX, 1080, 50);

        //Position des Hintergrundbildes festlegen
        backgroundOverlayX1 = 0;
        backgroundLandY1 = 0;

        //Hintergrundbild:
        //Um das ganze effizient einzufügen müssen wir hier mit den Bitmapfactory Otions spielen
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Nur die Ränder werden eingefügt
        //Hintergrundbild einfügen
        bitmapBackgroundOverlay = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.background_farm1, options);
        //Dadurch, dass wir nur die Ränder haben wird nicht so viel RAM verbraucht und wir können trz die Größe erfahren und rescalen
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapBackgroundOverlay = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.background_farm1, bitmapMainQuality, bitmapMainQuality);
        bitmapBackgroundOverlay = Bitmap.createScaledBitmap(bitmapBackgroundOverlay, screenX * 3, screenY, false);

        //Hintergrundland Bild:
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Nur die Ränder werden eingefügt
        //Hintergrundbild einfügen
        bitmapBackgroundLand = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.background_farm2, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapBackgroundLand = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.background_farm2, bitmapMainQuality, bitmapMainQuality);
        bitmapBackgroundLand = Bitmap.createScaledBitmap(bitmapBackgroundLand, screenX, screenY, false);

        //Loadingscreen Bild:
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Nur die Ränder werden eingefügt
        //Hintergrundbild einfügen
        bitmapBackgroundLoading = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.loadingscreen, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapBackgroundLoading = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.loadingscreen, 250, 250);
        bitmapBackgroundLoading = Bitmap.createScaledBitmap(bitmapBackgroundLoading, screenX, screenY, false);

        //Buttons initialisieren

        //Einstellungen Öffnen Button
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapSettingButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.button_setting, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapSettingButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.button_setting, 100, 100);
        bitmapSettingButton = Bitmap.createScaledBitmap(bitmapSettingButton, getScaledBitmapSize(screenX, 1080, 276), getScaledBitmapSize(screenY, 1920, 289), false);

        //Shop Öffnen Button
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapShopButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.button_shop, options);
        bitmapShopButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.button_shop, 100, 100);
        bitmapShopButton = Bitmap.createScaledBitmap(bitmapShopButton, getScaledBitmapSize(screenX, 1080, 211), getScaledBitmapSize(screenY, 1920, 289), false);

        bitmapSettingButtonX = getScaledCoordinates(screenX, 1080, 814);
        bitmapSettingButtonY = getScaledCoordinates(screenY, 1920, 156);
        bitmapShopButtonX = getScaledCoordinates(screenX, 1080, 603);
        bitmapShopButtonY = getScaledCoordinates(screenY, 1920, 158);

        //Wir haben alles geladen
        loading = false;
    }

    //Gibt den Preis der Elemente aus dem Shop aus
    private int getPrice(int whichOne) {
        //whichOne Legende: 0: Acker, 1: Gurke, 2: Land, 3: Werkzeug
        switch (whichOne) {
            case 0:
                return (int) (50*Math.pow((double) numAecker, 1.7));
            case 1:
                return (int) (500*Math.pow((double) numGurken, 1.5));
            case 2:
                return (int) (50*Math.pow((double) (numLand*8), 1.7));
            case 3:
                return 42;
        }
        return -1;
    }

    //Hintergrundmusikverwaltung
    private void backgroundMusicPlayer() {
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

    //Wenn wir den Modus verlassen
    GlobalVariables recycle() {
        loading = true;
        setSharedPreferences();
        //Audio releasen
        if (backgroundloop1 != null)
            backgroundloop1.pause();
        soundPool.release();
        click1 = -1;
        clock1 = -1;
        clock2 = -1;
        dirt1 = -1;
        dirt2 = -1;
        dirt3 = -1;
        dirt4 = -1;
        dirt5 = -1;
        dirt6 = -1;
        dirt7 = -1;
        dirt8 = -1;
        dirt9 = -1;
        gold1 = -1;
        plop1 = -1;
        plop2 = -1;
        plop3 = -1;
        plop4 = -1;
        plop5 = -1;
        plop6 = -1;
        plop7 = -1;
        plop8 = -1;
        plop9 = -1;

        //Bitmaps Recyclen
        bitmapBackgroundLand.recycle();
        bitmapBackgroundLand = null;
        bitmapBackgroundOverlay.recycle();
        bitmapBackgroundOverlay = null;
        bitmapShopButton.recycle();
        bitmapShopButton = null;
        bitmapSettingButton.recycle();
        bitmapSettingButton = null;

        return globalVariables;
    }
}

