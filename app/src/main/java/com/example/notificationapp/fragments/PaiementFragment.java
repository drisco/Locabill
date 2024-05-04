package com.example.notificationapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.notificationapp.AlertPaiement;
import com.example.notificationapp.EspaceLocataires;
import com.example.notificationapp.NumberToWords;
import com.example.notificationapp.R;
import com.example.notificationapp.models.CityItem;
import com.example.notificationapp.models.MesServices;
import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.Model_ticket;
import com.example.notificationapp.models.StatutRecu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaiementFragment extends Fragment {
    DatabaseReference databaseReference,databaseReference1,databaseReference2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String idAdm,idLca,ville,numero,date ,statut,id,somme,somme1,caution,avance,debutUsage,type,nom,prenom;
    StatutRecu tenant;
    Model_ticket tickets;
    TextView pyer;
    AlertPaiement popup;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_paiement, container, false);
        pyer =view.findViewById(R.id.pyer);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("localites");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("statutdumois");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("recu");

        sharedPreferences = getActivity().getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idAdm = sharedPreferences.getString("idAdmin", "");
        idLca = sharedPreferences.getString("id", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");
        somme1 = sharedPreferences.getString("somme", "");
        nom = sharedPreferences.getString("nom", "");
        prenom = sharedPreferences.getString("prenom", "");
        caution = sharedPreferences.getString("caution", "");
        debutUsage = sharedPreferences.getString("debutUsage", "");
        avance = sharedPreferences.getString("avance", "");
        type = sharedPreferences.getString("type", "");

        popup = new AlertPaiement(getActivity());
        popup.setCancelable(false);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodeDeVerification();

            }
        });

        return  view;
    }

    private void methodeDeVerification() {
        databaseReference1.child(idAdm).child(idLca).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                        tenant = citySnapshot.getValue(StatutRecu.class);
                        if (tenant != null && tenant.getStatut() != null && tenant.getStatut().equals("impayé")) {
                            date=tenant.getDate();
                            id= tenant.getId();
                            statut= tenant.getStatut();
                            somme= tenant.getSomme();
                            Toast.makeText(getContext(), "Paiement en retard", Toast.LENGTH_SHORT).show();
                            methodePayerRetard(date,id,somme);
                        }else {
                            Toast.makeText(getContext(), "Paiement normal0", Toast.LENGTH_SHORT).show();
                            methodePourPayer();
                        }
                    }
                }else {
                    Toast.makeText(getContext(), "Paiement normal", Toast.LENGTH_SHORT).show();
                    methodePourPayer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs
            }
        });

    }

    private void methodePayerRetard(String date, String id, String somme) {
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String heureActuelle = sdf.format(heure);
        databaseReference2.child(idAdm).child(idLca);
        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme));


        Model_ticket nouveauLocataire = new Model_ticket(id, nom,prenom , somme, numero, type, debutUsage, caution,avance ,numberInWords, date,heureActuelle);
        databaseReference2.setValue(nouveauLocataire);
        databaseReference1.child(idAdm).child(idLca).child(id).child("statut").setValue("payé");
        Intent intent = new Intent(getContext(), MesServices.class);
        getContext().startService(intent);
        startActivity(new Intent(getContext(), EspaceLocataires.class));
    }

    /*private void methodePourPayer() {
        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme1));
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String heureActuelle = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String dateFormatted = sdf2.format(heure);

        DatabaseReference localiteReference = databaseReference2.child(idAdm).child(idLca).push();
        String nouvelId = localiteReference.getKey();
        Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance,numberInWords, dateFormatted,heureActuelle);
        localiteReference.setValue(nouveauLocataire);
        databaseReference.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
        Intent intent = new Intent(getContext(), MesServices.class);
        getContext().startService(intent);
        startActivity(new Intent(getContext(), EspaceLocataires.class));

        localiteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                        tickets = citySnapshot.getValue(Model_ticket.class);
                        if (tickets != null && tickets.getDate() != null && tickets.getDate().equals(dateFormatted)) {
                            popup.show();
                            popup.setMessageText("Vous avez deja reglé le paiement ce mois merci beaucoup pour votre fiabilité ");
                            popup.getRetour().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popup.dismiss();
                                }
                            });
                        }else {
                            popup.show();
                            popup.setMessageText("Votre paiement de ce mois a été éffectuer avec succes merci beaucoup pour votre fiabilité ");
                            popup.getRetour().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getContext(), "Paiement effectuer avec succes", Toast.LENGTH_SHORT).show();
                                    String nouvelId = localiteReference.push().getKey();
                                    Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance,numberInWords, dateFormatted,heureActuelle);
                                    localiteReference.setValue(nouveauLocataire);
                                    databaseReference.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                                    // Créez un objet Intent pour démarrer le service
                                    Intent intent = new Intent(getContext(), MesServices.class);
                                    getContext().startService(intent);
                                    startActivity(new Intent(getContext(), EspaceLocataires.class));
                                    popup.dismiss();
                                }
                            });

                        }
                    }
                }else {
                    popup.show();
                    popup.setMessageText("Votre paiement de ce mois a été éffectuer avec succes merci beaucoup pour votre fiabilité ");
                    popup.getRetour().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Paiement effectuer avec succes", Toast.LENGTH_SHORT).show();
                            String nouvelId = localiteReference.push().getKey();
                            Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance,numberInWords, dateFormatted,heureActuelle);
                            localiteReference.setValue(nouveauLocataire);
                            databaseReference.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                            // Créez un objet Intent pour démarrer le service
                            Intent intent = new Intent(getContext(), MesServices.class);
                            getContext().startService(intent);
                            startActivity(new Intent(getContext(), EspaceLocataires.class));
                            popup.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/
    private void methodePourPayer() {
        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(somme1));
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String heureActuelle = sdf.format(heure);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String dateFormatted = sdf2.format(heure);

        DatabaseReference localiteReference = databaseReference2.child(idAdm).child(idLca);
        localiteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean paiementEffectue = false;
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                        Model_ticket ticket = citySnapshot.getValue(Model_ticket.class);
                        if (ticket != null && ticket.getDate() != null && ticket.getDate().equals(dateFormatted)) {
                            paiementEffectue = true;
                            break; // Sortir de la boucle si le paiement a déjà été effectué ce mois-ci
                        }
                    }
                    if (paiementEffectue) {
                        popup.show();
                        popup.setMessageText("Vous avez déjà réglé le paiement ce mois-ci, merci beaucoup pour votre fiabilité");
                        popup.getRetour().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popup.dismiss();
                            }
                        });
                    } else {
                        popup.show();
                        popup.setMessageText("Votre paiement de ce mois a été effectué avec succès, merci beaucoup pour votre fiabilité");
                        popup.getRetour().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getContext(), "Paiement effectué avec succès", Toast.LENGTH_SHORT).show();
                                String nouvelId = localiteReference.push().getKey();
                                Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance, numberInWords, dateFormatted, heureActuelle);
                                localiteReference.child(nouvelId).setValue(nouveauLocataire); // Utiliser child(nouvelId) pour ajouter un nouvel élément
                                databaseReference.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                                // Créez un objet Intent pour démarrer le service
                                Intent intent = new Intent(getContext(), MesServices.class);
                                getContext().startService(intent);
                                startActivity(new Intent(getContext(), EspaceLocataires.class));
                                popup.dismiss();
                            }
                        });
                    }
                } else {
                    // Aucune donnée n'existe, ajoutez les nouvelles données
                    popup.show();
                    popup.setMessageText("Votre paiement de ce mois a été effectué avec succès, merci beaucoup pour votre fiabilité ");
                    popup.getRetour().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Paiement effectué avec succès", Toast.LENGTH_SHORT).show();
                            String nouvelId = localiteReference.push().getKey();
                            Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance, numberInWords, dateFormatted, heureActuelle);
                            localiteReference.child(nouvelId).setValue(nouveauLocataire);
                            databaseReference.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                            // Créez un objet Intent pour démarrer le service
                            Intent intent = new Intent(getContext(), MesServices.class);
                            getContext().startService(intent);
                            startActivity(new Intent(getContext(), EspaceLocataires.class));
                            popup.dismiss();
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

}
