package com.example.guantesapp.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class UserMessageChatRoom {

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "messageType")
    private String messageType;

    @ColumnInfo(name = "statusChat")
    private boolean statusChat;

    @ColumnInfo(name = "identifier")
    private String identifier;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isStatusChat() {
        return statusChat;
    }

    public void setStatusChat(boolean statusChat) {
        this.statusChat = statusChat;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
