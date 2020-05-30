package com.example.guantesapp.model.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class UserChatRoom {

    @NotNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long id;


    @ColumnInfo(name = "email")
    private String email;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
