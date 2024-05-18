package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notificationapp.fragments.HistoriqueFragment;
import com.example.notificationapp.fragments.PaiementFragment;
import com.example.notificationapp.messervice.AlarmReceiver;
import com.example.notificationapp.messervice.MainBroadcastReceiver;
import com.example.notificationapp.messervice.RappelPlaning;
import com.example.notificationapp.models.Model_code_pin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EspaceLocataires extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ImageView profil,deconnexion,traitPay,traitHis;
    DatabaseReference databaseReference1;
    TextView nomEtPrenom,editer;
    private Fragment historiqueFragment;
    private BottomSheetDialog bottomSheetDialog;
    EditText editTex,editTex1,mdpedit,numeroet;
    Button btnVal;
    int incr;
    private Fragment paiementFragment;
    SharedPreferences sharedPreferences;
    AlertPaiement popup;
    SharedPreferences.Editor editor;
    DatabaseReference databaseReference;
    String idAdm,idLca,ville,numero,mdp ,somme1,nom,prenom,codepin;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_espace_locataires);

        profil =findViewById(R.id.profil);
        deconnexion =findViewById(R.id.deconnexion);
        nomEtPrenom =findViewById(R.id.nomEtPrenom);
        editer =findViewById(R.id.editer);
        traitPay =findViewById(R.id.traitPay);
        traitHis =findViewById(R.id.traitHis);
        historiqueFragment = new HistoriqueFragment();
        paiementFragment = new PaiementFragment();
        showHistoriqueFragment(null);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("codepin");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("localites");
        sharedPreferences = getSharedPreferences("codeconfirm",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        idAdm = sharedPreferences.getString("idAdmin", "");
        idLca = sharedPreferences.getString("id", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");
        somme1 = sharedPreferences.getString("somme", "");
        nom = sharedPreferences.getString("nom", "");
        prenom = sharedPreferences.getString("prenom", "");
        mdp = sharedPreferences.getString("mdp", "");
        codepin = sharedPreferences.getString("codepin", "");
        nomEtPrenom.setText(nom+" "+prenom);
        if (!idLca.isEmpty()){
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.setAction("com.example.notificationapp.models.ACTION_CUSTOM");

            Intent intent1 = new Intent(getApplicationContext(), MainBroadcastReceiver.class);
            intent1.setAction("com.example.notificationapp.models.ACTION_CUSTOM");

            RappelPlaning.scheduleChaqueUneMunite(this);
            RappelPlaning.scheduleApartirde25jusquafin(this);
            RappelPlaning.scheduleChaquelundi(this);
            RappelPlaning.scheduleTest(this);
        }
        popup = new AlertPaiement(EspaceLocataires.this);
        popup.setCancelable(false);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view, Gravity.END, 0, R.style.PopupMenuStyle);
                popupMenu.inflate(R.menu.menuloca);
                popupMenu.setOnMenuItemClickListener(EspaceLocataires.this);
                popupMenu.show();

            }
        });
        editer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CutPasteId")
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(EspaceLocataires.this);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.editpopup, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                editTex = bottomSheetView.findViewById(R.id.editNom);
                editTex.setText(nom);
                editTex1 = bottomSheetView.findViewById(R.id.editPrenom);
                editTex1.setText(prenom);
                mdpedit = bottomSheetView.findViewById(R.id.mdp);
                mdpedit.setText(mdp);
                numeroet = bottomSheetView.findViewById(R.id.numeroet);
                numeroet.setText(numero);
                btnVal = bottomSheetView.findViewById(R.id.btnValet);
                bottomSheetDialog.show();
                btnVal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!editTex.getText().toString().isEmpty() || !editTex1.getText().toString().isEmpty() || !mdpedit.getText().toString().isEmpty() || !numeroet.getText().toString().isEmpty()){
                            editLocaMethode(editTex.getText().toString(),editTex1.getText().toString(),mdpedit.getText().toString(),numeroet.getText().toString());
                            bottomSheetDialog.cancel();
                        }else{
                            Toast.makeText(EspaceLocataires.this, "Remplisser tout les champs", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void editLocaMethode(String nom, String prenom, String mdp,String numero) {
        DatabaseReference baselocal =databaseReference1.child(idAdm).child(ville).child(idLca);
        baselocal.child("nom").setValue(nom);
        baselocal.child("prenom").setValue(prenom);
        baselocal.child("pasword").setValue(mdp);
        baselocal.child("numero").setValue(numero);
    }

    // Méthode pour afficher le fragment Historique
    public void showHistoriqueFragment(View view) {
        traitPay.setVisibility(View.GONE);
        traitHis.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, historiqueFragment);
        transaction.commit();
    }

    // Méthode pour afficher le fragment Paiement
    public void showPaiementFragment(View view) {
        traitHis.setVisibility(View.GONE);
        traitPay.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, paiementFragment);
        transaction.commit();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.deco){
            popup = new AlertPaiement(EspaceLocataires.this);
            popup.setCancelable(true);
            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popup.show();
            popup.setTitreText("Avertissement");
            popup.setMessageText("Voullez-vous vraiment vous deconnecter ? si vous oui cliquer sur le bouton  se deconnecter");
            popup.setCancelText("SE DECONNECTER");
            popup.setCancelBackground(R.drawable.bg_circle_red);
            popup.setCancelTextColor(R.color.white);
            popup.getRetour().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.clear();
                    editor.apply();
                    popup.dismiss();
                    startActivity(new Intent(EspaceLocataires.this,RegisterAdmin.class));
                    finish();
                }
            });
        } else if (item.getItemId()==R.id.probleme) {
            startActivity(new Intent(EspaceLocataires.this, AnnonceProbleme.class));
            finish();

        } else if (item.getItemId()==R.id.codep){
            bottomSheetDialog = new BottomSheetDialog(EspaceLocataires.this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.codepopup, null);
            bottomSheetDialog.setContentView(bottomSheetView);
            if (codepin.isEmpty()){
                editTex = bottomSheetView.findViewById(R.id.editTex);
                editTex1 = bottomSheetView.findViewById(R.id.editTex1);
                btnVal = bottomSheetView.findViewById(R.id.btnVal);
                btnVal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!editTex.getText().toString().isEmpty() || !editTex1.getText().toString().isEmpty()){
                            if (!editTex.getText().toString().equals(editTex1.getText().toString())){
                                Toast.makeText(EspaceLocataires.this, "Verifier le code pin", Toast.LENGTH_SHORT).show();

                            }else{
                                addCode(editTex.getText().toString());
                            }
                        }else{
                            Toast.makeText(EspaceLocataires.this, "Remplisser tout les champs", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bottomSheetDialog.show();
            }else{
                popup = new AlertPaiement(EspaceLocataires.this);
                popup.setCancelable(true);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();
                popup.setTitreText("Avertissement");
                popup.setMessageText("Voullez-vous vraiment changer votre code pin?");
                popup.setCancelText("OUI");
                popup.setCancelBackground(R.drawable.bg_circle_red);
                popup.setCancelTextColor(R.color.white);
                popup.show();
                popup.getRetour().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popup.dismiss();
                        editTex = bottomSheetView.findViewById(R.id.editTex);
                        editTex.setText(codepin);
                        editTex1 = bottomSheetView.findViewById(R.id.editTex1);
                        editTex1.setText(codepin);
                        btnVal = bottomSheetView.findViewById(R.id.btnVal);
                        btnVal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!editTex.getText().toString().isEmpty() || !editTex1.getText().toString().isEmpty()){
                                    if (!editTex.getText().toString().equals(editTex1.getText().toString())){
                                        Toast.makeText(EspaceLocataires.this, "Verifier le code pin", Toast.LENGTH_SHORT).show();

                                    }else{
                                        addCodeUpd(editTex.getText().toString());
                                    }
                                }else{
                                    Toast.makeText(EspaceLocataires.this, "Remplisser tout les champs", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        bottomSheetDialog.show();
                    }
                });

            }
        }
        return true;
    }

    private void addCodeUpd(String code) {
        DatabaseReference localiteReference = databaseReference.child(idLca);
        localiteReference.child("code").setValue(code);
        editor.putString("codepin", codepin);
        editor.apply();
        bottomSheetDialog.dismiss();
    }

    private void addCode(String codepin) {
        bottomSheetDialog.dismiss();
        DatabaseReference localiteReference = databaseReference.child(idLca).push();
        editor.putString("codepin", codepin);
        editor.apply();
        String nouvelId = localiteReference.getKey();
        Model_code_pin codePn = new Model_code_pin(nouvelId,codepin);
        localiteReference.setValue(codePn);
        startActivity(new Intent(EspaceLocataires.this, Login.class));
        finish();
    }
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            //finish();
            finishAffinity();
        }
    }
}