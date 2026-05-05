package com.lucasmoraist.lms.infrastructure.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lucasmoraist.lms.domain.exceptions.CertificateException;
import com.lucasmoraist.lms.domain.exceptions.TokenException;
import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.domain.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TokenService implements TokenGateway {

    // TODO: Pensar em modos de implementar um refresh token para evitar que o usuário precise logar novamente a cada hora, ou seja, a cada expiração do token.

    private static final Integer EXPIRATION_TIME_IN_SECONDS = 3600; // 1 hour

    private final String applicationName;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public TokenService(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
        this.privateKey = loadPrivateKey();
        this.publicKey = loadPublicKey();
    }

    @Override
    public Token generateToken(Identity identity) {
        try {
            final List<String> roleNames = identity.getRoles().stream()
                    .map(role -> role.getName().name())
                    .toList();

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            String token = JWT.create()
                    .withIssuer(applicationName)
                    .withAudience(applicationName)
                    .withIssuedAt(Instant.now())
                    .withJWTId(UUID.randomUUID().toString())
                    .withExpiresAt(generateExpirationDate())
                    .withSubject(identity.getId().toString())
                    .withClaim("role", roleNames)
                    .withClaim("isActive", identity.getIsActive())
                    .withClaim("name", identity.getProfile().getName())
                    .withClaim("email", identity.getEmail())
                    .sign(algorithm);
            return new Token(token, EXPIRATION_TIME_IN_SECONDS);
        } catch (JWTCreationException e) {
            log.error("Error creating JWT token", e);
            throw new TokenException("Error creating JWT token", e);
        }
    }

    @Override
    public String getSubjectFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            return JWT.require(algorithm)
                    .withIssuer("learning-management-system")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            log.error("Token validation failed", e);
            throw new TokenException("Token validation failed", e);
        }
    }

    @Override
    public String getClaimFromToken(String token, String claim) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            return JWT.require(algorithm)
                    .withIssuer("learning-management-system")
                    .build()
                    .verify(token)
                    .getClaim(claim)
                    .asString();
        } catch (JWTVerificationException e) {
            log.error("Token validation failed", e);
            throw new TokenException("Token validation failed", e);
        }
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusSeconds(EXPIRATION_TIME_IN_SECONDS).toInstant(ZoneOffset.of("-03:00"));
    }

    private PrivateKey loadPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource("lms_pv_key.pem");
            String privateKeyPem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            log.error("Failed to load private key from file", e);
            throw new CertificateException("Failed to read private key file", e);
        }
    }

    public PublicKey loadPublicKey() {
        try {
            ClassPathResource resource = new ClassPathResource("lms_pb_key.pem");
            String publicKeyPem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(publicKeyPem);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            log.error("Failed to load public key", e);
            throw new CertificateException("Failed to load public key", e);
        }
    }

}
