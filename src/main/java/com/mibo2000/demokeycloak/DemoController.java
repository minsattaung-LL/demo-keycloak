package com.mibo2000.demokeycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DemoController {
    private static final String URL_PREFIX = "http://localhost:8080";
    private static final String SIGN_UP_URL_POSTFIX = "/admin/realms/testing-realm/users";

    final Keycloak keycloak;

    final AppConfig appConfig;

    @GetMapping("/customer")
    public ResponseEntity<?> getCustomer() {
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(createRequest(request));
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAdmin(Principal principal, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok("SUCCESS : " + principal.getName() + " | name : " + jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/attribute")
    public ResponseEntity<?> getAttributes(@AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok(Collections.singletonMap("code", principal.getClaimAsString("code")));
    }

    private String createRequest(Map<String, String> request) {
        RestTemplate restTemplate = new RestTemplate();
        log.info("request : " + request);
        Map<String, Object> userMap = new HashMap<>();

        userMap.put("username", request.get("username"));
        userMap.put("email", request.get("email"));
        userMap.put("enabled", true);

        List<Map<String, String>> credentials = new ArrayList<>();
        Map<String, String> credentialMap = new HashMap<>();
        credentialMap.put("type", "password");
        credentialMap.put("value", request.get("password"));
        credentials.add(credentialMap);

        userMap.put("credentials", credentials);

        HashMap<String, List<String>> map = new HashMap<>();
        List<String> code = new ArrayList<>();
        code.add(request.get("code"));
        map.put("code", code);
        userMap.put("attributes", map);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(keycloak.tokenManager().getAccessToken().getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(userMap, httpHeaders);
        log.info("body {}", requestEntity.getBody());
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(URL_PREFIX + SIGN_UP_URL_POSTFIX, requestEntity, Object.class);

            String[] uri = response.getHeaders().get("Location").get(0).split("/");
            String userId = uri[uri.length - 1];

            log.info("Info : " + response);

            if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(201))) {
                addRoleToUser(userId);
                log.info("User done");
                return userId;
            } else {
                throw new RuntimeException();
            }
        } catch (Exception ex) {
            log.error("error : " + ex);
            throw new RuntimeException();
        }
    }

    private void addRoleToUser(String userId) {
        String rolename = "admin-role";
        log.info("AddRoleToUser : {}", userId + " " + rolename);
        String client_id = keycloak.realm(appConfig.getKeycloakRealm()).clients().findByClientId(appConfig.getKeycloakClient()).get(0).getId();
        UserResource user = keycloak.realm(appConfig.getKeycloakRealm()).users().get(userId);

        List<RoleRepresentation> roleToAdd = new LinkedList<>();
        roleToAdd.add(keycloak.realm(appConfig.getKeycloakRealm()).clients().get(client_id).roles().get(rolename).toRepresentation());
        user.roles().clientLevel(client_id).add(roleToAdd);
    }
}
