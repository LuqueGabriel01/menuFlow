package com.gabriel.springboot.app.menuflow.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JwtUtil.class)
@TestPropertySource(properties = {
        "app.jwt.secret=jdhtojHtysualkhYgdmei54K8ohdjiey",
        "app.jwt.expiration=3600"
})
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("It should generate a valid token and extract the username and roles correctly.")
    void testJwt(){

        String username = "chefUser";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CHEF"));

        String token = jwtUtil.generateToken(username, authorities);

        assertAll("Token integrity validation",
                () -> assertTrue(jwtUtil.validateToken(token), "The token should be valid."),
                () -> assertEquals(username, jwtUtil.getUsernameFromToken(token), "The username should match."),
                () -> assertNotNull(token, "The token should not be null.")
        );
    }
    @Test
    @DisplayName("It should fail when validating a malformed or altered token.")
    void shouldFailWithInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.thisIsAnInvalidToken.Signature";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid, "An invented token should not be valid.");
    }

    @Test
    @DisplayName("It should fail if the token is empty or null.")
    void shouldReturnFalseForEmptyToken() {
        assertFalse(jwtUtil.validateToken(""), "An empty token should be invalid.");
        assertFalse(jwtUtil.validateToken(null), "A null token must be invalid.");
    }
}
