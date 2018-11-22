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
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MarquillaCode_Activity extends AppCompatActivity {

    //Debo recibir los bundle con la informacion proveniente de los intent
    //crear los strings para asociar a las variables
    //asynctask para consulta BD
        //dialog box para desplegar la info y manejar la confirmacion  o cancelacion

    EditText cod_marquilla;
    String Codigo_marquillaref="";
    int RTC_time;

    //Definicion de Cadenas para peticiones al servidor
    String peticion_ref="";
    String peticion_estabilidad="";


    //Strings para almacenar Respuesta a las peticiones del servidor

    //Respuesta a marquilla ref
    String cabecera_ref="";            //S
    String separador_comando_ref="";  //PMREF
    String respuesta_planta="";
    String respuesta_codigo_ref = "";
    String respuesta_talla="";
    String respuesta_color="";
    String respuesta_unidades_talla="";
    String respuesta_orden_corte="";
    String respuesta_nom_ref="";



    //RESPUESTA A SOLICITUD DE EXISTENCIA DE ESTABILIDAD
   String cabecera_estabilidad="";          //S
    String  separador_comando_estab="";     //PMESTAB
    String existe_estabilidad="";           //¿EXISTE ESTAB?
    String valor_estabilidad="";            //VALOR ESTAB



    String paquete_recibido="";  //Respuesta del servidor para peticion de Marquilla Ref
    String paquete_estabilidad=""; //RESPUESTA DEL SERVIDOR PARA PETICION DE EXISTENCIA DE ESTABILIDIDAD





    //Info proveniente de la ACTIVIDAD ANTERIOR
    String planta="";               //me la deben enviar de la actividad anterior
    String canaleta="";
    String direccion_ip="";
    String Codigo_referencia="";
    String Nombre_referencia="";
    ArrayList<String> modulo_recibido = new ArrayList<String>(); //Lista de valores modulo (en realidad esto no se muestra en la interfaz)
    ArrayList<String> lista_talla = new ArrayList<String>(); //Lista de valores para talla
    ArrayList<String> lista_color = new ArrayList<String>(); //Lista de valores para color
    ArrayList<String> unid_talla = new ArrayList<String>(); //Lista de valores para unidadesxtalla
    ArrayList<String> lista_orden_Corte = new ArrayList<String>(); //Lista de valores para orden de corte




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
        planta= extras.getString("PLANTA");
        canaleta=extras.getString("CANALETA");
        Codigo_referencia = extras.getString("CODREF");
        Nombre_referencia= extras.getString("NOMREF");
        lista_talla=  getIntent().getStringArrayListExtra("TALLA");
        lista_color = getIntent().getStringArrayListExtra("COLOR");
        direccion_ip= extras.getString("IP");
        lista_orden_Corte = getIntent().getStringArrayListExtra("OrdenCorte");




        //Manejo del Enter luego de haber pistoleado
        cod_marquilla.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...

                    Codigo_marquillaref = cod_marquilla.getText().toString(); //obtengo el código para consultar

                    peticion_ref = "C"+';'+"PMREF"+';'+Codigo_marquillaref+';'+direccion_ip;
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
        peticion_estabilidad= "C"+';'+"PMESTAB"+';'+respuesta_planta +';'+canaleta+';'+respuesta_codigo_ref+';'+respuesta_nom_ref+';'+respuesta_talla+';'+respuesta_color+';'+respuesta_orden_corte+';'+direccion_ip;
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
                paquete_recibido = new String(lMsg, 0, dp.getLength()); //Respuesta peticion Marquilla Ref
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
            StringTokenizer token_marquilla_ref = new StringTokenizer(paquete_recibido, ";");
            cabecera_ref = token_marquilla_ref.nextToken(); //S
            separador_comando_ref = token_marquilla_ref.nextToken(); //PMREF
            respuesta_planta = token_marquilla_ref.nextToken(); // Planta
            respuesta_codigo_ref = token_marquilla_ref.nextToken(); //CODREF
            respuesta_nom_ref = token_marquilla_ref.nextToken(); // NOMREF
            respuesta_talla = token_marquilla_ref.nextToken(); //TALLA
            respuesta_color = token_marquilla_ref.nextToken(); //COLOR
            respuesta_unidades_talla = token_marquilla_ref.nextToken(); //UNIDADESXTALLA
            respuesta_orden_corte = token_marquilla_ref.nextToken(); //OC



            //Dialog box para mostrar la informacion y que presione aceptar
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

            builder.setMessage("Esta es la informacion obtenida  \n" +
                    "Planta: "+ respuesta_planta+"\n" + "Codigo Ref: "+ respuesta_codigo_ref +"\n" + "Talla: "+ respuesta_talla+
                    "\n" + "Color: "+ respuesta_color + "\n" + "Unidades: "+respuesta_unidades_talla  );

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

            Toast.makeText(getApplicationContext(), "Entre al post execute "+  paquete_estabilidad, Toast.LENGTH_SHORT).show();


            //Separo la cadena obtenida con la informacion
            StringTokenizer token_existe_estabilidad = new StringTokenizer(paquete_estabilidad, ";");
            separador_comando_estab = token_existe_estabilidad.nextToken(); //S
            cabecera_estabilidad = token_existe_estabilidad.nextToken(); //PMESTAB
            existe_estabilidad = token_existe_estabilidad.nextToken(); //EXISTE
            valor_estabilidad = token_existe_estabilidad.nextToken(); //valor de la estabilidad



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

                        intent_before.putExtra("ESTAB",valor_estabilidad);
                        intent_before.putExtra("PLANTA",respuesta_planta);
                        intent_before.putExtra("CANALETA",canaleta);
                        intent_before.putExtra("CODREF",respuesta_codigo_ref);
                        intent_before.putExtra("NOMREF",respuesta_nom_ref);
                        intent_before.putExtra("TALLA",respuesta_talla);
                        intent_before.putExtra("COLOR",respuesta_color);
                        intent_before.putExtra("ORDENCORTE",respuesta_orden_corte);
                        intent_before.putExtra("UNIDTALLA",respuesta_unidades_talla);
                        intent_before.putExtra("IP",direccion_ip);
                        startActivity(intent_before);

                        //PAPAAAA VOY POR ACA MI CUCHITO LA BUENA MI PERRO DE BIEN 
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
            else
                {
                    //INDICA QUE NO EXISTE ESTABILIDAD !!!!!
                    //Va a la actividad correspondiente con la estabilidad estandar que creo que es la 0x0
                    //envía la info que adquirio del pistoleo
                }



            //Intent para ir hacía la siguiente actividad (con toda la info ingresada )



        } //Final POST EXECUTE

    }//Final PETICION











}//Final Appcompat
