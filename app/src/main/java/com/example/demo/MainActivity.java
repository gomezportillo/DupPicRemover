package com.example.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    EditText text_path;
    Button button_search;
    Switch switch_recursive;
    Uri uri_path;

    int REQUEST_CODE_OPEN_DIRECTORY = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the GUI variables
        text_path = findViewById(R.id.text_path);
        button_search = findViewById(R.id.button_search);
        switch_recursive = findViewById(R.id.switch_recursive);

        // Disable button until a path is selected
        button_search.setEnabled(false);

        // Set listeners
        text_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directory searcher
                // REF. https://github.com/googlesamples/android-DirectorySelection
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list_activity_intent = new Intent(MainActivity.this, ListActivity.class);

                Log.d("uri", "uri_path: "+uri_path);

                list_activity_intent.putExtra("URI", uri_path);
                list_activity_intent.putExtra("Recursive", switch_recursive.isChecked());

                startActivityForResult(list_activity_intent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY) {
            if (resultCode == Activity.RESULT_OK) {
                uri_path = data.getData();
                text_path.setText(uri_path.getPath());
                button_search.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "Result != OK", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
