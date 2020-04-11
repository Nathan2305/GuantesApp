package com.example.guantesapp.model.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.guantesapp.model.entities.Modelo;
import com.example.guantesapp.model.entities.ModeloxTalla;
import com.example.guantesapp.model.utils.AdapterVenta;
import com.example.guantesapp.R;
import com.example.guantesapp.model.utils.GridAdapterForStock;
import com.example.guantesapp.model.utils.Utils;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.guantesapp.model.ui.activities.MainActivity.listaGuantes;

public class ActivityVenta extends AppCompatActivity {

    Spinner spModelo, spTalla, spCantidad;
    GridView gridView;
    ProgressBar progressBar;
    Button addVenta;
    ArrayAdapter<CharSequence> adapter_tallas;
    ArrayAdapter<CharSequence> adapter_cantidad;
    String modeloSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        spModelo = findViewById(R.id.spModelo);
        spTalla = findViewById(R.id.spTalla);
        spCantidad = findViewById(R.id.spCantidad);
        gridView = findViewById(R.id.gridView);
        progressBar = findViewById(R.id.progress);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setProgressDrawable(fadingCircle);
        addVenta = findViewById(R.id.addVenta);

        adapter_tallas = ArrayAdapter.createFromResource(this, R.array.tallas_guantes, android.R.layout.simple_spinner_item);
        adapter_tallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTalla.setAdapter(adapter_tallas);

        adapter_cantidad = ArrayAdapter.createFromResource(this, R.array.cantidad, android.R.layout.simple_spinner_item);
        adapter_cantidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCantidad.setAdapter(adapter_cantidad);

        if (listaGuantes != null) {
            ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(this, R.layout.spinner_item, listaGuantes);
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
        addVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tallaSelected = (String) spTalla.getSelectedItem();
                final String cantidad = (String) spCantidad.getSelectedItem();
                if (!tallaSelected.isEmpty() && !cantidad.isEmpty() && !modeloSelected.isEmpty()) {
                    DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
                    final StringBuilder sb = new StringBuilder();
                    sb.append("modelo_link.modelo='" + modeloSelected + "'")
                            .append(" and talla='" + tallaSelected + "'")
                            .append(" and cantidad>0");
                    dataQueryBuilder.setWhereClause(sb.toString());
                    Backendless.Data.of(ModeloxTalla.class).find(dataQueryBuilder, new AsyncCallback<List<ModeloxTalla>>() {
                        @Override
                        public void handleResponse(List<ModeloxTalla> response) {
                            if (response.size() > 0) {
                                Map<String, Object> changes = new HashMap<>();
                                changes.put("cantidad", response.get(0).getCantidad() - Integer.parseInt(cantidad));
                                Backendless.Data.of(ModeloxTalla.class).update(sb.toString(), changes, new AsyncCallback<Integer>() {
                                    @Override
                                    public void handleResponse(Integer response) {
                                        Utils.showToast(getApplicationContext(), "Se registró la venta exitosamente");
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Utils.showToast(getApplicationContext(), "No se registró la venta exitosamente: " + fault.getMessage());
                                    }
                                });
                            } else {
                                Utils.showToast(getApplicationContext(), "No se encontraron coincidencias , no se registró nada!!");
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Utils.showToast(getApplicationContext(), "Error getting ModeloxTalla: " + fault.getMessage());
                        }
                    });
                } else {
                    Utils.showToast(getApplicationContext(), "Falta llenar datos!!");
                }
            }
        });

    }


    private void showModelos(final String modelo) {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("modelo like '" + modelo + "%'");
        Backendless.Data.of(Modelo.class).find(queryBuilder, new AsyncCallback<List<Modelo>>() {
            @Override
            public void handleResponse(List<Modelo> response) {
                if (!response.isEmpty()) {
                    GridAdapterForStock gridAdapter = new GridAdapterForStock(ActivityVenta.this, response);
                    gridView.setAdapter(gridAdapter);
                    progressBar.setVisibility(View.INVISIBLE);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            modeloSelected = parent.getItemAtPosition(position).toString();
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
