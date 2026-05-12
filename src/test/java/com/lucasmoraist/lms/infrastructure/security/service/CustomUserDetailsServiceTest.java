package com.lucasmoraist.lms.infrastructure.security.service;

import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Role;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    IdentityPersistence identityPersistence;
    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should load user details by username successfully")
    void case01() {
        final String username = "johndoe@email.com";
        final String password = "password123";
        final Role role = mock(Role.class);
        final Identity identity = mock(Identity.class);

        when(identityPersistence.findByEmail(eq(username))).thenReturn(Optional.of(identity));
        when(identity.getEmail()).thenReturn(username);
        when(identity.getPassword()).thenReturn(password);
        when(identity.getRoles()).thenReturn(Set.of(role));
        when(role.getName()).thenReturn(RoleType.INSTRUCTOR);

        UserDetails userDetailsResponse = this.customUserDetailsService.loadUserByUsername(username);

        assertEquals(username, userDetailsResponse.getUsername());
        assertEquals(password, userDetailsResponse.getPassword());
        assertEquals(1, userDetailsResponse.getAuthorities().size());
        assertEquals("ROLE_INSTRUCTOR", userDetailsResponse.getAuthorities().iterator().next().getAuthority());
        verify(identityPersistence, times(1)).findByEmail(eq(username));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void case02() {
        final String username = "johndoe@email.com";

        when(identityPersistence.findByEmail(eq(username))).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> this.customUserDetailsService.loadUserByUsername(username));

        verify(identityPersistence, times(1)).findByEmail(eq(username));
    }

}