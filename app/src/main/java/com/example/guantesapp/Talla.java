package com.example.guantesapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Talla {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id_talla")
    private String id_talla;

    @ColumnInfo(name = "talla")
    private String talla;

    @NonNull
    public String getId_talla() {
        return id_talla;
    }

    public void setId_talla(@NonNull String id_talla) {
        this.id_talla = id_talla;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }
}
