package com.example.guantesapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

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

}
