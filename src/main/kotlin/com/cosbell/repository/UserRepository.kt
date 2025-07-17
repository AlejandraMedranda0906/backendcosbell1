package com.cosbell.repository

import com.cosbell.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u JOIN FETCH u.roles r WHERE r.name = :roleName")
    fun findByRoles_Name(roleName: String): List<User>
}

/*interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?}*/