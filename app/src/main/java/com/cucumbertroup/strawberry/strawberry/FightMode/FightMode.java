package com.cucumbertroup.strawberry.strawberry.FightMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;
import com.cucumbertroup.strawberry.strawberry.OnlineFeatures;
import com.cucumbertroup.strawberry.strawberry.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Random;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

public class FightMode {
    //Der gespeicherte Context
    private Context fullContext;

    //Die Größe des Bildschirms damit alles auf jedem Handy gleich aussieht
    private int screenX;
    private int screenY;

    //Ort der letzten Berührung auf dem Bildschirm
    private float touchX1, touchY1, touchXPointer1, touchYPointer1;
    private int touchIndex1;
    private long touchTimerIndex1;
    private boolean actionDown, noButtonKlicked, actionPointerDown;
    //Bilder initialisieren
    private Bitmap bitmapBackgroundFights;
    private Bitmap bitmapBackgroundLoading;

    //Ort des Hintergrundbildes
    private int backgroundFightX1;

    //Laden wir gerade
    private boolean loading;

    //Testbuttons FIGHT initialisieren
    private Bitmap bitmapSpawnDiebButton;
    private Bitmap bitmapSpawnGoblinButton;
    private Bitmap bitmapSpawnOrkButton;
    private Bitmap bitmapSpawnLevelUp;
    private Bitmap bitmapLevelUpDamage;
    private Bitmap bitmapLevelUpDefense;
    private Bitmap bitmapLevelUpLife;
    private Bitmap bitmapWaffeHackeButton;
    private Bitmap bitmapWaffeHolzschildButton;
    private Bitmap bitmapWaffeKnueppelButton;
    private Bitmap bitmapWaffeRiesenschwertButton;
    private Bitmap bitmapWaffeSchwertButton;
    private Bitmap bitmapWaffeWechselnButton;
    private Bitmap bitmapSpawnRiese;
    private Bitmap bitmapLevelUpMana;
    private Bitmap bitmapRunButton;
    private Bitmap bitmapOnlineButton;
    private Bitmap bitmapUploadButton;
    private Bitmap bitmapDownloadButton;
    //private Bitmap bitmapHighscoreButton;

    //Feste Größen speichern
    private int textSize, textSizeBig, textX, textY;
    private int bitmapSpawnDiebButtonX, bitmapSpawnDiebButtonY;
    private int bitmapSpawnGoblinButtonX, bitmapSpawnGoblinButtonY;
    private int bitmapSpawnOrkButtonX, bitmapSpawnOrkButtonY;
    private int bitmapSpawnLevelUpX, bitmapSpawnLevelUpY;
    private int bitmapLevelUpDamageX, bitmapLevelUpDamageY;
    private int bitmapLevelUpDefenseX, bitmapLevelUpDefenseY;
    private int bitmapLevelUpLifeX, bitmapLevelUpLifeY;
    private int bitmapWaffeHackeButtonX, bitmapWaffeHackeButtonY;
    private int bitmapWaffeHolzschildButtonX, bitmapWaffeHolzschildButtonY;
    private int bitmapWaffeKnueppelButtonX, bitmapWaffeKnueppelButtonY;
    private int bitmapWaffeRiesenschwertButtonX, bitmapWaffeRiesenschwertButtonY;
    private int bitmapWaffeSchwertButtonX, bitmapWaffeSchwertButtonY;
    private int bitmapWaffeWechselnButtonX, bitmapWaffeWechselnButtonY;
    private int bitmapSpawnRieseX, bitmapSpawnRieseY;
    private int bitmapLevelUpManaX, bitmapLevelUpManaY;
    private int bitmapRunButtonX, bitmapRunButtonY;
    private int bitmapOnlineButtonX, bitmapOnlineButtonY;
    private int bitmapUploadButtonX, bitmapUploadButtonY;
    private int bitmapDownloadButtonX, bitmapDownloadButtonY;
    //private int bitmapHighscoreButtonX, bitmapHighscoreButtonY;

    //Musik initialisieren
    private SoundPool soundPool;
    private int click1 = -1;
    private int hit1 = -1;
    private int hit2 = -1;
    private int hit3 = -1;
    private int hit4 = -1;
    private int hit5 = -1;
    private int hit6 = -1;
    private int hit7 = -1;
    private int hit8 = -1;
    private int hit9 = -1;
    private int fireballSound = -1;
    private int knifesharpener = -1;
    private MediaPlayer backgroundloop2; //Fightmusik

    //Kampfessteuerung
    private int screenMitte;
    private int enemieSpawnLevel;
    private boolean levelUpPossible;
    private boolean chooseWeapon;

    //Alles was ich für den Fightmode brauche
    private Enemie enemie;
    private Character character;
    private StatusEffect characterStatusEffect;
    private StatusEffect enemieStatusEffect;
    private boolean runmode;
    private ScoreRun scoreRun;
    private ScoreRun highscoreRun;

    //Muss ich als haptisches Feedback dank Ability vibrieren?
    private boolean alreadyVibrating;

    //Verteidige dich gegen die Gegner
    private boolean defendNecessary;

    //Kreisinfos
    private int circleStrokeWidth;
    //private int circleRadiusMax;
    private int circleRadiusMin;
    private int circleRadius;
    private int circleRadiusAbsoluteMin;
    private boolean circleInRange;
    private int outerCircleColor;

    //FingerPath Infos
    private static final float TOUCH_TOLERANCE = 4;
    private float mainPathX, mainPathY;
    private Path mainPath;
    private FingerPath pathsMain;

    //Fingerpath Bogen Infos
    private float bowPathX, bowPathY;
    private Path bowPath;
    private FingerPath pathsBow;

    //Fightbooleans
    private boolean touchDefend;
    private boolean touchAttack;
    private boolean touchHardAttack;
    private boolean touchGesture;

    //Defend
    private int minDefendLineLength;

    //Angriff
    private int minAttackLineLength;

    private OnlineFeatures onlineFeatures;

    //Modi
    private GlobalVariables globalVariables;

    //Konstruktor
    public FightMode(Context context, int screenX, int screenY) {
        //Context abspeichern
        fullContext = context;

        //Ladebildschirm anzeigen?
        loading = true;

        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Um die Größe des Bildschirms auch hier zu kennen
        this.screenX = screenX;
        this.screenY = screenY;

        //Um auf die Variablen die man übergeben bekommt zuzugreifen
        globalVariables = GlobalVariables.getInstance();

        //Um auf die Online Features zugreifen zu können
        onlineFeatures = OnlineFeatures.getInstance(auth, database);

        //Alle Bilder einlesen
        initialiseGrafics();

        //Musik einlesen
        initialiseSound(fullContext);

        touchTimerIndex1 = System.currentTimeMillis();

        //Ein paar Sachen auslesen
        getSharedPreferences();

        //man startet erstmal immer mit vollen Leben
        character.setLife();
        //und vollem Mana
        character.recoverMana(1000);

        //Gegner Einrichtung
        enemieSpawnLevel = 1;
        spawnEnemie("Goblin");

        //Nur zur Sicherheit
        if (enemie != null)
            enemie.attackRightNowReset();

        //Standardvariablenreset
        levelUpPossible = character.canLevelUp();
        chooseWeapon = false;
        alreadyVibrating = false;
        noButtonKlicked = true;
        runmodeFinished();
    }

