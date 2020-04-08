package com.example.guantesapp.model.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.guantesapp.model.entities.MRoomTallaCantidad;
import com.example.guantesapp.model.entities.Modelo;
import com.example.guantesapp.model.entities.ModeloxTalla;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {
    public static final String BACKENDLESS_KEY = "D3A5917F-FC73-9C1C-FFBB-41FAF04BD300";
    public static final String APPLICATION_ID = "99E9488F-BC72-1A42-FF41-2FAF16A97300";

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static List<String> getCantidadPerModel(List<Modelo> list, String talla) {
        final List<String> listReturn = new ArrayList<>();
        DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < list.size(); k++) {
            sb.append("modelo_link.modelo='" + list.get(k).getModelo() + "'")
                    .append(" and talla='" + talla + "'");
            if (k != list.size() - 1) {
                sb.append(" or ");
            }
        }
        dataQueryBuilder.setWhereClause(sb.toString());
        Backendless.Data.of(ModeloxTalla.class).find(dataQueryBuilder, new AsyncCallback<List<ModeloxTalla>>() {
            @Override
            public void handleResponse(List<ModeloxTalla> response) {
                if (response.size() > 0) {
                    for (ModeloxTalla modeloxTalla : response) {
                        listReturn.add(String.valueOf(modeloxTalla.getCantidad()));
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.out.println("Errror getting cantidad - " + fault.getMessage());
            }
        });
        return listReturn;
    }


}
