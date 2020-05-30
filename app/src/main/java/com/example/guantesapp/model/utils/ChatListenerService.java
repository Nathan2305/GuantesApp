package com.example.guantesapp.model.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.PublishMessageInfo;
import com.backendless.rt.messaging.MessageInfoCallback;
import com.example.guantesapp.R;
import com.example.guantesapp.model.ui.activities.ListUsersChatActivity;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Date;

import static com.example.guantesapp.model.ui.activities.MainActivity.CHANNEL;
import static com.example.guantesapp.model.ui.activities.MainActivity.callback;
import static com.example.guantesapp.model.utils.Utils.inChat;

public class ChatListenerService extends Service {


    public ChatListenerService(Context context) {
        super();
    }


    public ChatListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startReceiveChatMessages();
        return START_STICKY;

    }

    private void startReceiveChatMessages() {
        if (callback == null) {
            callback = new MessageInfoCallback() {
                @Override
                public void handleResponse(PublishMessageInfo messageInfo) {
                    String publisherEmail = messageInfo.getHeaders().get("publisherEmail");
                    if (!"jonathanEmail".equalsIgnoreCase(publisherEmail)) {
                        String identifier = publisherEmail + "-" + "jonathanEmail";
                        //Message from User, NOT ME
                        Utils.addNewUserChat(getApplicationContext(), publisherEmail);
                        //Send messages to ChatActivity
                        Intent intent = new Intent("SendDataFromService");
                        Gson gson = new Gson();
                        String myJson = gson.toJson(messageInfo);
                        intent.putExtra("myjson", myJson);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        if (!inChat) {
                            DateFormat format = DateFormat.getTimeInstance();
                            Date date = new Date();
                            String dateString = format.format(date);
                            String message = messageInfo.getHeaders().get("message");
                            if (messageInfo.getHeaders().get("sizeImage") != null) {
                                //Messages receives with Images
                                int sizeModelsReceives = Integer.parseInt(messageInfo.getHeaders().get("sizeImage"));
                                if (sizeModelsReceives > 0) {
                                    for (int pos = 0; pos < sizeModelsReceives; pos++) {
                                        String val = "listUrl_" + pos;
                                        String eachUrl = messageInfo.getHeaders().get(val);
                                        Utils.saveMessageRoomDB(eachUrl, inChat, "img", getApplicationContext(), publisherEmail, dateString,identifier );
                                    }
                                    if (message != null && !message.isEmpty()) {
                                        Utils.saveMessageRoomDB(message, inChat, "txt", getApplicationContext(), publisherEmail, dateString, identifier);
                                    }
                                }
                            } else {
                                Utils.saveMessageRoomDB(message, inChat, "txt", getApplicationContext(), publisherEmail, dateString, identifier);
                            }
                            //Create Push Notification
                            String NOTIFICATION_CHANNEL_ID = "channel_id";
                            Intent intentNotify = new Intent(getApplicationContext(), ListUsersChatActivity.class);
                            intentNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentNotify, 0);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                String CHANNEL_NAME = "NotificationChannel";
                                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
                                notificationChannel.enableLights(true);
                                notificationChannel.enableVibration(true);
                                notificationChannel.setLightColor(Color.GREEN);
                                notificationChannel.setVibrationPattern(new long[]{500,500,500,500,500});
                                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_check)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.guardiano))
                                    .setContentTitle("Mensaje de comprador")
                                    .setContentText(message)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                            notificationManagerCompat.notify(1, builder.build());
                        }
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    System.out.println("got error " + fault);
                }
            };
            CHANNEL.addMessageListener(callback);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, BroadCastRestartService.class);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }
}