    //update ist quasi das DENKEN in der App
    public void updateFight() {
        //Greift der Gegner an?
        if (enemie != null) {
            defendNecessary = enemie.attackUpdate();
            if (defendNecessary)
                gotAttacked();
            //Statuseffekte
            statusEffectUpdate();
            //Angriffskreis
            circleUpdate();
        }
        //Runmode läuft
        if (runmode && enemie == null) {
            enemieSpawnLevel++;
            if (scoreRun != null)
                scoreRun.incrementPoints();
            spawnEnemie("Goblin");
        }

        //Um auf jeden Fall die Hintergrundmusik anzuhaben
        playSound(0);

        //Auf Firebase warten
        if (onlineFeatures != null) {
            onlineFeatures.waitForFirebase(fullContext);
        }
    }

    //draw ist das ZEICHNEN in der App
    public void drawFight(SurfaceHolder ourHolder, Canvas canvas, Paint paint) {
        //Standardfehlerabfangen
        if(ourHolder.getSurface().isValid()) {
            try {
                //canvas wird das Zeichenobjekt
                canvas = ourHolder.lockCanvas();
            } catch (IllegalArgumentException e) {
                ourHolder.unlockCanvasAndPost(canvas);
            }

            try {
                //Textfarbe setzen
                paint.setColor(Color.argb(255, 255, 255, 0));
                //Damit wir nicht ausversehen die Texte nur als Rand haben
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(textSize);

                if (loading) {
                    if (bitmapBackgroundLoading != null) {
                        canvas.drawBitmap(bitmapBackgroundLoading, 0, 0, paint);
                    }
                }
                else {
                    //Hintergrund malen
                    if (bitmapBackgroundFights != null)
                        canvas.drawBitmap(bitmapBackgroundFights, backgroundFightX1, 0, paint);

                    //Gegner Text malen
                    if (enemie != null)
                        canvas.drawText("Gegner: " + enemie.getName(), textX, textY, paint);
                    if (enemie != null)
                        canvas.drawText("Leben: " + enemie.getLife(), textX, 2 * textY, paint);
                    if (enemieSpawnLevel >= 1) {
                        canvas.drawText("Gegnerisches Spawnlevel: " + enemieSpawnLevel, textX, 4 * textY, paint);
                    }

                    //Run Score / Highscore
                    if (runmode && scoreRun != null)
                        canvas.drawText("Score: " + scoreRun.getPoints(), textX, 5 * textY, paint);
                    if (highscoreRun != null)
                        canvas.drawText("Highscore: " + highscoreRun.getPoints(), screenMitte, 5 * textY, paint);

                    //Character Stats malen:
                    if (character != null) {
                        if (character.getExperiencedNeeded() == -1)
                            canvas.drawText("Max Level!", screenMitte, textY, paint);
                        else
                            canvas.drawText("Erfahrung: " + character.getExperience() + " / " + character.getExperiencedNeeded(), screenMitte, textY, paint);
                        canvas.drawText("Level: " + character.getLevel(), screenMitte, 2 * textY, paint);
                        canvas.drawText("Leben: " + character.getLife(), screenMitte, 3 * textY, paint);
                    }

                    //Du kannst Levelupn
                    if (levelUpPossible && !defendNecessary) {
                        paint.setTextSize(textSizeBig);
                        canvas.drawText("LEVEL UP AVAILABLE!!", textX, 14 * textY, paint);
                        paint.setTextSize(textSize);
                    }

                    //Du wirst angegriffen
                    if (defendNecessary) {
                        paint.setTextSize(textSizeBig);
                        canvas.drawText("VERTEIDIGE DICH!!", textX, 14 * textY, paint);
                        paint.setTextSize(textSize);
                    }

                    //SpawnButtons malen
                    if (bitmapSpawnLevelUp != null)
                        canvas.drawBitmap(bitmapSpawnLevelUp, bitmapSpawnLevelUpX, bitmapSpawnLevelUpY, paint);
                    if (bitmapSpawnGoblinButton != null)
                        canvas.drawBitmap(bitmapSpawnGoblinButton, bitmapSpawnGoblinButtonX, bitmapSpawnGoblinButtonY, paint);
                    if (bitmapSpawnOrkButton != null)
                        canvas.drawBitmap(bitmapSpawnOrkButton, bitmapSpawnOrkButtonX, bitmapSpawnOrkButtonY, paint);
                    if (bitmapSpawnDiebButton != null)
                        canvas.drawBitmap(bitmapSpawnDiebButton, bitmapSpawnDiebButtonX, bitmapSpawnDiebButtonY, paint);
                    if (bitmapSpawnRiese != null)
                        canvas.drawBitmap(bitmapSpawnRiese, bitmapSpawnRieseX, bitmapSpawnRieseY, paint);

                    //Endlos Modus RUN
                    if (bitmapRunButton != null)
                        canvas.drawBitmap(bitmapRunButton, bitmapRunButtonX, bitmapRunButtonY, paint);

                    //Online Buttons
                    if (bitmapOnlineButton != null && FirebaseAuth.getInstance().getCurrentUser() == null)
                        canvas.drawBitmap(bitmapOnlineButton, bitmapOnlineButtonX, bitmapOnlineButtonY, paint);
                    if (bitmapDownloadButton != null && FirebaseAuth.getInstance().getCurrentUser() != null)
                        canvas.drawBitmap(bitmapDownloadButton, bitmapDownloadButtonX, bitmapDownloadButtonY, paint);
                    if (bitmapUploadButton != null && FirebaseAuth.getInstance().getCurrentUser() != null)
                        canvas.drawBitmap(bitmapUploadButton, bitmapUploadButtonX, bitmapUploadButtonY, paint);
                    //if (bitmapHighscoreButton != null && FirebaseAuth.getInstance().getCurrentUser() != null)
                    //    canvas.drawBitmap(bitmapHighscoreButton, bitmapHighscoreButtonX, bitmapHighscoreButtonY, paint);

                    //Levelup Buttons
                    if (levelUpPossible && !defendNecessary) {
                        if (bitmapLevelUpDamage != null)
                            canvas.drawBitmap(bitmapLevelUpDamage, bitmapLevelUpDamageX, bitmapLevelUpDamageY, paint);
                        if (bitmapLevelUpDefense != null)
                            canvas.drawBitmap(bitmapLevelUpDefense, bitmapLevelUpDefenseX, bitmapLevelUpDefenseY, paint);
                        if (bitmapLevelUpLife != null)
                            canvas.drawBitmap(bitmapLevelUpLife, bitmapLevelUpLifeX, bitmapLevelUpLifeY, paint);
                        if (bitmapLevelUpMana != null && character.getMaxMana() < 100)
                            canvas.drawBitmap(bitmapLevelUpMana, bitmapLevelUpManaX, bitmapLevelUpManaY, paint);
                    }

                    //Chooseweapon Buttons
                    if (bitmapWaffeWechselnButton != null)
                        canvas.drawBitmap(bitmapWaffeWechselnButton, bitmapWaffeWechselnButtonX, bitmapWaffeWechselnButtonY, paint);
                    if (chooseWeapon) {
                        if (bitmapWaffeHackeButton != null)
                            canvas.drawBitmap(bitmapWaffeHackeButton, bitmapWaffeHackeButtonX, bitmapWaffeHackeButtonY, paint);
                        if (bitmapWaffeHolzschildButton != null)
                            canvas.drawBitmap(bitmapWaffeHolzschildButton, bitmapWaffeHolzschildButtonX, bitmapWaffeHolzschildButtonY, paint);
                        if (bitmapWaffeKnueppelButton != null)
                            canvas.drawBitmap(bitmapWaffeKnueppelButton, bitmapWaffeKnueppelButtonX, bitmapWaffeKnueppelButtonY, paint);
                        if (bitmapWaffeRiesenschwertButton != null)
                            canvas.drawBitmap(bitmapWaffeRiesenschwertButton, bitmapWaffeRiesenschwertButtonX, bitmapWaffeRiesenschwertButtonY, paint);
                        if (bitmapWaffeSchwertButton != null)
                            canvas.drawBitmap(bitmapWaffeSchwertButton, bitmapWaffeSchwertButtonX, bitmapWaffeSchwertButtonY, paint);
                    }

                    //Kreis zeichnen
                    if (circleInRange && enemie != null) {
                        //Nur die Außenlinien
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(circleStrokeWidth);

                        //Fester INNERER Kreis
                        paint.setColor(Color.argb(255, 47, 156, 29));
                        canvas.drawCircle(touchXPointer1, touchYPointer1, circleRadiusMin, paint);

                        //Fester ÄUSSERER Kreis
                        paint.setColor(outerCircleColor);
                        canvas.drawCircle(touchXPointer1, touchYPointer1, circleRadius, paint);
                    }

                    //Wir zeichen den Strich immer
                    if (actionDown && enemie != null) {
                        //Abwehrstrich
                        //Initialisierung
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setColor(Color.argb(255, 47, 156, 29));
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        paint.setXfermode(null);
                        paint.setAlpha(0xff);

                        //Zeichnen
                        if (pathsMain != null) {
                            paint.setStrokeWidth(pathsMain.strokeWidth);
                            paint.setMaskFilter(null);

                            canvas.drawPath(pathsMain.path, paint);
                        }

                        if (pathsBow != null) {
                            paint.setStrokeWidth(pathsBow.strokeWidth);
                            paint.setMaskFilter(null);

                            canvas.drawPath(pathsBow.path, paint);
                        }
                    }
                }

            } catch (NullPointerException e) {
                setSharedPreferences();
                initialiseGrafics();
                return;
            }
            //Alles auf den Bildschirm malen
            //Und Canvas wieder freilassen (um Fehler zu minimieren(das könnte sogar der Fehler meiner ersten App gewesen sein))
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    //SharedPreferences auslesen
    public void getSharedPreferences() {
        //Allgemeine Daten die beide Modi brauchen können
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberryFight", 0);
        characterStatusEffect = new StatusEffect(sharedPreferences.getString("characterStatusEffect", "default"));

        //Character erstellen mit all den gespeicherten Infos
        character = new Character(new Weapon(sharedPreferences.getString("characterEquippedWeapon", "Hacke")), sharedPreferences.getInt("characterBaseDamage", 1), sharedPreferences.getInt("characterLife", 25), sharedPreferences.getInt("characterMaxLife", 25), sharedPreferences.getInt("characterDefense", 1), sharedPreferences.getInt("characterExperience", 0), sharedPreferences.getInt("characterLevel", 1), sharedPreferences.getInt("characterMaxMana", 30), sharedPreferences.getInt("characterCurrentMana", 30));

        String highscoreRunString = sharedPreferences.getString("highscoreRun", "");
        highscoreRun = new ScoreRun(highscoreRunString);
    }

    //SharedPreferences wieder sicher verwahren
    public void setSharedPreferences() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberryFight", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (character != null) {
            editor.putString("characterEquippedWeapon", character.getEquipedWeapon().getName());
            editor.putInt("characterBaseDamage",character.getBaseDamage());
            editor.putInt("characterLife", character.getLife());
            editor.putInt("characterMaxLife", character.getMaxLife());
            editor.putInt("characterDefense", character.getBaseDefense());
            editor.putInt("characterExperience", character.getExperience());
            editor.putInt("characterLevel", character.getLevel());
            editor.putInt("characterMaxMana", character.getMaxMana());
            editor.putInt("characterCurrentMana", character.getMana());
            if (characterStatusEffect != null)
                editor.putString("characterStatusEffect", characterStatusEffect.getName());
        }
        editor.putString("highscoreRun", highscoreRun.exportScoreRun());

        editor.apply();
    }

