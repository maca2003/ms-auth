package cl.duoc.msauth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiracion-ms}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    public Claims leerToken(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
