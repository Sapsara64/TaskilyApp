package com.example.holamundo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.mindrot.jbcrypt.BCrypt
import kotlin.time.Duration.Companion.milliseconds

private const val USUARIO_VALIDO = "userSara"
private const val PASSWORD_VALIDA = "123"
private const val TAG_LOGIN = "InicioSesion"

private val passwordHash: String by lazy {
    BCrypt.hashpw(PASSWORD_VALIDA, BCrypt.gensalt(12))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                var mostrarPantalla2 by remember {
                    mutableStateOf(false)
                }
                var usuarioAutenticado by remember {
                    mutableStateOf("")
                }

                if (mostrarPantalla2) {
                    // Llama a la función de Pantalla2.kt
                    Pantalla2()
                } else {
                    PantallaLogin(
                        onLoginCorrecto = { usuario ->
                            usuarioAutenticado = usuario
                            mostrarPantalla2 = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaLogin(
    onLoginCorrecto: (String) -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "User",
                modifier = Modifier.width(90.dp)
            )

            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    mensaje = ""
                },
                label = { Text("Enter user") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password",
                modifier = Modifier.width(90.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    mensaje = ""
                },
                label = { Text("Enter password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                cargando = true
                mensaje = ""
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        if (cargando) {
            LaunchedEffect(cargando) {
                delay(700.milliseconds)

                val usuarioIngresado = usuario.trim()
                val hashGuardado = passwordHash
                val accesoCorrecto = verificarCredenciales(
                    usuarioIngresado,
                    password
                )

                Log.i(TAG_LOGIN, "Usuario: $usuarioIngresado")
                Log.i(TAG_LOGIN, "Contraseña ingresada: $password")
                Log.i(TAG_LOGIN, "Hash BCrypt guardado: $hashGuardado")
                Log.i(
                    TAG_LOGIN,
                    "Resultado: ${
                        if (accesoCorrecto) {
                            "ACCESO CORRECTO"
                        } else {
                            "ACCESO DENEGADO"
                        }
                    }"
                )

                cargando = false

                if (accesoCorrecto) {
                    onLoginCorrecto(usuarioIngresado)
                } else {
                    mensaje = "Usuario o contraseña incorrectos"
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = mensaje)
        }
    }
}

private fun verificarCredenciales(
    usuario: String,
    password: String
): Boolean {
    if (usuario != USUARIO_VALIDO) {
        return false
    }

    return try {
        BCrypt.checkpw(password, passwordHash)
    } catch (e: IllegalArgumentException) {
        false
    }
}
