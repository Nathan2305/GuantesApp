package com.example.guantesapp.model.utils;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.guantesapp.model.entities.MRoomDB;
import com.example.guantesapp.model.entities.MRoomTallaCantidad;
import com.example.guantesapp.model.entities.MRoomUrlDB;

import java.util.List;

@Dao
public interface GuanteInfoDao {

    @Insert
    long insertModeloRoom(MRoomDB mRoomDB);

    @Insert
    long insertModeloUrlRoom(MRoomUrlDB mRoomUrlDB);

    @Query("SELECT orden FROM MRoomDB WHERE id =:aux_nombre")
    int getOrdenById(String aux_nombre);

    @Update
    void updateOrden(MRoomDB mRoomDB);

    @Update
    void updateTallaCantidad(MRoomTallaCantidad mRoomTallaCantidad);

    @Query("SELECT foto_url FROM MRoomUrlDB WHERE modelo=:aux_nombre")
    String getFotoUrlByName(String aux_nombre);


    /*@Query("SELECT cantidad FROM MRoomTallaCantidad WHERE talla=:aux_talla")
    String getCantidadByTalla(String aux_talla);*/

    @Insert
    long insertTallaCantidad(MRoomTallaCantidad mRoomTallaCantidad);


    @Query("SELECT *FROM MRoomTallaCantidad")
    List<MRoomTallaCantidad> getIdTallaCantidad();

    @Query("SELECT cantidad FROM MRoomTallaCantidad WHERE modelo=:aux_modelo AND talla=:aux_talla")
    int getCantidad(String aux_modelo, String aux_talla);
  }
