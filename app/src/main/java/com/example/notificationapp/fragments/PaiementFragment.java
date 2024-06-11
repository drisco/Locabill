package com.example.notificationapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.notificationapp.AlertPaiement;
import com.example.notificationapp.Bricefile;
import com.example.notificationapp.EspaceLocataires;
import com.example.notificationapp.List_of_tenants;
import com.example.notificationapp.NumberToWords;
import com.example.notificationapp.PopupMoney;
import com.example.notificationapp.R;
import com.example.notificationapp.UpdateTenant;
import com.example.notificationapp.models.ApiResponse;
import com.example.notificationapp.models.ClientData;
import com.example.notificationapp.models.Model_ticket;
import com.example.notificationapp.models.StatutRecu;
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


public class PaiementFragment extends Fragment {
    DatabaseReference databaseReference,databaseReference1,databaseReference2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ClientData client;
    String tokenpayer;
    String idAdm,idLca,ville,numero,date ,statut,id,somme,somme1,caution,avance,debutUsage,type,nom,prenom;

    Model_ticket tickets;

    TextView pyer;
    AlertPaiement popup;
    PopupMoney popupMoney;
    private EspaceLocataires mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EspaceLocataires) {
            mActivity = (EspaceLocataires) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

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
         client =new ClientData(200,type,idLca,numero,prenom,"https://mon_lien_de_callback.com");

        popup = new AlertPaiement(getActivity());
        popup.setCancelable(true);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodeDeVerification();
                /*popupMoney = new PopupMoney(getActivity());
                popupMoney.setCancelable(false);
                popupMoney.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                popupMoney.show();
                popupMoney.moov().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methodeDeVerification();
                    }
                });
                popupMoney.moov().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methodeDeVerification();
                    }
                });
                popupMoney.rtour().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupMoney.dismiss();
                    }
                });
                popupMoney.wave().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methodeDeVerification();
                    }
                });
                popupMoney.mtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methodeDeVerification();
                    }
                });
                popupMoney.orange().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methodeDeVerification();
                    }
                });*/


            }
        });
        resultatstatus();
        return  view;
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
                                Toast.makeText(getContext(), "Paiement en retard", Toast.LENGTH_SHORT).show();
                                methodePayerRetard(date,id,somme);
                                break;
                            }else {
                                Toast.makeText(getContext(), "Paiement normal0", Toast.LENGTH_SHORT).show();
                                methodePourPayer();
                            }
                            break;
                        }
                        break;
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
                sendClientData(client);
                /*Model_ticket nouveauLocataire = new Model_ticket(id, nom,prenom , somme, numero, type, debutUsage, caution,avance ,numberInWords, date,heureActuelle);
                localiteReference.child(id).setValue(nouveauLocataire);
                databaseReference1.child(idAdm).child(idLca).child(id).child("statut").setValue("payé");
                popup.dismiss();
                Intent intent =new Intent(getContext(), Bricefile.class);
                intent.putExtra("id", id);
                intent.putExtra("nom", nom);
                intent.putExtra("prenom", prenom);
                intent.putExtra("prix", somme);
                intent.putExtra("numero", numero);
                intent.putExtra("localite", ville);
                intent.putExtra("type_de_maison", type);
                intent.putExtra("mois", date);
                intent.putExtra("date", heureActuelle);
                startActivity(intent);
                getActivity().finish();*/
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
                        popup.setMessageText("Vous avez déjà réglé le paiement de ce mois-ci"+" "+dateFormatted+" "+", merci beaucoup pour votre fiabilité");
                        popup.setCancelText("Retour");
                        popup.getRetour().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendClientData(client);
                                popup.dismiss();
                                //goToHistoriqueFragment(view);
                            }
                        });
                    } else {
                        popup.show();
                        popup.setMessageText("Votre paiement de ce mois a été effectué avec succès, merci beaucoup pour votre fiabilité");
                        popup.getRetour().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendClientData(client);
                                /*String nouvelId = localiteReference.push().getKey();
                                Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance, numberInWords, dateFormatted, heureActuelle);
                                localiteReference.child(nouvelId).setValue(nouveauLocataire); // Utiliser child(nouvelId) pour ajouter un nouvel élément
                                databaseReference.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                                popup.dismiss();
                                Intent intent =new Intent(getContext(), Bricefile.class);
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
                                getActivity().finish();*/
                            }
                        });
                    }
                } else {
                    // Aucune donnée n'existe, ajoutez les nouvelles données
                    popup.show();
                    popup.setMessageText("Votre paiement de ce mois a été effectué avec succès, merci beaucoup pour votre fiabilité ");
                    popup.setCancelText("Payer maintenant");
                    popup.getRetour().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Paiement effectué avec succès", Toast.LENGTH_SHORT).show();
                            sendClientData(client);
                            /*String nouvelId = localiteReference.push().getKey();
                            Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, somme1, numero, type, debutUsage, caution, avance, numberInWords, dateFormatted, heureActuelle);
                            localiteReference.child(nouvelId).setValue(nouveauLocataire);
                            databaseReference.child(idAdm).child(ville).child(idLca).child("statut").setValue("payé");
                            popup.dismiss();
                            Intent intent =new Intent(getContext(), Bricefile.class);
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
                            getActivity().finish();*/
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

    private void sendClientData(ClientData clientData) {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(clientData);
        System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G HUGUHLIEZ "+json);
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

                    tokenpayer=apiResponse.getToken();
                    System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G TOKEN TOKEN TOKEN TOKEN  "+apiResponse.getToken());


                } else {
                    // Gérer le cas où le statut n'est pas réussi
                    System.out.println("Erreur lors du paiement: " + apiResponse.getMessage());
                }
            }
        });
    }

    private void redirectUser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
    private void resultatstatus() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.pay.moneyfusion.net/paiementNotif/"+tokenpayer)
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
                }

                // Traiter la réponse ici
                String responseBody = response.body().string();
                System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G RESULTAT "+responseBody);
                System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G RESULTAT "+responseBody);
                System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G RESULTAT "+responseBody);
                System.out.println("N?FBVHFBHBFGHGJGHHG JRGHKGH HLRGHIUG HHLGULHIURG G RESULTAT "+responseBody);
            }
        });
    }
    private void goToHistoriqueFragment(View view) {
        if (mActivity != null) {
            mActivity.showHistoriqueFragment(view); // Appel de la méthode de l'activité pour afficher le fragment historique
        }
    }

}
