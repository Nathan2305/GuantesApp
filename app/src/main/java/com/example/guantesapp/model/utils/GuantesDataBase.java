package com.example.guantesapp.model.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.guantesapp.model.entities.MRoomDB;
import com.example.guantesapp.model.entities.MRoomTallaCantidad;
import com.example.guantesapp.model.entities.MRoomUrlDB;

@Database(entities = {MRoomDB.class, MRoomUrlDB.class, MRoomTallaCantidad.class}, version = 1)
public abstract class GuantesDataBase extends RoomDatabase {
    private static GuantesDataBase instancia, instancia2, instancia3, instancia4;

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

    public static GuantesDataBase newInstance3(Context context) {
        if (instancia3 == null) {
            instancia3 = Room.databaseBuilder(context, GuantesDataBase.class, "listaTallaCantidad.db").build();
        }
        return instancia3;
    }

    public abstract GuanteInfoDao getGuantesInfoDao();
}
