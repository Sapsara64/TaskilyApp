
package com.example.holamundo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(volver: () -> Unit) {
    var apartadoSeleccionado by remember { mutableIntStateOf(0) }
    var segundos by remember { mutableIntStateOf(0) }
    var angulo by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) { delay(1000); segundos++ }
    }

    LaunchedEffect(Unit) {
        while (true) { delay(16); angulo = (angulo + 2f) % 360f }
    }

    val tiempo = String.format(Locale.getDefault(), "%02d:%02d:%02d", segundos / 3600, (segundos % 3600) / 60, segundos % 60)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Taskily") }, navigationIcon = {
                TextButton(onClick = volver) { Text("←") }
            })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = apartadoSeleccionado == 0,
                    onClick = { apartadoSeleccionado = 0 },
                    icon = { Text("⌂") },
                    label = { Text("Inicio") })

                NavigationBarItem(
                    selected = apartadoSeleccionado == 1,
                    onClick = { apartadoSeleccionado = 1 },
                    icon = { Text("⋯") },
                    label = { Text("Más") })

                NavigationBarItem(
                    selected = apartadoSeleccionado == 2,
                    onClick = { apartadoSeleccionado = 2 },
                    icon = { Text("●") },
                    label = { Text("Calendario") })
            }
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).padding(20.dp),
            contentAlignment = Alignment.Center) {
            when (apartadoSeleccionado) {
                0 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pantalla inicial",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(20.dp))
                    Image(painter = painterResource(R.drawable.imagen),
                        contentDescription = "Imagen q gira",
                        modifier = Modifier.size(280.dp).rotate(angulo))
                    Spacer(Modifier.height(20.dp))
                    Text("Tiempo: $tiempo")
                }

                1 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pantalla 2",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                }

                2 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pantalla 3",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
