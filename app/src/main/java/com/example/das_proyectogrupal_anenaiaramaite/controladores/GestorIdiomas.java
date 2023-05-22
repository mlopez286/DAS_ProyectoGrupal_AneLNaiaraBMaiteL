package com.example.das_proyectogrupal_anenaiaramaite.controladores;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/*                          CLASE 'GestorIdiomas':                              *
 * -----------------------------------------------------------------------------*
 *  Clase encargada de cambiar el idioma de la aplicación. Guarda como atributo *
 *  el idioma actual ( es - por defecto )                                       */

//##################################################################################################//

public class GestorIdiomas {
    // Atributo
    private Context contexto;

    // Variable global
    String idiomaActual="es";

// ---------------------------------- CONSTRUCTOR --------------------------------------------------//
    // Constructor
    public GestorIdiomas(Context contexto) {
        this.contexto = contexto;
    }

// --------------------------------- MÉTODOS -------------------------------------------------------//
    // 1)  Método setIdioma: Settea el idioma que recibe como parámetro en la aplicación
    public void setIdioma(String idioma){

        //Configurar el idioma
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Configuration configuration = contexto.getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = contexto.createConfigurationContext(configuration);
        contexto.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        // Settear el idioma actual como el idioma recién setteado
        idiomaActual = idioma;
    }
}
