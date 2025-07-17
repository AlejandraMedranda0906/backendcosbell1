package com.cosbell.controller

import com.cosbell.entity.User
import com.cosbell.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.cosbell.dto.RegisterRequest
import com.cosbell.dto.EmployeeWithServicesDTO

@RestController
@RequestMapping("/users")
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
}