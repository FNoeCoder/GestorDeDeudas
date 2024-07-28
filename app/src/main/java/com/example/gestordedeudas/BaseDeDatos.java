package com.example.gestordedeudas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

public class BaseDeDatos extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "deudas.db";
    private static final int VERSION_BD = 1;

    private static final String TABLA_PERSONAS = "personas";
    private static final String COLUMNA_ID_PERSONAS = "id";
    private static final String COLUMNA_NOMBRE_PERSONAS = "nombre";

    private static final String TABLA_DEUDAS = "deudas";
    private static final String COLUMNA_ID_DEUDAS = "id";
    private static final String COLUMNA_TITULO_DEUDAS = "titulo";
    private static final String COLUMNA_DESCRIPCION_DEUDAS = "descripcion";
    private static final String COLUMNA_CANTIDAD_DEUDAS = "cantidad";
    private static final String COLUMNA_ESTADO_DEUDAS = "estado";
    private static final String COLUMNA_PERSONAS_ID_DEUDAS = "personas_id";

    private static final String TABLA_HISTORIAL = "historial";
    private static final String COLUMNA_ID_HISTORIAL = "id";
    private static final String COLUMNA_CREACION_HISTORIAL = "creacion";
    private static final String COLUMNA_DESCRIPCION_HISTORIAL = "descripcion";
    private static final String COLUMNA_CANTIDAD_PAGA_HISTORIAL = "cantidad_paga";
    private static final String COLUMNA_DEUDAS_ID_HISTORIAL = "deudas_id";
    private static final String COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL = "deudas_personas_id";

    public BaseDeDatos(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_PERSONAS + " ("
                + COLUMNA_ID_PERSONAS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_NOMBRE_PERSONAS + " TEXT UNIQUE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_DEUDAS + " ("
                + COLUMNA_ID_DEUDAS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_TITULO_DEUDAS + " TEXT, "
                + COLUMNA_DESCRIPCION_DEUDAS + " TEXT, "
                + COLUMNA_CANTIDAD_DEUDAS + " FLOAT, "
                + COLUMNA_ESTADO_DEUDAS + " TEXT, "
                //+ COLUMNA_CREACION_DEUDAS + " TEXT, "
                //+ COLUMNA_FINALIZACION_DEUDAS + " TEXT, "
                + COLUMNA_PERSONAS_ID_DEUDAS + " INTEGER, "
                + "FOREIGN KEY(" + COLUMNA_PERSONAS_ID_DEUDAS + ") REFERENCES " + TABLA_PERSONAS + "(" + COLUMNA_ID_PERSONAS + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_HISTORIAL + " ("
                + COLUMNA_ID_HISTORIAL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_CREACION_HISTORIAL + " TEXT, "
                + COLUMNA_DESCRIPCION_HISTORIAL + " TEXT, "
                + COLUMNA_CANTIDAD_PAGA_HISTORIAL + " FLOAT, "
                + COLUMNA_DEUDAS_ID_HISTORIAL + " INTEGER, "
                + COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL + " INTEGER, "
                + "FOREIGN KEY(" + COLUMNA_DEUDAS_ID_HISTORIAL + ") REFERENCES " + TABLA_DEUDAS + "(" + COLUMNA_ID_DEUDAS + "), "
                + "FOREIGN KEY(" + COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL + ") REFERENCES " + TABLA_DEUDAS + "(" + COLUMNA_PERSONAS_ID_DEUDAS + "))");
    }

    public ArrayList<String> getPersonas(){
        // "id-nombre"
        ArrayList<String> personas = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_ID_PERSONAS, COLUMNA_NOMBRE_PERSONAS};

        Cursor cursor = db.query(TABLA_PERSONAS, columnas, null, null, null, null, COLUMNA_NOMBRE_PERSONAS);

        if(cursor.moveToFirst()){
            do{
                personas.add(cursor.getInt(0) + "-" + cursor.getString(1));
            }while(cursor.moveToNext());

            cursor.close();
        }

        return personas;
    }
    public void agregarPersona(String nombre){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLA_PERSONAS + " (" + COLUMNA_NOMBRE_PERSONAS + ") VALUES ('" + nombre + "')");
    }
    public void eliminarPersona(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLA_PERSONAS + " WHERE " + COLUMNA_ID_PERSONAS + " = " + id);
    }
    public boolean existePersona(String nombre){
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_ID_PERSONAS};
        String[] args = {nombre};
        Cursor cursor = db.query(TABLA_PERSONAS, columnas, COLUMNA_NOMBRE_PERSONAS + " = ?", args, null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }
    //obtener todas las deudas y la persona que las tiene

    public void setDeudaNueva(String titulo, String descripcion, float cantidad, float aporte, int idDeudor){
        // el estado cuando se crea una deuda es pendiente
        String estado = "pendiente";
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("INSERT INTO " + TABLA_DEUDAS + " (" + COLUMNA_TITULO_DEUDAS + ", " + COLUMNA_DESCRIPCION_DEUDAS + ", " + COLUMNA_CANTIDAD_DEUDAS + ", " + COLUMNA_PERSONAS_ID_DEUDAS + ", " + COLUMNA_ESTADO_DEUDAS + ") VALUES ('" + titulo + "', '" + descripcion + "', " + cantidad + ", " + idDeudor  + ", '" + estado + "')");
        // obtener el id de la deuda recién creada
        String[] columnas = {COLUMNA_ID_DEUDAS};
        String[] args = {titulo};
        Cursor cursor = db.query(TABLA_DEUDAS, columnas, COLUMNA_TITULO_DEUDAS + " = ?", args, null, null, null);
        int idDeuda = 0;
        if(cursor.moveToFirst()){
            idDeuda = cursor.getInt(0);
        }
        cursor.close();
        setHistorialCreacion(aporte, idDeuda, idDeudor);
    }
    public void setHistorialCreacion (float cantidad, int idDeuda, int idDeudor){
        // la fecha de creacion es la fecha con el formato de dia mes año por ejemplo: 01/01/2023
        Date date = new java.util.Date();
        String fecha = date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
        String descripcion = cantidad < 1 ? "Creacion de deuda" : "Creacion de deuda con monto de " + cantidad;

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("INSERT INTO " + TABLA_HISTORIAL + " (" + COLUMNA_CREACION_HISTORIAL + ", " + COLUMNA_DESCRIPCION_HISTORIAL + ", " + COLUMNA_CANTIDAD_PAGA_HISTORIAL + ", " + COLUMNA_DEUDAS_ID_HISTORIAL + ", " + COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL + ") VALUES ('" + fecha + "', '" + descripcion + "', " + cantidad + ", " + idDeuda + ", " + idDeudor + ")");
    }
    public void setHistorialPago (float cantidad, int idDeuda, int idDeudor){
        // la fecha de creacion es la fecha con el formato de dia mes año por ejemplo:
        Date date = new java.util.Date();
        String fecha = date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
        String descripcion = "Aporte de " + cantidad;

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLA_HISTORIAL + " (" + COLUMNA_CREACION_HISTORIAL + ", " + COLUMNA_DESCRIPCION_HISTORIAL + ", " + COLUMNA_CANTIDAD_PAGA_HISTORIAL + ", " + COLUMNA_DEUDAS_ID_HISTORIAL + ", " + COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL + ") VALUES ('" + fecha + "', '" + descripcion + "', " + cantidad + ", " + idDeuda + ", " + idDeudor + ")");
        db.close();
    }
    public void setHistorialFinalizacion (float cantidad, int idDeuda, int idDeudor){
        // la fecha de creacion es la fecha con el formato de dia mes año por ejemplo:
        Date date = new java.util.Date();
        String fecha = date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
        String descripcion = "Finalización de deuda con monto de " + cantidad;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLA_HISTORIAL + " (" + COLUMNA_CREACION_HISTORIAL + ", " + COLUMNA_DESCRIPCION_HISTORIAL + ", " + COLUMNA_CANTIDAD_PAGA_HISTORIAL + ", " + COLUMNA_DEUDAS_ID_HISTORIAL + ", " + COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL + ") VALUES ('" + fecha + "', '" + descripcion + "', " + cantidad + ", " + idDeuda + ", " + idDeudor + ")");
        //poner el estado de la deuda en finalizada
        setEstadoDeudaFinalizada(idDeuda);
        db.close();
    }
    public String getNombreDeudor(int idDeudor){
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {COLUMNA_NOMBRE_PERSONAS};
        String[] args = {String.valueOf(idDeudor)};
        Cursor cursor = db.query(TABLA_PERSONAS, columnas, COLUMNA_ID_PERSONAS + " = ?", args, null, null, null);

        String nombre = "";
        if(cursor.moveToFirst()){
            nombre = cursor.getString(0);
        }
        cursor.close();
        return nombre;
    }
    public int getIdDeudor(String nombreDeudor){
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_ID_PERSONAS};
        String[] args = {nombreDeudor};
        Cursor cursor = db.query(TABLA_PERSONAS, columnas, COLUMNA_NOMBRE_PERSONAS + " = ?", args, null, null, null);
        int id = 0;
        if(cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
    public boolean existeDeuda(String tituloDeuda){
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_TITULO_DEUDAS };
        String[] args = {tituloDeuda};
        Cursor cursor = db.query(TABLA_DEUDAS, columnas, COLUMNA_TITULO_DEUDAS + " = ?", args, null, null, null);
        boolean existe = cursor.getCount() > 0;

        cursor.close();

        return existe;
    }
    public String[] getDatosDeuda(int idDeuda){
        // [titulo, descripcion, monto, nombreDeudor, estado, totalPagado]
        String[] datos = new String[6];

        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_TITULO_DEUDAS, COLUMNA_DESCRIPCION_DEUDAS, COLUMNA_CANTIDAD_DEUDAS, COLUMNA_PERSONAS_ID_DEUDAS, COLUMNA_ESTADO_DEUDAS};
        String[] args = {String.valueOf(idDeuda)};
        Cursor cursor = db.query(TABLA_DEUDAS, columnas, COLUMNA_ID_DEUDAS + " = ?", args, null, null, null);
        if (cursor.moveToFirst()) {
            datos[0] = cursor.getString(0);
            datos[1] = cursor.getString(1);
            datos[2] = getNumeroSinDecimales(String.valueOf(cursor.getFloat(2)));
            datos[3] = getNombreDeudor(cursor.getInt(3));
            datos[4] = cursor.getString(4);
            datos[5] = getTotalPagado(idDeuda);
            cursor.close();
            return datos;
        } else {
            cursor.close();
            return null;
        }
    }
    public ArrayList<String> getHistorialDeuda(int idDeuda){
        ArrayList<String> historial = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_CREACION_HISTORIAL, COLUMNA_DESCRIPCION_HISTORIAL, COLUMNA_CANTIDAD_PAGA_HISTORIAL};
        String[] args = {String.valueOf(idDeuda)};
        Cursor cursor = db.query(TABLA_HISTORIAL, columnas, COLUMNA_DEUDAS_ID_HISTORIAL + " = ?", args, null, null, null);

        if(cursor.moveToFirst()){
            do{
                historial.add(cursor.getString(0) + "-" + cursor.getString(1));
            }while(cursor.moveToNext());
            cursor.close();
            return historial;
        }else{
            cursor.close();
            return null;
        }
    }
    public String getTotalPagado(int idDeuda){
        float total = 0;
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_CANTIDAD_PAGA_HISTORIAL};
        String[] args = {String.valueOf(idDeuda)};
        Cursor cursor = db.query(TABLA_HISTORIAL, columnas, COLUMNA_DEUDAS_ID_HISTORIAL + " = ?", args, null, null, null);
        if(cursor.moveToFirst()){
            do{
                total += cursor.getFloat(0);
            }while(cursor.moveToNext());
            cursor.close();
            return getNumeroSinDecimales(String.valueOf(total));
        }else{
            cursor.close();
            return String.valueOf(total);
        }
    }
    public String getNumeroSinDecimales(String numero){
        if(numero.contains(".")){
            return getNumeroSeparado(numero.substring(0, numero.indexOf(".")));
        }else{
            return getNumeroSeparado(numero);
        }
    }
    public String getNumeroSeparado(String numero){
        // que esté separado por comas cada 3 dígitos
        String numeroSeparado = "";

        int contador = 0;

        for(int i = numero.length() - 1; i >= 0; i--){
            numeroSeparado = numero.charAt(i) + numeroSeparado;
            contador++;
            if(contador == 3 && i != 0){
                numeroSeparado = "," + numeroSeparado;
                contador = 0;
            }
        }
        return numeroSeparado;
    }
    public void eliminarDeuda(int idDeuda){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLA_DEUDAS + " WHERE " + COLUMNA_ID_DEUDAS + " = " + idDeuda);
    }
    public void setEstadoDeudaFinalizada(int idDeuda){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLA_DEUDAS + " SET " + COLUMNA_ESTADO_DEUDAS + " = 'finalizada' WHERE " + COLUMNA_ID_DEUDAS + " = " + idDeuda);
        db.close();
    }
    public ArrayList<String> getDeudas() {
        // "id-titulo-monto-nombreDeudor"
        ArrayList<String> deudas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_ID_DEUDAS, COLUMNA_TITULO_DEUDAS, COLUMNA_CANTIDAD_DEUDAS, COLUMNA_PERSONAS_ID_DEUDAS};

        Cursor cursor = db.query(TABLA_DEUDAS, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String numeroSinDecimales = getNumeroSinDecimales(String.valueOf(cursor.getFloat(2)));
                deudas.add(
                        cursor.getInt(0) + "-" +
                                cursor.getString(1) + "-" +
                                numeroSinDecimales + "-" +
                                getNombreDeudor(cursor.getInt(3))
                );
            }while(cursor.moveToNext());
            cursor.close();
        }
        return deudas;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí puedes manejar la lógica para actualizar la base de datos si cambias su estructura en el futuro.
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_HISTORIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_DEUDAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PERSONAS);
        onCreate(db);
    }
}
