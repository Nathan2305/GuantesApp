package com.example.guantesapp.model.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.guantesapp.model.entities.ModeloUrlRoomDB;

public class Utils {
    public static final String BACKENDLESS_KEY = "D3A5917F-FC73-9C1C-FFBB-41FAF04BD300";
    public static final String APPLICATION_ID = "99E9488F-BC72-1A42-FF41-2FAF16A97300";
    private static String valor = "";

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getModeloUrl(Context context, String name) {
        new TaskgetModeloUrl().execute(context, name);
        return valor;
    }

    public static void insertModeloUrl(Context context, ModeloUrlRoomDB modeloUrlRoomDB) {
        new TaskInserIntoSQLModeloUrl().execute(context, modeloUrlRoomDB);
    }

    public static class TaskInserIntoSQLModeloUrl extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            Context context = (Context) objects[0];
            ModeloUrlRoomDB modelo = (ModeloUrlRoomDB) objects[1];
            long id = GuantesDataBase.newInstance2(context).getGuantesInfoDao().insertModeloUrlRoom(modelo);
            if (id>-1){
                System.out.println("Se guardó modelo por completo!!!");
            }
            return null;
        }
    }

    public static class TaskgetModeloUrl extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            Context context = (Context) objects[0];
            String modelo = (String) objects[1];
            String urlModelo = GuantesDataBase.newInstance2(context).getGuantesInfoDao().getFotoUrlByName(modelo);
            if (!urlModelo.isEmpty()) {
                valor = urlModelo;
            }
            return null;
        }
    }


}
