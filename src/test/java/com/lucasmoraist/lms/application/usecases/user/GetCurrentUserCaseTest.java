package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.application.utils.HeaderUtils;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import com.lucasmoraist.lms.infrastructure.security.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserCaseTest {

    @InjectMocks
    GetCurrentUserCase getCurrentUserCase;
    @Mock
    TokenService tokenService;
    @Mock
    IdentityPersistence identityPersistence;

    @Test
    @DisplayName("Should throw AuthenticationException when no Bearer token is present in Authorization header")
    void case01() {
        String traceId = "trace-1";
        String authorization = "NoBearerHeader";

        try (MockedStatic<HeaderUtils> headerUtils = Mockito.mockStatic(HeaderUtils.class)) {
            headerUtils.when(() -> HeaderUtils.getBearerToken(authorization)).thenReturn(null);

            AuthenticationException ex = assertThrows(AuthenticationException.class, () ->
                    getCurrentUserCase.execute(traceId, authorization)
            );

            assertTrue(ex.getMessage().contains("No Bearer token"));
            verifyNoInteractions(tokenService, identityPersistence);
        }
    }

    @Test
    @DisplayName("Should throw AuthenticationException when user is not found for the subject in token")
    void case02() {
        String traceId = "trace-2";
        String authorization = "Bearer token-123";
        String token = "token-123";
        UUID subjectUuid = UUID.randomUUID();
        String subject = subjectUuid.toString();

        try (MockedStatic<HeaderUtils> headerUtils = Mockito.mockStatic(HeaderUtils.class)) {
            headerUtils.when(() -> HeaderUtils.getBearerToken(authorization)).thenReturn(token);
            when(tokenService.getSubjectFromToken(token)).thenReturn(subject);
            when(identityPersistence.findById(subjectUuid)).thenReturn(Optional.empty());

            AuthenticationException ex = assertThrows(AuthenticationException.class, () ->
                    getCurrentUserCase.execute(traceId, authorization)
            );

            assertTrue(ex.getMessage().contains("User not found"));
            verify(tokenService, times(1)).getSubjectFromToken(token);
            verify(identityPersistence, times(1)).findById(subjectUuid);
        }
    }

    @Test
    @DisplayName("Should return Identity when user is found for the subject in token")
    void case03() {
        String traceId = "trace-3";
        String authorization = "Bearer token-abc";
        String token = "token-abc";
        UUID subjectUuid = UUID.randomUUID();
        String subject = subjectUuid.toString();

        Identity identity = new Identity();

        try (MockedStatic<HeaderUtils> headerUtils = Mockito.mockStatic(HeaderUtils.class)) {
            headerUtils.when(() -> HeaderUtils.getBearerToken(authorization)).thenReturn(token);
            when(tokenService.getSubjectFromToken(token)).thenReturn(subject);
            when(identityPersistence.findById(subjectUuid)).thenReturn(Optional.of(identity));

            Identity result = getCurrentUserCase.execute(traceId, authorization);

            assertSame(identity, result);
            verify(tokenService, times(1)).getSubjectFromToken(token);
            verify(identityPersistence, times(1)).findById(subjectUuid);
        }
    }

}