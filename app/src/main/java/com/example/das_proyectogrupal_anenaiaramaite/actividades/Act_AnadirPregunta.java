package com.example.das_proyectogrupal_anenaiaramaite.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.controladores.GestorIdiomas;
import com.example.das_proyectogrupal_anenaiaramaite.dialogs.DialogCampoVacio;

import java.util.HashMap;
import java.util.Map;

public class Act_AnadirPregunta extends AppCompatActivity {

    private String email;
    private String apellido;
    private String usuario;
    private EditText titulo;
    private EditText enunciado;
    private EditText op1;
    private EditText op2;
    private EditText op3;
    private EditText op4;

    private Button btn_publicar;
    private Button btn_regresar;

    String idioma;
    RequestQueue rq;  // Variable que gestiona el envío de peticiones a la BBDD remota

    private String URL_SERVER = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            this.email = savedInstanceState.getString("var_email");
            this.usuario = savedInstanceState.getString("var_usuario");
            this.apellido = savedInstanceState.getString("var_apellido");
            idioma = savedInstanceState.getString("var_idioma");

        }

        // Comprobar Intent
        Bundle extra = getIntent().getExtras();
        // Si no es null
        if ( extra != null ){
            // Recuperar idioma
            idioma = extra.getString("var_idioma");
            email = extra.getString("var_email");
            apellido = extra.getString("var_apellido");
            usuario = extra.getString("var_usuario");
            Log.d("IDIOMA", "RECOGE BUNDLE (INTENT) -> " + idioma);
        }

        // Settear idioma
        if (idioma != null){
            GestorIdiomas gestorIdiomas = new GestorIdiomas(this);
            gestorIdiomas.setIdioma(idioma);
            Log.d("IDIOMA", "SETTEA -> " + idioma);
        }

        setContentView(R.layout.activity_act_anadir_pregunta);


        // Inicializar la variable que realiza las peticiones a la BBDD remota
        rq = Volley.newRequestQueue(this);


        titulo = findViewById(R.id.add_titulo);
        enunciado = findViewById(R.id.add_enunciado);
        op1 = findViewById(R.id.tv_opcion1);
        op2 = findViewById(R.id.tv_opcion2);
        op3 = findViewById(R.id.tv_opcion3);
        op4 = findViewById(R.id.tv_opcion4);

        btn_publicar = findViewById(R.id.b_guardar);

        btn_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s_t = titulo.getText().toString().trim();
                String s_e = enunciado.getText().toString().trim();
                String s_op1 = op1.getText().toString().trim();
                String s_op2 =  op2.getText().toString().trim();
                String s_op3 = op3.getText().toString().trim();
                String s_op4 = op4.getText().toString().trim();

                // Comprobar que el título no esté vacío
                if (s_t.length() == 0){
                    DialogCampoVacio dialogCampoVacio = new DialogCampoVacio(getResources().getString(R.string.t_campoVacio), getResources().getString(R.string.titulo_vacio));
                    dialogCampoVacio.show(getSupportFragmentManager(), "Vacío");
                }
                else{
                    // Si el contenido es el placeholder
                    if (s_e.equals(getResources().getString(R.string.placeholder_enunciado)) || s_e.length()==0){
                        // Guardar enunciado vacio
                        DialogCampoVacio dialogCampoVacio = new DialogCampoVacio(getResources().getString(R.string.t_campoVacio), getResources().getString(R.string.enunciado_vacio));
                        dialogCampoVacio.show(getSupportFragmentManager(), "Vacío");
                    }
                    else{

                        // Si las opciones 1 y 2 están definidas
                        if (!s_op1.isEmpty() && !s_op2.isEmpty()){
                            // Si el 3 está vacío y el 4to no
                            if (s_op3.isEmpty() && !s_op4.isEmpty()){
                                // Intercambiar el orden
                                s_op3 = s_op4;
                                s_op4 = "";
                            }
                            registrarPregunta(s_t, s_e, s_op1, s_op2, s_op3, s_op4);
                        }
                        else{
                            DialogCampoVacio dialogCampoVacio = new DialogCampoVacio(getResources().getString(R.string.t_op_invalidas), getResources().getString(R.string.error_op));
                            dialogCampoVacio.show(getSupportFragmentManager(), "Vacío");
                        }
                    }
                }
            }
        });

        btn_regresar = findViewById(R.id.imageButton);

        btn_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void registrarPregunta(String s_t, String s_e, String s_op1, String s_op2, String s_op3, String s_op4) {

        String ruta = URL_SERVER + "preguntas.php";

        // 1) Comprobar si una pregunta con ese título y del mismo autor ya existe en la BD
        StringRequest sr = new StringRequest(Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Si no ha pregunta
                if (response.length() == 0){
                    // 2) Insertar pregunta en la BD
                    StringRequest sr = new StringRequest( Request.Method.POST, ruta, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("200")) {
                                Toast.makeText(Act_AnadirPregunta.this, getResources().getString(R.string.preguntaAnadida), Toast.LENGTH_SHORT).show();

                                // Crear un intent para que lo reciba el Fragment que inicia esta actividad (DashboardFragment)
                                Intent i = new Intent();

                                i.putExtra("var_titulo", s_t);

                                i.putExtra("var_enunciado", s_e);
                                i.putExtra("var_emailautor", email);

                                i.putExtra("var_op1", s_op1);
                                i.putExtra("var_op2", s_op2);
                                i.putExtra("var_op3",s_op3);
                                i.putExtra("var_op4",s_op4);
                                i.putExtra("var_idioma",idioma);


                                // Settea el código del resultado a 0 para poder identificarlo
                                setResult(0, i);

                                // Enviar la notificación
                                Log.d("FCM", " --> Va a enviar notificación");
                                enviarNotificacionAñadido();

                                // Destruir la actividad
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("DB_REMOTA", error.toString());
                        }
                    }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parametros = new HashMap<String, String>();
                            parametros.put("id_recurso", "anadirPregunta");
                            parametros.put("titulo", titulo.getText().toString());
                            parametros.put("enunciado", enunciado.getText().toString());
                            parametros.put("email", email);
                            parametros.put("op1", s_op1);
                            parametros.put("op2", s_op2);
                            parametros.put("op3", s_op3);
                            parametros.put("op4", s_op4);

                            return parametros;
                        }
                    };
                    // Añadir solicitud a la cola (RequestQueue)
                    rq.add(sr);
                    //-------------------------------------------------------------------------------


                }
                else{
                    DialogCampoVacio dialogCampoVacio = new DialogCampoVacio(getResources().getString(R.string.t_pregRegistrada), getResources().getString(R.string.m_pregRegistrada));
                    dialogCampoVacio.show(getSupportFragmentManager(), "Vacío");
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
                parametros.put("id_recurso", "getPregunta");
                parametros.put("email", email);
                parametros.put("titulo", s_t);
                return parametros;
            }
        };
        // Añadir solicitud a la cola (RequestQueue)
        rq.add(sr);


    }
    private void enviarNotificacionAñadido() {

        String ruta = URL_SERVER + "enviarNotificacion.php";
        StringRequest sr = new StringRequest(
                Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {}

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DB_REMOTA", error.toString());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                Log.d("Noti", apellido);
                String msg ="";
                switch(idioma){
                    case "es": msg = "¡" +usuario + " " + apellido + " ha creado una nueva encuesta!"; break;
                    case "eu": msg =  usuario + " " + apellido + " (e)k inkesta berri bat sortu du!" ; break;
                    case "en":  msg = usuario + " " + apellido +" has created a new survey!"; break;
                    default: msg = "¡" +usuario + " " + apellido + " ha creado una nueva encuesta!"; break;
                }
                parametros.put("mensaje", msg);
                parametros.put("titulo", titulo.getText().toString());
                return parametros;
            }
        };
        // Añadir solicitud a la cola (RequestQueue)
        rq.add(sr);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar el idioma
        outState.putString("var_idioma",  idioma);
    }
}