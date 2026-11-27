
package com.example.holamundo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class PeriodoEstadisticas {
    MENSUAL, SEMANAL, ANUAL
}

private val diasSemana = listOf(
    DayOfWeek.MONDAY to "Lunes",
    DayOfWeek.TUESDAY to "Martes",
    DayOfWeek.WEDNESDAY to "Miércoles",
    DayOfWeek.THURSDAY to "Jueves",
    DayOfWeek.FRIDAY to "Viernes",
    DayOfWeek.SATURDAY to "Sábado",
    DayOfWeek.SUNDAY to "Domingo"
)

private fun fechaMensual(mes: YearMonth, dia: Int): LocalDate {
    return mes.atDay(dia.coerceIn(1, mes.lengthOfMonth()))
}

private fun fechaAnual(anio: Int, mes: Int, dia: Int): LocalDate {
    val yearMonth = YearMonth.of(anio, mes)
    return yearMonth.atDay(dia.coerceIn(1, yearMonth.lengthOfMonth()))
}

private fun inicioCiclo(
    fecha: LocalDate,
    periodo: PeriodoEstadisticas,
    diaMes: Int,
    diaSemana: DayOfWeek,
    mesAnual: Int,
    diaAnual: Int
): LocalDate {
    return when (periodo) {
        PeriodoEstadisticas.MENSUAL -> {
            val mesActual = YearMonth.from(fecha)
            val candidato = fechaMensual(mesActual, diaMes)

            if (fecha.isBefore(candidato)) {
                fechaMensual(mesActual.minusMonths(1), diaMes)
            } else {
                candidato
            }
        }

        PeriodoEstadisticas.SEMANAL -> {
            val diferencia =
                (fecha.dayOfWeek.value - diaSemana.value + 7) % 7

            fecha.minusDays(diferencia.toLong())
        }

        PeriodoEstadisticas.ANUAL -> {
            val candidato = fechaAnual(
                fecha.year,
                mesAnual,
                diaAnual
            )

            if (fecha.isBefore(candidato)) {
                fechaAnual(
                    fecha.year - 1,
                    mesAnual,
                    diaAnual
                )
            } else {
                candidato
            }
        }
    }
}

private fun siguienteCiclo(
    inicio: LocalDate,
    periodo: PeriodoEstadisticas,
    diaMes: Int,
    diaSemana: DayOfWeek,
    mesAnual: Int,
    diaAnual: Int
): LocalDate {
    return when (periodo) {
        PeriodoEstadisticas.MENSUAL ->
            fechaMensual(
                YearMonth.from(inicio).plusMonths(1),
                diaMes
            )

        PeriodoEstadisticas.SEMANAL ->
            inicio.plusWeeks(1)

        PeriodoEstadisticas.ANUAL ->
            fechaAnual(
                inicio.year + 1,
                mesAnual,
                diaAnual
            )
    }
}

private fun cicloAnterior(
    inicio: LocalDate,
    periodo: PeriodoEstadisticas,
    diaMes: Int,
    diaSemana: DayOfWeek,
    mesAnual: Int,
    diaAnual: Int
): LocalDate {
    return when (periodo) {
        PeriodoEstadisticas.MENSUAL ->
            fechaMensual(
                YearMonth.from(inicio).minusMonths(1),
                diaMes
            )

        PeriodoEstadisticas.SEMANAL ->
            inicio.minusWeeks(1)

        PeriodoEstadisticas.ANUAL ->
            fechaAnual(
                inicio.year - 1,
                mesAnual,
                diaAnual
            )
    }
}

