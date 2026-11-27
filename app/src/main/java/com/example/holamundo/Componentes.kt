package com.example.holamundo

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MenuInferior(
    pantallaSeleccionada: String,
    cambiarPantalla: (String) -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = pantallaSeleccionada == "Inicio",
            onClick = { cambiarPantalla("Inicio") },
            icon = { Text("⌂") },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = pantallaSeleccionada == "Agregar",
            onClick = { cambiarPantalla("Agregar") },
            icon = { Text("+") },
            label = { Text("Agregar") }
        )

        NavigationBarItem(
            selected = pantallaSeleccionada == "Calendario",
            onClick = { cambiarPantalla("Calendario") },
            icon = { Text("▦") },
            label = { Text("Calendario") }
        )
    }
}