package application.dialog.config;

import application.dialog.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String ADMIN = "ADMINISTRATOR";
    private static final String SUPERVISOR = "SUPERVISOR";
    private static final String SELLER = "SELLER";
    private static final String BUYER = "BUYER";
    private static final String OPERATOR = "LOGISTICS_OPERATOR";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/auth/**", "/api/health").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/buyers").permitAll()

                        .requestMatchers("/api/users/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/sellers").hasRole(ADMIN)
                        .requestMatchers("/api/reports/**").hasAnyRole(ADMIN, SUPERVISOR)

                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole(SELLER, ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole(SELLER, ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole(SELLER, ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAnyRole(SELLER, ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyRole(SELLER, ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyRole(SELLER, ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyRole(SELLER, OPERATOR, ADMIN)
                        .requestMatchers("/api/inventory/**").hasAnyRole(SELLER, OPERATOR)
                        .requestMatchers("/api/warehouses/**").hasAnyRole(SELLER, OPERATOR, ADMIN)
                        .requestMatchers("/api/shipments/**").hasAnyRole(OPERATOR, ADMIN)

                        .requestMatchers("/api/carts/**").hasRole(BUYER)
                        .requestMatchers(HttpMethod.POST, "/api/orders/checkout").hasRole(BUYER)
                        .requestMatchers("/api/orders/**").hasAnyRole(BUYER, SELLER, OPERATOR, ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/returns").hasRole(BUYER)
                        .requestMatchers("/api/returns/**").hasAnyRole(BUYER, ADMIN, SUPERVISOR, OPERATOR)
                        .requestMatchers(HttpMethod.POST, "/api/refunds").hasAnyRole(BUYER, ADMIN)
                        .requestMatchers("/api/refunds/**").hasAnyRole(BUYER, ADMIN)

                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, failure) ->
                                write(response, HttpServletResponse.SC_UNAUTHORIZED, "AUTHENTICATION_REQUIRED",
                                        "This operation requires an authenticated user"))
                        .accessDeniedHandler((request, response, denied) ->
                                write(response, HttpServletResponse.SC_FORBIDDEN, "AUTHORIZATION_DENIED",
                                        "This role may not perform this operation")))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    private static void write(HttpServletResponse response, int status, String error, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":" + status + ",\"error\":\"" + error + "\",\"message\":\"" + message + "\"}");
    }
}
