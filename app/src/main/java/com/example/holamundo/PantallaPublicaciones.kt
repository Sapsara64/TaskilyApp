
package com.example.holamundo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun PantallaPublicaciones(
    usuario: String,
    onCerrarSesion: () -> Unit
) {
    var usuarios by remember {
        mutableStateOf(listOf<Pair<String, String>>())
    }

    var mensaje by remember { mutableStateOf("Cargando...") }
    var nombre by remember { mutableStateOf("") }
    var idEditar by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val url = "https://jsonplaceholder.typicode.com/users"

    // GET: consultar usuarios
    LaunchedEffect(Unit) {
        try {
            val respuesta = withContext(Dispatchers.IO) {
                val conexion = URL(url).openConnection() as HttpURLConnection

                try {
                    conexion.requestMethod = "GET"
                    conexion.inputStream.bufferedReader().use {
                        it.readText()
                    }
                } finally {
                    conexion.disconnect()
                }
            }

            val json = org.json.JSONArray(respuesta)

            usuarios = List(json.length()) { i ->
                val objeto = json.getJSONObject(i)

                Pair(
                    objeto.getString("id"),
                    objeto.getString("name") + " lol"
                )
            }

            mensaje = ""

        } catch (e: Exception) {
            mensaje = "Error: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bienvenido, $usuario")

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para escribir el nombre
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )

        // PUT: guardar cambios (ARRIBA)
        if (idEditar != null) {

            Text("Editando ID: $idEditar")

            Button(onClick = {
                scope.launch {
                    try {
                        enviarPeticion(
                            "$url/$idEditar",
                            "PUT",
                            JSONObject().put("name", nombre).toString()
                        )

                        usuarios = usuarios.map {
                            if (it.first == idEditar)
                                it.first to "$nombre lol"
                            else it
                        }

                        idEditar = null
                        nombre = ""
                        mensaje = "Usuario editado correctamente"

                    } catch (e: Exception) {
                        mensaje = "Error: ${e.message}"
                    }
                }
            }) {
                Text("Guardar cambios")
            }

            Button(onClick = {
                idEditar = null
                nombre = ""
            }) {
                Text("Cancelar")
            }
        }

        // POST: añadir usuario
        Button(onClick = {
            if (nombre.isNotBlank()) {
                scope.launch {
                    try {
                        val respuesta = enviarPeticion(
                            url,
                            "POST",
                            JSONObject().put("name", nombre).toString()
                        )

                        val id = JSONObject(respuesta).getString("id")

                        usuarios = usuarios + (id to "$nombre lol")
                        nombre = ""
                        mensaje = "Usuario añadido"

                    } catch (e: Exception) {
                        mensaje = "Error: ${e.message}"
                    }
                }
            }
        }) {
            Text("Añadir")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mostrar usuarios
        usuarios.forEach { persona ->

            Text("Id: ${persona.first}")
            Text("Nombre: ${persona.second}")

            // Seleccionar usuario para editar
            Button(onClick = {
                idEditar = persona.first
                nombre = persona.second.removeSuffix(" lol")
                mensaje = "Editando usuario ${persona.first}"
            }) {
                Text("Editar")
            }

            // DELETE: eliminar usuario
            Button(onClick = {
                scope.launch {
                    try {
                        enviarPeticion(
                            "$url/${persona.first}",
                            "DELETE"
                        )

                        usuarios = usuarios.filter {
                            it.first != persona.first
                        }

                        mensaje = "Usuario eliminado"

                    } catch (e: Exception) {
                        mensaje = "Error: ${e.message}"
                    }
                }
            }) {
                Text("Eliminar")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(mensaje)

        Button(onClick = onCerrarSesion) {
            Text("Regresar")
        }
    }
}

// Función para enviar POST, PUT y DELETE
suspend fun enviarPeticion(
    direccion: String,
    metodo: String,
    datos: String? = null
): String = withContext(Dispatchers.IO) {

    val conexion = URL(direccion).openConnection() as HttpURLConnection

    try {
        conexion.requestMethod = metodo

        if (datos != null) {
            conexion.doOutput = true
            conexion.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            conexion.outputStream.bufferedWriter().use {
                it.write(datos)
            }
        }

        if (conexion.responseCode in 200..299) {
            conexion.inputStream.bufferedReader().use {
                it.readText()
            }
        } else {
            throw Exception("Error HTTP: ${conexion.responseCode}")
        }

    } finally {
        conexion.disconnect()
    }
}
