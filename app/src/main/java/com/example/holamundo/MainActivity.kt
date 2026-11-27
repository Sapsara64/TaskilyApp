package com.example.holamundo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var pantalla by remember { mutableStateOf("Inicio") }
            Scaffold(
                bottomBar = {
                    MenuInferior(
                        pantallaSeleccionada = pantalla,
                        cambiarPantalla = { pantalla = it }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFF7F6FC))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Taskily",
                        fontSize = 28.sp,
                        color = Color(0xFF29243A)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    when (pantalla) {
                        "Inicio" -> {
                            Text(
                                text = "Tus tareas",
                                fontSize = 20.sp,
                                color = Color(0xFF29243A)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            TarjetaTarea(
                                "Revisar propuesta",
                                "Hoy · 10:00 · Trabajo",
                                "Pendiente"
                            )
                            TarjetaTarea(
                                "Ordenar mis notas",
                                "Hoy · 08:30 · Personal",
                                "Completado"
                            )
                            TarjetaTarea(
                                "Enviar presupuesto",
                                "21 oct · 10:00 · Trabajo",
                                "Pendiente"
                            )
                        }
                        "Agregar" -> {
                            Text(text = "Agregar nueva tarea", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Aquí podrás agregar tus tareas.")
                        }
                        "Calendario" -> {
                            Text(text = "Calendario", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Aquí aparecerán tus fechas importantes.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaTarea(
    titulo: String,
    descripcion: String,
    estado: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    color = Color(0xFF29243A)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = descripcion,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = estado,
                fontSize = 11.sp,
                color = if (estado == "Completado")
                    Color(0xFF16866B)
                else
                    Color(0xFF7950E8)
            )
        }
    }
}

@Composable
fun MenuInferior(
    pantallaSeleccionada: String,
    cambiarPantalla: (String) -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // INICIO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    cambiarPantalla("Inicio")
                }
            ) {
                Text(
                    text = "⌂",
                    fontSize = 27.sp,
                    color = if (pantallaSeleccionada == "Inicio")
                        Color(0xFF7950E8)
                    else Color.Gray
                )

                Text(
                    text = "Inicio",
                    fontSize = 12.sp,
                    color = if (pantallaSeleccionada == "Inicio")
                        Color(0xFF7950E8)
                    else Color.Gray
                )
            }

            // AGREGAR
            FloatingActionButton(
                onClick = {
                    cambiarPantalla("Agregar")
                },
                containerColor = Color(0xFF7950E8),
                contentColor = Color.White,
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp
                )
            }

            // CALENDARIO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    cambiarPantalla("Calendario")
                }
            ) {
                Text(
                    text = "▦",
                    fontSize = 25.sp,
                    color = if (pantallaSeleccionada == "Calendario")
                        Color(0xFF7950E8)
                    else Color.Gray
                )

                Text(
                    text = "Calendario",
                    fontSize = 12.sp,
                    color = if (pantallaSeleccionada == "Calendario")
                        Color(0xFF7950E8)
                    else Color.Gray
                )
            }
        }
    }
}
