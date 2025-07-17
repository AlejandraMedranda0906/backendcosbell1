package com.cosbell.controller

import com.cosbell.dto.PromotionDto
import com.cosbell.entity.Promotion
import com.cosbell.service.PromotionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

// Extension functions for conversion between Entity and DTO
fun Promotion.toDto(): PromotionDto = PromotionDto(this.id, this.name, this.description, this.startDate, this.endDate, this.conditions, this.imageUrl)
fun PromotionDto.toEntity(): Promotion = Promotion(this.id ?: 0, this.name, this.description, this.startDate, this.endDate, this.conditions, this.imageUrl)

@RestController
@RequestMapping("/api/promotions")
class PromotionController(private val promotionService: PromotionService) {

    @GetMapping
    fun getAllPromotions(): List<PromotionDto> = promotionService.findAll().map { it.toDto() }

    @GetMapping("/active")
    fun getActivePromotions(): List<PromotionDto> = promotionService.findActivePromotions().map { it.toDto() }

    @GetMapping("/{id}")
    fun getPromotionById(@PathVariable id: Long): ResponseEntity<PromotionDto> {
        val promotion = promotionService.findById(id)
        return if (promotion != null) ResponseEntity.ok(promotion.toDto()) else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createPromotion(@RequestBody promotionDto: PromotionDto): PromotionDto {
        val promotion = promotionDto.toEntity()
        return promotionService.save(promotion).toDto()
    }

    @PutMapping("/{id}")
    fun updatePromotion(@PathVariable id: Long, @RequestBody promotionDto: PromotionDto): ResponseEntity<PromotionDto> {
        val existingPromotion = promotionService.findById(id)
        return if (existingPromotion != null) {
            val updatedPromotion = promotionDto.toEntity().copy(id = id)
            val savedPromotion = promotionService.save(updatedPromotion)
            ResponseEntity.ok(savedPromotion.toDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deletePromotion(@PathVariable id: Long): ResponseEntity<Void> {
        return if (promotionService.findById(id) != null) {
            promotionService.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 