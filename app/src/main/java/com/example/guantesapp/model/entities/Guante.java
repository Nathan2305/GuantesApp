package com.example.guantesapp.model.entities;

public class Guante {
    private String objectId;
    private String name;

    public Guante(String name) {
        this.name = name;
    }

    public Guante() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
