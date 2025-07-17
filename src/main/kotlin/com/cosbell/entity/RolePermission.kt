package com.cosbell.entity

import jakarta.persistence.*
import java.io.Serializable


@Entity
@IdClass(RolePermissionId::class)
@Table(name = "role_permission")
data class RolePermission(
    @Id
    @Column(name = "role_id") // Especificar el nombre de la columna física
    val roleId: Long,

    @Id
    @Column(name = "permission_id") // Especificar el nombre de la columna física
    val permissionId: Long
)

/*data class RolePermissionId(
    val roleId: Long = 0,
    val permissionId: Long = 0
): java.io.Serializable*/