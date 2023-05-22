package com.example.das_proyectogrupal_anenaiaramaite.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.das_proyectogrupal_anenaiaramaite.R;
/*                         DIALOG 'DialogExiste':                             *
 * ---------------------------------------------------------------------------*
 *  Clase para el Dialog de 1 opción que se le muestra al usuario al intentar *
 *  añadir una canción que ya existe en la BD.                                */

//##################################################################################################//

public class DialogCampoVacio extends DialogFragment {
    private String titulo;
    private String mensaje;

// ---------------------------------- CONSTRUCTORES ------------------------------------------------//
    // Constructor vacío
    public DialogCampoVacio(){ }

    public DialogCampoVacio(String t, String m){
        this.titulo = t;
        this.mensaje = m;
    }

// ------------------------------------- MÉTODOS ---------------------------------------------------//
// 1)  Método onCreateDialog: Primer método para "construir" el diálogo
    public Dialog onCreateDialog (Bundle savedInstanceState){
        // Recuperar los datos tras el giro de la pantalla
        if (savedInstanceState != null){
            this.titulo = savedInstanceState.getString("var_titulo");
            this.mensaje = savedInstanceState.getString("var_mensaje");
        }

        super.onCreateDialog(savedInstanceState);
        // Instanciar el Builder para crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflar el diálogo con el xml 'dialog_1op' (personalizado)
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspecto = inflater.inflate(R.layout.dialog_1op, null);

        // Settear título del dialog
        TextView titulo = aspecto.findViewById(R.id.t_dialog_1op);
        titulo.setText(this.titulo);

        // Settear mensaje del dialog
        TextView mensaje = aspecto.findViewById(R.id.m_dialog_1op);
        mensaje.setText(this.mensaje);

        // Añadir Listener al botón para que al clickar desaparezca el dialog
        Button b_aceptar = aspecto.findViewById(R.id.b_dialog_1op);
        b_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Cargar el aspecto
        builder.setView(aspecto);

        // Settear el Dialog como cancelable
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar datos para evitar la pérdida al girar el móvil, interrupciones, etc.
        outState.putString("var_titulo", this.titulo);
        outState.putString("var_mensaje", this.mensaje);
    }
}
