package com.example.gestordedeudas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
    private static final String COLUMNA_CREACION_DEUDAS = "creacion";
    private static final String COLUMNA_FINALIZACION_DEUDAS = "finalizacion";
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
                + COLUMNA_CREACION_DEUDAS + " TEXT, "
                + COLUMNA_FINALIZACION_DEUDAS + " TEXT, "
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí puedes manejar la lógica para actualizar la base de datos si cambias su estructura en el futuro.
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_HISTORIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_DEUDAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PERSONAS);
        onCreate(db);
    }
}
