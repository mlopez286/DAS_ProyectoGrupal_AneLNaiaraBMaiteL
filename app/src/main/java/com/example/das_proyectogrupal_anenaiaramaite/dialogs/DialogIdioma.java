package com.example.das_proyectogrupal_anenaiaramaite.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.controladores.GestorIdiomas;
/*                         DIALOG 'DialogIdioma':                             *
 * ---------------------------------------------------------------------------*
 *  Clase para el Dialog de 3 opciones que se le muestra al usuario para      *
 *  cambiar el idioma de la app.                                              */

//##################################################################################################//

public class DialogIdioma extends DialogFragment {

    GestorIdiomas gestorIdiomas;
    String idioma, email;
// ---------------------------------- CONSTRUCTORES ------------------------------------------------//
    // Constructor vacío
    public DialogIdioma(){ }
    public DialogIdioma(String pIdioma, String pEmail, Context contexto){
        this.idioma = pIdioma;
        this.email = pEmail;
        this.gestorIdiomas =  new GestorIdiomas(contexto);
    }


// ------------------------------------- MÉTODOS ---------------------------------------------------//
// 1)  Método onCreateDialog: Primer método para "construir" el diálogo
    public Dialog onCreateDialog (Bundle savedInstanceState){
        super.onCreateDialog(savedInstanceState);

        // Recuperar los datos
        // Si no es null
        if (savedInstanceState!=null){
            // Recuperar los datos
            idioma = savedInstanceState.getString("var_idioma");
            dismiss();
        }

        // Instanciar el Builder para crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflar el diálogo con el xml 'dialog_1op' (personalizado)
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspecto = inflater.inflate(R.layout.dialog_3op, null);

        // CAMBIAR DE IDIOMA
        Button b_eu = aspecto.findViewById(R.id.b_dialog_3op_eu);
        b_eu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Cambiar idioma y refrescar la actividad
                if (gestorIdiomas != null){

                    // Mostrar TOAST informativo
                    Toast.makeText(getActivity(), "Hizkuntza euskarara aldatuta.", Toast.LENGTH_SHORT).show();

                    gestorIdiomas.setIdioma("eu");

                    // Guardar idioma como es
                    idioma = "eu";

                    // Lanzar un intent a la propia actividad para refrescar el contenido
                    refrescarActivity();
                }




            }
        });

        // CAMBIAR DE IDIOMA
        Button b_es = aspecto.findViewById(R.id.b_dialog_3op_es);
        b_es.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Cambiar idioma y refrescar la actividad
                if (gestorIdiomas != null) {
                    // Mostrar TOAST informativo
                    Toast.makeText(getActivity(), "Idioma cambiado a castellano.", Toast.LENGTH_SHORT).show();

                    // Cambiar idioma y refrescar la actividad
                    gestorIdiomas.setIdioma("es");

                    // Guardar idioma como es
                    idioma = "es";

                    // Lanzar un intent a la propia actividad para refrescar el contenido
                    refrescarActivity();
                }
            }
        });

        // CAMBIAR DE IDIOMA
        Button b_en = aspecto.findViewById(R.id.b_dialog_3op_en);
        b_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Cambiar idioma y refrescar la actividad
                if (gestorIdiomas != null) {
                    // Mostrar TOAST informativo
                    Toast.makeText(getActivity(), "Language changed to english.", Toast.LENGTH_SHORT).show();

                    // Cambiar idioma y refrescar la actividad
                    gestorIdiomas.setIdioma("en");

                    // Guardar idioma como es
                    idioma = "en";

                    // Lanzar un intent a la propia actividad para refrescar el contenido
                    refrescarActivity();
                }
            }
        });

        // Cargar el aspecto
        builder.setView(aspecto);

        // Settear el Dialog como cancelable
        builder.setCancelable(true);
        return builder.create();
    }

    // 7)  Método refrescarActivity: Lanza un intent a la propia actividad para aplicar los cambios
    //                               en el idioma
    public void refrescarActivity(){

        // Refrescar la página
        Intent i = getActivity().getIntent();

        // Enviar datos
        i.putExtra("var_email", email);
        i.putExtra("var_idioma", idioma);

        // Destruir actividad actual
        getActivity().finish();

        // Lanzar activity
        startActivity(i);
    }

}
