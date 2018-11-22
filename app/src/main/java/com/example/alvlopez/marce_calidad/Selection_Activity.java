package com.example.alvlopez.marce_calidad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.StringTokenizer;

public class Selection_Activity extends AppCompatActivity {


    Spinner sel_planta,sel_canaleta,sel_talla,sel_color;
    EditText cod_referencia,unidades;

    String value_codigoref = ""; //Variable para capturar el valor del cod referencia si se ingresa uno

    //Informacion del servidor a enviar la peticion

    //PUERTOS PARA PETICION DE VERIFICAR ESTABILIDAD
    private static final int SERVERPORT = 54986;  //PUERTO AL CUAL ENVÍA
    private static final int RECEIVEDPORT= 54980; //PUERTO ESCUCHA
    private static final String ADDRESS = "192.168.137.1";    //IP ESTATICA SERVIDOR



    //String para peticion al servidor
    String peticion_estabilidad="";


    //Variables info recibida del servidor

    String paquete_estabilidad="";      //Paquete recibido de la peticion


    //Info proveniente de la ACTIVIDAD ANTERIOR
    String val_planta="";
    ArrayList<String> modulo_recibido = new ArrayList<String>(); //Lista de valores modulo (en realidad esto no se muestra en la interfaz)
    String Codigo_referencia="";
    String Nombre_referencia="";
    ArrayList<String> lista_talla = new ArrayList<String>(); //Lista de valores para talla
    ArrayList<String> lista_color = new ArrayList<String>(); //Lista de valores para color
    ArrayList<String> unid_talla = new ArrayList<String>(); //Lista de valores para unidadesxtalla
    ArrayList<String> lista_orden_Corte = new ArrayList<String>(); //Lista de valores para orden de corte
    String centro_obtenido="";
    String subcentro_obtenido="";
    String direccion_ip="";
    //String canaleta="";


    //Adaptadores para setear los items al listview
    ArrayAdapter<String> adaptador_talla;
    ArrayAdapter<String> adaptador_color;
    ArrayAdapter<String> adaptador_unidades_talla;



    //String para valores seleccionados de los spinners

    String seleccion_planta="";
    String seleccion_talla="";
    String seleccion_canaleta="";
    String seleccion_color="";
    String seleccion_unidades_talla="";
    String seleccion_orden_corte="";

