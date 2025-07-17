package com.cosbell.service

import com.cosbell.entity.Horario
import com.cosbell.repository.HorarioRepository
import com.cosbell.repository.AppointmentRepository
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class HorarioService(
    private val horarioRepository: HorarioRepository,
    private val appointmentRepository: AppointmentRepository,
    private val servicioService: ServicioService,
    private val employeeServiceSpecialtyRepository: com.cosbell.repository.EmployeeServiceSpecialtyRepository
) {

    fun findAll(): List<Horario> = horarioRepository.findAll()

    fun findById(id: Long): Horario? = horarioRepository.findById(id).orElse(null)

    fun save(horario: Horario): Horario = horarioRepository.save(horario)

    fun deleteById(id: Long) = horarioRepository.deleteById(id)

    fun getAvailableTimes(
        date: LocalDate,
        servicioId: Long,
        employeeId: Long? = null
    ): List<LocalTime> {
        val dayOfWeek = date.dayOfWeek.toString()

        // Validar que el empleado ofrece el servicio
        if (employeeId != null) {
            // Inyectar el repositorio correspondiente
            if (!employeeServiceSpecialtyRepository.existsByEmployee_IdAndService_Id(employeeId, servicioId)) {
                return emptyList()
            }
        }
        // Buscar horarios específicos del empleado si se proporciona employeeId
        val horariosDelDia = if (employeeId != null) {
            horarioRepository.findByUser_IdAndDia(employeeId, dayOfWeek)
        } else {
            // Fallback a horarios por defecto si no se especifica empleado
            horarioRepository.findByDiaAndUserIsNull(dayOfWeek)
        }

        if (horariosDelDia.isEmpty()) {
            return emptyList() // No hay horarios configurados para este día
        }

        val servicio = servicioService.findById(servicioId)
            ?: throw IllegalArgumentException("Servicio no encontrado con ID: $servicioId")

        val serviceDurationMinutes = servicio.duration

        // Filtrar citas por empleado si se especifica
        val citasDelDia = if (employeeId != null) {
            appointmentRepository.findByFechaAndEmployee_Id(date, employeeId)
        } else {
            appointmentRepository.findByFecha(date)
        }

        val availableTimes = mutableListOf<LocalTime>()

        for (horario in horariosDelDia) {
            var currentTime = LocalTime.parse(horario.horaInicio)
            val endTime = LocalTime.parse(horario.horaFin)

            while (currentTime.plusMinutes(serviceDurationMinutes.toLong()).isBefore(endTime) ||
                   currentTime.plusMinutes(serviceDurationMinutes.toLong()).equals(endTime)) {

                var isSlotOccupied = false
                for (cita in citasDelDia) {
                    val citaStart = cita.hora
                    val citaEnd = citaStart.plusMinutes(cita.servicio.duration.toLong())

                    // Check for overlap
                    if ((currentTime.isBefore(citaEnd) && currentTime.plusMinutes(serviceDurationMinutes.toLong()).isAfter(citaStart))) {
                        isSlotOccupied = true
                        break
                    }
                }

                if (!isSlotOccupied) {
                    availableTimes.add(currentTime)
                }

                currentTime = currentTime.addMinutes(30) // Avanzar en intervalos de 30 minutos
            }
        }
        return availableTimes.sorted()
    }

    fun LocalTime.addMinutes(minutes: Long): LocalTime {
        return this.plus(minutes, ChronoUnit.MINUTES)
    }
}