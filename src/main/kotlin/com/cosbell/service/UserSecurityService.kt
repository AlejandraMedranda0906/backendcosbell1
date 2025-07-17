package com.cosbell.service

import com.cosbell.dto.LoginRequest
import com.cosbell.dto.TokenDto
import com.cosbell.entity.User
import com.cosbell.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.cosbell.security.JwtService // Importar JwtService

@Service
class UserSecurityService : UserDetailsService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtService: JwtService // Cambiado de jwtUtil a jwtService

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val userEntity = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("Usuario no encontrado")

        // Cargar roles con prefijo ROLE_ para que @PreAuthorize funcione
        val roleAuthorities = userEntity.roles.map { role ->
            SimpleGrantedAuthority("ROLE_${role.name.uppercase()}")
        }
        
        // Cargar permisos también si existen
        val permissionAuthorities = userEntity.roles.flatMap { role ->
            role.permissions.map { permission ->
                SimpleGrantedAuthority("${permission.httpMethod}:${permission.resourcePath}")
            }
        }
        
        val authorityList = roleAuthorities + permissionAuthorities

        return org.springframework.security.core.userdetails.User.builder()
            .username(userEntity.email)
            .password(userEntity.password)
            .authorities(authorityList)
            .build()
    }

    fun login(loginDto: LoginRequest): TokenDto {
        val authentication = authenticate(loginDto.email, loginDto.password)
        SecurityContextHolder.getContext().authentication = authentication
        val userEntity = userRepository.findByEmail(authentication.name) // Obtener el objeto User
            ?: throw UsernameNotFoundException("Usuario no encontrado para generar el token.")
        val accessToken = jwtService.generateToken(userEntity) // Pasar el objeto User
        return TokenDto().apply {
            jwt = accessToken
        }
    }

    fun authenticate(email: String, password: String): Authentication {
        val userDetails = loadUserByUsername(email)

        if (!passwordEncoder.matches(password, userDetails.password)) {
            throw UsernameNotFoundException("Credenciales incorrectas")
        }

        return UsernamePasswordAuthenticationToken(email, null, userDetails.authorities)
    }

    fun getCurrentAuthenticatedUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val userEmail = authentication.name
            return userRepository.findByEmail(userEmail)
        }
        return null
    }
}