    //Cadenas Separadas del paquete de existe estabilidad
    String separador_comando_estab="";
    String cabecera_estabilidad="";
    String existe_estabilidad="";
    String valor_estabilidad="";    //Valor recibido de la estabilidad que posee


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_);

        //Enlace XML
        sel_planta = (Spinner) findViewById(R.id.combo_planta);
        sel_canaleta = (Spinner) findViewById(R.id.combo_canaleta);  //No se tiene info sobre la canaleta
        sel_talla = (Spinner) findViewById(R.id.combo_talla);
        sel_color = (Spinner) findViewById(R.id.combo_color);
        cod_referencia = (EditText) findViewById(R.id.cod_referencia);
        unidades = (EditText) findViewById(R.id.unidades);


        //OBTENGO EXTRAS PROVENIENTES DE LA ACTIVIDAD ANTERIOR ***
        Bundle extras = getIntent().getExtras();       //me permite almacenar los extras, y recibir la info del intent


        //Antes lo obtenía con el serializableExtra asi:
        //PROBAR CUAL FORMA FUNCIONA
        // (ArrayList<String>) getIntent().getSerializableExtra("Unid_talla");

        val_planta = extras.getString("Planta");
        modulo_recibido =  getIntent().getStringArrayListExtra("miModulo");
        Codigo_referencia = extras.getString("CodRef");
        Nombre_referencia = extras.getString("NomRef");
        lista_talla=  getIntent().getStringArrayListExtra("miTalla");
        lista_color = getIntent().getStringArrayListExtra("miColor");
        unid_talla = getIntent().getStringArrayListExtra("Unid_talla");
        lista_orden_Corte = getIntent().getStringArrayListExtra("OrdenCorte");
        centro_obtenido = extras.getString("Centro");
        subcentro_obtenido= extras.getString("Subcentro");
        direccion_ip = extras.getString("IP");





        //Seteo los valores que no son Lista con lo que viene de la anterior actividad
        cod_referencia.setText(Codigo_referencia);



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



        //*** Aplicar adaptador a los spinner **

       //Estaticos
        sel_planta.setAdapter(adapter);   //Adaptador planta
        sel_canaleta.setAdapter(adapter_canaleta); //Adaptador canaleta  (no hay nada en la interfaz para canaleta)
        sel_talla.setAdapter(adaptador_talla);
        sel_color.setAdapter(adaptador_color);


        //Preseleccion de los valores a mostrar en los Spinners (solo aplica a planta)
        //pues para los otros la lista tiene los valores justos que deben aparecer
        String plan_array[]=   getResources().getStringArray(R.array.planta_array);
        int longitudplanta =getResources().getStringArray(R.array.planta_array).length;
        //Para planta
        for(int i=0; i<=longitudplanta;i++)
        {
            if(plan_array[i].equals(val_planta))
            {
                sel_planta.setSelection(i); //Seteo la seleccion del spinner de planta con el valor que debe tener
            }

        }



        //Respuesta a las selecciones del usuario en los Spinners

        //Planta
        sel_planta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                parent.getItemAtPosition(position);
                seleccion_planta= sel_planta.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Canaleta
        sel_canaleta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                parent.getItemAtPosition(position);
                seleccion_canaleta= sel_canaleta.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Talla
        sel_talla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                parent.getItemAtPosition(position);
                seleccion_talla= sel_talla.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Color
        sel_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                parent.getItemAtPosition(position);
                seleccion_color= sel_color.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

        //LA DIFERENCIA ES QUE ACA SOLO DEBO HABER TENIDO SELECCIONADO PLANTA Y CANALETA
        //PUES LOS OTROS PARAMETROS LOS CONSEGUIRE CON EL PISTOLEO DE LA MARQUILLA REF


        //VOY EN ESTA OPCION (YA ABARQUE LA DE ACPETAR)

        Intent intent_marquilla = new Intent(Selection_Activity.this, MarquillaCode_Activity.class);
        //Pendiente enviar todos los Extras a la proxima actividad
        if(!seleccion_planta.equals("") && !seleccion_canaleta.equals(""))
        {
            intent_marquilla.putExtra("PLANTA", seleccion_planta);
            intent_marquilla.putExtra("CANALETA", seleccion_canaleta);
            intent_marquilla.putExtra("CODREF", Codigo_referencia);
            intent_marquilla.putExtra("NOMREF", Nombre_referencia);
            intent_marquilla.putStringArrayListExtra("TALLA", lista_talla);
            intent_marquilla.putStringArrayListExtra("COLOR", lista_color);
            intent_marquilla.putExtra("IP", direccion_ip);
            intent_marquilla.putStringArrayListExtra("OrdenCorte", lista_orden_Corte);  //AQUI ENVIO LA POSIBLE LISTA DE ORDEN DE CORTE
            startActivity(intent_marquilla);
        }
        else
            {
                Toast.makeText(getApplicationContext(), "Por favor Seleccione Planta y Canaleta ", Toast.LENGTH_SHORT).show();

            }

    }


    //Funcion Boton Aceptar
    public void aceptar(View view)
    {
        //Valor que hay en el campo de codigo referencia
        value_codigoref = cod_referencia.getText().toString();
        //Debo verificar que la info no este vacia, me refiero a lo
        //que se selecciono desde los spinner
        if(!(seleccion_planta.equals("")) && !(seleccion_canaleta.equals("")) && !(seleccion_color.equals("")) && !(seleccion_talla.equals("")) )
        {
            if((!value_codigoref.equals("")))
            {
                //verificar si existe estabilidad alli tambien se envian los intents
                verificar_estabilidad();
            }

        }
        else
            {
                Toast.makeText(getApplicationContext(), "Información Incompleta!!", Toast.LENGTH_SHORT).show();
            }



    // *********************************************************************************************


    }


    //Se llama luego de haber seleccionado la info y presionado aceptar
    public void verificar_estabilidad()
    {

        paquete_estabilidad="";
       PETICION_ESTABILIDAD myestabilidad = new PETICION_ESTABILIDAD();

       //OJO FALTA POR AÑADIR UNIDADES X TALLA Y ORDEN DE CORTE PORQUE NO SE AUN COMO SABER CUAL SE ENVIA
       peticion_estabilidad= "C"+';'+"PMESTAB" +';'+seleccion_planta +';'+seleccion_canaleta+';'+value_codigoref+';'+Nombre_referencia+';'+seleccion_talla+';'+seleccion_color+';'+seleccion_orden_corte+';'+seleccion_unidades_talla+';'+direccion_ip;
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
                        Intent intent_after = new Intent(Selection_Activity.this,Main_Marce_Activity.class);
                        intent_after.putExtra("ESTAB",valor_estabilidad);
                        intent_after.putExtra("PLANTA",seleccion_planta);
                        intent_after.putExtra("CANALETA",seleccion_canaleta);
                        intent_after.putExtra("CODREF",value_codigoref);
                        intent_after.putExtra("NOMREF",Nombre_referencia);
                        intent_after.putExtra("TALLA",seleccion_talla);
                        intent_after.putExtra("COLOR",seleccion_color);
                        intent_after.putExtra("ORDENCORTE",seleccion_orden_corte);  //pendiente de enviar la orden de corte QUE DEBE SER ESPECIFICA YA
                        intent_after.putExtra("UNIDTALLA",seleccion_unidades_talla); // PENDIENTE DE ENVIAR UNIDADES TALLA
                        intent_after.putExtra("IP",direccion_ip);
                        startActivity(intent_after);
                    }
                });
                builder.setNegativeButton("ANTES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked BEFORE on the dialog
                        Intent intent_before = new Intent(Selection_Activity.this,BeforeActivity.class);
                        intent_before.putExtra("ESTAB",valor_estabilidad);
                        intent_before.putExtra("PLANTA",seleccion_planta);
                        intent_before.putExtra("CANALETA",seleccion_canaleta);
                        intent_before.putExtra("CODREF",value_codigoref);
                        intent_before.putExtra("NOMREF",Nombre_referencia);
                        intent_before.putExtra("TALLA",seleccion_talla);
                        intent_before.putExtra("COLOR",seleccion_color);
                        intent_before.putExtra("ORDENCORTE",seleccion_orden_corte);  //pendiente de enviar la orden de corte QUE DEBE SER ESPECIFICA
                        intent_before.putExtra("UNIDTALLA",seleccion_unidades_talla); // PENDIENTE DE ENVIAR UNIDADES TALLA
                        intent_before.putExtra("IP",direccion_ip);
                        startActivity(intent_before);
                        //VOY A LA ACTIVIDAD BEFORE (ANTES DE LAVADO)

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
            else
            {
                //NO TIENE ESTABILIDAD ENTONCES DEBO HACER EL PROCESO CORRESPONDIENTE DE CUANDO NO TIENE ESTAB
                //Va a la actividad correspondiente con la estabilidad estandar que creo que es la 0x0
                //YA LA INFO PISTOLEADA SE ENVÍO CON LA PETICION DE ESTABILIDAD
                Intent intent_no_estability= new Intent(Selection_Activity.this,Main_Marce_Activity.class);
                valor_estabilidad ="0X0";  //Voy a buscar la estabiliidad de la tabla estandar
                intent_no_estability.putExtra("VAL_ESTANDAR",valor_estabilidad);
                intent_no_estability.putExtra("PLANTA",seleccion_planta);
                intent_no_estability.putExtra("CANALETA",seleccion_canaleta);
                intent_no_estability.putExtra("CODREF",value_codigoref);
                intent_no_estability.putExtra("NOMREF",Nombre_referencia);
                intent_no_estability.putExtra("TALLA",seleccion_talla);
                intent_no_estability.putExtra("COLOR",seleccion_color);
                intent_no_estability.putExtra("ORDENCORTE",seleccion_orden_corte);  //pendiente de enviar la orden de corte QUE DEBE SER ESPECIFICA YA
                intent_no_estability.putExtra("UNIDTALLA",seleccion_unidades_talla);
                startActivity(intent_no_estability);
            }
        } //Final POST EXECUTE

    }//Final PETICION
}//final Appcompat
