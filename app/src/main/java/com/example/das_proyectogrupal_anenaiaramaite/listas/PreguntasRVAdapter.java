package com.example.das_proyectogrupal_anenaiaramaite.listas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyectogrupal_anenaiaramaite.Pregunta;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.interfaces.InterfazPreguntasAux;
import com.example.das_proyectogrupal_anenaiaramaite.ui.preguntas.PreguntasFragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PreguntasRVAdapter extends RecyclerView.Adapter<PreguntasRVAdapter.PreguntasViewHolder> {

    ArrayList<Pregunta> preguntas;
    InterfazPreguntasAux listener;
    PreguntasFragment contexto;

    private List<Pregunta> lista_preguntas_original;     // Lista con las preguntas de la aplicación
    private String URL_SERVER = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mlopez286/WEB/proyectoGrupal/";

    public PreguntasRVAdapter (ArrayList<Pregunta> lista, InterfazPreguntasAux pListener, PreguntasFragment context){
        contexto = context;
        preguntas = lista;
        listener = pListener;

        //Lista auxiliar para guardar la lista completa
        lista_preguntas_original = new ArrayList<>();
        lista_preguntas_original.addAll(lista);
    }

    // Método para actualizar la lista filtrada y la original
    public void actualizarOriginal(Pregunta p){
        lista_preguntas_original.remove(p);
    }

    public void filtrado(String txtBuscar){

        // Obtener el tamaño del texto a filtrar
        int l = txtBuscar.length();

        // Comprobar si se ha escrito algo
        if(l == 0){     // Si está vacío: Recuperar lista original
            this.preguntas.clear();
            preguntas.addAll(lista_preguntas_original);
        } else{         // Si no está vacío: Filtrar la lista
            // Comprobar si la version del dispositivo soporta la operación de filtrado Nougat
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                preguntas.clear();
                // Filtrar la lista
                List<Pregunta> collection = lista_preguntas_original.stream().filter(i -> i.getTitulo().toLowerCase().contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                preguntas.addAll(collection);
            }
            else {      // Si no la soporta, realizar el filtrado de otra manera
                preguntas.clear();
                for(Pregunta prod: lista_preguntas_original){
                    if(prod.getTitulo().toLowerCase().contains(txtBuscar.toLowerCase())){
                        preguntas.add(prod);
                    }
                }
            }
        }
        // Indicar que se han realizado cambios en la lista
        notifyDataSetChanged();
    }

    // Clase interna: ViewHolder
    public static class PreguntasViewHolder extends RecyclerView.ViewHolder{
        TextView titulo, autor, enunciado;
        ImageView icono;
        CardView cv;
        RecyclerView rv;

    // ---------------------------------- VIEWHOLDER --------------------------------------------------
        // Constructor de la clase
        public PreguntasViewHolder(@NonNull View itemView, InterfazPreguntasAux listener) {
            super(itemView);

            // Recoger elementos de la lista
            rv = itemView.findViewById(R.id.rv_preguntas);
            cv = itemView.findViewById(R.id.cv_pregunta);
            titulo = itemView.findViewById(R.id.tv_titulo);
            autor = itemView.findViewById(R.id.tv_autor);
            enunciado = itemView.findViewById(R.id.tv_enunciado);
            icono = itemView.findViewById(R.id.iv_pregunta);

            // Añadir Listener al CLICAR sobre el elemento: Muestra la información de la pregunta
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    // Comprobar que no sea null
                    if (listener != null) {

                        // Coger la posición clickada
                        int pos = getAdapterPosition();

                        // Comprobar que la posición sea válida
                        if (pos != rv.NO_POSITION) {
                            // Cargar animación de zoom
                            itemView.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.ampliar));
                            // Ejecutar el método para visualizar la información (implementando a través de una interfaz)
                            listener.onItemClick(pos);

                        }
                    }
                }
            });

            // Añadir Listener al MANTENER PULSADO sobre el elemento: Pregunta al usuario si quiere borrar la canción con 2 opciones:
            //   * SÍ: Elimina la canción de la lista + BD
            //   * NO: Cierra el Dialog
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // Comprobar que no sea null
                    if (listener != null) {
                        // Coger la posición clickada
                        int pos = getAdapterPosition();
                        // Comprobar que la posición sea válida
                        if (pos != rv.NO_POSITION) {
                            itemView.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.sacudir));
                            // Ejecutar el método para preguntar al usuario si desea borrar la pregunta (implementando a través de una interfaz)
                            listener.onItemLongClick(pos);
                        }
                    }
                    return true;
                }
            });
        }
    }

    //-----------------------------------------------------------------------------------------

    // Encargado de inflar el contenido de un nuevo item para la lista
    @Override
    public PreguntasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null);
        PreguntasViewHolder viewHolder = new PreguntasViewHolder(view, listener);
        return viewHolder;
    }

    // Realiza los cambios sobre cada item
    @Override
    public void onBindViewHolder(@NonNull PreguntasViewHolder holder, int position) {

        holder.titulo.setText(preguntas.get(position).getTitulo());
        holder.autor.setText( contexto.getResources().getString(R.string.preguntaDe) +" "+  preguntas.get(position).getEmailAutor());

        if (preguntas.get(position).getEnunciado().length()<=0){
            holder.enunciado.setVisibility(View.GONE);
        }
        else{
            holder.enunciado.setText(preguntas.get(position).getEnunciado());
        }

        /** Extraído de StackOverflow
         Pregunta: https://stackoverflow.com/questions/25093546/android-os-networkonmainthreadexception-at-android-os-strictmodeandroidblockgua
         Autor: https://stackoverflow.com/users/1776216/apriya
         **/

        // Líneas necesarias para realizar una petición HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Cargar fotos de perfil de los usuarios
        // .......~~~~****###### CARGAR IMAGEN DE PERFIL #######******~~~~~~......... //

        String pAutor = preguntas.get(position).getEmailAutor();

        // Preparar URL (genérica) al recurso, generada a partir del email del usuario
        String direccion = URL_SERVER + "PERFILES/" + "profile_"+pAutor.replace("@", "").replace(".", "")+".jpeg";
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
                    // Convertir imagen a Bitmap
                    Bitmap bitmapPerfil = BitmapFactory.decodeStream(conn.getInputStream());
                    //Colocar la imagen
                    holder.icono.setImageBitmap(Bitmap.createScaledBitmap(bitmapPerfil, 500, 500, false));
                }
                else{
                    if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {

                        // Elegir imagen aleatoria para los usuarios sin foto de perfil
                        // Settear una portada random entre las portadas de drawable
                        int[] portadas = {R.drawable.question_1, R.drawable.question_2, R.drawable.question_3, R.drawable.question_4, R.drawable.question_5, R.drawable.question_6, R.drawable.question_7};

                        // Generar un número aleatorio [0 - portadas.length()]
                        Random r = new Random();
                        int n = r.nextInt(portadas.length);

                        //Colocar la imagen
                        holder.icono.setImageDrawable(contexto.getResources().getDrawable(portadas[n]));
                    }
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

    }

    @Override
    public int getItemCount() {
        return preguntas.size();
    }


}
