package com.cosbell.repository

import com.cosbell.entity.Horario
import org.springframework.data.jpa.repository.JpaRepository

interface HorarioRepository : JpaRepository<Horario, Long> {
    fun findByDia(dia: String): List<Horario>
    fun findByUser_IdAndDia(userId: Long, dia: String): List<Horario>
    fun findByUser_Id(userId: Long): List<Horario>
    fun findByDiaAndUserIsNull(dia: String): List<Horario> // Horarios por defecto
}
