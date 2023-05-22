package com.example.das_proyectogrupal_anenaiaramaite.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Act_Registro extends AppCompatActivity {

    private TextView tv_logueate;
    private EditText et_nombre;     // EditText que contiene el nombre del usuario que intenta registrarse
    private EditText et_apellido;   // EditText que contiene el apellido del usuario que intenta registrarse
    private EditText et_email;      // EditText que contiene el email del usuario que intenta registrarse
    private EditText et_password1;  // EditText que contiene la contraseña del usuario que intenta registrarse
    private EditText et_password2;  // EditText que contiene la contraseña del usuario que intenta registrarse
    String idioma;

    //private View v;     // View con la vista asignada a esta actividad
    private RequestQueue requestQueue; // Variable que gestiona el envío de peticiones a la BBDD remota

    // URL del servidor AWS de DAS (22-23)
    private String URL_SERVER = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Comprobar savedInstanceState
        if (savedInstanceState != null){
            idioma = savedInstanceState.getString("var_idioma");
        }


        // Comprobar Intent
        Bundle extra = getIntent().getExtras();
        // Si no es null
        if ( extra != null ){
            // Recuperar idioma
            idioma = extra.getString("var_idioma");
        }

        // Settear idioma
        if (idioma != null){
            GestorIdiomas gestorIdiomas = new GestorIdiomas(this);
            gestorIdiomas.setIdioma(idioma);
        }
        setContentView(R.layout.activity_registro);

        // Obtener los objetos de la vista editados por el usuario
        et_nombre = findViewById(R.id.et_nombreRegistro);
        et_apellido = findViewById(R.id.et_apellidoRegistro);
        et_email = findViewById(R.id.et_emailRegistro);
        et_password1 = findViewById(R.id.et_passwordRegistro1);
        et_password2 = findViewById(R.id.et_passwordRegistro2);

        // Inicializar la variable que realiza las peticiones a la BBDD remota
        requestQueue = Volley.newRequestQueue(this);

        tv_logueate = findViewById(R.id.tv_Logueate);
        tv_logueate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear el intent que redirige la ejecución al Login
                volverLogin();
            }
        });
    }

    public void volverLogin(){
        Intent intent = new Intent(getApplicationContext(), Act_Login.class);
        intent.putExtra("var_idioma", idioma);
        intent.putExtra("var_remail", et_email.getText().toString());
        Log.d("EMAIL", "Envía: " + et_email.getText().toString());
        startActivity(intent);
        finish();
    }

    public void registrarUsuario(View v){

        // Obtener los datos introducidos por el usuario + eliminar espacios en blanco del comienzo
        String nombre = et_nombre.getText().toString().replaceAll("^\\s*","");
        String apellido = et_apellido.getText().toString().replaceAll("^\\s*","");
        String email = et_email.getText().toString().replaceAll("^\\s*","");
        String password1 = et_password1.getText().toString().replaceAll("^\\s*","");
        String password2 = et_password2.getText().toString().replaceAll("^\\s*","");

        // Comprobar que los campos no se encuentren vacíos
        if(nombre.equals("")){
            Toast.makeText(this,getResources().getString(R.string.meteNombre), Toast.LENGTH_LONG).show();
        } else if (apellido.equals("")) {
            Toast.makeText(this,getResources().getString(R.string.meteApellido), Toast.LENGTH_LONG).show();
        } else if (email.equals("")) {
            Toast.makeText(this,getResources().getString(R.string.meteEmail), Toast.LENGTH_LONG).show();
        } else if (password1.equals("") || password2.equals("")) {
            Toast.makeText(this,getResources().getString(R.string.meteContrasena), Toast.LENGTH_LONG).show();
        } else if (!password1.equals(password2)) {    // Comprobar que las contraseñas coincidan
            Toast.makeText(this,getResources().getString(R.string.contrasenaNoCoinciden), Toast.LENGTH_LONG).show();
        } else{     // Si se han completado todos los campos: Comprobar email + Registrar

            // Crear patrón para validar el email
            Pattern pattern = Pattern.compile("([a-z\\d]+(\\.?[a-z\\d])*)+@(([a-z]+)\\.([a-z]+))+");
            Matcher mather = pattern.matcher(email);

            // Comprobar que el email sea válido
            if (mather.find()) {    // El email ingresado es válido
                // Comprobar si el usuario ya se encuentra registrado en la BBDD remota.
                validarUsuario();

            } else {     // El email ingresado es inválido: Imprimir mensaje de error
                Toast.makeText(this, getResources().getString(R.string.emailInvalido), Toast.LENGTH_LONG).show();
            }
        }
    }

    // _________________________________________________________________________________________________

    /*  Método anadirUsuario:
        ---------------------
            *) Parámetros (Input):
                    1) (String) pUrl: Contiene la dirección URL del PHP que registra al usuario en la
                        BBDD.
            *) Parámetro (Output):
                    void
            *) Descripción:
                    Este método se ejecuta tras comprobar que el usuario no se encuentra registrado.
                    Se encarga de añadir al usuario en la BBDD remota y redirigir la ejecución al Login.
    */
    private void anadirUsuario(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SERVER+"usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {    // Si la petición ha sido exitosa

                // Mostrar mensaje informativo
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registroOk), Toast.LENGTH_LONG).show();

                // Volver al Login
                volverLogin();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {   // Si ha ocurriddo un error
                // Mostrar un mensaje de error
                String msg = "Error en la BD";
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
                Log.d("DB_REMOTA", error.toString());
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {// Parámetros que enviar con la petición
                Map<String, String> parametros = new HashMap<>();
                // Enviar parámetros para la petición por PHP
                parametros.put("id_recurso", "registro");
                parametros.put("nombre", et_nombre.getText().toString());
                parametros.put("apellido", et_apellido.getText().toString());
                parametros.put("email", et_email.getText().toString());
                parametros.put("password", et_password1.getText().toString());
                return parametros;
            }
        };

        // Añadir petición a la cola
        requestQueue.add(stringRequest);
    }

