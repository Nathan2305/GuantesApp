package com.example.guantesapp;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.guantesapp.MainActivity.modelos;

public class AgregarStock extends AppCompatActivity {
    Spinner spinnerModelo, spinnerTalla, spinnerCantidad;
    ArrayAdapter<CharSequence> adapter_tallas, adapter_cantidad;
    static int REQUEST_IMAGE_GALLERY = 100;
    ProgressBar progressBar;
    GridView rec_fotos;
    String modelo;
    Button addStock;
    ConstraintLayout layoutParent;
    public static final String MY_APP = "GuantesApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_stock);
        layoutParent = findViewById(R.id.layoutParent);
        progressBar = findViewById(R.id.progress);
        addStock = findViewById(R.id.addStock);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setProgressDrawable(fadingCircle);
        spinnerModelo = findViewById(R.id.sp_modelo2);
        spinnerTalla = findViewById(R.id.sp_talla2);
        spinnerCantidad = findViewById(R.id.sp_cantidad2);
        rec_fotos = findViewById(R.id.gridViewFotos);
        if (modelos != null) {
            final ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, modelos);
            adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerModelo.setAdapter(adapter_modelos);
        }

        adapter_tallas = ArrayAdapter.createFromResource(this, R.array.tallas_guantes, android.R.layout.simple_spinner_item);
        adapter_tallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_cantidad = ArrayAdapter.createFromResource(this, R.array.cantidad, android.R.layout.simple_spinner_item);
        adapter_cantidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTalla.setAdapter(adapter_tallas);
        spinnerCantidad.setAdapter(adapter_cantidad);

        spinnerModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelo = ((String) parent.getItemAtPosition(position));
                if (!modelo.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    showModelos(modelo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> fotosChecked = CustomSockGridViewAdapter.getUrlFotosChecked();
                final String talla = (String) spinnerTalla.getSelectedItem();
                final String cantidad = (String) spinnerCantidad.getSelectedItem();
                if (!fotosChecked.isEmpty() && !talla.isEmpty() && !cantidad.isEmpty()) {
                    disableViews(true);
                    progressBar.setVisibility(View.VISIBLE);
                    DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("imagenUrl='" + fotosChecked.get(0) + "'");
                    queryBuilder.setWhereClause(stringBuffer.toString());
                    Backendless.Data.of(ModeloChild.class).find(queryBuilder, new AsyncCallback<List<ModeloChild>>() {
                        @Override
                        public void handleResponse(List<ModeloChild> response) {
                            if (!response.isEmpty()) {
                                ModeloChild foundChild = response.get(0);
                                createTallaForModelChild(foundChild, talla, Integer.parseInt(cantidad));
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.i(MY_APP, "Error buscando modelo - " + fault.getMessage());
                            disableViews(false);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Uno o más campos vacíos", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private void showModelos(final String modelo) {
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
                    //adapter = new CustomAdapterforFotos(getApplicationContext(), listFoto, listModelo);

                    rec_fotos.setAdapter(new CustomSockGridViewAdapter(AgregarStock.this, listFoto, listModelo));
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goGallery();
            }
        }
    }

    private void goGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictuPath = pictureDir.getPath();
        Uri data = Uri.parse(pictuPath);
        pickPhotoIntent.setDataAndType(data, "image/*");
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_GALLERY);
    }


    private void createTallaForModelChild(final ModeloChild modeloChild, final String talla, final int cantidad) {
        final DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("modelo ='" + modeloChild.getNombre() + "'")
                .append(" and tallita='" + talla + "' ");
        dataQueryBuilder.setWhereClause(stringBuilder.toString());
        //Buscar si ya existe esa talla para el modelo respectivo
        Backendless.Data.of(Talla.class).find(dataQueryBuilder, new AsyncCallback<List<Talla>>() {
            @Override
            public void handleResponse(List<Talla> tallasFound) {
                if (tallasFound.isEmpty()) {   //se crea talla para ese modelo
                    Talla tallaObj = new Talla();
                    tallaObj.setModelo(modeloChild.getNombre());
                    tallaObj.setTallita(talla);
                    tallaObj.setCantidad(cantidad);
                    Backendless.Data.of(Talla.class).save(tallaObj, new AsyncCallback<Talla>() {
                        @Override
                        public void handleResponse(final Talla tallaCreated) {
                            HashMap<String, Object> parentObject = new HashMap<String, Object>();
                            parentObject.put("objectId", modeloChild.getObjectId());

                            HashMap<String, Object> childObject = new HashMap<String, Object>();
                            childObject.put("objectId", tallaCreated.getObjectId());

                            ArrayList<Map> children = new ArrayList<Map>();
                            children.add(childObject);
                            Backendless.Data.of("ModeloChild").addRelation(parentObject, "talla",
                                    children, new AsyncCallback<Integer>() {
                                        @Override
                                        public void handleResponse(Integer response) {
                                            Utils.showToast(getApplicationContext(), "Se creó stock para modelo " +
                                                    modeloChild.getNombre() + " y talla " + tallaCreated.getTallita());
                                            progressBar.setVisibility(View.GONE);
                                            disableViews(false);
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Log.i(MY_APP, "No se guardo relacion Child - Talla.. " + fault.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                            disableViews(false);
                                        }
                                    });

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.i(MY_APP, "Error creando Talla.. " + fault.getMessage());
                            progressBar.setVisibility(View.GONE);
                            disableViews(false);
                        }
                    });
                } else {   //Ya existe talla para ese modelo, entonces se actualizará su cantidad
                    Map<String, Object> changes = new HashMap<>();
                    changes.put("cantidad", tallasFound.get(0).getCantidad() + cantidad);
                    Backendless.Data.of(Talla.class).update(dataQueryBuilder.getWhereClause(), changes, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse(Integer response) {
                            Utils.showToast(getApplicationContext(), "Se actualizó el stock para modelo " + modeloChild.getNombre() + " y talla " + talla);
                            progressBar.setVisibility(View.GONE);
                            disableViews(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.i(MY_APP, "Error actualizando Stock: " + fault.getMessage());
                            progressBar.setVisibility(View.GONE);
                            disableViews(false);
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i(MY_APP, "Error finding talla... " + fault.getMessage());
                progressBar.setVisibility(View.GONE);
                disableViews(false);
            }
        });
    }

    public void disableViews(boolean val) {
        if (val) {
            layoutParent.setAlpha(0.5f);
            addStock.setEnabled(false);
            spinnerModelo.setEnabled(false);
            spinnerTalla.setEnabled(false);
            spinnerCantidad.setEnabled(false);
        } else {
            layoutParent.setAlpha(1f);
            addStock.setEnabled(true);
            spinnerModelo.setEnabled(true);
            spinnerTalla.setEnabled(true);
            spinnerCantidad.setEnabled(true);
        }

    }
}
