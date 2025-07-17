package com.cosbell.security

import com.cosbell.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.authority.SimpleGrantedAuthority
// import org.springframework.stereotype.Service // Comentado para deshabilitar este bean

//@Service // Comentado para deshabilitar este bean
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Usuario no encontrado: $username")
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            user.roles.map { SimpleGrantedAuthority("ROLE_" + it.name.uppercase()) }
        ).also {
            println("UserDetailsServiceImpl: Loaded UserDetails for ${it.username} with authorities: ${it.authorities}")
        }
    }
}