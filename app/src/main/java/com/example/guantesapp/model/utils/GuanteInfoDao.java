package com.example.guantesapp.model.utils;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.guantesapp.model.entities.ModeloRoomDB;
import com.example.guantesapp.model.entities.ModeloUrlRoomDB;

import java.util.List;

@Dao
public interface GuanteInfoDao {

    @Insert
    long insertModeloRoom(ModeloRoomDB modeloRoomDB);

    @Insert
    long insertModeloUrlRoom(ModeloUrlRoomDB modeloUrlRoomDB);

    @Query("SELECT orden FROM ModeloRoomDB WHERE id =:aux_nombre")
    int getOrdenById(String aux_nombre);

    @Update
    void updateOrden(ModeloRoomDB modeloRoomDB);

    @Query("SELECT foto_url FROM ModeloUrlRoomDB WHERE modelo=:aux_nombre")
    String getFotoUrlByName(String aux_nombre);
}
