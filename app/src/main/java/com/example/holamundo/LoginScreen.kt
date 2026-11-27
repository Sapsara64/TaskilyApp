
package com.example.holamundo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image

@Composable
fun LoginScreen(
    alIniciarSesion: () -> Unit
) {
    val morado = Color(0xFF7650DF)
    val fondo = Color(0xFFF7F6FC)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_main_nobg),
                contentDescription = "Logo de Taskily",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¡Organiza tu día!",
                fontSize = 27.sp,
                color = Color(0xFF29243A)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tus tareas, siempre contigo.",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = alIniciarSesion,
                modifier = Modifier
                    .width(280.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = morado
                )
            ) {
                Text(
                    text = "Continuar con Google",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Inicia sesión para acceder a Taskily",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
