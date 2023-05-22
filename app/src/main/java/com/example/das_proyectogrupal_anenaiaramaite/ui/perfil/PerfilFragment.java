package com.example.das_proyectogrupal_anenaiaramaite.ui.perfil;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.controladores.GestorIdiomas;
import com.example.das_proyectogrupal_anenaiaramaite.databinding.FragmentNotificationsBinding;
import com.example.das_proyectogrupal_anenaiaramaite.dialogs.DialogIdioma;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {

    // Atributos privados
    private FragmentNotificationsBinding binding;
    private String usuario;
    private String apellido;
    private String email;
    private String votos;
    private String publicaciones;
    private String fecha;
    String idioma;

    Button b_idioma;

    // Objeto RequestQueue para peticiones al servidor AWS
    private RequestQueue rq;

    // URL del servidor AWS de DAS (22-23)
    private static final String URL_server="http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";

    //---------------------------------------------------------------------------------
    // 1.1) Método constructor (vacío)
    public PerfilFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Extraído de StackOverflow
         Pregunta: https://stackoverflow.com/questions/25093546/android-os-networkonmainthreadexception-at-android-os-strictmodeandroidblockgua
         Autor: https://stackoverflow.com/users/1776216/apriya
         **/

        // Líneas necesarias para realizar una petición HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //------------------------------------------------------------------------------------------
        Bundle extras = getActivity().getIntent().getExtras();
        // Si tiene datos, recogerlos
        if (extras !=null) {
            email = extras.getString("var_email");
            usuario = extras.getString("var_usuario");
            apellido = extras.getString("var_apellido");
        }

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
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // {"votos":"10","publicaciones":"1","fecha":"2023-05-07"}
        // CONSULTA A DB REMOTA --> Obtener estadísticas del usuario
        // Indicar recurso del servidor

        // Instanciar el gestor de idiomas
        GestorIdiomas gestorIdiomas = new GestorIdiomas(getActivity());

        Log.e("PERFIL", "EMAIL:"+ email);
        Log.e("PERFIL", "USUARIO:"+ usuario);
        Log.e("PERFIL", "APELLIDO:"+ apellido);

        rq = Volley.newRequestQueue(getActivity());
        String ruta = URL_server + "contesta.php";
        StringRequest sr = new StringRequest(
                Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Si ha habido respuesta, obtener los datos del usuario
                // FORMATO: {"votos":"9","publicaciones":"1","fecha":"2023-05-07"}
                if (response.length()>0) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        // Extraer datos a partir de elementos JSON
                        votos = json.getString("votos");
                        publicaciones = json.getString("publicaciones");
                        fecha = json.getString("fecha");

                        Log.e("PERFIL", "VOTOS:"+ votos);
                        Log.e("PERFIL", "PUBLICACIONES:"+ publicaciones);
                        Log.e("PERFIL", "FECHA:"+ fecha);

                        TextView miEmail = binding.getRoot().findViewById(R.id.perfil_email);
                        TextView miNombre = binding.getRoot().findViewById(R.id.perfil_nombre);
                        TextView misVotos = binding.getRoot().findViewById(R.id.tv_num_votos);
                        TextView misPublicaciones = binding.getRoot().findViewById(R.id.tv_num_publicadas);
                        TextView miFecha = binding.getRoot().findViewById(R.id.tv_fechaUnion);
                        miEmail.setText(email);
                        miNombre.setText(usuario+" "+apellido);
                        misVotos.setText(votos);
                        misPublicaciones.setText(publicaciones);
                        miFecha.setText(fecha);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    // Asignar un nombre por defecto, no debería ocurrir
                    Log.d("DB_REMOTA", "Error, no se obtuvo respuesta");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Si ha habido un error, indicarlo en el log de errores de la app
                Log.e("DB_REMOTA", "Error al obtener estadísticas del usuario");
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Preparar parámetros para el PHP
                Map<String, String> parametros = new HashMap<String, String>();
                // RECURSO: contesta.php > getEstadisticas
                parametros.put("id_recurso", "getEstadisticas");
                parametros.put("email", email);
                // Enviar
                return parametros;
            }
        };
        // Añadir solicitud a la cola (RequestQueue)
        rq.add(sr);

        // .......~~~~****###### CARGAR IMAGEN DE PERFIL #######******~~~~~~......... //

        // Preparar URL (genérica) al recurso, generada a partir del email del usuario
        String direccion = URL_server + "PERFILES/" + "profile_"+email.replace("@", "").replace(".", "")+".jpeg";
        URL destino = null;

        try {
            // Crear objeto URL
            destino = new URL(direccion);
            // Establecer conexión y realizar una petición por GET
            HttpURLConnection conn = (HttpURLConnection) destino.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = 0;
            // Lanza petición
            conn.connect();

            try {
                // Recoger código de respuesta
                responseCode = conn.getResponseCode();
                // Si code 200 = SI HA IDO OK
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Obtener ImageView de la vista
                    ImageView iv_fotoperfil = view.findViewById(R.id.iv_perfil);
                    // Convertir imagen a Bitmap
                    Bitmap bitmapPerfil = BitmapFactory.decodeStream(conn.getInputStream());
                    //Colocar la imagen
                    iv_fotoperfil.setImageBitmap(Bitmap.createScaledBitmap(bitmapPerfil, 120, 120, false));
                    Log.e("PERFIL", "---> IMAGEN SETTEADA EN CAMPO CON ÉXITO");
                }
            }
            catch (MalformedURLException e) {
                Log.e("DB_REMOTA", e.getMessage().toString());
                throw new RuntimeException(e);
            }
        }
        catch (IOException e) {
            Log.e("DB_REMOTA", e.getMessage().toString());
        }

        // .......~~~~****###### CAMBIAR IMAGEN DE PERFIL #######******~~~~~~......... //

        // Preparar método ON_CLICK del pequeño icono de la cámara. Si se pulsa, se
        // iniciarán las acciones relativas al cambio de imagen de perfil
        ImageView iv_perfil = view.findViewById(R.id.camara_small);
        iv_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si la versión es superior a Marshmallow, comprobar permisos
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Comprobar si el permiso ha sido concedido
                    if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        // Si no está permitido, pedirlo
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permission, 600);
                    } else {
                        // Si está permitido, lanzar el intent a la aplicación de cámara
                        mostrarCuadroDialogo();
                    }
                } else {
                    // Si es inferior a MarshMallow, no solicitar permisos
                    mostrarCuadroDialogo();
                }
            }
        });

        b_idioma = binding.getRoot().findViewById(R.id.b_idioma);
        // if (b_idioma!=null){
        b_idioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Muesta un diálogo para cambiar el idioma
                DialogIdioma dialogIdioma = new DialogIdioma( idioma, email, getActivity());
                dialogIdioma.show(getActivity().getSupportFragmentManager(), "Idioma");
            }
        });
       // }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    //---------------------------------------------------------------------------------
    // 5) Método TAKE_PICTURE_LAUNCHER: Este será el encargado de llamar al método de subir
    // la imagen al servidor cuando se SAQUE POR LA CÁMARA
    private ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // Cuando haya recibido una respuesta y sea 'RESULT_OK'
                if (result.getResultCode() == RESULT_OK && result.getData()!= null) {
                    Bundle bundle = result.getData().getExtras();
                    // Obtener imagen y colocarla en el ImageView como Bitmap
                    ImageView iv_fotoPerfil = getActivity().findViewById(R.id.iv_perfil);
                    Bitmap foto = (Bitmap) bundle.get("data");
                    iv_fotoPerfil.setImageBitmap(foto);

                    // Obtener imagen en base 64 a partir del Bitmap
                    String fotoen64 = getImagenBase64(foto);
                    String nombre= "profile_"+email.replace("@", "").replace(".", "")+".jpeg";

                    // Subir imagen al servidor AWS de DAS
                    subirImagenAWS(fotoen64, nombre);
                }
                else {
                    Log.d("PERFIL", "No se ha sacado ninguna foto con la cámara");
                }
            });

    //---------------------------------------------------------------------------------
    // 6) Método TAKE_FROM_GALLERY: Este será el encargado de llamar al método de subir
    // la imagen al servidor cuando se SELECCIONE POR LA GALERÍA
    private ActivityResultLauncher<Intent> takeFromGallery =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // Cuando haya recibido una respuesta y sea 'RESULT_OK'
                if (result.getResultCode() == RESULT_OK && result.getData()!= null) {
                    Intent data = result.getData();
                    Uri imagen = data.getData();
                    ImageView iv_fotoPerfil = getActivity().findViewById(R.id.iv_perfil);
                    Bitmap foto = null;
                    try {
                        // Obtener foto de la galería en formato Bitmap
                        foto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagen);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // Settear imagen en ImageView
                    iv_fotoPerfil.setImageBitmap(foto);

                    // Obtener imagen en base 64, y generar nombre para la foto de perfil de este usuario en concreto
                    String fotoen64 = getImagenBase64(foto);
                    String nombre= "profile_"+email.replace("@", "").replace(".", "")+".jpeg";

                    // Subir imagen al servidor AWS de DAS
                    subirImagenAWS(fotoen64, nombre);
                }
                else {
                    Log.d("PERFIL", "No se ha seleccionado ninguna foto de la galería");
                }
            });

    //---------------------------------------------------------------------------------
    // 7) Método GET_IMAGEN_BASE64: Este método recibe como entrada una imagen en BITMAP
    // y devuelve como respuesta la misma imagen en BASE 64

    // FUENTE: The polyglot developer
    // * https://www.thepolyglotdeveloper.com/2015/06/from-bitmap-to-base64-and-back-with-native-android/
    private String getImagenBase64(Bitmap foto) {
        // Crear un nuevo 'ByteArrayOutputStream'
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fotoTransformada = stream.toByteArray();
        String fotoen64 = Base64.encodeToString(fotoTransformada, Base64.DEFAULT);
        return fotoen64;
    }

    //---------------------------------------------------------------------------------
    // 8) Método SUBIR_IMAGEN_AWS: El método que contiene la petición HTTP que subirá
    // las imágenes al servidor
    private void subirImagenAWS(String fotoen64 , String nombre) {
        // Mostrar un diálogo de carga para informar al usuario
        // * FUENTE: Android Developers
        // * https://developer.android.com/reference/android/app/ProgressDialog
        final ProgressDialog cargando = ProgressDialog.show(getActivity(), getResources().getString(R.string.t_dialogFotoPerfil), getResources().getString(R.string.m_espera)  );

        // Indicar la URL del recurso en el servidor
        String ruta = URL_server + "subirImagenes.php";
        // Crear StringRequest para la petición (POST)
        StringRequest sr = new StringRequest(Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Cerrar pantalla de carga
                cargando.dismiss();
                // Informar al usuario del estado con un Toast
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.imagenGuardadaOK), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Cerrar pantalla de carga
                cargando.dismiss();
                // Si hay un error con la subida, dejarlo indicado en el log de errores de la aplicación
                Log.e("CAMERA", error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Preparar parámetros de la petición PHP
                Map<String, String> parametros = new HashMap<String, String>();
                // RECURSO: subirImagenes.php>subirFoto
                parametros.put("id_recurso", "subirFoto");
                parametros.put("imagen", fotoen64);
                parametros.put("nombre", nombre);
                // Enviar
                return parametros;
            }
        };
        // Añadir solicitud a la cola (RequestQueue)
        rq.add(sr);
    }

    //---------------------------------------------------------------------------------
    // 9) Método OPEN_CAMERA: Este método lanzará el intent a la aplicación de cámara
    public void openCamera() {
        // Intent a la aplicación de cámara
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(intentCamara);
    }

    //---------------------------------------------------------------------------------
    // 10) Método OPEN_CAMERA: Este método lanzará el intent a la galería de imágenes
    private void elegirFotoDeGaleria() {
        // Intent a la aplicación de galería
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeFromGallery.launch(intentGaleria);
    }

    //----------------------------------------------------------------------------------------------
    // Respuesta a la petición de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 600:
                //Si se han concedido los permisos, mostrar el dialog de selección
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    mostrarCuadroDialogo();
                }else{
                    //Si no, mostrar un toast informativo
                    Toast.makeText(getActivity(), "NO ha concedido permisos", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    //----------------------------------------------------------------------------------
    // 11) Método MOSTRAR_CUADRO_DIALOGO: Con este método se creará un simple diálogo de selección:
    // Permite elegir entre la galería o la cámara de fotos para establecer una foto de perfil.
    private void mostrarCuadroDialogo(){

        // Crear un alert dialog genérico
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Colocar texto en las opciones del diálogo
        String[] pictureDialogItem={"De la galería","Sacar foto con la cámara" };
        // Crear un listener que reaccione a la selección del usuario
        alertDialog.setItems(pictureDialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selec) {
                switch(selec){
                    case 0:
                        // Si ha seleccionado la opción de 'Seleccionar foto de galería'
                        elegirFotoDeGaleria();
                        break;
                    case 1:
                        // Si ha seleccionado la opción de 'Sacar foto desde la cámara'
                        openCamera();
                        break;
                }
            }
        });
        // Mostrar diálogo
        alertDialog.show();
    }

    //---------------------------------------------------------------------------------
    // 13) Método ON_DESTROY_VIEW
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //---------------------------------------------------------------------------------
    // 14)Método ON_SAVE_INSTANCE_STATE: Este método se ejecuta antes del destroy() de la
    //    aplicación, por lo que es aquí dónde se deben guardar los datos que se quieran
    //    mantener. Por ejemplo,para evitar la pérdida de datos al girar el móvil, interrupciones,
    //    etc.
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar datos para evitar la pérdida al girar el móvil, interrupciones, etc.
        outState.putString("var_email", this.email);
        outState.putString("var_usuario", this.usuario);
        outState.putString("var_apellido", this.apellido);
        outState.putString("var_idioma", this.idioma);
        outState.putString("var_email", this.email);

    }


}