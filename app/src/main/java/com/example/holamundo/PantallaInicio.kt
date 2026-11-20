package com.example.holamundo
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(volver: () -> Unit) {
    var apartadoSeleccionado by remember { mutableIntStateOf(0) }
    var segundos by remember { mutableIntStateOf(0) }
    var angulo by remember { mutableFloatStateOf(0f) }

    // Posición y velocidad de la imagen
    var posicionX by remember { mutableFloatStateOf(0f) }
    var posicionY by remember { mutableFloatStateOf(0f) }
    var velocidadX by remember { mutableFloatStateOf(0f) }
    var velocidadY by remember { mutableFloatStateOf(0f) }

    val context = LocalContext.current
    val density = LocalDensity.current

    // Acceso al acelerómetro
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]

                // Movimiento según la inclinación del celular
                velocidadX += -x * 0.8f
                velocidadY += y * 0.8f
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (acelerometro != null) {
            sensorManager.registerListener(
                listener,
                acelerometro,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Cronómetro
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            segundos++
        }
    }

    val tiempo = String.format(
        Locale.getDefault(), "%02d:%02d:%02d",
        segundos / 3600, (segundos % 3600) / 60, segundos % 60
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taskily") },
                navigationIcon = {
                    TextButton(onClick = volver) {
                        Text("←")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = apartadoSeleccionado == 0,
                    onClick = { apartadoSeleccionado = 0 },
                    icon = { Text("⌂") },
                    label = { Text("Inicio") }
                )

                NavigationBarItem(
                    selected = apartadoSeleccionado == 1,
                    onClick = { apartadoSeleccionado = 1 },
                    icon = { Text("⋯") },
                    label = { Text("Más") }
                )

                NavigationBarItem(
                    selected = apartadoSeleccionado == 2,
                    onClick = { apartadoSeleccionado = 2 },
                    icon = { Text("●") },
                    label = { Text("Calendario") }
                )
            }
        }
    ) { padding ->

        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val anchoPantalla = with(density) { maxWidth.toPx() }
            val altoPantalla = with(density) { maxHeight.toPx() }
            val tamanoImagen = with(density) { 280.dp.toPx() }

            // Límites considerando el tamaño completo de la imagen
            val limiteX = ((anchoPantalla - tamanoImagen) / 2).coerceAtLeast(0f)
            val limiteY = ((altoPantalla - tamanoImagen) / 2).coerceAtLeast(0f)

            // Movimiento y rebote
            LaunchedEffect(limiteX, limiteY) {
                while (true) {
                    delay(16)

                    if (apartadoSeleccionado == 0) {
                        posicionX += velocidadX
                        posicionY += velocidadY

                        // Rebote horizontal
                        if (posicionX > limiteX) {
                            posicionX = limiteX
                            velocidadX = -abs(velocidadX) * 0.7f
                        }

                        if (posicionX < -limiteX) {
                            posicionX = -limiteX
                            velocidadX = abs(velocidadX) * 0.7f
                        }

                        // Rebote vertical
                        if (posicionY > limiteY) {
                            posicionY = limiteY
                            velocidadY = -abs(velocidadY) * 0.7f
                        }

                        if (posicionY < -limiteY) {
                            posicionY = -limiteY
                            velocidadY = abs(velocidadY) * 0.7f
                        }

                        // Fricción
                        velocidadX *= 0.96f
                        velocidadY *= 0.96f

                        // Giro constante
                        angulo = (angulo + 2f) % 360f
                    }
                }
            }

            when (apartadoSeleccionado) {
                0 -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Pantalla inicial",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(20.dp))
                        Text("Tiempo: $tiempo")
                    }

                    Image(
                        painter = painterResource(R.drawable.imagen),
                        contentDescription = "Imagen que gira y rebota con el movimiento del celular",
                        modifier = Modifier
                            .size(280.dp)
                            .align(Alignment.Center)
                            .graphicsLayer {
                                translationX = posicionX
                                translationY = posicionY
                                rotationZ = angulo
                            }
                    )
                }

                1 -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Pantalla 2",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                }

                2 -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Pantalla 3",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
