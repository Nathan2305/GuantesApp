package com.example.guantesapp.model.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.TextViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.PublishMessageInfo;
import com.backendless.messaging.PublishOptions;
import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.UserMessageChatRoom;
import com.example.guantesapp.model.utils.GuantesDataBase;
import com.example.guantesapp.model.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

import static com.example.guantesapp.model.ui.activities.MainActivity.CHANNEL;
import static com.example.guantesapp.model.utils.Utils.inChat;

public class ChatActivity extends AppCompatActivity {
    FloatingActionButton send;
    EditText txtmessage;
    String email;
    private MyBroadcastReceiver myReceiver;
    private ScrollView scrollChat;
    private LinearLayout lcontainer;
    int NUMBER_IMAGES = 2;
    boolean isPausedMessage = true;
    LinearLayout.LayoutParams layoutparamsRight = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    LinearLayout.LayoutParams layoutParamsLeft = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        layoutParamsLeft.setMargins(15, 20, 135, 4);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        txtmessage = findViewById(R.id.txtmessage);
        send = findViewById(R.id.send);
        scrollChat = findViewById(R.id.scrollChat);
        lcontainer = scrollChat.findViewById(R.id.lcontainer);
        loadHistoricalMessages();
        updateMessagesStatus(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            send.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.azulito)));
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(txtmessage, TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE);
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txtmessage.getText().toString();
                if (!msg.isEmpty()) {
                    publishByVendor(msg);
                }
            }
        });
        CHANNEL.addJoinListener(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                DateFormat formatter = DateFormat.getTimeInstance();
                Date date = new Date();
                //Utils.publishMessage(CHANNEL, "El Vendedor se unió al chat", "txt", ChatActivity.this, "jonathanEmail", formatter.format(date));
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

    private void updateMessagesStatus(boolean b) {
        new TaskUpdateMessageStatus().execute(b);
    }

    public class TaskUpdateMessageStatus extends AsyncTask<Boolean, Void, Void> {
        List<UserMessageChatRoom> list;

        @Override
        protected Void doInBackground(Boolean... voids) {
            boolean status = voids[0];
            list = GuantesDataBase.newInstance5(ChatActivity.this).getGuantesInfoDao().getListMessagePerStatus(false);
            for (UserMessageChatRoom u : list) {
                UserMessageChatRoom userMessageChatRoom = new UserMessageChatRoom();
                userMessageChatRoom.setId(u.getId());
                userMessageChatRoom.setStatusChat(status);
                userMessageChatRoom.setEmail(u.getEmail());
                userMessageChatRoom.setDate(u.getDate());
                userMessageChatRoom.setMessage(u.getMessage());
                userMessageChatRoom.setMessageType(u.getMessageType());
                GuantesDataBase.newInstance5(ChatActivity.this).getGuantesInfoDao().updateMessage(userMessageChatRoom);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isPausedMessage = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        inChat = true;
        putMessagesPaused();
        if (isPausedMessage) {
            updateMessagesStatus(true);
        }
        myReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("SendDataFromService");
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
    }

    private void putMessagesPaused() {
        new TaskgetMessagesPaused().execute();
    }

    public class TaskgetMessagesPaused extends AsyncTask<Void, Void, Void> {
        List<UserMessageChatRoom> listMessagePaused;

        @Override
        protected Void doInBackground(Void... voids) {
            listMessagePaused = GuantesDataBase.newInstance5(ChatActivity.this).getGuantesInfoDao().getListMessagePerStatus(false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listMessagePaused != null && !listMessagePaused.isEmpty()) {
                isPausedMessage = true;
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.height = 300;
                layoutParams.width = 300;
                layoutparamsRight.setMargins(150, 20, 15, 4);
                layoutparamsRight.gravity = Gravity.END;
                for (UserMessageChatRoom eachMessage : listMessagePaused) {
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    LinearLayout lroot = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_bubble_chat_right, viewGroup, false);
                    TextView txtvDate = lroot.findViewById(R.id.txtvDate);
                    lroot.setLayoutParams(layoutparamsRight);
                    if (!"img".equalsIgnoreCase(eachMessage.getMessageType())) {
                        lroot.setBackground(getResources().getDrawable(R.drawable.chat_txtv_right));
                        TextView txtView = lroot.findViewById(R.id.txtView);
                        txtView.setTextSize(18f);
                        txtView.setText(eachMessage.getMessage());
                    } else { //Message is image
                        CardView cardView = Utils.getCardView(ChatActivity.this);
                        cardView.setBackground(getResources().getDrawable(R.drawable.card_border_color));
                        if (cardView.getParent() != null) {
                            ((ViewGroup) cardView.getParent()).removeView(cardView); // <- fix
                        }
                        ImageView imgView = new ImageView(ChatActivity.this);
                        Picasso.with(ChatActivity.this).load(eachMessage.getMessage()).into(imgView);
                        cardView.addView(imgView);
                        lroot.addView(cardView);
                    }
                    txtvDate.setText(eachMessage.getDate());
                    // txtView.setPadding(60, 0, 0, 0);
                    lcontainer.addView(lroot);
                    scrollChat.fullScroll(View.FOCUS_DOWN);

                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        inChat = false;
        if (myReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        myReceiver = null;

    }

    private void loadHistoricalMessages() {
        new TaskPutMessageFromRoomIntoViews().execute();
    }

    public class TaskPutMessageFromRoomIntoViews extends AsyncTask<Void, Void, Void> {
        List<UserMessageChatRoom> messageChatRoomList;

        @Override
        protected Void doInBackground(Void... voids) {
            String identifier = email + "-" + "jonathanEmail";
            messageChatRoomList = GuantesDataBase.newInstance5(ChatActivity.this).getGuantesInfoDao().getAllMessagesFromUser(identifier);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Retrieves messages from DataBAse and show them in scroll
            if (messageChatRoomList != null && !messageChatRoomList.isEmpty()) {
                ViewGroup viewGroup = findViewById(android.R.id.content);
                for (UserMessageChatRoom userMessageChatRoom : messageChatRoomList) {
                    if ("jonathanEmail".equalsIgnoreCase(userMessageChatRoom.getEmail())) {
                        LinearLayout lroot = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_bubble_chat_left, viewGroup, false);
                        TextView txtView = lroot.findViewById(R.id.txtView);
                        TextView txtvDate = lroot.findViewById(R.id.txtvDate);
                        txtView.setTextSize(16f);
                        lroot.setLayoutParams(layoutParamsLeft);
                        txtView.setText(userMessageChatRoom.getMessage().trim());
                        DateFormat formatter = DateFormat.getTimeInstance();
                        Date date = new Date();
                        txtvDate.setText(formatter.format(date));
                        lcontainer.addView(lroot);
                        scrollChat.fullScroll(View.FOCUS_DOWN);
                    } else {
                        LinearLayout lroot = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_bubble_chat_right, viewGroup, false);
                        TextView txtvDate = lroot.findViewById(R.id.txtvDate);
                        layoutparamsRight.setMargins(150, 20, 15, 4);
                        layoutparamsRight.gravity = Gravity.END;
                        lroot.setLayoutParams(layoutparamsRight);
                        if (!"img".equalsIgnoreCase(userMessageChatRoom.getMessageType())) {
                            lroot.setBackground(getResources().getDrawable(R.drawable.chat_txtv_right));
                            TextView txtView = lroot.findViewById(R.id.txtView);
                            txtView.setTextSize(16f);
                            txtView.setText(userMessageChatRoom.getMessage());
                        } else { //Message is image
                            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.height = 300;
                            layoutParams.width = 300;
                            CardView cardView = Utils.getCardView(ChatActivity.this);
                            cardView.setBackground(getResources().getDrawable(R.drawable.card_border_color));
                            if (cardView.getParent() != null) {
                                ((ViewGroup) cardView.getParent()).removeView(cardView); // <- fix
                            }
                            ImageView imgView = new ImageView(ChatActivity.this);
                            Picasso.with(ChatActivity.this).load(userMessageChatRoom.getMessage()).into(imgView);
                            cardView.addView(imgView);
                            lroot.addView(cardView);
                        }
                        txtvDate.setText(userMessageChatRoom.getDate());
                        // txtView.setPadding(60, 0, 0, 0);
                        lcontainer.addView(lroot);
                        scrollChat.fullScroll(View.FOCUS_DOWN);
                    }
                }
            }
        }
    }


    public void publishByVendor(String msg) {
        LinearLayout lroot = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_bubble_chat_left, null, false);
        TextView txtView = lroot.findViewById(R.id.txtView);
        TextView txtvDate = lroot.findViewById(R.id.txtvDate);
        txtView.setTextSize(16f);
        lroot.setLayoutParams(layoutParamsLeft);
        txtView.setText(msg);
        DateFormat formatter = DateFormat.getTimeInstance();
        Date date = new Date();
        txtvDate.setText(formatter.format(date));
        lcontainer.addView(lroot);
        scrollChat.fullScroll(View.FOCUS_DOWN);
        txtmessage.setText("");
        PublishOptions publishOptions = new PublishOptions();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("messageType", "txt");
        hashMap.put("message", msg);
        hashMap.put("sent_to", email);
        hashMap.put("publisherEmail", "jonathanEmail");
        publishOptions.setHeaders(hashMap);
        //email from User, NOT ME
        Utils.publishMessage(Utils.CHANNEL_NAME, publishOptions, ChatActivity.this, email, formatter.format(date));
    }

    public void putIncomingMessageRight(PublishMessageInfo messageInfo) {
        String publisherEmail = messageInfo.getHeaders().get("publisherEmail");
        if (!"jonathanEmail".equalsIgnoreCase(publisherEmail) && email.equalsIgnoreCase(publisherEmail)) {
            //Receieve and put messages from users
            if (messageInfo.getHeaders().get("sizeImage") != null) {
                //Messages receives with Images
                int sizeModelsReceives = Integer.parseInt(messageInfo.getHeaders().get("sizeImage"));
                if (sizeModelsReceives > 0) {
                    List<String> modelos = new ArrayList<>();
                    for (int pos = 0; pos < sizeModelsReceives; pos++) {
                        String val = "listUrl_" + pos;
                        modelos.add(messageInfo.getHeaders().get(val));
                    }
                    //putMessageWithImages(modelos, messageInfo.getHeaders().get("messageType"));
                    putMessageWithImagesIntoViews(modelos, messageInfo);
                }
            } else { //Message with NO images
                String msgReceivedfromUser = messageInfo.getHeaders().get("message");
                String msgType = messageInfo.getHeaders().get("messageType");
                ViewGroup viewGroup = findViewById(android.R.id.content);
                LinearLayout lroot = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_bubble_chat_right, viewGroup, false);
                lroot.setBackground(getResources().getDrawable(R.drawable.chat_txtv_right));
                TextView txtvDate = lroot.findViewById(R.id.txtvDate);
                TextView txtMessage = lroot.findViewById(R.id.txtView);
                txtMessage.setTextSize(16f);
                layoutparamsRight.setMargins(150, 20, 15, 4);
                layoutparamsRight.gravity = Gravity.END;
                lroot.setLayoutParams(layoutparamsRight);
                txtMessage.setText(msgReceivedfromUser.trim());
                DateFormat formatter = DateFormat.getTimeInstance();
                Date date = new Date();
                txtvDate.setText(formatter.format(date));
                lcontainer.addView(lroot);
                scrollChat.fullScroll(View.FOCUS_DOWN);
                Utils.saveMessageRoomDB(msgReceivedfromUser.trim(), true, msgType, ChatActivity.this, publisherEmail, formatter.format(date), publisherEmail + "-" + "jonathanEmail");
            }
        }
    }

    private void putMessageWithImagesIntoViews(List<String> modelos, PublishMessageInfo messageInfo) {
        new TaskPutMessageReceivedIntoChat().execute(modelos, messageInfo);
    }

    public class TaskPutMessageReceivedIntoChat extends AsyncTask<Object, Void, Void> {
        List<String> urlFotos;
        PublishMessageInfo publishMessageInfo;
        String msg;
        DateFormat formatter = DateFormat.getTimeInstance();
        Date date = new Date();
        String dateString = formatter.format(date);

        @Override
        protected Void doInBackground(Object... objects) {
            try {
                urlFotos = (List<String>) objects[0];
                publishMessageInfo = (PublishMessageInfo) objects[1];
            } catch (Exception e) {
                System.out.println("Exeociont " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!urlFotos.isEmpty()) {

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.height = 300;
                layoutParams.width = 300;
                LinearLayout lroot = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_chat_right_with_image, null, false);

                if (urlFotos.size() <= NUMBER_IMAGES) {
                    msg = publishMessageInfo.getHeaders().get("message");
                    for (String eachUrl : urlFotos) {
                        CardView cardView = Utils.getCardView(ChatActivity.this);
                        cardView.setLayoutParams(layoutParams);
                        cardView.setBackground(getResources().getDrawable(R.drawable.card_border_color));
                        ImageView imgView = new ImageView(ChatActivity.this);
                        Picasso.with(ChatActivity.this).load(eachUrl).into(imgView);
                        cardView.addView(imgView);
                        if (cardView.getParent() != null) {
                            ((ViewGroup) cardView.getParent()).removeView(cardView); // <- fix
                        }
                        lroot.addView(cardView);
                        if (lroot.getParent() != null) {
                            ((ViewGroup) lroot.getParent()).removeView(lroot); // <- fix
                        }
                        lcontainer.addView(lroot);
                    }
                    LinearLayout txt_message = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_bubble_chat_right, null, false);
                    txt_message.setBackground(getResources().getDrawable(R.drawable.chat_txtv_right));
                    TextView txtView = txt_message.findViewById(R.id.txtView);
                    txtView.setText(msg);
                    TextView txtvDate = txt_message.findViewById(R.id.txtvDate);
                    txtvDate.setText(dateString);
                    layoutparamsRight.setMargins(150, 20, 15, 4);
                    layoutparamsRight.gravity = Gravity.END;
                    txt_message.setLayoutParams(layoutparamsRight);
                    lcontainer.addView(txt_message);
                    scrollChat.fullScroll(View.FOCUS_DOWN);
                } else {
                    int residuo = urlFotos.size() % NUMBER_IMAGES;
                    LinearLayout layoutPerPairofthree = null;
                    for (int k = 0; k < urlFotos.size(); k++) {
                        if (layoutPerPairofthree == null) {
                            layoutPerPairofthree = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_chat_right_with_image, null, false);
                        }
                        CardView cardView = Utils.getCardView(ChatActivity.this);
                        cardView.setBackground(getResources().getDrawable(R.drawable.card_border_color));
                        ImageView imgView = new ImageView(ChatActivity.this);
                        Picasso.with(ChatActivity.this).load(urlFotos.get(k)).into(imgView);
                        cardView.setLayoutParams(layoutParams);
                        cardView.addView(imgView);
                        if (cardView.getParent() != null) {
                            ((ViewGroup) cardView.getParent()).removeView(cardView); // <- fix
                        }
                        layoutPerPairofthree.addView(cardView);

                        if ((k + 1) % NUMBER_IMAGES == 0 && k + 1 + residuo <= urlFotos.size()) {//size =8, residuo=2
                            //agregar los 3 primeros en el layout
                            lcontainer.addView(layoutPerPairofthree);
                            layoutPerPairofthree = null;
                            scrollChat.fullScroll(View.FOCUS_DOWN);
                        } else if (k + 1 + residuo > urlFotos.size()) {
                            //Agrega los n últimos elementos en el layout
                            LinearLayout lastLayout = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_chat_right_with_image, null, false);
                            CardView cardFinal = Utils.getCardView(ChatActivity.this);
                            cardFinal.setBackground(getResources().getDrawable(R.drawable.card_border_color));
                            ImageView imgView2 = new ImageView(ChatActivity.this);
                            Picasso.with(ChatActivity.this).load(urlFotos.get(k)).into(imgView2);
                            cardFinal.setLayoutParams(layoutParams);
                            cardFinal.addView(imgView2);
                            if (cardFinal.getParent() != null) {
                                ((ViewGroup) cardFinal.getParent()).removeView(cardFinal); // <- fix
                            }
                            lastLayout.addView(cardFinal);
                            lcontainer.addView(lastLayout);
                            scrollChat.fullScroll(View.FOCUS_DOWN);
                        }
                    }
                    //}
                    msg = publishMessageInfo.getHeaders().get("message");
                    LinearLayout txt_message = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.layout_bubble_chat_right, null, false);
                    txt_message.setBackground(getResources().getDrawable(R.drawable.chat_txtv_right));
                    TextView txtView = txt_message.findViewById(R.id.txtView);
                    txtView.setText(msg);
                    TextView txtvDate = txt_message.findViewById(R.id.txtvDate);
                    txtvDate.setText(dateString);
                    layoutparamsRight.setMargins(150, 20, 15, 4);
                    layoutparamsRight.gravity = Gravity.END;
                    txt_message.setLayoutParams(layoutparamsRight);
                    lcontainer.addView(txt_message);

                }
                for (String eachUrl : urlFotos) {
                    Utils.saveMessageRoomDB(eachUrl, true, "img", ChatActivity.this, email, dateString, email + "-" + "jonathanEmail");
                }
                if (msg != null && !msg.isEmpty()) {
                    Utils.saveMessageRoomDB(msg, true, "txt", ChatActivity.this, email, dateString, email + "-" + "jonathanEmail");
                }

            }
        }
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = new Gson();
            PublishMessageInfo publishMessageInfo = gson.fromJson(intent.getStringExtra("myjson"), PublishMessageInfo.class);
            putIncomingMessageRight(publishMessageInfo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_share_modelos, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
