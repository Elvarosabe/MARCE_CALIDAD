package com.example.alvlopez.marce_calidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class BeforeActivity extends AppCompatActivity {


    Spinner sel_estabilidad;
    TextView Orden_corte;

    ArrayList<String> lista_estabilidad = new ArrayList<String>(); //Lista de valores para estabilidad

    //Adaptadores para setear los items al listview
    ArrayAdapter<String> adaptador_estabilidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before);

        //Enlace XML
        sel_estabilidad = (Spinner) findViewById(R.id.combo_estabilidad);
        Orden_corte = (TextView) findViewById(R.id.orden_corte);



        //*****  Spinners con info Dinamica ******
        //Con la info que tengo de la actividad anterior, debo añadirla a la lista para setear los items que tendra segun el adaptador
        //adaptador_estabilidad = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lista_estabilidad);
        //lista_estabilidad.add(elemento en cuestion);
        //adaptador_estabilidad.notifyDataSetChanged();



        //*** Aplicar adaptador a los spinner **
        //Dinamicos
        sel_estabilidad.setAdapter(adaptador_estabilidad);








    }//final oncreate


    //Funcion Boton Continuar
    public void continuar(View view)
    {
        //Con la estabilidad seleccionada debe proceder a hacer el pedido para buscar la tabla de medidas correspondiente
        //Luego seleccionar dialog box con el modo de trabajo (esto en caso de que no tenga marce)
        //si tiene MARCE puedo omitir la seleccion del modo de trabajo 
    }
}//final AppCompat
