package com.privateschool.server.service;

import com.privateschool.server.model.Course;
import com.privateschool.server.model.Role;
import com.privateschool.server.model.User;
import com.privateschool.server.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course testCourse;
    private User testInstructor;

    @BeforeEach
    void setUp() {
        testInstructor = new User();
        testInstructor.setId(1L);
        testInstructor.setName("John Doe");
        testInstructor.setUsername("johndoe");
        testInstructor.setRole(Role.TEACHER);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Java Programming");
        testCourse.setInstructor(testInstructor);
    }

    @Test
    void testAddCourse_Success() {
        // Given
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        Course result = courseService.addCourse(testCourse);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Java Programming");
        assertThat(result.getInstructor()).isEqualTo(testInstructor);
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    void testAddCourse_WithNullInstructor() {
        // Given
        Course courseWithoutInstructor = new Course();
        courseWithoutInstructor.setId(2L);
        courseWithoutInstructor.setName("Self-Study Course");
        courseWithoutInstructor.setInstructor(null);

        when(courseRepository.save(any(Course.class))).thenReturn(courseWithoutInstructor);

        // When
        Course result = courseService.addCourse(courseWithoutInstructor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getInstructor()).isNull();
        verify(courseRepository, times(1)).save(courseWithoutInstructor);
    }

    @Test
    void testUpdateCourse_Success() {
        // Given
        Course updatedCourse = new Course();
        updatedCourse.setId(1L);
        updatedCourse.setName("Advanced Java Programming");
        updatedCourse.setInstructor(testInstructor);

        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // When
        Course result = courseService.updateCourse(updatedCourse);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Advanced Java Programming");
        verify(courseRepository, times(1)).save(updatedCourse);
    }

    @Test
    void testDeleteCourse_Success() {
        // Given
        Long courseId = 1L;
        doNothing().when(courseRepository).deleteById(courseId);

        // When
        courseService.deleteCourse(courseId);

        // Then
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    void testDeleteCourse_NonExistentCourse() {
        // Given
        Long nonExistentId = 999L;
        doNothing().when(courseRepository).deleteById(nonExistentId);

        // When
        courseService.deleteCourse(nonExistentId);

        // Then
        verify(courseRepository, times(1)).deleteById(nonExistentId);
    }

    @Test
    void testFindAllCourses_WithCourses() {
        // Given
        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Python Programming");
        course2.setInstructor(testInstructor);

        List<Course> courses = Arrays.asList(testCourse, course2);
        when(courseRepository.findAll()).thenReturn(courses);

        // When
        List<Course> result = courseService.findAllCourses();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Java Programming");
        assertThat(result.get(1).getName()).isEqualTo("Python Programming");
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testFindAllCourses_EmptyList() {
        // Given
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Course> result = courseService.findAllCourses();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testAddCourse_WithNullCourse() {
        // Given
        when(courseRepository.save(null)).thenThrow(new IllegalArgumentException("Course cannot be null"));

        // When/Then
        try {
            courseService.addCourse(null);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Course cannot be null");
        }
        verify(courseRepository, times(1)).save(null);
    }
}