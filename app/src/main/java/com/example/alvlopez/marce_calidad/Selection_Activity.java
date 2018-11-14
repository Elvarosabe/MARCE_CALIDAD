package com.example.alvlopez.marce_calidad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class Selection_Activity extends AppCompatActivity {


    Spinner sel_planta,sel_canaleta,sel_talla,sel_color;
    EditText cod_referencia,unidades;





    //Variables para informacion proveniente de la anterior actividad
    String val_planta="";
    String Codigo_referencia="";
    String Nombre_referencia="";
    String Unidades="";
    String Orden_corte="";
    ArrayList<String> modulo_recibido = new ArrayList<String>();
    ArrayList<String> lista_talla = new ArrayList<String>(); //Lista de valores para talla
    ArrayList<String> lista_color = new ArrayList<String>(); //Lista de valores para color


    //Adaptadores para setear los items al listview
    ArrayAdapter<String> adaptador_talla;
    ArrayAdapter<String> adaptador_color;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_);

        //Enlace XML
        sel_planta = (Spinner) findViewById(R.id.combo_planta);
        sel_canaleta = (Spinner) findViewById(R.id.combo_canaleta);
        sel_talla = (Spinner) findViewById(R.id.combo_talla);
        sel_color = (Spinner) findViewById(R.id.combo_color);
        cod_referencia = (EditText) findViewById(R.id.cod_referencia);
        unidades = (EditText) findViewById(R.id.unidades);


        //OBTENGO EXTRAS PROVENIENTES DE LA ACTIVIDAD ANTERIOR ***
        Bundle extras = getIntent().getExtras();       //me permite almacenar los extras, y recibir la info del intent

        val_planta = extras.getString("Planta");
        Codigo_referencia = extras.getString("CodRef");
        Nombre_referencia = extras.getString("NomRef");
        Orden_corte  = extras.getString("OrdenCorte");
        modulo_recibido = (ArrayList<String>) getIntent().getSerializableExtra("miModulo");
        lista_talla= (ArrayList<String>) getIntent().getSerializableExtra("miTalla");
        lista_color = (ArrayList<String>) getIntent().getSerializableExtra("miColor");

        //******  Adaptadores para poblar Spinners **********

        //Spinners Estaticos

        //planta
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.planta_array,android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Canaleta
        ArrayAdapter<CharSequence> adapter_canaleta= ArrayAdapter.createFromResource(this,R.array.canaleta_array,android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_canaleta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //Talla
        adaptador_talla = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lista_talla);

        //Color
        adaptador_color = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lista_color);



        //*****  Spinners con info Dinamica ******
        //Con la info que tengo de la actividad anterior, debo añadirla a la lista para setear los items que tendra segun el adaptador
        //adaptador_talla = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lista_talla);
        //adaptador_color = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lista_color);
        //lista_talla.add(elemento en cuestion);
        //adaptador_talla.notifyDataSetChanged();
        //lista_color.add(elemento en cuestion);
        //adaptador_color.notifyDataSetChanged();


        //*** Aplicar adaptador a los spinner **

       //Estaticos
        sel_planta.setAdapter(adapter);   //Adaptador planta
        sel_canaleta.setAdapter(adapter_canaleta); //Adaptador canaleta

        //Dinamicos
        sel_talla.setAdapter(adaptador_talla);
        sel_color.setAdapter(adaptador_color);



        //Strings array equivalentes a los arreglos en Resource File
        String plan_array[]=   getResources().getStringArray(R.array.planta_array);

        int longitudplanta =getResources().getStringArray(R.array.planta_array).length;


        //Para planta
        for(int i=0; i<=longitudplanta;i++)
        {
            if(plan_array[i].equals(val_planta))
            {
                sel_planta.setSelection(i);
            }

        }




    }//Final oncreate

    //Funcion Boton reanudar
    public void Reanudar(View view)
    {
        //Llevarlo a la actividad principal pero con la informacion que tenía


    }

    //Funcion Boton Codigo de Barras
    public void codigo_barras(View view)
    {
        Intent intent_marquilla = new Intent(Selection_Activity.this, MarquillaCode_Activity.class);
        //Pendiente enviar todos los Extras a la proxima actividad
        startActivity(intent_marquilla);


    }


    //Funcion Boton Aceptar
    public void aceptar(View view)
    {
        //llamar la f. asincrona para la peticion al servidor con la info ingresada
        //verificar si existe estabilidad
        //Si EXISTE ESTABILIDAD
            //abrir dialog box preguntando en que momemnto es el Lavado (Antes o Despues)
                //lavado ANTES
                    //Actividad para seleccionar la estabilidad deseada (debo llevar la info entre actividades en esp ORDEN CORTE)
                //lavado DESPUES

        //NO EXISTE ESTABILIDAD
            //leer la tabla de medidas con la estabilidad unica y pasar a la actividad principal


    }
}//final Appcompat
