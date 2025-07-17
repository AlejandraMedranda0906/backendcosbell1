package com.cosbell.controller

import com.cosbell.dto.RatingRequestDto
import com.cosbell.entity.Rating
import com.cosbell.service.RatingService
import com.cosbell.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/ratings")
class RatingController(
    private val ratingService: RatingService,
    private val userService: UserService
) {

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createRating(@RequestBody request: RatingRequestDto, principal: Principal): ResponseEntity<Any> {
        return try {
            val user = userService.findByEmail(principal.name)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.")

            val newRating = ratingService.createRating(request.appointmentId, user.id!!, request.rating, request.comment)
            ResponseEntity.status(HttpStatus.CREATED).body(newRating)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }

    @GetMapping("/employee/{employeeId}")
    fun getApprovedRatingsByEmployeeId(@PathVariable employeeId: Long): ResponseEntity<List<Rating>> {
        val ratings = ratingService.getApprovedRatingsByEmployeeId(employeeId)
        return ResponseEntity.ok(ratings)
    }

    @GetMapping("/service/{serviceId}")
    fun getRatingsByServiceId(@PathVariable serviceId: Long): ResponseEntity<List<Rating>> {
        val ratings = ratingService.getRatingsByServiceId(serviceId)
        return ResponseEntity.ok(ratings)
    }

    // --- Endpoints de moderación (opcionales y para rol ADMIN) ---
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun approveRating(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val updatedRating = ratingService.approveRating(id)
            ResponseEntity.ok(updatedRating)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun deactivateRating(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val updatedRating = ratingService.deactivateRating(id)
            ResponseEntity.ok(updatedRating)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
} 