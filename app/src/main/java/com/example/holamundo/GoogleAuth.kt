
package com.example.holamundo

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun iniciarSesionConGoogle(
    activity: Activity
): Boolean {

    val credentialManager = CredentialManager.create(activity)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(
            activity.getString(R.string.default_web_client_id)
        )
        .setFilterByAuthorizedAccounts(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val resultado = credentialManager.getCredential(
            context = activity,
            request = request
        )

        val credential = resultado.credential

        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return false
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
                                ?: Exception("No se pudo iniciar sesión")
                        )
                    }
                }
        }

    } catch (e: Exception) {
        throw e
    }
}
