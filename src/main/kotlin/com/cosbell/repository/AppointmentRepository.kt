package com.cosbell.repository

import com.cosbell.entity.Appointment
import com.cosbell.entity.Servicio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime

interface AppointmentRepository : JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    fun findByUserId(userId: Long): List<Appointment>
    fun findByEmployee_Id(employeeId: Long): List<Appointment>
    fun existsByServicioAndFechaAndHora(servicio: Servicio, fecha: LocalDate, hora: LocalTime): Boolean
    fun findByFecha(fecha: LocalDate): List<Appointment>
    fun findByFechaAndEmployee_Id(fecha: LocalDate, employeeId: Long): List<Appointment>
    fun findByServicio_Id(servicioId: Long): List<Appointment>
    fun findByFechaBetween(start: LocalDate, end: LocalDate): List<Appointment>

    @Query("""
        SELECT a.employee.name, COUNT(a) 
        FROM Appointment a 
        GROUP BY a.employee.name 
        ORDER BY COUNT(a) DESC
        LIMIT 1
    """)
    fun findTopEmployee(): List<Array<Any>>

    @Query("""
        SELECT a.servicio.name, COUNT(a) 
        FROM Appointment a 
        GROUP BY a.servicio.name 
        ORDER BY COUNT(a) DESC
        LIMIT 1
    """)
    fun findTopService(): List<Array<Any>>

    @Query("""
        SELECT SUM(a.servicio.price) 
        FROM Appointment a 
        WHERE a.fecha = :fecha AND a.status = 'COMPLETED'
    """)
    fun getRevenueForDate(@Param("fecha") fecha: LocalDate): Double?

    @Query("""
        SELECT COUNT(a) 
        FROM Appointment a 
        WHERE a.fecha = :fecha
    """)
    fun countAppointmentsForDate(@Param("fecha") fecha: LocalDate): Int

    @Query("""
        SELECT a.servicio.name, COUNT(a) 
        FROM Appointment a 
        WHERE a.fecha = :fecha 
        GROUP BY a.servicio.name
    """)
    fun getServiceCountsByDate(@Param("fecha") fecha: LocalDate): List<Array<Any>>
}
