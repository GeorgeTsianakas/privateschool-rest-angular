package com.privateschool.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateschool.server.jwt.JwtTokenProvider;
import com.privateschool.server.model.Course;
import com.privateschool.server.model.CourseStudent;
import com.privateschool.server.model.Role;
import com.privateschool.server.model.User;
import com.privateschool.server.service.CourseService;
import com.privateschool.server.service.CourseStudentService;
import com.privateschool.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private UserService userService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private CourseStudentService courseStudentService;

    private User testUser;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setName("Test User");
        testUser.setRole(Role.STUDENT);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Test Course");
    }

    @Test
    @WithMockUser
    void register_WithNewUser_ShouldReturnCreated() throws Exception {
        // Arrange
        when(userService.findByUsername(anyString())).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService).findByUsername("testuser");
        verify(userService).saveUser(any(User.class));
    }

    @Test
    @WithMockUser
    void register_WithExistingUsername_ShouldReturnConflict() throws Exception {
        // Arrange
        when(userService.findByUsername(anyString())).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isConflict());

        verify(userService).findByUsername("testuser");
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithMockUser
    void register_ShouldSetRoleToStudent() throws Exception {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setName("New User");

        when(userService.findByUsername(anyString())).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });

        // Act & Assert
        mockMvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());

        verify(userService).saveUser(argThat(user -> user.getRole() == Role.STUDENT));
    }

    @Test
    @WithMockUser
    void getUser_WithAuthenticatedUser_ShouldReturnUserWithToken() throws Exception {
        // Arrange
        String token = "generated-jwt-token";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            "testuser", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(tokenProvider.generateToken(any())).thenReturn(token);

        // Act & Assert
        mockMvc.perform(get("/api/user/login")
                .with(authentication(authToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").value(token));

        verify(userService).findByUsername("testuser");
        verify(tokenProvider).generateToken(any());
    }

    @Test
    @WithMockUser
    void getUser_WithNoPrincipal_ShouldReturnOk() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/login"))
                .andExpect(status().isOk());

        verify(userService, never()).findByUsername(anyString());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @WithMockUser
    void enrollCourse_ShouldReturnCreated() throws Exception {
        // Arrange
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setId(1L);

        when(courseStudentService.saveCourseStudent(any(CourseStudent.class))).thenReturn(courseStudent);

        // Act & Assert
        mockMvc.perform(post("/api/user/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(courseStudentService).saveCourseStudent(any(CourseStudent.class));
    }

    @Test
    @WithMockUser
    void getAllCourses_ShouldReturnListOfCourses() throws Exception {
        // Arrange
        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Test Course 2");

        List<Course> courses = Arrays.asList(testCourse, course2);
        when(courseService.findAllCourses()).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/user/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Test Course"))
                .andExpect(jsonPath("$[1].name").value("Test Course 2"));

        verify(courseService).findAllCourses();
    }

    @Test
    @WithMockUser
    void getAllCourses_WhenNoCourses_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(courseService.findAllCourses()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/user/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseService).findAllCourses();
    }
}