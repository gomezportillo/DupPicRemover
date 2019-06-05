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
    private ListView lv_files;
    private Button delete_button;

    private boolean recursive;
    private Uri uri_path;
    private ArrayList<Fichero> arrFicheros;
    private CustomListAdapter adaptador = null;

    private ProgressDialog progressDialog;

    private HashMap<String, ArrayList<DocumentFile>> ficheros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        arrFicheros = getFilesFromURI(uri_path);

        lv_files = findViewById(R.id.listview_files);
        adaptador = new CustomListAdapter(this, arrFicheros);
        lv_files.setAdapter(adaptador);

        progressDialog.dismiss();

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deleted = 0;
                boolean checked[] = adaptador.getChecked();

                for (int i = 0; i < checked.length; i++)
                {
                    Log.d("hola", adaptador.getChecked()[i] + "");
                    if (adaptador.getChecked()[i])
                    {
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
    protected ArrayList<Fichero> getFilesFromURI(Uri uri)
    {
        Log.d("recursivo", String.valueOf(recursive));

        ArrayList<DocumentFile> arrDirectorios = new ArrayList<>();
        arrDirectorios.add(DocumentFile.fromTreeUri(this, uri));

        ficheros = new HashMap<>();
        boolean ocu = false;

        while (!arrDirectorios.isEmpty())
        {
            if (arrDirectorios.get(0).exists())
            {
                String tmp_str;
                // http://android-er.blogspot.com/2015/09/example-of-using-intentactionopendocume.html
                for (DocumentFile file : arrDirectorios.get(0).listFiles())
                {
                    if (file.isDirectory())
                    {
                        if (recursive)
                        {
                            //files_str.addAll(getFilesFromURI(file.getUri()));
                            arrDirectorios.add(file);
                        }
                    }
                    else if (file.getName() != null &&
                            (file.getName().endsWith(".jpg") ||
                            file.getName().endsWith(".png") ||
                            file.getName().endsWith(".jpeg")))
                    {

                        Log.d("hola", "--------------------------------------");

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
                                Log.d("hola", "fichero repetido");
                                ficheros.get(hash).add(file);
                            }
                            else
                            {
                                Log.d("hola", "fichero nuevo encontrado");
                                ArrayList<DocumentFile> aux = new ArrayList<>();
                                aux.add(file);
                                ficheros.put(hash, aux);
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
            arrDirectorios.remove(0);
        }

        if (!ocu)
        {
            Intent intent = new Intent(ListActivity.this, SuccessActivity.class);
            intent.putExtra("images_deleted", 0);
            startActivityForResult(intent, 0);
            finish();
        }

        //Una vez tengo TODAS las imagenes del smartphone, creo el adapter con las que se repiten
        ArrayList<Fichero> arrFich = new ArrayList<>();

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
                arrFich.add(fic);
            }
        }
        return arrFich;
    }

}
