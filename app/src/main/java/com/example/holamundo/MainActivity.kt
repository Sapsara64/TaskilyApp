
package com.example.holamundo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt


// ==========================================
// MODELO DE USUARIO
// ==========================================

data class Usuario(
    val id: Int,
    val nombre: String,
    val usuario: String,
    val password: String
)


// ==========================================
// ADMINISTRADOR DEL ARCHIVO JSON
// ==========================================

object UsuariosJSON {

    private const val ARCHIVO = "usuarios.json"

    // Leer usuarios del archivo JSON
    fun obtenerUsuarios(context: Context): MutableList<Usuario> {

        val lista = mutableListOf<Usuario>()

        try {
            val contenido = context.openFileInput(ARCHIVO)
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(contenido)
            val usuarios = json.getJSONArray("usuarios")

            for (i in 0 until usuarios.length()) {

                val objeto = usuarios.getJSONObject(i)

                lista.add(
                    Usuario(
                        id = objeto.getInt("id"),
                        nombre = objeto.getString("nombre"),
                        usuario = objeto.getString("usuario"),
                        password = objeto.getString("password")
                    )
                )
            }

        } catch (e: Exception) {
            // Si el archivo todavía no existe, no hay usuarios.
        }

        return lista
    }


    // Guardar usuarios en el archivo JSON
    fun guardarUsuarios(
        context: Context,
        lista: List<Usuario>
    ) {

        val usuariosJSON = JSONArray()

        lista.forEach { usuario ->

            val objeto = JSONObject()

            objeto.put("id", usuario.id)
            objeto.put("nombre", usuario.nombre)
            objeto.put("usuario", usuario.usuario)
            objeto.put("password", usuario.password)

            usuariosJSON.put(objeto)
        }

        val jsonPrincipal = JSONObject()
        jsonPrincipal.put("usuarios", usuariosJSON)

        context.openFileOutput(ARCHIVO, Context.MODE_PRIVATE)
            .bufferedWriter()
            .use {
                it.write(jsonPrincipal.toString(4))
            }
    }


    // Registrar un usuario nuevo
    fun registrarUsuario(
        context: Context,
        nombre: String,
        usuario: String,
        password: String
    ): String {

        val lista = obtenerUsuarios(context)

        // Comprobar si el usuario ya existe
        if (lista.any {
                it.usuario.equals(usuario, ignoreCase = true)
            }) {
            return "Ese nombre de usuario ya está registrado"
        }

        // Crear hash de la contraseña
        val passwordHash = BCrypt.hashpw(
            password,
            BCrypt.gensalt(12)
        )

        val nuevoUsuario = Usuario(
            id = (lista.maxOfOrNull { it.id } ?: 0) + 1,
            nombre = nombre,
            usuario = usuario,
            password = passwordHash
        )

        lista.add(nuevoUsuario)

        guardarUsuarios(context, lista)

        return "REGISTRO_CORRECTO"
    }


    // Verificar las credenciales del login
    fun verificarLogin(
        context: Context,
        usuario: String,
        password: String
    ): Boolean {

        val lista = obtenerUsuarios(context)

        val usuarioEncontrado = lista.find {
            it.usuario.equals(usuario, ignoreCase = true)
        } ?: return false

        return try {
            BCrypt.checkpw(
                password,
                usuarioEncontrado.password
            )
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}


// ==========================================
// MAIN ACTIVITY
// ==========================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                var pantalla by remember {
                    mutableStateOf("login")
                }

                var usuarioAutenticado by remember {
                    mutableStateOf("")
                }

                when (pantalla) {

                    "login" -> {

                        PantallaLogin(
                            onLoginCorrecto = { usuario ->

                                usuarioAutenticado = usuario
                                pantalla = "inicio"

                            },
                            irARegistro = {
                                pantalla = "registro"
                            }
                        )
                    }

                    "registro" -> {

                        PantallaRegistro(
                            onRegistroCorrecto = {
                                pantalla = "login"
                            },
                            volverLogin = {
                                pantalla = "login"
                            }
                        )
                    }

                    "inicio" -> {
                        Pantalla2()
                    }
                }
            }
        }
    }
}


// ==========================================
// PANTALLA DE LOGIN
// ==========================================

@Composable
fun PantallaLogin(
    onLoginCorrecto: (String) -> Unit,
    irARegistro: () -> Unit
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

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

                if (usuario.isBlank() || password.isBlank()) {

                    mensaje = "Completa todos los campos"

                } else {

                    scope.launch {
                        cargando = true
                        mensaje = ""

                        val accesoCorrecto = withContext(Dispatchers.IO) {
                            UsuariosJSON.verificarLogin(
                                context,
                                usuario.trim(),
                                password
                            )
                        }

                        cargando = false

                        if (accesoCorrecto) {
                            onLoginCorrecto(usuario.trim())
                        } else {
                            mensaje = "Usuario o contraseña incorrectos"
                        }
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (cargando) "Verificando..." else "Iniciar sesión"
            )
        }

        if (cargando) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text("¿No tienes cuenta?")

        TextButton(
            onClick = irARegistro
        ) {
            Text("Regístrate aquí")
        }
    }
}

