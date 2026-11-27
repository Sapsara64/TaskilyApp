
package com.example.holamundo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Calendario(
    tareas: List<Tarea>,
    modifier: Modifier = Modifier
) {
    var mesActual by remember {
        mutableStateOf(YearMonth.now())
    }

    var fechaSeleccionada by remember {
        mutableStateOf(LocalDate.now())
    }

    var mostrarEstadisticas by remember {
        mutableStateOf(false)
    }

    val diasDelMes = mesActual.lengthOfMonth()
    val primerDia = mesActual.atDay(1).dayOfWeek.value - 1

    val tareasDelDia = tareas.filter {
        it.fechaOrden == fechaSeleccionada.toString()
    }

    val tareasDelMes = tareas.filter {
        it.fechaOrden.startsWith(mesActual.toString())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5FB))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Calendario",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF29243A)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    mesActual = mesActual.minusMonths(1)
                }
            ) {
                Text("‹ Anterior")
            }

            Text(
                text = mesActual.month.getDisplayName(
                    TextStyle.FULL,
                    Locale("es", "ES")
                ).replaceFirstChar { it.uppercase() } +
                        " ${mesActual.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            TextButton(
                onClick = {
                    mesActual = mesActual.plusMonths(1)
                }
            ) {
                Text("Siguiente ›")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach { dia ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dia,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF898398)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalCeldas = ((primerDia + diasDelMes + 6) / 7) * 7

        for (fila in 0 until totalCeldas / 7) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (columna in 0 until 7) {
                    val indice = fila * 7 + columna
                    val numeroDia = indice - primerDia + 1

                    if (numeroDia in 1..diasDelMes) {
                        val fecha = mesActual.atDay(numeroDia)
                        val seleccionado = fecha == fechaSeleccionada

                        val tieneTarea = tareas.any {
                            it.fechaOrden == fecha.toString()
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .aspectRatio(1f)
                                .background(
                                    color = if (seleccionado) {
                                        Color(0xFF7650DF)
                                    } else {
                                        Color.White
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    fechaSeleccionada = fecha
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = numeroDia.toString(),
                                    color = if (seleccionado) {
                                        Color.White
                                    } else {
                                        Color(0xFF29243A)
                                    }
                                )

                                if (tieneTarea) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(
                                                if (seleccionado) Color.White
                                                else Color(0xFF7650DF),
                                                RoundedCornerShape(50)
                                            )
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // BOTÓN DE ESTADÍSTICAS

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF7650DF),
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    mostrarEstadisticas = true
                }
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.height(19.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(7.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(12.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(18.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Ver estadísticas",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tareas del ${fechaSeleccionada.dayOfMonth}/${fechaSeleccionada.monthValue}/${fechaSeleccionada.year}",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF29243A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (tareasDelDia.isEmpty()) {
            Text(
                text = "No tienes tareas para este día.",
                color = Color(0xFF898398)
            )
        } else {
            // Todas las tarjetas forman parte del desplazamiento general.
            tareasDelDia.forEach { tarea ->
                TarjetaTarea(
                    tarea = tarea,
                    alHacerClic = {
                        // Se conserva el comportamiento actual.
                    }
                )
            }
        }
    }

    if (mostrarEstadisticas) {
        EstadisticasDialog(
            tareas = tareas,
            onDismiss = { mostrarEstadisticas = false }
        )
    }
}
