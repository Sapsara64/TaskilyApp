
package com.example.holamundo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                var usuario by remember {
                    mutableStateOf("")
                }

                var password by remember {
                    mutableStateOf("")
                }

                var mensaje by remember {
                    mutableStateOf("")
                }

                var entrar by remember {
                    mutableStateOf(false)
                }

                val userSara = "userSara"
                val passwordCorrecta = "123"

                if (entrar) {

                    Pantalla2()

                } else {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // LOGO TASKILY
                        Image(
                            painter = painterResource(
                                id = R.drawable.logo
                            ),
                            contentDescription = "Logo",
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(25.dp)
                        )

                        // TITULO
                        Text(
                            text = "Inicio de sesión",
                            fontSize = 28.sp,
                            color = Color(0xFF1565C0)
                        )

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        // CAMPO DE USUARIO
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Usuario:",
                                modifier = Modifier.width(95.dp),
                                fontSize = 14.sp,
                                color = Color.Black
                            )

                            OutlinedTextField(
                                value = usuario,
                                onValueChange = {
                                    usuario = it
                                    mensaje = ""
                                },
                                placeholder = {
                                    Text("Escribe tu usuario")
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        // CAMPO DE CONTRASEÑA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Contraseña:",
                                modifier = Modifier.width(95.dp),
                                fontSize = 14.sp,
                                color = Color.Black
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    mensaje = ""
                                },
                                placeholder = {
                                    Text("Escribe tu contraseña")
                                },
                                singleLine = true,
                                visualTransformation =
                                    PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Password
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        // MENSAJE DE ERROR
                        if (mensaje.isNotEmpty()) {
                            Text(
                                text = mensaje,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        // BOTON INGRESAR
                        Button(
                            onClick = {
                                if (
                                    usuario == userSara &&
                                    password == passwordCorrecta
                                ) {
                                    mensaje = ""
                                    entrar = true
                                } else {
                                    mensaje =
                                        "Usuario o contraseña incorrectos"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            )
                        ) {
                            Text("Ingresar")
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                    }
                }
            }
        }
    }
}
