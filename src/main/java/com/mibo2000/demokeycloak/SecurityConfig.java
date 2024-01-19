package com.mibo2000.demokeycloak;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(HttpMethod.GET,"/admin").hasRole("admin-role")
                        .requestMatchers(HttpMethod.GET,"/attribute").hasRole("admin-role")
                        .requestMatchers("/**").permitAll()
                )
                .oauth2ResourceServer(oauth2Configurer -> oauth2Configurer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwt -> {
                    Map<String, Map<String, Collection<String>>> resourceAccess = jwt.getClaim("resource_access");
                    Map<String, Collection<String>> resourceAccessRoles = resourceAccess.get("testing-client");
                    if (resourceAccessRoles == null || resourceAccessRoles.isEmpty()) throw new AuthenticationException("Invalid Credentials") {
                        @Override
                        public String getMessage() {
                            return super.getMessage();
                        }
                    };
                    Collection<String> roles = resourceAccessRoles.get("roles");
                    var grantedAuthorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    return new JwtAuthenticationToken(jwt, grantedAuthorities);
                })))
        ;
        return httpSecurity.build();

    }
}