package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.notificationapp.models.ModelContract;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ViewContractActivity extends AppCompatActivity {

    ImageView signPro,snloca;
    TextView contratT;
    Button add,valide;
    SignatureView signatureView;
    private Bitmap signatureBitmap;
    SharedPreferences sharedPreferences;
    int incr;
    String idAdm,idLca;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_contract);

        signPro= findViewById(R.id.signPro);
        snloca= findViewById(R.id.snloca);
        contratT= findViewById(R.id.contrat);
        add= findViewById(R.id.add);
        valide= findViewById(R.id.valide);

        sharedPreferences = getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);

        idAdm = sharedPreferences.getString("idAdmin", "");
        idLca = sharedPreferences.getString("id", "");

        String contrat = getIntent().getStringExtra("contrat");
        String signatureUrl = getIntent().getStringExtra("signatureUrl");

        contratT.setText(contrat);
        Glide.with(this).load(signatureUrl).into(signPro);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewContractActivity.this);
                bottomSheetDialog.setCancelable(false);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.signlocataire, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                Button clearButton = bottomSheetView.findViewById(R.id.clearButton);
                signatureView = bottomSheetView.findViewById(R.id.signatureView);
                Button buttonRetrieveSignature = bottomSheetView.findViewById(R.id.buttonRetrieveSignature);

                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        signatureView.clearSignature();
                        snloca.setImageBitmap(null);
                    }
                });
                buttonRetrieveSignature.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        signatureBitmap = signatureView.getSignatureBitmap();
                        if (signatureBitmap != null) {
                            snloca.setImageBitmap(signatureBitmap);
                            add.setVisibility(View.GONE);
                            valide.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
        valide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signatureBitmap != null) {
                    ajouterSignature(signatureBitmap,idAdm,idLca);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ajouterSignature(Bitmap signatureBitmap, String idAdm, String idLca) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] signatureData = baos.toByteArray();

        // Référence Firebase Storage pour l'image de signature
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("signatures/" + idLca + "_signature.png");

        UploadTask uploadTask = storageRef.putBytes(signatureData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Obtenir l'URL de la signature
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String signatureUrl = uri.toString();
                // Appel de la méthode pour sauvegarder le texte OCR et la signature
                saveContractToFirebase(idLca, idAdm, signatureUrl);
            });
        }).addOnFailureListener(e -> {
            Log.e("FirebaseStorage", "Erreur lors du téléchargement de la signature", e);
        });
    }

    private void saveContractToFirebase(String idLca, String idAdm, String signatureUrl) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("contrats");

        // Créer un objet ModelContract avec les données
        ModelContract contract = new ModelContract(idLca, "", signatureUrl,true);

        // Ajouter ces données dans Firebase sous l'ID de l'utilisateur
        databaseRef.child(idLca).setValue(contract)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseDatabase", "Contrat ajouté avec succès"))
                .addOnFailureListener(e -> Log.e("FirebaseDatabase", "Erreur lors de l'ajout du contrat", e));

        startActivity(new Intent(ViewContractActivity.this, EspaceLocataires.class));
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        incr++;
        if (incr == 1) {
            Intent intent = new Intent(this, EspaceLocataires.class);
            startActivity(intent);
            finish();
        }
    }
}