@Composable
fun EstadisticasDialog(
    tareas: List<Tarea>,
    onDismiss: () -> Unit
) {
    val morado = Color(0xFF7650DF)
    val verde = Color(0xFF328653)
    val rosa = Color(0xFFF0B8C1)
    val texto = Color(0xFF29243A)
    val textoSecundario = Color(0xFF888197)
    val fondo = Color(0xFFF7F5FB)

    var periodo by remember {
        mutableStateOf(PeriodoEstadisticas.MENSUAL)
    }

    var diaMes by remember { mutableIntStateOf(4) }

    var diaSemana by remember {
        mutableStateOf(DayOfWeek.MONDAY)
    }

    var mesAnual by remember { mutableIntStateOf(1) }
    var diaAnual by remember { mutableIntStateOf(1) }

    var inicioSeleccionado by remember {
        mutableStateOf(
            inicioCiclo(
                LocalDate.now(),
                PeriodoEstadisticas.MENSUAL,
                4,
                DayOfWeek.MONDAY,
                1,
                1
            )
        )
    }

    var mostrarConfiguracion by remember {
        mutableStateOf(false)
    }

    val finSeleccionado = siguienteCiclo(
        inicioSeleccionado,
        periodo,
        diaMes,
        diaSemana,
        mesAnual,
        diaAnual
    )

    val formatoFecha = DateTimeFormatter.ofPattern(
        "d MMM yyyy",
        Locale("es", "MX")
    )

    val rangoTexto =
        "${inicioSeleccionado.format(formatoFecha)} – " +
                finSeleccionado.format(formatoFecha)

    val tareasDelPeriodo = tareas.filter { tarea ->
        try {
            val fecha = LocalDate.parse(tarea.fechaOrden)

            !fecha.isBefore(inicioSeleccionado) &&
                    fecha.isBefore(finSeleccionado)
        } catch (_: Exception) {
            false
        }
    }

    val total = tareasDelPeriodo.size

    val completadas = tareasDelPeriodo.count {
        it.estado == "Completado"
    }

    val pendientes = total - completadas

    // Datos de la gráfica de actividad
    val datosGrafica = if (periodo == PeriodoEstadisticas.ANUAL) {

        (0 until 12).map { indice ->
            val mes = YearMonth.from(inicioSeleccionado)
                .plusMonths(indice.toLong())

            val inicioMes = maxOf(
                inicioSeleccionado,
                mes.atDay(1)
            )

            val finMes = minOf(
                finSeleccionado,
                mes.plusMonths(1).atDay(1)
            )

            val cantidad = tareas.count { tarea ->
                try {
                    val fecha = LocalDate.parse(tarea.fechaOrden)

                    tarea.estado == "Completado" &&
                            !fecha.isBefore(inicioMes) &&
                            fecha.isBefore(finMes)
                } catch (_: Exception) {
                    false
                }
            }

            mes.format(
                DateTimeFormatter.ofPattern(
                    "MMM",
                    Locale("es", "MX")
                )
            ) to cantidad
        }

    } else {

        generateSequence(inicioSeleccionado) {
            it.plusDays(1)
        }
            .takeWhile { it.isBefore(finSeleccionado) }
            .map { fecha ->

                val cantidad = tareas.count { tarea ->
                    try {
                        LocalDate.parse(tarea.fechaOrden) == fecha &&
                                tarea.estado == "Completado"
                    } catch (_: Exception) {
                        false
                    }
                }

                val etiqueta = if (
                    periodo == PeriodoEstadisticas.SEMANAL
                ) {
                    fecha.format(
                        DateTimeFormatter.ofPattern(
                            "EEE",
                            Locale("es", "MX")
                        )
                    )
                } else {
                    fecha.dayOfMonth.toString()
                }

                etiqueta to cantidad
            }
            .toList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        shape = RoundedCornerShape(26.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Tu progreso",
                color = texto,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 580.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {

                // Selector de periodo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(fondo)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OpcionPeriodo(
                        texto = "Mensual",
                        seleccionada = periodo == PeriodoEstadisticas.MENSUAL,
                        modifier = Modifier.weight(1f)
                    ) {
                        periodo = PeriodoEstadisticas.MENSUAL

                        inicioSeleccionado = inicioCiclo(
                            LocalDate.now(),
                            PeriodoEstadisticas.MENSUAL,
                            diaMes,
                            diaSemana,
                            mesAnual,
                            diaAnual
                        )
                    }

                    OpcionPeriodo(
                        texto = "Semanal",
                        seleccionada = periodo == PeriodoEstadisticas.SEMANAL,
                        modifier = Modifier.weight(1f)
                    ) {
                        periodo = PeriodoEstadisticas.SEMANAL

                        inicioSeleccionado = inicioCiclo(
                            LocalDate.now(),
                            PeriodoEstadisticas.SEMANAL,
                            diaMes,
                            diaSemana,
                            mesAnual,
                            diaAnual
                        )
                    }

                    OpcionPeriodo(
                        texto = "Anual",
                        seleccionada = periodo == PeriodoEstadisticas.ANUAL,
                        modifier = Modifier.weight(1f)
                    ) {
                        periodo = PeriodoEstadisticas.ANUAL

                        inicioSeleccionado = inicioCiclo(
                            LocalDate.now(),
                            PeriodoEstadisticas.ANUAL,
                            diaMes,
                            diaSemana,
                            mesAnual,
                            diaAnual
                        )
                    }
                }

                // Rango y configuración
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Rango",
                        color = texto,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Configurar ciclo",
                            color = morado,
                            fontSize = 11.sp
                        )

                        IconButton(
                            onClick = {
                                mostrarConfiguracion = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurar ciclo",
                                tint = morado
                            )
                        }
                    }
                }

                // Navegación entre ciclos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(fondo)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "‹",
                        color = morado,
                        fontSize = 29.sp,
                        modifier = Modifier.clickable {
                            inicioSeleccionado = cicloAnterior(
                                inicioSeleccionado,
                                periodo,
                                diaMes,
                                diaSemana,
                                mesAnual,
                                diaAnual
                            )
                        }
                    )

                    Text(
                        text = rangoTexto,
                        color = texto,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "›",
                        color = morado,
                        fontSize = 29.sp,
                        modifier = Modifier.clickable {
                            inicioSeleccionado = siguienteCiclo(
                                inicioSeleccionado,
                                periodo,
                                diaMes,
                                diaSemana,
                                mesAnual,
                                diaAnual
                            )
                        }
                    )
                }

                // Gráfica circular
                Text(
                    text = "Distribución de tareas",
                    color = texto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GraficaPastel(
                        completadas = completadas,
                        pendientes = pendientes,
                        colorCompletadas = verde,
                        colorPendientes = rosa
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LeyendaEstadistica(
                            color = verde,
                            texto = "Completadas"
                        )

                        LeyendaEstadistica(
                            color = rosa,
                            texto = "Pendientes"
                        )
                    }
                }

                // Gráfica de barras
                Text(
                    text = if (periodo == PeriodoEstadisticas.ANUAL) {
                        "Tareas completadas por mes"
                    } else {
                        "Tareas completadas por día"
                    },
                    color = texto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                GraficaActividad(
                    datos = datosGrafica,
                    color = verde
                )

                // Tarjetas de resumen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TarjetaResumen(
                        titulo = "Total",
                        cantidad = total,
                        fondo = Color(0xFFF2EEFC),
                        colorTexto = morado,
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaResumen(
                        titulo = "Pendientes",
                        cantidad = pendientes,
                        fondo = Color(0xFFFFF0F1),
                        colorTexto = Color(0xFFD47785),
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaResumen(
                        titulo = "Completadas",
                        cantidad = completadas,
                        fondo = Color(0xFFEAF6EF),
                        colorTexto = verde,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (total == 0) {
                    Text(
                        text = "No hay tareas registradas en este periodo.",
                        color = textoSecundario,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = morado
                    )
                ) {
                    Text(
                        text = "Cerrar",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    )

    if (mostrarConfiguracion) {
        ConfiguracionCicloDialog(
            periodo = periodo,
            diaMesInicial = diaMes,
            diaSemanaInicial = diaSemana,
            mesAnualInicial = mesAnual,
            diaAnualInicial = diaAnual,
            onDismiss = {
                mostrarConfiguracion = false
            },
            onGuardar = { nuevoDiaMes, nuevaSemana, nuevoMes, nuevoDiaAnual ->
                diaMes = nuevoDiaMes
                diaSemana = nuevaSemana
                mesAnual = nuevoMes
                diaAnual = nuevoDiaAnual

                inicioSeleccionado = inicioCiclo(
                    LocalDate.now(),
                    periodo,
                    nuevoDiaMes,
                    nuevaSemana,
                    nuevoMes,
                    nuevoDiaAnual
                )

                mostrarConfiguracion = false
            }
        )
    }
}

@Composable
private fun GraficaActividad(
    datos: List<Pair<String, Int>>,
    color: Color
) {
    val maximo = (datos.maxOfOrNull { it.second } ?: 0)
        .coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF7F5FB))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Cantidad de tareas completadas",
            color = Color(0xFF888197),
            fontSize = 11.sp
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(155.dp)
        ) {
            if (datos.isNotEmpty()) {
                val ancho = size.width
                val alto = size.height

                val margenSuperior = 12.dp.toPx()
                val margenInferior = 12.dp.toPx()
                val altoGrafica = alto - margenSuperior - margenInferior

                // Líneas horizontales de referencia
                for (i in 0..4) {
                    val y = margenSuperior + altoGrafica * i / 4f

                    drawLine(
                        color = Color(0xFFE5E1EC),
                        start = Offset(0f, y),
                        end = Offset(ancho, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val puntos = datos.mapIndexed { indice, dato ->
                    val x = if (datos.size == 1) {
                        ancho / 2f
                    } else {
                        indice * ancho / (datos.size - 1).toFloat()
                    }

                    val y = margenSuperior +
                            altoGrafica -
                            (dato.second.toFloat() / maximo) * altoGrafica

                    Offset(x, y)
                }

                // Línea que conecta los puntos
                for (i in 0 until puntos.size - 1) {
                    drawLine(
                        color = color,
                        start = puntos[i],
                        end = puntos[i + 1],
                        strokeWidth = 3.dp.toPx()
                    )
                }

                // Puntos de la gráfica
                puntos.forEach { punto ->
                    drawCircle(
                        color = color,
                        radius = 4.dp.toPx(),
                        center = punto
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 1.8.dp.toPx(),
                        center = punto
                    )
                }
            }
        }

        if (datos.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = datos.first().first,
                    color = Color(0xFF888197),
                    fontSize = 10.sp
                )

                if (datos.size > 2) {
                    Text(
                        text = datos[datos.size / 2].first,
                        color = Color(0xFF888197),
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = datos.last().first,
                    color = Color(0xFF888197),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun ConfiguracionCicloDialog(
    periodo: PeriodoEstadisticas,
    diaMesInicial: Int,
    diaSemanaInicial: DayOfWeek,
    mesAnualInicial: Int,
    diaAnualInicial: Int,
    onDismiss: () -> Unit,
    onGuardar: (Int, DayOfWeek, Int, Int) -> Unit
) {
    var diaMes by remember {
        mutableIntStateOf(diaMesInicial)
    }

    var diaSemana by remember {
        mutableStateOf(diaSemanaInicial)
    }

    var mesAnual by remember {
        mutableIntStateOf(mesAnualInicial)
    }

    var diaAnual by remember {
        mutableIntStateOf(diaAnualInicial)
    }

    val nombresMeses = listOf(
        "Enero", "Febrero", "Marzo", "Abril",
        "Mayo", "Junio", "Julio", "Agosto",
        "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Configurar ciclo",
                color = Color(0xFF29243A),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Definir ciclo cada:",
                    color = Color(0xFF888197),
                    fontSize = 13.sp
                )

                when (periodo) {
                    PeriodoEstadisticas.MENSUAL -> {
                        SelectorDesplegable(
                            titulo = "Día de cada mes",
                            seleccionado = diaMes.toString(),
                            opciones = (1..31).map { it.toString() }
                        ) {
                            diaMes = it.toInt()
                        }

                        Text(
                            text = "Ejemplo: cada día 4 comenzará un nuevo ciclo mensual.",
                            color = Color(0xFF888197),
                            fontSize = 12.sp
                        )
                    }

                    PeriodoEstadisticas.SEMANAL -> {
                        SelectorDesplegable(
                            titulo = "Día de inicio de semana",
                            seleccionado = diasSemana.first {
                                it.first == diaSemana
                            }.second,
                            opciones = diasSemana.map { it.second }
                        ) { seleccionado ->
                            diaSemana = diasSemana.first {
                                it.second == seleccionado
                            }.first
                        }

                        Text(
                            text = "Cada semana comenzará el día que elijas.",
                            color = Color(0xFF888197),
                            fontSize = 12.sp
                        )
                    }

                    PeriodoEstadisticas.ANUAL -> {
                        SelectorDesplegable(
                            titulo = "Mes de inicio",
                            seleccionado = nombresMeses[mesAnual - 1],
                            opciones = nombresMeses
                        ) {
                            mesAnual = nombresMeses.indexOf(it) + 1
                        }

                        SelectorDesplegable(
                            titulo = "Día de inicio",
                            seleccionado = diaAnual.toString(),
                            opciones = (1..31).map { it.toString() }
                        ) {
                            diaAnual = it.toInt()
                        }

                        Text(
                            text = "Ejemplo: cada 4 de octubre comenzará un nuevo ciclo anual.",
                            color = Color(0xFF888197),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(
                        diaMes,
                        diaSemana,
                        mesAnual,
                        diaAnual
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7650DF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar configuración")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancelar",
                    color = Color(0xFF7650DF)
                )
            }
        }
    )
}

@Composable
private fun SelectorDesplegable(
    titulo: String,
    seleccionado: String,
    opciones: List<String>,
    onSeleccionar: (String) -> Unit
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = titulo,
            color = Color(0xFF29243A),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandido = true
                    },
                color = Color(0xFFF7F5FB),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 13.dp,
                        vertical = 13.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = seleccionado,
                        color = Color(0xFF29243A),
                        fontSize = 14.sp
                    )

                    Text(
                        text = "⌄",
                        color = Color(0xFF7650DF),
                        fontSize = 18.sp
                    )
                }
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    expandido = false
                },
                modifier = Modifier.heightIn(max = 240.dp),
                containerColor = Color.White
            ){
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(opcion)
                        },
                        onClick = {
                            onSeleccionar(opcion)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OpcionPeriodo(
    texto: String,
    seleccionada: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .clickable {
                onClick()
            },
        color = if (seleccionada) {
            Color(0xFF7650DF)
        } else {
            Color.Transparent
        },
        shape = RoundedCornerShape(9.dp)
    ) {
        Text(
            text = texto,
            color = if (seleccionada) {
                Color.White
            } else {
                Color(0xFF7650DF)
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 10.dp
            )
        )
    }
}

@Composable
private fun GraficaPastel(
    completadas: Int,
    pendientes: Int,
    colorCompletadas: Color,
    colorPendientes: Color
) {
    val total = completadas + pendientes

    Canvas(
        modifier = Modifier.size(155.dp)
    ) {
        val diametro = size.minDimension

        val posicion = Offset(
            (size.width - diametro) / 2,
            (size.height - diametro) / 2
        )

        val tamano = Size(diametro, diametro)

        if (total == 0) {
            drawArc(
                color = Color(0xFFEAE7F0),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = true,
                topLeft = posicion,
                size = tamano
            )
        } else {
            val anguloCompletadas =
                completadas.toFloat() / total.toFloat() * 360f

            if (completadas > 0) {
                drawArc(
                    color = colorCompletadas,
                    startAngle = -90f,
                    sweepAngle = anguloCompletadas,
                    useCenter = true,
                    topLeft = posicion,
                    size = tamano
                )
            }

            if (pendientes > 0) {
                drawArc(
                    color = colorPendientes,
                    startAngle = -90f + anguloCompletadas,
                    sweepAngle = 360f - anguloCompletadas,
                    useCenter = true,
                    topLeft = posicion,
                    size = tamano
                )
            }
        }
    }
}

@Composable
private fun LeyendaEstadistica(
    color: Color,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )

        Text(
            text = texto,
            color = Color(0xFF888197),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun TarjetaResumen(
    titulo: String,
    cantidad: Int,
    fondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(fondo)
            .padding(
                vertical = 13.dp,
                horizontal = 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = titulo,
            color = Color(0xFF888197),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Text(
            text = cantidad.toString(),
            color = colorTexto,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