    //Was passiert wenn man den Touchscreen im FIGHT Modus berührt
    public boolean onTouchFight(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        //Alle Arten von Bewegung (auf dem Screen) die man bearbeiten will
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //Ein paar Variablen resettenn
                touchX1 = motionEvent.getX();
                touchY1 = motionEvent.getY();
                alreadyVibrating = false;
                circleInRange = false;

                touchDefend = false;
                touchAttack = false;
                touchHardAttack = false;
                touchGesture = false;

                noButtonKlicked = false;

                //War da ein Button?
                if (touchX1 >= bitmapSpawnLevelUpX && touchX1 < (bitmapSpawnLevelUpX + bitmapSpawnLevelUp.getWidth())
                        && touchY1 >= bitmapSpawnLevelUpY && touchY1 < (bitmapSpawnLevelUpY + bitmapSpawnLevelUp.getHeight())) {
                    playSound(4);
                    enemieSpawnLevel++;
                    break;
                }
                if (touchX1 >= bitmapSpawnGoblinButtonX && touchX1 < (bitmapSpawnGoblinButtonX + bitmapSpawnGoblinButton.getWidth())
                        && touchY1 >= bitmapSpawnGoblinButtonY && touchY1 < (bitmapSpawnGoblinButtonY + bitmapSpawnGoblinButton.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Goblin");
                    }
                    break;
                }
                if (touchX1 >= bitmapSpawnDiebButtonX && touchX1 < (bitmapSpawnDiebButtonX + bitmapSpawnDiebButton.getWidth())
                        && touchY1 >= bitmapSpawnDiebButtonY && touchY1 < (bitmapSpawnDiebButtonY + bitmapSpawnDiebButton.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Dieb");
                    }
                    break;
                }
                if (touchX1 >= bitmapSpawnOrkButtonX && touchX1 < (bitmapSpawnOrkButtonX + bitmapSpawnOrkButton.getWidth())
                        && touchY1 >= bitmapSpawnOrkButtonY && touchY1 < (bitmapSpawnOrkButtonY + bitmapSpawnOrkButton.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Ork");
                    }
                    break;
                }
                if (touchX1 >= bitmapSpawnRieseX && touchX1 < (bitmapSpawnRieseX + bitmapSpawnRiese.getWidth())
                        && touchY1 >= bitmapSpawnRieseY && touchY1 < (bitmapSpawnRieseY + bitmapSpawnRiese.getHeight())) {
                    playSound(4);
                    if (enemie == null) {
                        spawnEnemie("Riese");
                    }
                    break;
                }

                if (touchX1 >= bitmapRunButtonX && touchX1 < (bitmapRunButtonX + bitmapRunButton.getWidth())
                        && touchY1 >= bitmapRunButtonY && touchY1 < (bitmapRunButtonY + bitmapRunButton.getHeight())) {
                    playSound(4);
                    initialiseRunmode();
                    break;
                }

                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    if (touchX1 >= bitmapOnlineButtonX && touchX1 < (bitmapOnlineButtonX + bitmapOnlineButton.getWidth())
                            && touchY1 >= bitmapOnlineButtonY && touchY1 < (bitmapOnlineButtonY + bitmapOnlineButton.getHeight())) {
                        playSound(4);
                        onlineFeatures.showSignInDialog(fullContext);
                        break;
                    }
                }

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (touchX1 >= bitmapUploadButtonX && touchX1 < (bitmapUploadButtonX + bitmapUploadButton.getWidth())
                            && touchY1 >= bitmapUploadButtonY && touchY1 < (bitmapUploadButtonY + bitmapUploadButton.getHeight())) {
                        playSound(4);
                        setSharedPreferences();
                        onlineFeatures.backupGamedata(fullContext);
                        break;
                    }

