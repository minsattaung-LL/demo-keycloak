package com.mibo2000.demokeycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${app.config.keycloak-client}")
    private String client;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.auth-server-url}")
    private String url;

    @Value("${app.config.keycloak-admin-user}")
    private String username;

    @Value("${app.config.keycloak-admin-password}")
    private String password;

    @Bean
    Keycloak keycloak() {
        System.out.println("####################################");
        System.out.println(String.format("Keycloak : url - %s | realm - %s | clientId - %s | clientSecret - %s | username - %s | password : %s",url, realm, client, clientSecret, username, password));
        System.out.println("####################################");
        return KeycloakBuilder.builder()
                .serverUrl(url)
                .realm(realm)
                .clientId(client)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
    }
}
