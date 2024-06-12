package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TestActivity extends AppCompatActivity {
    int incr;
    WebView webView;
    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        String textReceived = intent.getStringExtra("url");

// Récupération de la référence au WebView
        WebView webView = findViewById(R.id.web);

// Configuration du WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true); // Active le stockage DOM
        /*webView.getSettings().setLoadsImagesAutomatically(true); // Charge automatiquement les images
        webView.getSettings().setBuiltInZoomControls(true); */// Active le zoom intégré
        //webView.getSettings().setDisplayZoomControls(true); // Désactive les contrôles de zoom
       // webView.getSettings().setSupportMultipleWindows(true); // Supporte les fenêtres multiples
       // webView.getSettings().setMediaPlaybackRequiresUserGesture(false); // Permet le lecture audio sans geste utilisateur

       // Chargement de l'URL reçue
        webView.loadUrl(textReceived);
        

    }


@Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(TestActivity.this,EspaceLocataires.class));
            finish();
        }
    }
}