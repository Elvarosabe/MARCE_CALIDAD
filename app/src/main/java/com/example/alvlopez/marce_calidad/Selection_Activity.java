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


    //PUERTOS PARA VERIFICAR SI ESTA EN MARCE
    private static final int SERVERPORT_MARCE= 50000;  //PUERTO AL CUAL ENVIA
    private static final int RECEIVEDPORT_MARCE= 50100; //PUERTO POR EL QUE ESCUCHA


    //String para peticion al servidor
    String peticion_estabilidad="";
    String peticion_existencia_marce="";


    //Variables info recibida del servidor

    String paquete_estabilidad="";      //Paquete recibido de la peticion
    String paquete_existencia="";       //Paquete recibido de la peticion de existencia de MARCE



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

    //Variables para info recibida existencia MARCE
    String separador_comando_existencia="";
    String cabecera_existencia="";
    String existe_marce="";
    String planta_recibida_marce="";
    String canaleta_recibida_marce="";
    String codigo_operario_recibido_marce="";
    String nombre_operario_recibido_marce="";
    String estado_recibido_marce="";  //MONTAJE O PRODUCCION


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
    public void codigo_barras(View view)   //DESPUES DE LA LECTURA SE DEBE REPLICAR MCHAS DE LAS COSAS QUE SE HICIERON EN EL BOTON ACEPTAR
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
    public void aceptar(View view) //PENDIENTE COMPLETAR FUNCION DE LECTURA TABLA DE MEDIDAS Y DE ENVIO DE ESTA EN VARIOS INTENT
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
                //*******    EN EL LLAMADO  A ESTA FUNCION REALIZA MUCHOS PROCESOS ************
                //SI TIENE ESTABILIDAD
                    //PREGUNTA SI VA A MEDIR ANTES O DESPUES DE LAVADO
                        //ANTES DE LAVADO
                            //PASO TODA LA INFO QUE LLEVO HASTA EL MOMENTO
                            //VOY A LA ACTIVIDAD DE BEFORE ACTIVITY

                        //DESPUES DE LAVADO
                            //LEO LA TABLA DE MEDIDAS CON LA ESTABILIDAD QUE TENGO
                            //PREGUNTA SI TIENE MARCE O NO
                                //SI TIENE MARCE
                                        //OBTIENE LA INFO RESPECTIVA DE MARCE
                                        //VA A LA ACTIVIDAD MAINACTIVITY CON LOS INTENTS REQUERIDOS
                                //NO TIENE
                                    //DEBE IR A LA ACTIVIDAD PARA LOGUEARSE RESPECTIVAMENTE
                                    //DESDE ESA ACTIVIDAD SE LOGUEA (TIENE QUE TENER LA INFO QUE LLEVABA DESDE AQUI)
                                    //PASO AL MAINMARCE ACTIVITY
                //NO TIENE ESTABILIDAD
                    //LEE TABLA DE MEDIDAS CON LA ESTABILIDAD ESTANDAR
                    //PREGUNTA SI TIENE MARCE O NO
                        //SI TIENE MARCE
                            //OBTIENE LA INFO RESPECTIVA DE MARCE
                            //VA A LA ACTIVIDAD MAINACTIVITY CON LOS INTENTS REQUERIDOS
                        //SI NO TIENE
                            //DEBE IR A LA ACTIVIDAD PARA LOGUEARSE RESPECTIVAMENTE
                                //DESDE ESA ACTIVIDAD SE LOGUEA (TIENE QUE TENER LA INFO QUE LLEVABA DESDE AQUI)
                                //PASO AL MAINMARCE ACTIVITY
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


    public void verificar_existencia_marce()
    {
        //Se va a llamar cuando no encuentre estabilidad y luego de haber presionado aceptar porque debe seguir el proceso con la eestab estandar y verificar si esta en marce
        //antes de avanzar
        paquete_existencia="";
        PETICION_EXISTENCIA  existencia_marce = new PETICION_EXISTENCIA();
        peticion_existencia_marce="C"+';'+"PMEMARCE"+';'+seleccion_planta +';'+seleccion_canaleta+';'+direccion_ip;
        existencia_marce.execute(peticion_existencia_marce);


    }


    public void leer_tabla_medidas(String estabilidad) //PENDIENTE ESTA MONDAAAA
    {

        //DEBO LEER LA TABLA DE MEDIDAS CON LA ESTABILIDAD CORRESPONDIENTE

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

                        //ANTES DE ENVIAR LOS INTENTS DEBO LEER LA TABLA DE MEDIDAS CORRESPONDIENTE Y VERIFICAR SI TIENE MARCE O NO
                        //PARA VER SI DEBE LOGUEARSE O SE ENCUENTRA EN MONTAJE O PRODUCCION


                        leer_tabla_medidas(valor_estabilidad);         //LEO LA TABLA DE MEDIDAS CON LA RESPECTIVA ESTABILIDAD OBTENIDA
                        //********OJO QUE DICE LEER TABLAS DE MEDIDAS DESPUES????? **********
                        verificar_existencia_marce(); //VERIFICO SI EXISTE MARCE

                        //OJO QUE SI NO TIENE MARCE CUANDO VAYA A LA OTRA ACTIVIDAD TENGO QUE PASARLE ESTO
                        //AQUI SOLO DEBERIA SEGUIR SI TIENE MARCE
                        //**#*#*$ PORQUE SI NO TIENE DEBERIA IR A LA OTRA ACTIVIDAD #$#"%

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
                        intent_after.putExtra("COD_OP",codigo_operario_recibido_marce); //CODIGO OPERARIO
                        intent_after.putExtra("NOM_OP",nombre_operario_recibido_marce); //NOMBRE DE OPERARIO
                        intent_after.putExtra("ESTADO_MARCE",estado_recibido_marce); //PRODUCCION O MONTAJE

                        //PENDIENTE DE ENVIAR TABLA DE MEDIDA

                        startActivity(intent_after);

                    }
                });
                builder.setNegativeButton("ANTES", new DialogInterface.OnClickListener() { //MELA!!!
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
                        //EN BEFORE ACTIVITY DEBE HACER VARIAS COSAS OJO! VERIFICAR SI TIENE MARCE Y TRAER LA TABLA DE MEDIDAS INDICADA
                        //DE ACUERDO A LA ESTABILIDAD

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
            else
            {

                //CASO DONDE NO EXISTE ESTABILIDAD PAPOOOOOOOOOOO *************************


                //DEBO LEER LA TABLA DE MEDIDAS PARA LA ESTAB ESTANDAR
                valor_estabilidad ="0X0";  //Voy a buscar la estabiliidad de la tabla estandar
                leer_tabla_medidas(valor_estabilidad); //lectura de la tabla de medidas papo con la estab ESTANDAR

                //DEBO VERIFICAR EXISTENCIA DE MARCE
                verificar_existencia_marce(); //VERIFICO SI EXISTE MARCE

                //PUEDE DARSE EL CASO QUE ESTE EN MARCE PERO NO TENGA ESTABILIDAD
                Intent intent_no_Estabilidad = new Intent(Selection_Activity.this,Main_Marce_Activity.class);
                intent_no_Estabilidad.putExtra("ESTAB",valor_estabilidad);
                intent_no_Estabilidad.putExtra("PLANTA",seleccion_planta);
                intent_no_Estabilidad.putExtra("CANALETA",seleccion_canaleta);
                intent_no_Estabilidad.putExtra("CODREF",value_codigoref);
                intent_no_Estabilidad.putExtra("NOMREF",Nombre_referencia);
                intent_no_Estabilidad.putExtra("TALLA",seleccion_talla);
                intent_no_Estabilidad.putExtra("COLOR",seleccion_color);
                intent_no_Estabilidad.putExtra("ORDENCORTE",seleccion_orden_corte);  //pendiente de enviar la orden de corte QUE DEBE SER ESPECIFICA YA
                intent_no_Estabilidad.putExtra("UNIDTALLA",seleccion_unidades_talla); // PENDIENTE DE ENVIAR UNIDADES TALLA
                intent_no_Estabilidad.putExtra("IP",direccion_ip);
                intent_no_Estabilidad.putExtra("COD_OP",codigo_operario_recibido_marce); //CODIGO OPERARIO
                intent_no_Estabilidad.putExtra("NOM_OP",nombre_operario_recibido_marce); //NOMBRE DE OPERARIO
                intent_no_Estabilidad.putExtra("ESTADO_MARCE",estado_recibido_marce); //PRODUCCION O MONTAJE
                startActivity(intent_no_Estabilidad);



                //PUEDE DARSE EL CASO QUE NO ESTE EN MARCE Y NO TENGA ESTABIDILIDAD
                    //EN ESTE CASO PASA A LA ACTIVIDAD DE LOGUEO POR NO TENER MARCE




            }
        } //Final POST EXECUTE

    }//Final PETICION




    //**************************FUNCION ASINCRONA PARA PETICION DE EXISTENCIA DE MARCE ***************************************************
    class PETICION_EXISTENCIA extends AsyncTask<String,Void,String>
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
                DatagramPacket packet= new DatagramPacket(message,msg_length,local,SERVERPORT_MARCE);
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
                ds = new DatagramSocket(RECEIVEDPORT_MARCE); //Puerto DE ESCUCHA
                ds.setSoTimeout(10000);
                ds.receive(dp);  //Recibo la respuesta del servidor
                paquete_existencia = new String(lMsg, 0, dp.getLength());
            }catch (SocketTimeoutException e) {
                Log.i("I/UDP Client", "No llego nada");
                verificar_existencia_marce();
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

            Toast.makeText(getApplicationContext(), "Entre al post execute "+  paquete_existencia, Toast.LENGTH_SHORT).show();

            //Separo la cadena obtenida con la informacion
            StringTokenizer token_existe_marce = new StringTokenizer(paquete_existencia, ";");
            separador_comando_existencia = token_existe_marce.nextToken(); //S
            cabecera_existencia = token_existe_marce.nextToken(); //PMEMARCE
            existe_marce = token_existe_marce.nextToken(); //SI/NO

            //Verifico si ESTA EN MARCE
            if(existe_marce.equals("SI"))    //si existe o es distinta de la 0x0 que signifcaria que no existe?
            {
                planta_recibida_marce = token_existe_marce.nextToken();
                canaleta_recibida_marce = token_existe_marce.nextToken();
                codigo_operario_recibido_marce = token_existe_marce.nextToken();
                nombre_operario_recibido_marce = token_existe_marce.nextToken();
                estado_recibido_marce = token_existe_marce.nextToken();   //PRODUCCION O MONTAJE

                //MELO PORQUE LOS INTENT SE ESTAN ENVIANDO DESDE DONDE DEBE SER DESDE
                //EL LUGAR DONDE LLAME LA FUNCION


            }
            else
            {


                // SI MARCE NO EXISTE ENTONCES DEBE IR A LOGUEARSE EN LA OTRA ACTIVIDAD
                Intent intent_No_marce = new Intent(Selection_Activity.this,LogActivity.class);
                intent_No_marce.putExtra("ESTAB",valor_estabilidad);
                intent_No_marce.putExtra("PLANTA",seleccion_planta);
                intent_No_marce.putExtra("CANALETA",seleccion_canaleta);
                intent_No_marce.putExtra("CODREF",value_codigoref);
                intent_No_marce.putExtra("NOMREF",Nombre_referencia);
                intent_No_marce.putExtra("TALLA",seleccion_talla);
                intent_No_marce.putExtra("COLOR",seleccion_color);
                intent_No_marce.putExtra("ORDENCORTE",seleccion_orden_corte);  //pendiente de enviar la orden de corte QUE DEBE SER ESPECIFICA YA
                intent_No_marce.putExtra("UNIDTALLA",seleccion_unidades_talla); // PENDIENTE DE ENVIAR UNIDADES TALLA
                intent_No_marce.putExtra("IP",direccion_ip);


                //******* PENDIENTE DE ENVIAR TABLA DE MEDIDA****************

                startActivity(intent_No_marce);





            }
        } //Final POST EXECUTE

    }//Final PETICION










}//final Appcompat
