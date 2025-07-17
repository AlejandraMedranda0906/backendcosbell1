package com.cosbell.service

import com.cosbell.entity.Servicio
import com.cosbell.repository.ServicioRepository
import org.springframework.stereotype.Service

//@org.springframework.stereotype.Service
//class ServiceService(private val servicioRepository: ServiceRepository) {
//  fun getAllServicios(): List<com.cosbell.spa.entity.Service> = servicioRepository.findAll()
//}

/*@org.springframework.stereotype.Service
class ServiceService(private val servicioRepository: ServicioRepository) {

    fun findAll(): List<Servicio> = servicioRepository.findAll()

    fun findById(id: Long): Servicio? = servicioRepository.findById(id).orElse(null)

    fun save(servicio: Servicio): Servicio = servicioRepository.save(servicio)

    fun deleteById(id: Long) = servicioRepository.deleteById(id)
}*/
@Service
class ServicioService(private val servicioRepository: ServicioRepository) {
    fun findAll(): List<Servicio> = servicioRepository.findAll()
    fun findById(id: Long): Servicio? = servicioRepository.findById(id).orElse(null)
    fun save(servicio: Servicio): Servicio = servicioRepository.save(servicio)
    fun deleteById(id: Long) = servicioRepository.deleteById(id)

    fun findByCategoryId(categoryId: Long): List<Servicio> = servicioRepository.findByCategoryId(categoryId)
}