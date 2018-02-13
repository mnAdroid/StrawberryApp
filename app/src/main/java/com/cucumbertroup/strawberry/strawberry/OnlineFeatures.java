package com.cucumbertroup.strawberry.strawberry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cucumbertroup.strawberry.strawberry.BackupData.*;
import com.cucumbertroup.strawberry.strawberry.Model.Ranking_Run;
import com.cucumbertroup.strawberry.strawberry.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.LinkedList;

public class OnlineFeatures {
    //Singleton
    private static OnlineFeatures instance;

    //Firebasedaten
    private GlobalVariables globalVariables;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference users;
    private DatabaseReference backup;

    //Abspeichern der BackupDaten beim Herunterladen
    private boolean lBackupDataGeneralSaved;
    private boolean lBackupDataFarmSaved;
    private boolean lBackupDataFightSaved;
    private final LinkedList<BackupData_General> lBackupDataGeneral = new LinkedList<>();
    private final LinkedList<BackupData_Farm> lBackupDataFarm = new LinkedList<>();
    private final LinkedList<BackupData_Fight> lBackupDataFight = new LinkedList<>();

    //SignUp
    private MaterialEditText edtNewUser, edtNewPassword, edtNewEmail;

    //SignIn
    private MaterialEditText edtMail, edtPassword;

    //Abspeichern des eingeloggten Nutzers
    private boolean userChanged;
    private final LinkedList<String> lUserInfos = new LinkedList<>();

    private OnlineFeatures(FirebaseAuth auth, FirebaseDatabase database) {
        globalVariables = GlobalVariables.getInstance();
        this.auth = auth;
        users = database.getReference("users");
        this.database = database;
        if (auth.getUid() != null)
            backup = database.getReference("backupUserdata/" +  auth.getUid());
        else
            backup = null;

        lBackupDataFightSaved = false;
        lBackupDataFarmSaved = false;
        lBackupDataFightSaved = false;
    }
    public static synchronized OnlineFeatures getInstance(FirebaseAuth auth, FirebaseDatabase database) {
        if (OnlineFeatures.instance == null) {
            OnlineFeatures.instance = new OnlineFeatures(auth, database);
        }
        return OnlineFeatures.instance;
    }

