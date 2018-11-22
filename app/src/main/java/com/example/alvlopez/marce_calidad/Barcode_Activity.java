package com.example.alvlopez.marce_calidad;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.view.View.OnKeyListener;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class Barcode_Activity extends AppCompatActivity {


    EditText cod_bongo;
    String Codigo_bongo="";


    //Definicion de Cadenas para peticion al servidor
    String peticion="";
    String ip_dispositivo="";
    int RTC_time;

    //String separador de comando recibido

    String paquete_recibido="";

    //Cadenas Separadas del paquete recibido
    String separador_comando="";
    String cabecera="";
    String centro="";
    String subcentro="";

    //Auxiliares valores en arreglos
    String auxiliar_modulo="";
    String auxiliar_talla="";
    String auxiliar_color="";
    String auxiliar_unidadestalla="";
    String auxiliar_orden_corte="";


    //Intent para cambio de actividad y envío de extras
    Intent intent_omitir;
    Intent intent_codigo;

    //Informacion a recibir cuando se envía el codigo de bongo
    String planta="";
    String canaleta="";
    String codigo_referencia ="";
    String nombre_referencia="";

    ArrayList<String> modulo = new ArrayList<String>();
    ArrayList<String> talla = new ArrayList<String>();
    ArrayList<String> color_obtenido = new ArrayList<String>();
    ArrayList<String> unidades_talla = new ArrayList<String>();
    ArrayList<String> Orden_corte = new ArrayList<String>();


    //Informacion del servidor a enviar la peticion
    private static final int SERVERPORT = 58243;  //PUERTO AL CUAL ENVÍA
    private static final int RECEIVEDPORT= 58203; //PUERTO ESCUCHA
    private static final String ADDRESS = "192.168.137.1";    //IP ESTATICA SERVIDOR




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_);

        //Enlace XML
        cod_bongo = (EditText) findViewById(R.id.codigo_bongo);


        //Obtener IP DISPOSITIVO
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip_dispositivo = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(getApplicationContext(), ip_dispositivo, Toast.LENGTH_SHORT).show();


        //Manejo del Enter luego de haber pistoleado
        cod_bongo.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...
                    Codigo_bongo = cod_bongo.getText().toString(); //obtengo el código para consultar


                    //Cadena para peticion C;PMB;COD_BONGO;IP_DISPOSITIVO;RTC_TIME
                    peticion = "C"+';'+"PMB"+';'+Codigo_bongo+';'+ip_dispositivo+';'+Integer.toString(RTC_time);
                    if(!(Codigo_bongo.equals("")))  //Verificar que el codigo pistoleado no este vacío
                    {
                        PETICION myQuery = new PETICION();
                        myQuery.execute(peticion);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "No se ha recibido código", Toast.LENGTH_SHORT).show();

                    // ...
                    return true;
                }
                return false;
            }
        });


    } //Fin onCreate

    //Funcion al presionar el boton omitir
    public void omitir_button(View view)
    {
        //Intent para ir hacía la siguiente actividad (en este caso selecction activity)
        intent_omitir = new Intent(Barcode_Activity.this,Selection_Activity.class);
        intent_omitir.putExtra("IP",ip_dispositivo); //Ip dispositivo
        startActivity(intent_omitir); //Inicia la actividad

    }//fin boton omitir


    public void llamado_sincronica()
    {
        paquete_recibido="";
        PETICION myQuery = new PETICION();
        peticion = "C"+';'+"PMB"+';'+Codigo_bongo+';'+ip_dispositivo+';'+Integer.toString(RTC_time);
        myQuery.execute(peticion);
    }

    //**************************FUNCION ASINCRONA PARA PETICION *******************************************************
    class PETICION extends AsyncTask<String,Void,String>
    {

        /**
         * Se conecta al servidor y trata resultado
         * */
        @Override
        protected String doInBackground(String... values)
        {
            try {
                Log.i("I/UDP Client","Connecting. . .");
                DatagramSocket socket = new DatagramSocket();
                InetAddress local = InetAddress.getByName(ADDRESS);
                int msg_length=values[0].length();
                byte[] message = values[0].getBytes();
                DatagramPacket packet= new DatagramPacket(message,msg_length,local,SERVERPORT);
                socket.send(packet);  //Envío del paquete

            }
            catch (IOException e) {
                e.printStackTrace();
            }


            //RECEPCION DEL PAQUETE
                byte[] lMsg = new byte[1500];
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                DatagramSocket ds = null;
            try
            {
                ds = new DatagramSocket(RECEIVEDPORT); //Puerto DE ESCUCHA
                ds.setSoTimeout(10000);
                ds.receive(dp);  //Recibo la respuesta del servidor
                paquete_recibido = new String(lMsg, 0, dp.getLength());
            }catch (SocketTimeoutException e) {
                Log.i("I/UDP Client", "No llego nada");
                llamado_sincronica();
            }
            catch (SocketException e) {
                e.printStackTrace();

            }
            catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (ds != null) {
                    ds.close();
                }
            }


            return null;
        }


            /**
         * Oculta ventana emergente y muestra resultado en pantalla
         * */
        @Override
        protected void onPostExecute(String value)
        {

            Toast.makeText(getApplicationContext(), "Entre al post execute "+  paquete_recibido, Toast.LENGTH_SHORT).show();

            //Separo la cadena obtenida con la informacion
            StringTokenizer token_inicial = new StringTokenizer(paquete_recibido, ";");
            separador_comando = token_inicial.nextToken(); // S
            cabecera =  token_inicial.nextToken(); //  PMB

            if(cabecera.equals("PMB"))
            {
                planta = token_inicial.nextToken(); // PLANTA
                auxiliar_modulo = token_inicial.nextToken(); //auxiliar_Modulo
                codigo_referencia = token_inicial.nextToken(); // Cod_referencia
                nombre_referencia = token_inicial.nextToken(); //nombre referencia
                auxiliar_talla = token_inicial.nextToken(); // auxiliar talla
                auxiliar_color = token_inicial.nextToken(); // auxiliar color
                auxiliar_unidadestalla = token_inicial.nextToken();  //auxiliar unidadesxtalla
                auxiliar_orden_corte = token_inicial.nextToken(); // auxiliar orden de corte (por si es una lista)
                centro = token_inicial.nextToken(); // centro
                subcentro = token_inicial.nextToken();  // subcentro


                //Para el MODULO
                StringTokenizer token_modulo = new StringTokenizer(auxiliar_modulo, "#");
                   while(token_modulo.hasMoreTokens())
                   {
                       modulo.add(token_modulo.nextToken()); //Voy añadiendo los elementos que hay alli a la lista
                   }

                //Para la TALLA
                StringTokenizer token_talla = new StringTokenizer(auxiliar_talla, "#");
                while(token_talla.hasMoreTokens())
                {
                    talla.add(token_talla.nextToken()); //Voy añadiendo los elementos que hay alli a la lista
                }


                //para el COLOR
                StringTokenizer token_color = new StringTokenizer(auxiliar_color, "#");

                while(token_modulo.hasMoreTokens())
                {
                    color_obtenido.add(token_color.nextToken()); //Voy añadiendo los elementos que hay alli a la lista
                }



                //para las UNIDADESXTALLA
                // ¿aunque no estoy seguro si esto deberia ser una lista o un solo valor?
                StringTokenizer token_unidtalla = new StringTokenizer(auxiliar_unidadestalla, "#");
                while(token_unidtalla.hasMoreTokens())
                {
                    unidades_talla.add(token_color.nextToken()); //Voy añadiendo los elementos que hay alli a la lista
                }

                //para ORDEN DE CORTE

                StringTokenizer token_orden = new StringTokenizer(auxiliar_orden_corte, "#");
                while(token_orden.hasMoreTokens())
                {
                    Orden_corte.add(token_color.nextToken()); //Voy añadiendo los elementos que hay alli a la lista
                }


                //Intent para dirigirse a la siguiente actividad
                intent_codigo = new Intent(Barcode_Activity.this,Selection_Activity.class);

                //Envío de Info a la proxima actividad
                intent_codigo.putExtra("Planta",planta);
               // intent_omitir.putExtra("Canaleta",canaleta);
                intent_codigo.putStringArrayListExtra("miModulo", modulo); //Arraystring serializable*
                intent_codigo.putExtra("CodRef",codigo_referencia);
                intent_codigo.putExtra("NomRef",nombre_referencia);
                intent_codigo.putStringArrayListExtra("miTalla",talla);    //Arraystring serializable*
                intent_codigo.putStringArrayListExtra("miColor",color_obtenido);   //Arraystring serializable*
                intent_codigo.putStringArrayListExtra("Unid_talla",unidades_talla); //Arraystrin serializable*
                intent_codigo.putStringArrayListExtra("OrdenCorte",Orden_corte);  //Arraystring serializable*
                intent_codigo.putExtra("Centro",centro);
                intent_codigo.putExtra("Subcentro",subcentro);
                intent_codigo.putExtra("IP",ip_dispositivo); //Ip dispositivo
                startActivity(intent_omitir); //Inicia la actividad

            }//termina el IF de la cabecera

            }//Termina el PostExecute

    }//Final PETICION
}//Fin AppActivity
