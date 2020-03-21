package com.example.guantesapp.model.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.guantesapp.model.utils.AdapterVenta;
import com.example.guantesapp.R;
import com.example.guantesapp.model.utils.Utils;
import com.github.ybq.android.spinkit.style.FadingCircle;

import static com.example.guantesapp.model.ui.activities.MainActivity.listaGuantes;

public class ActivityVenta extends AppCompatActivity {

    Spinner spModelo, spTalla, spCantidad;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ProgressBar progressBar;
    Button addVenta;
    ArrayAdapter<CharSequence> adapter_tallas;
    ArrayAdapter<CharSequence> adapter_cantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        spModelo = findViewById(R.id.spModelo);
        spTalla = findViewById(R.id.spTalla);
        spCantidad = findViewById(R.id.spCantidad);
        recycler = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(ActivityVenta.this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
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
            ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, listaGuantes);
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
                if (adapter != null) {
                    try {
                        if (!((AdapterVenta) adapter).getModeloChecked().isEmpty()){
                           /* String modeloChecked = ((AdapterVenta) adapter).getModeloChecked().get(0);
                            String talla = (String) spTalla.getSelectedItem();
                            final String cantidad = (String) spCantidad.getSelectedItem();
                            if (!talla.isEmpty() && !cantidad.isEmpty() && !modeloChecked.isEmpty()) {
                                final DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
                                StringBuilder sb = new StringBuilder();
                                sb.append("modelo='" + modeloChecked + "'")
                                        .append(" and tallita='" + talla + "'");
                                dataQueryBuilder.setWhereClause(sb.toString());
                                Backendless.Data.of(Talla.class).find(dataQueryBuilder, new AsyncCallback<List<Talla>>() {
                                    @Override
                                    public void handleResponse(List<Talla> response) {
                                        if (!response.isEmpty()) {
                                            HashMap<String, Object> changes = new HashMap<>();
                                            int newCantidad = response.get(0).getCantidad() - Integer.parseInt(cantidad);
                                            changes.put("cantidad", newCantidad);
                                            Backendless.Data.of(Talla.class).update(dataQueryBuilder.getWhereClause(), changes, new AsyncCallback<Integer>() {
                                                @Override
                                                public void handleResponse(Integer response) {
                                                    Utils.showToast(getApplicationContext(), "Se registró la venta exitosamente!!");
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    System.out.println("Error actualizando venta " + fault.getMessage());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Utils.showToast(getApplicationContext(), "Error buscando talla " + fault.getMessage());
                                    }
                                });
                            } else {
                                Utils.showToast(getApplicationContext(), "Selecciona modelo, talla y cantidad..");
                            }*/
                        }else{
                            Utils.showToast(getApplicationContext(), "No hay modelos seleccionados");
                        }
                    }catch (Exception e){
                        System.out.println("Exepcion getting ModeloChecked... "+e.getMessage());
                    }



                } else {
                    Utils.showToast(getApplicationContext(), "Adapter nulo!!");
                }
            }
        });

    }


    private void showModelos(final String modelo) {
        /*progressBar.setVisibility(View.VISIBLE);
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("nombre like '" + modelo + "%'")
                .append(" and talla.cantidad>0");
        queryBuilder.setWhereClause(stringBuilder.toString());
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
                    adapter = new AdapterVenta(getApplicationContext(), listFoto, listModelo);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setHasFixedSize(true);
                    recycler.setAdapter(adapter);
                    ((AdapterVenta) adapter).setOnItemClickListener(new AdapterVenta.OnItemClickListener() {
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
        });*/

    }
}
