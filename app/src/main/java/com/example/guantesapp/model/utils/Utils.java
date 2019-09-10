package com.example.guantesapp.model.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.guantesapp.model.entities.Modelo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class Utils {
    public static final String BACKENDLESS_KEY = "D3A5917F-FC73-9C1C-FFBB-41FAF04BD300";
    public static final String APPLICATION_ID = "99E9488F-BC72-1A42-FF41-2FAF16A97300";
    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static List<String> getAllModelos(){
        final List<String>[] list=null;
        Backendless.Data.of(Modelo.class).find(new AsyncCallback<List<Modelo>>() {
            @Override
            public void handleResponse(List<Modelo> response) {
                if (!response.isEmpty()) {
                    list[0] = new ArrayList<>();
                    for (int k = 0; k < response.size(); k++) {
                        list[0].add(response.get(k).getNombre());
                    }

                } else {

                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
        return list[0];
    }

    public static boolean positionChecked(int position) {
        return true;
    }

    public static void unChecked(int position) {

    }

    public static void setChecked(int position) {
        //SharedPreferences sharedPreferences=SharedPreferences.Editor;
    }

    public void saveBitmapsOtherThread(Context context,String urlImage, String modelo) {
        new saveBitmapAsyncTask().execute(context, urlImage, modelo);
    }

    public static class saveBitmapAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            try {
                /*String[] proj = {MediaStore.Images.Media.DISPLAY_NAME};
                Cursor cursorImages = ((Context) objects[0]).getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
                while (cursorImages.moveToNext()){
                    System.out.println(cursorImages.getPosition()+".- Imagen "+cursorImages.getString(cursorImages.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                }*/
                Bitmap eachBitmap = Picasso.with((Context) objects[0]).load(String.valueOf(objects[1])).get();
                MediaStore.Images.Media.insertImage(((Context) (objects[0])).getContentResolver(), eachBitmap, String.valueOf(objects[2]), "Guante Orbit");
            } catch (IOException e) {
                Utils.showToast((Context) objects[0], "Excepcion guardando bitmap - " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
