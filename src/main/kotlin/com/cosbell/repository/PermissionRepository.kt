package com.cosbell.repository

import com.cosbell.entity.Permission
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRepository : JpaRepository<Permission, Long>
