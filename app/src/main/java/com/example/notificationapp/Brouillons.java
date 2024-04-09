package com.example.notificationapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Brouillons {

    /*private void convertToPdfAndSend() {

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
    }*/

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
}
