package com.cosbell.scheduler

import com.cosbell.service.AppointmentService
import com.cosbell.service.NotificationService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ReminderScheduler(
    private val appointmentService: AppointmentService,
    private val notificationService: NotificationService
) {

    // Ejecuta esta tarea cada hora (3600000 milisegundos)
    // Puedes ajustar la frecuencia según sea necesario
    @Scheduled(fixedRate = 3600000)
    fun sendAppointmentReminders() {
        println("Scheduler: Buscando citas para enviar recordatorios...")
        val appointmentsToRemind = appointmentService.findAppointmentsForReminder()

        if (appointmentsToRemind.isNotEmpty()) {
            println("Scheduler: Encontradas ${appointmentsToRemind.size} citas para recordatorio.")
            appointmentsToRemind.forEach { appointment ->
                try {
                    // Envía el recordatorio por correo
                    notificationService.sendAppointmentReminderEmail(appointment)
                    // Opcional: registrar también el enlace de WhatsApp si es aplicable para recordatorios
                    // notificationService.logWhatsAppNotificationLink(appointment)

                    // Marca la cita como recordatorio enviado
                    appointmentService.markReminderSent(appointment.id)
                    println("Scheduler: Recordatorio enviado y marcado para la cita ID: ${appointment.id}")
                } catch (e: Exception) {
                    System.err.println("Scheduler: Error al enviar recordatorio para la cita ID: ${appointment.id}. Error: ${e.message}")
                }
            }
        } else {
            println("Scheduler: No se encontraron citas pendientes de recordatorio.")
        }
    }
} 