package com.example.das_proyectogrupal_anenaiaramaite.ui.preguntas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.das_proyectogrupal_anenaiaramaite.Pregunta;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.actividades.Act_AnadirPregunta;
import com.example.das_proyectogrupal_anenaiaramaite.actividades.Act_Encuesta;
import com.example.das_proyectogrupal_anenaiaramaite.controladores.GestorIdiomas;
import com.example.das_proyectogrupal_anenaiaramaite.dialogs.DialogEliminar;
import com.example.das_proyectogrupal_anenaiaramaite.interfaces.InterfazPreguntasAux;
import com.example.das_proyectogrupal_anenaiaramaite.listas.PreguntasRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreguntasFragment extends Fragment implements InterfazPreguntasAux, SearchView.OnQueryTextListener{

    private ArrayList<Pregunta> l_preguntas = new ArrayList<Pregunta>();
    private PreguntasRVAdapter adaptador;
    private RecyclerView rv_preguntas;
    // Definir variable contexto para usarlo más adelante
    PreguntasFragment contexto = this;
    RequestQueue rq;
    String idioma;
    private int posicion=0;

    private String usuario, apellido, email;

    private SearchView txtBuscar;   // SearchView para filtrar la lista de encuestas


    // URL del servidor AWS de DAS (22-23)
    private String URL_SERVER = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //------------------------------------------------------------------------------------------

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
            idioma = savedInstanceState.getString("var_idioma");
            posicion = savedInstanceState.getInt("var_posicion", posicion);

        }
        // Obtener intent lanzado desde el Login hasta la
        // Act_MenuPrincipal.
        Bundle extras = getActivity().getIntent().getExtras();
        // Si tiene datos, recogerlos
        if (extras !=null) {
            email = extras.getString("var_email");
            usuario = extras.getString("var_usuario");
            apellido = extras.getString("var_apellido");
            idioma = extras.getString("var_idioma");
        }


        // Settear idioma
        if (idioma != null){
            GestorIdiomas gestorIdiomas = new GestorIdiomas(this.getActivity());
            gestorIdiomas.setIdioma(idioma);
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Obtener la vista
        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        // Recoger el RV de las canciones (global)
        rv_preguntas = (RecyclerView) view.findViewById(R.id.rv_preguntas);

        // Establecer GridLayout con 1 fila
        GridLayoutManager rejilla = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        rv_preguntas.setLayoutManager(rejilla);

        cargarPreguntas();

        // Obtener el searchView y añadir un Listener
        txtBuscar = view.findViewById(R.id.search_view);
        txtBuscar.setOnQueryTextListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = view.findViewById(R.id.fab_anadirPregunta);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Act_AnadirPregunta.class);
                i.putExtra("var_email", email);
                i.putExtra("var_apellido", apellido);
                i.putExtra("var_usuario", usuario);
                i.putExtra("var_idioma", idioma);

                // Espera respuesta de este intent para actualizar los datos de la lista
                startActivityIntent.launch(i);

            }
        });
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            // Si se ha obtenido resultados no nulos y el código de respuesta es 0
            if (result.getData() != null && result.getResultCode() == 0) {

                // Recoge el los datos enviados de vuelta
                String titulo = result.getData().getStringExtra("var_titulo");
                boolean cerrada = false;
                String enunciado = result.getData().getStringExtra("var_enunciado");
                String emailAutor = result.getData().getStringExtra("var_emailautor");
                int cont1 = 0;
                int cont2 = 0;
                int cont3 = 0;
                int cont4 = 0;

                String op1 = result.getData().getStringExtra("var_op1");
                String op2 = result.getData().getStringExtra("var_op2");
                String op3 = result.getData().getStringExtra("var_op3");
                String op4 = result.getData().getStringExtra("var_op4");

                añadirPregunta(titulo, cerrada, enunciado, emailAutor, cont1, cont2, cont3, cont4, op1, op2, op3, op4);
            }
        }
    });

    private void añadirPregunta(String titulo, boolean cerrada, String enunciado, String emailAutor, int cont1, int cont2, int cont3, int cont4, String op1, String op2, String op3, String op4) {

        Pregunta p = new Pregunta(titulo, cerrada, enunciado, emailAutor, cont1, cont2, cont3, cont4, op1, op2, op3, op4);
        l_preguntas.add(p);
        adaptador.notifyItemInserted(l_preguntas.size()-1);

    }

    private void cargarPreguntas() {
        ArrayList<Pregunta> aux = new ArrayList<>();

        // Mostrar una pantalla de carga mientras dure la operación
        ProgressDialog cargando = ProgressDialog.show(this.getActivity(), getResources().getString(R.string.cargaPreguntas), getResources().getString(R.string.d_cargaPreguntas));

        ///// --- VOLLEY
        rq = Volley.newRequestQueue(getActivity());
        String ruta = URL_SERVER + "preguntas.php";

        StringRequest sr = new StringRequest(
                Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Crear arryaList auxiliar para guardar las preguntas
                ArrayList<Pregunta> l_todas = new ArrayList<Pregunta>();

                // Si la respuesta no está vacía
                if (!response.isEmpty()) {
                    // Declarar el objeto JSON
                    JSONObject json;
                    try {

                        // Separar los elementos con el carácter /t
                        String[] aux = response.split("\t");

                        // Por cada pregunta
                        for (String elem : aux) {

                            // Parsear a JSON la canción
                            try {
                                json = new JSONObject(elem);

                                // Recoger los datos de la pregunta
                                //int id = (int) json.get("id");
                                String titulo = (String) json.get("titulo");
                                int cerrada = (int) json.get("cerrada");
                                String enunciado = (String) json.get("enunciado");
                                String email_autor = (String) json.get("email_autor");
                                int cont1 = (int) json.get("cont1");
                                int cont2 = (int) json.get("cont2");
                                int cont3 = (int) json.get("cont3");
                                int cont4 = (int) json.get("cont4");
                                String op1 = (String) json.get("op1");
                                String op2 = (String) json.get("op2");
                                String op3 = (String) json.get("op3");
                                String op4 = (String) json.get("op4");

                                boolean aux_cerrada = false;
                                if (cerrada == 0) aux_cerrada = false;
                                else  aux_cerrada = true;

                                // Instanciar la pregunta con los datos recogidos
                                Pregunta p = new Pregunta( titulo,  aux_cerrada,  enunciado,  email_autor,
                                        cont1,  cont2,  cont3,  cont4,
                                        op1,  op2,  op3,  op4);

                                // Añadir pregunta a la lista
                                l_todas.add(p);

                            }
                            catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        l_preguntas = l_todas;

                        adaptador = new PreguntasRVAdapter(l_preguntas, (InterfazPreguntasAux) contexto, contexto);
                        rv_preguntas.setAdapter(adaptador);

                        // Quitar pantalla de carga
                        cargando.dismiss();

                    }
                    catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DB_REMOTA", "Error al obtener preguntas de la DB");
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_recurso", "getPreguntas");
                return parametros;
            }
        };
        // Añadir solicitud a la cola (RequestQueue)
        rq.add(sr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemClick(int pos) {
        // Obtener resultado actual de la encuesta desde la base de datos => Resultados a "TIEMPO REAL"
        String ruta = URL_SERVER + "preguntas.php";
        StringRequest sr = new StringRequest(
                Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Si la respuesta no está vacía
                if (!response.isEmpty()) {
                    // Declarar el objeto JSON donde recoger la respuesta
                    JSONObject json;
                    // Separar los elementos con el carácter /t
                    String[] aux = response.split("\t");

                    // Parsear a JSON la pregunta
                    try {
                        json = new JSONObject(aux[0]);

                        // Recoger informacióna actualizada sobre los votos de la DB
                        int cerrada = (int) json.get("cerrada");
                        int cont1 = (int) json.get("cont1");
                        int cont2 = (int) json.get("cont2");
                        int cont3 = (int) json.get("cont3");
                        int cont4 = (int) json.get("cont4");

                        boolean aux_cerrada = false;
                        if (cerrada == 0) aux_cerrada = false;
                        else aux_cerrada = true;

                        Pregunta p = l_preguntas.get(pos);

                        //Actualizar los datos sobre la pregunta de la lista
                        p.setCont1(cont1);
                        p.setCont2(cont2);
                        p.setCont3(cont3);
                        p.setCont4(cont4);

                        String ruta = URL_SERVER + "contesta.php";
                        StringRequest sr = new StringRequest(
                                Request.Method.POST, ruta, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String respuesta = "";
                                boolean val = false;
                                // Si hay alguna repuesta registrada
                                if (response.length() > 0) {
                                    val = true;
                                    respuesta = response;
                                } else {
                                    val = false;
                                }

                                // Pasar los datos de la pregunta al Activity 'Act_Encuesta', sabiendo que
                                // el usuario SÍ HA VOTADO
                                Intent i = new Intent(getActivity(), Act_Encuesta.class);

                                i.putExtra("var_usuario", usuario);
                                i.putExtra("var_apellido", apellido);
                                i.putExtra("var_email", email);
                                i.putExtra("var_titulo", p.getTitulo());
                                i.putExtra("var_cerrada", p.isCerrada());
                                i.putExtra("var_enunciado", p.getEnunciado());
                                i.putExtra("var_emailautor", p.getEmailAutor());
                                i.putExtra("var_cont1", p.getCont1());
                                i.putExtra("var_cont2", p.getCont2());
                                i.putExtra("var_cont3", p.getCont3());
                                i.putExtra("var_cont4", p.getCont4());
                                i.putExtra("var_op1", p.getOp1());
                                i.putExtra("var_op2", p.getOp2());
                                i.putExtra("var_op3", p.getOp3());
                                i.putExtra("var_op4", p.getOp4());
                                i.putExtra("var_haVotado", val);
                                i.putExtra("var_respRegistrada", respuesta);
                                i.putExtra("var_idioma", idioma);

                                // Espera respuesta de este intent para actualizar los datos de la lista
                                startActivityIntent_actualizar.launch(i);

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
                                parametros.put("id_recurso", "comprobarVoto");
                                parametros.put("email_votante", email);
                                parametros.put("email_autor", p.getEmailAutor());
                                parametros.put("titulo", p.getTitulo());
                                return parametros;
                            }
                        };
                        // Añadir solicitud a la cola (RequestQueue)
                        rq.add(sr);

                    } catch (Exception e) {
                        Log.e("DB_REMOTA", "Ha ocurrido un error abriendo la encuesta");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DB_REMOTA", error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_recurso", "getPregunta");
                parametros.put("titulo", l_preguntas.get(pos).getTitulo());
                parametros.put("email", l_preguntas.get(pos).getEmailAutor());
                return parametros;
            }
        };
        // Añadir solicitud a la cola (RequestQueue)
        rq.add(sr);
    }

    ActivityResultLauncher<Intent> startActivityIntent_actualizar = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            // Si se ha obtenido resultados no nulos y el código de respuesta es 0
            if (result.getData() != null && result.getResultCode() == 1) {

                // Recoge el los datos enviados de vuelta
                String titulo = result.getData().getStringExtra("var_titulo");
                String email_autor = result.getData().getStringExtra("var_emailautor");
                int c1 = result.getData().getIntExtra("var_count1", 0);
                int c2 = result.getData().getIntExtra("var_count2", 0);
                int c3 = result.getData().getIntExtra("var_count3", 0);
                int c4 = result.getData().getIntExtra("var_count4", 0);

                Log.e("NULL", "Valores RECIBIDOS:");
                Log.e("NULL", "--------------------");
                Log.e("NULL", "counts --> "+c1+", "+c2+", "+c3+", "+c4);
                Log.e("NULL", "email y titulo --> "+email_autor+" - "+titulo);
                Log.e("NULL", "--");
                Log.e("NULL", "--");

                Log.d("POS", "POS--> "+posicion);

                // Obtener la pregunta no actualizada de la lista
                Pregunta p = l_preguntas.get(posicion);



                // Actualizar valores
                p.setCont1(c1);
                p.setCont2(c2);
                p.setCont3(c3);
                p.setCont4(c4);
                // Notificar al adaptador

                adaptador.notifyItemChanged(posicion,p);

                adaptador.notifyDataSetChanged();

            }
        }
    });


    @Override
    public void onItemLongClick(int pos) {
        // Comprobar si el usuario logueado es el autor de la pregunta
        Pregunta p = l_preguntas.get(pos);
        if (p.getEmailAutor().equals(email)){
            // Instanciar DialogEliminar
            DialogEliminar dialogEliminar = new DialogEliminar(l_preguntas.get(pos), pos, this);
            dialogEliminar.show(getActivity().getSupportFragmentManager(), "dialogEliminar");
        }
        else{
            Toast.makeText(getContext(), R.string.no_es_autor, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void eliminarOK(int pos) {
        Pregunta p = l_preguntas.get(pos);

        String ruta = URL_SERVER + "preguntas.php";
        StringRequest sr = new StringRequest(
        Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("200")){
                    // Quitar de la lista
                    Pregunta p = eliminarActualizar(pos);
                    adaptador.actualizarOriginal(p);
                    adaptador.notifyDataSetChanged();
                    Toast.makeText(getContext(), R.string.p_borrada, Toast.LENGTH_SHORT).show();
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
                parametros.put("id_recurso", "eliminarPregunta");
                parametros.put("email_autor", email);
                parametros.put("titulo", p.getTitulo());
                return parametros;
            }
        };
        // Añadir solicitud a la cola (RequestQueue)
        rq.add(sr);

    }

    private Pregunta eliminarActualizar(int pos) {
        // Quitar la pregunta a la lista del adaptador
        Pregunta p = l_preguntas.remove(pos);

        // Notificar al adaptador que se ha quitado la pregunta en la posición 'pos'
        adaptador.notifyItemRemoved(pos);
       return p;
    }

    //---------------------------------------------------------------------------------
    // 13) Método ON_SAVE_INSTANCE_STATE: Este método se ejecuta antes del destroy() de la
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
        // Guardar el idioma
        outState.putString("var_idioma",  idioma);
        outState.putInt("var_posicion", posicion);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Filtrar el texto en la lista
        if (adaptador!= null){
            adaptador.filtrado(newText);
        }
        return false;
    }


}