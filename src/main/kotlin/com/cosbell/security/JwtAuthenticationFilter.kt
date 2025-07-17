package com.cosbell.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import com.cosbell.service.UserSecurityService // Importar UserSecurityService

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userSecurityService: UserSecurityService // Cambiado de userDetailsServiceImpl
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        println("JwtAuthenticationFilter: Interceptando solicitud [${request.method}] ${request.requestURI}")
        println("JwtAuthenticationFilter: Interceptando solicitud para ${request.requestURI}")

        // Imprimir todos los encabezados de la solicitud para depuración
        val headerNames = request.headerNames
        println("JwtAuthenticationFilter: Encabezados de la solicitud:")
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            println("  $headerName: ${request.getHeader(headerName)}")
        }

        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            println("JwtAuthenticationFilter: No hay encabezado de autorización o no es un token Bearer.")
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        val email = jwtService.extractUsername(jwt)
        println("JwtAuthenticationFilter: JWT extraído: $jwt")
        println("JwtAuthenticationFilter: Email extraído del JWT: $email")

        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            println("JwtAuthenticationFilter: Cargando detalles de usuario para $email")
            val userDetails = userSecurityService.loadUserByUsername(email) // Usar userSecurityService
            if (jwtService.validateToken(jwt) != null && userDetails.username == email) {
                println("JwtAuthenticationFilter: Token JWT validado correctamente.")
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
                println("JwtAuthenticationFilter: Autenticación establecida para ${userDetails.username} con roles ${userDetails.authorities}")
                println("JwtAuthenticationFilter: Roles del usuario según Spring Security: ${userDetails.authorities}")
                println("JwtAuthenticationFilter: Contexto de seguridad actual ANTES de pasar al siguiente filtro: ${SecurityContextHolder.getContext().authentication}")
            } else if (jwtService.validateToken(jwt) == null) {
                println("JwtAuthenticationFilter: Token JWT no válido.")
            } else {
                println("JwtAuthenticationFilter: Usuario no coincide con el token.")
            }
        } else if (email == null) {
            println("JwtAuthenticationFilter: No se pudo extraer el email del token JWT.")
        } else {
            println("JwtAuthenticationFilter: Ya hay una autenticación en el contexto: ${SecurityContextHolder.getContext().authentication?.name}")
        }

        filterChain.doFilter(request, response)
        println("JwtAuthenticationFilter: Contexto de seguridad DESPUÉS de pasar al siguiente filtro: ${SecurityContextHolder.getContext().authentication}")
    }
}