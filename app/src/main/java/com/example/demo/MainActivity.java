package com.example.demo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity
{
    int REQUEST_DIRECTORY = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the download path as default path
        String download_path = Environment.DIRECTORY_DOWNLOADS;
        EditText text_path = findViewById(R.id.text_path);
        text_path.setText(download_path);

        text_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             // Buscador de directorios
            }
        });

        findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text_path = findViewById(R.id.text_path);
                Switch switch_recursive = findViewById(R.id.switch_recursive);

                Intent intent = new Intent(MainActivity.this, ListActivity.class);

                intent.putExtra("Path", text_path.getText().toString());
                intent.putExtra("Recursive", switch_recursive.isChecked());

                startActivityForResult(intent, 0);
            }
        });
    }
}
