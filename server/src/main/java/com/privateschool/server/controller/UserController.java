package com.privateschool.server.controller;

import com.privateschool.server.jwt.JwtTokenProvider;
import com.privateschool.server.model.CourseStudent;
import com.privateschool.server.model.Role;
import com.privateschool.server.model.User;
import com.privateschool.server.service.CourseService;
import com.privateschool.server.service.CourseStudentService;
import com.privateschool.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "User Management", description = "User registration, authentication, and public course operations")
@RestController
public class UserController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseStudentService courseStudentService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with STUDENT role. Username must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Username already exists",
                    content = @Content)
    })
    @PostMapping("/api/user/registration")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user.setRole(Role.STUDENT);
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login and get JWT token",
            description = "Authenticates the user with Basic Auth and returns user details with JWT token. " +
                    "Use Basic Authentication with username and password in the Authorization header."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = User.class)))
    })
    @GetMapping("/api/user/login")
    public ResponseEntity<?> getUser(Principal principal) {
        if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
            // When there is no authenticated user (e.g., anonymous), return 200 OK with empty body
            return ResponseEntity.ok().build();
        }
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        // Only proceed for tokens without credentials (e.g., stateless JWT auth). Otherwise, return OK without side effects.
        if (authenticationToken.getCredentials() != null) {
            return ResponseEntity.ok().build();
        }
        User user = userService.findByUsername(authenticationToken.getName());
        if (user == null) {
            return ResponseEntity.ok().build();
        }
        user.setToken(tokenProvider.generateToken(authenticationToken));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(
            summary = "Enroll in a course",
            description = "Enrolls the current user in a course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully enrolled",
                    content = @Content(schema = @Schema(implementation = CourseStudent.class)))
    })
    @PostMapping("/api/user/enroll")
    public ResponseEntity<?> enrollCourse(@RequestBody CourseStudent courseStudent) {
        return new ResponseEntity<>(courseStudentService.saveCourseStudent(courseStudent), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all available courses",
            description = "Returns a list of all courses available in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved courses")
    })
    @GetMapping("/api/user/courses")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.findAllCourses());
    }

}
