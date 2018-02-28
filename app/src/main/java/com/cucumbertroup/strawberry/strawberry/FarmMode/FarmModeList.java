package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.cucumbertroup.strawberry.strawberry.R;

import java.util.Arrays;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

class FarmModeList {
    //Application Context
    private Context fullContext;

    //outgesourcte Funktionen
    private FarmModeBackend farmModeBackend;

    //Bildschirmgroesse
    private int screenX;
    private int screenY;

    //Erdbeeren- und Ackerbilder
    private Bitmap bitmapAcker;
    private Bitmap bitmapErdbeere1;
    private Bitmap bitmapErdbeere2;
    private Bitmap bitmapErdbeere3;
    private Bitmap bitmapErdbeere4;
    private Bitmap bitmapErdbeere5;

    //Die SpaltenKoordinaten der Erdbeeren
    private int bitmapStrawberryYAbstand;

    //Der X Wert der Acker ist ja immer gleich
    private int bitmapAckerX;
    //Die Y Werte speicher ich in einer Liste
    private Float[] bitmapAckerY;
    //Abstand zwischen Aeckern ist immer gleich
    private int bitmapAckerAbstand;
    //Wie weit kann man maximal scrollen
    private float completeAckerHeight;
    //obere Grenze beim scrollen
    private int maxHeight;

    FarmModeList(Context context, int screenX, int screenY) {
        fullContext = context;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        bitmapAckerY = new Float[0];

        //Standard Ackerkoordinaten
        bitmapAckerX = getScaledCoordinates(screenX, 1080, 50);
        bitmapAckerAbstand = getScaledCoordinates(screenY, 1920, 550);
        //Wie weit oben der Acker sein muss bis er nicht mehr gemalt wird
        maxHeight = getScaledCoordinates(screenY, 1920, -50);

        bitmapStrawberryYAbstand = getScaledCoordinates(screenY, 1920, 200);

        //Backend einlesen
        farmModeBackend = FarmModeBackend.getInstance(context, screenX);

        //Alle Grafiken einlesen
        initialiseGrafics();
    }

    //Erdbeeren- und Ackerbilder einlesen
    private void initialiseGrafics() {
        //Acker einlesen
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapAcker = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.acker, options);
        bitmapAcker = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.acker, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapAcker = Bitmap.createScaledBitmap(bitmapAcker, getScaledBitmapSize(screenX, 1080, 961), getScaledBitmapSize(screenY, 1920, 476), false);

        //Erdbeere1 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere1 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere1, options);
        bitmapErdbeere1 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere1, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere1 = Bitmap.createScaledBitmap(bitmapErdbeere1, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        //Erdbeere2 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere2 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere2, options);
        bitmapErdbeere2 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere2, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere2 = Bitmap.createScaledBitmap(bitmapErdbeere2, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        //Erdbeere3 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere3 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere3, options);
        bitmapErdbeere3 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere3, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere3 = Bitmap.createScaledBitmap(bitmapErdbeere3, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        //Erdbeere4 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere4 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere4, options);
        bitmapErdbeere4 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere4, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere4 = Bitmap.createScaledBitmap(bitmapErdbeere4, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        //Erdbeere5 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere5 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere5, options);
        bitmapErdbeere5 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere5, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere5 = Bitmap.createScaledBitmap(bitmapErdbeere5, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);
    }

    void drawFarmList(Canvas canvas, Paint paint) {
        //Acker malen
        if (bitmapAcker != null && bitmapAckerY.length > 0) {
            //alle Aecker durchgehen und testen ob er auf dem Screen waere, wenn ja malen
            for (int j = 0; j < bitmapAckerY.length; j++)
                if (bitmapAckerY[j] >= maxHeight && bitmapAckerY[j] <= screenY) {
                    canvas.drawBitmap(bitmapAcker, bitmapAckerX, bitmapAckerY[j], paint);
                    //Erdbeere testweise malen
                    for (int i = 0; i < bitmapAckerY.length*8; i++) {
                        Strawberry tmp = farmModeBackend.getSpecificStrawberry(i);
                        if (tmp != null && tmp.getWachsStatus() != -1) {
                            Log.d("Ackerauswahl", "tmp.getAcker() " + tmp.getAcker() + "i/8 + 1 " + (i/8 + 1));
                            if (tmp.getAcker() == (i/8 + 1) && tmp.getAcker() == ( j + 1)) {
                                if (tmp.isReihe1())
                                    canvas.drawBitmap(getCorrectStrawberryImage(tmp.getWachsStatus()), tmp.getCoordinateX(), bitmapAckerY[j], paint);
                                else
                                    canvas.drawBitmap(getCorrectStrawberryImage(tmp.getWachsStatus()), tmp.getCoordinateX(), bitmapAckerY[j] + bitmapStrawberryYAbstand, paint);
                            }
                        }
                    }
                }

        }
    }

    private Bitmap getCorrectStrawberryImage(int wachsstatus) {
        switch (wachsstatus) {
            case 0:
                return bitmapErdbeere1;
            case 1:
                return bitmapErdbeere2;
            case 2:
                return bitmapErdbeere3;
            case 3:
                return bitmapErdbeere4;
            case 4:
                return bitmapErdbeere5;
        }
        return bitmapErdbeere2;
    }

    //regelmaessiges checken ob neuer Acker hinzugefuegt werden muss
    void updateAcker() {
       while (bitmapAckerY.length != farmModeBackend.getNumAecker()) {
           Float[] bitmapAckerYTemp = Arrays.copyOf(bitmapAckerY, bitmapAckerY.length + 1);
           if (bitmapAckerY.length == 0) {
               bitmapAckerYTemp[0] = (float) getScaledCoordinates(screenY, 1920, 500);
               bitmapAckerY = bitmapAckerYTemp;

               completeAckerHeight = 0;
           }
           else{
               bitmapAckerYTemp[bitmapAckerYTemp.length - 1] = (bitmapAckerY[bitmapAckerY.length - 1] + bitmapAckerAbstand);
               bitmapAckerY = bitmapAckerYTemp;
           }
        }
    }

    void scroll(float difference) {
        if (farmModeBackend.getNumAecker() > 2) {
            //wir scrollen nicht nach oben zu viel
            if (completeAckerHeight + difference < 0) {
                if (((bitmapAckerY.length - 2) * bitmapAckerAbstand) > Math.abs(completeAckerHeight + difference)) {
                    for (int i = 0; i < bitmapAckerY.length; i++) {
                        bitmapAckerY[i] += difference;
                    }
                    completeAckerHeight += difference;
                }
                //falls wir unten an die Grenze stossen
                else {
                    float tmp = ((bitmapAckerY.length - 2) * bitmapAckerAbstand) - Math.abs(completeAckerHeight);
                    for (int i = 0; i < bitmapAckerY.length; i++) {
                        bitmapAckerY[i] -= tmp;
                    }
                    completeAckerHeight -= tmp;
                }
            }
            //falls wir oben an die Grenze stossen
            else {
                for (int i = 0; i < bitmapAckerY.length; i++) {
                    bitmapAckerY[i] -= completeAckerHeight;
                }
                completeAckerHeight -= completeAckerHeight;
            }
        }
    }
}
