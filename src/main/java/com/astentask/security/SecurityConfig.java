package com.astentask.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                         .requestMatchers(
                                 "/h2-console/**",
                                 "/swagger-ui.html",
                                 "/swagger-ui/**",
                                 "/v3/api-docs/**"
                         ).permitAll()
                        .requestMatchers("/external/import-users").hasAnyRole("ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN")
                        //autenticação
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        //projetos
                        .requestMatchers(HttpMethod.GET, "/api/projects").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/projects/*").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "VIEWER")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/*").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/projects/*/stats").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                        //tarefas
                        .requestMatchers(HttpMethod.GET, "/api/projects/*/tasks").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/projects/*/tasks").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "VIEWER")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/*").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/*/status").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/*/assign").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/tasks/*/attachments").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*/attachments").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*/attachments/*").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        //comentários
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*/comments").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/tasks/*/comments").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.PUT, "/api/comments/*").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/*").hasRole("ADMIN")
                        //registr
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*/timelogs").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.POST, "/api/tasks/*/timelogs").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.PUT, "/api/timelogs/*").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.DELETE, "/api/timelogs/*").hasRole("ADMIN")
                        //dashboard
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/overview").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "VIEWER")
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/my-tasks").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "VIEWER")
                        .requestMatchers(HttpMethod.GET, "/api/reports/project/*").hasAnyRole("ADMIN", "PROJECT_MANAGER")

                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    static class JwtAuthFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;

        public JwtAuthFilter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtil.isTokenValid(token)) {
                    String email = jwtUtil.extractEmail(token);
                    String role = jwtUtil.extractRole(token);
                    System.out.println("ROLE do token: " + role);
                    var auth = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

}

