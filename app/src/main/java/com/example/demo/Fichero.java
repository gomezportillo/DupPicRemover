package com.example.demo;

import android.graphics.Bitmap;
import android.support.v4.provider.DocumentFile;

import java.util.ArrayList;

public class Fichero
{
    private Bitmap imagen;
    private String primera_ocu;
    private String ocurrencias;

    private ArrayList<DocumentFile> files;

    public Fichero(Bitmap bm, ArrayList<DocumentFile> fs)
    {
        this.files = fs;
        this.imagen = bm;

        String aux = "";
        for (int i = 1; i < fs.size(); i++)
        {
            aux += fs.get(i).getUri().getPath();
            if (i != fs.size() - 1)
            {
                aux += "\n";
            }
        }
        this.ocurrencias = aux;

        this.primera_ocu = fs.get(0).getUri().getPath();
    }

    public int eliminarFicheros()
    {
        int cont=0;
        for (int i = 1; i < files.size(); i++)
        {
            if(files.get(i).delete())
            {
                cont++;
            }
        }
        return cont;
    }

    public String getPrimera_ocu()
    {
        return primera_ocu;
    }

    public void setPrimera_ocu(String primera_ocu)
    {
        this.primera_ocu = primera_ocu;
    }

    public Bitmap getImagen()
    {
        return imagen;
    }

    public void setImagen(Bitmap imagen)
    {
        this.imagen = imagen;
    }

    public String getOcurrencias()
    {
        return ocurrencias;
    }

    public void setOcurrencias(String ocurrencias)
    {
        this.ocurrencias = ocurrencias;
    }

    public ArrayList<DocumentFile> getFiles()
    {
        return files;
    }

    public void setFiles(ArrayList<DocumentFile> files)
    {
        this.files = files;
    }
}
