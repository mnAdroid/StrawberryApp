package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;
import com.cucumbertroup.strawberry.strawberry.R;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

class FarmModeList {
    //Application Context
    private Context fullContext;

    //outgesourcte Funktionen
    private FarmModeBackend farmModeBackend;
    private FarmModeSound farmModeSound;
    private GlobalVariables globalVariables;

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

    //Zunaechst noch feste Werte fuer die Acker
    private int bitmapAcker1X, bitmapAcker1Y;
    private int bitmapAcker2X, bitmapAcker2Y;

    FarmModeList(Context context, int screenX, int screenY) {
        fullContext = context;

        //Bildschirmgröße abspeichern
        this.screenX = screenX;
        this.screenY = screenY;

        //Globale Infos laden
        globalVariables = GlobalVariables.getInstance();

        //Musik einlesen
        farmModeSound = FarmModeSound.getInstance(context);

        //Backend einlesen
        farmModeBackend = FarmModeBackend.getInstance(context);

        //Bildqualitaet einstellen
        farmModeBackend.setBitmapMainQuality(500);

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
        bitmapErdbeere2 = Bitmap.createScaledBitmap(bitmapErdbeere1, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        //Erdbeere3 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere3 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere3, options);
        bitmapErdbeere3 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere3, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere3 = Bitmap.createScaledBitmap(bitmapErdbeere1, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        //Erdbeere4 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere4 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere4, options);
        bitmapErdbeere4 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere4, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere4 = Bitmap.createScaledBitmap(bitmapErdbeere1, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        //Erdbeere5 einlesen
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapErdbeere5 = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.erdbeere5, options);
        bitmapErdbeere5 = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.erdbeere5, farmModeBackend.getBitmapMainQuality(), farmModeBackend.getBitmapMainQuality());
        bitmapErdbeere5 = Bitmap.createScaledBitmap(bitmapErdbeere1, getScaledBitmapSize(screenX, 1080, 271), getScaledBitmapSize(screenY, 1920, 268), false);

        bitmapAcker1X = getScaledCoordinates(screenX, 1080, 50);
        bitmapAcker1Y = getScaledCoordinates(screenY, 1920, 500);

        bitmapAcker2X = getScaledCoordinates(screenX, 1080, 50);
        bitmapAcker2Y = getScaledCoordinates(screenY, 1920, 1050);
    }

    void drawFarmList(Canvas canvas, Paint paint) {
        //Test Acker 1 malen
        if (bitmapAcker != null) {
            canvas.drawBitmap(bitmapAcker, bitmapAcker1X, bitmapAcker1Y, paint);
            canvas.drawBitmap(bitmapAcker, bitmapAcker2X, bitmapAcker2Y, paint);
        }
    }
}
