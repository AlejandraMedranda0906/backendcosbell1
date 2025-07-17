package com.cosbell.service

import com.cosbell.entity.RolePermission
import com.cosbell.entity.RolePermissionId
import com.cosbell.repository.RolePermissionRepository
import org.springframework.stereotype.Service

@Service
class RolePermissionService(private val repository: RolePermissionRepository) {

    fun findAll(): List<RolePermission> = repository.findAll()

    fun findById(id: RolePermissionId): RolePermission? = repository.findById(id).orElse(null)

    fun save(entity: RolePermission): RolePermission = repository.save(entity)

    fun deleteById(id: RolePermissionId) = repository.deleteById(id)
}