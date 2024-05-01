package com.example.notificationapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.notificationapp.Bricefile;
import com.example.notificationapp.EspaceLocataires;
import com.example.notificationapp.LoginAdmin;
import com.example.notificationapp.PopupRegister;
import com.example.notificationapp.R;
import com.example.notificationapp.models.Model_tenant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class RegistLocataireFrament  extends Fragment {
    private TextInputEditText confCode,phone;
    TextInputLayout confCodesv;
    private final String channelId = "countdown_notification_channel";
    private final int notificationId = 1;
    TextView dBtnRegister;
    String idAdmin, id,ville,nometprenom;
    PopupRegister popup;
    private final int PERMISSION_REQUEST_CODE = 100;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference reference;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_locataire, container, false);
        confCode =view.findViewById(R.id.confic);
        confCodesv =view.findViewById(R.id.confCode);
        phone =view.findViewById(R.id.phonep);
        dBtnRegister =view.findViewById(R.id.dBtnRegister);
        createNotificationChannel();
        reference = FirebaseDatabase.getInstance().getReference().child("localites");
        sharedPreferences = getActivity().getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
        dBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dBtnRegister.getText().toString().equals("Comfirmer")){

                    dBtnRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popup = new PopupRegister(getActivity());
                            popup.setCancelable(true);
                            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popup.show();
                            System.out.println("JFJKHGKFGKJHGF JGIRUGIHI JIRGIFHUGHRIHTG JHERIOHHEHI  "+"123");

                            confCodesv.setVisibility(View.VISIBLE);
                            SharedPreferences donnes = getContext().getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
                            String code = donnes.getString("codeconfirm", "");
                            if (!confCode.getText().toString().isEmpty()){
                                String ccode=confCode.getText().toString();
                                System.out.println("CODE "+code);
                                System.out.println("JFJKHGKFGKJHGF JGIRUGIHI JIRGIFHUGHRIHTG JHERIOHHEHI  "+ccode);
                                if (ccode.equals(code)){
                                    popup.dismiss();
                                    editor.clear();
                                    editor.apply();

                                    editor.putString("idAdmin",idAdmin );
                                    editor.putString("id",id );
                                    editor.putString("ville", ville);
                                    editor.putString("nometprenom", nometprenom);
                                    editor.apply();
                                    startActivity(new Intent(getContext(), EspaceLocataires.class));

                                }
                            }else {
                                popup.dismiss();
                                Toast.makeText(getContext(), "Veuillez saisir le code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    popup = new PopupRegister(getActivity());
                    popup.setCancelable(true);
                    popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popup.show();
                    if (!phone.getText().toString().isEmpty()){
                        requetteDeVerification(phone.getText().toString());
                    }else{
                        popup.dismiss();
                        Toast.makeText(getContext(), "Veuillez saisir votre numero de télephone", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'utilisateur accorde la permission, envoyer le SMS

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    sendNotification(generateRandomNumber());
                }
            } else {
                // Si l'utilisateur refuse la permission, afficher un message
                Toast.makeText(getContext(), "Permission refusée pour envoyer des SMS.", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private void requetteDeVerification(String number) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Pour chaque enfant de "idNoeud3"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Récupérer les données de l'utilisateur
                    for (DataSnapshot noeud2 :snapshot.getChildren()){
                        for (DataSnapshot noeud3 :noeud2.getChildren()){
                            Model_tenant utilisateur = noeud3.getValue(Model_tenant.class);
                            System.out.println("Numero: " + utilisateur.getNumero());
                            // Faire ce que vous voulez avec les données de l'utilisateur
                            if (utilisateur != null) {
                                popup.dismiss();
                                if (utilisateur.getNumero().equals(number)){
                                    idAdmin=utilisateur.getIdProprie();
                                    id=utilisateur.getId();
                                    ville=utilisateur.getLocalite();
                                    nometprenom=utilisateur.getNom()+utilisateur.getPrenom();
                                    dBtnRegister.setText("Comfirmer");
                                    confCodesv.setVisibility(View.VISIBLE);
                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);

                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            sendNotification(generateRandomNumber());
                                        }
                                    }
                                }
                            }else{
                                popup.dismiss();
                                Toast.makeText(getContext(), "Aucun Compte trouvé avec ce numero", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gestion des erreurs
                popup.dismiss();
                Log.e("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nom_notification";
            String description = "decription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static String generateRandomNumber() {
        // Créer une instance de la classe Random
        Random random = new Random();

        // Générer un nombre aléatoire de 6 chiffres
        int randomNumber = random.nextInt(900000) + 100000; // Entre 100000 et 999999 inclus

        // Convertir le nombre aléatoire en chaîne de caractères
        String randomString = Integer.toString(randomNumber);

        return randomString;
    }
    private void sendNotification(String codepin) {
        editor.putString("codeconfirm", codepin);
        editor.apply();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.drawable.locabill)
                .setContentTitle("Code de confirmation")
                .setContentText(codepin)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }
}
