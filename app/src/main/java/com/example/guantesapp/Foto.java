package com.example.guantesapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Foto {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id_foto")
    private String id_foto;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] img;

    @NonNull
    public String getId_foto() {
        return id_foto;
    }

    public void setId_foto(@NonNull String id_foto) {
        this.id_foto = id_foto;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

}
