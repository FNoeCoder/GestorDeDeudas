package com.example.gestordedeudas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DatosDeudaActivity extends AppCompatActivity {
    int idDeuda;
    int idDeudor;
    TextView tituloDeuda;
    TextView nombreDeudor;
    TextView descripcionDeuda;
    TextView montoDeuda;
    TextView montoPagado;
    ListView listaHistorial;
    EditText inputAporte;
    Button btnAportar;
    Button btnEliminarDeuda;
    Button btnSaldarDeuda;
    Intent intent;
    BaseDeDatos baseDeDatos;

    String tituloDeudaBD;
    String descripcionDeudaBD;
    String montoDeudaBD;
    String nombreDeudorBD;
    String deudaEstadoBD;
    String montoPagadoBD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_deuda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarVariables();
        obtenerDatosDeuda();
        eventosBotones();
    }
    public void inicializarVariables(){
        intent = getIntent();
        //idDeuda = intent.getStringExtra("idDeuda");
        tituloDeuda = findViewById(R.id.tvTituloDeuda);
        nombreDeudor = findViewById(R.id.tvDeudor);
        descripcionDeuda = findViewById(R.id.tvDescripcion);
        montoDeuda = findViewById(R.id.tvMontoTotal);
        montoPagado = findViewById(R.id.tvMontoPagado);
        listaHistorial = findViewById(R.id.listViewHistorial);
        inputAporte = findViewById(R.id.inputAporte);
        btnAportar = findViewById(R.id.btnAgregarAporte);
        btnEliminarDeuda = findViewById(R.id.btnEliminarDeuda);
        btnSaldarDeuda = findViewById(R.id.btnSaldarDeuda);
        baseDeDatos = new BaseDeDatos(this);
    }
    public void obtenerDatosDeuda() {
        // titulo -> posicion 0 - puesto
        // descripcion -> posicion 1 - puesto
        // montoTotal -> posicion 2 - puesto
        // nombreDeudor -> posicion 3 - no se usa
        // deudaEstado -> posicion 4 - no se usa
        // montoPagado -> posicion 5 - puesto
        idDeuda = Integer.parseInt(intent.getStringExtra("idDeuda"));
        String nomnbreDeudor = intent.getStringExtra("nombreDeudor");
        idDeudor = baseDeDatos.getIdDeudor(nomnbreDeudor);


        String[] datosDeuda = baseDeDatos.getDatosDeuda(idDeuda);

        tituloDeudaBD = datosDeuda[0];
        descripcionDeudaBD = datosDeuda[1];
        montoDeudaBD = datosDeuda[2];
        nombreDeudorBD = datosDeuda[3];
        deudaEstadoBD = datosDeuda[4];
        montoPagadoBD = datosDeuda[5];

        tituloDeuda.setText(tituloDeudaBD);
        descripcionDeuda.setText(descripcionDeudaBD.isEmpty() ? "Sin descripción" : descripcionDeudaBD);
        montoDeuda.setText(getString(R.string.totalDeuda) + "   " + montoDeudaBD);
        nombreDeudor.setText(nombreDeudorBD);
        montoPagado.setText(getString(R.string.totalAportes) + "   " +montoPagadoBD);

        ArrayList<String> historial = baseDeDatos.getHistorialDeuda(idDeuda);
        ArrayAdapter adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historial);
        listaHistorial.setAdapter(adaptador);
        adaptador.notifyDataSetChanged();
    }
    public void eventosBotones(){

        btnAportar.setOnClickListener(view -> {
            if (inputAporte.getText().toString().isEmpty()) {
                Toast.makeText(this, "Ingrese un monto", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(inputAporte.getText().toString()) > Double.parseDouble(montoDeudaBD) - Double.parseDouble(montoPagadoBD)) {
                Toast.makeText(this, "El monto ingresado es mayor al monto de la deuda", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(inputAporte.getText().toString()) <= 0) {
                Toast.makeText(this, "El monto ingresado debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            // si la deuda está finalizada no se puede agregar un aporte
            }else if (deudaEstadoBD.equals("finalizada")) {
                Toast.makeText(this, "La deuda ya está finalizada", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(inputAporte.getText().toString()) == Double.parseDouble(montoDeudaBD) - Double.parseDouble(montoPagadoBD)) {
                baseDeDatos.setHistorialFinalizacion(Float.parseFloat(inputAporte.getText().toString()), idDeuda, idDeudor);
                inputAporte.setText("");
                obtenerDatosDeuda();
                Toast.makeText(this, "Deuda saldada", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(inputAporte.getText().toString()) < Double.parseDouble(montoDeudaBD) - Double.parseDouble(montoPagadoBD)) {
                baseDeDatos.setHistorialPago(Float.parseFloat(inputAporte.getText().toString()), idDeuda, idDeudor);
                inputAporte.setText("");
                obtenerDatosDeuda();
                Toast.makeText(this, "Aporte agregado", Toast.LENGTH_SHORT).show();
            }
        });
        btnEliminarDeuda.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Eliminar deuda");
            builder.setMessage("¿Está seguro de que desea eliminar la deuda " + tituloDeudaBD + "?");
            builder.setPositiveButton("Sí", (dialogInterface, i) -> {
                baseDeDatos.eliminarDeuda(idDeuda);
                finish();
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            builder.show();
        });
        btnSaldarDeuda.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Saldar deuda");
            builder.setMessage("¿Está seguro de que deseas saldar la deuda " + tituloDeudaBD + "?");
            builder.setPositiveButton("Sí", (dialogInterface, i) -> {
                float montoRestante = Float.parseFloat(montoDeudaBD) - Float.parseFloat(montoPagadoBD);
                baseDeDatos.setHistorialFinalizacion(montoRestante, idDeuda, idDeudor);
                obtenerDatosDeuda();
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            builder.show();
        });


    }

}