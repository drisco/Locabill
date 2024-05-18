package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.notificationapp.models.Annonce;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnnonceProbleme extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    DatabaseReference databaseRef;
    private LinearLayout imageContainer;
    private int incr;
    private List<Bitmap> selectedImages = new ArrayList<>();
    Button buttonAddImage,buttonAdd;
    EditText editTextTitle,editTextDescription;
    SharedPreferences sharedPreferences;
    String idAdm,idLca,ville,numero,nom,prenom;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_probleme);
        imageContainer = findViewById(R.id.imageContainer);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAddImage = findViewById(R.id.buttonAddImage);
        sharedPreferences = getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);

        idAdm = sharedPreferences.getString("idAdmin", "");
        idLca = sharedPreferences.getString("id", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");
        nom = sharedPreferences.getString("nom", "");
        prenom = sharedPreferences.getString("prenom", "");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("annonces");

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceBottomSheet();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextTitle.getText().toString().isEmpty() || !editTextDescription.getText().toString().isEmpty()){
                    if (!selectedImages.isEmpty()){
                        addMethode(editTextTitle.getText().toString(),editTextDescription.getText().toString(),selectedImages);
                    }else{
                        Toast.makeText(AnnonceProbleme.this, "Image obligatoire", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AnnonceProbleme.this, "Titre et Description Obligattoire", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addMethode(String titre, String description, List<Bitmap> selectedImages) {
        List<String> imageUris = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        StorageReference imageRef = storageRef.child("images/IMG_"+timestamp + ".jpg");

        for (Bitmap bitmap : selectedImages) {
System.out.println("HEGHIUREH9TH REUH89HRZB HHTPJZ8HR9Z ERJ9RGHEGH09Z UENG9JGRJERZ JROGJOEJRHJERSOI  RENOGEJGREJE0P J90TRUJRGBH0ZEI J9HGRJREHGETJ "+selectedImages);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    System.out.println("HEGHIUREH9TH REUH89HRZB HHTPJZ8HR9Z ERJ9RGHEGH09Z UENG9JGRJERZ JROGJOEJRHJERSOI  RENOGEJGREJE0P J90TRUJRGBH0ZEI J9HGRJREHGETJ "+"selectedImages");
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    System.out.println("HEGHIUREH9TH REUH89HRZB HHTPJZ8HR9Z ERJ9RGHEGH09Z UENG9JGRJERZ JROGJOEJRHJERSOI  RENOGEJGREJE0P J90TRUJRGBH0ZEI J9HGRJREHGETJ "+"ZOOOOOOO");
                    imageUris.add(task.getResult().toString());
                    if (imageUris.size() == selectedImages.size()) {
                        System.out.println("HEGHIUREH9TH REUH89HRZB HHTPJZ8HR9Z ERJ9RGHEGH09Z UENG9JGRJERZ JROGJOEJRHJERSOI  RENOGEJGREJE0P J90TRUJRGBH0ZEI SIZESIZESIZE "+selectedImages.size());

                        saveImageUrisToDatabase(imageUris,titre,description);
                    }
                }
            });
        }

    }

    private void saveImageUrisToDatabase(List<String> imageUris, String titre, String description) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateAndTime = sdf.format(new Date());
        DatabaseReference myRef=databaseRef.child(idAdm).child(idLca).push();
        String nouvelId = myRef.getKey();
        Annonce data =new Annonce(nouvelId,nom,prenom,ville,numero,titre,description,dateAndTime,imageUris);
        myRef.setValue(data);
        startActivity(new Intent(AnnonceProbleme.this,EspaceLocataires.class));
        finish();
        System.out.println("HEGHIUREH9TH REUH89HRZB HHTPJZ8HR9Z ERJ9RGHEGH09Z UENG9JGRJERZ JROGJOEJRHJERSOI  RENOGEJGREJE0P J90TRUJRGBH0ZEI SIZESIZESIZE "+selectedImages.size());

    }

    @SuppressLint("MissingInflatedId")
    private void showImageSourceBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AnnonceProbleme.this);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, vous pouvez maintenant utiliser l'appareil photo
                dispatchTakePictureIntent();
            } else {
                // La permission a été refusée, vous pouvez afficher un message à l'utilisateur
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                addImageToContainer(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                    addImageToContainer(imageBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addImageToContainer(Bitmap bitmap) {
        selectedImages.add(bitmap);
        refreshImageContainer();
    }

    private void refreshImageContainer() {
        imageContainer.removeAllViews();
        for (Bitmap bitmap : selectedImages) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(8, 0, 8, 0);
            imageView.setLayoutParams(layoutParams);
            imageContainer.addView(imageView);

        }
    }

    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(AnnonceProbleme.this,EspaceLocataires.class));
            finish();
        }
    }

}
