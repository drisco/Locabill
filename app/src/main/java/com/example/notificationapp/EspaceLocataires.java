package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notificationapp.fragments.HistoriqueFragment;
import com.example.notificationapp.messervice.ServiceLocataire;
import com.example.notificationapp.models.ApiResponse;
import com.example.notificationapp.models.ClientData;
import com.example.notificationapp.models.Message;
import com.example.notificationapp.models.ModelContract;
import com.example.notificationapp.models.Model_code_pin;
import com.example.notificationapp.models.Model_ticket;
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
import java.util.Calendar;
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
    Message tenant1;
    PopupRegister popusCostum;

    DatabaseReference databaseReference01,databaseReference0,databaseReference1,dtabaseMessage,databaseReference2;
    //databaseReference0
    TextView nomEtPrenom,editer;
    DatabaseReference databaseRef;
    private Fragment historiqueFragment;
    private boolean revenuDepuisPaiement = false;
    String dateFormatted;

    private BottomSheetDialog bottomSheetDialog;
    EditText editTex,editTex1,mdpedit,numeroet;
    Button btnVal,btnmonney;
    int incr;
    private Fragment paiementFragment;
    SharedPreferences sharedPreferences,sharedPreferencesToken;
    AlertPaiement popup;
    SharedPreferences.Editor editor,editorToken;
    String idAdm,idLca,ville,tokenData,numero,mdp,date ,statut,id,somme ,somme1,nom,prenom,codepin,codepinId,caution,avance,debutUsage,type;
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
        dtabaseMessage = FirebaseDatabase.getInstance().getReference().child("message");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("recu");
        databaseReference0 = FirebaseDatabase.getInstance().getReference().child("codepin");
        databaseReference01 = FirebaseDatabase.getInstance().getReference().child("localites");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("cheikpaiement");
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
        codepinId = sharedPreferences.getString("codepinId", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");
        caution = sharedPreferences.getString("caution", "");
        debutUsage = sharedPreferences.getString("debutUsage", "");
        avance = sharedPreferences.getString("avance", "");
        type = sharedPreferences.getString("type", "");
        nomEtPrenom.setText(nom+" "+prenom+"\n "+numero);



        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.applyPattern("dd-MM-yyyy HH:mm");
        String heureActuelle  = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        String dateFormatted = sdf2.format(heure);

        // METHODE DE LANCEMENT DE CONTRAT DE BAIL
        checkIfContractExists(idAdm,idLca);
         tokenData = sharedPreferencesToken.getString("token", "");

            if (!tokenData.isEmpty()){
                popusCostum = new PopupRegister(EspaceLocataires.this);
                popusCostum.setCancelable(false);
                popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //popusCostum.show();
                resultatstatus(tokenData);
            }
            // INFOS DE LA REQUETTE
        String redirectUrl = "https://locabill-855d7.firebaseapp.com";
        client =new ClientData(200,type,idLca,numero,prenom,redirectUrl);

        if (!idLca.isEmpty()){

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ServiceLocataire.class));
            } else {
                startService(new Intent(this, ServiceLocataire.class));
            }

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

        // FONCTION POUR MODIFIER LES DONNEES DE LOCATAIRE
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

        nomEtenom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://locabill-855d7.firebaseapp.com/";
                databaseReference1.child(idAdm).child(ville).child(idLca).child("verifie").setValue("vrai");

            }
        });

        //resultatstatus();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    // METHODE POUR VERIFIER LE CONTRAT DE BAIL SIL EXISTE
    private void checkIfContractExists(String userId, String idLca) {
         databaseRef = FirebaseDatabase.getInstance().getReference("contrats");
        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    // Le contrat existe
                    ModelContract contract = dataSnapshot.getValue(ModelContract.class);
                    if (contract != null && contract.getEstSigne()) {
                        // Afficher le BottomSheet si le contrat existe
                        showBottomSheet(contract);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Erreur lors de la vérification du contrat", databaseError.toException());
            }
        });
    }


    // LA VUE DE SHEETBOTTOM POUR AFFICHER LE CONTRAT DE BAILL
    private void showBottomSheet(ModelContract contract) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(false);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_contract, null);

        Button viewContractButton = bottomSheetView.findViewById(R.id.viewContractButton);
        databaseRef = FirebaseDatabase.getInstance().getReference("contrats").child(idLca);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le contrat existe
                    ModelContract contraL = dataSnapshot.getValue(ModelContract.class);
                    if ( contraL.getEstSigne()) {
                        bottomSheetDialog.dismiss();
                    }else {
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();
                        // Lorsqu'on appuie sur le bouton, aller à une autre activité pour voir le contrat
                        viewContractButton.setOnClickListener(v -> {
                            Intent intent = new Intent(EspaceLocataires.this, ViewContractActivity.class);
                            intent.putExtra("contrat", contract.getContrat());
                            intent.putExtra("signatureUrl", contract.getSignatureUrl());
                            startActivity(intent);
                            finish();
                            bottomSheetDialog.dismiss();
                        });
                    }
                } else {
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                    // Lorsqu'on appuie sur le bouton, aller à une autre activité pour voir le contrat
                    viewContractButton.setOnClickListener(v -> {
                        Intent intent = new Intent(EspaceLocataires.this, ViewContractActivity.class);
                        intent.putExtra("contrat", contract.getContrat());
                        intent.putExtra("signatureUrl", contract.getSignatureUrl());
                        startActivity(intent);
                        finish();
                        bottomSheetDialog.dismiss();
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Erreur lors de la vérification du contrat", databaseError.toException());
            }
        });

    }



    // METHODE POUR LE RESULTAT DU PAIEMENT
    private void resultatstatus(String tokenData) {

        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme1));
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.applyPattern("dd-MM-yyyy HH:mm");
        String heureActuelle  = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
         dateFormatted = sdf2.format(heure);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
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

                    PaiementDetails statutRecu = gson.fromJson(responseBody, PaiementDetails.class);
                    if (statutRecu.getData() !=null && !statutRecu.getMessage().equals("paiement introuvable !!!!")){

                        if (statutRecu.getData().getStatut().equals("paid")){
                            String dateR = sharedPreferencesToken.getString("dateR", "");
                            String idR = sharedPreferencesToken.getString("idR", "");
                            String statutR = sharedPreferencesToken.getString("statutR", "");
                            if (!dateR.isEmpty() && !idR.isEmpty() ){
                                String nouvelId = localiteReference.push().getKey();
                                Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance, numberInWords, dateR, heureActuelle);
                                localiteReference.child(nouvelId).setValue(nouveauLocataire); // Utiliser child(nouvelId) pour ajouter un nouvel élément
                                databaseReference1.child(idAdm).child(ville).child(idLca).child("verifie").setValue("vrai");
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
                                intent.putExtra("mois", dateR);
                                intent.putExtra("date", heureActuelle);
                                startActivity(intent);
                                finish();
                            }else {
                                String nouvelId = localiteReference.push().getKey();
                                System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"+nouvelId);
                                System.out.println("VVVVVVVVVVVVVVVVVVVV  VVVVVVVVVVVVVVV  VVVVVVVVVV"+dateFormatted);
                                System.out.println("VVVVVVVVVVVVV  VVVVVVVVVVVVVVVVV  VVVVVVVVVVVVVVV"+dateFormatted);
                                popusCostum.dismiss();
                                Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom,prenom , somme1, numero, type, debutUsage, caution,avance ,numberInWords, dateFormatted,heureActuelle);
                                localiteReference.child(nouvelId).setValue(nouveauLocataire);
                                databaseReference01.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                                databaseReference1.child(idAdm).child(ville).child(idLca).child("verifie").setValue("vrai");
                                popup.dismiss();
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
                            }
                        }else {
                            popusCostum.dismiss();
                            System.out.println("STATUT EST FALSE FLASEJKEJJFJKJEJ FALSE FALSE FALSE FALSE "+statutRecu);
                            editorToken.clear();
                            editorToken.apply();
                        }
                    }else {
                        popusCostum.dismiss();
                        System.out.println("STATUT EST FALSE FLASEJKEJJFJKJEJ FALSE FALSE FALSE FALSE "+statutRecu);
                        editorToken.clear();
                        editorToken.apply();
                    }


                }

            }
        });
    }
    private void reslt(){
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.applyPattern("dd-MM-yyyy HH:mm");
        String heureActuelle  = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        dateFormatted = sdf2.format(heure);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+dateFormatted);
        DatabaseReference localiteReference = databaseReference2.child(idAdm).child(idLca);
        String nouvelId = localiteReference.push().getKey();
        System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"+nouvelId);
        Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom,prenom , somme1, numero, type, debutUsage, caution,avance ,"numberInWords", dateFormatted,heureActuelle);
        localiteReference.child(nouvelId).setValue(nouveauLocataire);
        databaseReference01.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
        databaseReference1.child(idAdm).child(ville).child(idLca).child("verifie").setValue("vrai");
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

    //METHODE DE VERIFICATION PAIEMENT EN RETARD OU PAIEMENT NORMALE
    private void methodeDeVerification() {
        //popupMoney.dismiss();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // Récupérer le mois précédent
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        String previousMonth = dateFormat.format(calendar.getTime());

// Pour obtenir la date actuelle du mois
        calendar = Calendar.getInstance();
        String currentMonth = dateFormat.format(calendar.getTime()); // Mois actuel, formaté

        databaseReference2.child(idAdm).child(idLca).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean previousMonthExists = false;
                boolean isCurrentMonthPaid = false;
                boolean isNewUser = true; // Flag pour vérifier si c'est un nouvel utilisateur

                if (snapshot.exists()) {
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                        Model_ticket tenant = citySnapshot.getValue(Model_ticket.class);

                        if (tenant != null && tenant.getDate() != null) {
                            // Si le paiement a été effectué pour le mois précédent
                            if (tenant.getDate().equals(previousMonth)) {
                                previousMonthExists = true;
                                isNewUser = false; // Ce n'est plus un nouvel utilisateur si un paiement pour le mois précédent existe
                                break;
                            }

                            // Si le paiement a été effectué pour le mois actuel
                            if (tenant.getDate().equals(currentMonth)) {
                                isCurrentMonthPaid = true;
                                break;
                            }
                        }
                    }

                    // Cas où l'utilisateur est un nouveau, donc il n'a jamais payé
                    if (isNewUser) {
                        // Si c'est le premier paiement, on lui demande de payer pour le mois actuel
                        System.out.println("C'est un nouvel utilisateur, il doit payer pour ce mois.");
                        methodePourPayer(); // Demande de paiement pour le mois en cours
                    }
                    // Si aucun paiement n'a été effectué pour le mois précédent, c'est un retard
                    else if (!previousMonthExists && !isCurrentMonthPaid) {
                        System.out.println("L'utilisateur a un mois de retard. Il doit payer pour le mois précédent.");
                        methodePayerRetard(previousMonth); // Demande de paiement pour le mois précédent
                    }
                    // Si le mois précédent est payé, mais le mois actuel ne l'est pas encore
                    else if (!isCurrentMonthPaid && previousMonthExists) {
                        System.out.println("Le mois précédent est payé, l'utilisateur doit maintenant payer pour ce mois.");
                        methodePourPayer(); // Demande de paiement pour le mois en cours
                    }
                } else {
                    // Si aucune donnée n'existe dans la snapshot, l'utilisateur n'a jamais payé
                    System.out.println("L'utilisateur n'a jamais payé, il doit payer pour le mois actuel.");
                    methodePourPayer(); // Demande de paiement pour le mois actuel
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs
            }
        });


    }

    //MEHODE DE PAIEMENT EN COURS
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

    // PAIEMENT EN RETARD METHODE
    private void methodePayerRetard(String date) {
        /*Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String heureActuelle = sdf.format(heure);
        DatabaseReference localiteReference = databaseReference2.child(idAdm).child(idLca);
        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme));*/
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
                popup.dismiss();

                bottomSheetDialog.dismiss();
                sendClientData(client);

            }
        });


    }

