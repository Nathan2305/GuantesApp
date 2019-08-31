package com.example.guantesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class AdapterTallaModeloChild extends RecyclerView.Adapter<AdapterTallaModeloChild.ViewHolder> {
    private Context context;
    private List<Talla> listTalla;
    private List<String> listaUrl;
    private OnItemClickListener mListener;
    private List<String> ImageChecked = new ArrayList<>();

    public AdapterTallaModeloChild(Context context, List<Talla> listTalla, List<String> listChild) {
        this.context = context;
        this.listTalla = listTalla;
        this.listaUrl = listChild;
    }

    @Override
    public AdapterTallaModeloChild.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.container_consulta, null);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTallaModeloChild.ViewHolder viewHolder, int i) {
        Picasso.with(this.context).load(listaUrl.get(i)).into(viewHolder.fotoFound);
        viewHolder.tallaFound.setText(listTalla.get(i).getTallita());
        viewHolder.cantidadFound.setText(" " + listTalla.get(i).getCantidad());
        viewHolder.modeloFound.setText(" " + listTalla.get(i).getModelo());
    }

    @Override
    public int getItemCount() {
        return listTalla.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoFound, check;
        TextView tallaFound, cantidadFound, modeloFound;
        CardView cardContainer;
        boolean isPressed = false;

        public ViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            fotoFound = itemView.findViewById(R.id.fotoFound);
            tallaFound = itemView.findViewById(R.id.tallaFound);
            cantidadFound = itemView.findViewById(R.id.cantidadFound);
            modeloFound = itemView.findViewById(R.id.modeloFound);
            check = itemView.findViewById(R.id.check);

            fotoFound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            if (isPressed) {
                                ///fotoFound.setAlpha(1f);  //Normal
                                check.setVisibility(View.GONE);
                                isPressed = false;
                                ImageChecked.add(listaUrl.get(position));
                            } else {
                               // fotoFound.setAlpha(0.5f); //Opaco
                                check.setVisibility(View.VISIBLE);
                                isPressed = true;
                                ImageChecked.remove(listaUrl.get(position));
                            }
                        }
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterTallaModeloChild.OnItemClickListener listener) {
        mListener = listener;
    }

    public List<String> itemsChecked(){
        return ImageChecked;
    }
}
