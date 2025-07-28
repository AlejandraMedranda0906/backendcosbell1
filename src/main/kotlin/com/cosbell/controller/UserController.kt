package com.cosbell.controller

import com.cosbell.entity.User
import com.cosbell.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.cosbell.dto.RegisterRequest
import com.cosbell.dto.EmployeeWithServicesDTO
import java.security.Principal

@RestController
@RequestMapping("api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@RequestBody request: RegisterRequest): User {
        return userService.createUser(request)
    }

    @GetMapping("/employees")
    fun getEmployees(): List<EmployeeWithServicesDTO> {
        return userService.getEmployeesWithServices()
    }

    @GetMapping("/clients")
    fun getClients(): List<User> {
        return userService.getClients()
    }

    @GetMapping("/personal")
    fun getPersonal(): List<Any> {
        val employees = userService.getEmployeesWithServices()
        val admins = userService.getAdmins()
        return admins + employees
    }

    @GetMapping("/me")
    fun getCurrentUser(principal: Principal): ResponseEntity<Map<String, String?>> {
        val user = userService.getByEmail(principal.name)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(
            mapOf(
                "email" to user.email,
                "phone" to user.phone
            )
        )
    }

    @GetMapping("/debug")
    fun debug(): String {
        return "Debug OK"
    }

    @PutMapping("/{id}")
    fun editUser(@PathVariable id: Long, @RequestBody request: RegisterRequest): ResponseEntity<User> {
        val updatedUser = userService.editUser(id, request)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = userService.deleteUser(id)
        return if (deleted) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}