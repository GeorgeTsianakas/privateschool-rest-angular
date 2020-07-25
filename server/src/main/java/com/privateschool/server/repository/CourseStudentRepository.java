package com.privateschool.server.repository;

import com.privateschool.server.model.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {

}
