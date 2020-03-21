package com.example.guantesapp.model.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guantesapp.R;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class AdapterVenta extends RecyclerView.Adapter<AdapterVenta.ViewHolder> {
    private Context context;
    private List<String> fotoList;
    private List<String> ImageChecked = new ArrayList<>();
    private List<String> modeloChecked = new ArrayList<>();
    List<String> listModelo;
    private OnItemClickListener mListener;

    public AdapterVenta(Context context, List<String> fotoList, List<String> listModelo) {
        this.context = context;
        this.fotoList = fotoList;
        this.listModelo=listModelo;
    }

    @Override
    public AdapterVenta.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.venta_modelo_layout, null);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(AdapterVenta.ViewHolder viewHolder, int i) {
        Picasso.with(this.context).load(fotoList.get(i)).into(viewHolder.found_fotos);
        viewHolder.nameModelo.setText(listModelo.get(i));
    }

    @Override
    public int getItemCount() {
        return fotoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView found_fotos, check;
        TextView nameModelo;
        boolean isTouched = false;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            found_fotos = itemView.findViewById(R.id.found_fotos);
            check = itemView.findViewById(R.id.check);
            nameModelo= itemView.findViewById(R.id.nameModelo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        if (!isTouched) {
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                                found_fotos.setAlpha(0.5f);
                                ImageChecked.add(fotoList.get(position));
                                modeloChecked.add(listModelo.get(position));
                                check.setVisibility(View.VISIBLE);
                                isTouched = true;
                            }
                        } else {
                            isTouched = false;
                            found_fotos.setAlpha(1f);
                            ImageChecked.remove(fotoList.get(position));
                            modeloChecked.remove(listModelo.get(position));
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

    public List<String> getUrlFotosChecked() {
        return ImageChecked;
    }

    public List<String> getModeloChecked(){
        return modeloChecked;
    }
}
