package com.cosbell.repository

import com.cosbell.entity.EmployeeServiceSpecialty
import com.cosbell.entity.EmployeeServiceSpecialtyId
import com.cosbell.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeServiceSpecialtyRepository : JpaRepository<EmployeeServiceSpecialty, EmployeeServiceSpecialtyId> {
    fun existsByEmployee_IdAndService_Id(employeeId: Long, serviceId: Long): Boolean
    fun findByEmployee_Id(employeeId: Long): List<EmployeeServiceSpecialty>
    fun deleteAllByEmployee(employee: User)

} 