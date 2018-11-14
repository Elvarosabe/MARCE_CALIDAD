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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Barcode_Activity extends AppCompatActivity {


    EditText cod_bongo;
    String Codigo_bongo="";



    //Definicion de Cadenas para peticion al servidor
    String peticion="";
    String ip_dispositivo="";
    int RTC_time;

    //String separador de comando recibido
    String separador_comando="";


    //Intent para cambio de actividad y envío de extras
    Intent intent_omitir;

    //Informacion a recibir cuando se envía el codigo de bongo
    String planta="";
    String codigo_referencia ="";
    String nombre_referencia="";
    String unidades_talla="";
    String Orden_corte="";
    ArrayList<String> modulo = new ArrayList<String>();
    ArrayList<String> talla = new ArrayList<String>();
    ArrayList<String> color_obtenido = new ArrayList<String>();


    //Informacion del servidor a enviar la peticion
    private static final int SERVERPORT = 60000;  //PUERTO
    private static final String ADDRESS = "10.40.56.244";    //IP ESTATICA SERVIDOR



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
                   // Toast.makeText(getApplicationContext(), Codigo_bongo, Toast.LENGTH_SHORT).show();

                    //Cadena para peticion C;PMB;COD_BONGO;IP_DISPOSITIVO;RTC_TIME
                    peticion = "C"+';'+"PMB"+';'+Codigo_bongo+ip_dispositivo+';'+Integer.toString(RTC_time);
                    PETICION myQuery = new PETICION();
                    if(!(Codigo_bongo.equals("")))  //Verificar que el codigo pistoleado no este vacío
                    {
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
        startActivity(intent_omitir); //Inicia la actividad

    }//fin boton omitir



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


                //Recepcion del paquete
                DatagramSocket RECIVED_PACKET = new DatagramSocket(SERVERPORT);
                byte[] recibido = new byte[8000];
                DatagramPacket recived = new DatagramPacket(recibido,recibido.length);
                Log.i("UDP client: ", "about to wait to receive");
                RECIVED_PACKET.setSoTimeout(4000);   //Tiempo de espera para la recepcion del paquete 4 segundos
                RECIVED_PACKET.receive(recived);
                String text = new String(recibido, 0, recived.getLength());




            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }


            /**
         * Oculta ventana emergente y muestra resultado en pantalla
         * */
        @Override
        protected void onPostExecute(String value)
        {

            //Separo la cadena obtenida con la informacion
            StringTokenizer tokens = new StringTokenizer(value, ";");
            separador_comando = tokens.nextToken(); //Contiene el comando recibido desde el servidor

            //En este punto añado los items a los que corresponden a ser listas
            //ejemplo
            // modulo.Add("modulo..");



            //Intent para ir hacía la siguiente actividad (en este caso selecction activity)

            intent_omitir = new Intent(Barcode_Activity.this,Selection_Activity.class);
            //Envío de Info a la proxima actividad
            intent_omitir.putExtra("Planta",planta);
            intent_omitir.putExtra("miModulo", modulo); //Arraystring serializable*
            intent_omitir.putExtra("CodRef",codigo_referencia);
            intent_omitir.putExtra("NomRef",nombre_referencia);
            intent_omitir.putExtra("miTalla",talla);    //Arraystring serializable*
            intent_omitir.putExtra("miColor",color_obtenido);   //Arraystring serializable*
            intent_omitir.putExtra("OrdenCorte",Orden_corte);
            startActivity(intent_omitir); //Inicia la actividad



        }

    }//Final PETICION





}//Fin AppActivity
