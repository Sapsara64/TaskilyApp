
package com.example.holamundo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

private const val ARCHIVO_TAREAS = "tareas.json"

fun guardarTareas(context: Context, tareas: List<Tarea>) {

    val jsonArray = JSONArray()

    tareas.forEach { tarea ->

        val objeto = JSONObject().apply {
            put("id", tarea.id)
            put("nombre", tarea.nombre)
            put("descripcion", tarea.descripcion)
            put("estado", tarea.estado)
            put("prioridad", tarea.prioridad)
            put("fechaOrden", tarea.fechaOrden)
            put("categoria", tarea.categoria)
            put("hora", tarea.hora)
            put("horaRecordatorio", tarea.horaRecordatorio)
            put("recordatorioActivo", tarea.recordatorioActivo)
            put("favorita", tarea.favorita)
            put("cloudId", tarea.cloudId)
        }

        jsonArray.put(objeto)
    }

    val archivo = File(context.filesDir, ARCHIVO_TAREAS)

    archivo.writeText(jsonArray.toString(4))
}

fun cargarTareas(context: Context): List<Tarea> {

    val archivo = File(context.filesDir, ARCHIVO_TAREAS)

    if (!archivo.exists()) return emptyList()

    return try {

        val jsonArray = JSONArray(archivo.readText())

        List(jsonArray.length()) { indice ->

            val objeto = jsonArray.getJSONObject(indice)

            Tarea(
                id = objeto.getInt("id"),
                nombre = objeto.getString("nombre"),
                descripcion = objeto.optString("descripcion", ""),
                estado = objeto.optString("estado", "Pendiente"),
                prioridad = objeto.optString("prioridad", "Media"),
                fechaOrden = objeto.optString("fechaOrden", ""),
                categoria = objeto.optString("categoria", "Otros"),
                hora = objeto.optString("hora", ""),
                horaRecordatorio = objeto.optString("horaRecordatorio", "09:00"),
                recordatorioActivo = objeto.optBoolean("recordatorioActivo", true),
                favorita = objeto.optBoolean("favorita", false),
                cloudId = objeto.optString(
                    "cloudId",
                    UUID.randomUUID().toString()
                )
            )
        }

    } catch (e: Exception) {
        emptyList()
    }
}
