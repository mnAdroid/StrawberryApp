package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;
import com.cucumbertroup.strawberry.strawberry.R;

import java.io.IOException;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

public class FarmShop {
    //Der gespeicherte Context
    private Context fullContext;

    //Bildschirmkoordinaten
    private int screenX, screenY;

    //Ort der letzten Berührung auf dem Bildschirm
    private float touchX1;
    private float touchX1down;
    private boolean touchPointer = false;
    //Abstand der letzten Bewegung auf dem Bildschirm
    private float deltaXMove;

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

    //Testbuttons FARM erstellen
    private Bitmap bitmapAckerKaufenButton;
    private Bitmap bitmapLandKaufenButton;
    private Bitmap bitmapGurkeKaufenButton;

    //Wo kommen die Buttons hin?
    private int textSize, textX, textY;
    private int bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY;
    private int bitmapLandKaufenButtonX, bitmapLandKaufenButtonY;
    private int bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY;

    //Musik initialisieren
    private SoundPool soundPool;
    private int click1 = -1;
    private int gold1 = -1;

    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    FarmShop(Context context, int screenX, int screenY, GlobalVariables globalVariables) {
        //Auf den Context können alle FarmMode Funktionen zugreifen
        fullContext = context;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        //Globale Infos laden
        this.globalVariables = globalVariables;

        //Alle Grafiken einlesen
        initialiseGrafics();

        //Musik einlesen
        initialiseSound(fullContext);

        //Daten einlesen
        getSharedPreferences();
    }

    //ZEICHNEN
    void drawFarmShop(SurfaceHolder ourHolder, Canvas canvas, Paint paint) {
        try {
            //Standardfehlerabfangen
            //Hintergrund ist erstmal einfach schwarz
            //Pinselfarbe wählen (bisher nur für den Text)
            canvas.drawColor(Color.BLACK);
            //Pinselfarbe wählen (bisher nur für den Text)
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(textSize);

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

            //Button malen
            if (bitmapAckerKaufenButton != null)
                canvas.drawBitmap(bitmapAckerKaufenButton, bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY, paint);
            if (bitmapLandKaufenButton != null && (numAecker / 4) >= numLand)
                canvas.drawBitmap(bitmapLandKaufenButton, bitmapLandKaufenButtonX, bitmapLandKaufenButtonY, paint);
            if (bitmapGurkeKaufenButton != null)
                canvas.drawBitmap(bitmapGurkeKaufenButton, bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY, paint);
        } catch (NullPointerException e) {
            setSharedPreferences();
        }
    }

    //SharedPreferences auslesen
    private void getSharedPreferences() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        numLand = sharedPreferences.getInt("numLand", 1);
        numGurken = sharedPreferences.getInt("numGurken", 1);

