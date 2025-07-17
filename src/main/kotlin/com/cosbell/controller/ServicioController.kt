package com.cosbell.controller

import com.cosbell.dto.ServicioDTO
import com.cosbell.entity.Servicio
import com.cosbell.entity.Category
import com.cosbell.service.ServicioService
import com.cosbell.repository.CategoryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

fun Servicio.toDto(): ServicioDTO = ServicioDTO(
    this.id, this.name, this.duration, this.price, this.description, this.descripcionExtend, this.category?.id, this.imageUrl
)

fun ServicioDTO.toEntity(category: Category): Servicio = Servicio(
    this.id ?: 0, this.name, this.duration, this.price, this.description, this.descripcionExtend, this.imageUrl, category
)

@RestController
@RequestMapping("/api/servicio")
class ServicioController(
    private val servicioService: ServicioService,
    private val categoryRepository: CategoryRepository
) {

    @GetMapping
    fun getAll(): List<ServicioDTO> = servicioService.findAll().map { it.toDto() }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ServicioDTO> {
        val servicio = servicioService.findById(id)
        return if (servicio != null) ResponseEntity.ok(servicio.toDto()) else ResponseEntity.notFound().build()
    }

    @GetMapping("/categoria/{categoryId}")
    fun getByCategory(@PathVariable categoryId: Long): List<ServicioDTO> =
        servicioService.findByCategoryId(categoryId).map { it.toDto() }

    @PostMapping
    fun create(@RequestBody servicioDto: ServicioDTO): ServicioDTO {
        val category = categoryRepository.findById(servicioDto.categoryId!!).orElseThrow { IllegalArgumentException("Categoría no encontrada") }
        val servicio = servicioDto.toEntity(category)
        return servicioService.save(servicio).toDto()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody servicioDto: ServicioDTO): ResponseEntity<ServicioDTO> {
        val servicio = servicioService.findById(id)
        return if (servicio != null) {
            val category = categoryRepository.findById(servicioDto.categoryId!!).orElseThrow { IllegalArgumentException("Categoría no encontrada") }
            val updatedServicio = servicioDto.toEntity(category).copy(id = id)
            val savedServicio = servicioService.save(updatedServicio)
            ResponseEntity.ok(savedServicio.toDto())
        } else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        return if (servicioService.findById(id) != null) {
            servicioService.deleteById(id)
            ResponseEntity.noContent().build()
        } else ResponseEntity.notFound().build()
    }
}