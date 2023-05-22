package com.example.das_proyectogrupal_anenaiaramaite.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.example.das_proyectogrupal_anenaiaramaite.MainActivity;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.controladores.GestorIdiomas;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Act_Login extends AppCompatActivity {

    // El idioma será español por defecto
    String idioma, remail;

    private TextView tv_registrate;
    private EditText et_email;      // EditText que contiene el email del usuario que intenta loguearse
    private EditText et_password;   // EditText que contiene la contraseña del usuario que intenta loguearse

    private RequestQueue requestQueue;  // Variable que gestiona el envío de peticiones a la BBDD remota

    // URL del servidor AWS de DAS (22-23)
    private String URL_SERVER = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Comprobar savedInstanceState
        if (savedInstanceState != null){
            idioma = savedInstanceState.getString("var_idioma");
        }
        else{
            //Por defecto tendrá el idioma español
            idioma = "es";
        }

        // Comprobar Intent
        Bundle extra = getIntent().getExtras();
        // Si no es null
        if ( extra != null ){
            // Recuperar idioma
            idioma = extra.getString("var_idioma");
            remail = extra.getString("var_remail");
            Log.d("EMAIL", "--> RECOGE EN BUNDLE remail: " + remail);
        }

        // Settear idioma
        if (idioma != null){
            GestorIdiomas gestorIdiomas = new GestorIdiomas(this);
            gestorIdiomas.setIdioma(idioma);
        }

        // Renderizar la vista
        setContentView(R.layout.activity_login);

        // Obtener los objetos de la vista editados por el usuario
        et_email = findViewById(R.id.et_emailLogin);
        et_password = findViewById(R.id.et_passwordLogin);

        // Si viene de la activity de registro y ha concretado su email
        Log.d("EMAIL", "VALOR DE remail: " + remail);
        if (remail!=null){
            // Settearlo en el editText
            et_email.setText(remail);
            Log.d("EMAIL", "SETTEA " + remail);
        }

        Context contexto = this;
        TextView b_idioma_es = findViewById(R.id.btn_EspanolLogin);
        b_idioma_es.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Settear idioma
                if (idioma != null){
                    GestorIdiomas gestorIdiomas = new GestorIdiomas(contexto);
                    gestorIdiomas.setIdioma("es");
                    idioma = "es";
                    refrescarActivity();
                    Toast.makeText(contexto, "Idioma cambiado a español.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView b_idioma_en = findViewById(R.id.btn_InglesLogin);
        b_idioma_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Settear idioma
                if (idioma != null){
                    GestorIdiomas gestorIdiomas = new GestorIdiomas(contexto);
                    gestorIdiomas.setIdioma("en");
                    idioma = "en";
                    refrescarActivity();
                    Toast.makeText(contexto, "Language changed to english.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView b_idioma_eu = findViewById(R.id.btn_EuskeraLogin);
        b_idioma_eu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Settear idioma
                if (idioma != null){
                    GestorIdiomas gestorIdiomas = new GestorIdiomas(contexto);
                    gestorIdiomas.setIdioma("eu");
                    idioma = "eu";
                    refrescarActivity();
                    Toast.makeText(contexto, "Hizkuntza euskarara aldatuta.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inicializar la variable que realiza las peticiones a la BBDD remota
        requestQueue = Volley.newRequestQueue(this);

        tv_registrate = findViewById(R.id.tv_Registrate);
        tv_registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                crearCuenta();
            }
        });

    }

    // _________________________________________________________________________________________________

    /*  Método crearCuenta:
        -------------------
            *) Parámetros (Input):
                    1) (View) v: Vista asociada al Activity actual.
            *) Parámetro (Output):
                    void
            *) Descripción:
                    Este método se ejecuta cuando el usuario pulsa el texto "¡Regístrate!". Se encarga
                    de redirigir la ejecución al Activity Registro.
    */
    public void crearCuenta(){
        // Crear el intent que redirige la ejecución al Registro
        Intent intent = new Intent(getApplicationContext(), Act_Registro.class);
        // Enviar datos
        intent.putExtra("var_idioma", idioma);
        startActivity(intent);
        finish();
    }

    // _________________________________________________________________________________________________

    /*  Método ingresar:
        ----------------
            *) Parámetros (Input):
                1) (View) v: Vista asociada al Activity actual
            *) Parámetro (Output):
                    void
            *) Descripción:
                    Este método se ejecuta cuando el usuario pulsa el botón "INGRESAR".
                    Valida la entrada de datos:
                        - Si los datos son válidos: Llama a la función que comprueba si el usuario está
                          registrado en la BBDD.
                        - Si los datos no son válidos: Muestra un mensaje de error.
    */
    public void ingresar(View v){

        // Obtener los datos introducidos por el usuario + eliminar espacios en blanco del comienzo
        String email = et_email.getText().toString().replaceAll("^\\s*","");
        String password = et_password.getText().toString().replaceAll("^\\s*","");

        // Comprobar que los campos no se encuentren vacíos
        if(email.equals("")){               // Si el email está vacío: Escribir un mensaje de error
            Toast.makeText(this,getResources().getString(R.string.ingresa_Email), Toast.LENGTH_LONG).show();
        } else if (password.equals("")) {   // Si la contraseña está vacía: Escribir un mensaje de error
            Toast.makeText(this, getResources().getString(R.string.ingresaPass), Toast.LENGTH_LONG).show();
        } else{

            // Crear un patrón para validar el email
            Pattern pattern = Pattern.compile("([a-z\\d]+(\\.?[a-z\\d])*)+@(([a-z]+)\\.([a-z]+))+");
            Matcher mather = pattern.matcher(email);

            // Comprobar que el email sea válido
            if (mather.find()) {    // El email ingresado es válido
                // Validar registro del usuario
                validarUsuario();
            } else {        // El email ingresado es inválido: Imprimir mensaje de error
                Toast.makeText(this, getResources().getString(R.string.passwd_incorrecta), Toast.LENGTH_LONG).show();
            }
        }
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
                        - Si está registrado: Redirige la ejecución al menú principal de la aplicación.
                        - Si no está registrado: Muestra un mensaje de error.

    */
    private void validarUsuario(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SERVER+"usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {   // Si la petición ha sido exitosa

                JSONObject json = null;
                try {
                    // Parsear la respuesta a JSON
                    json = new JSONObject(response);

                    // Comprobar que el usuario se encuentre registrado
                    if(json.get("exist").toString().equals("true")){

                        // Crear un intent para pasar a la Actividad Menu_Principal
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        // Guardar los datos del usuario (para mantener la sesión)
                        intent.putExtra("var_usuario", json.get("nombre").toString());
                        intent.putExtra("var_apellido", json.get("apellido").toString());
                        intent.putExtra("var_email", json.get("email").toString());
                        intent.putExtra("var_idioma", idioma);

                        // Cargar el Menú Principal
                        startActivity(intent);
                        finish();

                    } else{ // Si el usuario no se encuentra registrado --> Imprimir mensaje de error
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.userPass_incorrectos), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {    // Si ha ocurriddo un error
                // Mostrar un mensaje de error
                String msg = "Error DB";
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
                Log.d("DB_REMOTA", error.toString());
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { // Parámetros que enviar con la petición
                // Enviar parámetros para la petición por PHP
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_recurso", "login");  // Identificador para que el PHP sepa qué función ejecutar
                parametros.put("email", et_email.getText().toString());
                parametros.put("password", et_password.getText().toString());
                return parametros;
            }
        };
        // Añadir petición a la cola
        requestQueue.add(stringRequest);
    }

    // 7)  Método refrescarActivity: Lanza un intent a la propia actividad para aplicar los cambios
    //                               en el idioma
    public void refrescarActivity(){
        // Refrescar la página
        Intent i = getIntent();
        // Enviar datos
        i.putExtra("var_idioma", idioma);
        // Destruir actividad actual
        finish();
        // Lanzar activity
        startActivity(i);
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
    }
}