package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notificationapp.fragments.HistoriqueFragment;
import com.example.notificationapp.messervice.AlarmReceiver;
import com.example.notificationapp.messervice.MainBroadcastReceiver;
import com.example.notificationapp.messervice.RappelPlaning;
import com.example.notificationapp.models.ApiResponse;
import com.example.notificationapp.models.ClientData;
import com.example.notificationapp.models.Model_code_pin;
import com.example.notificationapp.models.Model_ticket;
import com.example.notificationapp.models.StatutRecu;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EspaceLocataires extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ImageView profil,deconnexion,traitPay,traitHis;
    ClientData client;
    PopupRegister popusCostum;

    DatabaseReference databaseReference01,databaseReference1,databaseReference2;
    TextView nomEtPrenom,editer;
    private Fragment historiqueFragment;
    private BottomSheetDialog bottomSheetDialog;
    EditText editTex,editTex1,mdpedit,numeroet;
    Button btnVal,btnmonney;
    int incr;
    private Fragment paiementFragment;
    SharedPreferences sharedPreferences,sharedPreferencesToken;
    AlertPaiement popup;
    SharedPreferences.Editor editor,editorToken;
    DatabaseReference databaseReference0;
    String idAdm,idLca,ville,tokenData,numero,mdp,date ,statut,id,somme ,somme1,nom,prenom,codepin,caution,avance,debutUsage,type;
    TextView nomEtenom;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_espace_locataires);

        nomEtenom =findViewById(R.id.nomEtPrenom);
        profil =findViewById(R.id.profil);
        deconnexion =findViewById(R.id.deconnexion);
        nomEtPrenom =findViewById(R.id.nomEtPrenom);
        editer =findViewById(R.id.editer);
        traitPay =findViewById(R.id.traitPay);
        traitHis =findViewById(R.id.traitHis);
        btnmonney =findViewById(R.id.btnmonney);
        historiqueFragment = new HistoriqueFragment();
        //paiementFragment = new PaiementFragment();
        showHistoriqueFragment(null);
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("statutdumois");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("recu");
        databaseReference0 = FirebaseDatabase.getInstance().getReference().child("codepin");
        databaseReference01 = FirebaseDatabase.getInstance().getReference().child("localites");
        sharedPreferences = getSharedPreferences("codeconfirm",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferencesToken = getSharedPreferences("tokenpaiement",Context.MODE_PRIVATE);
        editorToken = sharedPreferencesToken.edit();

        idAdm = sharedPreferences.getString("idAdmin", "");
        idLca = sharedPreferences.getString("id", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");
        somme1 = sharedPreferences.getString("somme", "");
        nom = sharedPreferences.getString("nom", "");
        prenom = sharedPreferences.getString("prenom", "");
        mdp = sharedPreferences.getString("mdp", "");
        codepin = sharedPreferences.getString("codepin", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");
        caution = sharedPreferences.getString("caution", "");
        debutUsage = sharedPreferences.getString("debutUsage", "");
        avance = sharedPreferences.getString("avance", "");
        type = sharedPreferences.getString("type", "");
        nomEtPrenom.setText("Bonjour "+nom+" "+prenom);

         tokenData = sharedPreferencesToken.getString("token", "");

            if (!tokenData.isEmpty()){
                popusCostum = new PopupRegister(EspaceLocataires.this);
                popusCostum.setCancelable(false);
                popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popusCostum.show();
                resultatstatus(tokenData);

            }
        client =new ClientData(200,type,idLca,numero,prenom,"redirectUrl");

        if (!idLca.isEmpty()){
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.setAction("com.example.notificationapp.models.ACTION_CUSTOM");

            Intent intent1 = new Intent(getApplicationContext(), MainBroadcastReceiver.class);
            intent1.setAction("com.example.notificationapp.models.ACTION_CUSTOM");

            RappelPlaning.scheduleChaqueUneMunite(this);
            RappelPlaning.scheduleApartirde25jusquafin(this);
            RappelPlaning.scheduleChaquelundi(this);
            
        }
        popup = new AlertPaiement(EspaceLocataires.this);
        popup.setCancelable(true);
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

        //resultatstatus();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void resultatstatus(String tokenData) {

        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme1));
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.applyPattern("dd-MM-yyyy HH:mm");
        String heureActuelle  = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        String dateFormatted = sdf2.format(heure);

        DatabaseReference localiteReference = databaseReference2.child(idAdm).child(idLca);
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.pay.moneyfusion.net/paiementNotif/"+tokenData)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Gérer l'échec de la requête
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }else{
                    // Traiter la réponse ici
                    String responseBody = response.body().string();
                    StatutRecu statutRecu = gson.fromJson(responseBody, StatutRecu.class);
                    System.out.println("AAAAAAAAAAAAAAAAAAAA AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASUCCESS VALEURRRRR  "+statutRecu.getStatut());

                    if (statutRecu.getStatut().equals("true")){
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASUCCESS TOKEN TOKEN  "+tokenData);
                        String dateR = sharedPreferencesToken.getString("dateR", "");
                        String idR = sharedPreferencesToken.getString("idR", "");
                        String statutR = sharedPreferencesToken.getString("statutR", "");
                        if (dateR.isEmpty() && idR.isEmpty() ){
                            String nouvelId = localiteReference.push().getKey();
                            Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance, numberInWords, dateFormatted, heureActuelle);
                            localiteReference.child(nouvelId).setValue(nouveauLocataire); // Utiliser child(nouvelId) pour ajouter un nouvel élément
                            databaseReference01.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                            popup.dismiss();
                            popusCostum.dismiss();
                            Intent intent =new Intent(getApplicationContext(), Bricefile.class);
                            intent.putExtra("id", idLca);
                            intent.putExtra("nom", nom);
                            intent.putExtra("prenom", prenom);
                            intent.putExtra("prix", somme1);
                            intent.putExtra("numero", numero);
                            intent.putExtra("localite", ville);
                            intent.putExtra("type_de_maison", type);
                            intent.putExtra("mois", dateFormatted);
                            intent.putExtra("date", heureActuelle);
                            startActivity(intent);
                            finish();
                        }else {
                            Model_ticket nouveauLocataire = new Model_ticket(idR, nom,prenom , somme, numero, type, debutUsage, caution,avance ,numberInWords, dateR,heureActuelle);
                            localiteReference.child(id).setValue(nouveauLocataire);
                            databaseReference1.child(idAdm).child(idLca).child(idR).child("statut").setValue("payé");
                            popup.dismiss();
                            Intent intent =new Intent(getApplicationContext(), Bricefile.class);
                            intent.putExtra("id", idR);
                            intent.putExtra("nom", nom);
                            intent.putExtra("prenom", prenom);
                            intent.putExtra("prix", somme);
                            intent.putExtra("numero", numero);
                            intent.putExtra("localite", ville);
                            intent.putExtra("type_de_maison", type);
                            intent.putExtra("mois", dateR);
                            intent.putExtra("date", heureActuelle);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        editorToken.clear();
                        editorToken.apply();
                    }
                }

            }
        });
    }

    private void editLocaMethode(String nom, String prenom, String mdp,String numero) {
        DatabaseReference baselocal =databaseReference01.child(idAdm).child(ville).child(idLca);
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
    @SuppressLint("MissingInflatedId")
    public void showPaiementFragment(View view) {
        traitHis.setVisibility(View.VISIBLE);
        //traitPay.setVisibility(View.VISIBLE);
        bottomSheetDialog = new BottomSheetDialog(EspaceLocataires.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.editpopupayer, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        btnmonney = bottomSheetView.findViewById(R.id.btnmonney);
        bottomSheetDialog.show();
        btnmonney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                methodeDeVerification();
            }
        });

    }

    private void methodeDeVerification() {
        //popupMoney.dismiss();
        databaseReference1.child(idAdm).child(idLca).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {

                        for (DataSnapshot autherSnap :citySnapshot.getChildren()){
                            StatutRecu tenant = autherSnap.getValue(StatutRecu.class);
                            if (tenant != null && tenant.getStatut() != null && tenant.getStatut().equals("impayé")) {
                                date=tenant.getDate();
                                id= tenant.getId();
                                statut= tenant.getStatut();
                                somme= tenant.getSomme();
                                editorToken.putString("dateR", date);
                                editorToken.putString("idR", id);
                                editorToken.putString("sommeR", somme);
                                editorToken.putString("statutR", statut);
                                editorToken.apply();
                                methodePayerRetard(date,id,somme);
                                break;
                            }else {
                                methodePourPayer();
                            }
                            break;
                        }
                        break;
                    }
                }else {
                    methodePourPayer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs
            }
        });

    }

    private void methodePourPayer() {
        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme1));
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.applyPattern("dd-MM-yyyy HH:mm");
        String heureActuelle  = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        String dateFormatted = sdf2.format(heure);

        DatabaseReference localiteReference = databaseReference2.child(idAdm).child(idLca);
        localiteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Model_ticket> allMessages = new ArrayList<>();
                if (snapshot.exists()) {
                    boolean paiementEffectue = false;
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                        Model_ticket ticket = citySnapshot.getValue(Model_ticket.class);
                        if (ticket != null && ticket.getDate() != null && ticket.getDate().equals(dateFormatted)) {
                            paiementEffectue = true;
                        }
                    }
                    if (paiementEffectue) {
                        popup.show();
                        popup.setMessageText("Vous avez déjà réglé le paiement de "+" "+dateFormatted+" "+", merci beaucoup pour votre fiabilité");
                        popup.setCancelText("Retour");
                        popup.getRetour().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //sendClientData(client);
                                bottomSheetDialog.dismiss();
                                popup.dismiss();
                                //goToHistoriqueFragment(view);
                            }
                        });
                    } else {
                        popup.show();
                        popup.setMessageText("Voulez-vous vraiment effectuer le paiement ? si oui appuyer sur le boutton");
                        popup.setCancelText("Payer maintenant");
                        popup.getRetour().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetDialog.dismiss();
                                sendClientData(client);

                            }
                        });
                    }
                } else {
                    // Aucune donnée n'existe, ajoutez les nouvelles données
                    popup.show();
                    popup.setMessageText("Voulez-vous vraiment effectuer le paiement ? si oui appuyer sur le boutton ");
                    popup.setCancelText("Payer maintenant");
                    popup.getRetour().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                            sendClientData(client);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer les erreurs
            }
        });
    }
    private void methodePayerRetard(String date, String id, String somme) {
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String heureActuelle = sdf.format(heure);
        DatabaseReference localiteReference = databaseReference2.child(idAdm).child(idLca);
        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme));
        popup.show();
        popup.setTitreText(" Loyer en retard");
        popup.setTitreColor(R.color.orange);
        popup.setRetard(" Mois non reglé : "+date);
        popup.setMessageText("Cher locataire, il semble que vous ayez un mois de loyer en retard. Merci de régulariser cette situation au plus vite pour éviter toute complication future. ");
        popup.setCancelText("Payer le mois");
        popup.setCancelBackground(R.drawable.bg_circle_green);
        popup.setCancelTextColor(R.color.white);
        popup.getRetour().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                sendClientData(client);

            }
        });


    }


    private void sendClientData(ClientData clientData) {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(clientData);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url("https://www.pay.moneyfusion.net/locabill/596ff227835b9233/pay/")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Gérer l'échec de la requête
                System.out.println("Erreur lors de l'envoi des données du client: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // Traiter la réponse ici
                String responseBody = response.body().string();
                ApiResponse apiResponse = gson.fromJson(responseBody, ApiResponse.class);

                if (apiResponse.isStatut()) {
                    // Le paiement est en cours, utilisez l'URL pour rediriger l'utilisateur vers le moyen de paiement
                    redirectUser(apiResponse.getUrl());

                    //editorToken.clear();
                    editorToken.putString("token", apiResponse.getToken());
                    editorToken.apply();
                    System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G TOKEN TOKEN TOKEN TOKEN  "+apiResponse.getToken());
                } else {
                    // Gérer le cas où le statut n'est pas réussi
                    System.out.println("Erreur lors du paiement: " + apiResponse.getMessage());
                }
            }
        });
    }

    private void redirectUser(String url) {

        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
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
        DatabaseReference localiteReference = databaseReference0.child(idLca);
        localiteReference.child("code").setValue(code);
        editor.putString("codepin", codepin);
        editor.apply();
        bottomSheetDialog.dismiss();
    }

    private void addCode(String codepin) {
        bottomSheetDialog.dismiss();
        DatabaseReference localiteReference = databaseReference0.child(idLca).push();
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