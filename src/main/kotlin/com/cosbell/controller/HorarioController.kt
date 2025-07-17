package com.cosbell.controller

import com.cosbell.dto.HorarioRequest
import com.cosbell.entity.Horario
import com.cosbell.service.HorarioService
import com.cosbell.service.UserSecurityService // Importar UserSecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime

@RestController
@RequestMapping("/api/horario")
class HorarioController(private val horarioService: HorarioService, private val userSecurityService: UserSecurityService) { // Inyectar UserSecurityService

    @GetMapping
    fun obtenerTodos(): List<Horario> = horarioService.findAll()

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<Horario> {
        val horario = horarioService.findById(id)
        return if (horario != null) ResponseEntity.ok(horario)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun crear(@RequestBody solicitud: HorarioRequest): ResponseEntity<Horario> {
        val currentUser = userSecurityService.getCurrentAuthenticatedUser()
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() // Retornar 401 si no hay usuario autenticado

        val horario = Horario(
            dia = solicitud.dia,
            horaInicio = solicitud.horaInicio,
            horaFin = solicitud.horaFin,
            user = currentUser // Asignar el usuario actual
        )
        return ResponseEntity.ok(horarioService.save(horario))
    }

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @RequestBody solicitud: HorarioRequest): ResponseEntity<Horario> {
        val existente = horarioService.findById(id)
        return if (existente != null) {
            val actualizado = horarioService.save(
                existente.copy(
                    dia = solicitud.dia,
                    horaInicio = solicitud.horaInicio,
                    horaFin = solicitud.horaFin
                    // El campo 'user' no se actualiza aquí porque es parte de la creación
                )
            )
            ResponseEntity.ok(actualizado)
        } else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Long): ResponseEntity<Void> {
        return if (horarioService.findById(id) != null) {
            horarioService.deleteById(id)
            ResponseEntity.noContent().build()
        } else ResponseEntity.notFound().build()
    }

    @GetMapping("/available-times")
    fun getAvailableTimes(
        @RequestParam date: LocalDate, 
        @RequestParam servicioId: Long,
        @RequestParam(required = false) employeeId: Long?
    ): ResponseEntity<List<LocalTime>> {
        return try {
            val availableTimes = horarioService.getAvailableTimes(date, servicioId, employeeId)
            ResponseEntity.ok(availableTimes)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}
