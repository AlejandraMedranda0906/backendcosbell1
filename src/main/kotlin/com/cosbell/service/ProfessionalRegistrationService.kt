package com.cosbell.service

import com.cosbell.dto.ProfessionalRegisterRequest
import com.cosbell.dto.HorarioRequest
import com.cosbell.entity.User
import com.cosbell.entity.Horario
import com.cosbell.entity.EmployeeServiceSpecialty
import com.cosbell.entity.EmployeeServiceSpecialtyId
import com.cosbell.repository.UserRepository
import com.cosbell.repository.RoleRepository
import com.cosbell.repository.HorarioRepository
import com.cosbell.repository.ServicioRepository
import com.cosbell.repository.EmployeeServiceSpecialtyRepository
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
            phone = request.phone,
            roles = listOf(role)
        )

        val savedUser = userRepository.save(user)

        // Guardar horarios
        request.schedules.forEach { sch ->
            val horario = Horario(
                dia = sch.dia,
                horaInicio = sch.horaInicio,
                horaFin = sch.horaFin,
                user = savedUser
            )
            horarioRepository.save(horario)
        }

        // Guardar especialidades si es empleado
        if (request.roleName.uppercase() == "EMPLOYEE") {
            request.serviceIds.forEach { serviceId ->
                val service = servicioRepository.findById(serviceId)
                    .orElseThrow { IllegalArgumentException("Servicio con ID $serviceId no encontrado.") }

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

    fun getAllEmployees(): List<User> =
        userRepository.findByRoles_Name("EMPLOYEE")

    @Transactional
    fun updateProfessional(id: Long, request: ProfessionalRegisterRequest): User {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Profesional con ID $id no encontrado.") }

        // Actualizar campos básicos
        user.apply {
            name = request.name
            phone = request.phone
        }

        // Actualizar email si cambió
        if (user.email != request.email) {
            if (userRepository.existsByEmail(request.email)) {
                throw IllegalArgumentException("El correo ya está registrado.")
            }
            user.email = request.email
        }

        // Actualizar password si se provee
        if (!request.password.isNullOrBlank()) {
            user.password = passwordEncoder.encode(request.password)
        }

        val updatedUser = userRepository.save(user)

        // Reemplazar horarios
        horarioRepository.deleteAllByUser(updatedUser)
        request.schedules.forEach { sch ->
            horarioRepository.save(
                Horario(
                    dia = sch.dia,
                    horaInicio = sch.horaInicio,
                    horaFin = sch.horaFin,
                    user = updatedUser
                )
            )
        }

        // Reemplazar especialidades si es empleado
        if (request.roleName.uppercase() == "EMPLOYEE") {
            employeeServiceSpecialtyRepository.deleteAllByEmployee(updatedUser)
            request.serviceIds.forEach { serviceId ->
                val service = servicioRepository.findById(serviceId)
                    .orElseThrow { IllegalArgumentException("Servicio con ID $serviceId no encontrado.") }
                employeeServiceSpecialtyRepository.save(
                    EmployeeServiceSpecialty(
                        id = EmployeeServiceSpecialtyId(employeeId = updatedUser.id, serviceId = service.id),
                        employee = updatedUser,
                        service = service
                    )
                )
            }
        }

        return updatedUser
    }

    @Transactional
    fun deleteProfessional(id: Long) {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Profesional con ID $id no encontrado.") }

        // Borrar horarios y especialidades
        horarioRepository.deleteAllByUser(user)
        employeeServiceSpecialtyRepository.deleteAllByEmployee(user)

        userRepository.delete(user)
    }
}