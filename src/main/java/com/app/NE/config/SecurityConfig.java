package com.app.NE.config;

import com.app.NE.enums.ERole;
import com.app.NE.security.handlers.CustomAccessDeniedHandler;
import com.app.NE.security.handlers.JwtAuthenticationEntryPoint;
import com.app.NE.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configure(http));
        http.authorizeHttpRequests(req -> req
                .requestMatchers("/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/error",
                        "/swagger-resources/**",
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/actuator/**",
                        "/api-docs/**"
                        ).permitAll()
                // ADMIN && MANAGER AUTHORISED REQs
                .requestMatchers("/api/v1/employement/activate/", "/api/v1/employement/deactivate/", "/api/v1/payroll/slips/", "/api/v1/deduction/all", "/api/v1/deduction/create", "/api/v1/deduction/delete/", "/api/v1/deduction/update/").hasAnyRole("ADMIN", "MANAGER")
                // ADMIN AUTHORISED REQs
                .requestMatchers("/api/v1/payroll/approve", "/api/v1/employee/all-paginated").hasAnyRole("ADMIN")

                // MANAGER AUTHORISED REQs
                .requestMatchers("/api/v1/employee/register", "/api/v1/employee/get-my-employees", "/api/v1/employement/register", "/api/v1/employement/update/", "/api/v1/payroll/my-slips", "/api/v1/payroll/process").hasAnyRole("MANAGER")
                .anyRequest().authenticated());

        http.exceptionHandling(
                exception -> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );
        http.exceptionHandling(
                exception -> exception.accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
