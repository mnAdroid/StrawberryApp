package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
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

    //Background
    private Bitmap bitmapShopBackground;
    //nicht interaktive Bitmaps:
    private Bitmap bitmapShopKeeper;
    //p2w Button
    private Bitmap bitmapP2WButton;
    //popup für mehr info von Schopelemente
    private Bitmap bitmapPopUpWindow;

    //Testbuttons FARM erstellen
    private Bitmap bitmapAckerKaufenButton;
    private Bitmap bitmapGurkeKaufenButton;

    //Wo kommen die Buttons hin?
    private int bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY;
    private int bitmapShopElement1ButtonX, bitmapShopElement1ButtonY;
    private int bitmapShopElement2ButtonX, bitmapShopElement2ButtonY;
    private int bitmapShopElement3ButtonX, bitmapShopElement3ButtonY;

    //Wo kommen die Grafikelemente hin?
    private int bitmapShopKeeperX, bitmapShopKeeperY;
    private int bitmapP2WButtonX, bitmapP2WButtonY;
    private int bitmapPopUpWindowX, bitmapPopUpWindowY;

    private int textSize, textX, textY;

    //Wo kommen die Texte des PopUp Fensters hin?
    private int popupTextHeaderX, popupTextHeaderY, popupTextHeaderSize;
    private int popupTextPriceX, popupTextPriceY;
    private int popupTextDescriptionX, popupTextDescriptionY, getPopupTextDescriptionWidth;

    private String popupTextHeader, popupTextPrice;

    //Anzeige des Beschreibungstexts benötigt extra Arbeit damit AUTOMATISCHE ZEILENUMBRÜCHE funktionieren!
    private TextPaint mTextPaint;
    private StaticLayout mTextLayout;

    private Rect popupRectangle;
    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;
    private FarmModeSound farmModeSound;
    private FarmModeBackend farmModeBackend;

    //FreedBitmaps?
    private boolean recycled;

    //Warum mehr als einmal malen
    private int update;

    //Wird das Popup Window derzeit angezeigt?
    private boolean popUp;

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
    }

    //ZEICHNEN
    void drawFarmShop(Canvas canvas, Paint paint) {
        if (!recycled && update<=5) {
            try {
                //Popup muss gemalt werden
                if (popUp) {
                    //Den Rest des Shops ausgrauen
                    paint.setColor(Color.argb(100, 128, 128, 128));
                    canvas.drawRect(popupRectangle, paint);
                    paint.setColor(Color.argb(255, 255, 255, 255)); //Weiss reset

                    //PopUp anzeigen
                    if (bitmapPopUpWindow != null)
                        canvas.drawBitmap(bitmapPopUpWindow, bitmapPopUpWindowX, bitmapPopUpWindowY, paint);

                    //Text malen
                    //Header:
                    paint.setTextSize(popupTextHeaderSize);
                    canvas.rotate(-3, popupTextHeaderX, popupTextHeaderY); //leicht schräg malen
                    canvas.drawText(popupTextHeader, popupTextHeaderX, popupTextHeaderY, paint);
                    canvas.rotate(3, popupTextHeaderX, popupTextHeaderY); //reset

                    //Preis zeichnen
                    paint.setColor(Color.argb(255, 0, 0, 0)); //Schwarz
                    canvas.drawText(popupTextPrice, popupTextPriceX, popupTextPriceY, paint);

                    //Description Box zeichnen
                    canvas.translate(popupTextDescriptionX, popupTextDescriptionY);
                    mTextLayout.draw(canvas);
                } else {
                    //Hintergrund malen
                    if (bitmapShopBackground != null)
                        canvas.drawBitmap(bitmapShopBackground, 0, 0, paint);
                    //Shopkeeper malen
                    if (bitmapShopKeeper != null)
                        canvas.drawBitmap(bitmapShopKeeper, bitmapShopKeeperX, bitmapShopKeeperY, paint);
                    //P2W Button malen
                    if (bitmapP2WButton != null)
                        canvas.drawBitmap(bitmapP2WButton, bitmapP2WButtonX, bitmapP2WButtonY, paint);

                    //Pinselfarbe wählen (bisher nur für den Text)
                    paint.setColor(Color.argb(255, 249, 129, 0));
                    paint.setStyle(Paint.Style.FILL);
                    paint.setTextSize(textSize);

                    //Wie viel Gold haben wir eigentlich?
                    canvas.drawText("Gold: " + globalVariables.getGold(), textX, 2 * textY, paint);

                    //Anzahl Äcker
                    canvas.drawText("Äcker: " + farmModeBackend.getNumAecker() + " | Kosten: " + farmModeBackend.getPriceAecker() + " Gold", textX, 4 * textY, paint);

                    //Elementeinfo malen
                    if (farmModeShopElements != null) {
                        //Erstes Element
                        if (farmModeShopElements.size() >= 1 && farmModeShopElements.get(0) != null) {
                            if (farmModeShopElements.get(0).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                                canvas.drawText(farmModeShopElements.get(0).getName() + " | Kosten: " + farmModeShopElements.get(0).getPrice() + " Gold", textX, 7 * textY, paint);
                            else
                                canvas.drawText(farmModeShopElements.get(0).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(0).getNecessaryAecker(), textX, 7 * textY, paint);
                        }
                        if (farmModeShopElements.size() >= 2 && farmModeShopElements.get(1) != null) {
                            if (farmModeShopElements.get(1).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                                canvas.drawText(farmModeShopElements.get(1).getName() + " | Kosten: " + farmModeShopElements.get(1).getPrice() + " Gold", textX, 10 * textY, paint);
                            else
                                canvas.drawText(farmModeShopElements.get(1).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(1).getNecessaryAecker(), textX, 10 * textY, paint);
                        }
                        if (farmModeShopElements.size() == 3 && farmModeShopElements.get(2) != null) {
                            if (farmModeShopElements.get(2).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                                canvas.drawText(farmModeShopElements.get(2).getName() + " | Kosten: " + farmModeShopElements.get(2).getPrice() + " Gold", textX, 13 * textY, paint);
                            else
                                canvas.drawText(farmModeShopElements.get(2).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(2).getNecessaryAecker(), textX, 13 * textY, paint);
                        }
                    } else {
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

                }
                //Es wurde gemalt
                update++;

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

                //Popup an?
                if (popUp) {
                    //Außerhalb des Fensters geklickt?
                    if (touchX1 < bitmapPopUpWindowX || touchX1 >= (bitmapPopUpWindowX + bitmapPopUpWindow.getWidth())
                            || touchY1 < bitmapPopUpWindowY || touchY1 >= (bitmapPopUpWindowY + bitmapPopUpWindow.getHeight())) {
                        popUp = false;
                        update = 0;
                        break;
                    }
                    else {
                        break;
                    }
                }
                else {
                    if (touchX1 >= bitmapAckerKaufenButtonX && touchX1 < (bitmapAckerKaufenButtonX + bitmapAckerKaufenButton.getWidth())
                            && touchY1 >= bitmapAckerKaufenButtonY && touchY1 < (bitmapAckerKaufenButtonY + bitmapAckerKaufenButton.getHeight())) {
                        if (globalVariables.getGold() >= (farmModeBackend.getPriceAecker() + farmModeBackend.getStrawberryPrice())) {
                            farmModeBackend.ackerGekauft();
                            farmModeSound.playSound(5, fullContext);
                        } else
                            farmModeSound.playSound(4, fullContext);
                        break;
                    }
                    if (farmModeShopElements != null && farmModeShopElements.size() >= 1 && farmModeShopElements.get(0) != null) {
                        //Element 1 kaufen button
                        if (touchX1 >= bitmapShopElement1ButtonX && touchX1 < (bitmapShopElement1ButtonX + bitmapGurkeKaufenButton.getWidth())
                                && touchY1 >= bitmapShopElement1ButtonY && touchY1 < (bitmapShopElement1ButtonY + bitmapGurkeKaufenButton.getHeight())) {
                            showPopUpWindow(farmModeShopElements.get(0));
                            farmModeSound.playSound(4, fullContext);
                        }
                    }
                    if (farmModeShopElements != null && farmModeShopElements.size() >= 2 && farmModeShopElements.get(1) != null) {
                        //Element 2 kaufen button
                        if (touchX1 >= bitmapShopElement2ButtonX && touchX1 < (bitmapShopElement2ButtonX + bitmapGurkeKaufenButton.getWidth())
                                && touchY1 >= bitmapShopElement2ButtonY && touchY1 < (bitmapShopElement2ButtonY + bitmapGurkeKaufenButton.getHeight())) {
                            showPopUpWindow(farmModeShopElements.get(1));
                            farmModeSound.playSound(4, fullContext);
                        }
                    }

                    if (farmModeShopElements != null && farmModeShopElements.size() == 3 && farmModeShopElements.get(2) != null) {
                        //Element 3 kaufen button
                        if (touchX1 >= bitmapShopElement3ButtonX && touchX1 < (bitmapShopElement3ButtonX + bitmapGurkeKaufenButton.getWidth())
                                && touchY1 >= bitmapShopElement3ButtonY && touchY1 < (bitmapShopElement3ButtonY + bitmapGurkeKaufenButton.getHeight())) {
                            showPopUpWindow(farmModeShopElements.get(2));
                            farmModeSound.playSound(4, fullContext);
                        }
                    }

                    //Bei Fehler beim Kaufen wird farmModeShopElements = null sein
                    if (farmModeShopElements == null) {
                        farmModeShopElements = farmModeBackend.getShopElements(fullContext);
                        break;
                    }
                }
        }
    }

    //Alle Bilder einlesen
    private void initialiseGrafics() {
        //Textposition setzen
        textX = getScaledCoordinates(screenX, 1080, 20);
        textY = getScaledCoordinates(screenY, 1920, 50);

        //Textgröße errechnen
        textSize = getScaledBitmapSize(screenX, 1080, 50);

        //Rechteck einstellen
        popupRectangle = new Rect(0, 0, screenX, screenY);
        //Bitmaps initialisieren

        //Acker Kaufen Button
        bitmapAckerKaufenButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.ackerkaufen_button, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapAckerKaufenButton = Bitmap.createScaledBitmap(bitmapAckerKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Gurke Kaufen Button
        bitmapGurkeKaufenButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.gurkekaufen_button, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapGurkeKaufenButton = Bitmap.createScaledBitmap(bitmapGurkeKaufenButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Hintergrund
        bitmapShopBackground = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shop_background, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapShopBackground = Bitmap.createScaledBitmap(bitmapShopBackground, screenX, screenY, false);

        //Shop Keeper
        bitmapShopKeeper = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopkeeper, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapShopKeeper = Bitmap.createScaledBitmap(bitmapShopKeeper, getScaledBitmapSize(screenX, 1080, 655), getScaledBitmapSize(screenY, 1920, 698), false);

        //P2W Button
        bitmapP2WButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.p2w_box, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapP2WButton = Bitmap.createScaledBitmap(bitmapP2WButton, getScaledBitmapSize(screenX, 1080, 311), getScaledBitmapSize(screenY, 1920, 206), false);

        //PopUp Info
        bitmapPopUpWindow = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shop_popup_window, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapPopUpWindow = Bitmap.createScaledBitmap(bitmapPopUpWindow, getScaledBitmapSize(screenX, 1080, 913), getScaledBitmapSize(screenY, 1920, 953), false);

        //Feste Werte setzen
        bitmapAckerKaufenButtonX = getScaledCoordinates(screenX, 1080, 800);
        bitmapAckerKaufenButtonY = getScaledCoordinates(screenY, 1920, 140);
        bitmapShopElement1ButtonX = bitmapAckerKaufenButtonX;
        bitmapShopElement1ButtonY = getScaledCoordinates(screenY, 1920, 290);
        bitmapShopElement2ButtonX = bitmapShopElement1ButtonX;
        bitmapShopElement2ButtonY = getScaledCoordinates(screenY, 1920, 440);
        bitmapShopElement3ButtonX = bitmapShopElement1ButtonX;
        bitmapShopElement3ButtonY = getScaledCoordinates(screenY, 1920, 590);

        bitmapShopKeeperX = getScaledCoordinates(screenX, 1080, 425);
        bitmapShopKeeperY = getScaledCoordinates(screenY, 1920, 1222);

        bitmapP2WButtonX = getScaledCoordinates(screenX, 1080, 400);
        bitmapP2WButtonY = getScaledCoordinates(screenY, 1920, 960);

        bitmapPopUpWindowX = getScaledCoordinates(screenX, 1080, 83);
        bitmapPopUpWindowY = getScaledCoordinates(screenY, 1920, 483);

        popupTextHeaderX = getScaledCoordinates(screenX, 1080, 203);
        popupTextHeaderY = getScaledCoordinates(screenY, 1920, 653);
        popupTextHeaderSize = getScaledBitmapSize(screenX, 1080, 75);

        popupTextPriceX = getScaledCoordinates(screenX, 1080, 203);
        popupTextPriceY = getScaledCoordinates(screenY, 1920, 1318);

        popupTextDescriptionX = getScaledCoordinates(screenX, 1080, 560);
        popupTextDescriptionY = getScaledCoordinates(screenY, 1920, 790);
        getPopupTextDescriptionWidth = getScaledCoordinates(screenX, 1080, 300);

        //einlesen der Descriptiontextbox
        mTextPaint=new TextPaint();
        mTextPaint.setTextSize(textSize);
        Typeface customFont = Typeface.createFromAsset(fullContext.getAssets(),"fonts/caladea-bold.ttf");
        mTextPaint.setTypeface(customFont);

        //Jetzt muss gemalt werden
        update = 0;
    }

    private void showPopUpWindow(FarmModeShopElement farmModeShopElement) {
        //Fehlerabfangen
        if (farmModeShopElement == null)
            return;

        update = 0;
        popUp = true;
        popupTextHeader = farmModeShopElement.getName();
        popupTextPrice = String.valueOf(farmModeShopElement.getPrice());
        mTextLayout = new StaticLayout(farmModeShopElement.getInfotext(), mTextPaint, getPopupTextDescriptionWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


        /*if (farmModeShopElements != null && farmModeShopElements.size() >= 1 && farmModeShopElements.get(0) != null) {
            //Element 1 kaufen button
            if (touchX1 >= bitmapShopElement1ButtonX && touchX1 < (bitmapShopElement1ButtonX + bitmapGurkeKaufenButton.getWidth())
                    && touchY1 >= bitmapShopElement1ButtonY && touchY1 < (bitmapShopElement1ButtonY + bitmapGurkeKaufenButton.getHeight())) {
                if (globalVariables.getGold() >= (farmModeShopElements.get(0).getPrice() + farmModeBackend.getStrawberryPrice())) {
                    farmModeShopElements = farmModeBackend.buyShopElements(fullContext, farmModeShopElements.get(0));
                    farmModeSound.playSound(5, fullContext);
                } else
                    farmModeSound.playSound(4, fullContext);
            }
        }*/
    }

    boolean onBackPressed() {
        if (popUp) {
            popUp = false;
            update = 0;
            return true;
        }
        return false;
    }

    //Wenn wir den Modus verlassen
    void recycle() {
        recycled = true;
        //Bitmaps Recyclen
        bitmapAckerKaufenButton.recycle();
        bitmapAckerKaufenButton = null;
        bitmapGurkeKaufenButton.recycle();
        bitmapGurkeKaufenButton = null;
        bitmapShopBackground.recycle();
        bitmapShopBackground = null;
        bitmapShopKeeper.recycle();
        bitmapShopKeeper = null;
        bitmapP2WButton.recycle();
        bitmapP2WButton = null;
        bitmapPopUpWindow.recycle();
        bitmapPopUpWindow = null;
        mTextLayout = null;
    }
}
