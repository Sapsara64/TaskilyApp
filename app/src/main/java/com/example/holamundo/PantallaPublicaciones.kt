package com.example.holamundo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Publicacion(
    val id: Int,
    val title: String,
    val body: String
)

object PublicacionesAPI {
    private const val BASE_URL =
        "https://jsonplaceholder.typicode.com/posts"

    suspend fun obtenerPublicaciones(): List<Publicacion> =
        withContext(Dispatchers.IO) {
            val connection = URL("$BASE_URL?_limit=20")
                .openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"

                val respuesta = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val json = org.json.JSONArray(respuesta)

                List(json.length()) { i ->
                    val item = json.getJSONObject(i)

                    Publicacion(
                        id = item.getInt("id"),
                        title = item.getString("title"),
                        body = item.getString("body")
                    )
                }
            } finally {
                connection.disconnect()
            }
        }

    suspend fun crearPublicacion(
        title: String,
        body: String
    ): Publicacion = withContext(Dispatchers.IO) {

        val connection = URL(BASE_URL)
            .openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
            )

            val json = JSONObject().apply {
                put("title", title)
                put("body", body)
                put("userId", 1)
            }

            connection.outputStream.use {
                it.write(json.toString().toByteArray(Charsets.UTF_8))
            }

            val respuesta = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val item = JSONObject(respuesta)

            Publicacion(
                id = item.getInt("id"),
                title = item.getString("title"),
                body = item.getString("body")
            )
        } finally {
            connection.disconnect()
        }
    }

    suspend fun editarPublicacion(
        id: Int,
        title: String,
        body: String
    ): Publicacion = withContext(Dispatchers.IO) {

        val connection = URL("$BASE_URL/$id")
            .openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "PUT"
            connection.doOutput = true
            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
            )

            val json = JSONObject().apply {
                put("title", title)
                put("body", body)
                put("userId", 1)
            }

            connection.outputStream.use {
                it.write(json.toString().toByteArray(Charsets.UTF_8))
            }

            val respuesta = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val item = JSONObject(respuesta)

            Publicacion(
                id = id,
                title = item.optString("title", title),
                body = item.optString("body", body)
            )
        } finally {
            connection.disconnect()
        }
    }

    suspend fun eliminarPublicacion(id: Int) =
        withContext(Dispatchers.IO) {

            val connection = URL("$BASE_URL/$id")
                .openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "DELETE"
                connection.responseCode
            } finally {
                connection.disconnect()
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPublicaciones(
    usuario: String,
    onCerrarSesion: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var publicaciones by remember {
        mutableStateOf<List<Publicacion>>(emptyList())
    }

    var cargando by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf("") }

    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarEliminar by remember { mutableStateOf(false) }

    var publicacionSeleccionada by remember {
        mutableStateOf<Publicacion?>(null)
    }

    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }

    fun cargarPublicaciones() {
        scope.launch {
            cargando = true

            try {
                publicaciones =
                    PublicacionesAPI.obtenerPublicaciones()
                mensaje = ""
            } catch (e: Exception) {
                mensaje = "Error al cargar publicaciones: ${e.message}"
            }

            cargando = false
        }
    }

    LaunchedEffect(Unit) {
        cargarPublicaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicaciones") },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    publicacionSeleccionada = null
                    titulo = ""
                    contenido = ""
                    mostrarDialogo = true
                }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Bienvenido, $usuario",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))



            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (publicaciones.isEmpty()) {
                Text("No hay publicaciones disponibles")
            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = publicaciones,
                        key = { it.id }
                    ) { publicacion ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = publicacion.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(publicacion.body)

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            publicacionSeleccionada =
                                                publicacion

                                            titulo = publicacion.title
                                            contenido = publicacion.body

                                            mostrarDialogo = true
                                        }
                                    ) {
                                        Text("Editar")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            publicacionSeleccionada =
                                                publicacion

                                            mostrarEliminar = true
                                        }
                                    ) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para crear o editar
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
            },
            title = {
                Text(
                    if (publicacionSeleccionada == null)
                        "Nueva publicación"
                    else
                        "Editar publicación"
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = contenido,
                        onValueChange = { contenido = it },
                        label = { Text("Contenido") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (titulo.isBlank() || contenido.isBlank()) {
                            mensaje = "Completa todos los campos"
                        } else {
                            scope.launch {
                                try {
                                    val seleccionada =
                                        publicacionSeleccionada

                                    if (seleccionada == null) {
                                        val nueva =
                                            PublicacionesAPI.crearPublicacion(
                                                titulo,
                                                contenido
                                            )

                                        val nuevoId =
                                            (publicaciones.maxOfOrNull {
                                                it.id
                                            } ?: 0) + 1

                                        publicaciones =
                                            listOf(
                                                nueva.copy(id = nuevoId)
                                            ) + publicaciones
                                    } else {
                                        val editada =
                                            PublicacionesAPI.editarPublicacion(
                                                seleccionada.id,
                                                titulo,
                                                contenido
                                            )

                                        publicaciones =
                                            publicaciones.map {
                                                if (it.id == seleccionada.id)
                                                    editada
                                                else
                                                    it
                                            }
                                    }

                                    mensaje = ""
                                    mostrarDialogo = false

                                } catch (e: Exception) {
                                    mensaje =
                                        "Error al guardar: ${e.message}"
                                }
                            }
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Confirmación para eliminar
    if (mostrarEliminar) {
        AlertDialog(
            onDismissRequest = {
                mostrarEliminar = false
            },
            title = {
                Text("Eliminar publicación")
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar esta publicación?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val seleccionada = publicacionSeleccionada

                        if (seleccionada != null) {
                            scope.launch {
                                try {
                                    PublicacionesAPI.eliminarPublicacion(
                                        seleccionada.id
                                    )

                                    publicaciones =
                                        publicaciones.filter {
                                            it.id != seleccionada.id
                                        }

                                    mensaje = ""
                                } catch (e: Exception) {
                                    mensaje =
                                        "Error al eliminar: ${e.message}"
                                }

                                mostrarEliminar = false
                            }
                        }
                    }
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarEliminar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
