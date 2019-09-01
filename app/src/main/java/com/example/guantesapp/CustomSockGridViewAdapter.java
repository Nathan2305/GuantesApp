package com.example.guantesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class CustomSockGridViewAdapter extends BaseAdapter {
    boolean isChecked = false;
    Context context;
    List<String> fotoList;
    List<String> listModelo;
    static List<String> urlModelos = new ArrayList<>();

    public CustomSockGridViewAdapter(Context context, List<String> fotoList, List<String> listModelo) {
        this.context = context;
        this.fotoList = fotoList;
        this.listModelo = listModelo;
    }

    @Override
    public int getCount() {
        return fotoList.size();
    }

    @Override
    public Object getItem(int position) {
        return fotoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return fotoList.get(position).getBytes().length;
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
        Picasso.with(context).load(fotoList.get(position)).into(found_fotos);
        nameModelo.setText(listModelo.get(position));
        found_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    check.setVisibility(View.GONE);
                    urlModelos.remove(listModelo.get(position));
                    isChecked = false;
                } else {
                    check.setVisibility(View.VISIBLE);
                    urlModelos.add(listModelo.get(position));
                    isChecked = true;
                }
            }
        });

        return convertView;
    }

    public static List<String> getUrlFotosChecked(){
        return urlModelos;
    }
}
