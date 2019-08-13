package com.example.guantesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class CustomAdapterforFotos extends RecyclerView.Adapter<CustomAdapterforFotos.ViewHolder> {
    Context context;
    List<Imagen> fotoList;

    public CustomAdapterforFotos(Context context, List<Imagen> fotoList) {
        this.context = context;
        this.fotoList = fotoList;
    }

    @Override
    public CustomAdapterforFotos.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.found_fotos, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterforFotos.ViewHolder viewHolder, int i) {
        Picasso.with(this.context).load(fotoList.get(i).getFoto()).into(viewHolder.found_fotos);
    }

    @Override
    public int getItemCount() {
        return fotoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView found_fotos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            found_fotos = itemView.findViewById(R.id.found_fotos);
        }
    }
}
