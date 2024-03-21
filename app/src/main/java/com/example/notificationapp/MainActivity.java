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
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 140;
    private static final int PERMISSION_REQUEST_CO = 10;
    /*private static final long INTERVAL_MS = 2 * 60 * 1000;*/ // 2 minutes en millisecondes
    private static final long INTERVAL_MS = 1 * 60 * 1000;
    int incr;
    private Handler handler;
    RelativeLayout rl1,rl2,rlt2;
    CircleImageView profil;
    ImageView r2;
    String idAdmin;
    EditText etToken;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rl1=findViewById(R.id.rl1);
        profil=findViewById(R.id.profil);
        rl2=findViewById(R.id.rl2);
        rlt2=findViewById(R.id.rlt2);
        r2=findViewById(R.id.r2);
        TextView foot=findViewById(R.id.foot);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailAdmin.class));
            }
        });
        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,List_of_tenants.class));
            }
        });
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MoreTenant.class));
            }
        });
        rlt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Creat_new_tenant.class));
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Creat_new_tenant.class));
            }
        });
        // Créer un Handler pour envoyer périodiquement le SMS
       // handler = new Handler();
       // handler.postDelayed(sendSMSRunnable, INTERVAL_MS);
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();

        }
/*
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                convertToPdfAndSend();
                checkPermissions();
            }
        });
*/

    }
    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CO);
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
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
        View view = findViewById(R.id.ll1);
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
            String phoneNumberWithCountryCode="+2250546968733";
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


            Uri uri =FileProvider.getUriForFile(this,"com.example.notificationapp.provider", pdfFile);

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

/*
    private void sendPdfViaWhatsApp(File pdfFile, String phoneNumberWithCountryCode) {
            Uri pdfUri = Uri.fromFile(pdfFile);
            String encodedPdfUri = Uri.encode(pdfUri.toString());
            if (pdfFile.exists()) {
                String whatsappUrl = String.format("https://api.whatsapp.com/send?phone=%s&text=Votre%20message&media=%s", phoneNumberWithCountryCode,encodedPdfUri);

                // Ouvrir l'application WhatsApp avec l'URL
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(whatsappUrl));
                startActivity(intent);
            } else {
                // Afficher un message d'erreur si le fichier PDF n'existe pas
                Toast.makeText(this, "Fichier PDF introuvable", Toast.LENGTH_SHORT).show();
            }

    }
*/

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission d'envoyer des SMS
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        } else {
            // Si les autorisations sont déjà accordées, envoyer un SMS
            sendSMS();
        }
    }

    // Méthode pour gérer la réponse de l'utilisateur à la demande d'autorisation
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

    // Méthode pour envoyer un SMS
    private void sendSMS() {
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+2250504389406");
        phoneNumbers.add("+2250504463805");
        phoneNumbers.add("+2250576155481");

        String message = "En cette fin de mois, nous tenions à vous rappeler que le loyer pour le mois de mars est dû d'ici le 10/04/2024.Afin d'éviter tout retard.En cas de difficultés financières ou pour discuter de modalités de paiement alternatives, n'hésitez pas à nous contacter dès que possible. La transparence et la communication sont essentielles pour maintenir une relation locataire-propriétaire harmonieuse.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            for (String phoneNumber : phoneNumbers) {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            }

            Toast.makeText(getApplicationContext(), "SMS envoyés avec succès.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erreur lors de l'envoi des SMS. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Runnable pour envoyer le SMS périodiquement
    private final Runnable sendSMSRunnable = new Runnable() {
        @Override
        public void run() {
            sendSMS(); // Appeler la méthode pour envoyer le SMS
            handler.postDelayed(this, INTERVAL_MS); // Programmer le prochain envoi
        }
    };
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