package com.example.holamundo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(volver: () -> Unit) {

    var apartadoSeleccionado by remember {
        mutableStateOf(0)
    }

    val apartados = listOf("Inicio", "Más", "Calendario")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taskily") },
                navigationIcon = { TextButton(onClick = volver) { Text("←") } }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(// PANTALLA 1: INICIO
                    selected = apartadoSeleccionado == 0,
                    onClick = { apartadoSeleccionado = 0 },
                    icon = { Text("⌂") }, label = { Text("Inicio") }
                )
                NavigationBarItem(// PANTALLA 2:
                    selected = apartadoSeleccionado == 1,
                    onClick = { apartadoSeleccionado = 1 },
                    icon = { Text("⋯") }, label = { Text("Más") }
                )
                NavigationBarItem(// PANTALLA 3:
                    selected = apartadoSeleccionado == 2,
                    onClick = { apartadoSeleccionado = 2 },
                    icon = { Text("●") }, label = { Text("Calendario") }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {

            when (apartadoSeleccionado) {

                0 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pantalla inicial",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                1 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pantalla 2",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                2 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pantalla 3",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
