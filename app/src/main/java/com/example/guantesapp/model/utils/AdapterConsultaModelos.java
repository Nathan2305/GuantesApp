package com.example.guantesapp.model.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.ModeloChild;
import com.example.guantesapp.model.entities.Talla;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


public class AdapterConsultaModelos extends RecyclerView.Adapter<AdapterConsultaModelos.ViewHolder> {
    public Context context;
    private List<Talla> listTalla;
    private List<String> listaUrl;
    private OnItemClickListener mListener;
    public static List<Drawable> listImagesPositionChecked = new ArrayList<>();

    public AdapterConsultaModelos(Context context, List<Talla> listTalla, List<String> listChild) {
        this.context = context;
        this.listTalla = listTalla;
        this.listaUrl = listChild;
    }

    @NonNull
    @Override
    public AdapterConsultaModelos.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.container_consulta, null);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterConsultaModelos.ViewHolder viewHolder, int i) {
        Picasso.with(this.context).load(listaUrl.get(i)).into(viewHolder.fotoFound);
        viewHolder.tallaFound.setText(listTalla.get(i).getTallita());
        viewHolder.cantidadFound.setText(String.valueOf(listTalla.get(i).getCantidad()));
        viewHolder.modeloFound.setText(listTalla.get(i).getModelo());
    }

    @Override
    public int getItemCount() {
        return listTalla.size();
    }

    public String getItemModelo(int position) {
        return listTalla.get(position).getModelo();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoFound;
        ImageView check;
        TextView tallaFound, cantidadFound, modeloFound;
        CardView cardContainer;

        public ViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);

            cardContainer = itemView.findViewById(R.id.cardContainer);
            fotoFound = itemView.findViewById(R.id.fotoFound);
            check = itemView.findViewById(R.id.check);
            tallaFound = itemView.findViewById(R.id.tallaFound);
            cantidadFound = itemView.findViewById(R.id.cantidadFound);
            modeloFound = itemView.findViewById(R.id.modeloFound);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        try {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                                if (fotoFound.getAlpha() != 1f) {
                                    fotoFound.setAlpha(1f); //Deseleccionar
                                    check.setVisibility(View.GONE);
                                    if (listImagesPositionChecked.contains(fotoFound.getDrawable())) {
                                        listImagesPositionChecked.remove(fotoFound.getDrawable());
                                    }
                                } else {
                                    fotoFound.setAlpha(0.5f);  //Seleccionar Foto
                                    check.setVisibility(View.VISIBLE);
                                    //listImagesPositionChecked.add(getItemModelo(position));
                                    listImagesPositionChecked.add(fotoFound.getDrawable());
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Excepcion Adapter - " + e.getMessage());
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterConsultaModelos.OnItemClickListener listener) {
        mListener = listener;
    }


}
