package com.example.guantesapp;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import java.io.File;
import java.util.List;
import static com.example.guantesapp.MainActivity.modelos;

public class AgregarStock extends AppCompatActivity {
    Spinner sp_modelo2, sp_talla2;
    ArrayAdapter<CharSequence> adapter_tallas;
    static int REQUEST_IMAGE_GALLERY = 100;
    ProgressBar progressBar;
    RecyclerView rec_fotos;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_stock);
        progressBar=findViewById(R.id.progress);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setProgressDrawable(doubleBounce);
        sp_modelo2 = findViewById(R.id.sp_modelo2);
        sp_talla2 = findViewById(R.id.sp_talla2);
        rec_fotos = findViewById(R.id.rec_fotos);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item,modelos);
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
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("modelo='" + modelo + "'");
        Backendless.Data.of(Imagen.class).find(queryBuilder, new AsyncCallback<List<Imagen>>() {
            @Override
            public void handleResponse(List<Imagen> response) {
                if (!response.isEmpty()) {
                    adapter = new CustomAdapterforFotos(getApplicationContext(), response);
                    rec_fotos.setLayoutManager(layoutManager);
                    rec_fotos.setHasFixedSize(true);
                    rec_fotos.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    ((CustomAdapterforFotos)adapter).setOnItemClickListener(new CustomAdapterforFotos.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    });

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"No hay modelos "+ modelo +" aún",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            Toast.makeText(getApplicationContext(),"Error getting fotos..",Toast.LENGTH_LONG).show();
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


}
