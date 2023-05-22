package com.example.das_proyectogrupal_anenaiaramaite.actividades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.controladores.GestorIdiomas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class Act_Encuesta extends AppCompatActivity {

    //Inicializar variables
    SeekBar sb1, sb2, sb3, sb4;
    TextView tv_op1, tv_op2, tv_op3, tv_op4, tv_noVotos;
    TextView tv_titulo, tv_autor, tv_enunciado, tv_numVotos;
    TextView tv_porcent1, tv_porcent2, tv_porcent3, tv_porcent4;
    PieChartView pieChartView;
    ColumnChartView columnChartView;

    Button b_enviar, b_pie, b_barras;

    double count1_anterior=0, count2_anterior=0, count3_anterior=0, count4_anterior=0;
    double count1=0, count2=0, count3=0, count4=0;
    boolean flag1=true, flag2=true, flag3=true, flag4=true, haVotado;


    String idioma, titulo, enunciado, email, op1, op2, op3, op4, usuario, apellido, email_autor, respuesta, respRegistrada;
    int id, pos, numOpciones, numVotos, numResp;
    boolean cerrada;

    private String URL_SERVER = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";
    RequestQueue rq;  // Variable que gestiona el envío de peticiones a la BBDD remota

    public Act_Encuesta(){ }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //------------------------------------------------------------------------------------------
        // Gestionar pérdida de información al girar la pantalla:
        // GUARDAR:
        //      * EMAIL
        //      * USUARIO
        //      * APELLIDO
        if (savedInstanceState != null){
            this.email = savedInstanceState.getString("var_email");
            this.usuario = savedInstanceState.getString("var_usuario");
            this.apellido = savedInstanceState.getString("var_apellido");
            this.idioma = savedInstanceState.getString("var_idioma");
            this.numVotos = savedInstanceState.getInt("var_numVotos");

            this.id = savedInstanceState.getInt("var_id");
            this.pos = savedInstanceState.getInt("var_pos");
            this.cerrada = savedInstanceState.getBoolean("var_cerrada");
            this.titulo = savedInstanceState.getString("var_titulo");
            this.enunciado = savedInstanceState.getString("var_enunciado");
            this.email_autor = savedInstanceState.getString("var_emailautor");
            this.haVotado = savedInstanceState.getBoolean("var_haVotado");
            this.respRegistrada = savedInstanceState.getString("var_respRegistrada");

            this.count1 = savedInstanceState.getInt("var_count1");
            this.count2 = savedInstanceState.getInt("var_count2");
            this.count3 = savedInstanceState.getInt("var_count3");
            this.count4 = savedInstanceState.getInt("var_count4");

            this.op1 = savedInstanceState.getString("var_op1");
            this.op2 = savedInstanceState.getString("var_op2");
            this.op3 = savedInstanceState.getString("var_op3");
            this.op4 = savedInstanceState.getString("var_op4");

            this.flag1 = savedInstanceState.getBoolean("var_flag1");
            this.flag2 = savedInstanceState.getBoolean("var_flag2");
            this.flag3 = savedInstanceState.getBoolean("var_flag3");
            this.flag4 = savedInstanceState.getBoolean("var_flag4");
        }


        // Definir número de preguntas por defecto
        numOpciones = 4;
        //------------------------------------------------------------------------------------------
        // Obtener intent lanzado desde el menú principal hasta la Act_Encuesta.
        Bundle extras = getIntent().getExtras();
        // Si tiene datos, recogerlos
        if (extras !=null) {
            email = extras.getString("var_email");
            usuario = extras.getString("var_usuario");
            apellido = extras.getString("var_apellido");
            idioma = extras.getString("var_idioma");

            id = extras.getInt("var_id");
            titulo = extras.getString("var_titulo");
            cerrada = extras.getBoolean("var_cerrada");
            enunciado = extras.getString("var_enunciado");
            email_autor = extras.getString("var_emailautor");
            haVotado = extras.getBoolean("var_haVotado");
            respRegistrada = extras.getString("var_respRegistrada");

            count1 = extras.getInt("var_cont1");
            count2 = extras.getInt("var_cont2");
            count3 = extras.getInt("var_cont3");
            count4 = extras.getInt("var_cont4");

            numVotos = (int) (count1 + count2 + count3 + count4);

            op1 = extras.getString("var_op1");
            op2 = extras.getString("var_op2");
            op3 = extras.getString("var_op3");
            op4 = extras.getString("var_op4");

            flag1=true; flag2=true; flag3=true; flag4=true;
        }

        // Settear idioma
        if (idioma != null){
            GestorIdiomas gestorIdiomas = new GestorIdiomas(this);
            gestorIdiomas.setIdioma(idioma);
        }

        setContentView(R.layout.activity_encuesta);

        // Conseguir elementos del layout
        sb1 = findViewById(R.id.sb_1);
        sb2 = findViewById(R.id.sb_2);
        sb3 = findViewById(R.id.sb_3);
        sb4 = findViewById(R.id.sb_4);

        tv_op1 = findViewById(R.id.tv_opcion1);
        tv_op2 = findViewById(R.id.tv_opcion2);
        tv_op3 = findViewById(R.id.tv_opcion3);
        tv_op4 = findViewById(R.id.tv_opcion4);
        tv_noVotos = findViewById(R.id.tv_noVotos);
        tv_numVotos = findViewById(R.id.tv_numVotos);

        tv_porcent1 = findViewById(R.id.tv_porcentaje1);
        tv_porcent2 = findViewById(R.id.tv_porcentaje2);
        tv_porcent3 = findViewById(R.id.tv_porcentaje3);
        tv_porcent4 = findViewById(R.id.tv_porcentaje4);

        tv_titulo = findViewById(R.id.tv_pregunta);
        tv_autor = findViewById(R.id.tv_emailautor);
        tv_enunciado = findViewById(R.id.tv_enunciado);

        // Botones
        b_enviar = findViewById(R.id.b_enviar);
        b_pie = findViewById(R.id.b_pie);
        b_barras = findViewById(R.id.b_barras);

        // Actualizar num Votos totales
        tv_numVotos.setText(getResources().getString(R.string.numVotos) + " " + numVotos+"");

        b_pie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                columnChartView.setVisibility(View.GONE);
                pieChartView.setVisibility(View.VISIBLE);

            }
        });

        b_barras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChartView.setVisibility(View.GONE);
                columnChartView.setVisibility(View.VISIBLE);

            }
        });

        // Gráficas
        pieChartView = findViewById(R.id.graficaPieChart);
        columnChartView = findViewById(R.id.graficaBarras);

        if (op3.isEmpty() && op4.isEmpty()){
            sb3.setVisibility(View.GONE);
            sb4.setVisibility(View.GONE);
            tv_porcent3.setVisibility(View.GONE);
            tv_porcent4.setVisibility(View.GONE);
            numOpciones = 2;
        }
        else{
            if (op4.isEmpty()){
                sb4.setVisibility(View.GONE);
                tv_porcent4.setVisibility(View.GONE);
                numOpciones = 3;
            }
        }

        //Settear los datos de la pregunta
        tv_titulo.setText(titulo);
        tv_autor.setText(getResources().getString(R.string.preguntaDe) +" "+ email_autor);

        if (enunciado.length() == 0){
            tv_enunciado.setVisibility(View.GONE);
        }
        else{
            tv_enunciado.setText(enunciado);
        }

        tv_op1.setText(op1);
        tv_op2.setText(op2);
        tv_op3.setText(op3);
        tv_op4.setText(op4);

        Log.e("ENCUESTA", "Contadores");
        Log.e("ENCUESTA", "C1-->"+count1);
        Log.e("ENCUESTA", "C2-->"+count2);
        Log.e("ENCUESTA", "C3-->"+count3);
        Log.e("ENCUESTA", "C4-->"+count4);

        if (!this.flag1){ sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));}
        if (!this.flag2){ sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));}
        if (!this.flag3){ sb3.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));}
        if (!this.flag4){ sb4.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));}

        // Dibujar gráficas
        cargarGraficaPie();
        cargarGraficaBarras();

        // Dejar sólo una visible
        columnChartView.setVisibility(View.GONE);

        //Cachear votos
        count1_anterior=count1; count2_anterior=count2; count3_anterior=count3; count4_anterior=count4;
        calcularPorcentaje();

    if (!haVotado){

        // ** Settear listener de las respuestas de la encuesta ** //
        //==========================================================//
        // 1.1) SEEK_BAR_1
        sb1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb1.startAnimation(AnimationUtils.loadAnimation(sb1.getContext(), R.anim.sacudir));
                calcularPorcentaje();
                return true;
            }
        });
        // 1.2) OPCION_1
        tv_op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_noVotos.setVisibility(View.GONE);
                // Comprobar condición
                if (flag1){
                    Log.d("RESPUESTA", "Has clicado 1");
                    // Si flag 1 es true
                    numResp = 1;
                    count1++;
                    respuesta = tv_op1.getText().toString();
                    count2=count2_anterior;
                    count3=count3_anterior;
                    count4=count4_anterior;

                    flag1=false;
                    flag2=true;
                    flag3=true;
                    flag4=true;
                    //Calcular el porcentaje de las opciones
                    calcularPorcentaje();

                    sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));
                    sb1.setProgress(100, true);
                    sb2.setProgress(0, true);
                    sb3.setProgress(0, true);
                    sb4.setProgress(0, true);
                    /*sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));
                    sb3.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));
                    sb4.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));*/
                }
            }
        });
        //==========================================================//
        // 2.1) SEEK_BAR_2
        sb2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb2.startAnimation(AnimationUtils.loadAnimation(sb2.getContext(), R.anim.sacudir));
                calcularPorcentaje();
                return true;
            }
        });
        // 2.2) OPCION_2
        tv_op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_noVotos.setVisibility(View.GONE);
                // Comprobar condición
                if (flag2){
                    Log.d("RESPUESTA", "Has clicado 2");
                    // Si flag 2 es true
                    count2++;
                    numResp=2;
                    respuesta = tv_op2.getText().toString();
                    count1=count1_anterior;
                    count3=count3_anterior;
                    count4=count4_anterior;

                    flag1=true;
                    flag2=false;
                    flag3=true;
                    flag4=true;
                    //Calcular el porcentaje de las opciones
                    calcularPorcentaje();

                    sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));
                    sb2.setProgress(100, true);
                    sb1.setProgress(0, true);
                    sb3.setProgress(0, true);
                    sb4.setProgress(0, true);
                   /* sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));
                    sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));
                    sb3.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));
                    sb4.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));*/
                }
            }
        });
        //==========================================================//
        // 3.1) SEEK_BAR_3
        sb3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb3.startAnimation(AnimationUtils.loadAnimation(sb3.getContext(), R.anim.sacudir));
               // calcularPorcentaje();
                return true;
            }
        });
        // 3.2) OPCION_3
        tv_op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_noVotos.setVisibility(View.GONE);
                // Comprobar condición
                if (flag3){
                    Log.d("RESPUESTA", "Has clicado 3");
                    // Si flag 3 es true
                    count3++;
                    respuesta = tv_op3.getText().toString();
                    count1=count1_anterior;
                    count2=count2_anterior;
                    count4=count4_anterior;
                    numResp=3;

                    flag1=true;
                    flag2=true;
                    flag3=false;
                    flag4=true;
                    //Calcular el porcentaje de las opciones
                    calcularPorcentaje();

                    sb3.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));
                    sb3.setProgress(100, true);
                    sb2.setProgress(0, true);
                    sb1.setProgress(0, true);
                    sb4.setProgress(0, true);

                }
            }
        });
        //==========================================================//
        // 4.1) SEEK_BAR_4
        sb4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb4.startAnimation(AnimationUtils.loadAnimation(sb4.getContext(), R.anim.sacudir));
                calcularPorcentaje();
                return true;
            }
        });
        // 4.2) OPCION_4
        tv_op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_noVotos.setVisibility(View.GONE);
                // Comprobar condición
                if (flag4){
                    Log.d("RESPUESTA", "Has clicado 4");
                    // Si flag 4 es true
                    count4++;
                    respuesta = tv_op4.getText().toString();
                    count1=count1_anterior;
                    count2=count2_anterior;
                    count3=count3_anterior;
                    numResp=4;

                    flag1=true;
                    flag2=true;
                    flag3=true;
                    flag4=false;
                    //Calcular el porcentaje de las opciones
                    calcularPorcentaje();

                    sb4.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));
                    sb4.setProgress(100, true);
                    sb1.setProgress(0, true);
                    sb3.setProgress(0, true);
                    sb2.setProgress(0, true);
                  /*  sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));
                    sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));
                    sb3.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track));
                    sb4.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_azul));*/

                }

            }
        });
    }
    else{  // Ya ha votado

        // Implementar método por defecto de onTouch
        sb1.setEnabled(false);
        sb2.setEnabled(false);
        sb3.setEnabled(false);
        sb4.setEnabled(false);

        // Si es la elegida por el usuario
        if (tv_op1.getText().equals(respRegistrada)){
            sb1.setProgress(100, true);
            sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_elegido));
        }
        else{  // Si no, pintar de gris
            sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_no_elegido));
        }

        // Si es la elegida por el usuario
        if (tv_op2.getText().equals(respRegistrada)){
            sb2.setProgress(100, true);
            sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_elegido));
        }
        else{  // Si no, pintar de gris
            sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_no_elegido));
        }

        // Si es la elegida por el usuario
        if (tv_op3.getText().equals(respRegistrada)){
            sb3.setProgress(100, true);
            sb3.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_elegido));
        }
        else{  // Si no, pintar de gris
            sb3.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_no_elegido));
        }

        // Si es la elegida por el usuario
        if (tv_op4.getText().equals(respRegistrada)){
            sb4.setProgress(100, true);
            sb4.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_elegido));
        }
        else{  // Si no, pintar de gris
            sb4.setProgressDrawable(getResources().getDrawable(R.drawable.progress_track_no_elegido));
        }

        b_enviar.setVisibility(View.GONE);
        haVotado = true;

    }
        b_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!haVotado){
                    RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                    String ruta = URL_SERVER + "preguntas.php";
                    StringRequest sr = new StringRequest(
                            Request.Method.POST, ruta, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("200")){
                                Toast.makeText(Act_Encuesta.this, getResources().getString(R.string.votoOK), Toast.LENGTH_SHORT).show();
                                haVotado  = true;
                                // Crear un intent para que lo reciba el Fragment que inicia esta actividad (DashboardFragment)
                                Intent i2 = new Intent();

                                i2.putExtra("var_titulo", titulo);
                                i2.putExtra("var_emailautor", email);

                                i2.putExtra("var_count1", (int)count1);
                                i2.putExtra("var_count2", (int)count2);
                                i2.putExtra("var_count3", (int)count3);
                                i2.putExtra("var_count4", (int)count4);

                                Log.e("NULL", "Valores enviados:");
                                Log.e("NULL", "--------------------");
                                Log.e("NULL", "counts --> "+count1+", "+count2+", "+count3+", "+count4);
                                Log.e("NULL", "email y titulo --> "+email+" - "+titulo);
                                Log.e("NULL", "--");
                                Log.e("NULL", "--");

                                // Settea el código del resultado a 0 para poder identificarlo
                                setResult(1, i2);
                                // Se deja a la espera del finish()

                                finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DB_REMOTA", error.toString());}
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("id_recurso", "actualizarVotos");
                        parametros.put("titulo", titulo);
                        parametros.put("email_autor", email_autor);
                        parametros.put("email_votante", email);
                        parametros.put("respuesta", respuesta);
                        parametros.put("numResp", numResp+"");

                        return parametros;
                    }
                };
                // Añadir solicitud a la cola (RequestQueue)
                rq.add(sr);

                }else{
                    Toast.makeText(Act_Encuesta.this, "¡¡¡YA HAS VOTADO EN ESTA ENCUESTA!!!", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private void cargarGraficaBarras() {
        // Si no hay votos
        if (count1 + count2 + count3 + count4 == 0.0){
            tv_noVotos.setVisibility(View.VISIBLE);
            tv_noVotos.setText(R.string.no_votos);
            tv_noVotos.setBackgroundColor(getResources().getColor(R.color.negroTransp));

        }
        else{ // Cargar los datos en la gráfica

            List<Column> columns = new ArrayList<Column>();
            // Definir subcolumnas
            List datos_aux = new ArrayList<>();
            datos_aux.add(new SubcolumnValue((float) count1, getResources().getColor(R.color.g_rojo)).setLabel(op1));
            datos_aux.add(new SubcolumnValue((float) count2, getResources().getColor(R.color.g_verde)).setLabel(op2));


                datos_aux.add(new SubcolumnValue((float) count3, getResources().getColor(R.color.g_azul)).setLabel(op3));


                datos_aux.add(new SubcolumnValue((float) count4, getResources().getColor(R.color.g_amarillo)).setLabel(op4));



            // Añadir subcolumnas a una columna
            Column column = new Column(datos_aux);
            column.setHasLabels(true);
            columns.add(column);

            ColumnChartData data = new ColumnChartData(columns);
            data.setStacked(false);

            // Settear la configuración
            columnChartView.setColumnChartData(data);

        }
    }

    /** FUENTE: https://github.com/lecho/hellocharts-android **/
    private void cargarGraficaPie() {

        // Si no hay votos
        if (count1 + count2 + count3 + count4 == 0.0){
            tv_noVotos.setText(R.string.no_votos);
            tv_noVotos.setBackgroundColor(getResources().getColor(R.color.negroTransp));

        }
        else{ // Cargar los datos en la gráfica
            // Crear array con las secciones de la tarta
            List datos = new ArrayList<>();
            datos.add(new SliceValue((float) count1, getResources().getColor(R.color.g_rojo)).setLabel(op1));
            datos.add(new SliceValue((float) count2,  getResources().getColor(R.color.g_verde)).setLabel(op2));
            datos.add(new SliceValue((float) count3,  getResources().getColor(R.color.g_azul)).setLabel(op3));
            datos.add(new SliceValue((float) count4, getResources().getColor(R.color.g_amarillo)).setLabel(op4));

            // Crear objeto para configurar el PieChartData
            PieChartData pieChartData = new PieChartData(datos);
            pieChartData.setHasLabels(true).setValueLabelTextSize(14);
            pieChartData.setHasCenterCircle(true);

            // Settear la configuración
            pieChartView.setPieChartData(pieChartData);

        }


    }

    private void calcularPorcentaje() {

        //Calcular total
        Log.d("RESPUESTA","--> C1="+count1+" C2="+count2+" C3="+count3+" C4="+count4);
        double total = count1 + count2 + count3 + count4;
        if (total == 0){total=1;}

        Log.d("RESPUESTA","FLAGS--> F1="+flag1+" C2="+flag2+" C3="+flag3+" C4="+flag4);

        //Calcular el porcentaje para todas las opciones
        double porc1 = (count1/total)*100;
        double porc2 = (count2/total)*100;
        double porc3 = 0.0, porc4 = 0.0;

        if (numOpciones==3){
            porc3= (count3/total)*100;
        }
        if (numOpciones==4){
            porc3 = (count3/total)*100;
            porc4 = (count4/total)*100;
        }

        Log.d("RESPUESTA","--> P1="+porc1+" P2="+porc2+" P3="+porc3+" P4="+porc4);
        //Colocar porcentaje en TextViews del layout

        // ----- Opción 1
        tv_porcent1.setText(String.format("%.0f%%", porc1));
        //sb1.setProgress(0, true);
        //sb1.setProgress((int) porc1, true);
        //Log.d("RESPUESTA", "% de 1 -->"+porc1);
        // ----- Opción 2
        tv_porcent2.setText(String.format("%.0f%%", porc2));
        //sb2.setProgress(0, true);
        //sb2.setProgress((int)porc2, true);
        //Log.d("RESPUESTA", "% de 2 -->"+porc2);

        // ----- Opción 3
        if (numOpciones >= 3){
            tv_porcent3.setText(String.format("%.0f%%", porc3));
            //sb3.setProgress(0, true);
            //sb3.setProgress((int)porc3, true);
            //Log.d("RESPUESTA", "% de 3 -->"+porc3);
        }

        // ----- Opción 4
        if (numOpciones == 4) {
            tv_porcent4.setText(String.format("%.0f%%", porc4));
            //sb4.setProgress(0, true);
            //sb4.setProgress((int) porc4, true);
            //Log.d("RESPUESTA", "% de 4 -->"+porc4);
        }
        // Actualizar gráficas
        cargarGraficaPie();
        cargarGraficaBarras();

        // Actualizar num Votos totales
        numVotos = (int) (count1 + count2 + count3 + count4);
        tv_numVotos.setText(getResources().getString(R.string.numVotos) + " " + numVotos+"");
    }

    //---------------------------------------------------------------------------------
    //    Método ON_SAVE_INSTANCE_STATE: Este método se ejecuta antes del destroy() de la
    //    aplicación, por lo que es aquí dónde se deben guardar los datos que se quieran
    //    mantener. Por ejemplo,para evitar la pérdida de datos al girar el móvil, interrupciones,
    //    etc.
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        int t = (int) (count1 + count2 + count3 + count4);
        super.onSaveInstanceState(outState);
        // Guardar datos para evitar la pérdida al girar el móvil, interrupciones, etc.
        outState.putString("var_email", this.email);
        outState.putString("var_usuario", this.usuario);
        outState.putString("var_apellido", this.apellido);

        outState.putInt("var_id", this.id);
        outState.putString("var_titulo", this.titulo);
        outState.putBoolean("var_cerrada", this.cerrada);
        outState.putString("var_enunciado", this.enunciado);
        outState.putString("var_emailautor", this.email_autor);
        outState.putBoolean("var_haVotado", this.haVotado);
        outState.putString("var_respRegistrada", this.respRegistrada);

        outState.putInt("var_count1", (int) count1);
        outState.putInt("var_count2", (int) count2);
        outState.putInt("var_count3", (int) count3);
        outState.putInt("var_count4", (int) count4);
        outState.putInt("var_numVotos", (int) t );

        outState.putString("var_op1", this.op1);
        outState.putString("var_op2", this.op2);
        outState.putString("var_op3", this.op3);
        outState.putString("var_op4", this.op4);

        outState.putBoolean("var_flag1", flag1);
        outState.putBoolean("var_flag2", flag2);
        outState.putBoolean("var_flag3", flag3);
        outState.putBoolean("var_flag4", flag4);

        outState.putString("var_idioma", idioma);
    }
}