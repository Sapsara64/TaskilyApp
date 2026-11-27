
package com.example.holamundo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TarjetaTarea(
    tarea: Tarea,
    alHacerClic: () -> Unit
) {
    val colorPrioridad = when (tarea.prioridad) {
        "Alta" -> Color(0xFFFFE5E5)
        "Media" -> Color(0xFFFFF1D6)
        else -> Color(0xFFE2F5E9)
    }

    val textoPrioridad = when (tarea.prioridad) {
        "Alta" -> Color(0xFFD94A4A)
        "Media" -> Color(0xFFB87916)
        else -> Color(0xFF328653)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { alHacerClic() }
            .padding(16.dp)
    ) {
        Text(
            text = tarea.nombre,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF29243A)
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = tarea.descripcion.ifBlank { "Sin descripción" },
            fontSize = 13.sp,
            color = Color(0xFF888197),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // FECHA LÍMITE (NUEVO)
        Spacer(modifier = Modifier.height(9.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Fecha límite",
                tint = Color(0xFF888197),
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Fecha límite: ${tarea.fechaOrden}",
                fontSize = 11.sp,
                color = Color(0xFF888197),
                fontWeight = FontWeight.Medium
            )
        }

        // PRIORIDAD Y ESTADO (SIN CAMBIOS)
        Spacer(modifier = Modifier.height(13.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorPrioridad,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = tarea.prioridad,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    color = textoPrioridad,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = tarea.estado,
                fontSize = 11.sp,
                color = if (tarea.estado == "Completado") {
                    Color(0xFF328653)
                } else {
                    Color(0xFF7650DF)
                }
            )
        }
    }
}
