
package com.example.holamundo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ==========================================
// PANTALLA DE REGISTRO
// ==========================================

@Composable
fun PantallaRegistro(
    onRegistroCorrecto: () -> Unit,
    volverLogin: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "CREAR CUENTA",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(25.dp))

        // NOMBRE
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Nombre",
                modifier = Modifier.width(90.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    mensaje = ""
                },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // USUARIO
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Usuario",
                modifier = Modifier.width(90.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    mensaje = ""
                },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CONTRASEÑA
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Contraseña",
                modifier = Modifier.width(90.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    mensaje = ""
                },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CONFIRMAR CONTRASEÑA
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Confirmar",
                modifier = Modifier.width(90.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = {
                    confirmarPassword = it
                    mensaje = ""
                },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BOTÓN CREAR CUENTA
        Button(
            onClick = {

                when {

                    nombre.isBlank() ||
                            usuario.isBlank() ||
                            password.isBlank() ||
                            confirmarPassword.isBlank() -> {

                        mensaje = "Completa todos los campos"
                    }

                    password.length < 4 -> {

                        mensaje = "La contraseña debe tener al menos 4 caracteres"
                    }

                    password != confirmarPassword -> {

                        mensaje = "Las contraseñas no coinciden"
                    }

                    else -> {

                        scope.launch {

                            cargando = true
                            mensaje = ""

                            val resultado = withContext(Dispatchers.IO) {

                                UsuariosJSON.registrarUsuario(
                                    context,
                                    nombre.trim(),
                                    usuario.trim(),
                                    password
                                )
                            }

                            cargando = false

                            if (resultado == "REGISTRO_CORRECTO") {

                                registroExitoso = true
                                mensaje = "¡Cuenta creada correctamente!"

                            } else {

                                mensaje = resultado
                            }
                        }
                    }
                }
            },
            enabled = !cargando && !registroExitoso,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (cargando) "Registrando..." else "Crear cuenta"
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // MENSAJE DE REGISTRO
        if (mensaje.isNotEmpty()) {

            Text(
                text = mensaje,
                color = if (registroExitoso)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text("¿Ya tienes cuenta?")

        TextButton(
            onClick = volverLogin
        ) {
            Text("Inicia sesión aquí")
        }

        // BOTÓN PARA REGRESAR AL LOGIN
        if (registroExitoso) {

            Button(
                onClick = onRegistroCorrecto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir al inicio de sesión")
            }
        }
    }
}