// _________________________________________________________________________________________________

    /*  Método validarUsuario:
        ----------------------
            *) Parámetros (Input):
                    1) (String) pUrl: Contiene la dirección URL del PHP que valida al usuario en la BBDD.
            *) Parámetro (Output):
                    void
            *) Descripción:
                    Este método se ejecuta tras validar los datos introducidos en el formulario.
                    Se encarga de comprobar si el usuario se encuentra registrado en la app:
                        - Si está registrado: Muestra un mensaje de error.
                        - Si no está registrado: Llama al método que registra al usuario en la BBDD y
                          redirige la ejecución al Login.
    */
    private void validarUsuario(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SERVER+"usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {   // Si la petición ha sido exitosa
                JSONObject json = null;
                try {
                    // Parsear la respuesta a JSON
                    json = new JSONObject(response);

                    // Comprobar que el usuario no se encuentre registrado
                    if(json.get("exist").toString().equals("false")){
                        // Llamar al método que registra al usuario en la BBDD
                        anadirUsuario();
                    } else{ // Si el usuario se encuentra registrado --> Imprimir mensaje de error
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.yaRegistrado), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { // Si ha ocurrido un error
                // Imprimir mensaje de error
                String msg = "Error BD";
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
                Log.d("DB_REMOTA", error.toString());
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { // Parámetros que enviar con la petición
                // Enviar parámetros para la petición por PHP
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_recurso", "existe"); // Identificador para que el PHP sepa qué función ejecutar
                parametros.put("email", et_email.getText().toString());
                return parametros;
            }
        };
        // Añadir petición a la cola
        requestQueue.add(stringRequest);
    }


    // -------------------------------------------------------------------------------------------------
    // 5)  Método onSaveInstanceState: Se ejecuta antes de hacer onDestroy para guardar los datos de
    //                                 de la pantalla en un objeto Bundle. Este objeto se recupera en
    //                                 el método onCreate.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar el idioma
        outState.putString("var_idioma",  idioma);
        Log.d("IDIOMA", "Guarda BUNDLE: " + idioma);
    }


}