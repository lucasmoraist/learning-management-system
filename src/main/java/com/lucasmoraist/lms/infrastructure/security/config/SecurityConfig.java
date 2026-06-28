package com.lucasmoraist.lms.infrastructure.security.config;

import com.lucasmoraist.lms.adapter.web.filter.SecurityFilter;
import com.lucasmoraist.lms.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    private static final String[] HEALTH_ENDPOINTS = {
            "/actuator/health",
            "/actuator/health/liveness",
            "/actuator/health/readiness"
    };

    private static final String[] SWAGGER_ENDPOINTS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    // TODO: Revisar rotar para serem bloqueadas
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 1. Endpoints Públicos (Monitoramento e Documentação)
                        .requestMatchers(HEALTH_ENDPOINTS).permitAll()
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()

                        // 2. Endpoints de Autenticação e Registro Inicial
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()

                        // 3. Controle de Usuários (UserController)
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/list").hasRole(RoleType.ADMIN.name())
                        .requestMatchers("/api/v1/users/me/**").authenticated() // /me, /me/update, /me/delete

                        // 4. Fluxo de Assinatura e Pagamentos (PaymentController)
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment/subscribe").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/payment/*/cancel").hasAnyRole(RoleType.SUBSCRIBER.name(), RoleType.ADMIN.name())

                        // 5. Criação e Edição de Catálogo (Cursos, Módulos e Lições)
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/create").hasAnyRole(RoleType.INSTRUCTOR.name(),
                                RoleType.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/modules/add/**").hasAnyRole(RoleType.INSTRUCTOR.name(), RoleType.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/lessons/add/module/**").hasAnyRole(RoleType.INSTRUCTOR.name(), RoleType.ADMIN.name())

                        // 6. Consumo de Conteúdo (Visualizar cursos e assistir lições)
                        // Exige que seja um Assinante, Instrutor ou Admin
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/**").hasAnyRole(RoleType.SUBSCRIBER.name(), RoleType.INSTRUCTOR.name(), RoleType.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/lessons/**").hasAnyRole(RoleType.SUBSCRIBER.name(), RoleType.INSTRUCTOR.name(), RoleType.ADMIN.name())

                        // Qualquer
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