        //Preise initialisieren
        priceAecker = getPrice(0);
        priceGurken = getPrice(1);
        priceLand = getPrice(2);
    }

    //SharedPreferences wieder sicher verwahren
    private void setSharedPreferences() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numAecker", numAecker);
        editor.putInt("numLand", numLand);
        editor.putInt("numGurken", numGurken);

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

                if ((numAecker / 4) < numLand && touchX1 >= bitmapAckerKaufenButtonX && touchX1 < (bitmapAckerKaufenButtonX + bitmapAckerKaufenButton.getWidth())
                        && touchY1 >= bitmapAckerKaufenButtonY && touchY1 < (bitmapAckerKaufenButtonY + bitmapAckerKaufenButton.getHeight())) {
                    if (globalVariables.getGold() >= (priceAecker + STRAWBERRY_PRICE) && numAecker < AECKER_MAX) {
                        ackerGekauft();
                        playSound(5);
                    } else
                        playSound(4);
                    break;
                }
                //land kaufen Button
                if ((numAecker / 4) >= numLand && touchX1 >= bitmapLandKaufenButtonX && touchX1 < (bitmapLandKaufenButtonX + bitmapLandKaufenButton.getWidth())
                        && touchY1 >= bitmapLandKaufenButtonY && touchY1 < (bitmapLandKaufenButtonY + bitmapLandKaufenButton.getHeight())) {
                    if (globalVariables.getGold() >= (priceLand + STRAWBERRY_PRICE) && numLand < LAND_MAX) {
                        numLand++;
                        globalVariables.setGold(globalVariables.getGold() - priceLand);
                        priceLand = getPrice(2);
                        playSound(5);
                    } else
                        playSound(4);
                    break;
                }
                //gurke kaufen button
                if (touchX1 >= bitmapGurkeKaufenButtonX && touchX1 < (bitmapGurkeKaufenButtonX + bitmapGurkeKaufenButton.getWidth())
                        && touchY1 >= bitmapGurkeKaufenButtonY && touchY1 < (bitmapGurkeKaufenButtonY + bitmapGurkeKaufenButton.getHeight())) {
                    if (globalVariables.getGold() >= (priceGurken + 1)) {
                        numGurken++;
                        globalVariables.setGold(globalVariables.getGold() - priceGurken);
                        priceGurken = getPrice(1);
                        playSound(5);
                    } else
                        playSound(4);
                    break;
                }

            //Spieler bewegt den Finger auf dem Bildschirm
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }

    //Jede Art von Sound abspielen
    private void playSound(int whichOne) {
        //whichone Legende: 4 -> Buttonklick; 5 -> Geld
        switch (whichOne) {
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
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //Musik tatsächlich einladen
            descriptor = assetManager.openFd("click1.wav");
            click1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("gold1.wav");
            gold1 = soundPool.load(descriptor, 0);
        } catch (Exception e) {
            //Errormessage
            Log.e("error", "failed to load sound files: " + e.toString());
        }
    }

    //Wenn ein Acker gekauft wurde
    private void ackerGekauft() {
        //Anzahl hochzählen und Gold abbuchen
        numAecker++;
        globalVariables.setGold(globalVariables.getGold() - priceAecker);
        priceAecker = getPrice(0);
    }

    //Alle Bilder einlesen
    private void initialiseGrafics() {
        //Textposition setzen
        textX = getScaledCoordinates(screenX, 1080, 20);
        textY = getScaledCoordinates(screenY, 1920, 50);

        //Textgröße errechnen
        textSize = getScaledBitmapSize(screenX, 1080, 50);
        //Buttons initialisieren

        //Acker Kaufen Button
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapAckerKaufenButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.ackerkaufen_button, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapAckerKaufenButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.ackerkaufen_button, 100, 100);
        bitmapAckerKaufenButton = Bitmap.createScaledBitmap(bitmapAckerKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Land Kaufen Button
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapLandKaufenButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.landkaufen_button, options);
        bitmapLandKaufenButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.landkaufen_button, 100, 100);
        bitmapLandKaufenButton = Bitmap.createScaledBitmap(bitmapLandKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Gurke Kaufen Button
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapGurkeKaufenButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.gurkekaufen_button, options);
        bitmapGurkeKaufenButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.gurkekaufen_button, 100, 100);
        bitmapGurkeKaufenButton = Bitmap.createScaledBitmap(bitmapGurkeKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Feste Werte setzen
        bitmapAckerKaufenButtonX = getScaledCoordinates(screenX, 1080, 20);
        bitmapAckerKaufenButtonY = getScaledCoordinates(screenY, 1920, 400);
        bitmapLandKaufenButtonX = bitmapAckerKaufenButtonX;
        bitmapLandKaufenButtonY = bitmapAckerKaufenButtonY;
        bitmapGurkeKaufenButtonX = getScaledCoordinates(screenX, 1080, 520);
        bitmapGurkeKaufenButtonY = bitmapAckerKaufenButtonY;
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

    //Wenn wir den Modus verlassen
    GlobalVariables recycle() {
        soundPool.release();
        click1 = -1;
        gold1 = -1;

        //Bitmaps Recyclen
        bitmapAckerKaufenButton.recycle();
        bitmapAckerKaufenButton = null;
        bitmapLandKaufenButton.recycle();
        bitmapLandKaufenButton = null;
        bitmapGurkeKaufenButton.recycle();
        bitmapGurkeKaufenButton = null;

        return globalVariables;
    }
}
