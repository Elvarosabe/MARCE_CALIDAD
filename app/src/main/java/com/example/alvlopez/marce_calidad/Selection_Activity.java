package com.example.alvlopez.marce_calidad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Selection_Activity extends AppCompatActivity {


    Spinner sel_planta,sel_canaleta,sel_talla,sel_color;
    EditText cod_referencia,unidades;



    //Informacion del servidor a enviar la peticion
    private static final int SERVERPORT = 54986;  //PUERTO AL CUAL ENVÍA
    private static final int RECEIVEDPORT= 54980; //PUERTO ESCUCHA
    private static final String ADDRESS = "192.168.137.1";    //IP ESTATICA SERVIDOR






    //String para peticion al servidor
    String peticion_estabilidad="";
    String existe_estabilidad="";

    //Variables info recibida del servidor

    String paquete_estabilidad="";      //Paquete recibido


    //Variables para informacion proveniente de la anterior actividad
    String val_planta="";
    String Codigo_referencia="";
    String Nombre_referencia="";
    String Unidades="";
    String Orden_corte="";
    String direccion_ip="";
    String canaleta="";
    ArrayList<String> modulo_recibido = new ArrayList<String>(); //Lista de valores modulo (en realidad esto no se muestra en la interfaz)
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
        canaleta=extras.getString("Canaleta");
        Codigo_referencia = extras.getString("CodRef");
        Nombre_referencia = extras.getString("NomRef");
        Orden_corte  = extras.getString("OrdenCorte");
        direccion_ip = extras.getString("IP");
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
        sel_canaleta.setAdapter(adapter_canaleta); //Adaptador canaleta  (no hay nada en la interfaz para canaleta)

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


        //Debo obtener valores seleccionados del spinner
        //planta,canaleta,talla,color
        //SETEAR LOS OTROS VALORES QUE ME HAGAN FALTA


    }//Final oncreate

    // *****************Funcionalidad BOTONES *******************************************************

    //Funcion Boton reanudar
    public void Reanudar(View view)
    {
        //Llevarlo a la actividad principal pero con la informacion que tenía
        //Podría cargar la info de un archivo en el que este guardada
        //cargar el archivo desde el splash

    }

    //Funcion Boton Codigo de Barras
    public void codigo_barras(View view)
    {
        Intent intent_marquilla = new Intent(Selection_Activity.this, MarquillaCode_Activity.class);
        //Pendiente enviar todos los Extras a la proxima actividad
        intent_marquilla.putExtra("IP",direccion_ip);
        intent_marquilla.putExtra("OrdenCorte",Orden_corte);
        intent_marquilla.putExtra("Planta",val_planta);             //se lo envío
        intent_marquilla.putExtra("Canaleta",canaleta);
        startActivity(intent_marquilla);


    }


    //Funcion Boton Aceptar
    public void aceptar(View view)
    {
        //verificar si existe estabilidad
        verificar_estabilidad();


    // *********************************************************************************************


    }

    public void verificar_estabilidad()
    {
        paquete_estabilidad="";
       PETICION_ESTABILIDAD myestabilidad = new PETICION_ESTABILIDAD();
       //OJO FALTA DEFINIR BIEN LA PETICION A ENVIAR
       peticion_estabilidad= "C"+';'+"PMESTAB"; //+';'+codigo_referencia +';'+orden_corte+';'+planta+';'+canaleta+';'+direccion_ip+';'+Integer.toString(RTC_time);
        myestabilidad.execute(peticion_estabilidad);

    }



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
                        Intent intent_before = new Intent(Selection_Activity.this,BeforeActivity.class);
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







}//final Appcompat
