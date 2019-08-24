package com.example.guantesapp;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class CustomAdapterforFotos extends RecyclerView.Adapter<CustomAdapterforFotos.ViewHolder> {
    private Context context;
    private List<String> fotoList;
    private List<String> ImageChecked = new ArrayList<>();
    private OnItemClickListener mListener;

    public CustomAdapterforFotos(Context context, List<String> fotoList) {
        this.context = context;
        this.fotoList = fotoList;
    }

    @Override
    public CustomAdapterforFotos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.found_fotos, null);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(CustomAdapterforFotos.ViewHolder viewHolder, int i) {
        Picasso.with(this.context).load(fotoList.get(i)).into(viewHolder.found_fotos);
    }

    @Override
    public int getItemCount() {
        return fotoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView found_fotos, check;
        boolean isTouched = false;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            found_fotos = itemView.findViewById(R.id.found_fotos);
            check = itemView.findViewById(R.id.check);
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
                                check.setVisibility(View.VISIBLE);
                                isTouched = true;
                            }
                        } else {
                            isTouched = false;
                            found_fotos.setAlpha(1f);
                            ImageChecked.remove(fotoList.get(position));
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
}
