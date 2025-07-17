package com.cosbell.repository

import com.cosbell.entity.Rating
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RatingRepository : JpaRepository<Rating, Long> {
    fun findByAppointmentId(appointmentId: Long): Rating?
    fun existsByAppointmentId(appointmentId: Long): Boolean
    fun findAllByAppointmentId(appointmentId: Long): List<Rating>
    fun findAllByAppointmentIdIn(appointmentIds: List<Long>): List<Rating>
} 