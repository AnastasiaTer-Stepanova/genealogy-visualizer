package genealogy.visualizer.service.authorization;

import genealogy.visualizer.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.Date;

public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final Duration duration;

    public JwtServiceImpl(String secret, Duration duration) {
        this.secretKey = new SecretKeySpec(secret.getBytes(), "HMACSHA256");
        this.duration = duration;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + duration.toMillis()))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        Claims claims = extractAllClaims(token);
        return (claims.getSubject().equals(user.getLogin())) && !claims.getExpiration().before(new Date());
    }

    @Override
    public String getLogin(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
