package com.privateschool.server.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelEntityTest {

    @Test
    void testUserEntity_GettersAndSetters() {
        // Given
        User user = new User();

        // When
        user.setId(1L);
        user.setName("John Doe");
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setRole(Role.STUDENT);
        user.setToken("jwt.token.here");

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getUsername()).isEqualTo("johndoe");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo(Role.STUDENT);
        assertThat(user.getToken()).isEqualTo("jwt.token.here");
    }

    @Test
    void testUserEntity_NoArgsConstructor() {
        // When
        User user = new User();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getRole()).isNull();
        assertThat(user.getToken()).isNull();
    }

    @Test
    void testUserEntity_EqualsAndHashCode() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setUsername("johndoe");

        User user2 = new User();
        user2.setId(1L);
        user2.setName("John Doe");
        user2.setUsername("johndoe");

        User user3 = new User();
        user3.setId(2L);
        user3.setName("Jane Doe");
        user3.setUsername("janedoe");

        // Then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1).isNotEqualTo(user3);
    }

    @Test
    void testUserEntity_ToString() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setUsername("johndoe");

        // When
        String toString = user.toString();

        // Then
        assertThat(toString).contains("John Doe");
        assertThat(toString).contains("johndoe");
    }

    @Test
    void testRoleEnum_Values() {
        // Then
        assertThat(Role.values()).containsExactly(Role.STUDENT, Role.TEACHER, Role.MANAGER);
    }

    @Test
    void testRoleEnum_ValueOf() {
        // When/Then
        assertThat(Role.valueOf("STUDENT")).isEqualTo(Role.STUDENT);
        assertThat(Role.valueOf("TEACHER")).isEqualTo(Role.TEACHER);
        assertThat(Role.valueOf("MANAGER")).isEqualTo(Role.MANAGER);
    }

    @Test
    void testCourseEntity_GettersAndSetters() {
        // Given
        Course course = new Course();
        User instructor = new User();
        instructor.setId(1L);
        instructor.setName("John Teacher");

        // When
        course.setId(1L);
        course.setName("Java Programming");
        course.setInstructor(instructor);

        // Then
        assertThat(course.getId()).isEqualTo(1L);
        assertThat(course.getName()).isEqualTo("Java Programming");
        assertThat(course.getInstructor()).isEqualTo(instructor);
        assertThat(course.getInstructor().getName()).isEqualTo("John Teacher");
    }

    @Test
    void testCourseEntity_NoArgsConstructor() {
        // When
        Course course = new Course();

        // Then
        assertThat(course).isNotNull();
        assertThat(course.getId()).isNull();
        assertThat(course.getName()).isNull();
        assertThat(course.getInstructor()).isNull();
    }

    @Test
    void testCourseEntity_EqualsAndHashCode() {
        // Given
        User instructor = new User();
        instructor.setId(1L);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Java Programming");
        course1.setInstructor(instructor);

        Course course2 = new Course();
        course2.setId(1L);
        course2.setName("Java Programming");
        course2.setInstructor(instructor);

        Course course3 = new Course();
        course3.setId(2L);
        course3.setName("Python Programming");

        // Then
        assertThat(course1).isEqualTo(course2);
        assertThat(course1.hashCode()).isEqualTo(course2.hashCode());
        assertThat(course1).isNotEqualTo(course3);
    }

    @Test
    void testCourseEntity_ToString() {
        // Given
        Course course = new Course();
        course.setId(1L);
        course.setName("Java Programming");

        // When
        String toString = course.toString();

        // Then
        assertThat(toString).contains("Java Programming");
    }

    @Test
    void testCourseEntity_WithNullInstructor() {
        // Given
        Course course = new Course();

        // When
        course.setId(1L);
        course.setName("Self-Study Course");
        course.setInstructor(null);

        // Then
        assertThat(course.getInstructor()).isNull();
    }

    @Test
    void testCourseStudentEntity_GettersAndSetters() {
        // Given
        CourseStudent courseStudent = new CourseStudent();
        User student = new User();
        student.setId(1L);
        student.setName("Jane Student");

        Course course = new Course();
        course.setId(1L);
        course.setName("Java Programming");

        // When
        courseStudent.setId(1L);
        courseStudent.setStudent(student);
        courseStudent.setCourse(course);

        // Then
        assertThat(courseStudent.getId()).isEqualTo(1L);
        assertThat(courseStudent.getStudent()).isEqualTo(student);
        assertThat(courseStudent.getCourse()).isEqualTo(course);
        assertThat(courseStudent.getStudent().getName()).isEqualTo("Jane Student");
        assertThat(courseStudent.getCourse().getName()).isEqualTo("Java Programming");
    }

    @Test
    void testCourseStudentEntity_NoArgsConstructor() {
        // When
        CourseStudent courseStudent = new CourseStudent();

        // Then
        assertThat(courseStudent).isNotNull();
        assertThat(courseStudent.getId()).isNull();
        assertThat(courseStudent.getStudent()).isNull();
        assertThat(courseStudent.getCourse()).isNull();
    }

    @Test
    void testCourseStudentEntity_EqualsAndHashCode() {
        // Given
        User student = new User();
        student.setId(1L);

        Course course = new Course();
        course.setId(1L);

        CourseStudent enrollment1 = new CourseStudent();
        enrollment1.setId(1L);
        enrollment1.setStudent(student);
        enrollment1.setCourse(course);

        CourseStudent enrollment2 = new CourseStudent();
        enrollment2.setId(1L);
        enrollment2.setStudent(student);
        enrollment2.setCourse(course);

        CourseStudent enrollment3 = new CourseStudent();
        enrollment3.setId(2L);

        // Then
        assertThat(enrollment1).isEqualTo(enrollment2);
        assertThat(enrollment1.hashCode()).isEqualTo(enrollment2.hashCode());
        assertThat(enrollment1).isNotEqualTo(enrollment3);
    }

    @Test
    void testCourseStudentEntity_ToString() {
        // Given
        User student = new User();
        student.setId(1L);
        student.setName("Jane Student");

        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setId(1L);
        courseStudent.setStudent(student);

        // When
        String toString = courseStudent.toString();

        // Then
        assertThat(toString).isNotNull();
    }

    @Test
    void testUserEntity_AllRoles() {
        // Given
        User student = new User();
        student.setRole(Role.STUDENT);

        User teacher = new User();
        teacher.setRole(Role.TEACHER);

        User manager = new User();
        manager.setRole(Role.MANAGER);

        // Then
        assertThat(student.getRole()).isEqualTo(Role.STUDENT);
        assertThat(teacher.getRole()).isEqualTo(Role.TEACHER);
        assertThat(manager.getRole()).isEqualTo(Role.MANAGER);
    }

    @Test
    void testUserEntity_TransientToken() {
        // Given
        User user = new User();
        user.setToken("jwt.token");

        // When
        String token = user.getToken();

        // Then - Token is transient, so it shouldn't be persisted to database
        assertThat(token).isEqualTo("jwt.token");
    }

    @Test
    void testCourseEntity_InstructorRelationship() {
        // Given
        User instructor = new User();
        instructor.setId(1L);
        instructor.setName("John Teacher");
        instructor.setRole(Role.TEACHER);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Java Basics");
        course1.setInstructor(instructor);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Advanced Java");
        course2.setInstructor(instructor);

        // Then - One instructor can teach multiple courses
        assertThat(course1.getInstructor()).isEqualTo(instructor);
        assertThat(course2.getInstructor()).isEqualTo(instructor);
        assertThat(course1.getInstructor()).isEqualTo(course2.getInstructor());
    }

    @Test
    void testCourseStudentEntity_Relationships() {
        // Given
        User student = new User();
        student.setId(1L);
        student.setRole(Role.STUDENT);

        User instructor = new User();
        instructor.setId(2L);
        instructor.setRole(Role.TEACHER);

        Course course = new Course();
        course.setId(1L);
        course.setName("Java Programming");
        course.setInstructor(instructor);

        CourseStudent enrollment = new CourseStudent();
        enrollment.setId(1L);
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        // Then - Verify complete relationship chain
        assertThat(enrollment.getStudent().getId()).isEqualTo(1L);
        assertThat(enrollment.getCourse().getId()).isEqualTo(1L);
        assertThat(enrollment.getCourse().getInstructor().getId()).isEqualTo(2L);
        assertThat(enrollment.getStudent().getRole()).isEqualTo(Role.STUDENT);
        assertThat(enrollment.getCourse().getInstructor().getRole()).isEqualTo(Role.TEACHER);
    }
}