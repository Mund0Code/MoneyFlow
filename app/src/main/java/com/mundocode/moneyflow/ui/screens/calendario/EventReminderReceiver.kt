package com.mundocode.moneyflow.ui.screens.calendario

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mundocode.moneyflow.core.NotificationHelper

class EventReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra("TITULO") ?: "Recordatorio"
        val fecha = intent.getStringExtra("FECHA") ?: ""
        val id = intent.getIntExtra("ID", 0)

        NotificationHelper.showNotification(
            context,
            title = "Evento Hoy: $titulo",
            message = "No olvides tu evento de hoy ($fecha)",
            notificationId = id
        )
    }
}