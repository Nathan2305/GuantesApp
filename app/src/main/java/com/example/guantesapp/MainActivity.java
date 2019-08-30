package com.example.guantesapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Spinner sp_modelo, sp_talla;
    FloatingActionButton fab_add, fab_add_stock, fab_add_photo;
    public static List<String> modelos;
    Button consultar;
    RecyclerView recFound;
    RecyclerView.LayoutManager layoutManager;
    ArrayAdapter<CharSequence> adapter_tallas;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    ProgressBar progress;
    boolean isOpen = false;
    List<Talla> listTalla;
    List<ModeloChild> listModeloChild;
    RecyclerView.Adapter adapterTallaModeloChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Backendless.initApp(getApplicationContext(), Utils.APPLICATION_ID, Utils.BACKENDLESS_KEY);
        sp_modelo = findViewById(R.id.sp_modelo);
        progress = findViewById(R.id.progress);
        final FadingCircle fadingCircle = new FadingCircle();
        progress.setProgressDrawable(fadingCircle);
        sp_talla = findViewById(R.id.sp_talla);
        fab_add = findViewById(R.id.fab_add);
        fab_add_stock = findViewById(R.id.fab_add_stock);
        fab_add_photo = findViewById(R.id.fab_add_photo);
        recFound = findViewById(R.id.recFound);
        consultar = findViewById(R.id.consultar);
        layoutManager = new LinearLayoutManager(this);
        recFound.setLayoutManager(layoutManager);
        recFound.setHasFixedSize(true);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        getAllModelos();
        adapter_tallas = ArrayAdapter.createFromResource(this, R.array.tallas_guantes, android.R.layout.simple_spinner_item);
        adapter_tallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_talla.setAdapter(adapter_tallas);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnimation();
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fab_add_stock.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.celeste)));
            fab_add_photo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.naranaja)));
        }
        fab_add_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_add_photo.startAnimation(fabClose);
                isOpen = false;
                startActivity(new Intent(getApplicationContext(), AgregarStock.class));
            }
        });
        fab_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_add_stock.startAnimation(fabClose);
                isOpen = false;
                startActivity(new Intent(getApplicationContext(), AgregarImagen.class));
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                listTalla = new ArrayList<>();
                listModeloChild = new ArrayList<>();
                final String modelo = (String) sp_modelo.getSelectedItem();
                final String talla = (String) sp_talla.getSelectedItem();
                final DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
                dataQueryBuilder.setPageSize(50);
                StringBuilder sb = new StringBuilder();
                final List<String> modelosUrl = new ArrayList<>();
                if (!talla.isEmpty()) {
                    sb.append(" tallita='" + talla + "'")
                            .append(" and modelo like'" + modelo + "%'")
                            .append(" and cantidad>0");
                    dataQueryBuilder.setWhereClause(sb.toString());
                    Backendless.Data.of(Talla.class).find(dataQueryBuilder, new AsyncCallback<List<Talla>>() {
                        @Override
                        public void handleResponse(List<Talla> response) {
                            if (!response.isEmpty()) {
                                for (Talla auxTalla : response) {
                                    listTalla.add(auxTalla);
                                }
                                Backendless.Data.of(ModeloChild.class).find(new AsyncCallback<List<ModeloChild>>() {
                                    @Override
                                    public void handleResponse(List<ModeloChild> response) {
                                        if (!response.isEmpty()) {
                                            for (ModeloChild modeloChild : response) {
                                                for (Talla auxTalla : listTalla) {
                                                    if (modeloChild.getNombre().equalsIgnoreCase(auxTalla.getModelo())) {
                                                        //listModeloChild.add(modeloChild);
                                                        modelosUrl.add(modeloChild.getImagenUrl());
                                                    }
                                                }
                                            }
                                            adapterTallaModeloChild = new AdapterTallaModeloChild(getApplicationContext(), listTalla, modelosUrl);
                                            recFound.setAdapter(adapterTallaModeloChild);
                                        }
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Utils.showToast(getApplicationContext(), "Error buscando modeloChild - " + fault.getMessage());
                                        progress.setVisibility(View.GONE);
                                    }
                                });


                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Utils.showToast(getApplicationContext(), "Error buscando talla - " + fault.getMessage());
                            progress.setVisibility(View.GONE);
                        }
                    });
                } else {
                    sb.append(" nombre like '" + modelo + "%'")
                            .append(" and talla.tallita!=null");
                    dataQueryBuilder.setWhereClause(sb.toString());
                    Backendless.Data.of(ModeloChild.class).find(dataQueryBuilder, new AsyncCallback<List<ModeloChild>>() {
                        @Override
                        public void handleResponse(List<ModeloChild> response) {
                            if (!response.isEmpty()) {
                                listModeloChild = response;
                                DataQueryBuilder dataQ=DataQueryBuilder.create();
                                dataQ.setPageSize(50);
                                Backendless.Data.of(Talla.class).find(dataQ,new AsyncCallback<List<Talla>>() {
                                    @Override
                                    public void handleResponse(List<Talla> response) {
                                        if (!response.isEmpty()) {
                                            for (Talla tallaobj : response) {
                                                for (ModeloChild modeloChild : listModeloChild) {
                                                    if (tallaobj.getModelo().equalsIgnoreCase(modeloChild.getNombre())) {
                                                        listTalla.add(tallaobj);
                                                    }
                                                }
                                            }
                                            Backendless.Data.of(ModeloChild.class).find(dataQueryBuilder, new AsyncCallback<List<ModeloChild>>() {
                                                @Override
                                                public void handleResponse(List<ModeloChild> response) {
                                                    if (!response.isEmpty()) {
                                                        for (Talla tallita : listTalla) {
                                                            for (ModeloChild modeloChild : response) {
                                                                if (tallita.getModelo().equalsIgnoreCase(modeloChild.getNombre())) {
                                                                    modelosUrl.add(modeloChild.getImagenUrl());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    adapterTallaModeloChild = new AdapterTallaModeloChild(getApplicationContext(), listTalla, modelosUrl);
                                                    recFound.setAdapter(adapterTallaModeloChild);
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    Utils.showToast(MainActivity.this, "Error buscando CHild -" + fault.getMessage());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Utils.showToast(getApplicationContext(), "Error getting Tallas -" + fault.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Utils.showToast(getApplicationContext(), "Error getting ModeloChild -" + fault.getMessage());
                        }
                    });
                }

                progress.setVisibility(View.GONE);
            }

        });

    }

    public void getAllModelos() {
        Backendless.Data.of(Modelo.class).find(new AsyncCallback<List<Modelo>>() {
            @Override
            public void handleResponse(List<Modelo> response) {
                if (!response.isEmpty()) {
                    modelos = new ArrayList<>();
                    for (int k = 0; k < response.size(); k++) {
                        modelos.add(response.get(k).getNombre());
                    }
                    try {
                        ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, modelos);
                        adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_modelo.setAdapter(adapter_modelos);
                    } catch (Exception e) {
                        System.out.println("Exception Guava... " + e.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No se han agredado modelos aún", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Algo salió mal.." + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAnimation() {
        if (isOpen) {
            fab_add.startAnimation(rotateForward);
            fab_add_stock.startAnimation(fabClose);
            fab_add_photo.startAnimation(fabClose);
            fab_add_stock.setClickable(false);
            fab_add_photo.setClickable(false);
            isOpen = false;
        } else {
            fab_add.startAnimation(rotateBackward);
            fab_add_stock.startAnimation(fabOpen);
            fab_add_photo.startAnimation(fabOpen);
            fab_add_stock.setClickable(true);
            fab_add_photo.setClickable(true);
            isOpen = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllModelos();
    }
}
