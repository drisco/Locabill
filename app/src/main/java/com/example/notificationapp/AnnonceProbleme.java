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
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class AnnonceProbleme extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    DatabaseReference databaseRef;
    private LinearLayout imageContainer;
    private int incr;
    private List<Bitmap> selectedImages = new ArrayList<>();
    Button buttonAdd;
    TextView buttonAddImage,text;
    ImageView play,pause,retour,vocal;
    EditText editTextTitle,editTextDescription;
    SharedPreferences sharedPreferences;
    String idAdm,idLca,ville,numero,nom,prenom;
    private TextToSpeech tts;
    private static final int REQUEST_MICROPHONE_PERMISSION = 123;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;


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
        text = findViewById(R.id.text);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        retour = findViewById(R.id.rtour);
        vocal = findViewById(R.id.vocal);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.FRENCH); // Définir la langue
                    if (result!= TextToSpeech.LANG_MISSING_DATA && result!= TextToSpeech.LANG_NOT_SUPPORTED) {
                        speakOutText(text.getText().toString()); // Appeler la méthode pour lire le texte
                    } else {
                        Log.e("TTS", "La langue n'est pas supportée");
                    }
                } else {
                    Log.e("TTS", "Initialisation échouée");
                }
            }
        });

        vocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AnnonceProbleme.this, android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AnnonceProbleme.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE_PERMISSION);
                } else {
                    // La permission est déjà accordée, vous pouvez lancer la reconnaissance vocale
                    startVoiceRecognition();
                }
            }
        });
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnnonceProbleme.this,EspaceLocataires.class));
                finish();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.stop();
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakOutText(text.getText().toString());
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });
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
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 50000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5500);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    private void speakOutText(String msge) {
        tts.speak(msge, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void addMethode(String titre, String description, List<Bitmap> selectedImages) {
        List<String> imageUris = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        StorageReference imageRef = storageRef.child("images/IMG_"+timestamp + ".jpg");

        for (Bitmap bitmap : selectedImages) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    imageUris.add(task.getResult().toString());
                    if (imageUris.size() == selectedImages.size()) {
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
        }else if (requestCode == REQUEST_MICROPHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée, lancez la reconnaissance vocale
                startVoiceRecognition();
            } else {
                // La permission a été refusée, informez l'utilisateur ou gérez la situation
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
            }else if(requestCode ==REQUEST_CODE_SPEECH_INPUT && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = result.get(0);
                editTextDescription.setText(spokenText);
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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 500);
            layoutParams.setMargins(5, 0, 5, 0);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            Glide.with(this).load(bitmap)
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(30, 0)))
                    .into(imageView);
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
