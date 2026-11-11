
package com.example.holamundo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.platform.LocalContext
import org.mindrot.jbcrypt.BCrypt

data class Usuario(
    val nombre: String,
    val usuario: String,
    val password: String
)

object UsuariosJSON {

    private const val ARCHIVO888 = "usuarios.json"

    fun obtenerUsuarios(
        context: android.content.Context
    ): MutableList<Usuario> {

        val archivo = java.io.File(context.filesDir, ARCHIVO888)

        if (!archivo.exists()) return mutableListOf()

        return try {
            val json = org.json.JSONArray(archivo.readText())

            MutableList(json.length()) { i ->
                val obj = json.getJSONObject(i)

                Usuario(
                    nombre = obj.getString("nombre"),
                    usuario = obj.getString("usuario"),
                    password = obj.getString("password")
                )
            }
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun registrarUsuario(
        context: android.content.Context,
        nombre: String,
        usuario: String,
        password: String
    ): String {

        val usuarios = obtenerUsuarios(context)

        if (usuarios.any { it.usuario == usuario }) {
            return "Este usuario ya existe"
        }

        val passwordHash = BCrypt.hashpw(
            password,
            BCrypt.gensalt()
        )

        usuarios.add(
            Usuario(nombre, usuario, passwordHash)
        )

        return if (guardarUsuarios(context, usuarios)) {
            "REGISTRO_CORRECTO"
        } else {
            "Error al guardar el usuario"
        }
    }

    fun validarUsuario(
        context: android.content.Context,
        usuario: String,
        password: String
    ): Boolean {

        val usuarios = obtenerUsuarios(context)

        val encontrado = usuarios.find {
            it.usuario == usuario
        }

        return encontrado != null &&
                BCrypt.checkpw(password, encontrado.password)
    }

    private fun guardarUsuarios(
        context: android.content.Context,
        usuarios: List<Usuario>
    ): Boolean {

        return try {
            val json = org.json.JSONArray()

            usuarios.forEach { usuario ->
                val obj = org.json.JSONObject()

                obj.put("nombre", usuario.nombre)
                obj.put("usuario", usuario.usuario)
                obj.put("password", usuario.password)

                json.put(obj)
            }

            val archivo = java.io.File(
                context.filesDir,
                ARCHIVO888
            )

            archivo.writeText(json.toString())
            true

        } catch (e: Exception) {
            false
        }
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                var pantalla by remember {
                    mutableStateOf("login")
                }

                var usuarioActual by remember {
                    mutableStateOf("")
                }

                when (pantalla) {

                    "login" -> PantallaLogin(
                        onLogin = { usuario ->
                            usuarioActual = usuario
                            pantalla = "menu"
                        },
                        onRegistro = {
                            pantalla = "registro"
                        }
                    )

                    "registro" -> PantallaRegistro(
                        onRegistroCorrecto = {
                            pantalla = "login"
                        },
                        volverLogin = {
                            pantalla = "login"
                        }
                    )

                    "menu" -> PantallaMenu(
                        onPantalla = { destino ->
                            pantalla = destino
                        },
                        onCerrarSesion = {
                            usuarioActual = ""
                            pantalla = "login"
                        }
                    )

                    "pantalla2" -> Pantalla2()

                    "api" -> PantallaPublicaciones(
                        usuario = usuarioActual,
                        onCerrarSesion = {
                            pantalla = "menu"
                        }
                    )

                    "inicio" -> PantallaInicio(
                        volver = {
                            pantalla = "menu"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaMenu(
    onPantalla: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "¡Bienvenido!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Selecciona a qué pantalla deseas ir")

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onPantalla("pantalla2") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pantalla 2")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { onPantalla("api") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pantalla con API")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { onPantalla("inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pantalla Inicio")
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun PantallaLogin(
    onLogin: (String) -> Unit,
    onRegistro: () -> Unit
) {

    val context = LocalContext.current

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(
                id = R.drawable.logo
            ),
            contentDescription = "Logo",
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Usuario",
                modifier = Modifier.width(100.dp)
            )

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contraseña",
                modifier = Modifier.width(100.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (UsuariosJSON.validarUsuario(
                        context,
                        usuario,
                        password
                    )
                ) {
                    mensaje = ""
                    onLogin(usuario)
                } else {
                    mensaje = "Usuario o contraseña incorrectos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        TextButton(
            onClick = onRegistro
        ) {
            Text("Crear cuenta")
        }

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
