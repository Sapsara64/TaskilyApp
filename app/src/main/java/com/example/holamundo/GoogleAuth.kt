
package com.example.holamundo

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun iniciarSesionConGoogle(
    activity: Activity
): Boolean {

    try {
        val credentialManager = CredentialManager.create(activity)

        val webClientId =
            activity.getString(R.string.default_web_client_id)

        // Primer intento: buscar cuentas de Google disponibles.
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Si no hay credenciales disponibles, mostrar el flujo
        // explícito de inicio de sesión con Google.
        val resultado = try {
            credentialManager.getCredential(
                context = activity,
                request = request
            )
        } catch (e: NoCredentialException) {

            Log.w(
                "TASKILY_GOOGLE",
                "No se encontraron credenciales. Intentando acceso explícito con Google."
            )

            val signInOption =
                GetSignInWithGoogleOption.Builder(webClientId)
                    .build()

            val signInRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            credentialManager.getCredential(
                context = activity,
                request = signInRequest
            )
        }

        val credential = resultado.credential

        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            throw Exception("Google devolvió un tipo de credencial inesperado.")
        }

        val googleToken = GoogleIdTokenCredential.createFrom(
            credential.data
        )

        val firebaseCredential = GoogleAuthProvider.getCredential(
            googleToken.idToken,
            null
        )

        return suspendCancellableCoroutine { continuation ->

            FirebaseAuth.getInstance()
                .signInWithCredential(firebaseCredential)
                .addOnCompleteListener { tarea ->

                    if (tarea.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        continuation.resumeWithException(
                            tarea.exception
                                ?: Exception("No se pudo iniciar sesión en Firebase.")
                        )
                    }
                }
        }

    } catch (e: Exception) {

        Log.e(
            "TASKILY_GOOGLE",
            "Error: ${e.javaClass.simpleName}: ${e.message}",
            e
        )

        throw e
    }
}
