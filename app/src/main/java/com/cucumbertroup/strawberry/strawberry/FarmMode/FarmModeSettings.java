package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;
import com.cucumbertroup.strawberry.strawberry.R;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

class FarmModeSettings {
    //Der gespeicherte Context
    private Context fullContext;

    //Bildschirmkoordinaten
    private int screenX, screenY;

    //Testbuttons FARM erstellen
    private Bitmap bitmapFightButton;
    private Bitmap bitmapMusikAnAusButton;

    //Wo kommen die Buttons hin?
    private int textSize, textX, textY;
    private int bitmapFightButtonX, bitmapFightButtonY;
    private int bitmapMusikAnAusButtonX, bitmapMusikAnAusButtonY;

    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;
    private FarmModeSound farmModeSound;

    //effizienteres Canvaszeichnen
    private int update;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    FarmModeSettings(Context context, int screenX, int screenY) {
        //Auf den Context können alle FarmMode Funktionen zugreifen
        fullContext = context;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        //Globale Infos laden
        globalVariables = GlobalVariables.getInstance();

        //Musik einlesen
        farmModeSound = FarmModeSound.getInstance();

        //Alle Grafiken einlesen
        initialiseGrafics();
        }

    //ZEICHNEN
    void drawFarmSettings(Canvas canvas, Paint paint) {
        try {
            if (update <= 5) {
                canvas.drawColor(Color.BLACK);
                //Pinselfarbe wählen (bisher nur für den Text)
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(textSize);

                //Klickcounter malen
                canvas.drawText("Clicks: " + globalVariables.getClickCount(), textX, 2 * textY, paint);

                //Test Button malen
                if (bitmapFightButton != null)
                    canvas.drawBitmap(bitmapFightButton, bitmapFightButtonX, bitmapFightButtonY, paint);
                if (bitmapMusikAnAusButton != null)
                    canvas.drawBitmap(bitmapMusikAnAusButton, bitmapMusikAnAusButtonX, bitmapMusikAnAusButtonY, paint);

                //es wurde alles gemalt
                update++;
            }
        } catch (NullPointerException e) {
            recycle();
        }
    }

    //Was passiert wenn man den Touchscreen im FARM Modus berührt
    void onTouchFarmSettings(MotionEvent motionEvent) {
        //Alle Arten von Bewegung (auf dem Screen) die man bearbeiten will
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //Spieler berührt den Bildschirm
            case MotionEvent.ACTION_DOWN:
                float touchX1 = motionEvent.getX();
                float touchY1 = motionEvent.getY();

                //fight button
                if (touchX1 >= bitmapFightButtonX && touchX1 < (bitmapFightButtonX + bitmapFightButton.getWidth())
                        && touchY1 >= bitmapFightButtonY && touchY1 < (bitmapFightButtonY + bitmapFightButton.getHeight())) {
                    farmModeSound.playSound(4, fullContext);
                    globalVariables.setGameMode(1);
                    break;
                }
                //musik an aus button
                if (touchX1 >= bitmapMusikAnAusButtonX && touchX1 < (bitmapMusikAnAusButtonX + bitmapMusikAnAusButton.getWidth())
                        && touchY1 >= bitmapMusikAnAusButtonY && touchY1 < (bitmapMusikAnAusButtonY + bitmapMusikAnAusButton.getHeight())) {
                    farmModeSound.playSound(4, fullContext);
                    if (globalVariables.getSoundOn()) {
                        globalVariables.setMusicOn(false);
                        //Musik pausieren muss noch rein
                        farmModeSound.pauseMusic();
                        globalVariables.setSoundOn(false);
                    } else {
                        globalVariables.setMusicOn(true);
                        farmModeSound.playSound(0, fullContext);
                        globalVariables.setSoundOn(true);
                    }
                    break;
                }
                break;
        }
    }

    //Alle Bilder einlesen
    private void initialiseGrafics() {
        //Textposition setzen
        textX = getScaledCoordinates(screenX, 1080, 20);
        textY = getScaledCoordinates(screenY, 1920, 50);

        //Textgröße errechnen
        textSize = getScaledBitmapSize(screenX, 1080, 50);

        //Fight Button
        bitmapFightButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.fight_button, 100, 100);
        bitmapFightButton = Bitmap.createScaledBitmap(bitmapFightButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Musik Button
        bitmapMusikAnAusButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.musikanaus_button, 100, 100);
        bitmapMusikAnAusButton = Bitmap.createScaledBitmap(bitmapMusikAnAusButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Feste Werte setzen

        bitmapFightButtonX = getScaledCoordinates(screenX, 1080, 270);
        bitmapFightButtonY = 4 * textY;
        bitmapMusikAnAusButtonX = getScaledCoordinates(screenX, 1080, 20);
        bitmapMusikAnAusButtonY = 4 * textY;

        //beim Öffnen der Einstellungen wird gemalt
        update = 0;
    }

    //Wenn wir den Modus verlassen
    void recycle() {
        //Bitmaps Recyclen
        bitmapFightButton.recycle();
        bitmapFightButton = null;
        bitmapMusikAnAusButton.recycle();
        bitmapMusikAnAusButton = null;
    }
}


