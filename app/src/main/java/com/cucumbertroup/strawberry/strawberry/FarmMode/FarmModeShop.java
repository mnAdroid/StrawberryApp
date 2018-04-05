package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;
import com.cucumbertroup.strawberry.strawberry.R;


import java.util.ArrayList;

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
    private int bitmapShopElement1ButtonX, bitmapShopElement1ButtonY;
    private int bitmapShopElement2ButtonX, bitmapShopElement2ButtonY;
    private int bitmapShopElement3ButtonX, bitmapShopElement3ButtonY;


    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;
    private FarmModeSound farmModeSound;
    private FarmModeBackend farmModeBackend;

    //FreedBitmaps?
    private boolean recycled;

    //Die drei Shop Items
    private ArrayList<FarmModeShopElement> farmModeShopElements;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    FarmModeShop(Context context, int screenX, int screenY) {
        //Auf den Context können alle FarmMode Funktionen zugreifen
        fullContext = context;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        //Globale Infos laden
        globalVariables = GlobalVariables.getInstance();

        //Musik einlesen
        farmModeSound = FarmModeSound.getInstance();

        //Backend Instance bekommen
        farmModeBackend = FarmModeBackend.getInstance(screenX);

        //Alle Grafiken einlesen
        initialiseGrafics();

        //Elemente einlesen
        farmModeShopElements = farmModeBackend.getShopElements(context);

        recycled = false;

        globalVariables.setGold(globalVariables.getGold() + 10000);
    }

    //ZEICHNEN
    void drawFarmShop(Canvas canvas, Paint paint) {
        if (!recycled) {
            try {
                //Standardfehlerabfangen
                //Hintergrund ist erstmal einfach schwarz
                //Pinselfarbe wählen (bisher nur für den Text)
                canvas.drawColor(Color.BLACK);
                //Pinselfarbe wählen (bisher nur für den Text)
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(textSize);

                //Wie viel Gold haben wir eigentlich?
                canvas.drawText("Gold: " + globalVariables.getGold(), textX, 2 * textY, paint);

                //Anzahl Äcker
                canvas.drawText("Äcker: " + farmModeBackend.getNumAecker() + " | Kosten: " + farmModeBackend.getPriceAecker() + " Gold", textX, 4 * textY, paint);

                //Elementeinfo malen
                if (farmModeShopElements != null && farmModeShopElements.size() == 3) {
                    //Erstes Element
                    if (farmModeShopElements.get(0) != null) {
                        if (farmModeShopElements.get(0).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                            canvas.drawText(farmModeShopElements.get(0).getName() + " | Kosten: " + farmModeShopElements.get(0).getPrice() + " Gold", textX, 7 * textY, paint);
                        else
                            canvas.drawText(farmModeShopElements.get(0).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(0).getNecessaryAecker(), textX, 7 * textY, paint);
                    }
                    if (farmModeShopElements.get(1) != null) {
                        if (farmModeShopElements.get(1).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                            canvas.drawText(farmModeShopElements.get(1).getName() + " | Kosten: " + farmModeShopElements.get(1).getPrice() + " Gold", textX, 10 * textY, paint);
                        else
                            canvas.drawText(farmModeShopElements.get(1).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(1).getNecessaryAecker(), textX, 10 * textY, paint);
                    }
                    if (farmModeShopElements.get(2) != null) {
                        if (farmModeShopElements.get(2).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                            canvas.drawText(farmModeShopElements.get(2).getName() + " | Kosten: " + farmModeShopElements.get(2).getPrice() + " Gold", textX, 13 * textY, paint);
                        else
                            canvas.drawText(farmModeShopElements.get(2).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(2).getNecessaryAecker(), textX, 13 * textY, paint);
                    }
                }
                else {
                    farmModeShopElements = farmModeBackend.getShopElements(fullContext);
                }

                //Button malen
                if (bitmapAckerKaufenButton != null)
                    canvas.drawBitmap(bitmapAckerKaufenButton, bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY, paint);
                if (bitmapGurkeKaufenButton != null) {
                    canvas.drawBitmap(bitmapGurkeKaufenButton, bitmapShopElement1ButtonX, bitmapShopElement1ButtonY, paint);
                    canvas.drawBitmap(bitmapGurkeKaufenButton, bitmapShopElement2ButtonX, bitmapShopElement2ButtonY, paint);
                    canvas.drawBitmap(bitmapGurkeKaufenButton, bitmapShopElement3ButtonX, bitmapShopElement3ButtonY, paint);
                }

            } catch (NullPointerException e) {
                if (farmModeBackend != null)
                    farmModeBackend.setSharedPreferences(fullContext);
                Log.e("drawShop", e.toString());
            }
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
                    if (globalVariables.getGold() >= (farmModeBackend.getPriceAecker() + farmModeBackend.getStrawberryPrice())) {
                        farmModeBackend.ackerGekauft();
                        farmModeSound.playSound(5, fullContext);
                    } else
                        farmModeSound.playSound(4, fullContext);
                    break;
                }
                if (farmModeShopElements != null && farmModeShopElements.size() == 3) {
                    //Element 1 kaufen button
                    if (touchX1 >= bitmapShopElement1ButtonX && touchX1 < (bitmapShopElement1ButtonX + bitmapGurkeKaufenButton.getWidth())
                            && touchY1 >= bitmapShopElement1ButtonY && touchY1 < (bitmapShopElement1ButtonY + bitmapGurkeKaufenButton.getHeight())) {
                        if (farmModeShopElements.get(0) != null && globalVariables.getGold() >= (farmModeShopElements.get(0).getPrice() + farmModeBackend.getStrawberryPrice())) {
                            farmModeShopElements = farmModeBackend.buyShopElements(fullContext, farmModeShopElements.get(0));
                            farmModeSound.playSound(5, fullContext);
                        } else
                            farmModeSound.playSound(4, fullContext);
                    }

                    //Element 2 kaufen button
                    if (touchX1 >= bitmapShopElement2ButtonX && touchX1 < (bitmapShopElement2ButtonX + bitmapGurkeKaufenButton.getWidth())
                            && touchY1 >= bitmapShopElement2ButtonY && touchY1 < (bitmapShopElement2ButtonY + bitmapGurkeKaufenButton.getHeight())) {
                        if (farmModeShopElements.get(1) != null && globalVariables.getGold() >= (farmModeShopElements.get(1).getPrice() + farmModeBackend.getStrawberryPrice())) {
                            farmModeShopElements = farmModeBackend.buyShopElements(fullContext, farmModeShopElements.get(1));
                            farmModeSound.playSound(5, fullContext);
                        } else
                            farmModeSound.playSound(4, fullContext);
                    }

                    //Element 3 kaufen button
                    if (touchX1 >= bitmapShopElement3ButtonX && touchX1 < (bitmapShopElement3ButtonX + bitmapGurkeKaufenButton.getWidth())
                            && touchY1 >= bitmapShopElement3ButtonY && touchY1 < (bitmapShopElement3ButtonY + bitmapGurkeKaufenButton.getHeight())) {
                        if (farmModeShopElements.get(2) != null && globalVariables.getGold() >= (farmModeShopElements.get(2).getPrice() + farmModeBackend.getStrawberryPrice())) {
                            farmModeShopElements = farmModeBackend.buyShopElements(fullContext, farmModeShopElements.get(2));
                            farmModeSound.playSound(5, fullContext);
                        } else
                            farmModeSound.playSound(4, fullContext);
                    }

                }
                //Bei Fehler beim Kaufen wird farmModeShopElements = null sein
                if (farmModeShopElements == null) {
                    farmModeShopElements = farmModeBackend.getShopElements(fullContext);
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
        bitmapAckerKaufenButtonX = getScaledCoordinates(screenX, 1080, 800);
        bitmapAckerKaufenButtonY = getScaledCoordinates(screenY, 1920, 140);
        bitmapShopElement1ButtonX = bitmapAckerKaufenButtonX;
        bitmapShopElement1ButtonY = getScaledCoordinates(screenY, 1920, 290);
        bitmapShopElement2ButtonX = bitmapShopElement1ButtonX;
        bitmapShopElement2ButtonY = getScaledCoordinates(screenY, 1920, 440);
        bitmapShopElement3ButtonX = bitmapShopElement1ButtonX;
        bitmapShopElement3ButtonY = getScaledCoordinates(screenY, 1920, 590);
    }

    //Wenn wir den Modus verlassen
    void recycle() {
        recycled = true;
        //Bitmaps Recyclen
        bitmapAckerKaufenButton.recycle();
        bitmapAckerKaufenButton = null;
        bitmapGurkeKaufenButton.recycle();
        bitmapGurkeKaufenButton = null;
    }
}
