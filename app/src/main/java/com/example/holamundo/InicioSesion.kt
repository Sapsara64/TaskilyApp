
package com.example.holamundo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MoradoTaskily = Color(0xFF7650DF)
private val FondoTaskily = Color(0xFFF7F6FC)
private val TextoTaskily = Color(0xFF29243A)

@Composable
fun InicioSesion(
    onGoogleClick: () -> Unit,
    cargando: Boolean = false,
    mensajeError: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoTaskily)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo_nobg),
            contentDescription = "Logo de Taskily",
            modifier = Modifier.size(190.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¡Bienvenido a Taskily!",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = TextoTaskily
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Organiza tus tareas y mantén todo en orden.",
            fontSize = 15.sp,
            color = TextoTaskily.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onGoogleClick,
            enabled = !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MoradoTaskily,
                disabledContainerColor = MoradoTaskily.copy(alpha = 0.6f)
            )
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(23.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Continuar con Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        if (mensajeError != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensajeError,
                color = Color(0xFFB3261E),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Tus tareas, siempre contigo.",
            fontSize = 13.sp,
            color = TextoTaskily.copy(alpha = 0.6f)
        )
    }
}