    //Zum Einloggen
    public void showSignInDialog(final Context fullContext) {
        final AlertDialog alertDialog = new AlertDialog.Builder(fullContext).create();
        alertDialog.setTitle(R.string.sign_in);
        alertDialog.setMessage("");

        LayoutInflater inflater = LayoutInflater.from(fullContext);
        View signInLayout = inflater.inflate(R.layout.sign_in_layout, null);

        edtMail = (MaterialEditText) signInLayout.findViewById(R.id.edtMail);
        edtPassword = (MaterialEditText) signInLayout.findViewById(R.id.edtPassword);

        Button btnSignIn = (Button) signInLayout.findViewById(R.id.btn_sign_in);
        Button btnSignUp = (Button) signInLayout.findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUpDialog(fullContext, alertDialog);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(edtMail.getText().toString(), edtPassword.getText().toString(), alertDialog, fullContext);
            }
        });

        alertDialog.setView(signInLayout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.show();
    }

    //Das tatsächliche Einloggen
    private void signIn(final String mail, final String password, final AlertDialog alertDialog, final Context fullContext) {
        //Sind alle Daten eingetragen?
        if(TextUtils.isEmpty(mail)) {
            Toast.makeText(fullContext, "Bitte E-Mail Adresse eingeben!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(fullContext, "Bitte Passwort eingeben!", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(mail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(fullContext, "Login erfolgreich", Toast.LENGTH_SHORT).show();
                userChanged = true;
                getUser(fullContext);
                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(fullContext, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Zum Registrieren
    private void showSignUpDialog(final Context fullContext, final AlertDialog signInDialog) {
        //Register Layout aufbauen
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(fullContext);
        alertDialog.setTitle(R.string.sign_up);
        alertDialog.setMessage("Bitte alles ausfüllen");

        LayoutInflater inflater = LayoutInflater.from(fullContext);
        View signUpLayout = inflater.inflate(R.layout.sign_up_layout, null);

        //Editfelder anlegen
        edtNewUser = (MaterialEditText) signUpLayout.findViewById(R.id.edtNewUserName);
        edtNewPassword = (MaterialEditText) signUpLayout.findViewById(R.id.edtNewPassword);
        edtNewEmail = (MaterialEditText) signUpLayout.findViewById(R.id.edtNewEmail);

        //Um bei Eingaben im Loginscreen diese zu übernehmen
        if (!edtMail.getText().toString().equals(""))
            edtNewEmail.setText(edtMail.getText().toString());
        if (!edtPassword.getText().toString().equals(""))
            edtNewPassword.setText(edtPassword.getText().toString());

        alertDialog.setView(signUpLayout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        //Die beiden Button erstellen
        alertDialog.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton(R.string.sign_up, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Sind alle Daten eingetragen?
                if(TextUtils.isEmpty(edtNewEmail.getText().toString())) {
                    Toast.makeText(fullContext, "Bitte E-Mail Adresse eingeben!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtNewPassword.getText().toString())) {
                    Toast.makeText(fullContext, "Bitte Passwort eingeben!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtNewUser.getText().toString())) {
                    Toast.makeText(fullContext, "Bitte Nutzernamen eingeben!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edtNewPassword.getText().toString().length() < 6) {
                    Toast.makeText(fullContext, "Bitte mindestens sechs Zeichen als Passwort nutzen", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!edtNewEmail.getText().toString().contains("@")) {
                    Toast.makeText(fullContext, "Die Email beinhaltet kein @ Zeichen", Toast.LENGTH_LONG).show();
                    return;
                }
                if(edtNewUser.getText().toString().contains("@")) {
                    Toast.makeText(fullContext, "Der Nutzername darf kein @ Zeichen enthalten", Toast.LENGTH_LONG).show();
                    return;
                }

                //Registrierung des neuen Nutzers
                auth.createUserWithEmailAndPassword(edtNewEmail.getText().toString(), edtNewPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //Der Nutzer wird ohne Passwort noch zusätzlich in die Datenbank geschrieben
                                User user = new User();
                                user.setEmail(edtNewEmail.getText().toString());
                                user.setUserName(edtNewUser.getText().toString());

                                //Nutzername ist der Key
                                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                                    users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(fullContext, "Erfolgreich registriert!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(fullContext, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //Um einfach an die Nutzerdaten ranzukommen
                                SharedPreferences sharedPreferences = fullContext.getSharedPreferences("LoggedInUser", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userName", user.getUserName());
                                editor.putString("userMail", user.getEmail());
                                editor.apply();

                                //Da man auch automatisch eingeloggt ist, kann man den login screen auch schließen
                                signInDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(fullContext, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alertDialog.show();
    }

    //Um Spieldaten zu backupen (hochladen)
    public void backupGamedata(final Context fullContext) {
        //Backup Klassen initialisieren
        BackupData_General backupDataGeneral = new BackupData_General(fullContext);
        BackupData_Farm backupDataFarm = new BackupData_Farm(fullContext);
        BackupData_Fight backupDataFight = new BackupData_Fight(fullContext);
        //Alle Variablen füllen
        backupDataGeneral.readBackupData();
        backupDataFarm.readBackupData();
        backupDataFight.readBackupData();
        //backup überprüfen
        if (backup == null)
            backup = database.getReference("backupUserdata/" +  auth.getUid());
        //Online hochladen
        backup.child("backupDataGeneral").setValue(backupDataGeneral)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(fullContext, "Erfolgreich General Daten hochgeladen!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(fullContext, "Fehler beim hochladen (backupDataGeneral): " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        backup.child("backupDataFarm").setValue(backupDataFarm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(fullContext, "Erfolgreich Farm Daten hochgeladen!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(fullContext, "Fehler beim hochladen (backupDataFarm): " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        backup.child("backupDataFight").setValue(backupDataFight)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(fullContext, "Erfolgreich Fight Daten hochgeladen!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(fullContext, "Fehler beim hochladen (backupDataFight): " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getSavedGamedata(final Context fullContext) {
        //backup überprüfen
        if (backup == null)
            backup = database.getReference("backupUserdata/" + auth.getUid());

        backup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    BackupData_General backupDataGeneral = child.getValue(BackupData_General.class);
                    lBackupDataGeneral.add(backupDataGeneral);
                    BackupData_Farm backupDataFarm = child.getValue(BackupData_Farm.class);
                    lBackupDataFarm.add(backupDataFarm);
                    BackupData_Fight backupDataFight = child.getValue(BackupData_Fight.class);
                    lBackupDataFight.add(backupDataFight);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(fullContext, "Fehler beim herunterladen: " + databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Um beim Einloggen trotzdem an Username und Email zu kommen
    private void getUser(final Context fullContext) {
        users = database.getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String temp = child.getValue(String.class);
                    lUserInfos.add(temp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(fullContext, "Fehler beim Laden der Nutzerinfos: " + databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public synchronized void waitForFirebase(Context fullContext) {
        if (lBackupDataGeneral.size() == 3) {
            for (int i = 0; lBackupDataGeneral.size() > i; i++) {
                lBackupDataGeneralSaved = lBackupDataGeneral.get(i).saveBackupData(fullContext);
                if (lBackupDataGeneralSaved) {
                    lBackupDataGeneral.clear();
                }
            }
        }
        if (lBackupDataFarm.size() == 3) {
            for (int i = 0; lBackupDataFarm.size() > i; i++) {
                lBackupDataFarmSaved = lBackupDataFarm.get(i).saveBackupData(fullContext);
                if (lBackupDataFarmSaved) {
                    lBackupDataFarm.clear();
                }
            }
        }
        if (lBackupDataFight.size() == 3) {
            for (int i = 0; lBackupDataFight.size() > i; i++) {
                lBackupDataFightSaved = lBackupDataFight.get(i).saveBackupData(fullContext);
                if (lBackupDataFightSaved) {
                    lBackupDataFight.clear();
                }
            }
        }
        if (lBackupDataGeneralSaved && lBackupDataFarmSaved && lBackupDataFightSaved) {
            lBackupDataGeneralSaved = false;
            lBackupDataFarmSaved = false;
            lBackupDataFightSaved = false;
            globalVariables.setGameMode(101);
        }

        if (userChanged) {
            if (lUserInfos.size() == 2) {
                userChanged = false;
                String userName;
                String userMail;
                if (lUserInfos.get(0).contains("@")) {
                    userName = lUserInfos.get(1);
                    userMail = lUserInfos.get(0);
                }
                 else {
                    userName = lUserInfos.get(0);
                    userMail = lUserInfos.get(1);
                }
                //Um einfach an die Nutzerdaten ranzukommen
                SharedPreferences sharedPreferences = fullContext.getSharedPreferences("LoggedInUser", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", userName);
                editor.putString("userMail", userMail);
                editor.apply();

                lUserInfos.clear();
            }
        }
    }
}

