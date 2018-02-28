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

class FarmModeShop {
    //Der gespeicherte Context
    private Context fullContext;

    //Bildschirmkoordinaten
    private int screenX, screenY;

    //Testbuttons FARM erstellen
    private Bitmap bitmapAckerKaufenButton;
    private Bitmap bitmapGurkeKaufenButton;

    //Wo kommen die Buttons hin?
    private int textSize, textX, textY;
    private int bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY;
    private int bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY;

    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;
    private FarmModeSound farmModeSound;
    private FarmModeBackend farmModeBackend;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    FarmModeShop(Context context, int screenX, int screenY) {
        //Auf den Context können alle FarmMode Funktionen zugreifen
        fullContext = context;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        //Globale Infos laden
        globalVariables = GlobalVariables.getInstance();

        globalVariables.setGold(10000000);

        //Musik einlesen
        farmModeSound = FarmModeSound.getInstance(context);

        //Backend Instance bekommen
        farmModeBackend = FarmModeBackend.getInstance(context, screenX);

        //Alle Grafiken einlesen
        initialiseGrafics();
    }

    //ZEICHNEN
    void drawFarmShop(Canvas canvas, Paint paint) {
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
            canvas.drawText("Gurken: " + farmModeBackend.getNumGurken() + " | Kosten: " + farmModeBackend.getPriceGurken() + " Gold", textX, 2 * textY, paint);

            //Anzahl Äcker
            canvas.drawText("Äcker: " + farmModeBackend.getNumAecker() + " | Kosten: " + farmModeBackend.getPriceAecker() + " Gold", textX, 4 * textY, paint);

            //Wie viel Gold haben wir eigentlich?
            canvas.drawText("Gold: " + globalVariables.getGold(), textX, 6 * textY, paint);

            //Button malen
            if (bitmapAckerKaufenButton != null)
                canvas.drawBitmap(bitmapAckerKaufenButton, bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY, paint);
            if (bitmapGurkeKaufenButton != null)
                canvas.drawBitmap(bitmapGurkeKaufenButton, bitmapGurkeKaufenButtonX, bitmapGurkeKaufenButtonY, paint);
        } catch (NullPointerException e) {
            farmModeBackend.setSharedPreferences(fullContext);
        }
    }

    //Was passiert wenn man den Touchscreen im FARM Modus berührt
    void onTouchFarm(MotionEvent motionEvent) {
        //Alle Arten von Bewegung (auf dem Screen) die man bearbeiten will
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //Spieler berührt den Bildschirm
            case MotionEvent.ACTION_DOWN:
                //Wo befanden wir uns am Anfang?
                float touchX1 = motionEvent.getX();
                float touchY1 = motionEvent.getY();

                if (touchX1 >= bitmapAckerKaufenButtonX && touchX1 < (bitmapAckerKaufenButtonX + bitmapAckerKaufenButton.getWidth())
                        && touchY1 >= bitmapAckerKaufenButtonY && touchY1 < (bitmapAckerKaufenButtonY + bitmapAckerKaufenButton.getHeight())) {
                    if (globalVariables.getGold() >= (farmModeBackend.getPriceAecker() + farmModeBackend.getSTRAWBERRY_PRICE())) {
                        farmModeBackend.ackerGekauft();
                        farmModeSound.playSound(5, fullContext);
                    } else
                        farmModeSound.playSound(4, fullContext);
                    break;
                }
                //gurke kaufen button
                if (touchX1 >= bitmapGurkeKaufenButtonX && touchX1 < (bitmapGurkeKaufenButtonX + bitmapGurkeKaufenButton.getWidth())
                        && touchY1 >= bitmapGurkeKaufenButtonY && touchY1 < (bitmapGurkeKaufenButtonY + bitmapGurkeKaufenButton.getHeight())) {
                    if (globalVariables.getGold() >= (farmModeBackend.getPriceGurken() + farmModeBackend.getSTRAWBERRY_PRICE())) {
                        farmModeBackend.gurkeGekauft();
                        farmModeSound.playSound(5, fullContext);
                    } else
                        farmModeSound.playSound(4, fullContext);
                    break;
                }

            //Spieler bewegt den Finger auf dem Bildschirm
            case MotionEvent.ACTION_MOVE:
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
        //Buttons initialisieren

        //Acker Kaufen Button
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapAckerKaufenButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.ackerkaufen_button, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapAckerKaufenButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.ackerkaufen_button, 100, 100);
        bitmapAckerKaufenButton = Bitmap.createScaledBitmap(bitmapAckerKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Gurke Kaufen Button
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapGurkeKaufenButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.gurkekaufen_button, options);
        bitmapGurkeKaufenButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.gurkekaufen_button, 100, 100);
        bitmapGurkeKaufenButton = Bitmap.createScaledBitmap(bitmapGurkeKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Feste Werte setzen
        bitmapAckerKaufenButtonX = getScaledCoordinates(screenX, 1080, 20);
        bitmapAckerKaufenButtonY = getScaledCoordinates(screenY, 1920, 400);
        bitmapGurkeKaufenButtonX = getScaledCoordinates(screenX, 1080, 270);
        bitmapGurkeKaufenButtonY = bitmapAckerKaufenButtonY;
    }

    //Wenn wir den Modus verlassen
    void recycle() {
        //Bitmaps Recyclen
        bitmapAckerKaufenButton.recycle();
        bitmapAckerKaufenButton = null;
        bitmapGurkeKaufenButton.recycle();
        bitmapGurkeKaufenButton = null;
    }
}
