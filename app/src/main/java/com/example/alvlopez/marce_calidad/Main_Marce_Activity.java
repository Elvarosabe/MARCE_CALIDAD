package com.example.alvlopez.marce_calidad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main_Marce_Activity extends AppCompatActivity {


    TextView Conteo_unidades;
    Button Btn_contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__marce_);

        //Enlace XML
        Conteo_unidades = (TextView) findViewById(R.id.conteo_unidades);
        Btn_contador =    (Button) findViewById(R.id.boton_contador);




    }//Final Oncreate


    //Funcion para realizar conteo al presionar el boton (Si es que está habilitado)
    public void contador(View view)
    {


    }

    //Funcion para vista de defectuosidad
    public void defectuosidad(View view)
    {
        //Debe ir a la actividad en la que se ingresan los defectos mas representativos
        Intent intent_defectos = new Intent(Main_Marce_Activity.this,Defectos_Activity.class);
        startActivity(intent_defectos);
    }
}//Final Appcompat
