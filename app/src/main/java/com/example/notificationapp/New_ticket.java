package com.example.notificationapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.Model_code_pin;
import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class New_ticket extends AppCompatActivity {
    NumberToWords numberToWords = new NumberToWords();
    private static final int PERMISSION_REQUEST_CODE = 140;
    private static final int PERMISSION_REQUEST_CO = 10;
    DatabaseReference databaseReference, databaseReference1;
    int incr,intmontant,intavance;String id_2,lieu;

    ImageView plus,moins;
    EditText edit;
    RelativeLayout show,recuId;
    ImageView qrImageView,retour1;
    TextView buttonPrintReceipt;
    Button buttonSendReceipt;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat monthYearFormat;
    TextView userNom,userPrenom,number,montant,type,date,debut,s_chiffre,payer,avance;
    int count = 0;String qrContent,idAdmin,montantChiffre,resultat,numberInWords;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("recu");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("localites").child(idAdmin);
        plus = findViewById(R.id.plus);
        edit = findViewById(R.id.edit);
        retour1 = findViewById(R.id.retour1);
        show = findViewById(R.id.show);
        recuId = findViewById(R.id.recuId);
        payer = findViewById(R.id.payer);
        avance = findViewById(R.id.avance);

        buttonPrintReceipt = findViewById(R.id.buttonPrintReceipt);
        buttonSendReceipt = findViewById(R.id.buttonSendReceipt);
        qrImageView = findViewById(R.id.qrImageView);

        userNom = findViewById(R.id.userNom);
        userPrenom = findViewById(R.id.userPrenom);
        number = findViewById(R.id.number);
        montant = findViewById(R.id.montant);
        type = findViewById(R.id.type);
        date = findViewById(R.id.date);
        s_chiffre = findViewById(R.id.s_chiffre);
        debut = findViewById(R.id.debut);

        monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()); // Format "Février 2024" pour la France
        setupDatePicker();
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
                    plus.setImageResource(R.drawable.cancel);
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


        Intent intent = getIntent();
        id_2 = intent.getStringExtra("id");
        String nom = intent.getStringExtra("nom");

        String prenom = intent.getStringExtra("prenom");
        String prix = intent.getStringExtra("prix");
        String numero = intent.getStringExtra("numero");
        String type_de_maison = intent.getStringExtra("type_de_maison");
        String debut_de_loca = intent.getStringExtra("debut_de_loca");
        String cautions = intent.getStringExtra("caution");
        String avances = intent.getStringExtra("avance");
        String date_ = intent.getStringExtra("date");
        lieu = intent.getStringExtra("lieu");
        numberInWords = NumberToWords.convertToWords(Integer.parseInt(prix));

        intmontant = Integer.parseInt(prix);
        intavance =Integer.parseInt(avances);
        if (intavance>=intmontant){
            resultat= String.valueOf(intavance-intmontant);
            avance.setText(resultat+ " FCFA");

        }else{
            resultat=avances;
            avance.setText(avances+ " FCFA");
        }


        s_chiffre.setText("Montant en chiffre : "+numberInWords+ " FCFA");
        montantChiffre=s_chiffre.getText().toString();
        userNom.setText(nom);
        userPrenom.setText(prenom);
        number.setText(numero);
        montant.setText(prix+ " FCFA");
        type.setText(type_de_maison);
        debut.setText(debut_de_loca);

        Date dates = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String dateFormatted = sdf.format(dates);

        date.setText(dateFormatted);

         // Générez le code QR à partir du contenu
        generateQRCode(id_2);

        buttonSendReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecuData(userNom.getText().toString(),userPrenom.getText().toString(),montant.getText().toString(),number.getText().toString(),
                        type.getText().toString(),debut.getText().toString(),cautions,avance.getText().toString(),date.getText().toString());
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
                    int avancein =Integer.parseInt(resultat);
                    avancein +=Integer.parseInt(s.toString());
                    resultat= String.valueOf(avancein);
                    avance.setText(resultat+" FCFA");
                }else{
                    if (intavance>=intmontant){
                        resultat= String.valueOf(intavance-intmontant);
                        avance.setText(resultat+ " FCFA");

                    }else{
                        avance.setText(avances+ " FCFA");
                    }

                }
            }
        });

    }

    private void setupDatePicker() {
        // Créez une instance de DatePickerDialog
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Créer un objet Calendar et le définir sur la date sélectionnée
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);

                // Formater la date sélectionnée en utilisant le format du mois et de l'année
                String mattedDate = monthYearFormat.format(selectedDate.getTime());
                debut.setText(mattedDate);
            }
        }, year, month, dayOfMonth);
    }


    private void addRecuData(String nom, String prenom, String montant, String numero, String type, String debutLoca, String caution, String avance, String date) {
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String heureActuelle = sdf.format(heure);

        databaseReference1.child(lieu).child(id_2).child("avance").setValue(resultat);
        DatabaseReference localiteReference = databaseReference.child(idAdmin).child(id_2).push();
        String nouvelId = localiteReference.getKey();
        Model_ticket nouveauLocataire = new Model_ticket(nouvelId, nom, prenom, montant, numero, type, debutLoca, caution, resultat,numberInWords, date,heureActuelle);
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

    private void generateQRCode(String id) {

        try {
            MultiFormatWriter formatWriter = new MultiFormatWriter();
            BitMatrix matrix = formatWriter.encode(id, BarcodeFormat.QR_CODE, 500, 500);
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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
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
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(screenWidth, screenHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);


// Obtenir la vue à dessiner
        View view = findViewById(R.id.detailsLayout);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);
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

        String message="Bonjour Monsieur/Madame "+ userPrenom.getText().toString()+ " Votre reçu de paiement du mois à été envoyé sur votre whatsapp";
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