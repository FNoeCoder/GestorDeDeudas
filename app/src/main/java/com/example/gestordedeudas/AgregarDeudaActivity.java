package com.example.gestordedeudas;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AgregarDeudaActivity extends AppCompatActivity {
    Spinner spinnerPersonas;
    EditText inputTituloDeuda;
    EditText inputDescripcionDeuda;
    EditText inputMontoDeuda;
    EditText inputAporteInicial;
    Button btnAgregarDeuda;
    Button btnLimpiar;
    Intent intent;

    //-----------------------------------------------
    ArrayList<String> personas = new ArrayList<>();
    ArrayList<String> personasId = new ArrayList<>();
    //-----------------------------------------------
    BaseDeDatos baseDeDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_deuda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        baseDeDatos = new BaseDeDatos(this);
        InicializarVariables();
        obtenerPersonas();
        ponerDatosSpinner();

        btnLimpiar.setOnClickListener(v -> {
            spinnerPersonas.setSelection(0);
            inputTituloDeuda.setText("");
            inputDescripcionDeuda.setText("");
            inputMontoDeuda.setText("");
            inputAporteInicial.setText("");
        });
        btnAgregarDeuda.setOnClickListener(v -> {
            //agregarDeuda();
            if (agregarDeuda()){
                intent = new Intent(this, DeudaActivity.class);
                startActivity(intent);
                finish();
            }


        });
    }
    public void InicializarVariables(){
        spinnerPersonas = findViewById(R.id.spinnerPersonas);
        inputTituloDeuda = findViewById(R.id.inputTituloDeuda);
        inputDescripcionDeuda = findViewById(R.id.inputDescripcionDeuda);
        inputMontoDeuda = findViewById(R.id.inputMontoDeuda);
        inputAporteInicial = findViewById(R.id.inputAporteInicial);
        btnAgregarDeuda = findViewById(R.id.btnGuardarDeuda);
        btnLimpiar = findViewById(R.id.btnLimpiar);
    }
    public void obtenerPersonas(){
        personas.clear();
        personasId.clear();
        ArrayList<String> personasBD = baseDeDatos.getPersonas();
        // el string es "id-personaNombre"
        if (personasBD.size() > 0){
            personas.add("Seleccione una persona");
            personasId.add("-1");
            for (int i = 0; i < personasBD.size(); i++){
                personas.add(personasBD.get(i).split("-")[1]);
                personasId.add(personasBD.get(i).split("-")[0]);
            }
        }
        else {
            personas.add("No hay personas registradas");
            personasId.add("-1");
        }
    }
    public void ponerDatosSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, personas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPersonas.setAdapter(adapter);
    }
    public boolean agregarDeuda(){
        if (spinnerPersonas.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Debe seleccionar una persona", Toast.LENGTH_SHORT).show();
            return false;
        }
        // si el titulo de la deuda esta vacio
        else if (inputTituloDeuda == null || inputTituloDeuda.getText().toString().isEmpty()){
            Toast.makeText(this, "Debe ingresar el titulo de la deuda", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (inputMontoDeuda == null || inputMontoDeuda.getText().toString().isEmpty()){
            Toast.makeText(this, "Debe ingresar el monto de la deuda", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (inputAporteInicial == null || inputAporteInicial.getText().toString().isEmpty()){
            Toast.makeText(this, "Debe ingresar el aporte inicial", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (parseInt(inputAporteInicial.getText().toString()) > parseInt(inputMontoDeuda.getText().toString())){
            Toast.makeText(this, "El aporte inicial no puede ser mayor al monto de la deuda", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (baseDeDatos.existeDeuda(inputTituloDeuda.getText().toString())){
            Toast.makeText(this, "Ya existe una deuda con ese titulo", Toast.LENGTH_SHORT).show();
            return false;
        }
        //si el monto es igual a la deuda
        else if (parseInt(inputMontoDeuda.getText().toString()) == parseInt(inputAporteInicial.getText().toString())){
            Toast.makeText(this, "La deuda no puede ser igual al aporte inicial", Toast.LENGTH_SHORT).show();
            return false;
        }
        //si el aporte inicial es menor al se agrega a la base de datos
        else if (parseInt(inputAporteInicial.getText().toString()) < parseInt(inputMontoDeuda.getText().toString())){
            try {
                int idPersona = parseInt(personasId.get(spinnerPersonas.getSelectedItemPosition()));
                String tituloDeuda = inputTituloDeuda.getText().toString();
                String descripcionDeuda = inputDescripcionDeuda.getText().toString();
                float montoDeuda = Float.parseFloat(inputMontoDeuda.getText().toString());
                float aporteInicial = Float.parseFloat(inputAporteInicial.getText().toString());
                baseDeDatos.setDeudaNueva(tituloDeuda, descripcionDeuda, montoDeuda, aporteInicial, idPersona);
                return true;
            }
            catch (Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(e.getMessage());
                builder.setTitle("Error");
                builder.show();
                return false;
            }
        }
        else {
            return false;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();

    }
}