package com.example.das_proyectogrupal_anenaiaramaite;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.das_proyectogrupal_anenaiaramaite.controladores.GestorIdiomas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.das_proyectogrupal_anenaiaramaite.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String idioma;

    private ActivityMainBinding binding;
    private String URL_SERVER = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Asignar el token del dispositivo al usuario logueado (para que reciba los mensajes FCM)
        registrarDispositivo();

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
        super.onCreate(savedInstanceState);

        // Pedir permisos de notificaciones al principio
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            //Si no lo tiene -> PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.navigation_dashboard, R.id.navigation_notifications)
                .build(); //R.id.navigation_home,
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

    // _________________________________________________________________________________________________

    /*  Método registrarDispositivo:
        ----------------------------
            *) Parámetros (Input):
            *) Parámetro (Output):
                    void
            *) Descripción:
                    Actualiza el token del usuario logueado con el del dispositivo actual para que
                    reciba las notificaciones FCM en este aparato.
    */
    private void registrarDispositivo(){
        Log.d("TOKEN","PRUEBA");

        // Obtener el token del dispositivo actual
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {       //Al completar el proceso
                // Comprobar si se ha conseguido el token
                if (!task.isSuccessful()) {
                    Log.d(ContentValues.TAG, "Fetching FCM registration token failed", task.getException());
                    Log.d("TOKEN", "Problemas para obtener el TOKEN");
                    return;
                }
                // Obtener el email del usuario y el token
                String email = getIntent().getExtras().getString("var_email");
                String token = task.getResult();
                Log.e("TOKEN",token);
                Log.e("TOKEN",email);

                // Comprobar que tanto el email como el token tengan valor
                if (token != null && email != null){    // Si no son null --> Actualizar la BBDD
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SERVER+"usuario.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {  // Si la petición ha sido exitosa
                            Log.d("TOKEN",response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { // Si ha ocurriddo un error
                            // Mostrar un mensaje de error
                            String msg = "ERROR  BD";
                            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
                            Log.d("DB_REMOTA", error.toString());
                        }
                    }
                    ){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError { // Parámetros que enviar con la petición
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("id_recurso", "token");
                            parametros.put("token", token);
                            parametros.put("email", email);
                            return parametros;
                        }
                    };

                    // Añadir petición a la cola
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);

                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar el idioma
        outState.putString("var_idioma",  idioma);
    }

}