package com.myapp.AuthConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.myapp.AuthFilter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class SecurityConfigDev {

    private final JwtAuthenticationFilter jwtFilter;
    public SecurityConfigDev(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtFilter = jwtAuthenticationFilter;
    }
   

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // JWT → no CSRF token required
            .csrf(AbstractHttpConfigurer::disable)

            // disable default login mechanisms
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // stateless session (JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/legion/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/**").hasAuthority("USER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authenticationEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler())
            )

            // ✅ register JWT filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
  

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
	
	
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
	    return (request, response, ex) -> {

	        response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.setContentType("application/json");

	        response.getWriter().write("""
	        {
	            "success": false,
	            "status": 401,
	            "message": "Unauthorized"
	        }
	        """);
	    };
	}
	
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
	    return (request, response, ex) -> {

	        response.setStatus(HttpStatus.FORBIDDEN.value());
	        response.setContentType("application/json");

	        response.getWriter().write("""
	        {
	            "success": false,
	            "status": 403,
	            "message": "Access Denied"
	        }
	        """);
	    };
	}
}