package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.adapter.web.dto.user.UpdateUserDTO;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserCaseTest {

    @InjectMocks
    UpdateUserCase updateUserCase;
    @Mock
    GetCurrentUserCase getCurrentUserCase;
    @Mock
    IdentityPersistence identityPersistence;
    @Mock
    ModelMapper modelMapper;

    @Test
    @DisplayName("Should update user and return saved identity")
    void case01() {
        String traceId = "trace-1";
        String authorization = "Bearer token";
        UpdateUserDTO dto = mock(UpdateUserDTO.class);

        Identity existing = new Identity();
        Identity saved = new Identity();

        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(existing);
        when(identityPersistence.save(existing)).thenReturn(saved);

        Identity result = updateUserCase.execute(traceId, authorization, dto);

        assertSame(saved, result);

        InOrder inOrder = inOrder(getCurrentUserCase, modelMapper, identityPersistence);
        inOrder.verify(getCurrentUserCase).execute(traceId, authorization);
        inOrder.verify(modelMapper).map(dto, existing);
        inOrder.verify(identityPersistence).save(existing);

        verifyNoMoreInteractions(getCurrentUserCase, modelMapper, identityPersistence);
    }

    @Test
    @DisplayName("Should propagate AuthenticationException when getCurrentUserCase throws")
    void case02() {
        String traceId = "trace-2";
        String authorization = "Bearer token";
        UpdateUserDTO dto = mock(UpdateUserDTO.class);

        when(getCurrentUserCase.execute(traceId, authorization))
                .thenThrow(new AuthenticationException("no auth"));

        AuthenticationException ex = assertThrows(AuthenticationException.class, () ->
                updateUserCase.execute(traceId, authorization, dto)
        );

        assertEquals("no auth", ex.getMessage());

        verifyNoInteractions(modelMapper, identityPersistence);
    }

}