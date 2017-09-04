package com.cucumbertroup.strawberry.strawberry;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;


public class MainActivity extends AppCompatActivity {

    //Gameview ist sowohl die Klasse die zeichnet als auch "denkt"
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Display Objekt erstellen
        Display display = getWindowManager().getDefaultDisplay();
        //Ergebnis in ein Punkt laden
        Point size = new Point();
        display.getSize(size);
        //Ergebnis speichern
        int screenX = size.x;
        int screenY = size.y;

        //Initialisierung der Gameview
        gameView = new GameView(this, screenX, screenY);
        setContentView(gameView);
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

}
