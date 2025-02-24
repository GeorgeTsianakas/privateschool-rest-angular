package com.privateschool.server.controller;

import com.privateschool.server.model.Course;
import com.privateschool.server.model.CourseStudent;
import com.privateschool.server.service.CourseStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StudentController {

    @Autowired
    private CourseStudentService courseStudentService;

    @GetMapping("/api/student/courses/{studentId}")
    public ResponseEntity<?> findAllCoursesOfStudent(@PathVariable Long studentId) {
        List<Course> courseList =
                courseStudentService.findAllCoursesOfStudent(studentId).stream()
                        .map(cs -> cs.getCourse())
                        .collect(Collectors.toList());
        return new ResponseEntity<>(courseList, HttpStatus.OK);
    }

    @PostMapping("/api/student/enroll")
    public ResponseEntity<?> enroll(@RequestBody CourseStudent courseStudent) {
        return new ResponseEntity<>(courseStudentService.saveCourseStudent(courseStudent), HttpStatus.CREATED);
    }

}
