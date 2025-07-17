package com.cosbell.service

import com.cosbell.entity.Rating
import com.cosbell.repository.RatingRepository
import com.cosbell.repository.AppointmentRepository
import com.cosbell.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RatingService(
    private val ratingRepository: RatingRepository,
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository
) {

    fun createRating(appointmentId: Long, userId: Long, ratingValue: Int, comment: String?): Rating {
        // 1. Validar que la cita existe
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { Exception("Cita no encontrada con ID: $appointmentId") }

        // 2. Validar que el usuario que valora sea el mismo que el de la cita
        if (appointment.userId != userId) {
            throw Exception("No tienes permiso para valorar esta cita.")
        }

        // 3. Validar que la cita ya haya ocurrido o esté en un estado final (CANCELLED o COMPLETED)
        val appointmentDateTime = LocalDateTime.of(appointment.fecha, appointment.hora)
        val isPastOrFinalized = appointmentDateTime.isBefore(LocalDateTime.now()) || 
                                appointment.status == "CANCELLED" || 
                                appointment.status == "COMPLETED"

        if (!isPastOrFinalized) {
            throw Exception("Solo se puede valorar una cita que ya ha ocurrido, ha sido cancelada o completada.")
        }

        // 4. Validar que no se haya valorado ya esta cita
        if (ratingRepository.existsByAppointmentId(appointmentId)) {
            throw Exception("Esta cita ya ha sido valorada.")
        }

        // 5. Validar que la calificación sea entre 1 y 5
        if (ratingValue < 1 || ratingValue > 5) {
            throw Exception("La calificación debe ser entre 1 y 5.")
        }

        val newRating = Rating(
            appointmentId = appointmentId,
            userId = userId,
            employeeId = appointment.employee.id,
            rating = ratingValue,
            comment = comment,
            createdAt = LocalDateTime.now(),
            approved = false, // Por defecto no aprobado, requiere moderación
            active = true
        )
        return ratingRepository.save(newRating)
    }

    fun getRatingByAppointmentId(appointmentId: Long): Rating? {
        return ratingRepository.findByAppointmentId(appointmentId)
    }

    // Este método se usaría para mostrar valoraciones públicas después de la moderación
    fun getApprovedRatingsByEmployeeId(employeeId: Long): List<Rating> {
        return ratingRepository.findAll().filter { 
            it.employeeId == employeeId && it.approved && it.active 
        }
    }

    fun getRatingsByServiceId(serviceId: Long): List<Rating> {
        val appointments = appointmentRepository.findByServicio_Id(serviceId)
        val appointmentIds = appointments.map { it.id }
        if (appointmentIds.isEmpty()) return emptyList()
        return ratingRepository.findAllByAppointmentIdIn(appointmentIds)
            .filter { it.active }
    }

    // Método para la moderación (ej. desde un panel de administrador)
    fun approveRating(ratingId: Long): Rating {
        val rating = ratingRepository.findById(ratingId)
            .orElseThrow { Exception("Valoración no encontrada con ID: $ratingId") }
        
        val updatedRating = rating.copy(approved = true)
        return ratingRepository.save(updatedRating)
    }

    // Método para la desactivación/eliminación lógica (ej. desde un panel de administrador)
    fun deactivateRating(ratingId: Long): Rating {
        val rating = ratingRepository.findById(ratingId)
            .orElseThrow { Exception("Valoración no encontrada con ID: $ratingId") }
        
        val updatedRating = rating.copy(active = false)
        return ratingRepository.save(updatedRating)
    }
} 