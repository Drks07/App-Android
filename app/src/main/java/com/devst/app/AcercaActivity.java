package com.devst.app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AcercaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acerca);

        WebView webView = findViewById(R.id.webViewAcerca);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://github.com/Drks07/App-Android");
    }

}
