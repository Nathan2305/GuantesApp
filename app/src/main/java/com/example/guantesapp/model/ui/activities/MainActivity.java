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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.example.guantesapp.model.utils.GridAdapter;
import com.example.guantesapp.R;
import com.example.guantesapp.model.utils.Utils;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


public class MainActivity extends AppCompatActivity {
    private TextView txtModelo;
    private Spinner sp_modelo;
    private Button btn4, btn5, btn6, btn7, btn8, btn9, btn10;
    private int talla;
    private CheckBox checkModelos;
    private ConstraintLayout layoutParent;
    private FloatingActionButton fab_add, fab_add_stock, fab_add_photo;
    public static List<String> listaGuantes;
    private Button consultar;
    private GridView gridView;
    private ArrayAdapter<CharSequence> adapter_tallas;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private ProgressBar progress;
    private boolean isOpen = false;
    public static final int REQUEST_WRITE_EXTERNAL = 100;
    FloatingActionButton fabShare;
    boolean allModelos = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Backendless.initApp(getApplicationContext(), Utils.APPLICATION_ID, Utils.BACKENDLESS_KEY);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btn10 = findViewById(R.id.btn10);
        txtModelo = findViewById(R.id.txtModelo);
        checkModelos = findViewById(R.id.checkModelos);
        layoutParent = findViewById(R.id.parentConstraint);
        sp_modelo = findViewById(R.id.sp_modelo);
        progress = findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        progress.setProgressDrawable(fadingCircle);
        fab_add = findViewById(R.id.fab_add);
        fab_add_stock = findViewById(R.id.fab_add_stock);
        fab_add_photo = findViewById(R.id.fab_add_photo);
        gridView = findViewById(R.id.recFound);
        consultar = findViewById(R.id.consultar);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        // getAllModelos();

        progress.setVisibility(View.VISIBLE);
        adapter_tallas = ArrayAdapter.createFromResource(this, R.array.tallas_guantes, android.R.layout.simple_spinner_item);
        adapter_tallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //sp_talla.setAdapter(adapter_tallas);
        checkModelos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp_modelo.setEnabled(false);
                    sp_modelo.setClickable(false);
                    sp_modelo.setVisibility(View.INVISIBLE);
                    txtModelo.setVisibility(View.INVISIBLE);
                    allModelos = false;
                } else {
                    sp_modelo.setEnabled(true);
                    sp_modelo.setClickable(true);
                    sp_modelo.setVisibility(View.VISIBLE);
                    txtModelo.setVisibility(View.VISIBLE);
                    allModelos = true;
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
            progress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.translucent)));
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
                progress.setVisibility(View.VISIBLE);
                disableViews(true);
                final String modelo = (String) sp_modelo.getSelectedItem();
                final DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
                dataQueryBuilder.setPageSize(50);
                StringBuilder sb = new StringBuilder();
                if (!allModelos) {
                    if (talla != 0) {
                        sb.append("talla='" + talla + "'")
                                .append(" and cantidad>=0");
                    }
                } else {
                    if (talla != 0) {
                        sb.append("modelo like '" + modelo + "%'")
                                .append(" and talla='" + talla + "'")
                                .append(" and cantidad>=0");
                    } else {
                        sb.append(" modelo like '" + modelo + "%'")
                                .append(" and cantidad>=0");
                    }
                }
                dataQueryBuilder.setWhereClause(sb.toString());
                Backendless.Data.of(Modelo.class).find(dataQueryBuilder, new AsyncCallback<List<Modelo>>() {
                    @Override
                    public void handleResponse(List<Modelo> response) {
                        try {
                            if (response.size() > 0) {
                                GridAdapter adapter = new GridAdapter(MainActivity.this, response);
                                gridView.setAdapter(adapter);
                                disableViews(false);
                                progress.setVisibility(View.GONE);
                            } else {
                                Utils.showToast(getApplicationContext(), "No hay modelos " + modelo + " disponibles");
                                disableViews(false);
                                progress.setVisibility(View.GONE);
                                gridView.setAdapter(null);
                            }
                        } catch (Exception e) {
                            System.out.println("Excepcion..... " + e.getMessage());
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        disableViews(false);
                        progress.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    /*private void SaveBitmap() {
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
    }
*/
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
        Backendless.Data.of(Guante.class).find(new AsyncCallback<List<Guante>>() {
            @Override
            public void handleResponse(List<Guante> response) {
                if (!response.isEmpty()) {
                    listaGuantes = new ArrayList<>();
                    for (Guante guante : response) {
                        listaGuantes.add(guante.getName());
                    }
                    try {
                        ArrayAdapter<String> adapter_modelos = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, listaGuantes);
                        adapter_modelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_modelo.setAdapter(adapter_modelos);
                        progress.setVisibility(View.GONE);
                    } catch (Exception e) {
                        System.out.println("Exception Guava... " + e.getMessage());
                        progress.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No se han agredado listaGuantes aún", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
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


    public void clickBtnTalla(View view) {
        switch (view.getId()) {
            case R.id.btn4:
                talla = 4;
                btn4.setBackground(getResources().getDrawable(R.drawable.circle_btn_selected));
                btn5.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn6.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn7.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn8.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn9.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn10.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                break;
            case R.id.btn5:
                talla = 5;
                btn5.setBackground(getResources().getDrawable(R.drawable.circle_btn_selected));
                btn4.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn6.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn7.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn8.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn9.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn10.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                break;
            case R.id.btn6:
                talla = 6;
                btn6.setBackground(getResources().getDrawable(R.drawable.circle_btn_selected));
                btn4.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn5.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn7.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn8.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn9.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn10.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                break;
            case R.id.btn7:
                talla = 7;
                btn7.setBackground(getResources().getDrawable(R.drawable.circle_btn_selected));
                btn4.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn6.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn5.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn8.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn9.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn10.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                break;
            case R.id.btn8:
                talla = 8;
                btn8.setBackground(getResources().getDrawable(R.drawable.circle_btn_selected));
                btn4.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn6.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn5.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn7.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn9.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn10.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                break;
            case R.id.btn9:
                talla = 9;
                btn9.setBackground(getResources().getDrawable(R.drawable.circle_btn_selected));
                btn4.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn6.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn5.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn8.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn7.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn10.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                break;
            case R.id.btn10:
                talla = 10;
                btn10.setBackground(getResources().getDrawable(R.drawable.circle_btn_selected));
                btn4.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn6.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn5.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn8.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn9.setBackground(getResources().getDrawable(R.drawable.circle_btn));
                btn7.setBackground(getResources().getDrawable(R.drawable.circle_btn));
        }
    }

}
