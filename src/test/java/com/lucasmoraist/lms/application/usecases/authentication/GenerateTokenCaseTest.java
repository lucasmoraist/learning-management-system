package com.lucasmoraist.lms.application.usecases.authentication;

import com.lucasmoraist.lms.adapter.web.dto.auth.LoginDTO;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.domain.model.Token;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateTokenCaseTest {

    @InjectMocks
    GenerateTokenCase generateTokenCase;
    @Mock
    IdentityPersistence identityPersistence;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    TokenGateway tokenGateway;

    String traceId;
    LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        traceId = "test-trace-id";
        loginDTO = new LoginDTO(
                "johndoe@email.com",
                "password123"
        );
    }

    @Test
    @DisplayName("Should generate a token successfully when valid credentials are provided")
    void case01() {
        final Token expectedTokenValue = new Token(
                "test-token-value",
                3600
        );
        final Identity identity = mock(Identity.class);

        when(identityPersistence.findByEmail(anyString())).thenReturn(Optional.of(identity));
        when(identity.getPassword()).thenReturn("password123");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenGateway.generateToken(any())).thenReturn(expectedTokenValue);

        Token token = generateTokenCase.execute(traceId, loginDTO);

        assertEquals(expectedTokenValue, token);
        assertEquals(expectedTokenValue.getAccessToken(), token.getAccessToken());
        assertEquals(expectedTokenValue.getExpiresIn(), token.getExpiresIn());
        assertEquals(expectedTokenValue.getType(), token.getType());
        verify(tokenGateway, times(1)).generateToken(any());
        verify(identityPersistence, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw AuthenticationException when email is not found")
    void case02() {
        try (LogCaptor logCaptor = LogCaptor.forClass(GenerateTokenCase.class)) {
            when(identityPersistence.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class,
                    () -> generateTokenCase.execute(traceId, loginDTO));

            assertTrue(logCaptor.getWarnLogs().getFirst().contains("No user found with email"));
            verify(identityPersistence, times(1)).findByEmail(anyString());
            verifyNoInteractions(tokenGateway, passwordEncoder);
        }
    }

    @Test
    @DisplayName("Should throw AuthenticationException when password is incorrect")
    void case03() {
        try (LogCaptor logCaptor = LogCaptor.forClass(GenerateTokenCase.class)) {
            final Identity identity = mock(Identity.class);

            when(identityPersistence.findByEmail(anyString())).thenReturn(Optional.of(identity));
            when(identity.getPassword()).thenReturn("password123");
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            assertThrows(AuthenticationException.class,
                    () -> generateTokenCase.execute(traceId, loginDTO));

            assertTrue(logCaptor.getWarnLogs().getFirst().contains("Invalid password for email"));
            verify(identityPersistence, times(1)).findByEmail(anyString());
            verify(passwordEncoder, times(1)).matches(anyString(), anyString());
            verifyNoInteractions(tokenGateway);
        }
    }

}