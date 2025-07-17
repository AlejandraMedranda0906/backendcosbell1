package com.cosbell.controller

import com.cosbell.entity.Category
import com.cosbell.repository.CategoryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/category")
class CategoryController(
    private val categoryRepository: CategoryRepository
) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> {
        val categories = categoryRepository.findAll()
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): ResponseEntity<Category> {
        val category = categoryRepository.findById(id)
        return if (category.isPresent) {
            ResponseEntity.ok(category.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createCategory(@RequestBody category: Category): ResponseEntity<Category> {
        val savedCategory = categoryRepository.save(category)
        return ResponseEntity.ok(savedCategory)
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: Long, @RequestBody category: Category): ResponseEntity<Category> {
        val existingCategory = categoryRepository.findById(id)
        return if (existingCategory.isPresent) {
            val updatedCategory = category.copy(id = id)
            val savedCategory = categoryRepository.save(updatedCategory)
            ResponseEntity.ok(savedCategory)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        return if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 