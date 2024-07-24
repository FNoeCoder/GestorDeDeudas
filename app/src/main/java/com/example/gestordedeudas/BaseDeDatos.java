package com.example.gestordedeudas;

import static java.lang.Float.parseFloat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

public class BaseDeDatos extends SQLiteOpenHelper {
    /* la base de datos que planeo usar
    CREATE TABLE IF NOT EXISTS `Personas` (
      `id` INT NOT NULL AUTO_INCREMENT,
      `nombre` VARCHAR(45) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC)
    ) ENGINE = InnoDB;

    CREATE TABLE IF NOT EXISTS `deudas` (
      `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
      `titulo` VARCHAR(30) NOT NULL,
      `descripcion` VARCHAR(100) NOT NULL,
      `cantidad` FLOAT UNSIGNED NOT NULL,
      `creacion` DATETIME NOT NULL,
      `finalizacion` DATETIME NOT NULL,
      `estado` VARCHAR(30) NOT NULL,
      `Personas_id` INT NOT NULL,
      PRIMARY KEY (`id`),
      INDEX `fk_deudas_Personas_idx` (`Personas_id` ASC),
      CONSTRAINT `fk_deudas_Personas`
        FOREIGN KEY (`Personas_id`)
        REFERENCES `Personas` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
    ) ENGINE = InnoDB;

    CREATE TABLE IF NOT EXISTS `historial` (
      `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
      `creacion` DATETIME NOT NULL,
      `descripcion` VARCHAR(80) NOT NULL,
      `cantidadPaga` FLOAT UNSIGNED NOT NULL,
      `deudas_id` INT UNSIGNED NOT NULL,
      `deudas_Personas_id` INT NOT NULL,
      PRIMARY KEY (`id`),
      INDEX `fk_historial_deudas1_idx` (`deudas_id`, `deudas_Personas_id`),
      CONSTRAINT `fk_historial_deudas1`
        FOREIGN KEY (`deudas_id`, `deudas_Personas_id`)
        REFERENCES `deudas` (`id`, `Personas_id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
    ) ENGINE = InnoDB;
    */

    private static String NOMBRE_BD = "deudas.db";

    private String TABLA_PERSONAS = "personas";
        private String COLUMNA_ID_PERSONAS = "id";
        private String COLUMNA_NOMBRE_PERSONAS = "nombre";

    private String TABLA_DEUDAS = "deudas";
        private String COLUMNA_ID_DEUDAS = "id";
        private String COLUMNA_TITULO_DEUDAS = "titulo";
        private String COLUMNA_DESCRIPCION_DEUDAS = "descripcion";
        private String COLUMNA_CANTIDAD_DEUDAS = "cantidad";
        private String COLUMNA_ESTADO_DEUDAS = "estado";
        private String COLUMNA_CREACION_DEUDAS = "creacion";
        private String COLUMNA_FINALIZACION_DEUDAS = "finalizacion";
        private String COLUMNA_PERSONAS_ID_DEUDAS = "personas_id";

    private String TABLA_HISTORIAL = "historial";
        private String COLUMNA_ID_HISTORIAL = "id";
        private String COLUMNA_CREACION_HISTORIAL = "creacion";
        private String COLUMNA_DESCRIPCION_HISTORIAL = "descripcion";
        private String COLUMNA_CANTIDAD_PAGA_HISTORIAL = "cantidad_paga";
        private String COLUMNA_DEUDAS_ID_HISTORIAL = "deudas_id";
        private String COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL = "deudas_personas_id";

    SQLiteDatabase db;

