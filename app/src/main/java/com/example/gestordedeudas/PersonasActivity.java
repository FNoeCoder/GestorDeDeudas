package com.example.gestordedeudas;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class PersonasActivity extends AppCompatActivity {
    ListView ListViewPersonas;
    EditText inputNombre;
    Button btnAgregarPersona;
    ArrayAdapter<String> adapter;
    ArrayList<String> personasNombres;
    ArrayList<String> personasIDs;
    BaseDeDatos baseDeDatos;
    private static final String exprecionRegularNombre = "^[a-zA-Z]{3,}((\\ )[a-zA-Z]{3,})*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarVariables();
        //id->nombre
        obtenerDatos();

        crearFuncionEliminar();
        btnAgregarPersona.setOnClickListener(v -> {
            String nombre = inputNombre.getText().toString();
            if (baseDeDatos.existePersona(nombre)){
                Toast.makeText(this, "Nombre ya existe", Toast.LENGTH_SHORT).show();
            }
            else if (nombre.matches(exprecionRegularNombre)){
                baseDeDatos.agregarPersona(nombre);
                obtenerDatos();
                crearFuncionEliminar();
                inputNombre.setText("");
            }
            else {
                Toast.makeText(this, "Nombre inválido", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void inicializarVariables(){
        ListViewPersonas = findViewById(R.id.listaPersonas);
        inputNombre = findViewById(R.id.inputNombre);
        btnAgregarPersona = findViewById(R.id.btnAgregarPersona);
        personasNombres = new ArrayList<>();
        personasIDs = new ArrayList<>();
        baseDeDatos = new BaseDeDatos(this);
    }
    public void obtenerDatos(){
        personasNombres.clear();
        personasIDs.clear();
        ArrayList<String> personas = baseDeDatos.getPersonas();
        if (personas.size() > 0){
            for (String persona : personas) {
                String[] datos = persona.split("-");
                personasIDs.add(datos[0]);
                personasNombres.add(datos[1]);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personasNombres);
            ListViewPersonas.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else {
            personasNombres.add("No hay personas");
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personasNombres);
            ListViewPersonas.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    public void crearFuncionEliminar(){
        if (personasIDs.size() > 0) {
            ListViewPersonas.setOnItemLongClickListener((parent, view, position, id) -> {
                // obtener el id de la persona y convertirla a entero
                int personaID = Integer.parseInt(personasIDs.get(position));
                String personaNombre = personasNombres.get(position);

                // hacer un dialog para preguntar si se quiere eliminar
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Eliminar persona");
                builder.setMessage("¿Desea eliminar a " + personaNombre + "?, se eliminará todo su contenido");
                builder.setPositiveButton("Eliminar", (dialog, which) -> {
                    if (baseDeDatos.personaTieneDeudas(personaID)){
                        Toast.makeText(this, "Esta persona tiene deudas", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        baseDeDatos.eliminarPersona(personaID);
                        obtenerDatos();
                    }

                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();

                return true;
            });

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();

    }
}