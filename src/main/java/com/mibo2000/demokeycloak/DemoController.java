package com.mibo2000.demokeycloak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;

@RestController
@Slf4j
public class DemoController {
    @GetMapping("/customer")
    public ResponseEntity<?> getCustomer() {
        return ResponseEntity.ok("SUCCESS");
    }
    @GetMapping("/admin")
    public ResponseEntity<?> getAdmin(Principal principal, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok("SUCCESS : "+principal.getName() + " | name : " + jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/attribute")
    public ResponseEntity<?> getAttributes(@AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok(Collections.singletonMap("code", principal.getClaimAsString("code")));
    }
}
