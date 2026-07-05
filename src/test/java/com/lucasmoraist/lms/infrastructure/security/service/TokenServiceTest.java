package com.lucasmoraist.lms.infrastructure.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.exceptions.TokenException;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.domain.model.user.Role;
import com.lucasmoraist.lms.domain.model.auth.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();

        String applicationName = "learning-management-system";
        ReflectionTestUtils.setField(tokenService, "applicationName", applicationName);
        ReflectionTestUtils.setField(tokenService, "publicKey", publicKey);
        ReflectionTestUtils.setField(tokenService, "privateKey", privateKey);
    }

    private Identity createMockIdentity() {
        Role role = Role.builder().name(RoleType.USER).build();
        Profile profile = Profile.builder().name("John Doe").build();

        return Identity.builder()
                .id(UUID.randomUUID())
                .email("johndoe@email.com")
                .isActive(true)
                .roles(Set.of(role))
                .profile(profile)
                .build();
    }

    @Nested
    @DisplayName("Scenarios for generateToken")
    class GenerateToken {

        @Test
        @DisplayName("Should generate a valid JWT token when the identity is provided.")
        void case01() {
            Identity identity = createMockIdentity();

            Token result = tokenService.generateToken(identity);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isNotBlank();
            assertThat(result.getExpiresIn()).isEqualTo(3600);

            DecodedJWT decodedJWT = JWT.require(Algorithm.RSA256(publicKey, privateKey)).build().verify(result.getAccessToken());
            assertThat(decodedJWT.getSubject()).isEqualTo(identity.getId().toString());
            assertThat(decodedJWT.getClaim("email").asString()).isEqualTo(identity.getEmail());
            assertThat(decodedJWT.getClaim("role").asList(String.class)).contains("USER");
        }

        @Test
        @DisplayName("Should throw TokenException when there is an error during token generation")
        void case02() {
            ReflectionTestUtils.setField(tokenService, "privateKey", null);
            Identity identity = createMockIdentity();

            assertThatThrownBy(() -> tokenService.generateToken(identity))
                    .isInstanceOf(TokenException.class)
                    .hasMessageContaining("Error creating JWT token");
        }
    }

    @Nested
    @DisplayName("Scenarios for getSubjectFromToken")
    class GetSubjectFromToken {

        @Test
        @DisplayName("Should return the subject from a valid JWT token.")
        void case01() {
            Identity identity = createMockIdentity();
            String token = tokenService.generateToken(identity).getAccessToken();

            String subject = tokenService.getSubjectFromToken(token);

            assertThat(subject).isEqualTo(identity.getId().toString());
        }

        @Test
        @DisplayName("Should throw TokenException when the token is invalid or cannot be verified.")
        void case02() {
            String invalidToken = "invalid.token.string";

            assertThatThrownBy(() -> tokenService.getSubjectFromToken(invalidToken))
                    .isInstanceOf(TokenException.class)
                    .hasMessageContaining("Token validation failed");
        }
    }

    @Nested
    @DisplayName("Scenarios for getClaimFromToken")
    class GetClaimsFromToken {

        @Test
        @DisplayName("Should return the specified claim from a valid JWT token.")
        void case01() {
            Identity identity = createMockIdentity();
            String token = tokenService.generateToken(identity).getAccessToken();

            String email = tokenService.getClaimFromToken(token, "email");
            String name = tokenService.getClaimFromToken(token, "name");

            assertThat(email).isEqualTo("johndoe@email.com");
            assertThat(name).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should throw TokenException when the token is invalid or cannot be verified.")
        void case02() {
            assertThatThrownBy(() -> tokenService.getClaimFromToken("token-wrong", "email"))
                    .isInstanceOf(TokenException.class)
                    .hasMessageContaining("Token validation failed");
        }
    }

}