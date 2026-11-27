
package com.example.holamundo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class Tarea(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val estado: String,
    val prioridad: String,
    val fechaOrden: String,
    val categoria: String
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // PANTALLA ACTUAL
            var pantalla by remember {
                mutableStateOf("Inicio")
            }

            // FILTRO DE TAREAS
            var filtro by remember {
                mutableStateOf("Todas")
            }

            // ORDENAMIENTO
            var orden by remember {
                mutableStateOf("Prioridad")
            }

            // LISTA COMPARTIDA DE TAREAS
            val tareas = remember {
                mutableStateListOf(
                    Tarea(
                        1,
                        "Revisar propuesta",
                        "Hoy · 10:00 · Trabajo",
                        "Pendiente",
                        "Alta",
                        "2026-11-26",
                        "Trabajo"
                    ),
                    Tarea(
                        2,
                        "Ordenar mis notas",
                        "Hoy · 08:30 · Personal",
                        "Completado",
                        "Baja",
                        "2026-11-26",
                        "Personal"
                    ),
                    Tarea(
                        3,
                        "Enviar presupuesto",
                        "21 oct · 10:00 · Trabajo",
                        "Pendiente",
                        "Media",
                        "2026-10-21",
                        "Trabajo"
                    )
                )
            }

            // CONTADOR PARA NUEVOS IDS
            var siguienteId by remember {
                mutableIntStateOf(4)
            }

            // TAREA SELECCIONADA PARA VER DETALLES
            var tareaSeleccionada by remember {
                mutableStateOf<Tarea?>(null)
            }

            // TAREA QUE SE VA A EDITAR
            var tareaEditar by remember {
                mutableStateOf<Tarea?>(null)
            }

            Scaffold(
                containerColor = Color(0xFFF7F6FC),

                bottomBar = {
                    MenuInferior(
                        pantallaSeleccionada = pantalla,
                        cambiarPantalla = { nuevaPantalla ->
                            pantalla = nuevaPantalla
                        }
                    )
                }

            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    when (pantalla) {

                        // PANTALLA DE INICIO
                        "Inicio" -> {

                            Inicio(
                                tareas = tareas,
                                filtro = filtro,
                                cambiarFiltro = { filtro = it },
                                orden = orden,
                                cambiarOrden = { orden = it },



                                alSeleccionarTarea = { tarea ->
                                    tareaSeleccionada = tarea
                                }
                            )
                        }

                        // PANTALLA PARA AGREGAR TAREAS

                        // PANTALLA CALENDARIO
                        "Calendario" -> {

                            Calendario(
                                tareas = tareas
                            )
                        }
                    }
                }
            }

            // VENTANA DE DETALLES DE LA TAREA
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
                            val nuevoEstado =
                                if (tareas[indice].estado == "Completado") {
                                    "Pendiente"
                                } else {
                                    "Completado"
                                }

                            tareas[indice] =
                                tareas[indice].copy(
                                    estado = nuevoEstado
                                )
                        }

                        tareaSeleccionada = null
                    },

                    eliminar = {
                        tareas.removeAll {
                            it.id == tarea.id
                        }

                        tareaSeleccionada = null
                    },

                    editar = {
                        tareaEditar = tarea
                        tareaSeleccionada = null
                    }
                )
            }

            // EDICIÓN: SE CONECTARÁ CON EL FORMULARIO
            // DE EDICIÓN EN EL SIGUIENTE PASO.
        }
    }
}
