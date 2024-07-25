package com.example.gestordedeudas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DeudaActivity extends AppCompatActivity {
    EditText inputBuscar;
    Spinner spinnerBusqueda;
    Button btnBuscar;
    ListView ListViewDeudas;
    Button btnRedirigirAAgregar;
    Intent intent;
    BaseDeDatos baseDeDatos;
    ArrayAdapter<String> adapter;

    // ----------------
    ArrayList<String> listaDeudas = new ArrayList<>();
    ArrayList<String> listaDeudasId = new ArrayList<>();
    // ----------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deuda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InicializarVariables();

        btnRedirigirAAgregar.setOnClickListener(v -> {
            Toast.makeText(this, "Para agregar una deuda", Toast.LENGTH_SHORT).show();
        });

    }
    public void InicializarVariables(){
        try {
            baseDeDatos = new BaseDeDatos(this);
            inputBuscar = findViewById(R.id.inputBuscar);
            spinnerBusqueda = findViewById(R.id.busquedaSpiner);
            btnBuscar = findViewById(R.id.btnBuscar);
            ListViewDeudas = findViewById(R.id.listDeudas);
            btnRedirigirAAgregar = findViewById(R.id.btnEnviarAgregarDeuda);
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void getDatosDeuda(){
        listaDeudas.clear();
        listaDeudasId.clear();
        ArrayList<String> datosDeuda = baseDeDatos.getDeudas();
        if (datosDeuda.size() == 0){
            listaDeudas.add("No hay deudas");
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeudas);
            ListViewDeudas.setAdapter(adapter);
        }
        else{
            // "id-titulo
            for (String dato : datosDeuda){
                String[] datosFila = dato.split("-");
                listaDeudas.add(datosFila[1]);
                listaDeudasId.add(datosFila[0]);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeudas);
            ListViewDeudas.setAdapter(adapter);

        }


    }
}