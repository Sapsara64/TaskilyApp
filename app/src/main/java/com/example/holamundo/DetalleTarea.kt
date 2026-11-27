package com.example.holamundo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetalleTarea(
    tarea: Tarea,
    cerrar: () -> Unit,
    completar: () -> Unit,
    eliminar: () -> Unit,
    editar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = cerrar,
        shape = RoundedCornerShape(22.dp),
        containerColor = Color(0xFFFDFBFF),

        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tarea.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF29243A)
                )

                Box {
                    IconButton(
                        onClick = { menuAbierto = true }
                    ) {
                        Text(
                            text = "⋮",
                            fontSize = 28.sp
                        )
                    }

                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = {
                            menuAbierto = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                menuAbierto = false
                                editar()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = {
                                menuAbierto = false
                                confirmarEliminar = true
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (tarea.estado == "Completado") {
                                        "Marcar como pendiente"
                                    } else {
                                        "Completar tarea"
                                    }
                                )
                            },
                            onClick = {
                                menuAbierto = false
                                completar()
                            }
                        )
                    }
                }
            }
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {


                Text(
                    text = tarea.descripcion.ifBlank {
                        "Sin descripción"
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF777287)
                )

                HorizontalDivider()

                FilaDetalle("Fecha", tarea.fechaOrden)
                FilaDetalle("Categoría", tarea.categoria)
                FilaDetalle("Prioridad", tarea.prioridad)
                FilaDetalle("Estado", tarea.estado)
            }
        },

        confirmButton = {
            TextButton(onClick = cerrar) {
                Text(
                    text = "Cerrar",
                    color = Color(0xFF7650DF)
                )
            }
        }
    )

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = {
                confirmarEliminar = false
            },

            title = {
                Text("¿Eliminar tarea?")
            },

            text = {
                Text("Esta acción no se puede deshacer.")
            },

            confirmButton = {
                TextButton(
                    onClick = eliminar
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color.Red
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FilaDetalle(
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titulo,
            color = Color(0xFF898398),
            fontSize = 13.sp
        )

        Text(
            text = valor.ifBlank { "No especificado" },
            color = Color(0xFF29243A),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