// SEND REQUEST BUILLING METHOD
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
                    System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G TOKEN TOKEN TOKEN TOKEN AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA "+apiResponse.getUrl());
                    redirectUser(apiResponse.getUrl());

                    //editorToken.clear();
                    editorToken.putString("token", apiResponse.getToken());
                    editorToken.apply();
                } else {
                    // Gérer le cas où le statut n'est pas réussi
                    System.out.println("Erreur lors du paiement: " + apiResponse.getMessage());
                }
            }
        });
    }

    // REDIRECT ON WEBSITE METHOD
    private void redirectUser(String url) {

        /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

// Démarrez l'activité de navigateur pour ouvrir l'URL
        startActivity(browserIntent);*/
        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
    }

    //CLICK ITEMS METHOD
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
                    startActivity(new Intent(EspaceLocataires.this,LoginAdmin.class));
                    finish();
                }
            });
        } else if (item.getItemId()==R.id.probleme) {
            startActivity(new Intent(EspaceLocataires.this, AnnonceProbleme.class));
            finish();

        } else if (item.getItemId()==R.id.contrat) {
            Intent intent =new Intent(EspaceLocataires.this, VoirContrat.class);
            intent.putExtra("idAdm", idAdm);
            intent.putExtra("idLca", idLca);
            intent.putExtra("veri", "veriLoca");
            startActivity(intent);
            finish();

        }else if (item.getItemId()==R.id.codep){
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
//UPDATE CODEPIN METHOD
    private void addCodeUpd(String code) {
        DatabaseReference localiteReference = databaseReference0.child(idLca).child(codepinId);
        localiteReference.child("code").setValue(code);
        editor.putString("codepin", code);
        editor.apply();
        bottomSheetDialog.dismiss();
    }
//ADD CODEPIN METHOD
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

    /*@Override
    public void onBackPressed() {
        // Empêcher de revenir à l'activité précédente
        super.onBackPressed();
        // Tu peux aussi finir l'activité actuelle pour une gestion plus propre
        finish();
    }*/
    @Override
    public void onBackPressed() {
        incr++;
        if (incr == 1) {
            // Fermer l'application en utilisant finishAffinity()
            finishAffinity();
            super.onBackPressed();
        } else {
            // Afficher un message ou effectuer une autre action si l'utilisateur appuie une première fois
            Toast.makeText(this, "Appuyez à nouveau pour quitter l'application", Toast.LENGTH_SHORT).show();
        }
    }
}