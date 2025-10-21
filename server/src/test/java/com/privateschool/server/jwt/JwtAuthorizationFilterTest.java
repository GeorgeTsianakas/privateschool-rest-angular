package com.privateschool.server.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    private JwtAuthorizationFilter jwtAuthorizationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, jwtTokenProvider);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Given
        String token = "Bearer valid.jwt.token";
        request.addHeader("Authorization", token);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(any())).thenReturn(authentication);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNotNull();
        assertThat(contextAuth.getName()).isEqualTo("testuser");
        assertThat(contextAuth.getAuthorities()).contains(new SimpleGrantedAuthority("ROLE_STUDENT"));

        verify(jwtTokenProvider, times(1)).validateToken(request);
        verify(jwtTokenProvider, times(1)).getAuthentication(request);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Given
        String token = "Bearer invalid.jwt.token";
        request.addHeader("Authorization", token);

        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNull();

        verify(jwtTokenProvider, times(1)).validateToken(request);
        verify(jwtTokenProvider, never()).getAuthentication(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoToken() throws ServletException, IOException {
        // Given - No Authorization header

        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNull();

        verify(jwtTokenProvider, times(1)).validateToken(request);
        verify(jwtTokenProvider, never()).getAuthentication(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NullToken() throws ServletException, IOException {
        // Given: Simulate null token by not setting the Authorization header at all
        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNull();

        verify(jwtTokenProvider, times(1)).validateToken(request);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidTokenTeacherRole() throws ServletException, IOException {
        // Given
        String token = "Bearer valid.teacher.token";
        request.addHeader("Authorization", token);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "teacher.user",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
        );

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(any())).thenReturn(authentication);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNotNull();
        assertThat(contextAuth.getName()).isEqualTo("teacher.user");
        assertThat(contextAuth.getAuthorities()).contains(new SimpleGrantedAuthority("ROLE_TEACHER"));

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidTokenManagerRole() throws ServletException, IOException {
        // Given
        String token = "Bearer valid.manager.token";
        request.addHeader("Authorization", token);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "manager.user",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(any())).thenReturn(authentication);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNotNull();
        assertThat(contextAuth.getAuthorities()).contains(new SimpleGrantedAuthority("ROLE_MANAGER"));

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_EmptyToken() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "");

        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNull();

        verify(jwtTokenProvider, times(1)).validateToken(request);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_TokenWithoutBearerPrefix() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "invalid.format.token");

        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNull();

        verify(jwtTokenProvider, times(1)).validateToken(request);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NullAuthenticationFromProvider() throws ServletException, IOException {
        // Given
        String token = "Bearer valid.jwt.token";
        request.addHeader("Authorization", token);

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(any())).thenReturn(null);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(contextAuth).isNull();

        verify(jwtTokenProvider, times(1)).validateToken(request);
        verify(jwtTokenProvider, times(1)).getAuthentication(request);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_FilterChainContinues() throws ServletException, IOException {
        // Given
        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_SecurityContextIsSet() throws ServletException, IOException {
        // Given
        String token = "Bearer valid.jwt.token";
        request.addHeader("Authorization", token);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(any())).thenReturn(authentication);

        // Verify context is clear before
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // When
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}