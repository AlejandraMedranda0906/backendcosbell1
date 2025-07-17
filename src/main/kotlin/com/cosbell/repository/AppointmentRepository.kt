package com.cosbell.repository

import com.cosbell.entity.Appointment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import com.cosbell.entity.Servicio
import java.time.LocalDate
import java.time.LocalTime

/*interface AppointmentRepository : JpaRepository<Appointment, Long> {
    fun existsByServicioIdAndFechaAndHora(servicioId: Long, fecha: LocalDate, hora: LocalTime): Boolean
    fun findByEmail(email: String): List<Appointment>*/


interface AppointmentRepository : JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    fun findByUserId(userId: Long): List<Appointment>
    fun findByEmployee_Id(employeeId: Long): List<Appointment>
    fun existsByServicioAndFechaAndHora(servicio: Servicio, fecha: LocalDate, hora: LocalTime): Boolean
    fun findByFecha(fecha: LocalDate): List<Appointment>
    fun findByFechaAndEmployee_Id(fecha: LocalDate, employeeId: Long): List<Appointment>
    fun findByServicio_Id(servicioId: Long): List<Appointment>
}