                    if (touchX1 >= bitmapDownloadButtonX && touchX1 < (bitmapDownloadButtonX + bitmapDownloadButton.getWidth())
                            && touchY1 >= bitmapDownloadButtonY && touchY1 < (bitmapDownloadButtonY + bitmapDownloadButton.getHeight())) {
                        playSound(4);
                        onlineFeatures.getSavedGamedata(fullContext);
                        break;
                    }

                    /*if (touchX1 >= bitmapHighscoreButtonX && touchX1 < (bitmapHighscoreButtonX + bitmapHighscoreButton.getWidth())
                            && touchY1 >= bitmapHighscoreButtonY && touchY1 < (bitmapHighscoreButtonY + bitmapHighscoreButton.getHeight())) {
                        playSound(4);

                        break;
                    }*/
                }
                //Level Up
                if (levelUpPossible) {
                    if (touchX1 >= bitmapLevelUpDamageX && touchX1 < (bitmapLevelUpDamageX + bitmapLevelUpDamage.getWidth())
                            && touchY1 >= bitmapLevelUpDamageY && touchY1 < (bitmapLevelUpDamageY + bitmapLevelUpDamage.getHeight())) {
                        playSound(4);
                        character.levelUp(1, 0, 0, 0);
                        if (!character.canLevelUp())
                            levelUpPossible = false;
                        break;
                    }
                    if (touchX1 >= bitmapLevelUpDefenseX && touchX1 < (bitmapLevelUpDefenseX + bitmapLevelUpDefense.getWidth())
                            && touchY1 >= bitmapLevelUpDefenseY && touchY1 < (bitmapLevelUpDefenseY + bitmapLevelUpDefense.getHeight())) {
                        playSound(4);
                        character.levelUp(0, 0, 1, 0);
                        if (!character.canLevelUp())
                            levelUpPossible = false;
                        break;
                    }
                    if (touchX1 >= bitmapLevelUpLifeX && touchX1 < (bitmapLevelUpLifeX + bitmapLevelUpLife.getWidth())
                            && touchY1 >= bitmapLevelUpLifeY && touchY1 < (bitmapLevelUpLifeY + bitmapLevelUpLife.getHeight())) {
                        playSound(4);
                        character.levelUp(0, 1, 0, 0);
                        if (!character.canLevelUp())
                            levelUpPossible = false;
                        break;
                    }
                    if (touchX1 >= bitmapLevelUpManaX && touchX1 < (bitmapLevelUpManaX + bitmapLevelUpMana.getWidth())
                            && touchY1 >= bitmapLevelUpManaY && touchY1 < (bitmapLevelUpManaY + bitmapLevelUpMana.getHeight())) {
                        playSound(4);
                        character.levelUp(0, 0, 0, 100);
                        if (!character.canLevelUp())
                            levelUpPossible = false;
                        break;
                    }
                }

                //Weapon Choose Buttons
                if (touchX1 >= bitmapWaffeWechselnButtonX && touchX1 < (bitmapWaffeWechselnButtonX + bitmapWaffeWechselnButton.getWidth())
                        && touchY1 >= bitmapWaffeWechselnButtonY && touchY1 < (bitmapWaffeWechselnButtonY + bitmapWaffeWechselnButton.getHeight())) {
                    playSound(4);
                    chooseWeapon = !chooseWeapon;
                    break;
                }
                if (chooseWeapon) {
                    Weapon equippedWeapon;
                    if (touchX1 >= bitmapWaffeHackeButtonX && touchX1 < (bitmapWaffeHackeButtonX + bitmapWaffeHackeButton.getWidth())
                            && touchY1 >= bitmapWaffeHackeButtonY && touchY1 < (bitmapWaffeHackeButtonY + bitmapWaffeHackeButton.getHeight())) {
                        playSound(8);
                        equippedWeapon = new Weapon("Bogen");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeHolzschildButtonX && touchX1 < (bitmapWaffeHolzschildButtonX + bitmapWaffeHolzschildButton.getWidth())
                            && touchY1 >= bitmapWaffeHolzschildButtonY && touchY1 < (bitmapWaffeHolzschildButtonY + bitmapWaffeHolzschildButton.getHeight())) {
                        playSound(8);
                        equippedWeapon = new Weapon("Holzschild");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeKnueppelButtonX && touchX1 < (bitmapWaffeKnueppelButtonX + bitmapWaffeKnueppelButton.getWidth())
                            && touchY1 >= bitmapWaffeKnueppelButtonY && touchY1 < (bitmapWaffeKnueppelButtonY + bitmapWaffeKnueppelButton.getHeight())) {
                        playSound(8);
                        equippedWeapon = new Weapon("Knüppel");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeRiesenschwertButtonX && touchX1 < (bitmapWaffeRiesenschwertButtonX + bitmapWaffeRiesenschwertButton.getWidth())
                            && touchY1 >= bitmapWaffeRiesenschwertButtonY && touchY1 < (bitmapWaffeRiesenschwertButtonY + bitmapWaffeRiesenschwertButton.getHeight())) {
                        playSound(8);
                        equippedWeapon = new Weapon("Riesenschwert");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                    if (touchX1 >= bitmapWaffeSchwertButtonX && touchX1 < (bitmapWaffeSchwertButtonX + bitmapWaffeSchwertButton.getWidth())
                            && touchY1 >= bitmapWaffeSchwertButtonY && touchY1 < (bitmapWaffeSchwertButtonY + bitmapWaffeSchwertButton.getHeight())) {
                        playSound(8);
                        equippedWeapon = new Weapon("Schwert");
                        character.setEquipedWeapon(equippedWeapon);
                        chooseWeapon = false;
                        break;
                    }
                }

                //Abwehrstrich
                mainPath = new Path();
                pathsMain = new FingerPath(20, mainPath);

                mainPath.reset();
                mainPath.moveTo(x, y);
                mainPathX = x;
                mainPathY = y;

                noButtonKlicked = true;
                actionDown = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //Einhändige Waffe oder Schild
                if (character.getEquipedWeapon().getWaffenart() == 0 || character.getEquipedWeapon().getWaffenart() == 3)
                    break;
                //zweihändige Waffe
                if (character.getEquipedWeapon().getWaffenart() == 1) {
                    touchIndex1 = motionEvent.getActionIndex();
                    touchXPointer1 = motionEvent.getX(touchIndex1);
                    touchYPointer1 = motionEvent.getY(touchIndex1);
                    touchTimerIndex1 = System.currentTimeMillis();
                    actionPointerDown = true;
                    circleInRange = true;
                }
                //Bogen
                if (character.getEquipedWeapon().getWaffenart() == 2) {
                    touchIndex1 = motionEvent.getActionIndex();
                    touchXPointer1 = motionEvent.getX(touchIndex1);
                    touchYPointer1 = motionEvent.getY(touchIndex1);

                    bowPath = new Path();
                    pathsBow = new FingerPath(20, bowPath);

                    bowPath.reset();
                    bowPath.moveTo(touchXPointer1, touchYPointer1);
                    bowPathX = touchXPointer1;
                    bowPathY = touchYPointer1;

                    actionPointerDown = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //Bei Einhandangriff
                if (!actionPointerDown && !circleInRange && character.getEquipedWeapon().getWaffenart() != 2) {
                    //Abwehrlinie muss flüssig aussehen
                    float dx = Math.abs(x - mainPathX);
                    float dy = Math.abs(y - mainPathY);

                    try {
                        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                            mainPath.quadTo(mainPathX, mainPathY, (x + mainPathX) / 2, (y + mainPathY) / 2);
                            mainPathX = x;
                            mainPathY = y;
                        }
                    } catch (NullPointerException e) {
                        Log.d("Abwehrstrich zeichnen", e.toString());
                    }
                    break;
                }
                //Bei Zweihandangriff verschwindet die Line
                if (actionPointerDown && circleInRange) {
                    mainPath.reset();
                    pathsMain = null;
                    break;
                }
                //Beim Bogen zeichnen wir eine andere Linie
                if (actionPointerDown && !circleInRange) {
                    int pointerCount = motionEvent.getPointerCount();
                    for(int i = 0; i < pointerCount; ++i) {
                        if (motionEvent.getPointerId(i) == 1) {
                            float bowX = motionEvent.getX(1);
                            float bowY = motionEvent.getY(1);
                            //Abwehrlinie muss flüssig aussehen
                            float dx = Math.abs(bowX - bowPathX);
                            float dy = Math.abs(bowY - bowPathY);

                            try {
                                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                                    Log.d("Bogen, ", "gezeichnet");
                                    bowPath.quadTo(bowPathX, bowPathY, (bowX + bowPathX) / 2, (bowY + bowPathY) / 2);
                                    bowPathX = bowX;
                                    bowPathY = bowY;
                                }
                            } catch (NullPointerException e) {
                                Log.d("Abwehrstrich zeichnen", e.toString());
                            }
                        }
                    }
                    break;
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (touchIndex1 == motionEvent.getActionIndex()) {
                    actionPointerDown = false;

                    if (bowPath != null)
                        bowPath.reset();
                    pathsBow = null;

                    //Starker Angriff
                    if (circleInRange && !touchAttack && !touchDefend && !touchGesture && !touchHardAttack) {
                        touchHardAttack = true;
                        hardAttack();
                        break;
                    }
                    //Bogenangriff
                    if (Math.abs(touchXPointer1 - motionEvent.getX(1)) < Math.round(Math.abs(touchYPointer1 - motionEvent.getY(1))/5) && motionEvent.getY(1) - touchYPointer1 > minAttackLineLength) {
                        if (!circleInRange && !touchAttack && !touchDefend && !touchGesture && !touchHardAttack) {
                            touchAttack = true;
                            attack(Math.abs(touchYPointer1 - motionEvent.getY(1)));
                            break;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (actionDown) {
                    actionDown = false;

                    //Die Linie verschwindet ja wieder nachdem man losgelassen hat
                    mainPath.lineTo(mainPathX, mainPathY);
                    mainPath.reset();
                    pathsMain = null;

                    //Fähigkeiten Spells
                    if (noButtonKlicked) { //Solange kein Button benutzt wurde
                        //Angriff
                        if (Math.abs(touchX1 - motionEvent.getX()) < Math.round(Math.abs(touchY1 - motionEvent.getY())/5) && touchY1 - motionEvent.getY() > minAttackLineLength) {
                            if (!touchAttack && !touchDefend && !touchGesture && !touchHardAttack && character.getEquipedWeapon().getWaffenart() != 2) {
                                touchAttack = true;
                                attack(Math.abs(touchY1 - motionEvent.getY()));
                                break;
                            }
                        }
                        //Verteidigung
                        if (Math.abs(touchX1 - motionEvent.getX()) > minDefendLineLength && Math.abs(touchY1 - motionEvent.getY()) < (Math.round(Math.abs(touchX1 - motionEvent.getX()))/5)) {
                            if (!touchAttack && !touchDefend && !touchGesture && !touchHardAttack) {
                                defend();
                                break;
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    //Jede Art von Sound abspielen
    private void playSound(int whichOne) {
        //whichone Legende: 0 -> Hintergrundmusik; 1 -> Sähsound; 2 -> Uhr Sound; 3 -> Erntesound; 4 -> Buttonklick; 5 -> Geld; 6 -> hit; 7 -> fireballsound; 8 -> knifesharpener

        //Zufallszahl generieren um die aussäh und erntegeräusche abwechslungsreicher zu machen
        Random random = new Random();
        int randomInt = 1;

        for (int i = 1; i <= 9; i++) {
            randomInt = random.nextInt(10);
        }
        switch (whichOne) {
            case 0:
                if (globalVariables.getMusicOn())
                    backgroundMusicPlayer();
                break;
            case 4:
                if (globalVariables.getSoundOn())
                    soundPool.play(click1, 1, 1, 0, 0, 1);
                break;
            case 6:
                if (globalVariables.getSoundOn()) {
                    switch (randomInt) {
                        case 1:
                            soundPool.play(hit1, 1, 1, 0, 0, 1);
                            break;
                        case 2:
                            soundPool.play(hit2, 1, 1, 0, 0, 1);
                            break;
                        case 3:
                            soundPool.play(hit3, 1, 1, 0, 0, 1);
                            break;
                        case 4:
                            soundPool.play(hit4, 1, 1, 0, 0, 1);
                            break;
                        case 5:
                            soundPool.play(hit5, 1, 1, 0, 0, 1);
                            break;
                        case 6:
                            soundPool.play(hit6, 1, 1, 0, 0, 1);
                            break;
                        case 7:
                            soundPool.play(hit7, 1, 1, 0, 0, 1);
                            break;
                        case 8:
                            soundPool.play(hit8, 1, 1, 0, 0, 1);
                            break;
                        case 9:
                            soundPool.play(hit9, 1, 1, 0, 0, 1);
                            break;
                    }
                }
                break;
            case 7:
                if (globalVariables.getSoundOn())
                    soundPool.play(fireballSound, 1, 1, 0, 0, 1);
                break;
            case 8:
                if (globalVariables.getSoundOn())
                    soundPool.play(knifesharpener, 1, 1, 0, 0, 1);
                break;
        }
    }

    //Musik einlesen
    private void initialiseSound(Context context) {
        //In neuen Versionen soll man das halt jetzt so machen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            //Aber ich will die alten Versionen trz nicht verlieren deshalb lassen wir das mal drin
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //Musik tatsächlich einladen
            descriptor = assetManager.openFd("click1.wav");
            click1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit1.wav");
            hit1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit2.wav");
            hit2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit3.wav");
            hit3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit4.wav");
            hit4 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit5.wav");
            hit5 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit6.wav");
            hit6 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit7.wav");
            hit7 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit8.wav");
            hit8 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit9.wav");
            hit9 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("fireballsound.wav");
            fireballSound = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("knifesharpener.wav");
            knifesharpener = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            //Errormessage
            Log.e("error", "failed to load sound files");
        }
    }

    //Alle Bilder einlesen
    private void initialiseGrafics() {
        //Textposition setzen
        textX = getScaledCoordinates(screenX, 1080, 20);
        textY = getScaledCoordinates(screenY, 1920, 50);
        //Textgröße errechnen
        textSize = getScaledBitmapSize(screenX, 1080, 50);
        textSizeBig = getScaledBitmapSize(screenX, 1080, 100);
        //Verteidigungslinie
        minDefendLineLength = getScaledBitmapSize(screenX, 1080, 250);
        //Angriffslinie
        minAttackLineLength = getScaledBitmapSize(screenX, 1080, 450);
        screenMitte = screenX/2;

        //Kreisinfos berechnen
        if (circleStrokeWidth == 0) {
            circleStrokeWidth = getScaledBitmapSize(screenX, 1080, 15);
            //circleRadiusMax = getScaledBitmapSize(screenX, 1080, 250);
            circleRadiusMin = getScaledBitmapSize(screenX, 1080, 100);
            circleRadiusAbsoluteMin = getScaledBitmapSize(screenX, 1080, 75);
        }

        //Hintergrundbildposition
        backgroundFightX1 = getScaledCoordinates(screenX, 1080, -540);

        //Hinntergrundbildscaling
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapBackgroundFights = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.fightbackground2, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapBackgroundFights = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.fightbackground2, 200, 200);
        bitmapBackgroundFights = Bitmap.createScaledBitmap(bitmapBackgroundFights, screenX * 2, screenY, false);

        //Loadingscreen Bild:
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Nur die Ränder werden eingefügt
        //Hintergrundbild einfügen
        bitmapBackgroundLoading = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.loadingscreen, options);
        //Dann Bitmap gerescaled einfügen und die Anzeige auf die Standardgröße neuscalen
        bitmapBackgroundLoading = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.loadingscreen, 250, 250);
        bitmapBackgroundLoading = Bitmap.createScaledBitmap(bitmapBackgroundLoading, screenX, screenY, false);

        //Buttons
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapSpawnDiebButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.spawndieb_button, options);
        bitmapSpawnDiebButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.spawndieb_button, 100, 100);
        bitmapSpawnDiebButton = Bitmap.createScaledBitmap(bitmapSpawnDiebButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapSpawnGoblinButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.spawngoblin_button, options);
        bitmapSpawnGoblinButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.spawngoblin_button, 100, 100);
        bitmapSpawnGoblinButton = Bitmap.createScaledBitmap(bitmapSpawnGoblinButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapSpawnOrkButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.spawnork_button, options);
        bitmapSpawnOrkButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.spawnork_button, 100, 100);
        bitmapSpawnOrkButton = Bitmap.createScaledBitmap(bitmapSpawnOrkButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapSpawnLevelUp = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.spawnlevelplus_button, options);
        bitmapSpawnLevelUp = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.spawnlevelplus_button, 100, 100);
        bitmapSpawnLevelUp = Bitmap.createScaledBitmap(bitmapSpawnLevelUp, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapSpawnRiese = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.spawnriese_button, options);
        bitmapSpawnRiese = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.spawnriese_button, 100, 100);
        bitmapSpawnRiese = Bitmap.createScaledBitmap(bitmapSpawnRiese, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapRunButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.run_button, options);
        bitmapRunButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.run_button, 100, 100);
        bitmapRunButton = Bitmap.createScaledBitmap(bitmapRunButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapOnlineButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.online_button, options);
        bitmapOnlineButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.online_button, 100, 100);
        bitmapOnlineButton = Bitmap.createScaledBitmap(bitmapOnlineButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapDownloadButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.download_button, options);
        bitmapDownloadButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.download_button, 100, 100);
        bitmapDownloadButton = Bitmap.createScaledBitmap(bitmapDownloadButton, getScaledBitmapSize(screenX, 1080, 70), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapUploadButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.upload_button, options);
        bitmapUploadButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.upload_button, 100, 100);
        bitmapUploadButton = Bitmap.createScaledBitmap(bitmapUploadButton, getScaledBitmapSize(screenX, 1080, 70), getScaledBitmapSize(screenY, 1920, 100), false);

        /*options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapHighscoreButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.highscore_button, options);
        bitmapHighscoreButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.highscore_button, 100, 100);
        bitmapHighscoreButton = Bitmap.createScaledBitmap(bitmapHighscoreButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);*/

        //Feste Werte setzen
        bitmapRunButtonX = getScaledCoordinates(screenX, 1080, 270);
        bitmapRunButtonY = getScaledCoordinates(screenY, 1920, 300);
        bitmapSpawnLevelUpX = getScaledCoordinates(screenX, 1080, 20);
        bitmapSpawnLevelUpY = bitmapRunButtonY;
        bitmapSpawnGoblinButtonX = bitmapSpawnLevelUpX;
        bitmapSpawnGoblinButtonY = getScaledCoordinates(screenY, 1920, 450);
        bitmapSpawnOrkButtonX = bitmapRunButtonX;
        bitmapSpawnOrkButtonY = bitmapSpawnGoblinButtonY;
        bitmapSpawnDiebButtonX = getScaledCoordinates(screenX, 1080, 520);
        bitmapSpawnDiebButtonY = bitmapSpawnGoblinButtonY;
        bitmapSpawnRieseX = getScaledCoordinates(screenX, 1080, 770);
        bitmapSpawnRieseY = bitmapSpawnGoblinButtonY;
        bitmapOnlineButtonX = bitmapSpawnDiebButtonX;
        bitmapOnlineButtonY = bitmapRunButtonY;
        bitmapUploadButtonX = bitmapOnlineButtonX;
        bitmapUploadButtonY = bitmapOnlineButtonY;
        bitmapDownloadButtonX = getScaledCoordinates(screenX, 1080, 650);
        bitmapDownloadButtonY = bitmapOnlineButtonY;
        //bitmapHighscoreButtonX = bitmapSpawnRieseX;
        //bitmapHighscoreButtonY = bitmapOnlineButtonY;

        //Level UP Buttons
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapLevelUpDamage = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.levelupdamage_button, options);
        bitmapLevelUpDamage = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.levelupdamage_button, 100, 100);
        bitmapLevelUpDamage = Bitmap.createScaledBitmap(bitmapLevelUpDamage, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapLevelUpDefense = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.levelupdefense_button, options);
        bitmapLevelUpDefense = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.levelupdefense_button, 100, 100);
        bitmapLevelUpDefense = Bitmap.createScaledBitmap(bitmapLevelUpDefense, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapLevelUpLife = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.leveluplife_button, options);
        bitmapLevelUpLife = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.leveluplife_button, 100, 100);
        bitmapLevelUpLife = Bitmap.createScaledBitmap(bitmapLevelUpLife, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapLevelUpMana = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.levelupattackspeed_button, options);
        bitmapLevelUpMana = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.levelupattackspeed_button, 100, 100);
        bitmapLevelUpMana = Bitmap.createScaledBitmap(bitmapLevelUpMana, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //bitmapLevelUpMana
        bitmapLevelUpDamageX = getScaledCoordinates(screenX, 1080, 50);
        bitmapLevelUpDamageY = getScaledCoordinates(screenY, 1920, 850);
        bitmapLevelUpDefenseX = getScaledCoordinates(screenX, 1080, 300);
        bitmapLevelUpDefenseY = bitmapLevelUpDamageY;
        bitmapLevelUpLifeX = getScaledCoordinates(screenX, 1080, 550);
        bitmapLevelUpLifeY = bitmapLevelUpDamageY;
        bitmapLevelUpManaX = getScaledCoordinates(screenX, 1080, 800);
        bitmapLevelUpManaY = bitmapLevelUpDamageY;

        //Weaponchoose Buttons
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapWaffeHackeButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.waffehacke_button, options);
        bitmapWaffeHackeButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.waffehacke_button, 100, 100);
        bitmapWaffeHackeButton = Bitmap.createScaledBitmap(bitmapWaffeHackeButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapWaffeHolzschildButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.waffeholzschild_button, options);
        bitmapWaffeHolzschildButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.waffeholzschild_button, 100, 100);
        bitmapWaffeHolzschildButton = Bitmap.createScaledBitmap(bitmapWaffeHolzschildButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapWaffeKnueppelButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.waffeknueppel_button, options);
        bitmapWaffeKnueppelButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.waffeknueppel_button, 100, 100);
        bitmapWaffeKnueppelButton = Bitmap.createScaledBitmap(bitmapWaffeKnueppelButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapWaffeRiesenschwertButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.wafferiesenschwert_button, options);
        bitmapWaffeRiesenschwertButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.wafferiesenschwert_button, 100, 100);
        bitmapWaffeRiesenschwertButton = Bitmap.createScaledBitmap(bitmapWaffeRiesenschwertButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapWaffeSchwertButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.waffeschwert_button, options);
        bitmapWaffeSchwertButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.waffeschwert_button, 100, 100);
        bitmapWaffeSchwertButton = Bitmap.createScaledBitmap(bitmapWaffeSchwertButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmapWaffeWechselnButton = BitmapFactory.decodeResource(fullContext.getResources(), R.drawable.waffewechseln_button, options);
        bitmapWaffeWechselnButton = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.waffewechseln_button, 100, 100);
        bitmapWaffeWechselnButton = Bitmap.createScaledBitmap(bitmapWaffeWechselnButton, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 100), false);

        //Feste Werte
        bitmapWaffeWechselnButtonX = getScaledCoordinates(screenX, 1080, 50);
        bitmapWaffeWechselnButtonY = getScaledCoordinates(screenY, 1920, 1620);
        bitmapWaffeSchwertButtonX = getScaledCoordinates(screenX, 1080, 300);
        bitmapWaffeSchwertButtonY = bitmapWaffeWechselnButtonY;
        bitmapWaffeRiesenschwertButtonX = getScaledCoordinates(screenX, 1080, 550);
        bitmapWaffeRiesenschwertButtonY = bitmapWaffeWechselnButtonY;
        bitmapWaffeKnueppelButtonX = bitmapWaffeRiesenschwertButtonX;
        bitmapWaffeKnueppelButtonY = getScaledCoordinates(screenY, 1920, 1770);
        bitmapWaffeHolzschildButtonX = bitmapWaffeWechselnButtonX;
        bitmapWaffeHolzschildButtonY = bitmapWaffeKnueppelButtonY;
        bitmapWaffeHackeButtonX = bitmapWaffeSchwertButtonX;
        bitmapWaffeHackeButtonY = bitmapWaffeKnueppelButtonY;

        loading = false;
    }

    //Gegner erstellen
    private void spawnEnemie(String name) {
        enemie = new Enemie(name, enemieSpawnLevel);
        enemieStatusEffect = new StatusEffect("default");
    }

    //AUF ZUM ANGRIFF!! AAAAHHH
    private void attack(float lineLength) {
        if (enemie != null && character != null) {
            //Damage
            if (lineLength >= minAttackLineLength) {
                int meleeDamage = character.getMeleeDamage();
                float extraDamage = (character.getMeleeDamage()*(lineLength - minAttackLineLength) / 1000);
                enemie.defend(meleeDamage + extraDamage);
            }

            //Angriffssound abspielen
            touchAttack = false;
            playSound(6);

            //Lebt der Gegner noch?
            if (!enemie.getLifeStatus()) {
                if (character.setExperience(enemie.getExperience())) {
                    levelUpPossible = true;
                }
                //Zurücksetzen des Gegners und des Charakters
                enemie = null;
                defendNecessary = false;
                if (character != null && !runmode) {
                    character.setLife();
                    character.recoverMana(1000);
                }
                enemieStatusEffect.resetStatusEffect();
            }
        }
    }

    //Der zweihändige Angriff
    private void hardAttack() {
        if (enemie != null && character != null) {
            circleInRange = false;
            //Damage
            //bis 1000 steigt es, bei 1000 sind es 125% dmg. bis zum Abbruch geht es wieder auf 100% runter. es beginnt bei 33%
            if (System.currentTimeMillis() - touchTimerIndex1 <= 1050) {
                enemie.defend(character.getMeleeDamage() + (character.getMeleeDamage()*(((float)(System.currentTimeMillis() - touchTimerIndex1)/1000))));
                Log.d("attack1", "" + (character.getMeleeDamage() + (character.getMeleeDamage()*(((float)(System.currentTimeMillis() - touchTimerIndex1)/1000)))));
            }
            else {
                enemie.defend((2*(character.getMeleeDamage() + character.getMeleeDamage()) - (character.getMeleeDamage()/3 + (character.getMeleeDamage()*(((float)(System.currentTimeMillis() - touchTimerIndex1)/1000))))));
            }
            //Angriffssound abspielen
            playSound(6);
            //Angriff durchgeführt
            touchHardAttack = false;

            //Lebt der Gegner noch?
            if (!enemie.getLifeStatus()) {
                if (character.setExperience(enemie.getExperience())) {
                    levelUpPossible = true;
                }
                //Zurücksetzen des Gegners und des Charakters
                enemie = null;
                defendNecessary = false;
                if (character != null && !runmode) {
                    character.setLife();
                    character.recoverMana(1000);
                }
                enemieStatusEffect.resetStatusEffect();
            }
        }
    }

    //VERTEIDIGE DICH GEGEN DAS SCHWEIN!
    private void defend() {
        //Solange der Gegner auch existiert
        if (enemie != null) {
            //falls der Gegner gerade angreift
            if (enemie.getAttackRightNow()) {
                touchDefend = true;
                enemie.attackRightNowReset();
                Log.d("defend reset", "");
                defendNecessary = false;
            }
        }
    }

    //wurden wir / wir wurden angegriffen
    private void gotAttacked() {
        if (enemie != null && character != null) {
            //man hat eine Sekunde Zeit um auf einen Angriff zu reagieren
            if (System.currentTimeMillis() - enemie.getLastAttackTime() > 1000 && defendNecessary) {
                //Man wird tatsächlich angegriffen
                if (character.getLife() > 0) {
                    //Die ersten 10 Defense Punkte zehlen 1 zu 1 als Abwehr. Danach nur noch zur Hälfte
                    if (character.getBaseDefense() < 10) {
                        if (enemie.getDamage() >= character.getBaseDefense()) //Damit man beim Angreifen nicht heilt
                            if (!character.gotAttacked(Math.abs(character.getBaseDefense() - enemie.getDamage()))) {
                                runmodeFinished();
                            }
                    } else {
                        if (enemie.getDamage() - 10 >= ((character.getBaseDefense() - 10) / 2))
                            if (!character.gotAttacked(Math.abs(((character.getBaseDefense() - 10) / 2) - (enemie.getDamage() - 10)))) {
                                runmodeFinished();
                            }
                    }
                }
                //Da der Gegner verschwindet wenn man im RUN Mode stirbt
                if (enemie != null)
                    enemie.attackRightNowReset();
                defendNecessary = false;
                if (actionPointerDown)
                    circleInRange = true;
            }
        }
    }

    //Wenn der Zurückbutton gedrücckt wurde
    public GlobalVariables recycle() {
        loading = true;

        runmodeFinished();
        //Fightmode Infos sichern
        setSharedPreferences();
        //alles leeren
        enemie = null;
        chooseWeapon = false;

        //Sounds aufräumen
        if (backgroundloop2 != null)
            backgroundloop2.pause();
        soundPool.release();
        click1 = -1;
        hit1 = -1;
        hit2 = -1;
        hit3 = -1;
        hit4 = -1;
        hit5 = -1;
        hit6 = -1;
        hit7 = -1;
        hit8 = -1;
        hit9 = -1;
        fireballSound = -1;
        knifesharpener = -1;

        //Bitmaps releasen
        bitmapBackgroundFights.recycle();
        bitmapBackgroundFights = null;
        bitmapSpawnDiebButton.recycle();
        bitmapSpawnDiebButton = null;
        bitmapSpawnGoblinButton.recycle();
        bitmapSpawnGoblinButton = null;
        bitmapSpawnOrkButton.recycle();
        bitmapSpawnOrkButton = null;
        bitmapSpawnLevelUp.recycle();
        bitmapSpawnLevelUp = null;
        bitmapLevelUpDamage.recycle();
        bitmapLevelUpDamage = null;
        bitmapLevelUpDefense.recycle();
        bitmapLevelUpDefense = null;
        bitmapLevelUpLife.recycle();
        bitmapLevelUpLife = null;
        bitmapWaffeHackeButton.recycle();
        bitmapWaffeHackeButton = null;
        bitmapWaffeHolzschildButton.recycle();
        bitmapWaffeHolzschildButton = null;
        bitmapWaffeKnueppelButton.recycle();
        bitmapWaffeKnueppelButton = null;
        bitmapWaffeRiesenschwertButton.recycle();
        bitmapWaffeRiesenschwertButton = null;
        bitmapWaffeSchwertButton.recycle();
        bitmapWaffeSchwertButton = null;
        bitmapWaffeWechselnButton.recycle();
        bitmapWaffeWechselnButton = null;
        bitmapSpawnRiese.recycle();
        bitmapSpawnRiese = null;
        bitmapLevelUpMana.recycle();
        bitmapLevelUpMana = null;
        bitmapRunButton.recycle();
        bitmapRunButton = null;
        bitmapOnlineButton.recycle();
        bitmapOnlineButton = null;
        bitmapDownloadButton.recycle();
        bitmapDownloadButton = null;
        bitmapUploadButton.recycle();
        bitmapUploadButton = null;
        //bitmapHighscoreButton.recycle();
        //bitmapHighscoreButton = null;

        if(enemieStatusEffect != null)
            enemieStatusEffect.resetStatusEffect();
        enemieStatusEffect = null;
        if (characterStatusEffect != null)
            characterStatusEffect.resetStatusEffect();
        characterStatusEffect = null;

        return globalVariables;
    }

    //Fireball
    public void fireball() {
        if (enemie != null) {
            if (!touchAttack && !touchDefend && !touchGesture && !touchHardAttack) {
                touchGesture = true;
                enemie.defend(30);
                enemieStatusEffect.setStatusEffect("ignite");

                if (!alreadyVibrating) {
                    try {
                        Vibrator vibrator = (Vibrator) fullContext.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);
                        alreadyVibrating = true;
                    } catch (NullPointerException e) {
                        Log.d("Vibrationsfehler: ", e.toString());
                    }
                }

                playSound(7);
                if (!enemie.getLifeStatus()) {
                    if (character.setExperience(enemie.getExperience())) {
                        levelUpPossible = true;
                    }
                    enemie = null;
                    defendNecessary = false;
                    if (character != null) {
                        character.setLife();
                        character.recoverMana(1000);
                    }
                    enemieStatusEffect.resetStatusEffect();
                }
            }
        }
    }

    //Statuseffekte abgeben?
    private void statusEffectUpdate() {
        //enemie status effect
        if (enemie != null && enemieStatusEffect != null) {
            if (enemieStatusEffect.getStatusEffectNumber() == 0) { //ignite
                if ((System.currentTimeMillis() - enemieStatusEffect.getLastTick()) >= 1000) {
                    enemie.trueDamage(1);
                    enemieStatusEffect.setLastTick();
                    if ((System.currentTimeMillis() - enemieStatusEffect.getTimeGotEffect()) >= 5000) {
                        enemieStatusEffect.resetStatusEffect();
                    }
                }
            }
        }
    }

    //Haptisches Feedback abgeben?
    /*private void abilityFeedbackUpdate() {
        if (enemie != null) {
            Vibrator vibrator = (Vibrator) fullContext.getSystemService(Context.VIBRATOR_SERVICE);
            if (actionDown && System.currentTimeMillis() - touchTimer >= 1500 && !alreadyVibrating) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(100);
                    alreadyVibrating = true;
                }
            }
        }
    }*/

    //Hintergrundmusikverwaltung
    private void backgroundMusicPlayer() {
        try {
            //Beim ersten Start der Fightmusik
            if (backgroundloop2 == null && globalVariables.getMusicOn()) {
                backgroundloop2 = MediaPlayer.create(fullContext, R.raw.gameloop2);
                backgroundloop2.setLooping(true);
                backgroundloop2.start();
            }
            //Wenn wir nachträglich wieder in den Fightmodus wechseln
            if (backgroundloop2 != null && !backgroundloop2.isPlaying() && globalVariables.getMusicOn()) {
                backgroundloop2.start();
            }
        } catch (IllegalStateException e) {
            Log.d("gamemode2 Error", e.toString());
        }
    }

    //Kreisupdate
    private void circleUpdate() {
        //Wir berechnen nur wenn auch was zu berechnen da ist
        if (actionPointerDown && circleInRange) {
            //Der Radius wird kleiner
            circleRadius = getScaledBitmapSize(screenX, 1080, 200 - (int) (System.currentTimeMillis() - touchTimerIndex1)/10);
            //Aber nie noch kleiner :D
            if (circleRadius <= circleRadiusAbsoluteMin)
                circleInRange = false;
            //Farbe "berechnen"
            //paint.setColor(Color.argb(255, 31, 44, 167)); -> paint.setColor(Color.argb(255, 47, 156, 29));
            if (System.currentTimeMillis() - touchTimerIndex1 <= 1000)
                outerCircleColor = Color.argb(255, 31 + Math.round((System.currentTimeMillis() - touchTimerIndex1)/53), 44 + Math.round((System.currentTimeMillis() - touchTimerIndex1)/8), 167 - Math.round((System.currentTimeMillis() - touchTimerIndex1)/6));
            else
                outerCircleColor = Color.argb(255, 47 - Math.round((System.currentTimeMillis() - touchTimerIndex1 - 1000)/53), 156 - Math.round((System.currentTimeMillis() - touchTimerIndex1 - 1000)/8), 29 + Math.round((System.currentTimeMillis() - touchTimerIndex1 - 1000)/6));
        }
    }

    //Wenn der Endless RUN Mode beendet wird
    private void runmodeFinished() {
        if (runmode) {
            runmode = false;
            character.setLife();
            character.recoverMana(1000);
            enemie = null;
            enemieSpawnLevel = 1;

            if (highscoreRun != null) {
                if (scoreRun.getPoints() >= highscoreRun.getPoints())
                    highscoreRun = scoreRun;
            }
        }
        else
            runmode = false;
    }

    //Endlosmuodus initialisieren
    private void initialiseRunmode() {
        enemieSpawnLevel = 1;
        runmode = true;
        scoreRun = new ScoreRun();
        if (enemie == null) {
            spawnEnemie("Goblin");
            scoreRun.incrementPoints();
        }
    }
}