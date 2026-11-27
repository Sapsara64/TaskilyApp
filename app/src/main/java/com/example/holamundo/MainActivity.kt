
package com.example.holamundo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Tarea(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val estado: String,
    val prioridad: String,
    val fechaOrden: String,
    val categoria: String,
    val hora: String = "",
    val horaRecordatorio: String = "09:00",
    val recordatorioActivo: Boolean = true,
    val favorita: Boolean = false,
    val cloudId: String = java.util.UUID.randomUUID().toString()
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val solicitarPermiso = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            solicitarPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        crearCanalNotificaciones(this)

        setContent {

            var mostrarSplash by remember { mutableStateOf(true) }
            var sesionIniciada by remember {
                mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
            }
            var mostrarAlerta by remember { mutableStateOf(false) }
            var mostrarCupertino by remember { mutableStateOf(false) }

            val infiniteTransition =
                rememberInfiniteTransition(label = "rotacion ")

            val rotacion by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        2000,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rot"
            )

            if (pantalla == 1) {

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000L)
                        segundos--

                        if (segundos == 0) {
                            colorFondo = Color(
                                red = Random.nextFloat(),
                                green = Random.nextFloat(),
                                blue = Random.nextFloat(),
                                alpha = 1f
                            )
                            segundos = 5
                        }

                        sincronizacionLista = true

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error al sincronizar: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    cargandoTareas = false
                }
            }

            // Guardar automáticamente los cambios en el almacenamiento local.
            // Las escrituras en Firestore se realizan en cada acción.
            LaunchedEffect(tareas.toList(), sincronizacionLista) {
                if (sincronizacionLista) {
                    guardarTareas(
                        this@MainActivity,
                        tareas.toList()
                    )
                }
            }

            if (mostrarSplash) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F6FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_nobg),
                        contentDescription = "Logo de Taskily",
                        modifier = Modifier.size(220.dp)
                    )
                }

            } else if (!sesionIniciada) {

                LoginScreen(
                    alIniciarSesion = {
                        scope.launch {
                            try {
                                iniciarSesionConGoogle(this@MainActivity)

                                sesionIniciada =
                                    FirebaseAuth.getInstance().currentUser != null

                                if (sesionIniciada) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "¡Bienvenido a Taskily!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "No se pudo iniciar sesión: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )

            } else if (cargandoTareas) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F6FC)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color(0xFF7650DF)
                    )
                }

            } else if (!sincronizacionLista) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F6FC)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "No se pudieron cargar tus tareas. Comprueba tu conexión e inténtalo de nuevo.",
                        color = Color(0xFF29243A)
                    )
                }

            } else {

                Scaffold(
                    bottomBar = {
                        MenuInferior(
                            pantallaSeleccionada = pantalla,
                            cambiarPantalla = {
                                pantalla = it

                                if (it != "Agregar") {
                                    tareaEditar = null
                                }
                            }
                        )
                    }
                ) { padding ->

                    when (pantalla) {

                        "Inicio" -> {
                            Inicio(
                                tareas = tareas,
                                filtro = filtro,
                                cambiarFiltro = { filtro = it },
                                orden = orden,
                                cambiarOrden = { orden = it },
                                alSeleccionarTarea = {
                                    tareaSeleccionada = it
                                },
                                alCambiarFavorita = { tarea ->

                                    val indice = tareas.indexOfFirst {
                                        it.id == tarea.id
                                    }

                                    if (indice != -1) {
                                        val actualizada = tareas[indice].copy(
                                            favorita = !tareas[indice].favorita
                                        )

                                        tareas[indice] = actualizada
                                        guardarEnNube(actualizada)
                                    }
                                },
                                modifier = Modifier.padding(padding)
                            )
                        }

                        "Agregar" -> {
                            NuevaTarea(
                                tareaEditar = tareaEditar,

                                guardarTarea = {
                                        nombre,
                                        descripcion,
                                        prioridad,
                                        fecha,
                                        categoria,
                                        hora,
                                        horaRecordatorio,
                                        recordatorioActivo ->

                                    val tareaExistente = tareaEditar

                                    if (tareaExistente == null) {

                                        val nuevaTarea = Tarea(
                                            id = siguienteId++,
                                            nombre = nombre,
                                            descripcion = descripcion,
                                            estado = "Pendiente",
                                            prioridad = prioridad,
                                            fechaOrden = fecha,
                                            categoria = categoria,
                                            hora = hora,
                                            horaRecordatorio = horaRecordatorio,
                                            recordatorioActivo = recordatorioActivo
                                        )

                                        tareas.add(nuevaTarea)
                                        guardarEnNube(nuevaTarea)

                                        programarRecordatorio(
                                            this@MainActivity,
                                            nuevaTarea
                                        )

                                    } else {

                                        val indice = tareas.indexOfFirst {
                                            it.id == tareaExistente.id
                                        }

                                        if (indice != -1) {
                                            val tareaActualizada =
                                                tareaExistente.copy(
                                                    nombre = nombre,
                                                    descripcion = descripcion,
                                                    prioridad = prioridad,
                                                    fechaOrden = fecha,
                                                    categoria = categoria,
                                                    hora = hora,
                                                    horaRecordatorio = horaRecordatorio,
                                                    recordatorioActivo = recordatorioActivo
                                                )

                                            tareas[indice] = tareaActualizada
                                            guardarEnNube(tareaActualizada)

                                            programarRecordatorio(
                                                this@MainActivity,
                                                tareaActualizada
                                            )
                                        }
                                    }

                                    tareaEditar = null
                                    pantalla = "Inicio"
                                },

                                modifier = Modifier.padding(padding)
                            )
                        }

                        "Calendario" -> {
                            Calendario(
                                tareas = tareas,
                                modifier = Modifier.padding(padding)
                            )
                        }
                    }

                    tareaSeleccionada?.let { tarea ->

                        DetalleTarea(
                            tarea = tarea,

                            cerrar = {
                                tareaSeleccionada = null
                            },

                            completar = {

                                val indice = tareas.indexOfFirst {
                                    it.id == tarea.id
                                }

                                if (indice != -1) {
                                    val estadoNuevo =
                                        if (tareas[indice].estado == "Completado") {
                                            "Pendiente"
                                        } else {
                                            "Completado"
                                        }

                                    val tareaActualizada = tareas[indice].copy(
                                        estado = estadoNuevo
                                    )

                                    tareas[indice] = tareaActualizada
                                    guardarEnNube(tareaActualizada)

                                    if (estadoNuevo == "Completado") {
                                        cancelarRecordatorio(
                                            this@MainActivity,
                                            tarea.id
                                        )
                                    } else {
                                        programarRecordatorio(
                                            this@MainActivity,
                                            tareaActualizada
                                        )
                                    }
                                }

                                tareaSeleccionada = null
                            },

                            eliminar = {
                                val uid = usuario?.uid

                                if (uid != null) {
                                    scope.launch {
                                        try {
                                            // Primero se elimina de Firestore.
                                            eliminarTareaFirestore(
                                                uid,
                                                tarea.cloudId
                                            )

                                            // Si se eliminó correctamente, se quita de la lista.
                                            tareas.removeAll {
                                                it.cloudId == tarea.cloudId
                                            }

                                            cancelarRecordatorio(
                                                this@MainActivity,
                                                tarea.id
                                            )

                                            tareaSeleccionada = null

                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "No se pudo eliminar de la nube: ${e.localizedMessage}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            },

                            editar = {
                                tareaEditar = tarea
                                tareaSeleccionada = null
                                pantalla = "Agregar"
                            }
                        )
                    }
                }
            }
        }
    }
}
