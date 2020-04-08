package com.example.guantesapp.model.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.Modelo;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


public class GridAdapterConsulta extends BaseAdapter {

    private Context context;
    private List<Modelo> listaModelos;
    private int check = -1;
    ViewHolder viewHolder;

    public GridAdapterConsulta(Context context, List<Modelo> listaModelos) {
        this.context = context;
        this.listaModelos = listaModelos;
    }

    @Override
    public int getCount() {
        return listaModelos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaModelos.get(position).getModelo();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.container_consulta, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        viewHolder = (ViewHolder) convertView.getTag();
        Picasso.with(context).load(listaModelos.get(position).getFoto_url()).into(viewHolder.fotoFound);
       // viewHolder.nom_modelo.setText(listaModelos.get(position).getModelo());
        //updateRecords(listaModelos);
        Modelo modelo = listaModelos.get(position);
        if (modelo.isChecked()) {
            viewHolder.check.setVisibility(View.VISIBLE);
            //viewHolder.cardView.setAlpha(0.5f);
        } else {
            viewHolder.check.setVisibility(View.INVISIBLE);
            //viewHolder.cardView.setAlpha(1f);
        }
        return convertView;
    }

    public void updateRecords(List<Modelo> list) {
        this.listaModelos = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView fotoFound, check;
        TextView cantidad, nom_modelo;
        CardView cardView;

        ViewHolder(View root) {
            fotoFound = root.findViewById(R.id.fotoFound);
           // nom_modelo = root.findViewById(R.id.nom_modelo);
            check = root.findViewById(R.id.check);
            cantidad = root.findViewById(R.id.cantidad);
            cardView = root.findViewById(R.id.card);
        }
    }

}
