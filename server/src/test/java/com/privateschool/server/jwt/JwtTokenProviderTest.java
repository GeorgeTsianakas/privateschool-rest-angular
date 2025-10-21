package com.privateschool.server.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    private String jwtSecret = "testSecretKeyForJwtTokenProviderTesting";
    private String jwtTokenPrefix = "Bearer ";
    private String jwtHeaderString = "Authorization";
    private Long jwtExpirationInMs = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtTokenPrefix", jwtTokenPrefix);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtHeaderString", jwtHeaderString);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", jwtExpirationInMs);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Arrange
        String username = "testuser";
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_STUDENT")
        );

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token claims
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        assertEquals(username, claims.getSubject());
        assertEquals("ROLE_STUDENT", claims.get("roles"));
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void generateToken_WithMultipleRoles_ShouldIncludeAllRoles() {
        // Arrange
        String username = "adminuser";
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_STUDENT"),
            new SimpleGrantedAuthority("ROLE_TEACHER")
        );

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        String roles = (String) claims.get("roles");
        assertTrue(roles.contains("ROLE_STUDENT"));
        assertTrue(roles.contains("ROLE_TEACHER"));
    }

    @Test
    void getAuthentication_WithValidToken_ShouldReturnAuthentication() {
        // Arrange
        String username = "testuser";
        String token = Jwts.builder()
            .setSubject(username)
            .claim("roles", "ROLE_STUDENT")
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();

        when(request.getHeader(jwtHeaderString)).thenReturn(jwtTokenPrefix + token);

        // Act
        Authentication result = jwtTokenProvider.getAuthentication(request);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getName());
        assertNotNull(result.getAuthorities());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    void getAuthentication_WithRoleWithoutPrefix_ShouldAddRolePrefix() {
        // Arrange
        String username = "testuser";
        String token = Jwts.builder()
            .setSubject(username)
            .claim("roles", "STUDENT")
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();

        when(request.getHeader(jwtHeaderString)).thenReturn(jwtTokenPrefix + token);

        // Act
        Authentication result = jwtTokenProvider.getAuthentication(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    void getAuthentication_WithNoToken_ShouldReturnNull() {
        // Arrange
        when(request.getHeader(jwtHeaderString)).thenReturn(null);

        // Act
        Authentication result = jwtTokenProvider.getAuthentication(request);

        // Assert
        assertNull(result);
    }

    @Test
    void getAuthentication_WithInvalidTokenPrefix_ShouldReturnNull() {
        // Arrange
        when(request.getHeader(jwtHeaderString)).thenReturn("InvalidPrefix token123");

        // Act
        Authentication result = jwtTokenProvider.getAuthentication(request);

        // Assert
        assertNull(result);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = Jwts.builder()
            .setSubject("testuser")
            .claim("roles", "ROLE_STUDENT")
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();

        when(request.getHeader(jwtHeaderString)).thenReturn(jwtTokenPrefix + token);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(request);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        String expiredToken = Jwts.builder()
            .setSubject("testuser")
            .claim("roles", "ROLE_STUDENT")
            .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired 1 second ago
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();

        when(request.getHeader(jwtHeaderString)).thenReturn(jwtTokenPrefix + expiredToken);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(request);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithNoToken_ShouldReturnFalse() {
        // Arrange
        when(request.getHeader(jwtHeaderString)).thenReturn(null);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(request);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithInvalidTokenFormat_ShouldReturnFalse() {
        // Arrange
        when(request.getHeader(jwtHeaderString)).thenReturn("InvalidTokenFormat");

        // Act
        boolean isValid = jwtTokenProvider.validateToken(request);

        // Assert
        assertFalse(isValid);
    }
}