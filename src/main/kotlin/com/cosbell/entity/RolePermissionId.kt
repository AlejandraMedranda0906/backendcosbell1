package com.cosbell.entity

import jakarta.persistence.Column
import java.io.Serializable // Importar Serializable

data class RolePermissionId(
    @Column(name = "role_id") // Especificar el nombre de la columna física
    val roleId: Long = 0,
    @Column(name = "permission_id") // Especificar el nombre de la columna física
    val permissionId: Long = 0
): java.io.Serializable 