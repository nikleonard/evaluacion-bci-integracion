package cl.bci.evaluacion.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilidad para generación y validación de tokens JWT.
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Genera un token JWT para un usuario.
     *
     * @param email Email del usuario (usado como subject del token)
     * @return Token JWT válido con claim de rol "usuario"
     */
    public String generateJWT(String email) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("rol", "usuario")
                .issuedAt(now)
                .expiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}
