<?php
/*                      PHP - 'enviarNotificacion.php':                      *
 * --------------------------------------------------------------------------*
 *  Este PHP es el encargado de recoger los tokens de ciertos usuarios y     *
 *  enviarles un mensaje por FCM                                             */
###############################################################################

    // Incluir conexión con la BD del archivo 'conexion.php'
    include 'conexion.php';

    // Definir la clave FCM generada
    $FCM_APIKEY = 'AAAA0q2yAK4:APA91bF2XU8qokZeeR6LM_z2LTC5TkLRO2zSO2XtkuA-kq2dRSjDiZ60tGYxIWKfuSbqeACN7raZx_0MrUixMkxMvzUGGdLmapE0JTHIUXNgVIvHsbcr2v0G4ehXTeBkcMCUuNr6QljZ';
        
    if ($_SERVER['REQUEST_METHOD'] == 'POST'){

# 1.- Obtener tokens de los destinatarios ------------------------------------------------------->
        // Recoger parámetros
        $mensaje = addslashes($_POST['mensaje']);
        $titulo = addslashes($_POST['titulo']);

        //Array para recoger los tokens a los que enviar la notificación
        $tokens = array();
        
        // Consulta: Conseguir los tokens de los usuarios que tengan registrado un token
        $query=$con->prepare("SELECT DISTINCT U.token FROM Usuario U WHERE U.token IS NOT NULL;");
            
        // Ejecutar consulta
        $query -> execute();

        // Recoger resultado 
        $res = $query->get_result();
         
        // Procesar respuesta
        for ($i = 1; $i <= mysqli_num_rows($res); $i++) {
           $fila = $res->fetch_assoc();
           if ($i != mysqli_num_rows($res) ){  
                          echo "\t";
           }
           // Añadir token al array
           array_push($tokens, $fila['token']);
        }
       
        // Cerrar la conexión con la BD
        $con->close();

# 2.- Preparar mensaje para enviar ----------------------------------------------------------->
       
        // Definir la cabecera del mensaje
        $cabecera = array(
            "Authorization: key=$FCM_APIKEY",
            'Content-Type: application/json'
        );

        // Definir los datos del mensaje
        $msg = array(
            'registration_ids' => $tokens,  // Tokens objetivo
            'notification' => array(        
                'body' => $mensaje,             // Mensaje
                'title' => $titulo,             // Título del mensaje
                'click_action' => "NUEVO"       // Definir que la notificación abra la pantalla de registro (definido en el manifest)
            ) 
        );

        // Parsear el mensaje a JSON
        $msgJSON = json_encode($msg);

        // Imprimir el mensaje en JSON
        print_r($msgJSON);

# 3.- Enviar el mensaje mediante curl ----------------------------------------------------------->
        
        // Inicializar el handler de curl
        $ch = curl_init(); 

        // indicar el destino de la petición, el servicio FCM de google
        curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
       
        // Indicar que la conexión es de tipo POST
        curl_setopt( $ch, CURLOPT_POST, true );

        // Agregar las cabeceras 
        curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
        
        // Indicar que se desea recibir la respuesta a la conexión en forma de string
        curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

        // Agregar los datos de la petición en formato JSON 
        curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON);
        
        // Ejecutar la llamada 
        $resultado = curl_exec( $ch );

        // Imprimir resultado
        if (curl_errno($ch)) { 
            print curl_error($ch); 
        } 
        echo $resultado;

        // Cerrar el handler de curl
        curl_close( $ch );
    }
?>