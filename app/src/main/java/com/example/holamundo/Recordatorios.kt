package com.example.holamundo

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val CANAL_RECORDATORIOS = "taskily_recordatorios"

fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            CANAL_RECORDATORIOS,
            "Recordatorios de Taskily",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos de tus tareas pendientes"
        }

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.createNotificationChannel(canal)
    }
}

fun programarRecordatorio(context: Context, tarea: Tarea) {
    cancelarRecordatorio(context, tarea.id)

    if (!tarea.recordatorioActivo || tarea.estado == "Completado") {
        return
    }

    try {
        val fecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).parse(tarea.fechaOrden) ?: return

        val partesHora = tarea.horaRecordatorio.split(":")
        val hora = partesHora.getOrNull(0)?.toIntOrNull() ?: 9
        val minuto = partesHora.getOrNull(1)?.toIntOrNull() ?: 0

        val calendario = Calendar.getInstance().apply {
            time = fecha
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendario.timeInMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(context, RecordatorioReceiver::class.java).apply {
            putExtra("idTarea", tarea.id)
            putExtra("nombreTarea", tarea.nombre)
            putExtra("descripcionTarea", tarea.descripcion)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tarea.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendario.timeInMillis,
            pendingIntent
        )

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun cancelarRecordatorio(context: Context, idTarea: Int) {
    val intent = Intent(context, RecordatorioReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        idTarea,
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )

    if (pendingIntent != null) {
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}

class RecordatorioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        crearCanalNotificaciones(context)

        val idTarea = intent.getIntExtra("idTarea", 0)
        val nombre = intent.getStringExtra("nombreTarea") ?: "Tarea pendiente"
        val descripcion = intent.getStringExtra("descripcionTarea") ?: ""

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CANAL_RECORDATORIOS)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }

        val notificacion = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Taskily · $nombre")
            .setContentText(
                descripcion.ifBlank { "Es hora de revisar tu tarea." }
            )
            .setStyle(
                Notification.BigTextStyle().bigText(
                    descripcion.ifBlank { "Es hora de revisar tu tarea." }
                )
            )
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.notify(idTarea, notificacion)
    }
}
