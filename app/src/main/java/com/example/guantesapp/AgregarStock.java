package com.example.guantesapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class AgregarStock extends AppCompatActivity {
    Spinner sp_modelo2, sp_talla2;
    ArrayAdapter<CharSequence> adapter_modelos;
    ArrayAdapter<CharSequence> adapter_tallas;
    static int REQUEST_IMAGE_GALLERY = 100;
    RecyclerView rec_fotos;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    EditText cantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_stock);
        sp_modelo2 = findViewById(R.id.sp_modelo2);
        sp_talla2 = findViewById(R.id.sp_talla2);
        rec_fotos = findViewById(R.id.rec_fotos);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        cantidad = findViewById(R.id.cantidad);
        adapter_modelos = ArrayAdapter.createFromResource(this, R.array.modelos_guantes, android.R.layout.simple_spinner_item);
        adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_modelo2.setAdapter(adapter_modelos);

        adapter_tallas = ArrayAdapter.createFromResource(this, R.array.tallas_guantes, android.R.layout.simple_spinner_item);
        adapter_tallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_talla2.setAdapter(adapter_tallas);

        sp_modelo2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String modelo = ((String) parent.getItemAtPosition(position));
                if (!modelo.isEmpty()) {
                    showModelsPerPkFoto(modelo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showModelsPerPkFoto(String modelo) {
        new TaskFindModelsPerFotoPk().execute(modelo);
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

    public class TaskFindModelsPerFotoPk extends AsyncTask<String, Void, Void> {
        boolean isThereFotos = false;
        List<Foto> fotoList;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                List<Foto> foundFotos = AppDataBase.getInstanceFotoBD(getApplicationContext()).getFotoDao().getAllFoto();
                if (!foundFotos.isEmpty()) {
                    fotoList = new ArrayList<>();
                    for (Foto eachFoto : foundFotos) {
                        if (eachFoto.getId_foto().contains(strings[0].toLowerCase())) {
                            fotoList.add(eachFoto);
                        }
                    }
                    isThereFotos = true;
                }
            } catch (Exception e) {
                System.out.println("Error getting allFotos..." + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isThereFotos) {
                Toast.makeText(getApplicationContext(), "Se encontraron fotos", Toast.LENGTH_SHORT).show();
                adapter = new CustomAdapterforFotos(getApplicationContext(), fotoList);
                rec_fotos.setLayoutManager(layoutManager);
                rec_fotos.setHasFixedSize(true);
                rec_fotos.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron fotos", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
