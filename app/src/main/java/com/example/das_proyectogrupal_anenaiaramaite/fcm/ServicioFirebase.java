
// _____________________________________ UBICACIÓN DEL PAQUETE _____________________________________
package com.example.das_proyectogrupal_anenaiaramaite.fcm;

// ______________________________________ PAQUETES IMPORTADOS ______________________________________

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.das_proyectogrupal_anenaiaramaite.R;
import com.example.das_proyectogrupal_anenaiaramaite.actividades.Act_Login;
import com.example.das_proyectogrupal_anenaiaramaite.actividades.Act_Registro;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import static android.content.ContentValues.TAG;
import java.util.Map;

/* ################################### CLASE SERVICIO FIREBASE #####################################
    *) Descripción:
        La función de esta clase es gestionar el servicio de mensajería FCM con Firebase.

    *) Tipo: FirebaseMessagingService
*/
public class ServicioFirebase extends FirebaseMessagingService {

// ____________________________________________ Métodos ____________________________________________

/*  Método onMessageReceived:
    -------------------------
        *) Parámetros (Input):
                1) (RemoteMessage) message: Contiene mensaje que ha recibido.
        *) Parámetro (Output):
                void
        *) Descripción:
                Este metodo se ejecuta al recibir un mensaje de Firebase. Si el mensaje no está
                vacío crea una notificación para informar al usuario.
*/
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        // Comprobar que el mensaje recibido no esté vacío
        if(message.getData() != null){
            enviarNotificacion(message);
        }

        // Para que las notificaciones aparezcan en 1er plano, hay que tratar los datos
        // Si los datos recibidos no son nulos
        if(message.getNotification() != null){ // 2do plano
            enviarNotificacion(message);
        }


    }

// _________________________________________________________________________________________________

/*  Método enviarNotificacion:
    --------------------------
        *) Parámetros (Input):
                1) (RemoteMessage) remoteMessage: Contiene mensaje que ha recibido.
        *) Parámetro (Output):
                void
        *) Descripción:
                Crea la notificación que muestra al usuario el mensaje recibido por FCM.
*/
    private void enviarNotificacion(RemoteMessage remoteMessage){

        // Comprobar permisos de notificación
        // Si no los tiene, sale del método
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Obtener el título y el contenido del mensaje recibido
        Map<String,String> data = remoteMessage.getData();

        // Recuperar título del mensaje
        String title = remoteMessage.getNotification().getTitle();

        // Recuperar texto del mensaje
        String body = remoteMessage.getNotification().getBody();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "fcm-message";

        // Comprobar que el dispositivo tenga una versión igual o superior a la Oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Crear el canal de notificación
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Mi notificacion", NotificationManager.IMPORTANCE_MAX);

            // Configuración del canal de notificación
            channel.setDescription("fcm-message channel para app");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long[]{0,1000,500,1000});
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }
        // Crear el intent que se ejecutará si el usuario pulsa la notificación
        Intent i = new Intent (this, Act_Login.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificacón
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.mail)
                .setColor(getResources().getColor(R.color.g_azul))
                .setTicker("FCM")
                .setContentTitle(title)
                .setContentText(body)
                .setVibrate(new long[]{0,1000,500,1000})
                .setContentIntent(pendingIntent)
                .setContentInfo("info");

        // Mandar la notificación
        manager.notify(1,builder.build());
    }

// _________________________________________________________________________________________________

/*  Método onNewToken:
    ------------------
        *) Parámetros (Input):
                1) (String) token: Contiene el token del dispositivo.
        *) Parámetro (Output):
                void
        *) Descripción:
                Este método se ejecuta cuando se detecta un nuevo token. Al recibir el nuevo token
                lo subscribe en el tópico llamado dispositivos.
*/
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TOKEN","ENTRA");
        Log.d(TAG,"Refreshed Token " + token);
        // Subscribir el token actual al tópico "dispositivos"
        FirebaseMessaging.getInstance().subscribeToTopic("dispositivos");
        Log.d("TOKEN","El token actual es" + token);
    }
}