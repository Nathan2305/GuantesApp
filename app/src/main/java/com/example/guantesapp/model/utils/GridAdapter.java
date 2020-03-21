package com.example.guantesapp.model.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.Modelo;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


public class GridAdapter extends BaseAdapter {
    Context context;
    List<Modelo> listaModelos;

    public GridAdapter(Context context, List<Modelo> listaModelos) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderGridview viewHoldernew;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.container_consulta, null);
            viewHoldernew = new ViewHolderGridview();
            viewHoldernew.fotoFound = convertView.findViewById(R.id.fotoFound);
           /* viewHoldernew.tallaFound = convertView.findViewById(R.id.tallaFound);
            viewHoldernew.cantidadFound = convertView.findViewById(R.id.cantidadFound);*/
            convertView.setTag(viewHoldernew);
        } else {
            viewHoldernew = (ViewHolderGridview) convertView.getTag();
        }
        Picasso.with(context).load(listaModelos.get(position).getFoto_url()).into(viewHoldernew.fotoFound);
        return convertView;
    }


    static class ViewHolderGridview {
        ImageView fotoFound;
    }
}
