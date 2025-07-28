package com.cosbell.service

import com.cosbell.dto.AuthResponse
import org.springframework.stereotype.Service
import com.cosbell.dto.LoginRequest
import com.cosbell.dto.RegisterRequest
import com.cosbell.entity.Role
import com.cosbell.entity.User
import com.cosbell.repository.RoleRepository
import com.cosbell.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import com.cosbell.dto.EmployeeWithServicesDTO
import com.cosbell.repository.EmployeeServiceSpecialtyRepository
import com.cosbell.controller.toDto
import com.cosbell.dto.HorarioRequest
import com.cosbell.repository.HorarioRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val employeeServiceSpecialtyRepository: EmployeeServiceSpecialtyRepository,
    private val horarioRepository: HorarioRepository
) {

    fun createUser(request: RegisterRequest): User {
        if (userRepository.existsByEmail(request.email)) {
            throw Exception("El correo ya está registrado")
        }

        val role: Role = roleRepository.findByName(request.role.uppercase())
            ?: throw Exception("Rol no encontrado")

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            roles = mutableListOf(role)
        )
        return userRepository.save(user)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw Exception("Credenciales incorrectas")
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw Exception("Credenciales incorrectas")
        }
        return AuthResponse(
            message = "Login exitoso",
            email = user.email,
            token = "fake-jwt-token",
            role = user.roles.firstOrNull()?.name
        )
    }

    fun getEmployees(): List<User> = userRepository.findByRoles_Name("EMPLOYEE")

    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun getClients(): List<User> = userRepository.findByRoles_Name("CLIENT")

    fun getEmployeesWithServices(): List<EmployeeWithServicesDTO> {
        val employees = userRepository.findByRoles_Name("EMPLOYEE")
        return employees.map { employee ->
            val specialties = employeeServiceSpecialtyRepository.findByEmployee_Id(employee.id)
            val services = specialties.map { it.service.toDto() }
            val horarios = horarioRepository.findByUser_Id(employee.id).map {
                HorarioRequest(it.dia, it.horaInicio, it.horaFin)
            }
            EmployeeWithServicesDTO(
                id = employee.id,
                name = employee.name,
                email = employee.email,
                roleId = employee.roles.firstOrNull()?.id ?: 0,
                services = services,
                horarios = horarios
            )
        }
    }

    fun getAdmins(): List<User> = userRepository.findByRoles_Name("ADMIN")

    fun getByEmail(email: String): User? = userRepository.findByEmail(email)

    fun editUser(id: Long, request: RegisterRequest): User? {
        val user = userRepository.findById(id).orElse(null) ?: return null
        val role = roleRepository.findByName(request.role.uppercase())
            ?: throw Exception("Rol no encontrado")

        val updatedUser = user.copy(
            name = request.name,
            email = request.email,
            password = if (request.password.isNotBlank()) passwordEncoder.encode(request.password) else user.password,
            roles = listOf(role)
        )

        return userRepository.save(updatedUser)
    }

    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else false
    }
}
