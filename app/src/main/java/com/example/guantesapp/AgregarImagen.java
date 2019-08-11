package com.example.guantesapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.guantesapp.AgregarStock.REQUEST_IMAGE_GALLERY;

public class AgregarImagen extends AppCompatActivity {

    ImageView foto_guante;
    Button btn_save;
    EditText id_foto;
    String pathImage;
    boolean imageEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_imagen);
        foto_guante = findViewById(R.id.foto_guante);
        btn_save = findViewById(R.id.btn_save);
        id_foto = findViewById(R.id.id_foto);
        foto_guante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionGallery();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageEmpty) {
                    try {
                        String idFoto = id_foto.getText().toString();
                        FileInputStream fileInputStream = new FileInputStream(pathImage);
                        if (!idFoto.isEmpty() && fileInputStream != null) {
                            byte[] imgbyte = new byte[fileInputStream.available()];
                            fileInputStream.read(imgbyte);
                            Foto foto = new Foto();
                            foto.setId_foto(idFoto.toLowerCase());
                            foto.setImg(imgbyte);
                            new TaskAddFoto().execute(foto);
                        } else {
                            Toast.makeText(getApplicationContext(), "Debes ingresar Id de la imagen...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        System.out.println("Error guardando imagen " + e.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Debes cargar una imagen...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public class TaskAddFoto extends AsyncTask<Foto, Void, Void> {
        boolean resul = false;
        @Override
        protected Void doInBackground(Foto... fotos) {
            try {
                Foto auxFoto = AppDataBase.getInstanceFotoBD(getApplicationContext()).getFotoDao().existPKFoto(fotos[0].getId_foto());
                if (auxFoto == null) {
                    AppDataBase.getInstanceFotoBD(getApplicationContext()).getFotoDao().insertFoto(fotos);
                    resul = true;
                }
            } catch (Exception e) {
                System.out.println("Excepcion saving foto.." + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (resul) {
                Toast.makeText(getApplicationContext(), "Foto saved properly...", Toast.LENGTH_SHORT).show();
                foto_guante.setImageResource(R.drawable.ic_add_photo);
                id_foto.setText("");
                imageEmpty = true;
            } else {
                Toast.makeText(getApplicationContext(), "Foto already exist...Not Uploaded", Toast.LENGTH_SHORT).show();
            }
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
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
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
