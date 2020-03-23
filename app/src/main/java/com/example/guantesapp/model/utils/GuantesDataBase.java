package com.example.guantesapp.model.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.guantesapp.model.entities.ModeloRoomDB;
import com.example.guantesapp.model.entities.ModeloUrlRoomDB;

@Database(entities = {ModeloRoomDB.class, ModeloUrlRoomDB.class}, version = 1)
public abstract class GuantesDataBase extends RoomDatabase {
    private static GuantesDataBase instancia,instancia2;

    public static GuantesDataBase newInstance(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(context, GuantesDataBase.class, "listaGuantes.db").build();
        }
        return instancia;
    }

    public static GuantesDataBase newInstance2(Context context) {
        if (instancia2 == null) {
            instancia2 = Room.databaseBuilder(context, GuantesDataBase.class, "listaGuantesModeloUrl.db").build();
        }
        return instancia2;
    }

    public abstract GuanteInfoDao getGuantesInfoDao();
}
