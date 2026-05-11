package com.lucasmoraist.lms.adapter.web.filter;

import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.infrastructure.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @InjectMocks
    SecurityFilter securityFilter;

    @Mock
    TokenGateway tokenGateway;

    @Mock
    CustomUserDetailsService customUserDetailsService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should not authenticate when no Authorization header is present")
    void case01() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(tokenGateway, customUserDetailsService);
    }

    @Test
    @DisplayName("Should not authenticate when token has no email claim")
    void case02() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-123");
        when(tokenGateway.getClaimFromToken("token-123", "email")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(tokenGateway, times(1)).getClaimFromToken("token-123", "email");
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    @DisplayName("Should authenticate user when valid token and user found")
    void case03() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-abc");
        when(tokenGateway.getClaimFromToken("token-abc", "email")).thenReturn("user@example.com");

        UserDetails userDetails = new User(
                "user@example.com",
                "N/A",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        securityFilter.doFilter(request, response, filterChain);

        verify(tokenGateway, times(1)).getClaimFromToken("token-abc", "email");
        verify(customUserDetailsService, times(1)).loadUserByUsername("user@example.com");
        verify(filterChain, times(1)).doFilter(request, response);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("user@example.com", auth.getName());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Should not authenticate when user not found for email in token")
    void case04() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-xyz");
        when(tokenGateway.getClaimFromToken("token-xyz", "email")).thenReturn("missing@example.com");
        when(customUserDetailsService.loadUserByUsername("missing@example.com"))
                .thenThrow(new UsernameNotFoundException("not found"));

        securityFilter.doFilter(request, response, filterChain);

        verify(tokenGateway, times(1)).getClaimFromToken("token-xyz", "email");
        verify(customUserDetailsService, times(1)).loadUserByUsername("missing@example.com");
        verify(filterChain, times(1)).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
