package com.example.guantesapp.model.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.ModeloxTalla;

import java.util.List;

public class ResumenGuantesActivity extends AppCompatActivity {
    TextView totalGuantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_guantes);
        totalGuantes = findViewById(R.id.totalGuantes);

        DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
        dataQueryBuilder.setPageSize(100);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("cantidad>0");
        dataQueryBuilder.setWhereClause(stringBuilder.toString());
        Backendless.Data.of(ModeloxTalla.class).find(dataQueryBuilder, new AsyncCallback<List<ModeloxTalla>>() {
            @Override
            public void handleResponse(List<ModeloxTalla> response) {
                int cantidad = 0;
                for (ModeloxTalla modeloxTalla : response) {
                    cantidad = cantidad + modeloxTalla.getCantidad();
                }
                totalGuantes.setText(String.valueOf(cantidad));
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }
}
