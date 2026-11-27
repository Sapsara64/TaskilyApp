package com.example.holamundo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Inicio(
    tareas: List<Tarea>,
    filtro: String,
    cambiarFiltro: (String) -> Unit,
    orden: String,
    cambiarOrden: (String) -> Unit,
    alSeleccionarTarea: (Tarea) -> Unit,
    modifier: Modifier = Modifier
) {

    var menuAbierto by remember {
        mutableStateOf(false)
    }

    val tareasFiltradas = tareas.filter { tarea ->
        when (filtro) {
            "Pendientes" -> tarea.estado == "Pendiente"
            "Completadas" -> tarea.estado == "Completado"
            else -> true
        }
    }

    val tareasOrdenadas = when (orden) {

        "Prioridad" -> tareasFiltradas.sortedBy {
            when (it.prioridad) {
                "Alta" -> 1
                "Media" -> 2
                else -> 3
            }
        }

        "Fecha" -> tareasFiltradas.sortedBy {
            it.fechaOrden
        }

        "Nombre" -> tareasFiltradas.sortedBy {
            it.nombre.lowercase()
        }

        else -> tareasFiltradas
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6FC))
            .padding(20.dp)
    ) {

        // TITULO

        Text(
            text = "Taskily",
            fontSize = 28.sp,
            color = Color(0xFF29243A)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // MENU DE FILTROS

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF0EEF5))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            listOf(
                "Todas",
                "Pendientes",
                "Completadas"
            ).forEach { opcion ->

                val cantidad = when (opcion) {

                    "Pendientes" -> tareas.count {
                        it.estado == "Pendiente"
                    }

                    "Completadas" -> tareas.count {
                        it.estado == "Completado"
                    }

                    else -> tareas.size
                }

                val seleccionado = filtro == opcion

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (seleccionado)
                                Color.White
                            else
                                Color.Transparent
                        )
                        .clickable {
                            cambiarFiltro(opcion)
                        }
                        .padding(
                            horizontal = 2.dp,
                            vertical = 12.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "$opcion $cantidad",
                        fontSize = 11.sp,
                        color = if (seleccionado)
                            Color(0xFF754CE6)
                        else
                            Color(0xFF777287)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // FILTRO DE ORDENAMIENTO

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Filtro",
                fontSize = 15.sp,
                color = Color(0xFF29243A)
            )

            Box {

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .clickable {
                            menuAbierto = true
                        }
                        .padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "☷",
                        fontSize = 15.sp,
                        color = Color(0xFF754CE6)
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "Ordenar: $orden",
                        fontSize = 12.sp,
                        color = Color(0xFF754CE6)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "▼",
                        fontSize = 10.sp,
                        color = Color(0xFF754CE6)
                    )
                }

                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = {
                        menuAbierto = false
                    }
                ) {

                    listOf(
                        "Prioridad",
                        "Fecha",
                        "Nombre"
                    ).forEach { opcion ->

                        DropdownMenuItem(
                            text = {
                                Text(opcion)
                            },
                            onClick = {
                                cambiarOrden(opcion)
                                menuAbierto = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // TITULO DE TAREAS

        Text(
            text = "Tus tareas",
            fontSize = 20.sp,
            color = Color(0xFF29243A)
        )

        Spacer(modifier = Modifier.height(15.dp))

        // LISTA DE TAREAS

        if (tareasOrdenadas.isEmpty()) {

            Text(
                text = "No hay tareas en esta categoría.",
                fontSize = 14.sp,
                color = Color(0xFF777287)
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                items(
                    items = tareasOrdenadas,
                    key = { it.id }
                ) { tarea ->

                    TarjetaTarea(
                        tarea = tarea,
                        alHacerClic = {
                            alSeleccionarTarea(tarea)
                        }
                    )
                }
            }
        }
    }
}
