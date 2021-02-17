package io.mikovsky.workly.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.mikovsky.workly.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

import static io.mikovsky.workly.security.SecurityConstants.EXPIRATION_TIME;
import static io.mikovsky.workly.security.SecurityConstants.SECRET;

@Component
public class JwtTokenProvider {

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(now.getTime() + EXPIRATION_TIME);

        String userId = user.getId().toString();

        return JWT.create()
                .withSubject(userId)
                .withClaim("id", userId)
                .withClaim("email", user.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC512(SECRET));
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(SECRET))
                    .build()
                    .verify(token);
            return true;
        } catch (AlgorithmMismatchException | SignatureVerificationException | TokenExpiredException | InvalidClaimException e) {
            return false;
        }
    }

    public Long extractUserIdFromJwt(String token) {
        String stringId = JWT.decode(token).getClaim("id").asString();
        return Long.parseLong(stringId);
    }

}
