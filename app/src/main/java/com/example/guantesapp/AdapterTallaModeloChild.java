package com.example.guantesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class AdapterTallaModeloChild extends RecyclerView.Adapter<AdapterTallaModeloChild.ViewHolder> {
    private Context context;
    private List<Talla> listTalla;
    private List<ModeloChild> listChild;

    public AdapterTallaModeloChild(Context context, List<Talla> listTalla, List<ModeloChild> listChild) {
        this.context = context;
        this.listTalla = listTalla;
        this.listChild = listChild;
    }

    @Override
    public AdapterTallaModeloChild.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.container_consulta, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTallaModeloChild.ViewHolder viewHolder, int i) {
        Picasso.with(this.context).load(listChild.get(i).getImagenUrl()).into(viewHolder.fotoFound);
        viewHolder.tallaFound.setText(listTalla.get(i).getTallita());
        viewHolder.cantidadFound.setText(" " + listTalla.get(i).getCantidad());
    }

    @Override
    public int getItemCount() {
        return listTalla.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoFound;
        TextView tallaFound, cantidadFound;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoFound = itemView.findViewById(R.id.fotoFound);
            tallaFound = itemView.findViewById(R.id.tallaFound);
            cantidadFound = itemView.findViewById(R.id.cantidadFound);
        }
    }
}
