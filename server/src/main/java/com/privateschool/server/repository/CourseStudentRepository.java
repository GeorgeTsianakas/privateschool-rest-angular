package com.privateschool.server.repository;

import com.privateschool.server.model.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {

    List<CourseStudent> findByCourseInstructorId(Long instructorId);

    List<CourseStudent> findByStudentId(Long studentId);

}
