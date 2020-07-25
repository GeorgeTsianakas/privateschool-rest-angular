package com.privateschool.server.repository;

import com.privateschool.server.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
