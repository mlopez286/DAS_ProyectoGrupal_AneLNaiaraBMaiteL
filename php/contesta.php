<?php

    // Incluir conexión con la BD del archivo 'conexion.php'
    include 'conexion.php';

    // Recoger parámetros
    $id_recurso = $_POST['id_recurso']; 

    // En función de la consulta, ejecutar un comando u otro:
    switch ($id_recurso) {
        #-----------------------------------------------------------------------------------#
        case "comprobarVoto":
            if ($_SERVER['REQUEST_METHOD'] == 'POST'){
                
                // Recoger las variables
                $titulo = addslashes($_POST['titulo']);
                $email_votante = addslashes($_POST['email_votante']);
                $email_autor = addslashes($_POST['email_autor']);
                
                # Preparar consulta a ejecutar
                $query=$con->prepare("SELECT respuesta from Contesta WHERE email_usuario='$email_votante' and titulo='$titulo' and email_autor='$email_autor'");
                # Ejecutar consulta contra la DB
                $query->execute();
                # Obtener resultado
                $resultado = $query->get_result();

                # Si ha habido resultado (Ya ha votado en esta pregunta)
                if ($fila = $resultado->fetch_assoc()) {
                    # Devolver resultado 
                    echo $fila['respuesta'];  
                }
                
            }else{
                # Error actualizando los datos del usuario
                echo '{"code":"500"}';
            }
        break;
        case "getEstadisticas":
            if ($_SERVER['REQUEST_METHOD'] == 'POST'){
                
                $email= addslashes($_POST['email']);
                  
                    // Obtener número de votos de esa persona
                    $consulta="SELECT COUNT(*) FROM Contesta WHERE email_usuario='$email';";
                    $result = mysqli_query($con, $consulta);
                    if($result){
                        #Acceder al resultado
                        $votos = mysqli_fetch_row($result)[0];
                        
                        // Obtener numero de publicaciones de esa persona
                        $consulta="SELECT COUNT(*) FROM Pregunta WHERE email_autor='$email';";
                        // Ejecutar consulta y recoger resultado 
                        $result = mysqli_query($con, $consulta);
                        
                        if($result){
                            $publicaciones = mysqli_fetch_row($result)[0];
                           
                            // Obtener numero de publicaciones de esa persona
                            $consulta="SELECT fecha FROM Usuario WHERE email='$email';";
                            // Ejecutar consulta y recoger resultado 
                            $result = mysqli_query($con, $consulta);
                            if($result){
                                $fecha=mysqli_fetch_row($result)[0];
                            
                                echo '{"votos":"';
                                echo $votos;
                                echo '","publicaciones":"';
                                echo $publicaciones;
                                echo '","fecha":"';
                                echo $fecha;
                                echo '"}';  
                            }
                        }
                }
                
            }

        break;
    }
?>