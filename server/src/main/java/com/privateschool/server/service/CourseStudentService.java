package com.privateschool.server.service;

import com.privateschool.server.model.CourseStudent;

import java.util.List;

public interface CourseStudentService {

    CourseStudent saveCourseStudent(CourseStudent courseStudent);

    List<CourseStudent> findAllCoursesOfStudent(Long studentId);

    List<CourseStudent> findAllStudentsOfInstructor(Long instructorId);

    List<CourseStudent> findAllEnrollments();

}
