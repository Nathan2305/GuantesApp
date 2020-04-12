package com.example.guantesapp.model.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.guantesapp.model.utils.GridAdapterConsulta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.guantesapp.model.entities.Guante;
import com.example.guantesapp.model.entities.Modelo;
import com.example.guantesapp.R;
import com.example.guantesapp.model.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;
import it.sephiroth.android.library.picasso.Target;


public class MainActivity extends AppCompatActivity {
    private TextView txtModelo;
    private Spinner sp_modelo, sp_talla;
    private CheckBox checkModelos;
    private ConstraintLayout layoutParent;
    private FloatingActionButton fab_add, fab_add_stock, fab_add_photo;
    public static List<String> listaGuantes;
    private FloatingActionButton consultar;
    private GridView gridView;
    private ArrayAdapter<CharSequence> adapter_tallas;

    public List<String> listNameModelSelected;
    public List<Modelo> listModelosObj;
    // public List<String> listFotoUrlString;
    public List<Bitmap> listModeloSelectedBitmap;

    private Animation fabOpen, fabClose, rotateForward, rotateBackward, progressIcon;
    private ProgressBar progressBar;
    private boolean isOpen = false;
    public static final int REQUEST_WRITE_EXTERNAL = 100;
    FloatingActionButton fabShare;
    boolean allModelos = false;
    GridAdapterConsulta adapterConsulta = null;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Backendless.initApp(getApplicationContext(), Utils.APPLICATION_ID, Utils.BACKENDLESS_KEY);
        txtModelo = findViewById(R.id.txtModelo);
        checkModelos = findViewById(R.id.checkModelos);
        layoutParent = findViewById(R.id.parentConstraint);
        sp_modelo = findViewById(R.id.sp_modelo);
        sp_talla = findViewById(R.id.sp_talla);
        progressBar = findViewById(R.id.progressBar);
        fab_add = findViewById(R.id.fab_add);
        fab_add_stock = findViewById(R.id.fab_add_stock);
        fab_add_photo = findViewById(R.id.fab_add_photo);
        gridView = findViewById(R.id.recFound);
        consultar = findViewById(R.id.consultar);
        getAllModelos();
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        adapter_tallas = ArrayAdapter.createFromResource(this, R.array.tallas_guantes, android.R.layout.simple_spinner_item);
        adapter_tallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_talla.setAdapter(adapter_tallas);
        checkModelos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp_modelo.setEnabled(false);
                    sp_modelo.setClickable(false);
                    sp_modelo.setVisibility(View.INVISIBLE);
                    txtModelo.setVisibility(View.INVISIBLE);
                    allModelos = true;
                } else {
                    sp_modelo.setEnabled(true);
                    sp_modelo.setClickable(true);
                    sp_modelo.setVisibility(View.VISIBLE);
                    txtModelo.setVisibility(View.VISIBLE);
                    allModelos = false;
                }
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnimation();
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fab_add_stock.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.celeste)));
            fab_add_photo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.naranaja)));
            fabShare.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verdeBonito)));
            consultar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.azulito)));
        }

        fab_add_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AgregarStock.class));
                fab_add_photo.startAnimation(fabClose);
                fab_add_stock.startAnimation(fabClose);
                fab_add.startAnimation(rotateBackward);
                isOpen = false;

            }
        });
        fab_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AgregarModelo.class));
                fab_add_photo.startAnimation(fabClose);
                fab_add_stock.startAnimation(fabClose);
                fab_add.startAnimation(rotateBackward);
                isOpen = false;

            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                disableViews(true);
                final String modelo = (String) sp_modelo.getSelectedItem();
                final String talla = (String) sp_talla.getSelectedItem();
                DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
                dataQueryBuilder.addSortBy("created ASC");
                dataQueryBuilder.setPageSize(50);
                StringBuilder sb = new StringBuilder();
                if (allModelos) { //Corregir para la nueva talla
                    if (talla != null & !talla.isEmpty()) {
                        sb.append("ModeloxTalla[modelo_link].talla='").append(talla).append("'")
                                .append(" and ModeloxTalla[modelo_link].cantidad>0");
                    } else { //buscar por modelo y talla
                        sb.append("ModeloxTalla[modelo_link].cantidad>0");
                    }
                } else {// búsqueda por modelo
                    if (talla != null & !talla.isEmpty()) { // por talla
                        sb.append("modelo like'").append(modelo).append("%'").append(" and ModeloxTalla[modelo_link].talla='").append(talla).append("'")
                                .append(" and ModeloxTalla[modelo_link].cantidad>0");
                    } else { //todos los modelos del modelo seleccionado
                        sb.append("modelo like'").append(modelo).append("%'")
                                .append(" and ModeloxTalla[modelo_link].cantidad>0");
                    }
                }
                dataQueryBuilder.setWhereClause(sb.toString());
                Backendless.Data.of(Modelo.class).find(dataQueryBuilder, new AsyncCallback<List<Modelo>>() {
                    @Override
                    public void handleResponse(final List<Modelo> responseModel) {
                        if (responseModel.size() > 0) {
                            listModelosObj = responseModel;
                            listNameModelSelected = new ArrayList<>();
                            //listFotoUrlString = new ArrayList<>();
                            adapterConsulta = new GridAdapterConsulta(MainActivity.this, listModelosObj);
                            gridView.setAdapter(adapterConsulta);
                        } else {
                            Utils.showToast(getApplicationContext(), "No hay modelos " + modelo + " disponibles en talla " + talla);
                            gridView.setAdapter(null);
                        }
                        disableViews(false);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Utils.showToast(getApplicationContext(), "Error consultando modelos :" + fault.getMessage());
                        disableViews(false);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
                Modelo modelo = listModelosObj.get(position);
                if (modelo.isChecked()) {
                    modelo.setChecked(false);
                    count--;
                    if (listNameModelSelected.contains(modelo.getModelo())) {
                        listNameModelSelected.remove(modelo.getModelo());
                    }
                } else {
                    modelo.setChecked(true);
                    count++;
                    listNameModelSelected.add((String) adapterConsulta.getItem(position));
                }
                if (count > 1) {
                    mode.setTitle(count + " Guantes");
                } else {
                    mode.setTitle(count + " Guante");
                }
                listModelosObj.get(position).setChecked(modelo.isChecked());
                adapterConsulta.updateRecords(listModelosObj);
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.share) {
                    if (!listNameModelSelected.isEmpty()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
                        dataQueryBuilder.setPageSize(listNameModelSelected.size());
                        for (int pos = 0; pos < listNameModelSelected.size(); pos++) {
                            stringBuilder.append(" modelo ='").append(listNameModelSelected.get(pos)).append("'");
                            if (pos != listNameModelSelected.size() - 1) {
                                stringBuilder.append(" or ");
                            }
                        }
                        dataQueryBuilder.setWhereClause(stringBuilder.toString());
                        Backendless.Data.of(Modelo.class).find(dataQueryBuilder, new AsyncCallback<List<Modelo>>() {
                            @Override
                            public void handleResponse(List<Modelo> response) {
                                if (response.size() > 0) {
                                    listModeloSelectedBitmap = new ArrayList<>();
                                    for (Modelo modelo : response) {
                                        Picasso.with(MainActivity.this).load(modelo.getFoto_url()).into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                                                listModeloSelectedBitmap.add(bitmap);
                                            }

                                            @Override
                                            public void onBitmapFailed(Drawable drawable) {
                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable drawable) {
                                            }
                                        });
                                    }
                                    SaveBitmap();
                                } else {
                                    System.out.println("No hay modelos");
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                System.out.println("No hay modelos agregados");
                            }
                        });
                    }
                } else if (item.getItemId() == R.id.select) {
                    for (Modelo each_modelo : listModelosObj) {
                        if (!each_modelo.isChecked()) {
                            each_modelo.setChecked(true);
                            listNameModelSelected.add(each_modelo.getModelo());
                            count++;
                        }
                    }
                    adapterConsulta.updateRecords(listModelosObj);
                    if (count > 1) {
                        mode.setTitle(count + " Guantes");
                    } else {
                        mode.setTitle(count + " Guante");
                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                count = 0;
                for (Modelo eachModelo : listModelosObj) {
                    if (eachModelo.isChecked()) {
                        eachModelo.setChecked(false);
                    }
                }
                adapterConsulta.updateRecords(listModelosObj);
                listNameModelSelected.clear();
            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_other_options, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }
        });
    }

    private void SaveBitmap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionWrite();
        } else {
            Intent shareIntent;
            if (listNameModelSelected.size() > 0) {
                shareIntent = new Intent();
                boolean fromFile = true;
                try {
                    if (listNameModelSelected.size() <= 1) {
                        String path = getPathIfExists(listNameModelSelected.get(0));
                        if (path == null) {
                            Bitmap bitmap = listModeloSelectedBitmap.get(0);
                            path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, listNameModelSelected.get(0), "Guante Orbit");
                            fromFile = false;
                        }
                        Uri uri = fromFile ? Uri.fromFile(new File(path)) : Uri.parse(path);
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    } else {
                        ArrayList<Uri> imageUris = new ArrayList<>();
                        for (int i = 0; i < listNameModelSelected.size(); i++) {
                            String path = getPathIfExists(listNameModelSelected.get(i));
                            if (path == null) {
                                Bitmap eachBitmap = listModeloSelectedBitmap.get(i);
                                path = MediaStore.Images.Media.insertImage(this.getContentResolver(), eachBitmap, listNameModelSelected.get(i), "Guante Orbit");
                                fromFile = false;
                            }
                            Uri uri = fromFile ? Uri.fromFile(new File(path)) : Uri.parse(path);
                            imageUris.add(uri); // Add your image URIs here
                        }
                        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                    }
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));
                } catch (Exception e) {
                    Utils.showToast(MainActivity.this, "Excepcion bitmap -" + e.getMessage());
                }
            } else {
                Utils.showToast(this, "Selecciona al menos una imagen para compartir");
            }
        }
    }

    private String getPathIfExists(String titleToFind) {
        String path = null;
        Uri uri;
        Cursor cursor;
        int title;
        int index_data;
        String titleFound;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] columnsToquery = {MediaStore.Images.Media.TITLE, MediaStore.MediaColumns.DATA};
        cursor = MainActivity.this.getContentResolver().query(uri, columnsToquery, null, null, null);
        if (cursor != null) {
            title = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE);
            index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                titleFound = cursor.getString(title);
                if (titleFound.equalsIgnoreCase(titleToFind)) {
                    path = cursor.getString(index_data);
                    break;
                }
            }
        }
        return path;
    }

    private void requestPermissionWrite() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SaveBitmap();
            }
        }
    }

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        Cursor cursor;
        int title, description;
        String title_;
        String description_;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        title = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE);
        description = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        while (cursor.moveToNext()) {
            title_ = cursor.getString(title);
            description_ = cursor.getString(description);
            System.out.println("Title :" + title_ + " - " + "Description :" + description_);
        }
        return listOfAllImages;
    }

    public void getAllModelos() {
        DataQueryBuilder db = DataQueryBuilder.create();
        db.setPageSize(15);
        Backendless.Data.of(Guante.class).find(db, new AsyncCallback<List<Guante>>() {
            @Override
            public void handleResponse(List<Guante> response) {
                if (!response.isEmpty()) {
                    listaGuantes = new ArrayList<>();
                    for (Guante guante : response) {
                        listaGuantes.add(guante.getName());
                    }
                    ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item, listaGuantes);
                    adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_modelo.setAdapter(adapter_modelos);
                } else {
                    Toast.makeText(getApplicationContext(), "No se han agredado listaGuantes aún", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // Toast.makeText(getApplicationContext(), "Algo salió mal.." + fault.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Error getting modelos ", fault.getMessage());
            }
        });
    }

    private void handleAnimation() {
        if (isOpen) {
            fab_add.startAnimation(rotateBackward);
            fab_add_stock.startAnimation(fabClose);
            fab_add_photo.startAnimation(fabClose);
            fab_add_stock.setClickable(false);
            fab_add_photo.setClickable(false);
            isOpen = false;
        } else {
            fab_add.startAnimation(rotateForward);
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
        //getAllModelos();
    }

    public void disableViews(boolean val) {
        if (val) {
            layoutParent.setAlpha(0.5f);
            consultar.setEnabled(false);
            fab_add.setEnabled(false);
            sp_modelo.setEnabled(false);
            //sp_talla.setEnabled(false);
        } else {
            layoutParent.setAlpha(1f);
            consultar.setEnabled(true);
            fab_add.setEnabled(true);
            sp_modelo.setEnabled(true);
            //sp_talla.setEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sell:
                startActivity(new Intent(this, ActivityVenta.class));
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean existeBitmap(Cursor cursor, String titleBitmap) {
        boolean val = false;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String titleExternal = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                if (titleBitmap.equalsIgnoreCase(titleExternal)) {
                    val = true;
                    break;
                }
            }
        }
        return val;
    }

}
