package com.cosbell.service

import com.cosbell.dto.HorarioRequest
import com.cosbell.dto.ProfessionalRegisterRequest
import com.cosbell.entity.*
import com.cosbell.repository.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfessionalRegistrationService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val horarioRepository: HorarioRepository,
    private val servicioRepository: ServicioRepository,
    private val employeeServiceSpecialtyRepository: EmployeeServiceSpecialtyRepository
) {

    @Transactional
    fun registerProfessional(request: ProfessionalRegisterRequest): User {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("El correo ya está registrado.")
        }

        val role = roleRepository.findByName(request.roleName.uppercase())
            ?: throw IllegalArgumentException("Rol '${request.roleName}' no encontrado.")

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            roles = listOf(role)
        )
        val savedUser = userRepository.save(user)

        // Save schedules for the professional
        request.schedules.forEach { scheduleRequest ->
            val horario = Horario(
                dia = scheduleRequest.dia,
                horaInicio = scheduleRequest.horaInicio,
                horaFin = scheduleRequest.horaFin,
                user = savedUser
            )
            horarioRepository.save(horario)
        }

        // Link services to the professional if it's an employee
        if (request.roleName.uppercase() == "EMPLOYEE") {
            request.serviceIds.forEach { serviceId ->
                val service = servicioRepository.findById(serviceId).orElse(null)
                    ?: throw IllegalArgumentException("Servicio con ID $serviceId no encontrado.")
                val employeeSpecialty = EmployeeServiceSpecialty(
                    id = EmployeeServiceSpecialtyId(employeeId = savedUser.id, serviceId = service.id),
                    employee = savedUser,
                    service = service
                )
                employeeServiceSpecialtyRepository.save(employeeSpecialty)
            }
        }

        return savedUser
    }

    fun getAllEmployees(): List<User> {
        // Eliminamos la búsqueda del rol y usamos directamente el nombre del rol en la consulta.
        return userRepository.findByRoles_Name("EMPLOYEE")
    }
} 