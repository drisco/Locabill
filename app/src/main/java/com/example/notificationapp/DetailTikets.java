package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class DetailTikets extends AppCompatActivity {

    TextView userNom,userPrenom,number,montant,type,date,debut,s_chiffre,payer;
    ImageView qrImageView1;


    int incr;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_tikets);


        userNom = findViewById(R.id.userNom);
        userPrenom = findViewById(R.id.userPrenom);
        number = findViewById(R.id.number);
        montant = findViewById(R.id.montant);
        type = findViewById(R.id.type);
        date = findViewById(R.id.date);
        s_chiffre = findViewById(R.id.s_chiffre);
        debut = findViewById(R.id.debut);
        qrImageView1 = findViewById(R.id.qrImageView);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String nom = intent.getStringExtra("nom");

        String prenom = intent.getStringExtra("prenom");
        String prix = intent.getStringExtra("prix");
        String numero = intent.getStringExtra("numero");
        String type_de_maison = intent.getStringExtra("type_de_maison");
        String debut_de_loca = intent.getStringExtra("date");
        String chiffre = intent.getStringExtra("chiffre");
        String heuredate = intent.getStringExtra("debut_de_loca");

        s_chiffre.setText(chiffre);
        userNom.setText(nom);
        userPrenom.setText(prenom);
        number.setText(numero);
        montant.setText("Montatnt en chiffre: "+prix+ " FCFA");
        type.setText(type_de_maison);
        debut.setText(debut_de_loca);
        date.setText(heuredate);

        String qrContent=id;
        try {
            MultiFormatWriter formatWriter = new MultiFormatWriter();
            BitMatrix matrix = formatWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcode = new BarcodeEncoder();
            Bitmap bitmap = barcode.createBitmap(matrix);
            qrImageView1.setImageBitmap(bitmap);
            // Vous pouvez ajouter ici d'autres actions si nécessaire
        } catch (WriterException e) {
            e.printStackTrace();
            // Gérez les exceptions ici
        }

        ;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(DetailTikets.this,Historique.class));
            finish();
        }
    }
}