    public BaseDeDatos(Context context) {
        super(context, NOMBRE_BD, null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Aquí puedes crear las tablas de la base de datos.
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_PERSONAS + " (" + COLUMNA_ID_PERSONAS + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMNA_NOMBRE_PERSONAS + " TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_DEUDAS + " (" + COLUMNA_ID_DEUDAS + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMNA_TITULO_DEUDAS + " TEXT, " + COLUMNA_DESCRIPCION_DEUDAS + " TEXT, " + COLUMNA_CANTIDAD_DEUDAS + " FLOAT, " + COLUMNA_ESTADO_DEUDAS + " TEXT, " + COLUMNA_CREACION_DEUDAS + " DATETIME, " + COLUMNA_FINALIZACION_DEUDAS + " DATETIME, " + COLUMNA_PERSONAS_ID_DEUDAS + " INTEGER REFERENCES " + TABLA_PERSONAS + "(" + COLUMNA_ID_PERSONAS + "))");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_HISTORIAL + " (" + COLUMNA_ID_HISTORIAL + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMNA_CREACION_HISTORIAL + " DATETIME, " + COLUMNA_DESCRIPCION_HISTORIAL + " TEXT, " + COLUMNA_CANTIDAD_PAGA_HISTORIAL + " FLOAT, " + COLUMNA_DEUDAS_ID_HISTORIAL + " INTEGER, " + COLUMNA_DEUDAS_PERSONAS_ID_HISTORIAL + " INTEGER REFERENCES " + TABLA_DEUDAS + "(" + COLUMNA_ID_DEUDAS + ", " + COLUMNA_PERSONAS_ID_DEUDAS + "))");
    }

    public boolean insertarPersona(String nombre) {
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_PERSONAS, nombre);
        long id = db.insert(TABLA_PERSONAS, null, values);
        return id != -1;
    }
    public boolean eliminarPersona(int id) {
        //Eliminar a la persona con el id especificado y retornar true si se realizó correctamente, false en caso contrario.
        return db.delete(TABLA_PERSONAS, COLUMNA_ID_PERSONAS + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public boolean aliminarPersona(String nombre) {
        //Eliminar a la persona con el nombre especificado y retornar true si se realizó correctamente, false en caso contrario.
        return db.delete(TABLA_PERSONAS, COLUMNA_NOMBRE_PERSONAS + " = ?", new String[]{nombre}) > 0;
    }
    public boolean actualizarPersona(int id, String nombre) {
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_PERSONAS, nombre);
        return db.update(TABLA_PERSONAS, values, COLUMNA_ID_PERSONAS + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public boolean actualizarPersona(String nombre, String nuevoNombre) {
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_PERSONAS, nuevoNombre);
        return db.update(TABLA_PERSONAS, values, COLUMNA_NOMBRE_PERSONAS + " = ?", new String[]{nombre}) > 0;
    }
    public ArrayList<String> getNombresPersonas(){
        ArrayList<String> nombres = new ArrayList<>();

        Cursor cursor = db.query(TABLA_PERSONAS, new String[]{COLUMNA_NOMBRE_PERSONAS}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                nombres.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return nombres;
    }
    // funcion para insertar deuda y que la creacion este nula por que aun no se ha pagado, pero si la creacion es la fecha actual
    public boolean insertarDeuda(String titulo, String descripcion, float cantidad, String estado, int personas_id) {
        String fechaYHoraActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ContentValues values = new ContentValues();
        values.put(COLUMNA_TITULO_DEUDAS, titulo);
        values.put(COLUMNA_DESCRIPCION_DEUDAS, descripcion);
        values.put(COLUMNA_CANTIDAD_DEUDAS, cantidad);
        values.put(COLUMNA_CREACION_DEUDAS, fechaYHoraActual);
        //Se puede ingresar null en el campo finalizacion porque aun no se ha pagado
        values.put(COLUMNA_FINALIZACION_DEUDAS, "NULL");
        values.put(COLUMNA_ESTADO_DEUDAS, estado);
        values.put(COLUMNA_PERSONAS_ID_DEUDAS, personas_id);
        long id = db.insert(TABLA_DEUDAS, null, values);
        return id != -1;
    }
    public boolean eliminarDeuda(int id){
        //Eliminar a la deuda con el id especificado y retornar true si se realizó correctamente, false en caso contrario.
        return db.delete(TABLA_DEUDAS, COLUMNA_ID_DEUDAS + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public boolean eliminarTodasLasDeudasPorPersona(int personas_id){
        //Eliminar todas las deudas de la persona con el id especificado y retornar true si se realizó correctamente, false en caso contrario.
        return db.delete(TABLA_DEUDAS, COLUMNA_PERSONAS_ID_DEUDAS + " = ?", new String[]{String.valueOf(personas_id)}) > 0;
    }
    public boolean actualizarTituloDeuda(int id, String titulo){
        ContentValues values = new ContentValues();
        values.put(COLUMNA_TITULO_DEUDAS, titulo);

        return db.update(TABLA_DEUDAS, values, COLUMNA_ID_DEUDAS + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public boolean actualizarDescripcionDeuda(int id, String descripcion){
        ContentValues values = new ContentValues();
        values.put(COLUMNA_DESCRIPCION_DEUDAS, descripcion);

        return db.update(TABLA_DEUDAS, values, COLUMNA_ID_DEUDAS + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public boolean actualizarCantidadDeuda(int id, float cantidad){
        ContentValues values = new ContentValues();
        values.put(COLUMNA_CANTIDAD_DEUDAS, cantidad);

        return db.update(TABLA_DEUDAS, values, COLUMNA_ID_DEUDAS + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public ArrayList<String> getDatosDeuda(int deuda_id) {
        ArrayList<String> datos = new ArrayList<>();

        String query = "SELECT " + COLUMNA_TITULO_DEUDAS + ", " +
                COLUMNA_ID_DEUDAS + ", " +
                COLUMNA_DESCRIPCION_DEUDAS + ", " +
                COLUMNA_CANTIDAD_DEUDAS + ", " +
                COLUMNA_ESTADO_DEUDAS + ", " +
                COLUMNA_CREACION_DEUDAS + ", " +
                COLUMNA_FINALIZACION_DEUDAS + ", " +
                COLUMNA_NOMBRE_PERSONAS +
                " FROM " + TABLA_DEUDAS +
                " INNER JOIN " + TABLA_PERSONAS +
                " ON " + TABLA_DEUDAS + "." + COLUMNA_PERSONAS_ID_DEUDAS +
                " = " + TABLA_PERSONAS + "." + COLUMNA_ID_PERSONAS +
                " WHERE " + COLUMNA_ID_DEUDAS + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(deuda_id)});

        if (cursor.moveToFirst()) {
            datos.add(cursor.getString(0)); // Título
            datos.add(cursor.getString(1)); // ID
            datos.add(cursor.getString(2)); // Descripción
            datos.add(cursor.getString(3)); // Cantidad
            datos.add(cursor.getString(4)); // Estado
            datos.add(cursor.getString(5)); // Creación
            datos.add(cursor.getString(6)); // Finalización
            datos.add(cursor.getString(7)); // Nombre de la persona
        }

        cursor.close();
        return datos;
    }




    public ArrayList<String> getTitulosDeudas(String orden, String[] whereArgs){
        ArrayList<String> titulos = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT personas.id, personas.nombre, deudas.titulo, deudas.cantidad, deudas.creacion, deudas.estado ");
        queryBuilder.append("FROM personas ");
        queryBuilder.append("INNER JOIN deudas ON personas.id = deudas.personas_id ");

        if (whereArgs != null && whereArgs.length > 0) {
            switch (whereArgs[0]) {
                case "persona":
                    queryBuilder.append("WHERE personas.nombre = ? ");
                    break;
                case "estado":
                    queryBuilder.append("WHERE deudas.estado = ? ");
                    break;
                case "titulo":
                    queryBuilder.append("WHERE deudas.titulo = ? ");
                    break;
                case "menorDe":
                    queryBuilder.append("WHERE deudas.cantidad < ? ");
                    break;
                case "mayorDe":
                    queryBuilder.append("WHERE deudas.cantidad > ? ");
                    break;
                case "antesDe":
                    queryBuilder.append("WHERE deudas.creacion < ? ");
                    break;
                case "despuesDe":
                    queryBuilder.append("WHERE deudas.creacion > ? ");
                    break;
                default:
                    break;
            }
        }
        queryBuilder.append("ORDER BY ").append(orden);

        Cursor cursor = db.rawQuery(queryBuilder.toString(), whereArgs);
        if (cursor.moveToFirst()) {
            do {
                titulos.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return titulos;
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí puedes manejar la lógica para actualizar la base de datos si cambias su estructura en el futuro.
    }
}
