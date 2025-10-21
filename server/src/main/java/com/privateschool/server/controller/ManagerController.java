package com.privateschool.server.controller;

import com.privateschool.server.model.CourseStudent;
import com.privateschool.server.service.CourseStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Manager Operations", description = "Endpoints for manager/administrator operations (requires MANAGER role)")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class ManagerController {

    @Autowired
    private CourseStudentService courseStudentService;

    @Operation(
            summary = "Get all course enrollments",
            description = "Returns all course enrollments in the system. Only accessible by managers."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all enrollments",
                    content = @Content(schema = @Schema(implementation = CourseStudent.class)))
    })
    @GetMapping("/api/manager/enrollments")
    public ResponseEntity<?> findAllEnrollments() {
        return ResponseEntity.ok(courseStudentService.findAllEnrollments());
    }

}
