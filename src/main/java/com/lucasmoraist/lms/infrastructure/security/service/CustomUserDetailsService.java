package com.lucasmoraist.lms.infrastructure.security.service;

import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IdentityPersistence identityPersistence;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Identity user = this.identityPersistence.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name())));

        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

}
