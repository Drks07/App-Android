package com.devst.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;



import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;





public class HomeActivity extends AppCompatActivity {

    //Encapsulamientos
    private String emailUsuario, contrasenaUsuario, nombreUsuario ="";
    private TextView tvBienvenida, tvContrasenaHome, tvNombreHome;
    private CameraManager camara;
    private String camaraID = null;
    private boolean luz = false;
    private Button btnLinterna;

    //Función para capturar resultados para el perfil
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    String nombre = result.getData().getStringExtra("nombre_editado");
                    String contra = result.getData().getStringExtra("contraseña_editada");
                    String user = result.getData().getStringExtra("username_editado");
                    if (nombre != null && contra != null && user != null){
                        tvBienvenida.setText("Hola, " + nombre);
                        tvContrasenaHome.setText(contra);
                        tvNombreHome.setText(user);
                    }
                }
            });

    //Launcher para pedir permiso de camara en tiempo de ejecucion
    private final ActivityResultLauncher<String>permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                alternarluz(); //Si conceden permiso, intentamos prender/apagar
            } else {
                Toast.makeText(this, "Permiso de camara denegado", Toast.LENGTH_SHORT).show();
            }
            });

    private void alternarluz(){
        try {
            luz = !luz;
            camara.setTorchMode(camaraID, luz);
            btnLinterna.setText(luz ? "Apagar linterna" : "Encender linterna");
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error al controlar la linterna", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (camaraID != null && luz) {
            try {
                camara.setTorchMode(camaraID, false);
                luz = false;
                if (btnLinterna != null) btnLinterna.setText("Encender Linterna");
            } catch (CameraAccessException ignored) {}
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        //Referencias
        tvNombreHome = findViewById(R.id.tvNombreHome);
        tvContrasenaHome = findViewById(R.id.tvContrasenaHome);
        tvBienvenida = findViewById(R.id.tvBienvenida);
        Button btnIrPerfil = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        Button btnAcercaDe = findViewById(R.id.btnAcercaDe);

        btnLinterna = findViewById(R.id.btnLinterna);
        Button btnCamara = findViewById(R.id.btnCamara);
        camara = (CameraManager) getSystemService(CAMERA_SERVICE);

        //Recibir datos desde el login
        emailUsuario = getIntent().getStringExtra("email_usuario");
        contrasenaUsuario = getIntent().getStringExtra("contrasena_usuario");
        nombreUsuario = getIntent().getStringExtra("nombre_usuario");
        if (emailUsuario == null ) emailUsuario = ""; //if mas corto
        tvBienvenida.setText("Bienvenido: " + emailUsuario);
        tvContrasenaHome.setText(contrasenaUsuario);
        tvNombreHome.setText(nombreUsuario);

        //Evento Explicito iniciar vista perfil
        btnIrPerfil.setOnClickListener(View -> { //puede ir solo la v envez de View
            Intent perfil = new Intent(HomeActivity.this, PerfilActivity.class); //Donde estoy y hacia donde quiero ir
            perfil.putExtra("email_usuario", emailUsuario);
            perfil.putExtra("contrasena_usuario", contrasenaUsuario);
            perfil.putExtra("nombre_usuario", nombreUsuario);
            editarPerfilLauncher.launch(perfil);
        });

        //Evento Explicito iniciar vista Acerca de
        btnAcercaDe.setOnClickListener(View -> {
            Intent acerca = new Intent(HomeActivity.this, AcercaActivity.class);
            startActivity(acerca);
        });

        //Eventos Implicito para abrir una web
        btnAbrirWeb.setOnClickListener(View -> {
            Uri url = Uri.parse("http://www.santotomas.cl");
            Intent viewWeb = new Intent(Intent.ACTION_VIEW, url);
            startActivity(viewWeb);
        });

        //Evento implicito correo
        btnEnviarCorreo.setOnClickListener(View -> {
            Intent correo = new Intent(Intent.ACTION_SENDTO);
            correo.setData(Uri.parse("mailto:")); //Uri para usar una URL. Solo para correo electronico
            correo.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario}); //Para saber que correo voy a usar para enviar el mail
            correo.putExtra(Intent.EXTRA_SUBJECT, "Prueba de correo :)"); //Para el asunto del mail
            correo.putExtra(Intent.EXTRA_TEXT, "Hola mundo desde el BTN Correo"); //Para el cuerpo del mail
            startActivity(Intent.createChooser(correo, "Enviar correo a:"));
        });


        //Evento implicito compartir
        btnCompartir.setOnClickListener(View -> {
            Intent compartir = new Intent(Intent.ACTION_SEND); //Para enviar, no es lo mismo que sendto
            compartir.setType("text/plain"); //Tipo de texto que uso sera texto plano
            compartir.putExtra(Intent.EXTRA_TEXT, "Hola mundo desde android"); //Cuerpo del texto plano
            startActivity(Intent.createChooser(compartir, "Compartiendo: "));

        });

        try{
            for(String id : camara.getCameraIdList()) {
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean disponibleFlash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer lensFacing = cc.get(CameraCharacteristics.LENS_FACING);
                if (Boolean.TRUE.equals(disponibleFlash) && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK){
                    camaraID = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Toast.makeText(this, "No se puede acceder a la camara", Toast.LENGTH_SHORT).show();
        }

        btnLinterna.setOnClickListener(v -> {
            if (camaraID == null) {
                Toast.makeText(this,"Este dispositivo no tiene flash disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            //Verifica permiso en tiempo de ejecucion
            boolean camGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

            if (camGranted) {
                alternarluz();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        //Evento para iniciar CamaraActivity
        btnCamara.setOnClickListener(v ->
                startActivity(new Intent(this, CamaraActivity.class))
        );
    }

}