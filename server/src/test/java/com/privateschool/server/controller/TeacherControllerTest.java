package com.privateschool.server.controller;

import com.privateschool.server.jwt.JwtTokenProvider;
import com.privateschool.server.model.Course;
import com.privateschool.server.model.CourseStudent;
import com.privateschool.server.model.Role;
import com.privateschool.server.model.User;
import com.privateschool.server.service.CourseStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherController.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseStudentService courseStudentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private User testTeacher;
    private User testStudent1;
    private User testStudent2;
    private Course testCourse;
    private CourseStudent enrollment1;
    private CourseStudent enrollment2;

    @BeforeEach
    void setUp() {
        testTeacher = new User();
        testTeacher.setId(1L);
        testTeacher.setName("John Teacher");
        testTeacher.setUsername("john.teacher");
        testTeacher.setRole(Role.TEACHER);

        testStudent1 = new User();
        testStudent1.setId(2L);
        testStudent1.setName("Jane Student");
        testStudent1.setUsername("jane.student");
        testStudent1.setRole(Role.STUDENT);

        testStudent2 = new User();
        testStudent2.setId(3L);
        testStudent2.setName("Bob Student");
        testStudent2.setUsername("bob.student");
        testStudent2.setRole(Role.STUDENT);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Java Programming");
        testCourse.setInstructor(testTeacher);

        enrollment1 = new CourseStudent();
        enrollment1.setId(1L);
        enrollment1.setStudent(testStudent1);
        enrollment1.setCourse(testCourse);

        enrollment2 = new CourseStudent();
        enrollment2.setId(2L);
        enrollment2.setStudent(testStudent2);
        enrollment2.setCourse(testCourse);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStudentsOfTeacher_WithStudents() throws Exception {
        // Given
        Long teacherId = 1L;
        List<CourseStudent> enrollments = Arrays.asList(enrollment1, enrollment2);
        when(courseStudentService.findAllStudentsOfInstructor(teacherId)).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Jane Student"))
                .andExpect(jsonPath("$[0].username").value("jane.student"))
                .andExpect(jsonPath("$[0].role").value("STUDENT"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("Bob Student"))
                .andExpect(jsonPath("$[1].username").value("bob.student"));

        verify(courseStudentService, times(1)).findAllStudentsOfInstructor(teacherId);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStudentsOfTeacher_EmptyList() throws Exception {
        // Given
        Long teacherId = 999L;
        when(courseStudentService.findAllStudentsOfInstructor(teacherId)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseStudentService, times(1)).findAllStudentsOfInstructor(teacherId);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStudentsOfTeacher_SingleStudent() throws Exception {
        // Given
        Long teacherId = 1L;
        List<CourseStudent> enrollments = Collections.singletonList(enrollment1);
        when(courseStudentService.findAllStudentsOfInstructor(teacherId)).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Jane Student"));

        verify(courseStudentService, times(1)).findAllStudentsOfInstructor(teacherId);
    }

    @Test
    void testGetStudentsOfTeacher_Unauthorized() throws Exception {
        // Given
        Long teacherId = 1L;

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isUnauthorized());

        verify(courseStudentService, never()).findAllStudentsOfInstructor(anyLong());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetStudentsOfTeacher_ForbiddenForStudent() throws Exception {
        // Given
        Long teacherId = 1L;

        // When/Then - Student role should not have access to teacher endpoints
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isForbidden());

        verify(courseStudentService, never()).findAllStudentsOfInstructor(anyLong());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetStudentsOfTeacher_ForbiddenForManager() throws Exception {
        // Given
        Long teacherId = 1L;

        // When/Then - Manager role should not have access to teacher endpoints
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isForbidden());

        verify(courseStudentService, never()).findAllStudentsOfInstructor(anyLong());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStudentsOfTeacher_WithZeroId() throws Exception {
        // Given
        Long teacherId = 0L;
        when(courseStudentService.findAllStudentsOfInstructor(teacherId)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseStudentService, times(1)).findAllStudentsOfInstructor(teacherId);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStudentsOfTeacher_WithNegativeId() throws Exception {
        // Given
        Long teacherId = -1L;
        when(courseStudentService.findAllStudentsOfInstructor(teacherId)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseStudentService, times(1)).findAllStudentsOfInstructor(teacherId);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStudentsOfTeacher_VerifyStudentDetailsComplete() throws Exception {
        // Given
        Long teacherId = 1L;
        testStudent1.setPassword("encodedPassword"); // Should not be exposed in JSON
        List<CourseStudent> enrollments = Collections.singletonList(enrollment1);
        when(courseStudentService.findAllStudentsOfInstructor(teacherId)).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].role").exists());

        verify(courseStudentService, times(1)).findAllStudentsOfInstructor(teacherId);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStudentsOfTeacher_MultipleStudentsSameCourse() throws Exception {
        // Given
        Long teacherId = 1L;

        User student3 = new User();
        student3.setId(4L);
        student3.setName("Alice Student");
        student3.setUsername("alice.student");
        student3.setRole(Role.STUDENT);

        CourseStudent enrollment3 = new CourseStudent();
        enrollment3.setId(3L);
        enrollment3.setStudent(student3);
        enrollment3.setCourse(testCourse);

        List<CourseStudent> enrollments = Arrays.asList(enrollment1, enrollment2, enrollment3);
        when(courseStudentService.findAllStudentsOfInstructor(teacherId)).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/teacher/students/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Jane Student"))
                .andExpect(jsonPath("$[1].name").value("Bob Student"))
                .andExpect(jsonPath("$[2].name").value("Alice Student"));

        verify(courseStudentService, times(1)).findAllStudentsOfInstructor(teacherId);
    }
}