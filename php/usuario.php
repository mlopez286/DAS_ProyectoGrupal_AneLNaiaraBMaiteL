<?php
	include 'conexion.php';
	
	$id_recurso=$_POST['id_recurso'];
	$email=$_POST['email'];
	
	switch($id_recurso){
		#-----------------------------------------------------------------------------------#
		case "login":
			$password=$_POST['password'];

			$consulta = "SELECT * FROM Usuario WHERE email='$email'";
			$result = mysqli_query($con, $consulta);
			if($result){
				#Acceder al resultado
				$fila = mysqli_fetch_row($result);
				$hash = $fila[3];
				$exist = password_verify($password, $hash);
				if($exist){
					# Generar el array con los resultados con la forma Atributo - Valor
					$arrayresultados = array(
						'exist' => 'true',
						'nombre' => $fila[1],
						'apellido' => $fila[2],
						'email' => $fila[0]
					);
					#Devolver el resultado en formato JSON
					echo json_encode($arrayresultados);
				} else{
					echo '{"exist":"false"}';
				}
			} else{
					echo "No existe";
			}
			break;

		#-----------------------------------------------------------------------------------#
		case "existe":

			$consulta = "SELECT * FROM Usuario WHERE email='$email'";
			$result = mysqli_query($con, $consulta);

			if($result){
				#Acceder al resultado
				$fila = mysqli_fetch_row($result);
				if(isset($fila[0])){
					echo '{"exist":"true"}';
				} else{
					echo '{"exist":"false"}';
				}
			} else{
				echo "No existe";
			}
			break;

		#-----------------------------------------------------------------------------------#
		case "registro":
			$nombre = $_POST["nombre"];
			$apellido = $_POST["apellido"];
			$email = $_POST["email"];
			$password = $_POST["password"];
			$fecha = date('Y-m-d');

			$hash = password_hash($password,PASSWORD_DEFAULT);
			
			$consulta = "INSERT INTO Usuario (nombre,apellido,email,contrasena,fecha) VALUES('$nombre','$apellido','$email','$hash', '$fecha')";
			$result = $con->query($consulta);
			
			if($result){
				echo "Datos insertados";
			} else{
				echo "No se pudo insertar";
			}
			break;
			
		#-----------------------------------------------------------------------------------#
		case "token":
			$token = $_POST["token"];
			
			$consulta = "UPDATE Usuario SET token='$token' WHERE email='$email'";

			$result = $con->query($consulta);
			if($result){
				echo "{'done':'true'}";
			} else{
				echo "{'done':'false'}";
			}
			break;
	}

	$con->close();	
?>