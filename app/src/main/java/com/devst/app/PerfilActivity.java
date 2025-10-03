package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilActivity extends AppCompatActivity {

    //Encapsulamiento
    private TextView tvCorreoPerfil, tvContrasenaPerfil, tvNombrePerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        String emailRecibido = getIntent().getStringExtra("email_usuario");
        String contrasenaRecibido = getIntent().getStringExtra("contrasena_usuario");
        String nombreRecibido = getIntent().getStringExtra("nombre_usuario");
        if (emailRecibido == null) emailRecibido = "";

        //Referencias
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil);
        tvCorreoPerfil.setText(emailRecibido);
        tvContrasenaPerfil = findViewById(R.id.tvContrasenaPerfil);
        tvContrasenaPerfil.setText(contrasenaRecibido);
        tvNombrePerfil = findViewById(R.id.tvNombrePefil);
        tvNombrePerfil.setText(nombreRecibido);

        //Funcion para el boton de volver al menu de inicio que esta en el perfil
        Button btnVolverAlPerfil= findViewById(R.id.btnVolverAlPerfil);

        btnVolverAlPerfil.setOnClickListener(v -> {
            finish();
        });
    }
}