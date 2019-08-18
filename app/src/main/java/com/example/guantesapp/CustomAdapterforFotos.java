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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class CustomAdapterforFotos extends RecyclerView.Adapter<CustomAdapterforFotos.ViewHolder> {
    private Context context;
    private List<Imagen> fotoList;
    private OnItemClickListener mListener;

    public CustomAdapterforFotos(Context context, List<Imagen> fotoList) {
        this.context = context;
        this.fotoList = fotoList;
    }

    @Override
    public CustomAdapterforFotos.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.found_fotos, null);
        return new ViewHolder(view, mListener);
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
        ImageView found_fotos,check;
        boolean isTouched = false;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            found_fotos = itemView.findViewById(R.id.found_fotos);
            check = itemView.findViewById(R.id.check);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if (!isTouched) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                                found_fotos.setAlpha(0.5f);
                                check.setVisibility(View.VISIBLE);
                                isTouched = true;
                            }
                        } else {
                            isTouched = false;
                            found_fotos.setAlpha(1f);
                            check.setVisibility(View.GONE);
                        }

                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
