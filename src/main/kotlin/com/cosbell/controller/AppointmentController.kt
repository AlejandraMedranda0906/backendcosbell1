package com.cosbell.controller

import com.cosbell.dto.AppointmentDto
import com.cosbell.entity.Appointment
import com.cosbell.service.AppointmentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate
import java.time.LocalTime
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import com.cosbell.service.UserService

@RestController
@RequestMapping("/api/citas")
class AppointmentController(
    private val appointmentService: AppointmentService,
    private val userService: UserService
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun create(@RequestBody request: AppointmentDto): Appointment =
        appointmentService.createAppointment(request)

    @PostMapping("/user")
    @PreAuthorize("hasRole('CLIENT')")
    fun createForUser(@RequestBody request: AppointmentDto, principal: Principal): Appointment {
        val user = userService.findByEmail(principal.name) ?: throw RuntimeException("Usuario no encontrado")
        return appointmentService.createAppointment(request.copy(userId = user.id!!))
    }

    @GetMapping("/user/{userId}")
    fun getAppointmentsByUserId(
        @PathVariable userId: Long,
        @RequestParam(required = false) month: Int?,
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) serviceId: Long?
    ): ResponseEntity<List<AppointmentDto>> {
        val citas = appointmentService.findByUserId(userId, month, year, serviceId)
        return ResponseEntity.ok(citas)
    }

    @GetMapping("/employee/me")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    fun getAppointmentsForAuthenticatedEmployee(
        principal: Principal,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fecha: LocalDate?,
        @RequestParam(required = false) servicioId: Long?,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<List<AppointmentDto>> {
        println("AppointmentController: Accediendo a /employee/me. Autenticación en contexto: ${SecurityContextHolder.getContext().authentication}")
        val user = userService.findByEmail(principal.name) ?: throw RuntimeException("Usuario no encontrado")
        val employeeId = user.id
        val citas = appointmentService.findByEmployeeId(employeeId, fecha, servicioId, userId)
        return ResponseEntity.ok(citas)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun cancelAppointment(@PathVariable id: Long): ResponseEntity<Void> {
        appointmentService.cancelAppointment(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getAppointmentById(@PathVariable id: Long): ResponseEntity<Appointment> {
        val cita = appointmentService.findAppointmentById(id)
        return if (cita != null) ResponseEntity.ok(cita) else ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun update(@PathVariable id: Long, @RequestBody request: AppointmentDto): ResponseEntity<Appointment> {
        return try {
            val updatedCita = appointmentService.updateAppointment(id, request)
            ResponseEntity.ok(updatedCita)
        } catch (e: Exception) {
            when (e.message) {
                "Cita no encontrada" -> ResponseEntity.notFound().build()
                "Servicio no encontrado al actualizar" -> ResponseEntity.badRequest().body(null)
                "Horario ocupado" -> ResponseEntity.badRequest().body(null)
                else -> ResponseEntity.internalServerError().body(null)
            }
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun updateStatus(@PathVariable id: Long, @RequestParam status: String): ResponseEntity<Appointment> {
        return try {
            val updated = appointmentService.updateAppointmentStatus(id, status)
            ResponseEntity.ok(updated)
        } catch (e: Exception) {
            when (e.message) {
                "Cita no encontrada" -> ResponseEntity.notFound().build()
                else -> ResponseEntity.internalServerError().body(null)
            }
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getAllAppointments(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fecha: LocalDate?,
        @RequestParam(required = false) employeeId: Long?,
        @RequestParam(required = false) servicioId: Long?,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<List<AppointmentDto>> {
        val citas = appointmentService.getAllAppointmentsDto(fecha, employeeId, servicioId, userId)
        return ResponseEntity.ok(citas)
    }
}