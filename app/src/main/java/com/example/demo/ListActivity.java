package com.example.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    private boolean recursive;
    private Uri uri_path;
    private ArrayList<Fichero> arrFicheros;
    private CustomListAdapter adaptador = null;

    private HashMap<String, ArrayList<DocumentFile>> ficheros;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ListView lv_files;
        Button delete_button;
        ProgressDialog progressDialog;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getSupportActionBar().setTitle(R.string.dup_images_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage(getText(R.string.loading));
        progressDialog.show();

        // Get the GUI variables
        delete_button = findViewById(R.id.button_delete);

        // Get parameters from prev. activity
        // REF: https://stackoverflow.com/a/5265952/3594238

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            recursive = extras.getBoolean("Recursive");
            uri_path = (Uri) extras.get("URI");
        }
        else
        {
            recursive = (boolean) savedInstanceState.getSerializable("Recursive");
            uri_path = (Uri) savedInstanceState.getSerializable("URI");
        }

        // Search for files
        arrFicheros = getRepeatedFilesFromURI(uri_path);

        lv_files = findViewById(R.id.listview_files);
        adaptador = new CustomListAdapter(this, arrFicheros);
        lv_files.setAdapter(adaptador);

        progressDialog.dismiss();

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deleted = 0;
                boolean checked[] = adaptador.getChecked();

                for (int i = 0; i < checked.length; i++) {
                    Log.d("mi_debug", adaptador.getChecked()[i] + "");
                    if (adaptador.getChecked()[i]) {
                        deleted += arrFicheros.get(i).eliminarFicheros();
                    }
                }

                Intent intent = new Intent(ListActivity.this, SuccessActivity.class);
                intent.putExtra("images_deleted", deleted);
                startActivityForResult(intent, 0);
                finish();
            }
        });
    }

    /**
     * @param uri The URI of the directory for duplicated images to be found
     * @return An ArrayList with the repeated images
     */
    protected ArrayList<Fichero> getRepeatedFilesFromURI(Uri uri)
    {
        Log.d("mi_debug", "getFilesFromURI");
        DocumentFile df_path = DocumentFile.fromTreeUri(this, uri);

        ArrayList<DocumentFile> array_aux;
        ficheros = new HashMap<>();

        ArrayList<Fichero> repeated_images = new ArrayList<>();

        boolean ocu = false;

        if (df_path.exists())
        {
            String tmp_str;
            // http://android-er.blogspot.com/2015/09/example-of-using-intentactionopendocume.html
            for (DocumentFile file : df_path.listFiles())
            {
                if (file.isDirectory() && recursive)
                {
                    ArrayList<Fichero> ficheros_aux = getRepeatedFilesFromURI(file.getUri());
                    repeated_images.addAll( ficheros_aux );
                }
                else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg"))
                {
                    Log.d("mi_debug", "--------------------------------------");

                    try
                    {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), file.getUri());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String hash = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        if (ficheros.containsKey(hash))
                        {
                            ocu = true;
                            Log.d("mi_debug", "fichero repetido");
                            ficheros.get(hash).add(file);
                        }
                        else
                        {
                            Log.d("mi_debug", "fichero nuevo encontrado");
                            array_aux = new ArrayList<>();
                            array_aux.add(file);
                            ficheros.put(hash, array_aux);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Path " + uri_path.toString() + " does not exists", Toast.LENGTH_SHORT).show();
        }

        if(!ocu)
        {
            Intent intent = new Intent(ListActivity.this, SuccessActivity.class);
            intent.putExtra("images_deleted", 0);
            startActivityForResult(intent, 0);
            finish();
        }

        //Una vez tengo TODAS las imagenes del smartphone, creo el adapter con las que se repiten

        Iterator it = ficheros.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<DocumentFile> aux = (ArrayList<DocumentFile>) pair.getValue();

            if (aux.size() > 1)
            {
                Fichero fic = null;
                try
                {
                    fic = new Fichero(MediaStore.Images.Media.getBitmap(this.getContentResolver(), aux.get(0).getUri()), aux);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                repeated_images.add(fic);
            }
        }
        return repeated_images;
    }
}
