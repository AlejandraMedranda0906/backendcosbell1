package com.cosbell.service

import com.cosbell.entity.Appointment
import com.cosbell.repository.UserRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.mail.SimpleMailMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.util.UriComponentsBuilder

@Service
class NotificationService(
    private val mailSender: JavaMailSender,
    private val userRepository: UserRepository,
    private val whatsAppService: WhatsAppService
) {

    fun sendAppointmentConfirmationEmail(appointment: Appointment) {
        val user = userRepository.findById(appointment.userId)
            .orElseThrow { Exception("Usuario no encontrado para el ID: ${appointment.userId}") }

        val message = SimpleMailMessage()
        message.setTo(appointment.email)
        message.setSubject("Confirmación de Cita Cosbell SPA")
        message.setText("""
            ¡Hola ${user.name}!

            Tu cita ha sido confirmada con éxito.
            Detalles de la cita:
            Servicio: ${appointment.servicio.name}
            Fecha: ${appointment.fecha}
            Hora: ${appointment.hora}
            Profesional: ${appointment.employee.name}

            ¡Esperamos verte pronto!
            Atentamente,
            Cosbell SPA
        """.trimIndent())
        mailSender.send(message)
    }

    fun sendAppointmentReminderEmail(appointment: Appointment) {
        val user = userRepository.findById(appointment.userId)
            .orElseThrow { Exception("Usuario no encontrado para el ID: ${appointment.userId}") }

        val message = SimpleMailMessage()
        message.setTo(appointment.email)
        message.setSubject("Recordatorio de Cita Cosbell SPA")
        message.setText("""
            ¡Hola ${user.name}!

            Te recordamos tu próxima cita con Cosbell SPA.
            Detalles de la cita:
            Servicio: ${appointment.servicio.name}
            Fecha: ${appointment.fecha}
            Hora: ${appointment.hora}
            Profesional: ${appointment.employee.name}

            ¡Te esperamos!
            Atentamente,
            Cosbell SPA
        """.trimIndent())
        mailSender.send(message)
    }

    fun logWhatsAppNotificationLink(appointment: Appointment) {
        val user = userRepository.findById(appointment.userId)
            .orElseThrow { Exception("Usuario no encontrado para el ID: ${appointment.userId}") }

        val phoneNumber = "${appointment.phone}" // Asegúrate de que el número incluya el código de país (ej. +521234567890)
        val messageText = "Hola ${user.name}, tu cita con Cosbell SPA para el servicio de ${appointment.servicio.name} el ${appointment.fecha} a las ${appointment.hora} con ${appointment.employee.name} ha sido confirmada. ¡Esperamos verte pronto!"

        val whatsappLink = UriComponentsBuilder.fromUriString("https://wa.me/")
            .path(phoneNumber)
            .queryParam("text", messageText)
            .build()
            .toUriString()

        println("*** ENLACE DE WHATSAPP PARA NOTIFICACIÓN MANUAL ***")
        println("Cliente: ${user.name} (${appointment.email})")
        println("Número de teléfono: ${appointment.phone}")
        println("Enlace para enviar WhatsApp: $whatsappLink")
        println("****************************************************")
    }

    fun sendPasswordResetEmail(email: String, name: String, resetLink: String) {
        val subject = "Recuperación de contraseña - Cosbell SPA"
        val body = """
            ¡Hola $name!

            Hemos recibido una solicitud para restablecer tu contraseña.
            Haz clic en el siguiente enlace para definir una nueva contraseña:
            $resetLink

            Si no solicitaste este cambio, puedes ignorar este correo.

            Atentamente,
            Cosbell SPA
        """.trimIndent()
        println("\n--- ENVÍO DE CORREO DE RECUPERACIÓN ---")
        println("Para: $email")
        println("Asunto: $subject")
        println("Cuerpo:\n$body")
        println("--------------------------------------\n")
        val message = SimpleMailMessage()
        message.setTo(email)
        message.setSubject(subject)
        message.setText(body)
        mailSender.send(message)
    }

    fun sendAppointmentConfirmationWhatsApp(appointment: Appointment) {
        val user = try { userRepository.findById(appointment.userId).orElse(null) } catch (e: Exception) { null }
        val nombre = user?.name ?: "cliente"
        val negocio = "Cosbell SPA"
        val servicio = appointment.servicio.name
        val fecha = appointment.fecha
        val hora = appointment.hora
        val empleado = appointment.employee.name

        val body = """
        Hola $nombre, tu cita en $negocio fue confirmada.
        Servicio: $servicio
        Fecha: $fecha  Hora: $hora
        Profesional: $empleado
        Si necesitas reprogramar, responde este mensaje.
    """.trimIndent()

        whatsAppService.sendText(appointment.phone, body)
    }

    fun sendAppointmentReminderWhatsApp(appointment: Appointment) {
        val user = try { userRepository.findById(appointment.userId).orElse(null) } catch (e: Exception) { null }
        val nombre = user?.name ?: "cliente"
        val negocio = "Cosbell SPA"
        val servicio = appointment.servicio.name
        val fecha = appointment.fecha
        val hora = appointment.hora

        val body = """
        Recordatorio: $nombre, tu cita en $negocio es hoy.
        Servicio: $servicio
        Fecha: $fecha  Hora: $hora
        Te esperamos.
    """.trimIndent()

        whatsAppService.sendText(appointment.phone, body)
    }

} 