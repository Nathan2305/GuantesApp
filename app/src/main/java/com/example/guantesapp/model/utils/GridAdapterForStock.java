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


public class GridAdapterForStock extends BaseAdapter {
    Context context;
    List<Modelo> listaModelos;


    public GridAdapterForStock(Context context, List<Modelo> listaModelos) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoldernew;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.each_modelo_consulta, parent,false);
            viewHoldernew = new ViewHolder();
            viewHoldernew.fotoFound = convertView.findViewById(R.id.fotoFound);
            //viewHoldernew.cantidad = convertView.findViewById(R.id.cantidad);
            //viewHoldernew.nom_modelo = convertView.findViewById(R.id.nom_modelo);
            //viewHoldernew.card = convertView.findViewById(R.id.card);
            convertView.setTag(viewHoldernew);
        } else {
            viewHoldernew = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load(listaModelos.get(position).getFoto_url()).into(viewHoldernew.fotoFound);
        //viewHoldernew.nom_modelo.setText(listaModelos.get(position).getModelo());
        return convertView;
    }


    public class ViewHolder {
        //CardView card;
        ImageView fotoFound;
       // TextView cantidad, nom_modelo;

    }


}
