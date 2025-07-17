package com.cosbell.repository

import com.cosbell.entity.RolePermission
import com.cosbell.entity.RolePermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionRepository : JpaRepository<RolePermission, RolePermissionId>
