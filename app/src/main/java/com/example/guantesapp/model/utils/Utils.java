package com.example.guantesapp.model.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishMessageInfo;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.DataQueryBuilder;
import com.example.guantesapp.model.entities.MRoomTallaCantidad;
import com.example.guantesapp.model.entities.MRoomUrlDB;
import com.example.guantesapp.model.entities.Modelo;
import com.example.guantesapp.model.entities.ModeloxTalla;
import com.example.guantesapp.model.entities.UserChatRoom;
import com.example.guantesapp.model.entities.UserMessageChatRoom;
import com.example.guantesapp.model.ui.activities.ChatActivity;
import com.example.guantesapp.model.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Utils {
    public static final String BACKENDLESS_KEY = "D3A5917F-FC73-9C1C-FFBB-41FAF04BD300";
    public static final String APPLICATION_ID = "99E9488F-BC72-1A42-FF41-2FAF16A97300";
    public static final String CHANNEL_NAME = "general";
    public static boolean inChat = false;

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static void addNewUserChat(Context context, String email) {
        new TaskInserNewUserchat().execute(context, email);
    }

    public static CardView getCardView(ChatActivity chatActivity) {
        CardView cardView = new CardView(chatActivity);
        cardView.setCardElevation(10);
        cardView.setUseCompatPadding(true);
        cardView.setRadius(10);
        return cardView;
    }

    public static class TaskInserNewUserchat extends AsyncTask<Object, Void, Void> {
        long id = 0L;

        @Override
        protected Void doInBackground(Object... objects) {
            Context context = (Context) objects[0];
            String email = (String) objects[1];
            String foundEmail = GuantesDataBase.newInstance4(context).getGuantesInfoDao().getEmail(email);
            if (foundEmail == null) {
                UserChatRoom userChatRoom = new UserChatRoom();
                userChatRoom.setEmail(email);
                id = GuantesDataBase.newInstance4(context).getGuantesInfoDao().insertUserChat(userChatRoom);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (id > -1 && id != 0L) {
                Log.i(Utils.class.getSimpleName(), "Se agregó nuevo User");
                //Think about Show Notification
            } else {
                Log.i(Utils.class.getSimpleName(), "No se agregó nuevo User");
            }
        }
    }

    public static void publishMessage(String canal, final PublishOptions publishOptions, final Context context, final String email, final String date) {
        Backendless.Messaging.publish(canal, "", publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                //Email from User, NOT MINE
                saveMessageRoomDB(publishOptions.getHeaders().get("message"), true, "txt", context, "jonathanEmail", date,email+"-"+"jonathanEmail");
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

    public static void saveMessageRoomDB(String msg, boolean inChat, String msgType, Context context, String email, String date,String identifier) {
        new TaskInsertMessagesRoom().execute(msg, inChat, msgType, context, email, date,identifier);
    }

    public static class TaskInsertMessagesRoom extends AsyncTask<Object, Void, Void> {
        long id = 0L;

        @Override
        protected Void doInBackground(Object... objects) {
            String msg = (String) objects[0];
            boolean inChat = (boolean) objects[1];
            String msgType = (String) objects[2];
            Context context = (Context) objects[3];
            String email = (String) objects[4];
            String date = (String) objects[5];
            String identifier = (String) objects[6];
            UserMessageChatRoom userMessageChatRoom = new UserMessageChatRoom();
            userMessageChatRoom.setMessage(msg);
            userMessageChatRoom.setStatusChat(inChat);
            userMessageChatRoom.setMessageType(msgType);
            userMessageChatRoom.setEmail(email);
            userMessageChatRoom.setDate(date);
            userMessageChatRoom.setIdentifier(identifier);

            id = GuantesDataBase.newInstance5(context).getGuantesInfoDao().insertMessageUserChat(userMessageChatRoom);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (id > -1L) {
                System.out.println("Se guardó el mensaje");
            } else {
                System.out.println("No sé guardó el mensaje");
            }

        }
    }

}
