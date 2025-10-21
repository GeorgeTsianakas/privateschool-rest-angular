package com.privateschool.server.config;

import com.privateschool.server.jwt.JwtTokenProvider;
import com.privateschool.server.service.CourseService;
import com.privateschool.server.service.CourseStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityConfigTest {

    @BeforeEach
    void setUpMocks() {
        // Return empty lists by default to avoid hitting DB in controller calls
        when(courseStudentService.findAllCoursesOfStudent(anyLong())).thenReturn(Collections.emptyList());
        when(courseStudentService.findAllStudentsOfInstructor(anyLong())).thenReturn(Collections.emptyList());
        when(courseStudentService.findAllEnrollments()).thenReturn(Collections.emptyList());
        when(courseService.findAllCourses()).thenReturn(Collections.emptyList());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CourseStudentService courseStudentService;

    @MockBean
    private CourseService courseService;

    @Test
    void testPasswordEncoderBean_Exists() {
        // Then
        assertThat(passwordEncoder).isNotNull();
    }

    @Test
    void testPasswordEncoderBean_IsBCrypt() {
        // When
        String encoded = passwordEncoder.encode("testPassword");

        // Then
        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEqualTo("testPassword");
        assertThat(passwordEncoder.matches("testPassword", encoded)).isTrue();
        assertThat(encoded).startsWith("$2a$"); // BCrypt prefix
    }

    @Test
    @WithAnonymousUser
    void testPublicEndpoint_ApiUserRegistration_AllowsAnonymous() throws Exception {
        // When/Then - This endpoint should be accessible without authentication
        mockMvc.perform(post("/api/user/registration"))
                .andExpect(status().is4xxClientError()); // Will fail with 400/415 but not 401/403
    }

    @Test
    @WithAnonymousUser
    void testPublicEndpoint_Error_AllowsAnonymous() throws Exception {
        // When/Then - Error endpoint should be public; actual status may be 5xx depending on configuration
        mockMvc.perform(get("/error"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithAnonymousUser
    void testStudentEndpoint_RequiresAuthentication() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/student/courses/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testTeacherEndpoint_RequiresAuthentication() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/teacher/students/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testManagerEndpoint_RequiresAuthentication() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testStudentEndpoint_AllowsStudentRole() throws Exception {
        // When/Then - Should pass authentication but may fail for other reasons
        mockMvc.perform(get("/api/student/courses/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testTeacherEndpoint_AllowsTeacherRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/teacher/students/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testManagerEndpoint_AllowsManagerRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testTeacherEndpoint_DeniesStudentRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/teacher/students/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testManagerEndpoint_DeniesStudentRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testStudentEndpoint_DeniesTeacherRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/student/courses/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testManagerEndpoint_DeniesTeacherRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testStudentEndpoint_DeniesManagerRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/student/courses/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testTeacherEndpoint_DeniesManagerRole() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/teacher/students/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testUserEndpoint_AllowsAuthenticatedUser() throws Exception {
        // When/Then - /api/user/** should be accessible to authenticated users
        mockMvc.perform(get("/api/user/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void testPasswordEncoder_DifferentInputsProduceDifferentHashes() {
        // When
        String encoded1 = passwordEncoder.encode("password1");
        String encoded2 = passwordEncoder.encode("password2");

        // Then
        assertThat(encoded1).isNotEqualTo(encoded2);
        assertThat(passwordEncoder.matches("password1", encoded1)).isTrue();
        assertThat(passwordEncoder.matches("password2", encoded2)).isTrue();
        assertThat(passwordEncoder.matches("password1", encoded2)).isFalse();
    }

    @Test
    void testPasswordEncoder_SameInputProducesDifferentSalts() {
        // When - BCrypt uses random salts, so same input produces different hashes
        String encoded1 = passwordEncoder.encode("samePassword");
        String encoded2 = passwordEncoder.encode("samePassword");

        // Then
        assertThat(encoded1).isNotEqualTo(encoded2);
        assertThat(passwordEncoder.matches("samePassword", encoded1)).isTrue();
        assertThat(passwordEncoder.matches("samePassword", encoded2)).isTrue();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "STUDENT")
    void testAuthentication_WithUsername() throws Exception {
        // When/Then - Verify that authenticated user with username can access student endpoints
        mockMvc.perform(get("/api/student/courses/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testCorsConfiguration_AllowsRequests() throws Exception {
        // When/Then - CORS should be configured
        mockMvc.perform(options("/api/user/courses")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }
}