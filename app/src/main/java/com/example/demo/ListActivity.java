package com.example.demo;

import android.net.Uri;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ListActivity extends AppCompatActivity
{
    private ListView lv_files;

    boolean recursive;
    Uri uri_path;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getSupportActionBar().setTitle(R.string.dup_images_txt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
//        Toast.makeText(getApplicationContext(), "Path " + path , Toast.LENGTH_SHORT).show();

        // Search for files
        List<String> files = new ArrayList<>();

        DocumentFile df_path = DocumentFile.fromTreeUri(this, uri_path);
        if (df_path.exists())
        {
            String tmp_str;
            // http://android-er.blogspot.com/2015/09/example-of-using-intentactionopendocume.html
            for (DocumentFile file : df_path.listFiles()) {

                if(file.isDirectory())
                {
                    // is a Directory and this is not recursive
                }
                else
                {
                    tmp_str = (file.getName() + " (" + file.getType() + ")\n");
                    files.add(tmp_str);
                }
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Path _" + uri_path.toString() + "_ does not exists", Toast.LENGTH_SHORT).show();
        }

        // REF. https://stackoverflow.com/a/5070922/3594238
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                files);

        lv_files = findViewById(R.id.listview_files);
        lv_files.setAdapter(arrayAdapter);
    }
}
