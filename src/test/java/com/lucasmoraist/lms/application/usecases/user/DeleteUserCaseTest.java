package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import com.lucasmoraist.lms.infrastructure.security.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUserCaseTest {

    @InjectMocks
    DeleteUserCase deleteUserCase;
    @Mock
    TokenService tokenService;
    @Mock
    IdentityPersistence identityPersistence;

    String traceId;

    @BeforeEach
    void setUp() {
        traceId = TraceIdUtils.generateTraceId();
    }

    @Test
    @DisplayName("Should delete user successfully when valid token is provided")
    void case01() {
        final String authorization = "Bearer valid-token";
        final String subject = "123e4567-e89b-12d3-a456-426614174000";

        when(tokenService.getSubjectFromToken(any())).thenReturn(subject);

        deleteUserCase.execute(traceId, authorization);

        verify(tokenService, times(1)).getSubjectFromToken(any());
        verify(identityPersistence, times(1)).deleteByEntity(any());
    }

    @Test
    @DisplayName("Should throw AuthenticationException when no Bearer token is provided")
    void case02() {
        final String authorization = "Invalid authorization header";

        assertThrows(AuthenticationException.class,
                () -> deleteUserCase.execute(traceId, authorization));

        verifyNoInteractions(tokenService, identityPersistence);
    }

}