package com.cosbell.repository

import com.cosbell.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
 
interface CategoryRepository : JpaRepository<Category, Long> 