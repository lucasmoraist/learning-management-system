package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.adapter.web.dto.user.CreateUserDTO;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.exceptions.UniqueKeyDatabaseException;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserCaseTest {

    @InjectMocks
    CreateUserCase createUserCase;
    @Mock
    IdentityPersistence identityPersistence;
    @Mock
    PasswordEncoder passwordEncoder;

    String traceId;
    CreateUserDTO dto;

    @BeforeEach
    void setUp() {
        traceId = TraceIdUtils.generateTraceId();
        dto = new CreateUserDTO(
                "John Doe",
                LocalDate.of(2000, 1, 1),
                "86100550865",
                "johndoe@email.com",
                "password123",
                RoleType.STUDENT
        );
    }

    @Test
    @DisplayName("Should create a new user successfully")
    void case01() {
        final Identity identity = mock(Identity.class);

        when(identityPersistence.save(any())).thenReturn(identity);
        when(identity.getId()).thenReturn(UUID.randomUUID());

        assertDoesNotThrow(() -> createUserCase.execute(traceId, dto));

        verify(identityPersistence, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    @DisplayName("Should throw UniqueKeyDatabaseException when email already exists")
    void case02() {
        when(identityPersistence.findByEmail(any()))
                .thenThrow(new UniqueKeyDatabaseException("User with email " + dto.email() + " already exists"));

        assertThrows(UniqueKeyDatabaseException.class,
                () -> createUserCase.execute(traceId, dto));

        verify(identityPersistence, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

}