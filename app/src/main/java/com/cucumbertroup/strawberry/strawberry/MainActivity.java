package com.cucumbertroup.strawberry.strawberry;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnGesturePerformedListener {

    //Gameview ist sowohl die Klasse die zeichnet als auch "denkt"
    private GameView gameView;

    //Gesture Detection
    protected GestureLibrary mLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //allererster Loadingscreen
        setContentView(R.layout.activity_main);

        //Display Objekt erstellen
        Display display = getWindowManager().getDefaultDisplay();
        //Ergebnis in ein Punkt laden
        Point size = new Point();
        display.getSize(size);
        //Um die Bildschirmgrößen an die Gameview weiterzugeben
        int screenX = size.x;
        int screenY = size.y;

        //Initialisierung der Gameview
        gameView = new GameView(this, screenX, screenY);
        //Initialisierung der GestureOverlayView (zur Gestenerkennung)
        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        //Initialisierung eines FrameLayouts um die GestureOverlayView ÜBER der Gameview anzuzeigen
        FrameLayout frameLayout = new FrameLayout(this);

        //Einstellungen der gestureOverlayView
        gestureOverlayView.setOrientation(gestureOverlayView.ORIENTATION_VERTICAL);
        gestureOverlayView.setEventsInterceptionEnabled(false); //Das hier ist super wichtig damit die Gameview weiterhin MotionEvents bearbeiten kann
        gestureOverlayView.setGestureStrokeType(gestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);

        //Um die Gestenerkennung unsichtbar zu gestalten
        int transparentcolor = Color.parseColor("#00FFFFFF");
        gestureOverlayView.setGestureColor(transparentcolor);
        gestureOverlayView.setUncertainGestureColor(transparentcolor);

        //Einlesen der definierten Gesten
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load())
            Toast.makeText(this, "Fehler beim Laden", Toast.LENGTH_SHORT).show();

        //Weiterleitung der Touchgesten an die Gameview
        gestureOverlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gameView.onTouchEvent(motionEvent);
            }
        });
        //Initialisierung der Gestenerkennungsfunktion
        gestureOverlayView.addOnGesturePerformedListener(this);

        //Einfügen der gameView unten und darüber kommt die gestureOverlayView
        frameLayout.addView(gameView, 0);
        frameLayout.addView(gestureOverlayView, 1);

        //Anzeigen des besten Layouts der Welt
        setContentView(frameLayout);
    }

    //Wenn das Spiel gestartet wird
    @Override
    protected void onResume() {
        super.onResume();

        gameView.resume();
    }

    //Wenn das Spiel geschlossen wird
    @Override
    protected void onPause() {
        super.onPause();

        gameView.pause();
    }

    //Wenn der Zurückbutton geklickt wird.
    @Override
    public void onBackPressed() {
        gameView.onBackPressed();
    }

    //Was machen wir bei Gesten?
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture
            gesture) {
        ArrayList<Prediction> predictions =
                mLibrary.recognize(gesture);

        if (predictions.size() > 0 && predictions.get(0).score > 3.8) {
            Log.d("score: ", ""+ predictions.get(0).score);

            String action = predictions.get(0).name;

            gameView.onGesturePerformed(action);
        }
    }
}
