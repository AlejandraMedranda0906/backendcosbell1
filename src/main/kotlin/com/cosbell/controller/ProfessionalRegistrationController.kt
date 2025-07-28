package com.cosbell.controller

import com.cosbell.dto.ProfessionalRegisterRequest
import com.cosbell.entity.User
import com.cosbell.service.ProfessionalRegistrationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/professionals")
class ProfessionalRegistrationController(
    private val professionalRegistrationService: ProfessionalRegistrationService,
    private val professionalService: ProfessionalRegistrationService

) {

    @PostMapping("/register")
    fun registerProfessional(@RequestBody request: ProfessionalRegisterRequest): ResponseEntity<Any> {
        return try {
            val professional = professionalRegistrationService.registerProfessional(request)
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Profesional registrado exitosamente", "userId" to professional.id))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "Error al registrar profesional: ${e.message}"))
        }
    }
    @DeleteMapping("/{id}")
    fun deleteProfessional(@PathVariable id: Long): ResponseEntity<Void> {
        professionalService.deleteProfessional(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun updateProfessional(
        @PathVariable id: Long,
        @RequestBody request: ProfessionalRegisterRequest
    ): ResponseEntity<User> {
        val updated = professionalService.updateProfessional(id, request)
        return ResponseEntity.ok(updated)
    }
} 