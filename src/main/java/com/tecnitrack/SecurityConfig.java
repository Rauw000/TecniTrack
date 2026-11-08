package com.tecnitrack;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class SecurityConfig {

    private final UsuarioActivoFilter usuarioActivoFilter;

    public SecurityConfig(UsuarioActivoFilter usuarioActivoFilter) {
        this.usuarioActivoFilter = usuarioActivoFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/registro",
                        "/css/**",
                        "/js/**",
                        "/img/**"
                ).permitAll()
                // Funcionalidades exclusivas del administrador
                .requestMatchers("/admin/**").hasRole("Administrador")
                // Funcionalidades exclusivas del técnico
                .requestMatchers("/tecnico/**").hasRole("Tecnico")
                // Funcionalidades exclusivas del cliente
                .requestMatchers("/cliente/**").hasRole("Cliente")
                // Las demás páginas requieren iniciar sesión
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error")
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                );
        http.addFilterAfter(
                usuarioActivoFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
// Esto permite que el filtro se ejecute únicamente dentro de la cadena de Spring Security.
    @Bean
    public FilterRegistrationBean<UsuarioActivoFilter> usuarioActivoFilterRegistration(
            UsuarioActivoFilter filter) {

        FilterRegistrationBean<UsuarioActivoFilter> registration
                = new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}
