package com.example.holamundo

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Morado = Color(0xFF7650DF)
private val Fondo = Color(0xFFF7F6FC)
private val Texto = Color(0xFF29243A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTarea(
    tareaEditar: Tarea? = null,
    guardarTarea: (
        String, String, String, String, String,
        String, String, Boolean
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var nombre by remember(tareaEditar) {
        mutableStateOf(tareaEditar?.nombre ?: "")
    }

    var descripcion by remember(tareaEditar) {
        mutableStateOf(tareaEditar?.descripcion ?: "")
    }

    var prioridad by remember(tareaEditar) {
        mutableStateOf(tareaEditar?.prioridad ?: "Media")
    }

    var categoria by remember(tareaEditar) {
        mutableStateOf(tareaEditar?.categoria ?: "Personal")
    }

    var menuCategoria by remember { mutableStateOf(false) }

    var recordatorio by remember(tareaEditar) {
        mutableStateOf(tareaEditar?.recordatorioActivo ?: true)
    }

    var hora by remember(tareaEditar) {
        mutableStateOf(tareaEditar?.hora ?: "")
    }

    var horaRecordatorio by remember(tareaEditar) {
        mutableStateOf(tareaEditar?.horaRecordatorio ?: "09:00")
    }

    var fechaMillis by remember(tareaEditar) {
        mutableStateOf(
            tareaEditar?.fechaOrden?.let {
                try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(it)?.time
                } catch (e: Exception) {
                    null
                }
            } ?: System.currentTimeMillis()
        )
    }

    var mostrarCalendario by remember { mutableStateOf(false) }

    val fechaTexto = remember(fechaMillis) {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            .format(Date(fechaMillis))
    }

    val categorias = listOf(
        "Personal", "Trabajo", "Estudio",
        "Salud", "Hogar", "Otros"
    )


    fun abrirSelectorHora(valorActual: String, alElegir: (String) -> Unit) {
        val partes = valorActual.split(":")
        val horaInicial = partes.getOrNull(0)?.toIntOrNull()
            ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minutoInicial = partes.getOrNull(1)?.toIntOrNull()
            ?: Calendar.getInstance().get(Calendar.MINUTE)

        val contextoMorado = android.view.ContextThemeWrapper(
            context,
            R.style.TaskilyTimePicker
        )

        val dialogo = TimePickerDialog(
            contextoMorado,
            { _, horaElegida, minutoElegido ->
                alElegir(
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        horaElegida,
                        minutoElegido
                    )
                )
            },
            horaInicial,
            minutoInicial,
            true
        )

        // Cambiar el color de los botones del selector
        dialogo.setOnShowListener {
            dialogo.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(android.graphics.Color.rgb(118, 80, 223))

            dialogo.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(android.graphics.Color.rgb(118, 80, 223))
        }
        dialogo.window?.setBackgroundDrawableResource(
            R.drawable.fondo_dialogo_redondeado
        )
        dialogo.show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {

        // TITULO
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_main_nobg),
                contentDescription = "Logo de Taskily",
                modifier = Modifier.size(42.dp)
            )

            Text(
                if (tareaEditar == null) "Nueva tarea" else "Editar tarea",
                fontSize = 28.sp,
                color = Color(0xFF29243A)
            )
        }



        Spacer(Modifier.height(22.dp))

        Text("Título de la tarea *", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(7.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = { Text("Escribe el nombre de tu tarea") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Morado,
                unfocusedBorderColor = Color(0xFFE7E2EF),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(15.dp))

        Text("Descripción / notas", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(7.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = { Text("Agrega detalles de tu tarea...") },
            modifier = Modifier.fillMaxWidth().height(90.dp),
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Morado,
                unfocusedBorderColor = Color(0xFFE7E2EF),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(15.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

            Column(Modifier.weight(1f)) {
                Text("Fecha *", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(7.dp))

                OutlinedButton(
                    onClick = { mostrarCalendario = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Texto
                    )
                ) {
                    Icon(Icons.Default.DateRange, null, tint = Morado)
                    Spacer(Modifier.width(7.dp))
                    Text(fechaTexto, fontSize = 11.sp)
                }
            }

            Column(Modifier.weight(1f)) {
                Text("Hora (opcional)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(7.dp))

                OutlinedButton(
                    onClick = {
                        abrirSelectorHora(hora) { hora = it }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Texto
                    )
                ) {
                    Icon(Icons.Default.Schedule, null, tint = Morado)
                    Spacer(Modifier.width(7.dp))
                    Text(hora.ifBlank { "Sin hora" }, fontSize = 11.sp)
                }

                if (hora.isNotBlank()) {
                    TextButton(
                        onClick = { hora = "" },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Quitar hora", color = Morado, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(17.dp))

        Text("Prioridad", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("Baja", "Media", "Alta").forEach { opcion ->
                val seleccionada = prioridad == opcion

                OutlinedButton(
                    onClick = { prioridad = opcion },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (seleccionada) Color(0xFFEDE6FF) else Color.White,
                        contentColor = if (seleccionada) Morado else Color(0xFF898398)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (seleccionada) Morado else Color(0xFFE7E2EF)
                    )
                ) {
                    Text("⚑  $opcion", fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(17.dp))

        Text("Categoría", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        Box {
            OutlinedButton(
                onClick = { menuCategoria = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Texto
                )
            ) {
                Icon(Icons.Default.FavoriteBorder, null, tint = Morado)
                Spacer(Modifier.width(10.dp))
                Text(categoria, modifier = Modifier.weight(1f), fontSize = 12.sp)
                Icon(Icons.Default.KeyboardArrowDown, null)
            }

            DropdownMenu(
                expanded = menuCategoria,
                onDismissRequest = { menuCategoria = false },
                modifier = Modifier.background(Color.White)
            ) {
                categorias.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            categoria = opcion
                            menuCategoria = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(15.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Notifications, null, tint = Morado)
                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        "Recordatorio",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        if (recordatorio) "El mismo día, a las $horaRecordatorio"
                        else "Desactivado",
                        fontSize = 10.sp,
                        color = Color(0xFF898398)
                    )

                    if (recordatorio) {
                        TextButton(
                            onClick = {
                                abrirSelectorHora(horaRecordatorio) {
                                    horaRecordatorio = it
                                }
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Cambiar hora", color = Morado, fontSize = 11.sp)
                        }
                    }
                }

                Switch(
                    checked = recordatorio,
                    onCheckedChange = { recordatorio = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Morado
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            if (tareaEditar == null)
                "Borrador · aún no se incluye en tus tareas"
            else
                "Los cambios se aplicarán a esta tarea",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 10.sp,
            color = Color(0xFF9992A8)
        )

        Spacer(Modifier.height(14.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank()) {
                    guardarTarea(
                        nombre,
                        descripcion,
                        prioridad,
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(fechaMillis)),
                        categoria,
                        hora,
                        horaRecordatorio,
                        recordatorio
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Escribe el título de la tarea",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(49.dp),
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Morado)
        ) {
            Text(
                if (tareaEditar == null) "Guardar tarea  ✓" else "Guardar cambios  ✓",
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (mostrarCalendario) {
        val estadoFecha = rememberDatePickerState(
            initialSelectedDateMillis = fechaMillis
        )

        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        estadoFecha.selectedDateMillis?.let {
                            fechaMillis = it
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar", color = Morado)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }
}
