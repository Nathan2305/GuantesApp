package com.example.guantesapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Modelo.class,Foto.class,Talla.class},version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private static AppDataBase instance_modelo = null;
    private static AppDataBase instance_foto = null;
    private static AppDataBase instance_talla = null;

    public static AppDataBase getInstanceModeloBD(Context context) {

        if (instance_modelo == null) {
            instance_modelo = Room.databaseBuilder(context, AppDataBase.class, "modelo.db").build();
        }
        return instance_modelo;
    }

    public static AppDataBase getInstanceFotoBD(Context context) {
        if (instance_foto == null) {
            instance_foto = Room.databaseBuilder(context, AppDataBase.class, "foto.db").build();
        }
        return instance_foto;
    }

    public static AppDataBase getInstanceTallaBD(Context context) {
        if (instance_talla == null) {
            instance_talla = Room.databaseBuilder(context, AppDataBase.class, "talla.db").build();
        }
        return instance_talla;
    }

    public abstract ModeloDao getModeloDao();
    public abstract FotoDao getFotoDao();
    public abstract TallaDao getTallaDao();
}
