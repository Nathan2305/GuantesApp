package com.example.guantesapp.model.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.backendless.servercode.annotation.Async;
import com.example.guantesapp.R;
import com.example.guantesapp.model.entities.Guante;
import com.example.guantesapp.model.entities.MRoomTallaCantidad;
import com.example.guantesapp.model.entities.Modelo;
import com.example.guantesapp.model.entities.ModeloxTalla;
import com.example.guantesapp.model.utils.GridAdapterForStock;
import com.example.guantesapp.model.utils.GuantesDataBase;
import com.example.guantesapp.model.utils.Utils;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.guantesapp.model.ui.activities.MainActivity.listaGuantes;

public class AgregarStock extends AppCompatActivity {
    Spinner spinnerModelo, spinnerTalla, spinnerCantidad;
    ArrayAdapter<CharSequence> adapter_tallas, adapter_cantidad;
    static int REQUEST_IMAGE_GALLERY = 100;
    ProgressBar progressBar;
    GridView gridViewModelos;
    String modelo;
    Button addStock;
    String modeloSelected;
    ConstraintLayout layoutParent;
    public static final String MY_APP = "GuantesApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_stock);
        progressBar = findViewById(R.id.progress);
        layoutParent = findViewById(R.id.layoutParent);
        addStock = findViewById(R.id.addStock);
        spinnerModelo = findViewById(R.id.sp_modelo2);
        spinnerTalla = findViewById(R.id.sp_talla2);
        spinnerCantidad = findViewById(R.id.sp_cantidad2);
        gridViewModelos = findViewById(R.id.gridViewFotos);
        if (listaGuantes != null) {
            final ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(AgregarStock.this, R.layout.spinner_item, listaGuantes);
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
                final String talla = (String) spinnerTalla.getSelectedItem();
                final String cantidad = (String) spinnerCantidad.getSelectedItem();
                if (!modeloSelected.isEmpty() && !talla.isEmpty() && !cantidad.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    existeTallaModelo(modeloSelected, talla, cantidad);
                } else {
                    Utils.showToast(getApplicationContext(), "Falta seleccionar datos");
                }
            }
        });
    }

    public void existeTallaModelo(final String modelo, final String talla, final String cantidad) {
        disableViews(true);
        final StringBuilder sb = new StringBuilder();
        sb.append("modelo_link.modelo='").append(modelo).append("'")
                .append(" and talla='").append(talla).append("'");
        DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
        dataQueryBuilder.setWhereClause(sb.toString());
        Backendless.Data.of(ModeloxTalla.class).find(dataQueryBuilder, new AsyncCallback<List<ModeloxTalla>>() {
            @Override
            public void handleResponse(List<ModeloxTalla> response) {
                if (response.size() > 0) {
                    //Existe modelo para esa talla,entonces actualizar
                    int cantidad_found = response.get(0).getCantidad();
                    final int newCantidad = cantidad_found + Integer.parseInt(cantidad);
                    Map<String, Object> changes = new HashMap<>();
                    changes.put("talla", talla);
                    changes.put("cantidad", newCantidad);
                    Backendless.Data.of(ModeloxTalla.class).update(sb.toString(), changes, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse(Integer response) {
                            Utils.showToast(getApplicationContext(), "Se actualizó la tabla");
                            MRoomTallaCantidad mRoomTallaCantidad = new MRoomTallaCantidad();
                            mRoomTallaCantidad.setTalla(talla);
                            mRoomTallaCantidad.setCantidad(newCantidad);
                            mRoomTallaCantidad.setModelo(modelo);
                            updateSQLTallaCantidad(mRoomTallaCantidad);
                            progressBar.setVisibility(View.GONE);
                            disableViews(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Utils.showToast(getApplicationContext(), "No se actualizó la tabla: " + fault.getMessage());
                            progressBar.setVisibility(View.GONE);
                            disableViews(false);
                        }
                    });
                } else {
                    ///No existe modelo para esa talla, crear relación
                    ModeloxTalla modeloxTalla = new ModeloxTalla();
                    modeloxTalla.setCantidad(Integer.parseInt(cantidad));
                    modeloxTalla.setTalla(talla);
                    Backendless.Data.of(ModeloxTalla.class).save(modeloxTalla, new AsyncCallback<ModeloxTalla>() {
                        @Override
                        public void handleResponse(final ModeloxTalla response2) {
                            HashMap<String, Object> parentObject = new HashMap<>();
                            parentObject.put("objectId", response2.getObjectId());

                            Backendless.Data.of("ModeloxTalla").addRelation(parentObject,
                                    "modelo_link", "modelo='" + modelo + "'", new AsyncCallback<Integer>() {
                                        @Override
                                        public void handleResponse(Integer response) {
                                            Utils.showToast(getApplicationContext(), "Se creó la relacion");
                                            MRoomTallaCantidad mRoomTallaCantidad = new MRoomTallaCantidad();
                                            mRoomTallaCantidad.setModelo(modelo);
                                            mRoomTallaCantidad.setCantidad(response2.getCantidad());
                                            mRoomTallaCantidad.setTalla(response2.getTalla());
                                            insertSQLTallaCantidad(mRoomTallaCantidad);
                                            progressBar.setVisibility(View.GONE);
                                            disableViews(false);
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Utils.showToast(getApplicationContext(), "No se creó la relacion :" + fault.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                            disableViews(false);
                                        }
                                    });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Utils.showToast(getApplicationContext(), "No se guardó el objeto: " + fault.getMessage());
                            disableViews(false);
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.showToast(getApplicationContext(), "Error buscando stock: " + fault.getMessage());
                disableViews(false);
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
                    final GridAdapterForStock gridAdapter = new GridAdapterForStock(AgregarStock.this, response);
                    gridViewModelos.setAdapter(gridAdapter);
                    progressBar.setVisibility(View.GONE);
                    gridViewModelos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    public class TaskgetModeloUrl extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            return null;
        }
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


    public void insertSQLTallaCantidad(MRoomTallaCantidad mRoomTallaCantidad) {
        new TaskInsertTallaCantidad().execute(mRoomTallaCantidad);
    }

    public class TaskInsertTallaCantidad extends AsyncTask<MRoomTallaCantidad, Void, Void> {
        @Override
        protected Void doInBackground(MRoomTallaCantidad... mRoomTallaCantidads) {
            long id = GuantesDataBase.newInstance3(AgregarStock.this).getGuantesInfoDao().insertTallaCantidad(mRoomTallaCantidads[0]);
            if (id > -1) {
                System.out.println("Se guardó en tabla TallaCantidad");
            } else {
                System.out.println("No se guardó en tabla TallaCantidad");
            }
            return null;
        }

    }

    public void updateSQLTallaCantidad(MRoomTallaCantidad mRoomTallaCantidad) {
        new TaskUpdateTallaCantidad().execute(mRoomTallaCantidad);
    }

    public class TaskUpdateTallaCantidad extends AsyncTask<MRoomTallaCantidad, Void, Void> {
        @Override
        protected Void doInBackground(MRoomTallaCantidad... mRoomTallaCantidads) {
            String modelo = mRoomTallaCantidads[0].getModelo();
            String talla = mRoomTallaCantidads[0].getTalla();
            int cantidad = mRoomTallaCantidads[0].getCantidad();
            MRoomTallaCantidad mRoomTallaCantidadToUpdate = null;
            List<MRoomTallaCantidad> listmRoomTallaCantidad = GuantesDataBase.newInstance3(AgregarStock.this).getGuantesInfoDao().getIdTallaCantidad();
            if (listmRoomTallaCantidad != null && listmRoomTallaCantidad.size() > 0) {
                for (MRoomTallaCantidad eachModelTallaCantidad : listmRoomTallaCantidad) {
                    if (modelo.equalsIgnoreCase(eachModelTallaCantidad.getModelo())
                            && talla.equalsIgnoreCase(eachModelTallaCantidad.getTalla())) {
                        mRoomTallaCantidadToUpdate = eachModelTallaCantidad;
                        mRoomTallaCantidadToUpdate.setCantidad(cantidad);
                        break;
                    }
                }
                if (mRoomTallaCantidadToUpdate != null) {
                    GuantesDataBase.newInstance3(AgregarStock.this).getGuantesInfoDao().updateTallaCantidad(mRoomTallaCantidadToUpdate);
                }

            }
            return null;
        }
    }
}
