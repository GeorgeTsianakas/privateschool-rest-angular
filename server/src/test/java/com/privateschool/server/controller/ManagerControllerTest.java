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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseStudentService courseStudentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private User testStudent1;
    private User testStudent2;
    private User testTeacher;
    private Course testCourse1;
    private Course testCourse2;
    private CourseStudent enrollment1;
    private CourseStudent enrollment2;
    private CourseStudent enrollment3;

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

        testCourse1 = new Course();
        testCourse1.setId(1L);
        testCourse1.setName("Java Programming");
        testCourse1.setInstructor(testTeacher);

        testCourse2 = new Course();
        testCourse2.setId(2L);
        testCourse2.setName("Python Programming");
        testCourse2.setInstructor(testTeacher);

        enrollment1 = new CourseStudent();
        enrollment1.setId(1L);
        enrollment1.setStudent(testStudent1);
        enrollment1.setCourse(testCourse1);

        enrollment2 = new CourseStudent();
        enrollment2.setId(2L);
        enrollment2.setStudent(testStudent2);
        enrollment2.setCourse(testCourse1);

        enrollment3 = new CourseStudent();
        enrollment3.setId(3L);
        enrollment3.setStudent(testStudent1);
        enrollment3.setCourse(testCourse2);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllEnrollments_WithEnrollments() throws Exception {
        // Given
        List<CourseStudent> enrollments = Arrays.asList(enrollment1, enrollment2, enrollment3);
        when(courseStudentService.findAllEnrollments()).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].student.id").value(2))
                .andExpect(jsonPath("$[0].student.name").value("Jane Student"))
                .andExpect(jsonPath("$[0].course.id").value(1))
                .andExpect(jsonPath("$[0].course.name").value("Java Programming"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].student.name").value("Bob Student"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].course.name").value("Python Programming"));

        verify(courseStudentService, times(1)).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllEnrollments_EmptyList() throws Exception {
        // Given
        when(courseStudentService.findAllEnrollments()).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseStudentService, times(1)).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllEnrollments_SingleEnrollment() throws Exception {
        // Given
        List<CourseStudent> enrollments = Collections.singletonList(enrollment1);
        when(courseStudentService.findAllEnrollments()).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].student.name").value("Jane Student"))
                .andExpect(jsonPath("$[0].course.name").value("Java Programming"));

        verify(courseStudentService, times(1)).findAllEnrollments();
    }

    @Test
    void testGetAllEnrollments_Unauthorized() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isUnauthorized());

        verify(courseStudentService, never()).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetAllEnrollments_ForbiddenForStudent() throws Exception {
        // When/Then - Student role should not have access to manager endpoints
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isForbidden());

        verify(courseStudentService, never()).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetAllEnrollments_ForbiddenForTeacher() throws Exception {
        // When/Then - Teacher role should not have access to manager endpoints
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isForbidden());

        verify(courseStudentService, never()).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllEnrollments_VerifyCompleteDataStructure() throws Exception {
        // Given
        List<CourseStudent> enrollments = Collections.singletonList(enrollment1);
        when(courseStudentService.findAllEnrollments()).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].student").exists())
                .andExpect(jsonPath("$[0].student.id").exists())
                .andExpect(jsonPath("$[0].student.name").exists())
                .andExpect(jsonPath("$[0].student.username").exists())
                .andExpect(jsonPath("$[0].student.role").exists())
                .andExpect(jsonPath("$[0].course").exists())
                .andExpect(jsonPath("$[0].course.id").exists())
                .andExpect(jsonPath("$[0].course.name").exists())
                .andExpect(jsonPath("$[0].course.instructor").exists())
                .andExpect(jsonPath("$[0].course.instructor.id").exists())
                .andExpect(jsonPath("$[0].course.instructor.name").exists());

        verify(courseStudentService, times(1)).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllEnrollments_MultipleStudentsMultipleCourses() throws Exception {
        // Given
        User student3 = new User();
        student3.setId(4L);
        student3.setName("Alice Student");
        student3.setUsername("alice.student");
        student3.setRole(Role.STUDENT);

        User teacher2 = new User();
        teacher2.setId(5L);
        teacher2.setName("Mary Teacher");
        teacher2.setUsername("mary.teacher");
        teacher2.setRole(Role.TEACHER);

        Course course3 = new Course();
        course3.setId(3L);
        course3.setName("Data Structures");
        course3.setInstructor(teacher2);

        CourseStudent enrollment4 = new CourseStudent();
        enrollment4.setId(4L);
        enrollment4.setStudent(student3);
        enrollment4.setCourse(course3);

        List<CourseStudent> enrollments = Arrays.asList(enrollment1, enrollment2, enrollment3, enrollment4);
        when(courseStudentService.findAllEnrollments()).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].student.name").value("Alice Student"))
                .andExpect(jsonPath("$[3].course.name").value("Data Structures"))
                .andExpect(jsonPath("$[3].course.instructor.name").value("Mary Teacher"));

        verify(courseStudentService, times(1)).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllEnrollments_VerifyInstructorDetails() throws Exception {
        // Given
        List<CourseStudent> enrollments = Collections.singletonList(enrollment1);
        when(courseStudentService.findAllEnrollments()).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].course.instructor.id").value(1))
                .andExpect(jsonPath("$[0].course.instructor.name").value("John Teacher"))
                .andExpect(jsonPath("$[0].course.instructor.username").value("john.teacher"))
                .andExpect(jsonPath("$[0].course.instructor.role").value("TEACHER"));

        verify(courseStudentService, times(1)).findAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllEnrollments_LargeDataSet() throws Exception {
        // Given - Simulate a larger dataset
        CourseStudent enrollment4 = new CourseStudent();
        enrollment4.setId(4L);
        enrollment4.setStudent(testStudent2);
        enrollment4.setCourse(testCourse2);

        CourseStudent enrollment5 = new CourseStudent();
        enrollment5.setId(5L);
        enrollment5.setStudent(testStudent1);
        enrollment5.setCourse(testCourse1);

        List<CourseStudent> enrollments = Arrays.asList(
                enrollment1, enrollment2, enrollment3, enrollment4, enrollment5
        );
        when(courseStudentService.findAllEnrollments()).thenReturn(enrollments);

        // When/Then
        mockMvc.perform(get("/api/manager/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        verify(courseStudentService, times(1)).findAllEnrollments();
    }
}