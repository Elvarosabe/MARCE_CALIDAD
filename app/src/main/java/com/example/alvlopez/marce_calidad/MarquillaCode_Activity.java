package com.example.alvlopez.marce_calidad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MarquillaCode_Activity extends AppCompatActivity {

    //Debo recibir los bundle con la informacion proveniente de los intent
    //crear los strings para asociar a las variables
    //asynctask para consulta BD
        //dialog box para desplegar la info y manejar la confirmacion  o cancelacion

    EditText cod_marquilla;
    String Codigo_marquillaref="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquilla_code_);

        //Enlace XML
        cod_marquilla = (EditText) findViewById(R.id.codigo_marquillaref);


        //Manejo del Enter luego de haber pistoleado
        cod_marquilla.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...

                    Codigo_marquillaref = cod_marquilla.getText().toString(); //obtengo el código para consultar
                    Toast.makeText(getApplicationContext(), Codigo_marquillaref, Toast.LENGTH_SHORT).show();
                    //Debo llamar a la funcion asincrona que se encarga de la consulta en la BD de la info del codigo
                    //en el post execute del asynctask debo abrir el dialog box para desplegar la info, asociarla a las vbles y confirmar







                    // ...
                    return true;
                }
                return false;
            }
        });








    }//Final Oncreate
}//Final Appcompat
