package com.cosbell.controller

import com.cosbell.entity.Role
import com.cosbell.repository.RoleRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/roles")
class RoleController(
    private val roleRepository: RoleRepository
) {

    @GetMapping
    fun getAllRoles(): ResponseEntity<List<Role>> {
        val roles = roleRepository.findAll()
        return ResponseEntity.ok(roles)
    }

    @GetMapping("/{id}")
    fun getRoleById(@PathVariable id: Long): ResponseEntity<Role> {
        val role = roleRepository.findById(id)
        return if (role.isPresent) {
            ResponseEntity.ok(role.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/name/{name}")
    fun getRoleByName(@PathVariable name: String): ResponseEntity<Role> {
        val role = roleRepository.findByName(name)
        return if (role != null) {
            ResponseEntity.ok(role)
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 