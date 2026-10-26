
package com.example.holamundo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var pantalla by remember { mutableStateOf(1) }

            var texto by remember { mutableStateOf("") }
            var titulo by remember { mutableStateOf("Hola") }
            var segundos by remember { mutableStateOf(5) }
            var colorFondo by remember {
                mutableStateOf(Color(0xFFBBDEFB))
            }
            var mostrarAlerta by remember { mutableStateOf(false) }
            var mostrarCupertino by remember { mutableStateOf(false) }

            val infiniteTransition =
                rememberInfiniteTransition(label = "rotacion ")

            val rotacion by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        2000,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rot"
            )

            if (pantalla == 1) {

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000L)
                        segundos--

                        if (segundos == 0) {
                            colorFondo = Color(
                                red = Random.nextFloat(),
                                green = Random.nextFloat(),
                                blue = Random.nextFloat(),
                                alpha = 1f
                            )
                            segundos = 5
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorFondo),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(text = "$segundos", fontSize = 24.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = titulo, fontSize = 30.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.Black.copy(alpha = 0.5f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Fondo del Stack", color = Color.White)
                            }

                            Text(
                                "Imagen ",
                                color = Color.Yellow,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp)
                            )

                            Image(
                                painter = painterResource(
                                    id = R.drawable.imagen
                                ),
                                contentDescription = "Imagen",
                                modifier = Modifier
                                    .size(300.dp)
                                    .rotate(rotacion)
                                    .align(Alignment.Center)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = texto,
                            onValueChange = { texto = it },
                            label = { Text("Mi App") },
                            placeholder = {
                                Text("Escribe aquí...")
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(onClick = {
                            if (texto.isNotBlank()) titulo = texto
                        }
                        ) {Text("Cambiar texto") }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {
                            Button(onClick = { mostrarAlerta = true }
                            ) {
                                Text("Alert")
                            }

                            Button(
                                onClick = { mostrarCupertino = true }
                            ) {
                                Text("Cupertino")
                            }
                        }

                        // BOTON AGREGADO PARA CAMBIAR DE PANTALLA
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { pantalla = 2 }
                        ) {
                            Text("Ir a Pantalla 2")
                        }
                    }
                }
            } else {
                Pantalla2(
                    onRegresar = { pantalla = 1 }
                )
            }

            if (mostrarAlerta) {
                AlertDialog(
                    onDismissRequest = {
                        mostrarAlerta = false
                    },
                    title = {
                        Text("Alerta")
                    },
                    text = {
                        Text("SOy una alerta hola")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mostrarAlerta = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                mostrarAlerta = false
                            }
                        ) { Text("Cancelar") }
                    }
                )
            }

            if (mostrarCupertino) {
                AlertDialog(
                    onDismissRequest = {
                        mostrarCupertino = false
                    },
                    shape = RoundedCornerShape(14.dp),
                    containerColor = Color(0xFFF2F2F7),
                    title = {
                        Text(
                            "Cupertino",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    text = {
                        Text(
                            "Hola soy el cupertino.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = {
                                    mostrarCupertino = false
                                }
                            ) {
                                Text(
                                    "Cancelar",
                                    color = Color(0xFF007AFF)
                                )
                            }

                            TextButton(
                                onClick = {
                                    mostrarCupertino = false
                                    titulo = "¡Mi app!"
                                }
                            ) {
                                Text(
                                    "Aceptar",
                                    color = Color(0xFF007AFF),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
