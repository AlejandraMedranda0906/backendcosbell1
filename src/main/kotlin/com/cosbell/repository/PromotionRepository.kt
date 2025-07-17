package com.cosbell.repository

import com.cosbell.entity.Promotion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
 
@Repository
interface PromotionRepository : JpaRepository<Promotion, Long> 