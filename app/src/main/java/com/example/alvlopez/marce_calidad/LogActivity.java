package com.example.alvlopez.marce_calidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {

    //Esto solo debe suceder cuando no tenga MARCE *****
    //Pues si tiene MARCE, se debe traer la informacion del nombre de la persona correspondiente


    Spinner Loguin;


    ArrayList<String> lista_personas = new ArrayList<String>();
    //Adaptadores para setear los items al listview
    ArrayAdapter<String> adaptador_persona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        //ENLACE XML
        Loguin = (Spinner) findViewById(R.id.combo_logueo);



        //*****  Spinners con info Dinamica ******
        //Con la info que tengo de la actividad anterior, debo añadirla a la lista para setear los items que tendra segun el adaptador
        adaptador_persona = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lista_personas);
        lista_personas.add("ACA DEBE IR LA LISTA DE PERSONAS A AÑADIR OBTENIDA DE LA PETICION");
        adaptador_persona.notifyDataSetChanged();



    }//Final oncreate








    //Boton para enviar solicitud y continuar
    public void continuar(View view)
    {
        //Llamado funcion asyncrona para envio de solicitud
        //No puede avanzar sin haber seleccionado a alguien
    }
}//Final appcompat
