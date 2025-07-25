package com.cosbell.security

import com.cosbell.service.UserSecurityService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userSecurityService: UserSecurityService
) : OncePerRequestFilter() {

    // Lista de rutas públicas exactas
    private val publicPaths = listOf(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/servicio",
        "/api/horario/available-times",
        "/api/citas/user/",
        "/users/employees",
        "/users/clients",
        "/api/config/check",
        "/api/servicio/",
        "/api/category",
        "/api/category/",
        "/api/promotions",
        "/api/promotions/",
        "/api/ratings",
        "/api/ratings/",
        "/ws-native",
        "/error"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath
        println("JwtAuthenticationFilter: Interceptando solicitud [${request.method}] $path")

        // Saltar rutas públicas exactas
        if (publicPaths.any { path == it }) {
            println("JwtAuthenticationFilter: Ruta pública detectada. Continuando sin autenticación.")
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            println("JwtAuthenticationFilter: No hay encabezado Authorization o no es Bearer.")
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        val email = jwtService.extractUsername(jwt)

        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userSecurityService.loadUserByUsername(email)
            if (jwtService.validateToken(jwt) != null && userDetails.username == email) {
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
                println("JwtAuthenticationFilter: Autenticado correctamente a $email")
            } else {
                println("JwtAuthenticationFilter: Token inválido o usuario no coincide.")
            }
        }

        filterChain.doFilter(request, response)
    }
}
