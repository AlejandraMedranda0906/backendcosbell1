package com.cosbell.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.cosbell.entity.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {
    private val secretKey = "mi_clave_secreta_segura"

    fun generateToken(user: User): String {
        val algorithm = Algorithm.HMAC256(secretKey)
        val rolesClaim = user.roles.mapNotNull { it.name }
        println("JwtService: Generating token for user ${user.email} with roles: $rolesClaim")
        return JWT.create()
            .withSubject(user.email)
            .withClaim("roles", rolesClaim)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .sign(algorithm)
    }

    fun extractUsername(token: String): String? {
        return try {
            val decodedJWT = validateToken(token)
            println("JwtService: Extracted username: ${decodedJWT.subject}")
            decodedJWT.subject
        } catch (e: Exception) {
            println("JwtService: Error extracting username from token: ${e.message}")
            null
        }
    }

    fun validateToken(token: String): DecodedJWT {
        if (token.isNullOrBlank()) {
            throw IllegalArgumentException("El token JWT no puede ser nulo o vacío para la validación.")
        }
        val algorithm = Algorithm.HMAC256(secretKey)
        val verifier = JWT.require(algorithm).build()
        val decoded = verifier.verify(token)
        println("JwtService: Token validado. Claims: ${decoded.claims}")
        return decoded
    }
}