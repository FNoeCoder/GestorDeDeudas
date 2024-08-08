package com.example.gestordedeudas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button Personas;
    Button Deudas;
    Button Salir;
    Intent intent;
    BaseDeDatos bd;
    private long pauseTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Personas = findViewById(R.id.btnPersonas);
        Deudas = findViewById(R.id.btnDeudas);
        Salir = findViewById(R.id.btnSalir);
        Personas.setOnClickListener(v -> {
            intent = new Intent(this, PersonasActivity.class);
            startActivity(intent);
        });
        Deudas.setOnClickListener(v -> {
            //Toast.makeText(this, "Deudas", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, DeudaActivity.class);
            startActivity(intent);
        });
        Salir.setOnClickListener(v -> {
            //Cerrar la aplicación
            finish();
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        long pauseTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bd != null) {
        }
    }
}