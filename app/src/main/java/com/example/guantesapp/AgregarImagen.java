package com.example.guantesapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.guantesapp.AgregarStock.REQUEST_IMAGE_GALLERY;
import static com.example.guantesapp.MainActivity.modelos;

public class AgregarImagen extends AppCompatActivity {

    ImageView foto_guante;
    Button btn_save;
    Spinner spinnerModelo;
    String pathImage;
    CheckBox checkNuevo;
    boolean imageEmpty = true;
    TextView textViewNuevo;
    EditText nuevoModelo;
    Bitmap selectedImage;
    ProgressBar progress;
    public static final String pathRemote = "ModelosGuantes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_imagen);
        checkNuevo = findViewById(R.id.checkNuevo);
        textViewNuevo = findViewById(R.id.textViewNuevo);
        nuevoModelo = findViewById(R.id.nuevoModelo);
        progress = findViewById(R.id.progress);
        Sprite doubleBounce = new FadingCircle();
        progress.setProgressDrawable(doubleBounce);
        spinnerModelo = findViewById(R.id.name_foto);
        if (modelos != null) {
            ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, modelos);
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
                    final String nameFoto;
                    File fileToUpload = new File(pathImage);
                    if (!checkNuevo.isChecked()) {
                        nameFoto = spinnerModelo.getSelectedItem().toString();
                    } else {
                        nameFoto = nuevoModelo.getText().toString();
                    }
                    if (!nameFoto.isEmpty()) {
                        Backendless.Files.upload(fileToUpload, pathRemote, new AsyncCallback<BackendlessFile>() {
                            @Override
                            public void handleResponse(BackendlessFile response) {
                                //Toast.makeText(getApplicationContext(), "Se guardó foto correctamente -" + response.getFileURL(), Toast.LENGTH_LONG).show();
                                final Imagen imagen = new Imagen();
                                imagen.setFoto(response.getFileURL());
                                imagen.setModelo(nameFoto);
                                Backendless.Data.of(Imagen.class).save(imagen, new AsyncCallback<Imagen>() {
                                    @Override
                                    public void handleResponse(Imagen response) {
                                        final Modelo modelo = new Modelo();
                                        modelo.setNombre(nameFoto);
                                        Backendless.Data.of(Modelo.class).save(modelo, new AsyncCallback<Modelo>() {
                                            @Override
                                            public void handleResponse(Modelo response) {
                                                saveModeloChild(response,imagen);
                                            }
                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                if ("1155".equalsIgnoreCase(fault.getCode())) {
                                                    saveModeloChild(modelo,imagen);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "No se creó el nuevo Modelo.", Toast.LENGTH_LONG).show();
                                                    progress.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(getApplicationContext(), "Algo salió mal subiendo la imagen.." + fault.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        progress.setVisibility(View.GONE);
                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(getApplicationContext(), "Algo salió mal subiendo la foto.." + fault.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                progress.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        Utils.showToast(getApplicationContext(), "Ingresa el nombre del modelo...");
                        progress.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Seleccion una imagen...",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void saveModeloChild(final Modelo modelo,final Imagen imagen) {
        Backendless.Data.of(ModeloChild.class).find(new AsyncCallback<List<ModeloChild>>() {
            @Override
            public void handleResponse(List<ModeloChild> response) {
                if (!response.isEmpty()) {
                    int size = response.size();
                    String nombreModeloChild = modelo.getNombre() + size;
                    ModeloChild modeloChild = new ModeloChild();
                    modeloChild.setNombre(nombreModeloChild);

                    Backendless.Data.of(ModeloChild.class).save(modeloChild, new AsyncCallback<ModeloChild>() {
                        @Override
                        public void handleResponse(ModeloChild response) {
                            setImageToModelChild(response,imagen);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getApplicationContext(), "Algo salió mal hijo.." + fault.getMessage(),Toast.LENGTH_LONG).show();
                            progress.setVisibility(View.GONE);
                        }
                    });
                }else{
                    String nombreModeloChild = modelo.getNombre();
                    ModeloChild modeloChild = new ModeloChild();
                    modeloChild.setNombre(nombreModeloChild);
                    modeloChild.setImagen(imagen);
                    Backendless.Data.of(ModeloChild.class).save(modeloChild, new AsyncCallback<ModeloChild>() {
                        @Override
                        public void handleResponse(ModeloChild response) {
                           // Utils.showToast(getApplicationContext(),"Se guardó Hijo correctamente");
                            setImageToModelChild(response,imagen);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getApplicationContext(), "Algo salió mal hijo.." + fault.getMessage(),Toast.LENGTH_LONG).show();
                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Algo salió mal mal hijo.." + fault.getMessage(),Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            }
        });

    }

    private void setImageToModelChild(ModeloChild modelChild,Imagen imagen) {
        ArrayList<Imagen> list=new ArrayList<>();
        list.add(imagen);
        Backendless.Data.of(ModeloChild.class).setRelation(modelChild, "imagen", list, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {
                Utils.showToast(getApplicationContext(),"Se guardó el modelo por completo!!....");
                foto_guante.setImageResource(R.drawable.ic_cloud_upload_black_24dp);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.showToast(getApplicationContext(),"NO se guardó la relación ModeloChild-Imagen");
                //foto_guante.setImageResource(R.drawable.ic_cloud_upload_black_24dp);
                progress.setVisibility(View.GONE);
            }
        });

    }


    private void checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
        } else {
            goGallery();
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
}
