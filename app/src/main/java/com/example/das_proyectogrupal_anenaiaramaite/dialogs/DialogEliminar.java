package com.example.das_proyectogrupal_anenaiaramaite.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.das_proyectogrupal_anenaiaramaite.Pregunta;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.interfaces.InterfazPreguntasAux;

/*                         DIALOG 'DialogEliminar':                             *
 * -----------------------------------------------------------------------------*
 *  Clase para el Dialog de 2 opciones que se le muestra al usuario al mantener *
 *  pulsado sobre una portada, y que le pregunta si desea borrar la canción.    */

//##################################################################################################//

public class DialogEliminar extends DialogFragment  {
   // Atributos
    private Pregunta p;
    private int pos;

    // Interfaz auxiliar para ejecutar métodos implementados en otra clase
    private InterfazPreguntasAux listener;

// ---------------------------------- CONSTRUCTORES ------------------------------------------------//
    // Constructor
    public DialogEliminar(Pregunta pregunta, int pos, InterfazPreguntasAux listener){
        this.p = pregunta;
        this.pos = pos;
        this.listener = listener;
    }
    // Constructor vacío
    public DialogEliminar() { listener = null;   }

// ------------------------------------- MÉTODOS ---------------------------------------------------//
    // 1)  Método onCreateDialog: Primer método para "construir" el diálogo
    public Dialog onCreateDialog (@Nullable Bundle savedInstanceState){
        // Recuperar los datos tras el giro de la pantalla
        if (savedInstanceState != null){
            dismiss();
        }

        super.onCreateDialog(savedInstanceState);



        // Si la canción seleccionado NO es null
        if ( p!= null ) {
            // Instanciar el Builder para crear el diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Inflar el diálogo con el xml 'dialog_2op' (personalizado)
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View aspecto = inflater.inflate(R.layout.dialog_2op, null);

            // Settear título del dialog
            TextView titulo = aspecto.findViewById(R.id.t_dialog_2op);
            titulo.setText(R.string.t_dialogEliminar);

            // Settear mensaje del dialog
            TextView mensaje = aspecto.findViewById(R.id.m_dialog_2op);
            mensaje.setText(R.string.m_dialogEliminar);

            // Añadir Listener al botón para que al seleccionar OK se elimine la canción
            Button b_ok = aspecto.findViewById(R.id.b_dialog_2op_ok);
            b_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        // Método implementado en FragmentCanciones para eliminar la canción
                        // seleccionada
                        listener.eliminarOK(pos);
                        dismiss();
                    }
                }
            });

            // Añadir Listener al botón para que al darle a VOLVER desaparezca el dialog
            Button b_atras = aspecto.findViewById(R.id.b_dialog_2op_atras);
            b_atras.setOnClickListener(new View.OnClickListener() {
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
        else{
            // Si no se ha seleccionado ninguna canción ( o se ha girado la pantalla)
            // Instanciar el Builder para crear el diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Inflar el diálogo con el xml 'dialog_2op' (personalizado)
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View aspecto = inflater.inflate(R.layout.dialog_2op, null);

            // Settear título del dialog
            TextView titulo = aspecto.findViewById(R.id.t_dialog_2op);
            titulo.setText(R.string.t_dialogEliminar);

            // Settear mensaje del dialog
            TextView mensaje = aspecto.findViewById(R.id.m_dialog_2op);
            mensaje.setText(R.string.m_dialogEliminar);

            // Hacer que desaparezca el dialog ( la canción es null y daría null pointer)
            dismiss();

            // Cargar el aspecto
            builder.setView(aspecto);
            // Settear el Dialog como cancelable
            builder.setCancelable(true);

            return builder.create();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}