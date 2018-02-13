package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;
import com.cucumbertroup.strawberry.strawberry.R;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

public class FarmMode {
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

    //Bilder initialisieren
    private Bitmap bitmapBackgroundOverlay;
    private Bitmap bitmapBackgroundLoading;
    private Bitmap bitmapBackgroundLand;

    //Textgroesse
    private int textSize, textX, textY;

    //Wo befinden sich die virtuellen Button und wie gross sind sie
    private int shopButtonX, shopButtonY;
    private int shopButtonHeight, shopButtonWidth;
    private int settingButtonX, settingButtonY;
    private int settingButtonHeight, settingButtonWidth;

    private FarmSettings farmSettings;
    private FarmShop farmShop;

    //Qualität: 250 - 1000
    private int bitmapMainQuality;

    //Laden wir gerade
    private boolean loading;

    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;

    //Musikabspielklasse
    private FarmModeSound farmModeSound;

    //Backend des Farmmodus
    private FarmModeBackend farmModeBackend;

    //Konstruktor (um die ganze Klasse überhaupt verwenden zu können)
    public FarmMode(Context context, int screenX, int screenY) {
        //Auf den Context können alle FarmMode Funktionen zugreifen
        fullContext = context;

        //Ladebildschirm anzeigen?
        loading = true;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        //Globale Infos laden
        globalVariables = GlobalVariables.getInstance();

        bitmapMainQuality = 500;

        //Alle Grafiken einlesen
        initialiseGrafics();

        //Musik einlesen
        farmModeSound = FarmModeSound.getInstance(context);

        //Backend einlesen
        farmModeBackend = FarmModeBackend.getInstance(context);
    }

    //update ist quasi das DENKEN in der App
    public void updateFarm() {
        farmModeBackend.strawberriesUpdate();
        farmModeSound.playSound(0, fullContext);
    }

    //ZEICHNEN
    public void drawFarm(SurfaceHolder ourHolder, Canvas canvas, Paint paint) {
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
                    farmSettings.drawFarmSettings(canvas, paint);
                } else if (farmShop != null) {
                    farmShop.drawFarmShop(canvas, paint);
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

                    //Anzahl der Erdbeeren
                    canvas.drawText("Erdbeeren: " + farmModeBackend.getNumStrawberries(), 3 * textX, 11 * textY, paint);

                    //Anzahl Gurken
                    canvas.drawText("Gurken: " + farmModeBackend.getNumGurken(), 3 * textX, 12 * textY, paint);

                    //Anzahl Aecker
                    canvas.drawText("Äcker: " + farmModeBackend.getNumAecker(), 3 * textX, 13 * textY, paint);

                    //Wie viel Gold haben wir eigentlich?
                    canvas.drawText("Gold: " + globalVariables.getGold(), 3 * textX, 14 * textY, paint);
                }
            } catch (NullPointerException e) {
                farmModeBackend.setSharedPreferences(fullContext);
                return;
            }
            //Alles auf den Bildschirm malen
            //Und Canvas wieder freilassen (um Fehler zu minimieren(das könnte sogar der Fehler meiner ersten App gewesen sein))
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    //Was passiert wenn man den Touchscreen im FARM Modus berührt
    public boolean onTouchFarm(MotionEvent motionEvent) {
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
                    //Settings Button
                    if (touchX1 >= settingButtonX && touchX1 < (settingButtonX + settingButtonWidth)
                            && touchY1 >= settingButtonY && touchY1 < (settingButtonY + settingButtonHeight)) {
                        farmModeSound.playSound(4, fullContext);
                        farmSettings = new FarmSettings(fullContext, screenX, screenY);
                        break;
                    }
                    //Shop Button
                    if (touchX1 >= shopButtonX && touchX1 < (shopButtonX + shopButtonWidth)
                            && touchY1 >= shopButtonY && touchY1 < (shopButtonY + shopButtonHeight)) {
                        farmModeSound.playSound(4, fullContext);
                        farmShop = new FarmShop(fullContext, screenX, screenY);
                        break;
                    }
                    //Wir haben geklickt, in einem Klickergame müssen wir doch mit der Info irgendwas machen oder? :D
                    farmModeBackend.gotClickedFarm(zustand, fullContext);
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

        //Einstellungen Öffnen Button
        settingButtonWidth = getScaledBitmapSize(screenX, 1080, 276);
        settingButtonHeight = getScaledBitmapSize(screenY, 1920, 186);
        settingButtonX = getScaledCoordinates(screenX, 1080, 814);
        settingButtonY = getScaledCoordinates(screenY, 1920, 156);

        //Shop Öffnen Button
        shopButtonWidth = getScaledBitmapSize(screenX, 1080, 211);
        shopButtonHeight = getScaledBitmapSize(screenY, 1920, 162);
        shopButtonX = getScaledCoordinates(screenX, 1080, 603);
        shopButtonY = getScaledCoordinates(screenY, 1920, 220);

        //Wir haben alles geladen
        loading = false;
    }

    //Wenn wir den Modus verlassen
    public GlobalVariables recycle() {
        loading = true;
        farmModeBackend.setSharedPreferences(fullContext);

        //Sound recyclen
        farmModeSound.recycle();
        //Bitmaps Recyclen
        bitmapBackgroundLand.recycle();
        bitmapBackgroundLand = null;
        bitmapBackgroundOverlay.recycle();
        bitmapBackgroundOverlay = null;
        return globalVariables;
    }

    public void onBackPressed() {
        if (farmShop != null) {
            farmShop.recycle();
            farmShop = null;
        }
        if (farmSettings != null) {
            farmSettings.recycle();
            farmSettings = null;
        }
    }

    public void getSharedPreferences() {
        farmModeBackend.getSharedPreferences(fullContext);
    }

    public void setSharedPreferences() {
        farmModeBackend.setSharedPreferences(fullContext);
    }
}