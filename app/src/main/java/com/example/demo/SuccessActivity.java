package com.example.demo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SuccessActivity extends AppCompatActivity {

    private int images_deleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        getSupportActionBar().setTitle(R.string.success_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        images_deleted = getIntent().getIntExtra("images_deleted", 0);

        String message = getString(R.string.result_txt) + " " + images_deleted;
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        ImageView img = findViewById(R.id.imageView1);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                startActivityForResult(intent, 0);
                finish();
            }
        });
    }
}
