package com.example.demo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Fichero> items;
    private boolean checked[];

    public CustomListAdapter(Activity actividad, ArrayList<Fichero> items)
    {
        this.activity = actividad;
        this.items = items;
        checked=new boolean[items.size()];
        for (int i=0;i<items.size();i++)
        {
            checked[i]=false;
        }
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.custom_files_list, null);
        }

        //creamos un objeto de la clase WebsEsTl
        final Fichero item = items.get(position);

        //Asignamos los recursos a las variable
        final TextView txt_borro = v.findViewById(R.id.txt_borro);
        final TextView txt_guardo = v.findViewById(R.id.txt_guardo);
        //txt_guardo.setVisibility(View.INVISIBLE);
        CheckBox checkBox = v.findViewById(R.id.checkBox);
        ImageView imagen = v.findViewById(R.id.imgWeb);

        //Enviamos informacion a la vista apartir de la informacion que contenga la clase:
        txt_borro.setText(item.getPrimera_ocu() + "\n" + item.getOcurrencias());
        imagen.setImageBitmap(item.getImagen());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CompoundButton) view).isChecked()) {
                    System.out.println("Checked");
                    checked[position]=true;
                    txt_guardo.setText(item.getPrimera_ocu());
                    txt_borro.setText(item.getOcurrencias());
                } else {
                    System.out.println("Un-Checked");
                    checked[position]=false;
                    txt_guardo.setText("");
                    txt_borro.setText(item.getPrimera_ocu() + "\n" + item.getOcurrencias());
                }
            }
        });

        return v;
    }

    public boolean[] getChecked()
    {
        return checked;
    }
}
