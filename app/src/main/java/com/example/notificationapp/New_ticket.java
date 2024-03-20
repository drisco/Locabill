package com.example.notificationapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class New_ticket extends AppCompatActivity {
    NumberToWords numberToWords = new NumberToWords();
    private static final int PERMISSION_REQUEST_CODE = 140;
    private static final int PERMISSION_REQUEST_CO = 10;
    DatabaseReference databaseReference;
    int incr;String id_2;

    ImageView plus,moins;
    EditText edit;
    RelativeLayout show,recuId;
    ImageView qrImageView,retour1;
    TextView buttonPrintReceipt;
    Button buttonSendReceipt;
    TextView userNom,userPrenom,number,caution,montant,type,date,avance,debut,s_chiffre,payer;
    int count = 0;String qrContent,idAdmin;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("recu");
        System.out.println("HJEFFGIFGUHRFJRHREJOFRPIPRJOITJOMJOG3LOBJOI3R "+idAdmin);
        plus = findViewById(R.id.plus);
        edit = findViewById(R.id.edit);
        retour1 = findViewById(R.id.retour1);
        show = findViewById(R.id.show);
        recuId = findViewById(R.id.recuId);
        payer = findViewById(R.id.payer);
        buttonPrintReceipt = findViewById(R.id.buttonPrintReceipt);
        buttonSendReceipt = findViewById(R.id.buttonSendReceipt);
        qrImageView = findViewById(R.id.qrImageView);

        if (checkPermissionBoolean()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();

        }
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if (count==1){
                    show.setVisibility(View.VISIBLE);
                    plus.setImageResource(R.drawable._replay);
                }else if (count==2){
                    show.setVisibility(View.GONE);
                    plus.setImageResource(R.drawable.add);
                    count=0;
                }
            }
        });
        retour1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(New_ticket.this, List_of_tenants.class));
                finish();
            }
        });

        userNom = findViewById(R.id.userNom);
        userPrenom = findViewById(R.id.userPrenom);
        number = findViewById(R.id.number);
        caution = findViewById(R.id.caution);
        montant = findViewById(R.id.montant);
        type = findViewById(R.id.type);
        date = findViewById(R.id.date);
        s_chiffre = findViewById(R.id.s_chiffre);
        avance = findViewById(R.id.avance);
        debut = findViewById(R.id.debut);
        Intent intent = getIntent();
        id_2 = intent.getStringExtra("id");
        String nom = intent.getStringExtra("nom");
        System.out.println("HJEFFGIFGUHRFJRHREJOFRPIPRJOITJOMJOG3LOBJOI3R IDIDDIIDID"+nom);

        String prenom = intent.getStringExtra("prenom");
        String prix = intent.getStringExtra("prix");
        String numero = intent.getStringExtra("numero");
        String type_de_maison = intent.getStringExtra("type_de_maison");
        String debut_de_loca = intent.getStringExtra("debut_de_loca");
        String cautions = intent.getStringExtra("caution");
        String avances = intent.getStringExtra("avance");
        String date_ = intent.getStringExtra("date");
        String numberInWords = NumberToWords.convertToWords(Integer.parseInt(prix));

        s_chiffre.setText("Montant en chiffre : "+numberInWords+ " FCFA");
        userNom.setText(nom);
        userPrenom.setText(prenom);
        number.setText(numero);
        caution.setText(cautions+" FCFA");
        avance.setText(avances+ " FCFA");
        montant.setText(prix+ " FCFA");
        type.setText(type_de_maison);
        debut.setText(debut_de_loca);

        Date dates = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String dateFormatted = sdf.format(dates);

        date.setText(dateFormatted);

         // Générez le code QR à partir du contenu
        generateQRCode(userNom.getText().toString(),userPrenom.getText().toString(),montant.getText().toString(),number.getText().toString(),
                type.getText().toString(),debut.getText().toString(),caution.getText().toString(),avance.getText().toString(),date.getText().toString());

        buttonSendReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecuData(userNom.getText().toString(),userPrenom.getText().toString(),montant.getText().toString(),number.getText().toString(),
                        type.getText().toString(),debut.getText().toString(),caution.getText().toString(),avance.getText().toString(),date.getText().toString());
                convertToPdfAndSend();
                checkPermissions();
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){
                    montant.setText(s.toString()+" FCFA");
                    s_chiffre.setText("Montant en chiffre : "+NumberToWords.convertToWords(Integer.parseInt(s.toString()))+ " FCFA");
                }else{
                    montant.setText(prix+" FCFA");
                    s_chiffre.setText("Montant en chiffre : "+numberInWords+ " FCFA");
                  }
            }
        });
        buttonPrintReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQRCode(userNom.getText().toString(),userPrenom.getText().toString(),montant.getText().toString(),number.getText().toString(),
                        type.getText().toString(),debut.getText().toString(),caution.getText().toString(),avance.getText().toString(),date.getText().toString());

            }
        });
    }


    private void addRecuData(String nom, String prenom, String montant, String numero, String type, String debutLoca, String caution, String avance, String date) {
        DatabaseReference localiteReference = databaseReference.child(idAdmin).child(id_2).push();
        String nouvelId = localiteReference.getKey();
        System.out.println("FJGKJGHFDHGFEJNGEKHGEHGREJPOJKJZHµPJHIOTJOUZH%JGJ%HGJM5GMJOJHEGJOIGEJOIJGEKJKJKJJKKKLLK?JB"+nom);
        Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, montant, numero, type, debutLoca, caution, avance, date);
        localiteReference.setValue(nouveauLocataire);
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CO);
    }

    private boolean checkPermissionBoolean() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void generateQRCode(String toString, String toString1, String toString2, String toString3, String toString4, String toString5, String toString6, String toString7, String toString8) {
        System.out.println("GFVHHJDG 99999999 TRUE TRUE AVANT= "+qrContent);

        qrContent ="\nNom: " + toString + "\nPrénom: " + toString1 + "\nPrix: " + toString2 +
                "\nNuméro: " + toString3 + "\nType de maison: " + toString4 + "\nDébut de location: " + toString5 +
                "\nCaution: " + toString6 + "\nAvance: " + toString7 + "\nDate: " + toString8;
        System.out.println("GFVHHJDG 99999999 TRUE TRUE APRES= "+qrContent);

        try {
            MultiFormatWriter formatWriter = new MultiFormatWriter();
            BitMatrix matrix = formatWriter.encode(qrContent, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcode = new BarcodeEncoder();
            Bitmap bitmap = barcode.createBitmap(matrix);
            qrImageView.setImageBitmap(bitmap);
            // Vous pouvez ajouter ici d'autres actions si nécessaire
        } catch (WriterException e) {
            e.printStackTrace();
            // Gérez les exceptions ici
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission d'envoyer des SMS
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        } else {
            // Si les autorisations sont déjà accordées, envoyer un SMS
            sendSMS();
        }
    }
    private void convertToPdfAndSend() {

        String filename= System.currentTimeMillis()+ "reçu.pdf";
        String pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Reçus/"+filename;
        File pdfFile = new File(pdfFilePath);

        if (pdfFile.exists()) {
            // Supprimer le fichier PDF précédent s'il existe
            boolean deleted = pdfFile.delete();
            if (!deleted) {
                // Gestion de l'échec de la suppression du fichier
                Toast.makeText(this, "Erreur lors de la suppression du fichier PDF précédent", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Créer un nouveau document PDF
        PdfDocument document = new PdfDocument();

        // Créer une page PDF
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1080, 1920, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Dessiner la vue de connexion sur le canvas
        View view = findViewById(R.id.recuId);
        view.draw(canvas);

        // Terminer la page
        document.finishPage(page);

        // Enregistrer le document PDF localement
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Reçus/";
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                // Gestion de l'échec de création du répertoire
                Toast.makeText(this, "Erreur lors de la création du répertoire", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        pdfFile = new File(directoryPath + filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            document.writeTo(outputStream);
            document.close();
            outputStream.flush();
            outputStream.close();

            // Envoyer le PDF via WhatsApp
            String phoneNumberWithCountryCode=number.getText().toString();
            sendPdfViaWhatsApp(pdfFile, phoneNumberWithCountryCode);

        } catch (IOException e) {
            e.printStackTrace();
            // Gestion de l'erreur d'écriture du fichier PDF
            Toast.makeText(this, "Erreur lors de la génération du fichier PDF"+e, Toast.LENGTH_SHORT).show();
        }
    }
    private void sendPdfViaWhatsApp(File pdfFile, String phoneNumberWithCountryCode) {
        if (pdfFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");


            Uri uri = FileProvider.getUriForFile(this,"com.example.notificationapp.provider", pdfFile);

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra("jid", phoneNumberWithCountryCode + "@s.whatsapp.net");
            intent.setPackage("com.whatsapp");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Afficher un message d'erreur si le fichier PDF n'existe pas
            Toast.makeText(this, "Fichier PDF introuvable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'utilisateur accorde la permission, envoyer le SMS
                sendSMS();
            } else {
                // Si l'utilisateur refuse la permission, afficher un message
                Toast.makeText(getApplicationContext(), "Permission refusée pour envoyer des SMS.", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
    private void sendSMS() {

        String message="Bonjour Monsieur/Madame "+ userPrenom.getText().toString()+ " Votre reçu de paiement du mois à été géneré";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(number.getText().toString(), null, parts, null, null);


            Toast.makeText(getApplicationContext(), "SMS envoyés avec succès.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erreur lors de l'envoi des SMS. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(New_ticket.this,List_of_tenants.class));
            finish();
        }
    }

}