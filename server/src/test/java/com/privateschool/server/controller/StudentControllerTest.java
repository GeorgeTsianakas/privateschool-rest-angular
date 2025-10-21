package com.privateschool.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseStudentService courseStudentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User testStudent;
    private User testInstructor;
    private Course testCourse1;
    private Course testCourse2;
    private CourseStudent enrollment1;
    private CourseStudent enrollment2;

    @BeforeEach
    void setUp() {
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setName("Jane Student");
        testStudent.setUsername("jane.student");
        testStudent.setRole(Role.STUDENT);

        testInstructor = new User();
        testInstructor.setId(2L);
        testInstructor.setName("John Teacher");
        testInstructor.setUsername("john.teacher");
        testInstructor.setRole(Role.TEACHER);

        testCourse1 = new Course();
        testCourse1.setId(1L);
        testCourse1.setName("Java Programming");
        testCourse1.setInstructor(testInstructor);

        testCourse2 = new Course();
        testCourse2.setId(2L);
        testCourse2.setName("Python Programming");
        testCourse2.setInstructor(testInstructor);

        enrollment1 = new CourseStudent();
        enrollment1.setId(1L);
        enrollment1.setStudent(testStudent);
        enrollment1.setCourse(testCourse1);

        enrollment2 = new CourseStudent();
        enrollment2.setId(2L);
        enrollment2.setStudent(testStudent);
        enrollment2.setCourse(testCourse2);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetCoursesOfStudent_WithCourses() throws Exception {
        // Given
        Long studentId = 1L;
        List<CourseStudent> enrollments = Arrays.asList(enrollment1, enrollment2);
        when(courseStudentService.findAllCoursesOfStudent(studentId)).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Programming"))
                .andExpect(jsonPath("$[0].instructor.id").value(2))
                .andExpect(jsonPath("$[0].instructor.name").value("John Teacher"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Python Programming"));

        verify(courseStudentService, times(1)).findAllCoursesOfStudent(studentId);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetCoursesOfStudent_EmptyList() throws Exception {
        // Given
        Long studentId = 999L;
        when(courseStudentService.findAllCoursesOfStudent(studentId)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseStudentService, times(1)).findAllCoursesOfStudent(studentId);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetCoursesOfStudent_SingleCourse() throws Exception {
        // Given
        Long studentId = 1L;
        List<CourseStudent> enrollments = Collections.singletonList(enrollment1);
        when(courseStudentService.findAllCoursesOfStudent(studentId)).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Programming"));

        verify(courseStudentService, times(1)).findAllCoursesOfStudent(studentId);
    }

    @Test
    void testGetCoursesOfStudent_Unauthorized() throws Exception {
        // Given
        Long studentId = 1L;

        // When/Then
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isUnauthorized());

        verify(courseStudentService, never()).findAllCoursesOfStudent(anyLong());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testEnrollStudent_Success() throws Exception {
        // Given
        CourseStudent newEnrollment = new CourseStudent();
        newEnrollment.setStudent(testStudent);
        newEnrollment.setCourse(testCourse1);

        CourseStudent savedEnrollment = new CourseStudent();
        savedEnrollment.setId(3L);
        savedEnrollment.setStudent(testStudent);
        savedEnrollment.setCourse(testCourse1);

        when(courseStudentService.saveCourseStudent(any(CourseStudent.class))).thenReturn(savedEnrollment);

        // When/Then
        mockMvc.perform(post("/api/student/enroll")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEnrollment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.student.id").value(1))
                .andExpect(jsonPath("$.course.id").value(1));

        verify(courseStudentService, times(1)).saveCourseStudent(any(CourseStudent.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testEnrollStudent_WithValidData() throws Exception {
        // Given
        CourseStudent enrollment = new CourseStudent();
        enrollment.setId(5L);
        enrollment.setStudent(testStudent);
        enrollment.setCourse(testCourse2);

        when(courseStudentService.saveCourseStudent(any(CourseStudent.class))).thenReturn(enrollment);

        // When/Then
        mockMvc.perform(post("/api/student/enroll")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.student.name").value("Jane Student"))
                .andExpect(jsonPath("$.course.name").value("Python Programming"));

        verify(courseStudentService, times(1)).saveCourseStudent(any(CourseStudent.class));
    }

    @Test
    void testEnrollStudent_Unauthorized() throws Exception {
        // Given
        CourseStudent enrollment = new CourseStudent();

        // When/Then
        mockMvc.perform(post("/api/student/enroll")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollment)))
                .andExpect(status().isUnauthorized());

        verify(courseStudentService, never()).saveCourseStudent(any(CourseStudent.class));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetCoursesOfStudent_ForbiddenForTeacher() throws Exception {
        // Given
        Long studentId = 1L;

        // When/Then - Teacher role should not have access to student endpoints
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isForbidden());

        verify(courseStudentService, never()).findAllCoursesOfStudent(anyLong());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetCoursesOfStudent_ForbiddenForManager() throws Exception {
        // Given
        Long studentId = 1L;

        // When/Then - Manager role should not have access to student endpoints
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isForbidden());

        verify(courseStudentService, never()).findAllCoursesOfStudent(anyLong());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetCoursesOfStudent_WithZeroId() throws Exception {
        // Given
        Long studentId = 0L;
        when(courseStudentService.findAllCoursesOfStudent(studentId)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseStudentService, times(1)).findAllCoursesOfStudent(studentId);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetCoursesOfStudent_WithNegativeId() throws Exception {
        // Given
        Long studentId = -1L;
        when(courseStudentService.findAllCoursesOfStudent(studentId)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/api/student/courses/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseStudentService, times(1)).findAllCoursesOfStudent(studentId);
    }
}