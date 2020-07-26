package com.privateschool.server.service;

import com.privateschool.server.model.Course;

import java.util.List;

public interface CourseService {

    Course addCourse(Course course);

    Course updateCourse(Course course);

    void deleteCourse(Long courseId);

    List<Course> findAllCourses();

}
