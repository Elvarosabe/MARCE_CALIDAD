package com.example.alvlopez.marce_calidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;

public class Medidas_Activity extends AppCompatActivity {


    TableLayout tabla_medidas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medidas_);

        //ENLACE XML
        tabla_medidas = (TableLayout) findViewById(R.id.tabla_medidas);

        //La informacion añadida en la tabla debe ser dinamica
        //https://stackoverflow.com/questions/24078275/how-to-add-a-row-dynamically-in-a-tablelayout-in-android






    }//Final Oncreate
}//Final Appcompat
