package com.cosbell.service

import com.cosbell.entity.Promotion
import com.cosbell.repository.PromotionRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PromotionService(private val promotionRepository: PromotionRepository) {

    fun findAll(): List<Promotion> = promotionRepository.findAll()

    fun findById(id: Long): Promotion? = promotionRepository.findById(id).orElse(null)

    fun save(promotion: Promotion): Promotion = promotionRepository.save(promotion)

    fun deleteById(id: Long) = promotionRepository.deleteById(id)

    fun findActivePromotions(): List<Promotion> {
        val today = LocalDate.now()
        return promotionRepository.findAll().filter { promotion ->
            (promotion.startDate.isBefore(today) || promotion.startDate.isEqual(today)) &&
            (promotion.endDate.isAfter(today) || promotion.endDate.isEqual(today))
        }
    }
} 