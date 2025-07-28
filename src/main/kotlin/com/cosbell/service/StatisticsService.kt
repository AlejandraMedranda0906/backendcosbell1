package com.cosbell.service

import com.cosbell.dto.EmployeeCountDto
import com.cosbell.dto.ServiceCountDto
import com.cosbell.dto.StatisticsDto
import com.cosbell.repository.AppointmentRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StatisticsService(
    private val appointmentRepository: AppointmentRepository
) {

    fun getDailyStatistics(baseDate: LocalDate): StatisticsDto {
        return buildStatistics(baseDate, baseDate)
    }

    fun getWeeklyStatistics(baseDate: LocalDate): StatisticsDto {
        val startOfWeek = baseDate.minusDays(baseDate.dayOfWeek.value.toLong() - 1) // Lunes
        val endOfWeek = startOfWeek.plusDays(6)
        return buildStatistics(startOfWeek, endOfWeek)
    }

    fun getMonthlyStatistics(baseDate: LocalDate): StatisticsDto {
        val startOfMonth = baseDate.withDayOfMonth(1)
        val endOfMonth = baseDate.withDayOfMonth(baseDate.lengthOfMonth())
        return buildStatistics(startOfMonth, endOfMonth)
    }

    fun getAllTimeStatistics(): StatisticsDto {
        val completedAppointments = appointmentRepository
            .findAll()
            .filter { it.status.equals("COMPLETED", ignoreCase = true) }

        val topEmployee = completedAppointments.groupingBy { it.employee.name }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "N/A"

        val topService = completedAppointments.groupingBy { it.servicio.name }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "N/A"

        val revenue = completedAppointments.sumOf { it.servicio.price }

        val serviceBreakdown = completedAppointments.groupingBy { it.servicio.name }
            .eachCount()
            .map { (name, count) -> ServiceCountDto(name, count) }

        val employeesBreakdown = completedAppointments.groupingBy { it.employee.name }
            .eachCount()
            .map { (name, count) -> EmployeeCountDto(name, count) }

        return StatisticsDto(
            topEmployee = topEmployee,
            topService = topService,
            totalRevenue = revenue,
            totalAppointments = completedAppointments.size,
            servicesBreakdown = serviceBreakdown,
            employeesBreakdown = employeesBreakdown
        )
    }

    private fun buildStatistics(startDate: LocalDate, endDate: LocalDate): StatisticsDto {
        val completedAppointments = appointmentRepository
            .findByFechaBetween(startDate, endDate)
            .filter { it.status.equals("COMPLETED", ignoreCase = true) }

        val topEmployee = completedAppointments.groupingBy { it.employee.name }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "N/A"

        val topService = completedAppointments.groupingBy { it.servicio.name }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "N/A"

        val revenue = completedAppointments.sumOf { it.servicio.price }

        val serviceBreakdown = completedAppointments.groupingBy { it.servicio.name }
            .eachCount()
            .map { (name, count) -> ServiceCountDto(name, count) }

        val employeesBreakdown = completedAppointments.groupingBy { it.employee.name }
            .eachCount()
            .map { (name, count) -> EmployeeCountDto(name, count) }

        return StatisticsDto(
            topEmployee = topEmployee,
            topService = topService,
            totalRevenue = revenue,
            totalAppointments = completedAppointments.size,
            servicesBreakdown = serviceBreakdown,
            employeesBreakdown = employeesBreakdown
        )
    }
}
