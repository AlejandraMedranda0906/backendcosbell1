package com.cosbell.dto

data class ServiceCountDto(
    val name: String,
    val count: Int
)

data class StatisticsDto(
    val topEmployee: String,
    val topService: String,
    val totalRevenue: Double,
    val totalAppointments: Int,
    val servicesBreakdown: List<ServiceCountDto> = emptyList(),
    val employeesBreakdown: List<EmployeeCountDto> = emptyList() // <--- NUEVO

)
