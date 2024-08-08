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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DeudaActivity extends AppCompatActivity {
    ListView ListViewDeudas;
    Button btnRedirigirAAgregar;
    Intent intent;
    BaseDeDatos baseDeDatos;
    ArrayAdapter<String> adapter;

    // ----------------
    ArrayList<String> listaDeudas = new ArrayList<>();
    ArrayList<String> listaDeudasId = new ArrayList<>();
    ArrayList<String> listaDeudasDeudor = new ArrayList<>();
    ArrayList<String> listaDeudasDeudaMonto = new ArrayList<>();
    // ----------------
    ArrayList<String> listaDeudasMostrar = new ArrayList<>();

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
        getDatosDeuda();
        funcionesListaView();

        btnRedirigirAAgregar.setOnClickListener(v -> {
            intent = new Intent(this, AgregarDeudaActivity.class);
            startActivity(intent);
            finish();
        });
    }
    public void InicializarVariables(){
        try {
            baseDeDatos = new BaseDeDatos(this);
            ListViewDeudas = findViewById(R.id.listaDeudas);
            btnRedirigirAAgregar = findViewById(R.id.btnEnviarAgregarDeuda);
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void getDatosDeuda(){
        listaDeudas.clear();
        listaDeudasId.clear();
        listaDeudasDeudor.clear();
        listaDeudasDeudaMonto.clear();
        listaDeudasMostrar.clear();
        ArrayList<String> datosDeuda = baseDeDatos.getDeudas();
        if (datosDeuda.size() == 0){
            listaDeudasMostrar.add("No hay deudas");
            listaDeudasId.add("-1");
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeudasMostrar);
            ListViewDeudas.setAdapter(adapter);
            //Actualizar lista
            adapter.notifyDataSetChanged();
        }
        else{
            // "id-titulo

            for (String dato : datosDeuda){
                String[] datosFila = dato.split("-");
                listaDeudas.add(datosFila[1]);
                listaDeudasId.add(datosFila[0]);
                listaDeudasDeudor.add(datosFila[3]);
                listaDeudasDeudaMonto.add(datosFila[2]);

                listaDeudasMostrar.add(  datosFila[3] + ": " + datosFila[1]);// + " - " + datosFila[2]
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeudasMostrar);
            ListViewDeudas.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }
    public void getDatosDeuda(ArrayList<String> datosDeuda){
        listaDeudas.clear();
        listaDeudasId.clear();
        listaDeudasDeudor.clear();
        listaDeudasDeudaMonto.clear();
        listaDeudasMostrar.clear();
        if (datosDeuda.size() == 0){
            listaDeudasMostrar.add("No hay deudas");
            listaDeudasId.add("-1");
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeudasMostrar);
            ListViewDeudas.setAdapter(adapter);
            //Actualizar lista
            adapter.notifyDataSetChanged();
        }
        else{
            // "id-titulo

            for (String dato : datosDeuda){
                String[] datosFila = dato.split("-");
                listaDeudas.add(datosFila[1]);
                listaDeudasId.add(datosFila[0]);
                listaDeudasDeudor.add(datosFila[3]);
                listaDeudasDeudaMonto.add(datosFila[2]);

                listaDeudasMostrar.add(  datosFila[3] + ": " + datosFila[1]);// + " - " + datosFila[2]
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeudasMostrar);
            ListViewDeudas.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }
    public void funcionesListaView(){
        // hacer un if si el primer elemento es -1 no hacer nada
        if (listaDeudasId.get(0).equals("-1")){
            return;
        }
        else
        {
            ListViewDeudas.setOnItemClickListener((parent, view, position, id) -> {
                //Toast.makeText(this, "Redirigir: " + listaDeudas.get(position), Toast.LENGTH_SHORT).show();
                intent = new Intent(this, DatosDeudaActivity.class);
                intent.putExtra("idDeuda", listaDeudasId.get(position));
                intent.putExtra("nombreDeudor", listaDeudasDeudor.get(position));
                startActivity(intent);
                finish();
            });
            // al mantener precionado el iten hacer un toast
            ListViewDeudas.setOnItemLongClickListener((parent, view, position, id) -> {
                //Toast.makeText(this, "Eliminar: " + listaDeudas.get(position), Toast.LENGTH_SHORT).show();
                return false;
            });
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();

    }
}