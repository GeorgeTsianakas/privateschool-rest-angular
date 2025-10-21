package com.privateschool.server.controller;

import com.privateschool.server.model.Course;
import com.privateschool.server.model.CourseStudent;
import com.privateschool.server.service.CourseStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Student Operations", description = "Endpoints for student-specific operations (requires STUDENT role)")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class StudentController {

    @Autowired
    private CourseStudentService courseStudentService;

    @Operation(
            summary = "Get enrolled courses for a student",
            description = "Returns all courses that a specific student is enrolled in"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved student's courses",
                    content = @Content(schema = @Schema(implementation = Course.class)))
    })
    @GetMapping("/api/student/courses/{studentId}")
    public ResponseEntity<?> findAllCoursesOfStudent(
            @Parameter(description = "ID of the student", required = true)
            @PathVariable Long studentId) {
        List<Course> courseList =
                courseStudentService.findAllCoursesOfStudent(studentId).stream()
                        .map(cs -> cs.getCourse())
                        .collect(Collectors.toList());
        return new ResponseEntity<>(courseList, HttpStatus.OK);
    }

    @Operation(
            summary = "Enroll student in a course",
            description = "Enrolls a student in a specified course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully enrolled",
                    content = @Content(schema = @Schema(implementation = CourseStudent.class)))
    })
    @PostMapping("/api/student/enroll")
    public ResponseEntity<?> enroll(@RequestBody CourseStudent courseStudent) {
        return new ResponseEntity<>(courseStudentService.saveCourseStudent(courseStudent), HttpStatus.CREATED);
    }

}
