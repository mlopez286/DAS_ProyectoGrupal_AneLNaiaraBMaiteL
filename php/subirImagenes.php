<?php
   
#======================================================================#
#                          SUBIR IMÁGENES.php                          #
########################################################################
# Este fichero gestionará la subida de imágenes al servidor AWS de la asignatura.
# De esta forma, cada usuario podrá almacenar una imagen de perfil que se mostrará
# al iniciar sesión en la aplicación.

    # Si el método HTTP es POST, proceder
    if($_SERVER['REQUEST_METHOD']=='POST'){
 
        # Recoger parámetros de la petición: Imagen en 
        # base 64 y nombre con el que  almacenar el fichero
        $base=$_POST['imagen'];
        # Decodificar de base64 a binario
        $binary=base64_decode($base);
        $nombre = $_POST['nombre'];

        # Definir la ruta donde almacenar el recurso
        $ruta="PERFILES/$nombre";

        # Añadir cabecera
        header('Content-Type: bitmap; charset=utf-8');
    
        if (!empty($base)){
            # Subir imagen (binario) a la ruta indicada del servidor
            if (file_put_contents($ruta, $binary)){
                # Ha ido bien
                echo "La imagen se ha subido";
            }
            else{
                # Ha ocurrido un error
                echo "Ha ocurrido un error";
            }
        }

    }
?>