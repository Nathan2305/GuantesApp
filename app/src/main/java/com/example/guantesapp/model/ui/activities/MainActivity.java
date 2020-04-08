package com.example.guantesapp.model.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.guantesapp.model.utils.GridAdapterConsulta;
import com.example.guantesapp.model.utils.GuantesDataBase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


public class MainActivity extends AppCompatActivity {
    private TextView txtModelo;
    private Spinner sp_modelo, sp_talla;
    private CheckBox checkModelos;
    private ConstraintLayout layoutParent;
    private FloatingActionButton fab_add, fab_add_stock, fab_add_photo;
    public static List<String> listaGuantes;
    public List<Modelo> listModelos;
    private Button consultar;
    private GridView gridView;
    private ArrayAdapter<CharSequence> adapter_tallas;
    private List<String> listNameShare;
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
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //progress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.translucent)));
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
       /* fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveBitmap();
            }
        });*/
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
                        sb.append("ModeloxTalla[modelo_link].talla='" + talla + "'")
                                .append(" and ModeloxTalla[modelo_link].cantidad>0");
                    } else { //buscar por modelo y talla
                        sb.append("ModeloxTalla[modelo_link].cantidad>0");
                    }
                } else {// búsqueda por modelo
                    if (talla != null & !talla.isEmpty()) { // por talla
                        sb.append("modelo like'" + modelo + "%'")
                                .append(" and ModeloxTalla[modelo_link].talla='" + talla + "'")
                                .append(" and ModeloxTalla[modelo_link].cantidad>0");
                    } else { //todos los modelos del modelo seleccionado
                        sb.append("modelo like'" + modelo + "%'")
                                .append(" and ModeloxTalla[modelo_link].cantidad>0");
                    }
                }
                dataQueryBuilder.setWhereClause(sb.toString());
                Backendless.Data.of(Modelo.class).find(dataQueryBuilder, new AsyncCallback<List<Modelo>>() {
                    @Override
                    public void handleResponse(final List<Modelo> responseModel) {
                        if (responseModel.size() > 0) {
                            listModelos = responseModel;
                            listNameShare = new ArrayList<>();
                            adapterConsulta = new GridAdapterConsulta(MainActivity.this, listModelos);
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
                count++;
                if (count > 1) {
                    mode.setTitle(count + " Guantes");
                } else {
                    mode.setTitle(count + " Guante");
                }
                Modelo modelo = listModelos.get(position);
                if (modelo.isChecked()) {
                    modelo.setChecked(false);
                } else {
                    modelo.setChecked(true);
                }
                listModelos.get(position).setChecked(modelo.isChecked());
                adapterConsulta.updateRecords(listModelos);
                listNameShare.add((String) adapterConsulta.getItem(position));
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

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                count = 0;
                for (Modelo eachModelo : listModelos) {
                    if (eachModelo.isChecked()) {
                        eachModelo.setChecked(false);
                    }
                }
                adapterConsulta.updateRecords(listModelos);
            }
        });


    }





   /* private void SaveBitmap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionWrite();
        } else {
            if (listImagesPositionChecked.size() > 0) {
                if (listImagesPositionChecked.size() <= 1) {
                    Drawable mDrawable = listImagesPositionChecked.get(0);
                    Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                    try {

                        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, "modeloX", "Guante Orbit");
                        Uri uri = Uri.parse(path);

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/jpeg");
                        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));

                    } catch (Exception e) {
                        System.out.println("Excepcion bitmap -" + e.getMessage());
                    }
                } else {
                    ArrayList<Uri> imageUris = new ArrayList<>();
                    for (Drawable drawable : listImagesPositionChecked) {
                        Bitmap mBitmap = ((BitmapDrawable) drawable).getBitmap();
                        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, "modeloX", "Guante Orbit");
                        Uri uri = Uri.parse(path);
                        imageUris.add(uri); // Add your image URIs here

                    }
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));
                }
            } else {
                Utils.showToast(this, "Selecciona al menos una imagen para compartir");
            }
        }
    }*/




   /* private ActionMode.Callback mActionModelCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_other_options, menu);
            mode.setTitle("Choose Other Options");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.search:
                    Utils.showToast(getApplicationContext(), "SELECTED SEARCH");
                    mode.finish();
                    return true;
                case R.id.add:
                    Utils.showToast(getApplicationContext(), "SELECTED ADD");
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };*/

    private void requestPermissionWrite() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SaveBitmap();
            }
        }
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
                    ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, listaGuantes);
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
        getAllModelos();
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

    public void saveBitmapsOtherThread(String urlImage, String modelo) {
        new saveBitmapAsyncTask().execute(this, urlImage, modelo);
    }

    public class saveBitmapAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            try {
                Context context = (Context) objects[0];
                String urlImage = String.valueOf(objects[1]);
                String title = String.valueOf(objects[2]);

                String[] proj = {MediaStore.Images.Media.TITLE};
                Cursor cursorImages = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
                if (cursorImages != null) {
                    if (!existeBitmap(cursorImages, title)) {
                        Bitmap eachBitmap = Picasso.with(context).load(urlImage).get();
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), eachBitmap, title, "Guante Orbit");
                    }
                }
            } catch (IOException e) {
                Utils.showToast((Context) objects[0], "Excepcion guardando bitmap - " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
