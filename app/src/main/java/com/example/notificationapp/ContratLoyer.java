package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ContratLoyer extends AppCompatActivity {

    private ImageView documentImageView,signature;
    private EditText extractedTextView;
    private SignatureView signatureView;
    private Button clearButton,next;
    private Button buttonRetrieveSignature;
    private Bitmap signatureBitmap;
    private CustomScrollView scrollView;


    private Button captureButton;
    int incr;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private Bitmap capturedBitmap; // Image capturée



    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contrat_loyer);

        next = findViewById(R.id.next);
        scrollView = findViewById(R.id.scrollView);
        documentImageView = findViewById(R.id.documentImageView);
        extractedTextView = findViewById(R.id.extractedTextView);
        signatureView = findViewById(R.id.signatureView);
        captureButton = findViewById(R.id.captureButton);
        clearButton = findViewById(R.id.clearButton);
        signature = findViewById(R.id.signature);
        buttonRetrieveSignature = findViewById(R.id.buttonRetrieveSignature);


        captureButton.setOnClickListener(v -> {
            showImageSourceBottomSheet();

        });

        signatureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Désactiver le défilement
                        scrollView.setScrollingEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Réactiver le défilement
                        scrollView.setScrollingEnabled(true);
                        break;
                }
                return false; // Laisser la `SignatureView` gérer le dessin
            }
        });


        buttonRetrieveSignature.setOnClickListener(v -> {
            // Récupérer la signature et l'afficher dans une variable
            signatureBitmap = signatureView.getSignatureBitmap();
            if (signatureBitmap != null) {
                signature.setImageBitmap(signatureBitmap);
            } else {

            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearSignature();
                signature.setImageBitmap(null);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (signatureBitmap != null && !extractedTextView.getText().toString().isEmpty()) {
                    Intent intent = new Intent(ContratLoyer.this, ResultatContrat.class);
                    intent.putExtra("EXTRACTED_TEXT", extractedTextView.getText().toString());
                    // Passer la signature sous forme de ByteArray
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] signatureByteArray = stream.toByteArray();
                    intent.putExtra("SIGNATURE_IMAGE", signatureByteArray);
                    startActivity(intent);
                    finish();
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }




    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // La permission est déjà accordée
            dispatchTakePictureIntent();
        }
    }

    @SuppressLint("MissingInflatedId")
    private void showImageSourceBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ContratLoyer.this);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_image_source, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.buttonTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                requestCameraPermission();
            }
        });

        bottomSheetView.findViewById(R.id.buttonChooseFromGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                dispatchChooseFromGalleryIntent();
            }
        });

        bottomSheetDialog.show();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchChooseFromGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                capturedBitmap = (Bitmap) extras.get("data");
                documentImageView.setImageBitmap(capturedBitmap);
                // Appliquer l'OCR à l'image capturée
                extractTextFromImage(capturedBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                // Obtenir l'URI de l'image sélectionnée
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    try {
                        // Convertir l'URI en Bitmap
                        capturedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        documentImageView.setImageBitmap(capturedBitmap);
                        // Appliquer l'OCR à l'image sélectionnée
                        extractTextFromImage(capturedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void extractTextFromImage(Bitmap bitmap) {
        extractedTextView.setText("");
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        com.google.mlkit.vision.text.TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    // Récupérer le texte extrait
                    StringBuilder extractedText = new StringBuilder();
                    for (Text.TextBlock block : result.getTextBlocks()) {
                        // Ajouter chaque bloc de texte avec ses lignes respectives
                        for (Text.Line line : block.getLines()) {
                            extractedText.append(line.getText()).append("\n");
                        }
                        extractedText.append("\n");
                    }
                    extractedTextView.setText(extractedText.toString());

                    // Ajouter la signature et sauvegarder le document
                    Bitmap signedDocument = overlayTextAndSignature(extractedText.toString(), capturedBitmap);
                    saveDocumentWithTextAndSignature(signedDocument);
                })
                .addOnFailureListener(e -> Log.e("OCR", "Erreur lors de l'extraction du texte", e));
    }

    private Bitmap overlayTextAndSignature(String extractedText, Bitmap signatureBitmap) {
        // Créer un Bitmap pour le texte avec la signature
        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);

        int width = 800; // Largeur du document généré
        int height = signatureBitmap.getHeight() + 300; // Ajuster selon la taille du texte et de la signature
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawColor(Color.WHITE);

        // Dessiner le texte sur le Bitmap
        canvas.drawText(extractedText, 10, 50, paint);

        // Dessiner la signature en dessous du texte
        canvas.drawBitmap(signatureBitmap, 10, height - signatureBitmap.getHeight() - 50, null);

        return resultBitmap;
    }

    private void saveDocumentWithTextAndSignature(Bitmap finalDocument) {
        File file = new File(getExternalFilesDir(null), "document_with_signature.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            finalDocument.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(ContratLoyer.this,MainActivity.class));
            finish();
        }
    }
}