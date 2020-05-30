package com.example.guantesapp.model.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.UserChatRoom;
import com.example.guantesapp.model.ui.activities.ListUsersChatActivity;

import java.util.List;

public class AdapterRecViewListChat extends RecyclerView.Adapter<AdapterRecViewListChat.ViewHolder> {
    List<UserChatRoom> list;
    Context context;
    onChatUserListener onChatUserListener;

    public AdapterRecViewListChat(List<UserChatRoom> list, Context context, onChatUserListener onChatUserListener) {
        this.list = list;
        this.context = context;
        this.onChatUserListener = onChatUserListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View container = LayoutInflater.from(context).inflate(R.layout.container_each_user_chat, parent, false);
        return new ViewHolder(container,onChatUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.emailUser.setText(list.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView emailUser;
        onChatUserListener onChatUserListener;

        public ViewHolder(@NonNull View itemView,onChatUserListener onChatUserListener) {
            super(itemView);
            emailUser = itemView.findViewById(R.id.emailUser);
            this.onChatUserListener=onChatUserListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChatUserListener.onItemChatClick(getAdapterPosition());
        }
    }


    public interface onChatUserListener {
        void onItemChatClick(int pos);
    }
}
