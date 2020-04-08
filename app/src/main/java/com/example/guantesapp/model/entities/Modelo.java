package com.example.guantesapp.model.entities;


import java.util.Date;

public class Modelo extends Guante {

    private String objectId;
    private Date created;
    private String modelo;
    private String foto_url;
    private boolean isChecked;
    public Modelo(String modelo) {
        this.modelo = modelo;
    }

    public Modelo() {

    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getModelo() {
        return modelo;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
