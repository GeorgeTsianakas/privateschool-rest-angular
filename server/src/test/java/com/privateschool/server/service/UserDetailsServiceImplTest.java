package com.privateschool.server.service;

import com.privateschool.server.model.Role;
import com.privateschool.server.model.User;
import com.privateschool.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(Role.STUDENT);
    }

    @Test
    void testLoadUserByUsername_UserExists_StudentRole() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("johndoe");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("johndoe");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword123");

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).isNotNull();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).contains(new SimpleGrantedAuthority("ROLE_STUDENT"));

        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testLoadUserByUsername_UserExists_TeacherRole() {
        // Given
        testUser.setRole(Role.TEACHER);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("johndoe");

        // Then
        assertThat(userDetails).isNotNull();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).contains(new SimpleGrantedAuthority("ROLE_TEACHER"));

        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testLoadUserByUsername_UserExists_ManagerRole() {
        // Given
        testUser.setRole(Role.MANAGER);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("johndoe");

        // Then
        assertThat(userDetails).isNotNull();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).contains(new SimpleGrantedAuthority("ROLE_MANAGER"));

        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with username: nonexistent");

        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsername_NullUsername() {
        // Given
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with username: null");

        verify(userRepository, times(1)).findByUsername(null);
    }

    @Test
    void testLoadUserByUsername_EmptyUsername() {
        // Given
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with username: ");

        verify(userRepository, times(1)).findByUsername("");
    }

    @Test
    void testLoadUserByUsername_UserExistsWithNullPassword() {
        // Given
        testUser.setPassword(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("johndoe");

        // Then
        assertThat(userDetails).isNotNull();
        // Spring Security User doesn't accept null passwords, so null is converted to empty string
        assertThat(userDetails.getPassword()).isEmpty();
        assertThat(userDetails.getUsername()).isEqualTo("johndoe");

        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testLoadUserByUsername_UserExistsWithEmptyPassword() {
        // Given
        testUser.setPassword("");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("johndoe");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getPassword()).isEmpty();
        assertThat(userDetails.getUsername()).isEqualTo("johndoe");

        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testLoadUserByUsername_CaseSensitiveUsername() {
        // Given
        when(userRepository.findByUsername("JohnDoe")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("johndoe");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("johndoe");

        // When/Then - different case should fail
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("JohnDoe"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void testLoadUserByUsername_VerifyUserDetailsProperties() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("johndoe");

        // Then
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();

        verify(userRepository, times(1)).findByUsername("johndoe");
    }
}