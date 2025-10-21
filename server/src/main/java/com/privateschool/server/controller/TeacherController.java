package com.privateschool.server.controller;

import com.privateschool.server.model.User;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Teacher Operations", description = "Endpoints for teacher-specific operations (requires TEACHER role)")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class TeacherController {

    @Autowired
    private CourseStudentService courseStudentService;

    @Operation(
            summary = "Get all students of a teacher",
            description = "Returns all students enrolled in courses taught by a specific teacher/instructor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved students",
                    content = @Content(schema = @Schema(implementation = User.class)))
    })
    @GetMapping("/api/teacher/students/{teacherId}")
    public ResponseEntity<?> findAllStudentsOfInstructor(
            @Parameter(description = "ID of the teacher/instructor", required = true)
            @PathVariable Long teacherId) {
        List<User> students =
                courseStudentService.findAllStudentsOfInstructor(teacherId).stream()
                        .map(cs -> cs.getStudent())
                        .collect(Collectors.toList());
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

}
