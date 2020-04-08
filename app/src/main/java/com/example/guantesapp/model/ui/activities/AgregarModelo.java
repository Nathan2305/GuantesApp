package com.example.guantesapp.model.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.example.guantesapp.model.entities.Guante;
import com.example.guantesapp.model.entities.MRoomDB;
import com.example.guantesapp.model.entities.MRoomUrlDB;
import com.example.guantesapp.model.entities.Modelo;
import com.example.guantesapp.R;
import com.example.guantesapp.model.utils.GuantesDataBase;
import com.example.guantesapp.model.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.guantesapp.model.ui.activities.AgregarStock.REQUEST_IMAGE_GALLERY;
import static com.example.guantesapp.model.ui.activities.MainActivity.listaGuantes;

public class AgregarModelo extends AppCompatActivity {

    ImageView foto_guante;
    Button btn_save;
    Spinner spinnerModelo;
    String pathImage;
    CheckBox checkNuevo;
    boolean imageEmpty = true;
    TextView textViewNuevo;
    boolean newModel = false;
    EditText nuevoModelo;
    Bitmap selectedImage;
    ProgressBar progress;
    public static final String pathRemote = "ModelosGuantes";
    ArrayAdapter<String> adapter_modelos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_imagen);
        checkNuevo = findViewById(R.id.checkNuevo);
        textViewNuevo = findViewById(R.id.textViewNuevo);
        nuevoModelo = findViewById(R.id.nuevoModelo);
        progress = findViewById(R.id.progress);
        spinnerModelo = findViewById(R.id.name_foto);
        if (listaGuantes != null) {
            adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, listaGuantes);
            adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerModelo.setAdapter(adapter_modelos);
        }

        foto_guante = findViewById(R.id.foto_guante);
        btn_save = findViewById(R.id.btn_save);
        spinnerModelo = findViewById(R.id.name_foto);
        foto_guante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionGallery();
            }
        });
        checkNuevo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewNuevo.setVisibility(View.VISIBLE);
                    nuevoModelo.setVisibility(View.VISIBLE);
                    spinnerModelo.setEnabled(false);
                    spinnerModelo.setClickable(false);
                } else {
                    textViewNuevo.setVisibility(View.GONE);
                    nuevoModelo.setVisibility(View.GONE);
                    spinnerModelo.setEnabled(true);
                    spinnerModelo.setClickable(true);
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageEmpty) {
                    progress.setVisibility(View.VISIBLE);
                    String nombreModelo;
                    File fileToUpload = new File(pathImage);
                    if (!checkNuevo.isChecked()) {
                        nombreModelo = spinnerModelo.getSelectedItem().toString();
                    } else {
                        nombreModelo = nuevoModelo.getText().toString();
                        newModel = true;
                    }
                    if (!nombreModelo.isEmpty()) {
                        saveImageFile(fileToUpload, pathRemote, nombreModelo);
                    } else {
                        Utils.showToast(getApplicationContext(), "Ingresa el nombre del modelo...");
                        progress.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccion una imagen...", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void saveImageFile(File fileToUpload, String pathRemote, final String modelo) {
        Backendless.Files.upload(fileToUpload, pathRemote, new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(BackendlessFile response) {
                //Se guardó la imagen
                foto_guante.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_upload_black_24dp));
                guardarTablaModelo(response.getFileURL(), modelo);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Algo salió mal subiendo FotoFile ...." + fault.getMessage(),
                        Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void guardarTablaModelo(String imageUrl, final String nombre) {
        //Guardar en tabla Modelo
        final Guante guante = new Guante(nombre);
        if (newModel) { //Se debe guardar en tablas Modelo y Guante Modelo-01234
            try {
                MRoomDB mRoomDB = new MRoomDB();
                mRoomDB.setId(nombre);
                mRoomDB.setOrden(0);
                saveModelIntoSQLite(mRoomDB);
                final String nom_modeloFull = guante.getName() + "-" + mRoomDB.getOrden();
                final Modelo modelo = new Modelo(nom_modeloFull);
                modelo.setFoto_url(imageUrl);

                Backendless.Data.of(Modelo.class).save(modelo, new AsyncCallback<Modelo>() {
                    @Override
                    public void handleResponse(Modelo response) {
                        Utils.showToast(AgregarModelo.this, "Se guardó el modelo " + response.getName() + " correctamente");
                        guardarTablaGuante(guante.getName(), modelo.getModelo(), modelo.getFoto_url());
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Utils.showToast(AgregarModelo.this, "Algo salió mal guardando modelo " + modelo.getName());
                    }
                });
                progress.setVisibility(View.GONE);
            } catch (Exception e) {
                System.out.println("Excepcion : " + e.getMessage());
            }
        } else { //Guardar en tabla Modelo , Obtener orden en Room
            getModeloFromRoomDBSaveBknd(new String[]{guante.getName(), imageUrl});
            progress.setVisibility(View.GONE);
        }

    }

    private void guardarTablaGuante(final String nombre, final String modeloFullIntoroom, final String fotoUrlIntoRoom) {
        Guante guante = new Guante(nombre);
        Backendless.Data.of(Guante.class).save(guante, new AsyncCallback<Guante>() {
            @Override
            public void handleResponse(Guante response) {
                Utils.showToast(AgregarModelo.this, "Se guardó el Guante " + response.getName() + " correctamente");
                if (listaGuantes == null) {
                    listaGuantes = new ArrayList<>();
                    adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, listaGuantes);
                    adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerModelo.setAdapter(adapter_modelos);
                } else {
                    adapter_modelos.setNotifyOnChange(true);
                    listaGuantes.add(response.getName());
                }
                MRoomUrlDB mRoomUrlDB = new MRoomUrlDB();
                mRoomUrlDB.setModelo(modeloFullIntoroom);
                mRoomUrlDB.setFoto_url(fotoUrlIntoRoom);
                insertModeloUrl(mRoomUrlDB);
                checkNuevo.setChecked(false);
                newModel=false;
                spinnerModelo.setEnabled(true);
                spinnerModelo.setClickable(true);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.showToast(AgregarModelo.this, "No se guardó el Guante " + nombre + " correctamente - " + fault.getMessage());
            }
        });
    }

    private void saveModelIntoSQLite(MRoomDB mRoomDB) {
        new TaskSaveModelRoom().execute(mRoomDB);
    }

    private void getModeloFromRoomDBSaveBknd(String[] strings) {
        new TaskgetModeloFromRoom().execute(strings);
    }

    public class TaskgetModeloFromRoom extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                String name = strings[0];
                String fotoUrl = strings[1];
                int ordenByModel = GuantesDataBase.newInstance(getApplicationContext()).getGuantesInfoDao().getOrdenById(name);
                if (ordenByModel >= 0) {
                    ordenByModel++;
                    MRoomDB mRoomDB = new MRoomDB();
                    mRoomDB.setId(name);
                    mRoomDB.setOrden(ordenByModel);
                    GuantesDataBase.newInstance(getApplicationContext()).getGuantesInfoDao().updateOrden(mRoomDB);

                    int newordenToSave = GuantesDataBase.newInstance(getApplicationContext()).getGuantesInfoDao().getOrdenById(name);

                    Modelo modelo = new Modelo();
                    modelo.setModelo(mRoomDB.getId() + "-" + newordenToSave);
                    modelo.setFoto_url(fotoUrl);
                    Backendless.Data.of(Modelo.class).save(modelo, new AsyncCallback<Modelo>() {
                        @Override
                        public void handleResponse(Modelo response) {
                            System.out.println("Se guardó modelo ya existente");
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            System.out.println("No se guardó modelo ya existente");
                        }
                    });
                    progress.setVisibility(View.GONE);

                }
            } catch (Exception e) {
                System.out.println("Exececiopmn Room :" + e.getMessage());

            }
            return null;
        }
    }

    private void checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
        } else {
            goGallery();
        }
    }

    public void insertModeloUrl(MRoomUrlDB mRoomUrlDB) {
        try {
            new TaskInserIntoSQLModeloUrl().execute(mRoomUrlDB);
        } catch (Exception e) {
            System.out.println("Error Method insertModeloUrl - " + e.getMessage());
        }
    }

    public class TaskInserIntoSQLModeloUrl extends AsyncTask<MRoomUrlDB, Void, Void> {
        @Override
        protected Void doInBackground(MRoomUrlDB... objects) {
            try {
                long id = GuantesDataBase.newInstance2(getApplicationContext()).getGuantesInfoDao().insertModeloUrlRoom(objects[0]);
                if (id > -1) {
                    System.out.println("Se guardó modelo por completo!!!");
                }
            } catch (Exception e) {
                System.out.println("Error Method TaskInserIntoSQLModeloUrl - " + e.getMessage());
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                try {
                    Uri imageUri = data.getData();
                    pathImage = getPath(imageUri);
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    //Bitmap selectedImage = cropBitmap(BitmapFactory.decodeStream(imageStream));
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    foto_guante.setImageBitmap(selectedImage);
                    imageEmpty = false;
                } catch (Exception e) {
                    System.out.println("Excepcion - " + e.getMessage());
                }

            }
        }
    }

    private String getPath(Uri imageUri) {
        if (imageUri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(imageUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return imageUri.getPath();
    }


    public class TaskSaveModelRoom extends AsyncTask<MRoomDB, Void, Void> {
        @Override
        protected Void doInBackground(MRoomDB... mRoomDBS) {
            try {
                long id = GuantesDataBase.newInstance(getApplicationContext()).getGuantesInfoDao().insertModeloRoom(mRoomDBS[0]);
                if (id > -1L) {
                    System.out.println("Se guardó el modelo en RoomDB");
                } else {
                    System.out.println("No se guardó el modelo en RoomDB");
                }
            } catch (Exception e) {
                System.out.println("Excepcion - TaskSaveModelRoom method - " + e.getMessage());
            }

            return null;
        }
    }
}
