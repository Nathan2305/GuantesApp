package com.example.guantesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.util.ArrayList;
import java.util.List;

import static com.example.guantesapp.MainActivity.modelos;

public class ActivityVenta extends AppCompatActivity {

    Spinner spModelo, spTalla, spCantidad;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        spModelo=findViewById(R.id.spModelo);
        spTalla=findViewById(R.id.spTalla);
        spCantidad=findViewById(R.id.spCantidad);
        recycler=findViewById(R.id.recycler);
        layoutManager=new LinearLayoutManager(ActivityVenta.this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        progressBar=findViewById(R.id.progress);
        FadingCircle fadingCircle=new FadingCircle();
        progressBar.setProgressDrawable(fadingCircle);
        if (modelos != null) {
            ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, modelos);
            adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spModelo.setAdapter(adapter_modelos);
        }
        spModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String modelo = ((String) parent.getItemAtPosition(position));
                if (!modelo.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    showModelos(modelo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void showModelos(final String modelo) {
        progressBar.setVisibility(View.VISIBLE);
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("nombre like '" + modelo + "%'");
        Backendless.Data.of(ModeloChild.class).find(queryBuilder, new AsyncCallback<List<ModeloChild>>() {
            @Override
            public void handleResponse(List<ModeloChild> response) {
                if (!response.isEmpty()) {
                    List<String> listFoto = new ArrayList<>();
                    List<String> listModelo = new ArrayList<>();

                    for (ModeloChild modeloChild : response) {
                        listFoto.add(modeloChild.getImagenUrl());
                        listModelo.add(modeloChild.getNombre());
                    }
                    adapter = new CustomAdapterforFotos(getApplicationContext(), listFoto, listModelo);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setHasFixedSize(true);
                    recycler.setAdapter(adapter);
                    ((CustomAdapterforFotos) adapter).setOnItemClickListener(new CustomAdapterforFotos.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.showToast(getApplicationContext(), "Algo salió mal buscando modelo " + modelo + " : " + fault.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
