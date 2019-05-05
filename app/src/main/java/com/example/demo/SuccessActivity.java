package com.example.demo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SuccessActivity extends AppCompatActivity {

    int images_found;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        getSupportActionBar().setTitle(R.string.success_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            images_found = extras.getInt("Images_found");
        }
        else
        {
            images_found = (int) savedInstanceState.getSerializable("Images_found");
        }

        String message = R.string.result_txt + Integer.toString(images_found);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
