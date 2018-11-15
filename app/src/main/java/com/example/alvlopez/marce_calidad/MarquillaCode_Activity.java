package com.example.alvlopez.marce_calidad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MarquillaCode_Activity extends AppCompatActivity {

    //Debo recibir los bundle con la informacion proveniente de los intent
    //crear los strings para asociar a las variables
    //asynctask para consulta BD
        //dialog box para desplegar la info y manejar la confirmacion  o cancelacion

    EditText cod_marquilla;
    String Codigo_marquillaref="";
    int RTC_time;

    //Definicion de Cadenas para peticion al servidor
    String peticion_ref="";
    String peticion_estabilidad="";


    //Info a llenar segun lo que se reciba de la BD

    String codigo_referencia ="";
    String nombre_referencia="";
    String unidades_talla="";
    String modulo = "";
    String talla = "";
    String color_obtenido="";
    String canaleta="";


    String paquete_recibido="";
    String paquete_estabilidad="";


    String existe_estabilidad="";



    //Strings para informacion proveniente de la anterior actividad (Selection Aactivity)
    String planta="";               //me la deben enviar de la actividad anterior
    String direccion_ip="";
    String orden_corte="";

    //Informacion del servidor a enviar la peticion
    private static final int SERVERPORT = 54986;  //PUERTO AL CUAL ENVÍA
    private static final int RECEIVEDPORT= 54980; //PUERTO ESCUCHA
    private static final String ADDRESS = "192.168.137.1";    //IP ESTATICA SERVIDOR

    //OJO DEBE USARSE PUERTOS DISTINTOS DE ACUERDO A LA PETICION QUE SE ESTE HACIENDO


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquilla_code_);

        //Enlace XML
        cod_marquilla = (EditText) findViewById(R.id.codigo_marquillaref);



        //Recibo Extras de la anterior Actividad *********
        Bundle extras = getIntent().getExtras();       //me permite almacenar los extras, y recibir la info del intent
        direccion_ip= extras.getString("IP");
        orden_corte = extras.getString("OrdenCorte");
        canaleta=extras.getString("Canaleta");
        planta= extras.getString("Planta");


        //Manejo del Enter luego de haber pistoleado
        cod_marquilla.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...

                    Codigo_marquillaref = cod_marquilla.getText().toString(); //obtengo el código para consultar
                    //Debo llamar a la funcion asincrona que se encarga de la consulta en la BD de la info del codigo
                    //en el post execute del asynctask debo abrir el dialog box para desplegar la info, asociarla a las vbles y confirmar

                    //Cadena para peticion C;PMREF;COD_BONGO;IP_DISPOSITIVO;RTC_TIME
                    peticion_ref = "C"+';'+"PMREF"+';'+Codigo_marquillaref+';'+direccion_ip+';'+Integer.toString(RTC_time);
                    if(!(Codigo_marquillaref.equals("")))  //Verificar que el codigo pistoleado no este vacío
                    {
                        PETICION_REF myQuery = new PETICION_REF();
                        myQuery.execute(peticion_ref);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "No se ha recibido código", Toast.LENGTH_SHORT).show();





                    // ...
                    return true;
                }
                return false;
            }
        });


    }//Final Oncreate


    public void llamado_sincronica()   //PETICION CON EL CODIGO PISTOLEADO
    {
        paquete_recibido="";
        PETICION_REF myQuery = new PETICION_REF();
        peticion_ref =  "C"+';'+"PMREF"+';'+Codigo_marquillaref+';'+direccion_ip+';'+Integer.toString(RTC_time);
        myQuery.execute(peticion_ref);
    }


    public void verificar_estabilidad() //VERIFICA EXISTENCIA DE ESTABILIDAD

    {
        paquete_estabilidad="";
        PETICION_ESTABILIDAD myestabilidad = new PETICION_ESTABILIDAD();
        peticion_estabilidad= "C"+';'+"PMESTAB"+';'+codigo_referencia +';'+orden_corte+';'+planta+';'+canaleta+';'+direccion_ip+';'+Integer.toString(RTC_time);
        myestabilidad.execute(peticion_estabilidad);

    }

    //**************************FUNCION ASINCRONA PARA PETICION DE MARQUILLA REF***************************************************
    class PETICION_REF extends AsyncTask<String,Void,String>
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
            //StringTokenizer tokens = new StringTokenizer(paquete_recibido, ";");
            //separador_comando = tokens.nextToken(); //Contiene el comando recibido desde el servidor




            //Dialog box para mostrar la informacion y que presione aceptar
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

            builder.setMessage("Esta es la informacion obtenida  \n" +
                    "Planta: "+ planta+"\n" + "Codigo Ref: "+ codigo_referencia +"\n" + "Talla: "+ talla+
                    "\n" + "Color: "+ color_obtenido + "\n" + "Unidades: "+unidades_talla  );

            // Add the buttons
            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked ACEPTAR button
                    //Debo llamar a funcion que va a hacer la peticion a la base de datos para
                    //Verificar si existe o no estabilidad
                    verificar_estabilidad();
                    dialog.dismiss();  //no se si puedo llamar esto aqui

                }
            });
            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User CANCELO the dialog
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();




            //Intent para ir hacía la siguiente actividad (con toda la info ingresada )



        } //Final POST EXECUTE

    }//Final PETICION MARQUILLA REF



    //**************************FUNCION ASINCRONA PARA PETICION DE EXIST ESTABILIDAD ***************************************************
    class PETICION_ESTABILIDAD extends AsyncTask<String,Void,String>
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
                paquete_estabilidad = new String(lMsg, 0, dp.getLength());
            }catch (SocketTimeoutException e) {
                Log.i("I/UDP Client", "No llego nada");
                verificar_estabilidad();
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
            //StringTokenizer tokens = new StringTokenizer(paquete_recibido, ";");
            //separador_comando = tokens.nextToken(); //Contiene el comando recibido desde el servidor
            //existe_estabilidad= ASIGNO SI SI O NO


            //Verifico si existe o no estabilidad
            if(existe_estabilidad.equals("EXISTE"))    //si existe o es distinta de la 0x0 que signifcaria que no existe?
            {
                //Alertdialog preguntando si la medida se realiza antes o despues de lavado
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                builder.setMessage("¿ Desea Medir Antes o Despues de Lavado ?");

                // Add the buttons
                builder.setPositiveButton("DESPUES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked DESPUES button

                    }
                });
                builder.setNegativeButton("ANTES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked BEFORE on the dialog
                        Intent intent_before = new Intent(MarquillaCode_Activity.this,BeforeActivity.class);
                        //Enviar todos los intents requeridos en before activity
                        startActivity(intent_before);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
            else
                {

                    //Va a la actividad correspondiente con la estabilidad estandar que creo que es la 0x0
                    //envía la info que adquirio del pistoleo
                }



            //Intent para ir hacía la siguiente actividad (con toda la info ingresada )



        } //Final POST EXECUTE

    }//Final PETICION











}//Final Appcompat
