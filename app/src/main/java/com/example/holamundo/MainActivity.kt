
package com.example.holamundo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import kotlinx.coroutines.delay

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
    val favorita: Boolean = false
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

            var mostrarSplash by remember {
                mutableStateOf(true)
            }

            LaunchedEffect(Unit) {
                delay(1800)
                mostrarSplash = false
            }

            val tareas = remember {
                mutableStateListOf<Tarea>().apply {
                    addAll(cargarTareas(this@MainActivity))
                }
            }

            var pantalla by remember {
                mutableStateOf("Inicio")
            }

            var filtro by remember {
                mutableStateOf("Todas")
            }

            var orden by remember {
                mutableStateOf("Fecha")
            }

            var siguienteId by remember {
                mutableIntStateOf(
                    (tareas.maxOfOrNull { it.id } ?: 0) + 1
                )
            }

            var tareaSeleccionada by remember {
                mutableStateOf<Tarea?>(null)
            }

            var tareaEditar by remember {
                mutableStateOf<Tarea?>(null)
            }

            LaunchedEffect(tareas.toList()) {
                guardarTareas(this@MainActivity, tareas.toList())

                tareas.forEach { tarea ->
                    programarRecordatorio(this@MainActivity, tarea)
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
                        painter = painterResource(
                            id = R.drawable.logo_nobg
                        ),
                        contentDescription = "Logo de Taskily",
                        modifier = Modifier.size(220.dp)
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
                                        tareas[indice] = tareas[indice].copy(
                                            favorita = !tareas[indice].favorita
                                        )
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

                                    val tareaActualizada =
                                        tareas[indice].copy(
                                            estado = estadoNuevo
                                        )

                                    tareas[indice] = tareaActualizada

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
                                tareas.removeAll {
                                    it.id == tarea.id
                                }

                                cancelarRecordatorio(
                                    this@MainActivity,
                                    tarea.id
                                )

                                tareaSeleccionada = null
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
