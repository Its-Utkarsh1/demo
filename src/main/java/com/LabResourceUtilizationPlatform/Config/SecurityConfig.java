package com.LabResourceUtilizationPlatform.Config;

import com.LabResourceUtilizationPlatform.Security.AuthEntryPointJwt;
import com.LabResourceUtilizationPlatform.Security.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final AuthEntryPointJwt authEntryPointJwt;

   @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Authentication
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // System Admin
                        .requestMatchers(
                                "/api/system/**"
                        ).hasRole("SYSTEM_ADMIN")

                        // Institution Admin
                        .requestMatchers(
                                "/api/institutions/**",
                                "/api/departments/**"
                        ).hasAnyRole("SYSTEM_ADMIN", "INSTITUTION_ADMIN")

                        // Labs
                        .requestMatchers(
                                "/api/labs/**"
                        ).hasAnyRole(
                                "SYSTEM_ADMIN",
                                "INSTITUTION_ADMIN",
                                "DEPARTMENT_HEAD",
                                "LAB_MANAGER",
                                "LAB_TECHNICIAN",
                                "PROFESSOR",
                                "ASSOCIATE_PROFESSOR",
                                "ASSISTANT_PROFESSOR",
                                "RESEARCHER",
                                "RESEARCH_ASSOCIATE",
                                "RESEARCH_SCIENTIST",
                                "STUDENT"
                        )

                        // Equipment
                        .requestMatchers(
                                "/api/equipment/**"
                        ).hasAnyRole(
                                "SYSTEM_ADMIN",
                                "INSTITUTION_ADMIN",
                                "DEPARTMENT_HEAD",
                                "LAB_MANAGER",
                                "LAB_TECHNICIAN",
                                "PROFESSOR",
                                "ASSOCIATE_PROFESSOR",
                                "ASSISTANT_PROFESSOR",
                                "RESEARCHER",
                                "RESEARCH_ASSOCIATE",
                                "RESEARCH_SCIENTIST",
                                "STUDENT"
                        )

                        // Bookings
                        .requestMatchers("/api/bookings/**")
                        .hasAnyRole(
                                "SYSTEM_ADMIN",
                                "INSTITUTION_ADMIN",
                                "DEPARTMENT_HEAD",
                                "LAB_MANAGER",
                                "PROFESSOR",
                                "ASSOCIATE_PROFESSOR",
                                "ASSISTANT_PROFESSOR",
                                "RESEARCHER",
                                "RESEARCH_ASSOCIATE",
                                "RESEARCH_SCIENTIST",
                                "STUDENT"
                        )

                        // Dashboard
                        .requestMatchers(
                                "/api/dashboard/**"
                        ).authenticated()

                        // Maintenance
                        .requestMatchers(
                                "/api/maintenance/**"
                        ).hasAnyRole(
                                "SYSTEM_ADMIN",
                                "INSTITUTION_ADMIN",
                                "DEPARTMENT_HEAD",
                                "LAB_MANAGER",
                                "LAB_TECHNICIAN"
                        )

                        // Sharing
                        .requestMatchers(
                                "/api/sharing/**"
                        ).hasAnyRole(
                                "SYSTEM_ADMIN",
                                "INSTITUTION_ADMIN",
                                "DEPARTMENT_HEAD",
                                "LAB_MANAGER",
                                "RESEARCHER",
                                "RESEARCH_ASSOCIATE",
                                "RESEARCH_SCIENTIST"
                        )

                        // Notifications
                        .requestMatchers(
                                "/api/notifications/**"
                        ).authenticated()

                        // Admin/User Management
                        .requestMatchers(
                                "/api/users/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                );

        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt));

        httpSecurity.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return  httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
