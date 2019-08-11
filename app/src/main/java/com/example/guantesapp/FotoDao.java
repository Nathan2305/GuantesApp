package com.example.guantesapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface FotoDao {
    @Insert
    void insertFoto(Foto... fotos);

    @Query("SELECT *FROM Foto WHERE Foto.id_foto=:pk_to_insert")
    Foto existPKFoto(String pk_to_insert);

    @Query("SELECT *FROM Foto")
    List<Foto> getAllFoto();
}
