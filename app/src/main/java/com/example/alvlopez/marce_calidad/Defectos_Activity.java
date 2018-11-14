package com.example.alvlopez.marce_calidad;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class Defectos_Activity extends AppCompatActivity {


    RadioButton s11,s12,s13,s14,s15,s16,s17,s18,s19,s110,s111;   //fila 1
    RadioButton s21,s22,s23,s24,s25,s26,s27,s28,s29,s210,s211;   //fila 2
    RadioButton s31,s32,s33,s34,s35,s36,s37,s38,s39,s310,s311;   //fila 3
    RadioButton s41,s42,s43,s44,s45,s46,s47,s48,s49,s410,s411;   //fila 4
    RadioButton s51,s52,s53,s54,s55,s56,s57,s58,s59,s510,s511;   //fila 5
    RadioButton s61,s62,s63,s64,s65,s66,s67,s68,s69,s610,s611;   //fila 6
    RadioButton s71,s72,s73,s74,s75,s76,s77,s78,s79,s710,s711;   //fila 7
    RadioButton s81,s82,s83,s84,s85,s86,s87,s88,s89,s810,s811;   //fila 8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defectos_);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        s11 = (RadioButton) findViewById(R.id.sel11);















    }//Final Oncreate

    public void confirmacion_defectos(View view)
    {
        String selector="";

        for(int i=1; i<8;i++) //For filas
        {
            for(int c=1;c<11;c++) //For columnas
            {

                 selector = "sel"+Integer.toString(i) + Integer.toString(c);
                // String eval = selector+".isChecked()"

                //if(eval.isChecked())
                //{
                    //deberia guardar los datos en algo
                //}




            }
        }





    }//Fin funcion confirmacion defectos










}//Final AppCompat
