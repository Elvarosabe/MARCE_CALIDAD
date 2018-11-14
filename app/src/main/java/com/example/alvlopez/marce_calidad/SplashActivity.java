package com.example.alvlopez.marce_calidad;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    // Duración en milisegundos que se mostrará el splash
    private final int DURACION_SPLASH = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            public void run()
            {

               //En este punto debe hacerse lo correspondiente  a las tareas del splash

                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(SplashActivity.this, Barcode_Activity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);

    }//Final Oncreate
}//Final AppCompat
