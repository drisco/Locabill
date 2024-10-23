package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.example.notificationapp.models.ModelContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ResultatContrat extends AppCompatActivity {
    TextView textView;
    ImageView imageView;
    Button add;
    String idAdmin;
    PopupRegister popup;
    int incr;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultat_contrat);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        // Récupérer le texte extrait
        String extractedText = getIntent().getStringExtra("EXTRACTED_TEXT");

        // Récupérer l'image de la signature
        byte[] signatureByteArray = getIntent().getByteArrayExtra("SIGNATURE_IMAGE");
        Bitmap signatureBitmap = BitmapFactory.decodeByteArray(signatureByteArray, 0, signatureByteArray.length);

        // Afficher les données dans les vues (TextView, ImageView, etc.)
        textView = findViewById(R.id.contrat);
        textView.setText(extractedText);

        add = findViewById(R.id.add);
        imageView = findViewById(R.id.signPro);
        imageView.setImageBitmap(signatureBitmap);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupRegister(ResultatContrat.this);
                popup.setCancelable(false);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();
                uploadSignatureAndSaveContract(signatureBitmap,extractedText,idAdmin);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void uploadSignatureAndSaveContract(Bitmap signatureBitmap, String extractedText, String userId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] signatureData = baos.toByteArray();

        // Référence Firebase Storage pour l'image de signature
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("signatures/" + userId + "_signature.png");

        UploadTask uploadTask = storageRef.putBytes(signatureData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Obtenir l'URL de la signature
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String signatureUrl = uri.toString();
                // Appel de la méthode pour sauvegarder le texte OCR et la signature
                saveContractToFirebase(userId, extractedText, signatureUrl);
            });
        }).addOnFailureListener(e -> {
            popup.dismiss();
            Log.e("FirebaseStorage", "Erreur lors du téléchargement de la signature", e);
        });
    }

    private void saveContractToFirebase(String userId, String extractedText, String signatureUrl) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("contrats");

        // Créer un objet ModelContract avec les données
        ModelContract contract = new ModelContract(userId, extractedText, signatureUrl,true);

        // Ajouter ces données dans Firebase sous l'ID de l'utilisateur
        databaseRef.child(userId).setValue(contract)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseDatabase", "Contrat ajouté avec succès"))
                .addOnFailureListener(e -> Log.e("FirebaseDatabase", "Erreur lors de l'ajout du contrat", e));
        popup.dismiss();
        startActivity(new Intent(ResultatContrat.this, DetailAdmin.class));
        finish();
    }



    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(ResultatContrat.this,ContratLoyer.class));
            finish();
        }
    }
}