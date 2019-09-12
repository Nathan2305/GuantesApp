package com.example.guantesapp.model.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guantesapp.R;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class AdapterAgregarStock extends BaseAdapter {
    boolean isChecked = false;
    Context context;
    List<String> listUrlModelos;
    List<String> listModelo;
    static List<String> urlModelos = new ArrayList<>();

    public AdapterAgregarStock(Context context, List<String> listUrlModelos, List<String> listModelo) {
        this.context = context;
        this.listUrlModelos = listUrlModelos;
        this.listModelo = listModelo;
    }

    @Override
    public int getCount() {
        return listUrlModelos.size();
    }

    @Override
    public String getItem(int position) {
        return listUrlModelos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listUrlModelos.get(position).getBytes().length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.found_fotos, parent, false);
        }

        ImageView found_fotos = convertView.findViewById(R.id.found_fotos);
        final ImageView check = convertView.findViewById(R.id.check);
        TextView nameModelo = convertView.findViewById(R.id.nameModelo);
        Picasso.with(context).load(listUrlModelos.get(position)).into(found_fotos);
        nameModelo.setText(listModelo.get(position));

        return convertView;
    }

    public static List<String> getUrlFotosChecked() {
        return urlModelos;
    }
}
