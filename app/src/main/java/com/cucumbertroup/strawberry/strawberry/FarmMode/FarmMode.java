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
    private float touchY1;
    //Was war der höchste und niedrigste Punkt den wir berührt haben
    private float touchY1MaxDown;
    private float touchY1MaxUp;
    //Berühren wir den Bildschirm mit mehr als einem Finger?
    private boolean touchPointer = false;
    //Abstand der letzten Bewegung auf dem Bildschirm
    private float deltaXMove;
    //Wann fand das TouchEvent statt
    private long touchTimer;
    //in welche Richtung ging der letzte move
    private boolean lastMoveVertical;
    private boolean lastMoveUp;

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

    private FarmModeSettings farmModeSettings;
    private FarmModeShop farmModeShop;

    //Laden wir gerade
    private boolean loading;

    //Globale Variablenübertragungsklasse ;)
    private GlobalVariables globalVariables;

    //Musikabspielklasse
    private FarmModeSound farmModeSound;

    //Backend des Farmmodus
    private FarmModeBackend farmModeBackend;

    //Acker und Erdbeeren malen
    private FarmModeList farmModeList;

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

        //Musik einlesen
        farmModeSound = FarmModeSound.getInstance();

        //Farmliste einlesen
        farmModeList = new FarmModeList(context, screenX, screenY);

        //Backend einlesen
        farmModeBackend = FarmModeBackend.getInstance(screenX);

        //Bildqualitaet einstellen
        farmModeBackend.setBitmapMainQuality(500);

        //Alle Grafiken einlesen
        initialiseGrafics();
    }

    //update ist quasi das DENKEN in der App
    public void updateFarm(long fps) {
        if (farmModeBackend != null)
            farmModeBackend.strawberriesUpdate();
        if (farmModeSound != null)
            farmModeSound.playSound(0, fullContext);
        if (farmModeList != null) {
            farmModeList.updateAcker();
            farmModeList.updateScrollAnimation(fps);
        }
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
                } else if (farmModeSettings != null) {
                    farmModeSettings.drawFarmSettings(canvas, paint);
                } else if (farmModeShop != null) {
                    farmModeShop.drawFarmShop(canvas, paint);
                } else {
                    //Absoluter Hintergrund
                    if (bitmapBackgroundLand != null)
                        canvas.drawBitmap(bitmapBackgroundLand, 0, backgroundLandY1, paint);

                    //Acker und Erdbeeren malen
                    if (farmModeList != null)
                        farmModeList.drawFarmList(canvas, paint);

                    //Hintergrund Overlay
                    if (bitmapBackgroundOverlay != null)
                        canvas.drawBitmap(bitmapBackgroundOverlay, backgroundOverlayX1, 0, paint);

                    //Pinselfarbe wählen (bisher nur für den Text)
                    paint.setColor(Color.argb(255, 249, 129, 0));
                    paint.setStyle(Paint.Style.FILL);
                    paint.setTextSize(textSize);

                    //Wie viel Gold haben wir?
                    canvas.drawText("Gold: " + globalVariables.getGold(), 3 * textX, 2 * textY, paint);
                }
            } catch (NullPointerException e) {
                if (farmModeBackend != null)
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
                touchY1MaxDown = motionEvent.getY();
                touchY1MaxUp = motionEvent.getY();
                //Wo befinden wir uns gerade? (Hier noch identisch mit touchX1down)
                touchX1 = motionEvent.getX();
                touchY1 = motionEvent.getY();
                //Der zweite Finger ist noch nicht auf dem Screen
                touchPointer = false;
                //Wie spät ist es?
                touchTimer = System.currentTimeMillis();
                //ScrollAnimation beenden
                farmModeList.stopScrollAnimation();

                //Wir erlauben keine Buttonklicks wenn wir gerade laden
                if (!loading) {
                    if (farmModeShop != null) {
                        farmModeShop.onTouchFarm(motionEvent);
                        return true;
                    }
                    if (farmModeSettings != null) {
                        farmModeSettings.onTouchFarmSettings(motionEvent);
                        return true;
                    }
                    //War da ein Button?
                    //Settings Button
                    if (touchX1 >= settingButtonX && touchX1 < (settingButtonX + settingButtonWidth)
                            && touchY1 >= settingButtonY && touchY1 < (settingButtonY + settingButtonHeight)) {
                        farmModeSound.playSound(4, fullContext);
                        farmModeSettings = new FarmModeSettings(fullContext, screenX, screenY);
                        return true;
                    }
                    //Shop Button
                    if (touchX1 >= shopButtonX && touchX1 < (shopButtonX + shopButtonWidth)
                            && touchY1 >= shopButtonY && touchY1 < (shopButtonY + shopButtonHeight)) {
                        farmModeSound.playSound(4, fullContext);
                        farmModeShop = new FarmModeShop(fullContext, screenX, screenY);
                        return true;
                    }
                    //Wir haben geklickt, in einem Klickergame müssen wir doch mit der Info irgendwas machen oder? :D
                    farmModeBackend.gotClickedFarm(zustand, fullContext);
                }
                return true;

            //Spieler bewegt den Finger auf dem Bildschirm
            case MotionEvent.ACTION_MOVE:
                if (!touchPointer && !loading) {
                    //In welche Richtung hat sich der Finger bewegt? | Differenz der beiden Werte
                    deltaXMove = motionEvent.getX() - touchX1;
                    float deltaYMove = motionEvent.getY() - touchY1;
                    //wo befinden wir uns in diesem Schritt
                    touchX1 = motionEvent.getX();
                    touchY1 = motionEvent.getY();

                    //Sind die neuen Werte höher oder niedriger als unser Maximum?
                    if (touchY1 > touchY1MaxUp)
                        touchY1MaxUp = touchY1;
                    if (touchY1 < touchY1MaxDown)
                        touchY1MaxDown = touchY1;

                    if (Math.abs(deltaXMove) > Math.abs(deltaYMove)) {
                        //Bedingung für die Äußeren Grenzen
                        if (((backgroundOverlayX1 + deltaXMove) < 0) && ((backgroundOverlayX1 + deltaXMove) > (-2 * screenX))) {
                            //Standardmovement (Folge dem Finger)
                            backgroundOverlayX1 += deltaXMove;
                        }
                        lastMoveVertical = false;
                    }
                    else {
                        lastMoveVertical = true;
                        //Finger bewegt sich nach oben / unten
                        lastMoveUp = deltaYMove >= 0;
                        //einfaches Scrollen (Am Finger kleben)
                        farmModeList.scroll(deltaYMove);
                    }
                }
                return true;
            //Zweiter Finger kommt dazu:
            case MotionEvent.ACTION_POINTER_DOWN:
                touchPointer = true;
                return true;
            case MotionEvent.ACTION_UP:
                //Wie weit hat sich der Finger insgesamt bewegt? | Differenz der beiden Werte
                float deltaXClick = motionEvent.getX() - touchX1down;
                float deltaYClick;
                if (lastMoveUp) {
                    deltaYClick = motionEvent.getY() - touchY1MaxDown;
                }
                else {
                    deltaYClick = motionEvent.getY() - touchY1MaxUp;
                }

                //FLINGING DER ACKERLISTE

                //Starten der Scrollanimation
                if (farmModeList != null && lastMoveVertical)
                    farmModeList.startScrollAnimation(touchTimer - System.currentTimeMillis(), deltaYClick);

                //resetten der touchY1 Koordinaten
                touchY1MaxDown = screenY;
                touchY1MaxUp = 0;

                //RESET DER HINTERGRUNDBILDPOSITION

                //Wären wir über den Rand gekommen?
                if (((backgroundOverlayX1 + deltaXClick) > 0) || ((backgroundOverlayX1 + deltaXClick) < (-2 * screenX))) {
                    //Rechts
                    if ((backgroundOverlayX1 + deltaXClick) < 0) {
                        backgroundOverlayX1 = -2 * screenX;
                        zustand = 2;
                        return true;
                    }
                    //Links
                    if ((backgroundOverlayX1 + deltaXClick) > (-2 * screenX)) {
                        backgroundOverlayX1 = 0;
                        zustand = 0;
                        return true;
                    }
                }
                //Zurückswapen nach Links, Rechts, Mitte wenn nach Stillstand Bedingungen zutreffen

                //Wenn wir von links nach links kommen
                if (backgroundOverlayX1 + deltaXMove > (-0.3 * screenX) && zustand == 0) {
                    backgroundOverlayX1 = 0;
                    zustand = 0;
                    return true;
                }
                //wenn wir von der mitte nach links kommen
                if (backgroundOverlayX1 + deltaXMove > (-0.7 * screenX) && zustand == 1) {
                    backgroundOverlayX1 = 0;
                    zustand = 0;
                    return true;
                }
                //wenn wir von rechts nach rechts kommen
                if (backgroundOverlayX1 + deltaXMove < (-1.7 * screenX) && zustand == 2) {
                    backgroundOverlayX1 = -2 * screenX;
                    zustand = 2;
                    return true;
                }
                //von rechts zur mitte kommen
                if (backgroundOverlayX1 + deltaXMove < (-1.3 * screenX) && zustand == 1) {
                    backgroundOverlayX1 = -2 * screenX;
                    zustand = 2;
                    return true;
                }
                //Mitte
                backgroundOverlayX1 = (-1 * screenX);
                zustand = 1;
                return true;
        }
        return false;
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
        bitmapBackgroundOverlay = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.background_farm1, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapBackgroundOverlay = Bitmap.createScaledBitmap(bitmapBackgroundOverlay, screenX * 3, screenY, false);

        //Hintergrundland Bild:
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Nur die Ränder werden eingefügt
        //Hintergrundbild einfügen
        bitmapBackgroundLand = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.background_farm2, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapBackgroundLand = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.background_farm2, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
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
    public void recycle() {
        loading = true;
        if (farmModeBackend != null)
            farmModeBackend.setSharedPreferences(fullContext);

        //Sound recyclen
        if (farmModeSound != null) {
            farmModeSound.recycle();
            farmModeSound = null;
        }

        //Backend recyclen
        if (farmModeBackend != null) {
            farmModeBackend.recycle();
            farmModeBackend = null;
        }

        //AckerList recyclen
        if (farmModeList != null) {
            farmModeList.recycle();
            farmModeList = null;
        }

        //Shop und Settings recyclen
        if (farmModeShop != null) {
            farmModeShop.recycle();
            farmModeShop = null;
        }
        if (farmModeSettings != null) {
            farmModeSettings.recycle();
            farmModeSettings = null;
        }
        //Bitmaps Recyclen
        bitmapBackgroundLand.recycle();
        bitmapBackgroundLand = null;
        bitmapBackgroundOverlay.recycle();
        bitmapBackgroundOverlay = null;
    }

    public void onBackPressed() {
        if (farmModeShop != null) {
            farmModeShop.recycle();
            farmModeShop = null;
        }
        if (farmModeSettings != null) {
            farmModeSettings.recycle();
            farmModeSettings = null;
        }
    }

    public void getSharedPreferences() {
        if (farmModeBackend != null)
            farmModeBackend.getSharedPreferences(fullContext);
    }

    public void setSharedPreferences() {
        if (farmModeBackend != null)
            farmModeBackend.setSharedPreferences(fullContext);
    }
}