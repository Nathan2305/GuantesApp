package com.example.guantesapp.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ModeloUrlRoomDB {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id" )
    private int id;

    @ColumnInfo(name="modelo" )
    private String modelo;

    @ColumnInfo(name="foto_url" )
    private String foto_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }
}
