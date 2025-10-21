package com.privateschool.server.service;

import com.privateschool.server.model.Course;
import com.privateschool.server.model.CourseStudent;
import com.privateschool.server.model.Role;
import com.privateschool.server.model.User;
import com.privateschool.server.repository.CourseStudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseStudentServiceImplTest {

    @Mock
    private CourseStudentRepository courseStudentRepository;

    @InjectMocks
    private CourseStudentServiceImpl courseStudentService;

    private CourseStudent testEnrollment;
    private User testStudent;
    private User testInstructor;
    private Course testCourse;

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

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Java Programming");
        testCourse.setInstructor(testInstructor);

        testEnrollment = new CourseStudent();
        testEnrollment.setId(1L);
        testEnrollment.setStudent(testStudent);
        testEnrollment.setCourse(testCourse);
    }

    @Test
    void testSaveCourseStudent_Success() {
        // Given
        when(courseStudentRepository.save(any(CourseStudent.class))).thenReturn(testEnrollment);

        // When
        CourseStudent result = courseStudentService.saveCourseStudent(testEnrollment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStudent()).isEqualTo(testStudent);
        assertThat(result.getCourse()).isEqualTo(testCourse);
        verify(courseStudentRepository, times(1)).save(testEnrollment);
    }

    @Test
    void testSaveCourseStudent_NewEnrollment() {
        // Given
        CourseStudent newEnrollment = new CourseStudent();
        newEnrollment.setStudent(testStudent);
        newEnrollment.setCourse(testCourse);

        CourseStudent savedEnrollment = new CourseStudent();
        savedEnrollment.setId(2L);
        savedEnrollment.setStudent(testStudent);
        savedEnrollment.setCourse(testCourse);

        when(courseStudentRepository.save(any(CourseStudent.class))).thenReturn(savedEnrollment);

        // When
        CourseStudent result = courseStudentService.saveCourseStudent(newEnrollment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        verify(courseStudentRepository, times(1)).save(newEnrollment);
    }

    @Test
    void testFindAllCoursesOfStudent_WithEnrollments() {
        // Given
        Long studentId = 1L;

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Python Programming");
        course2.setInstructor(testInstructor);

        CourseStudent enrollment2 = new CourseStudent();
        enrollment2.setId(2L);
        enrollment2.setStudent(testStudent);
        enrollment2.setCourse(course2);

        List<CourseStudent> enrollments = Arrays.asList(testEnrollment, enrollment2);
        when(courseStudentRepository.findByStudentId(studentId)).thenReturn(enrollments);

        // When
        List<CourseStudent> result = courseStudentService.findAllCoursesOfStudent(studentId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCourse().getName()).isEqualTo("Java Programming");
        assertThat(result.get(1).getCourse().getName()).isEqualTo("Python Programming");
        verify(courseStudentRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void testFindAllCoursesOfStudent_EmptyList() {
        // Given
        Long studentId = 999L;
        when(courseStudentRepository.findByStudentId(studentId)).thenReturn(Collections.emptyList());

        // When
        List<CourseStudent> result = courseStudentService.findAllCoursesOfStudent(studentId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(courseStudentRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void testFindAllStudentsOfInstructor_WithStudents() {
        // Given
        Long instructorId = 2L;

        User student2 = new User();
        student2.setId(3L);
        student2.setName("Bob Student");
        student2.setUsername("bob.student");
        student2.setRole(Role.STUDENT);

        CourseStudent enrollment2 = new CourseStudent();
        enrollment2.setId(3L);
        enrollment2.setStudent(student2);
        enrollment2.setCourse(testCourse);

        List<CourseStudent> enrollments = Arrays.asList(testEnrollment, enrollment2);
        when(courseStudentRepository.findByCourseInstructorId(instructorId)).thenReturn(enrollments);

        // When
        List<CourseStudent> result = courseStudentService.findAllStudentsOfInstructor(instructorId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStudent().getName()).isEqualTo("Jane Student");
        assertThat(result.get(1).getStudent().getName()).isEqualTo("Bob Student");
        verify(courseStudentRepository, times(1)).findByCourseInstructorId(instructorId);
    }

    @Test
    void testFindAllStudentsOfInstructor_EmptyList() {
        // Given
        Long instructorId = 999L;
        when(courseStudentRepository.findByCourseInstructorId(instructorId)).thenReturn(Collections.emptyList());

        // When
        List<CourseStudent> result = courseStudentService.findAllStudentsOfInstructor(instructorId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(courseStudentRepository, times(1)).findByCourseInstructorId(instructorId);
    }

    @Test
    void testFindAllEnrollments_WithEnrollments() {
        // Given
        User student2 = new User();
        student2.setId(3L);
        student2.setName("Alice Student");
        student2.setUsername("alice.student");
        student2.setRole(Role.STUDENT);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Data Structures");
        course2.setInstructor(testInstructor);

        CourseStudent enrollment2 = new CourseStudent();
        enrollment2.setId(2L);
        enrollment2.setStudent(student2);
        enrollment2.setCourse(course2);

        List<CourseStudent> allEnrollments = Arrays.asList(testEnrollment, enrollment2);
        when(courseStudentRepository.findAll()).thenReturn(allEnrollments);

        // When
        List<CourseStudent> result = courseStudentService.findAllEnrollments();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStudent().getName()).isEqualTo("Jane Student");
        assertThat(result.get(1).getStudent().getName()).isEqualTo("Alice Student");
        verify(courseStudentRepository, times(1)).findAll();
    }

    @Test
    void testFindAllEnrollments_EmptyList() {
        // Given
        when(courseStudentRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CourseStudent> result = courseStudentService.findAllEnrollments();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(courseStudentRepository, times(1)).findAll();
    }

    @Test
    void testSaveCourseStudent_WithNullStudent() {
        // Given
        CourseStudent enrollmentWithNullStudent = new CourseStudent();
        enrollmentWithNullStudent.setId(10L);
        enrollmentWithNullStudent.setStudent(null);
        enrollmentWithNullStudent.setCourse(testCourse);

        when(courseStudentRepository.save(any(CourseStudent.class))).thenReturn(enrollmentWithNullStudent);

        // When
        CourseStudent result = courseStudentService.saveCourseStudent(enrollmentWithNullStudent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStudent()).isNull();
        verify(courseStudentRepository, times(1)).save(enrollmentWithNullStudent);
    }

    @Test
    void testFindAllCoursesOfStudent_MultipleCoursesFromSameInstructor() {
        // Given
        Long studentId = 1L;

        Course course2 = new Course();
        course2.setId(3L);
        course2.setName("Advanced Java");
        course2.setInstructor(testInstructor);

        CourseStudent enrollment2 = new CourseStudent();
        enrollment2.setId(5L);
        enrollment2.setStudent(testStudent);
        enrollment2.setCourse(course2);

        List<CourseStudent> enrollments = Arrays.asList(testEnrollment, enrollment2);
        when(courseStudentRepository.findByStudentId(studentId)).thenReturn(enrollments);

        // When
        List<CourseStudent> result = courseStudentService.findAllCoursesOfStudent(studentId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCourse().getInstructor()).isEqualTo(testInstructor);
        assertThat(result.get(1).getCourse().getInstructor()).isEqualTo(testInstructor);
        verify(courseStudentRepository, times(1)).findByStudentId(studentId);
    }
}