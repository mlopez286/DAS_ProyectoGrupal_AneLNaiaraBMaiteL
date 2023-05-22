<?php
/*                            PHP - 'preguntas.php':                         *
 * --------------------------------------------------------------------------*
 *  Este PHP es el encargado de realizar consultas contra la base de datos,  *
 *  concretamente sobre la tabla de Pregunta.                                */
###############################################################################

    // Incluir conexión con la BD del archivo 'conexion.php'
    include 'conexion.php';

    // Recoger parámetros
    $id_recurso = $_POST['id_recurso']; 

    // En función de la consulta, ejecutar un comando u otro:
    switch ($id_recurso) {
        #-----------------------------------------------------------------------------------#
        // 1)   getPreguntas: Método para conseguir lista de preguntas (TODAS)
        case "getPreguntas":
          if ($_SERVER['REQUEST_METHOD'] == 'POST'){
                // Consulta
                $query=$con->prepare("SELECT * FROM Pregunta;");
                
                // Ejecutar consulta
                $query -> execute();
 
                // Recoger resultado 
                $res = $query->get_result();
              
                // Preparar la respuesta
                for ($i = 1; $i <= mysqli_num_rows($res); $i++) {
                    $fila = $res->fetch_assoc();
                    echo json_encode($fila,JSON_UNESCAPED_UNICODE); 
                    if ($i != mysqli_num_rows($res) ){  
                                   echo "\t";
                    }
               }
            }
        break;
        #-----------------------------------------------------------------------------------#
        case "anadirPregunta":
            if ($_SERVER['REQUEST_METHOD'] == 'POST'){
                
                // Recoger las variables
                $titulo = addslashes($_POST['titulo']);
                $enunciado = addslashes($_POST['enunciado']);
                $email = addslashes($_POST['email']);
                $op1 = trim(addslashes($_POST['op1']));
                $op2 = trim(addslashes($_POST['op2']));
                $op3 = trim(addslashes($_POST['op3']));
                $op4 = trim(addslashes($_POST['op4']));

                
                if (strlen(trim($op3)) == 0){$op3=NULL;}
                if (strlen(trim($op4)) == 0){$op4=NULL;}

                echo "OP1 --> $op1  -";
                echo "OP2 --> $op2  -";
                echo "OP3 --> $op3  -";
                echo "OP4 --> $op4  -";

                 // AÑADIRLA EN Pregunta ---------------------------------------------------------> 
                    // Consulta
                
                 $query = "INSERT INTO Pregunta (titulo, cerrada, enunciado, email_autor, cont1, cont2, cont3, cont4, op1, op2, op3, op4) VALUES ('$titulo', 0, '$enunciado', '$email', 0, 0, 0, 0, '$op1', '$op2', '$op3', '$op4' )";
                    // Ejecutar la consulta
                 $res = $con->query($query);

                // Si ha ido bien
                 if ($res === TRUE){
                    echo '{"code":"200"}';    
                 }
                 else{   
                    // Preparar resultado   
                    echo '{"code":"500"}';
                }
            }
        break;
        case "actualizarVotos":
     
        # Si el método HTTP es POST, proceder
        if ($_SERVER['REQUEST_METHOD'] == "POST"){
         
            # Recoger parámetros de la petición
            $titulo=addslashes($_POST['titulo']);
            $email_autor=addslashes($_POST['email_autor']);
            $email_votante=addslashes($_POST['email_votante']);
            $respuesta=addslashes($_POST['respuesta']);
            $count1=addslashes($_POST['count1']);
            $count2=addslashes($_POST['count2']);
            $count3=addslashes($_POST['count3']);
            $count4=addslashes($_POST['count4']);
     
            # Preparar consulta 
            $query = "UPDATE Pregunta SET cont1='$count1', cont2='$count2', cont3='$count3', cont4='$count4' WHERE titulo='$titulo' AND email_autor='$email_autor'";
            # Ejecutarla y obtener resultado
            $resultado = $con -> query($query);
           
            if ($resultado === TRUE){
                echo '{"code":"200"}';
                
                # Actualización exitosa -> Crear un registro en la tabla 'Contesta' para saber que
                # este usuario YA HA VOTADO

                echo $email_autor;
                echo $email_votante;
                echo $titulo;
                echo $respuesta;

                $query = "INSERT INTO Contesta (email_usuario, email_autor, titulo, respuesta) VALUES ('$email_votante', '$email_autor', '$titulo', '$respuesta')";
                $res = $con ->query($query);

                # Si ha ido bien
                if($res === TRUE){
                    # Devolver resultado 'code 200'
                    echo '{"code":"200"}';
                } 
                else{ # Si no ha ido bien
                    # Devolver resultado 'code 500'
                    echo '{"code":"500"}';
            }
        }
        break;
        }
        #-----------------------------------------------------------------------------------#
        // 1)   getPregunta: Método para conseguir una pregunta con sus PKs
        case "getPregunta":
            if ($_SERVER['REQUEST_METHOD'] == 'POST'){
                $titulo=addslashes($_POST['titulo']);
                $email=addslashes($_POST['email']);

                  // Consulta
                  $query=$con->prepare("SELECT * FROM Pregunta WHERE titulo='$titulo' AND email_autor='$email';");
                  
                  // Ejecutar consulta
                  $query -> execute();
   
                  // Recoger resultado 
                  $res = $query->get_result();
                
                  // Preparar la respuesta
                  for ($i = 1; $i <= mysqli_num_rows($res); $i++) {
                      $fila = $res->fetch_assoc();
                      echo json_encode($fila,JSON_UNESCAPED_UNICODE); 
                      if ($i != mysqli_num_rows($res) ){  
                                     echo "\t";
                      }
                 }
              }
          break;
          #-----------------------------------------------------------------------------------#
        # 3) Eliminar una pregunta de la DB 
        case "eliminarPregunta":
            
            # Si el método HTTP es POST, proceder
            if ($_SERVER['REQUEST_METHOD'] == "POST"){

                # Recoger parámetros de la petición
                $t=addslashes($_POST['titulo']);
                $emailAutor=addslashes($_POST['email_autor']);
            
                # Preparar consulta y ejecutarla
                $query = "DELETE from Pregunta where titulo ='$t' and email_autor='$emailAutor'";
                $respuesta = $con ->query($query);

                # Si devuelve TRUE (OK)
                if($respuesta === TRUE){
                    # Devolver resultado 'code 200'
                    echo '{"code":"200"}';
                }else{ # Si devuelve FALSE (error)
                    # Devolver resultado 'code 500'
                    echo '{"code":"500"}';
                }
            
            }
        break;
    }
?>