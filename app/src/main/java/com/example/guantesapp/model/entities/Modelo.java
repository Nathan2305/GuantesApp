package com.example.guantesapp.model.entities;


public class Modelo extends Guante {

    private String objectId;
    private String modelo;
    private String foto_url;

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
}
