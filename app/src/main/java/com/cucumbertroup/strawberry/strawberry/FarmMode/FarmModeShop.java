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
    //einfaches Schild (erstmal nur als acker kaufen button)
    private Bitmap bitmapSimpleShield;

    //Wo kommen die Buttons hin?
    private int bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY;
    private int bitmapShopElement1ButtonX, bitmapShopElement1ButtonY;
    private int bitmapShopElement2ButtonX, bitmapShopElement2ButtonY;
    private int bitmapShopElement3ButtonX, bitmapShopElement3ButtonY;
    private int bitmapGoldSchildX, bitmapGoldSchildY;

    //Wo kommen die Grafikelemente hin?
    private int bitmapShopKeeperX, bitmapShopKeeperY;
    private int bitmapP2WButtonX, bitmapP2WButtonY;
    private int bitmapPopUpWindowX, bitmapPopUpWindowY;
    private int bitmapPopUpExitButtonX, bitmapPopUpExitButtonY, bitmapPopUpExitButtonHeight, bitmapPopUpExitButtonWidth;
    private int bitmapPopUpKaufenButtonX, bitmapPopUpKaufenButtonY, bitmapPopUpKaufenButtonHeight, bitmapPopUpKaufenButtonWidth;

    private int textSize, textX, textY;
    private int textAckerKaufenX, textAckerKaufenY;
    private int textGoldX, textGoldY;

    //Wo kommen die Texte des PopUp Fensters hin?
    private int popupTextHeaderX, popupTextHeaderY, popupTextHeaderSize;
    private int popupTextPriceX, popupTextPriceY;
    private int popupTextDescriptionX, popupTextDescriptionY, getPopupTextDescriptionWidth;

    private String popupTextHeader, popupTextPrice;
    private int popupElementIndex;
    private int popupIconX, popupIconY;

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
        farmModeBackend = FarmModeBackend.getInstance(screenX, screenY);

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

                    //Icon malen
                    canvas.drawBitmap(farmModeShopElements.get(popupElementIndex).getIcon(), popupIconX, popupIconY, paint);

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
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setStyle(Paint.Style.FILL);
                    paint.setTextSize(textSize);

                    //Acker Kaufen Button malen
                    if (bitmapSimpleShield != null)
                        canvas.drawBitmap(bitmapSimpleShield, bitmapAckerKaufenButtonX, bitmapAckerKaufenButtonY, paint);
                    //Anzahl Äcker und Preis
                    canvas.drawText("Acker: " + farmModeBackend.getNumAecker(), textAckerKaufenX, textAckerKaufenY, paint);
                    canvas.drawText("Preis: " + farmModeBackend.getPriceAecker() + " G", textAckerKaufenX, textAckerKaufenY + textY, paint);

                    //Gold Schild (provisorisch)
                    if (bitmapSimpleShield != null)
                        canvas.drawBitmap(bitmapSimpleShield, bitmapGoldSchildX, bitmapGoldSchildY, paint);
                    //Goldmenge
                    canvas.drawText("Gold: " + globalVariables.getGold(), textGoldX, textGoldY, paint);


                    //Elementeinfo malen
                    if (farmModeShopElements != null) {
                        //Erstes Element
                        if (farmModeShopElements.size() >= 1 && farmModeShopElements.get(0) != null && farmModeShopElements.get(0).getIcon() != null) {
                            /*if (farmModeShopElements.get(0).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                                canvas.drawText(farmModeShopElements.get(0).getName() + " | Kosten: " + farmModeShopElements.get(0).getPrice() + " Gold", textX, 7 * textY, paint);
                            else
                                canvas.drawText(farmModeShopElements.get(0).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(0).getNecessaryAecker(), textX, 7 * textY, paint);*/
                            canvas.drawBitmap(farmModeShopElements.get(0).getIcon(), bitmapShopElement1ButtonX, bitmapShopElement1ButtonY, paint);
                        }
                        if (farmModeShopElements.size() >= 2 && farmModeShopElements.get(1) != null && farmModeShopElements.get(1).getIcon() != null) {
                            /*if (farmModeShopElements.get(1).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                                canvas.drawText(farmModeShopElements.get(1).getName() + " | Kosten: " + farmModeShopElements.get(1).getPrice() + " Gold", textX, 10 * textY, paint);
                            else
                                canvas.drawText(farmModeShopElements.get(1).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(1).getNecessaryAecker(), textX, 10 * textY, paint);*/
                            canvas.drawBitmap(farmModeShopElements.get(1).getIcon(), bitmapShopElement2ButtonX, bitmapShopElement2ButtonY, paint);
                        }
                        if (farmModeShopElements.size() == 3 && farmModeShopElements.get(2) != null && farmModeShopElements.get(2).getIcon() != null) {
                            /*if (farmModeShopElements.get(2).getNecessaryAecker() <= farmModeBackend.getNumAecker())
                                canvas.drawText(farmModeShopElements.get(2).getName() + " | Kosten: " + farmModeShopElements.get(2).getPrice() + " Gold", textX, 13 * textY, paint);
                            else
                                canvas.drawText(farmModeShopElements.get(2).getName() + " | Benötigte Äcker: " + farmModeShopElements.get(2).getNecessaryAecker(), textX, 13 * textY, paint);*/
                            canvas.drawBitmap(farmModeShopElements.get(2).getIcon(), bitmapShopElement3ButtonX, bitmapShopElement3ButtonY, paint);
                        }
                    } else {
                        farmModeShopElements = farmModeBackend.getShopElements(fullContext);
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
                        //Exit Button?
                        if (touchX1 >= bitmapPopUpExitButtonX && touchX1 < (bitmapPopUpExitButtonX + bitmapPopUpExitButtonWidth)
                                && touchY1 >= bitmapPopUpExitButtonY && touchY1 < (bitmapPopUpExitButtonY + bitmapPopUpExitButtonHeight)) {
                            popUp = false;
                            update = 0;
                            break;
                        }
                        //Kaufen Button?
                        if (touchX1 >= bitmapPopUpKaufenButtonX && touchX1 < (bitmapPopUpKaufenButtonX + bitmapPopUpKaufenButtonWidth)
                                && touchY1 >= bitmapPopUpKaufenButtonY && touchY1 < (bitmapPopUpKaufenButtonY + bitmapPopUpKaufenButtonHeight)) {
                            if (globalVariables.getGold() >= (Integer.parseInt(popupTextPrice) + farmModeBackend.getStrawberryPrice())) {
                                Log.d("farmModeShopElements", "" + farmModeShopElements);
                                Log.d("popupElementIndex", "" + popupElementIndex);
                                farmModeShopElements = farmModeBackend.buyShopElements(fullContext, farmModeShopElements.get(popupElementIndex));
                                farmModeSound.playSound(5, fullContext);
                                popUp = false;
                                update = 0;
                            } else
                                farmModeSound.playSound(4, fullContext);
                            break;
                        }
                        break;
                    }
                }
                else {
                    if (touchX1 >= bitmapAckerKaufenButtonX && touchX1 < (bitmapAckerKaufenButtonX + bitmapSimpleShield.getWidth())
                            && touchY1 >= bitmapAckerKaufenButtonY && touchY1 < (bitmapAckerKaufenButtonY + bitmapSimpleShield.getHeight())) {
                        if (globalVariables.getGold() >= (farmModeBackend.getPriceAecker() + farmModeBackend.getStrawberryPrice())) {
                            farmModeBackend.ackerGekauft();
                            farmModeSound.playSound(5, fullContext);
                        } else
                            farmModeSound.playSound(4, fullContext);
                        update = 0;
                        break;
                    }
                    if (farmModeShopElements != null && farmModeShopElements.size() >= 1 && farmModeShopElements.get(0) != null) {
                        //Element 1 kaufen button
                        if (touchX1 >= bitmapShopElement1ButtonX && touchX1 < (bitmapShopElement1ButtonX + farmModeShopElements.get(0).getIcon().getWidth())
                                && touchY1 >= bitmapShopElement1ButtonY && touchY1 < (bitmapShopElement1ButtonY + farmModeShopElements.get(0).getIcon().getHeight())) {
                            showPopUpWindow(0);
                            farmModeSound.playSound(4, fullContext);
                        }
                    }
                    if (farmModeShopElements != null && farmModeShopElements.size() >= 2 && farmModeShopElements.get(1) != null) {
                        //Element 2 kaufen button
                        if (touchX1 >= bitmapShopElement2ButtonX && touchX1 < (bitmapShopElement2ButtonX + farmModeShopElements.get(1).getIcon().getWidth())
                                && touchY1 >= bitmapShopElement2ButtonY && touchY1 < (bitmapShopElement2ButtonY + farmModeShopElements.get(1).getIcon().getHeight())) {
                            showPopUpWindow(1);
                            farmModeSound.playSound(4, fullContext);
                        }
                    }

                    if (farmModeShopElements != null && farmModeShopElements.size() == 3 && farmModeShopElements.get(2) != null) {
                        //Element 3 kaufen button
                        if (touchX1 >= bitmapShopElement3ButtonX && touchX1 < (bitmapShopElement3ButtonX + farmModeShopElements.get(2).getIcon().getWidth())
                                && touchY1 >= bitmapShopElement3ButtonY && touchY1 < (bitmapShopElement3ButtonY + farmModeShopElements.get(2).getIcon().getHeight())) {
                            showPopUpWindow(2);
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
        bitmapSimpleShield = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.simple_shield, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapSimpleShield = Bitmap.createScaledBitmap(bitmapSimpleShield, getScaledBitmapSize(screenX, 1080, 327), getScaledBitmapSize(screenY, 1920, 192), false);

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
        //Acker Kaufen
        bitmapAckerKaufenButtonX = getScaledCoordinates(screenX, 1080, 430);
        bitmapAckerKaufenButtonY = getScaledCoordinates(screenY, 1920, 510);
        textAckerKaufenX = getScaledCoordinates(screenX, 1080, 460);
        textAckerKaufenY = getScaledCoordinates(screenY, 1920, 600);

        //Gold Schild
        bitmapGoldSchildX = getScaledCoordinates(screenX, 1080, 150);
        bitmapGoldSchildY = getScaledCoordinates(screenY, 1920, 1250);
        textGoldX = getScaledCoordinates(screenX, 1080, 185);
        textGoldY = getScaledCoordinates(screenY, 1920, 1365);

        //Shop Elemente Icons
        bitmapShopElement1ButtonX = getScaledCoordinates(screenX, 1080, 75);
        bitmapShopElement1ButtonY = getScaledCoordinates(screenY, 1920, 216);
        bitmapShopElement2ButtonX = getScaledCoordinates(screenX, 1080, 465);
        bitmapShopElement2ButtonY = getScaledCoordinates(screenY, 1920, 171);
        bitmapShopElement3ButtonX = getScaledCoordinates(screenX, 1080, 72);
        bitmapShopElement3ButtonY = getScaledCoordinates(screenY, 1920, 597);

        //Shopgurke
        bitmapShopKeeperX = getScaledCoordinates(screenX, 1080, 425);
        bitmapShopKeeperY = getScaledCoordinates(screenY, 1920, 1222);

        //Troll Button
        bitmapP2WButtonX = getScaledCoordinates(screenX, 1080, 400);
        bitmapP2WButtonY = getScaledCoordinates(screenY, 1920, 960);

        //Pop Up Stuff
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

        bitmapPopUpExitButtonHeight = getScaledBitmapSize(screenX, 1080, 179);
        bitmapPopUpExitButtonWidth = getScaledBitmapSize(screenY, 1920, 129);
        bitmapPopUpExitButtonX = getScaledBitmapSize(screenX, 1080, 780);
        bitmapPopUpExitButtonY = getScaledBitmapSize(screenY, 1920, 549);

        bitmapPopUpKaufenButtonHeight = getScaledBitmapSize(screenX, 1080, 204);
        bitmapPopUpKaufenButtonWidth = getScaledBitmapSize(screenY, 1920, 346);
        bitmapPopUpKaufenButtonX = getScaledBitmapSize(screenX, 1080, 136);
        bitmapPopUpKaufenButtonY = getScaledBitmapSize(screenY, 1920, 1188);

        popupIconX = getScaledBitmapSize(screenX, 1080, 200);
        popupIconY = getScaledBitmapSize(screenY, 1920, 850);

        //einlesen der Descriptiontextbox
        mTextPaint=new TextPaint();
        mTextPaint.setTextSize(textSize);
        Typeface customFont = Typeface.createFromAsset(fullContext.getAssets(),"fonts/caladea-bold.ttf");
        mTextPaint.setTypeface(customFont);

        //Jetzt muss gemalt werden
        update = 0;
    }

    private void showPopUpWindow(int index) {
        FarmModeShopElement farmModeShopElement = farmModeShopElements.get(index);
        //Fehlerabfangen
        if (farmModeShopElement == null)
            return;

        update = 0;
        popUp = true;
        popupTextHeader = farmModeShopElement.getName();
        popupTextPrice = String.valueOf(farmModeShopElement.getPrice());
        popupElementIndex = index;
        if (farmModeShopElement.getNecessaryAecker() <= farmModeBackend.getNumAecker())
            mTextLayout = new StaticLayout(farmModeShopElement.getInfotext(), mTextPaint, getPopupTextDescriptionWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        else
            mTextLayout = new StaticLayout("Um dieses Item zu kaufen brauchst du noch " + (farmModeShopElement.getNecessaryAecker() - farmModeBackend.getNumAecker()) + " Äcker.", mTextPaint, getPopupTextDescriptionWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
        bitmapSimpleShield.recycle();
        bitmapSimpleShield = null;
        bitmapShopBackground.recycle();
        bitmapShopBackground = null;
        bitmapShopKeeper.recycle();
        bitmapShopKeeper = null;
        bitmapP2WButton.recycle();
        bitmapP2WButton = null;
        bitmapPopUpWindow.recycle();
        bitmapPopUpWindow = null;
        mTextLayout = null;

        for (FarmModeShopElement farmModeShopElement : farmModeShopElements) {
            if (farmModeShopElement != null)
                farmModeShopElement.recycle();
        }
    }
}
