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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setName("Test User");
        testUser.setRole(Role.STUDENT);
    }

    @Test
    void saveUser_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(rawPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User savedUser = userService.saveUser(testUser);

        // Assert
        assertNotNull(savedUser);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(testUser);
        assertEquals(encodedPassword, testUser.getPassword());
    }

    @Test
    void saveUser_ShouldCallRepositorySaveMethod() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.saveUser(testUser);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findByUsername(username);

        // Assert
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
        assertEquals(testUser.getId(), foundUser.getId());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnNull() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findByUsername(username);

        // Assert
        assertNull(foundUser);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void findAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setName("Test User 2");
        user2.setRole(Role.TEACHER);

        List<User> userList = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.findAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser));
        assertTrue(result.contains(user2));
        verify(userRepository).findAll();
    }

    @Test
    void findAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<User> result = userService.findAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void updateUser_ShouldSaveAndReturnUpdatedUser() {
        // Arrange
        testUser.setName("Updated Name");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User updatedUser = userService.updateUser(testUser);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_ShouldCallRepositorySaveMethod() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateUser(testUser);

        // Assert
        verify(userRepository, times(1)).save(testUser);
    }
}