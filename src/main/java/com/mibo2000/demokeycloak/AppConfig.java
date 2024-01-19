package com.mibo2000.demokeycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.config")
@Configuration
@Getter
@Setter
public class AppConfig {

    private String keycloakAdminUser;

    private String keycloakAdminPassword;

    private String keycloakClient;

    private String keycloakClientSecret;

    private String keycloakRealm;
}
