package com.example.guantesapp.model.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.UserChatRoom;
import com.example.guantesapp.model.utils.AdapterRecViewListChat;
import com.example.guantesapp.model.utils.GuantesDataBase;
import com.example.guantesapp.model.utils.Utils;

import java.util.List;

public class ListUsersChatActivity extends AppCompatActivity implements AdapterRecViewListChat.onChatUserListener {
    RecyclerView recViewListChat;
    List<UserChatRoom> listChat;
    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users_chat);
        recViewListChat = findViewById(R.id.recViewListChat);
        new TaskGetUserchat().execute();
    }

    @Override
    public void onItemChatClick(int pos) {
        Intent intent=new Intent(ListUsersChatActivity.this,ChatActivity.class);
        intent.putExtra("email",listChat.get(pos).getEmail());
        startActivity(intent);
    }

    public class TaskGetUserchat extends AsyncTask<Void, Void, Void> {
        List<UserChatRoom> list;

        @Override
        protected Void doInBackground(Void... voids) {
            list = GuantesDataBase.newInstance4(ListUsersChatActivity.this).getGuantesInfoDao().getUsersChatRoom();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!list.isEmpty()) {
                listChat=list;
                adapter = new AdapterRecViewListChat(listChat, ListUsersChatActivity.this,ListUsersChatActivity.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListUsersChatActivity.this);
                recViewListChat.setLayoutManager(linearLayoutManager);
                recViewListChat.setAdapter(adapter);
                recViewListChat.setHasFixedSize(true);
            } else {
                Utils.showToast(ListUsersChatActivity.this, "Still no chat Users ");
            }
        }
    }
}
