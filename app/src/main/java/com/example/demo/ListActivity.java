package com.example.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ListActivity extends AppCompatActivity
{
    private ListView lv_files;
    private Button delete_button;

    private boolean recursive;
    private Uri uri_path;
    private List<String> files;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getSupportActionBar().setTitle(R.string.dup_images_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
//        Toast.makeText(getApplicationContext(), "Path " + path , Toast.LENGTH_SHORT).show();

        // Search for files
        files = getFilesFromURI(uri_path);

        // REF. https://stackoverflow.com/a/5070922/3594238
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                files);

        lv_files = findViewById(R.id.listview_files);
        lv_files.setAdapter(arrayAdapter);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ListActivity.this, SuccessActivity.class);

                intent.putExtra("Images_found", files.size());

                startActivityForResult(intent, 0);
            }
        });
    }

    protected List<String> getFilesFromURI(Uri uri)
    {
        DocumentFile df_path = DocumentFile.fromTreeUri(this, uri);

        List<String> files_str = new ArrayList<>();
        List<File> files = new ArrayList<>();

        if (df_path.exists())
        {
            String tmp_str;
            // http://android-er.blogspot.com/2015/09/example-of-using-intentactionopendocume.html
            for (DocumentFile file : df_path.listFiles())
            {
                if(file.isDirectory())
                {
                    if (recursive)
                    {
                        files_str.addAll( getFilesFromURI( file.getUri() ));
                    }
                }
                else if (file.getName().endsWith(".jpg") ||
                        file.getName().endsWith(".png") ||
                        file.getName().endsWith(".jpeg"))
                {
                    files.add(new File( file.getUri().getPath() ));

                    // Save the name
                    tmp_str = (file.getName() + "\n");
                    files_str.add(tmp_str);

                    // Generate the hash md5
                    String image_path = file.getUri().getPath();
                    String image_string = getBase64String( image_path );
                    String md5 = md5(image_string);
                    Log.d("md5", md5);
                }
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Path " + uri_path.toString() + " does not exists", Toast.LENGTH_SHORT).show();
        }
        return files_str;
    }

    // REF. https://stackoverflow.com/a/41396283/3594238
    private String getBase64String(String path)
    {
        // give your image file url in mCurrentPhotoPath
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // In case you want to compress your image, here it's at 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    //REF. https://mobikul.com/converting-string-md5-hashes-android/
    public String md5(String input_string)
    {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(input_string.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
            {
                hexString.append( Integer.toHexString(0xFF & messageDigest[i]) );
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
