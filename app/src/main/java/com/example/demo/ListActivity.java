package com.example.demo;

import android.os.Environment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getSupportActionBar().setTitle(R.string.dup_images_txt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get parameters from prev. activity
        // REF: https://stackoverflow.com/a/5265952/3594238
        boolean recursive;
        String path;
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            recursive = extras.getBoolean("Recursive");
            path = extras.getString("Path");
        }
        else
        {
            recursive = (boolean) savedInstanceState.getSerializable("Recursive");
            path = (String) savedInstanceState.getSerializable("Path");

        }
//        Toast.makeText(getApplicationContext(), "Path " + path , Toast.LENGTH_SHORT).show();

        // Search for files
        List<String> files = null;

        File picture_file = new File(path);
        if (picture_file.exists())
        {
            Toast.makeText(getApplicationContext(), "Path _" + path + "_", Toast.LENGTH_SHORT).show();


            if (recursive)
            {
                files = getListFilesRecursive(picture_file);
            }
            else
            {
                files = getListFiles(picture_file);
            }
            Toast.makeText(getApplicationContext(), "List view size: " + files.size(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Path _" + path + "_ does not exists", Toast.LENGTH_SHORT).show();

            files = new ArrayList<String>();
            files.add("Hola");
            files.add("Hola");
            files.add("Hola");
        }

        // REF. https://stackoverflow.com/a/5070922/3594238
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                files);

        lv_files = findViewById(R.id.listview_files);
        lv_files.setAdapter(arrayAdapter);
    }


    //REF. https://stackoverflow.com/a/9531063/3594238
    private List<String> getListFilesRecursive(File parentDir)
    {
        ArrayList<String> inFiles = new ArrayList<String>();
        File[] files = parentDir.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                inFiles.addAll(getListFilesRecursive(file));
            }
            else
            {
                inFiles.add(file.getName());
            }
        }
        return inFiles;
    }

    //REF. https://stackoverflow.com/a/9531063/3594238
    private List<String> getListFiles(File parentDir)
    {
        List<String> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        while (!files.isEmpty())
        {
            File file = files.remove();
            if (file.isDirectory())
            {
                files.addAll(Arrays.asList(file.listFiles()));
            }
            else
            {
                inFiles.add(file.getName());
            }
        }
        return inFiles;
    }
}
