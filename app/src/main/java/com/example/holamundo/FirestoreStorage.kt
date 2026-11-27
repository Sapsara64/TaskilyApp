
package com.example.holamundo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private val db = FirebaseFirestore.getInstance()

private fun coleccionTareas(uid: String) =
    db.collection("usuarios")
        .document(uid)
        .collection("tareas")

private fun tareaAMapa(tarea: Tarea): Map<String, Any> = mapOf(
    "nombre" to tarea.nombre,
    "descripcion" to tarea.descripcion,
    "estado" to tarea.estado,
    "prioridad" to tarea.prioridad,
    "fechaOrden" to tarea.fechaOrden,
    "categoria" to tarea.categoria,
    "hora" to tarea.hora,
    "horaRecordatorio" to tarea.horaRecordatorio,
    "recordatorioActivo" to tarea.recordatorioActivo,
    "favorita" to tarea.favorita
)

private fun documentoATarea(
    id: Int,
    cloudId: String,
    datos: Map<String, Any>
): Tarea {
    return Tarea(
        id = id,
        cloudId = cloudId,
        nombre = datos["nombre"] as? String ?: "",
        descripcion = datos["descripcion"] as? String ?: "",
        estado = datos["estado"] as? String ?: "Pendiente",
        prioridad = datos["prioridad"] as? String ?: "Media",
        fechaOrden = datos["fechaOrden"] as? String ?: "",
        categoria = datos["categoria"] as? String ?: "",
        hora = datos["hora"] as? String ?: "",
        horaRecordatorio = datos["horaRecordatorio"] as? String ?: "09:00",
        recordatorioActivo = datos["recordatorioActivo"] as? Boolean ?: true,
        favorita = datos["favorita"] as? Boolean ?: false
    )
}

/**
 * Si Firestore ya tiene tareas, esas son las que se cargan.
 * Si la nube está vacía, sube las tareas locales existentes.
 */
suspend fun sincronizarTareasFirestore(
    uid: String,
    tareasLocales: List<Tarea>
): List<Tarea> {
    val coleccion = coleccionTareas(uid)
    val documentos = coleccion.get().await()

    if (documentos.isEmpty && tareasLocales.isNotEmpty()) {
        val lote = db.batch()

        tareasLocales.forEach { tarea ->
            lote.set(
                coleccion.document(tarea.cloudId),
                tareaAMapa(tarea)
            )
        }

        lote.commit().await()
    }

    val documentosActualizados = coleccion.get().await()

    return documentosActualizados.documents.mapIndexedNotNull { indice, documento ->
        val datos = documento.data ?: return@mapIndexedNotNull null
        documentoATarea(
            id = indice + 1,
            cloudId = documento.id,
            datos = datos
        )
    }
}

suspend fun guardarTareaFirestore(uid: String, tarea: Tarea) {
    coleccionTareas(uid)
        .document(tarea.cloudId)
        .set(tareaAMapa(tarea))
        .await()
}

suspend fun eliminarTareaFirestore(uid: String, cloudId: String) {
    coleccionTareas(uid)
        .document(cloudId)
        .delete()
        .await()
}
