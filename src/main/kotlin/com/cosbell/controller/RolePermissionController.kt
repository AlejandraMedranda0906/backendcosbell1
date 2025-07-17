package com.cosbell.controller

import com.cosbell.entity.RolePermission
import com.cosbell.entity.RolePermissionId
import com.cosbell.service.RolePermissionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/role-permissions")
class RolePermissionController(private val service: RolePermissionService) {

    @GetMapping
    fun getAll() = service.findAll()

    @GetMapping("/role/{roleId}/permission/{permissionId}")
    fun getById(@PathVariable roleId: Long, @PathVariable permissionId: Long): ResponseEntity<RolePermission> {
        val id = RolePermissionId(roleId, permissionId)
        val entity = service.findById(id)
        return if (entity != null) ResponseEntity.ok(entity) else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun create(@RequestBody entity: RolePermission): RolePermission = service.save(entity)

    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
    fun delete(@PathVariable roleId: Long, @PathVariable permissionId: Long): ResponseEntity<Void> {
        val id = RolePermissionId(roleId, permissionId)
        return if (service.findById(id) != null) {
            service.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}