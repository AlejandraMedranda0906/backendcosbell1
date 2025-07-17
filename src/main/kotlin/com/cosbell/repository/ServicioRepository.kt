package com.cosbell.repository

import com.cosbell.entity.Servicio
import org.springframework.data.jpa.repository.JpaRepository

interface ServicioRepository : JpaRepository<Servicio, Long> {
    fun findByCategoryId(categoryId: Long): List<Servicio>
}
