# Private School REST API - Dockerized Microservice

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-8-blue.svg)](https://www.oracle.com/java/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A production-ready, cloud-native RESTful API microservice for private school management. Built with Spring Boot and packaged as a **standalone executable JAR** (no external Tomcat required) with complete Docker containerization support.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Quick Start](#quick-start)
- [Development Setup](#development-setup)
- [Docker Deployment](#docker-deployment)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Security](#security)
- [Configuration](#configuration)
- [Monitoring](#monitoring)
- [Production Deployment](#production-deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

---

## Overview

The Private School REST API is a comprehensive backend system for managing school operations including:
- User authentication and authorization (JWT-based)
- Student, Teacher, and Manager role management
- Course management and enrollment
- Secure REST endpoints with role-based access control

### Key Characteristics

✅ **Standalone JAR** - No external application server needed (embedded Tomcat)
✅ **Dockerized** - Complete containerization with Docker Compose
✅ **Cloud-Ready** - Deploy to any cloud provider (AWS, Azure, GCP, etc.)
✅ **Microservice Architecture** - Independently deployable and scalable
✅ **Production-Ready** - Health checks, metrics, logging, and monitoring
✅ **Fully Tested** - 140+ comprehensive unit and integration tests
✅ **Secure** - JWT authentication, BCrypt passwords, role-based authorization

---

## Architecture

### System Architecture

```
┌────────────────────────────────────────────────────────────┐
│                   Docker Compose Stack                      │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────┐    ┌─────────────────────┐   │
│  │   Spring Boot App       │    │   MySQL Database    │   │
│  │   (Container)           │◄───┤   (Container)       │   │
│  │                         │    │                     │   │
│  │  • Executable JAR      │    │  • MySQL 8.0        │   │
│  │  • Embedded Tomcat     │    │  • Persistent Vol.  │   │
│  │  • Port: 8080          │    │  • Port: 3306       │   │
│  │  • Health Checks       │    │  • Auto-init        │   │
│  │  • Actuator Metrics    │    │  • Liquibase        │   │
│  └─────────────────────────┘    └─────────────────────┘   │
│           │                               │                │
│           └───────────┬───────────────────┘                │
│                       │                                    │
│            privateschool-network (bridge)                  │
└────────────────────────────────────────────────────────────┘
                        │
                        ▼
              http://localhost:8080
```

### Application Layers

```
┌─────────────────────────────────────────┐
│         REST Controllers                │
│  (UserController, StudentController,    │
│   TeacherController, ManagerController) │
└─────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│      Security Layer (JWT)               │
│  (JwtTokenProvider,                     │
│   JwtAuthorizationFilter)               │
└─────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Service Layer                   │
│  (UserService, CourseService,           │
│   CourseStudentService)                 │
└─────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│       Repository Layer (JPA)            │
│  (UserRepository, CourseRepository,     │
│   CourseStudentRepository)              │
└─────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│      MySQL Database                     │
│  (Liquibase-managed schema)             │
└─────────────────────────────────────────┘
```

---

## Features

### 🔐 Authentication & Authorization
- JWT-based stateless authentication
- BCrypt password encryption
- Role-based access control (STUDENT, TEACHER, MANAGER)
- Secure token generation and validation

### 👥 User Management
- User registration with automatic role assignment
- Login with JWT token generation
- Password encryption and validation
- User profile management

### 📚 Course Management
- Create, read, update, delete courses
- Assign instructors to courses
- List all available courses
- Course metadata management

### 📝 Enrollment Management
- Student course enrollment
- View enrolled courses per student
- View enrolled students per teacher
- Manager oversight of all enrollments

### 📊 Monitoring & Health
- Spring Boot Actuator endpoints
- Health checks for container orchestration
- Application metrics and statistics
- Database connectivity monitoring

### 🧪 Comprehensive Testing
- 140+ unit and integration tests
- Comprehensive code coverage
- SonarQube compliant test suite

---

## Technology Stack

### Backend Framework
- **Spring Boot 2.3.2** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication and authorization
- **Spring Boot Actuator** - Monitoring and metrics

### Database
- **MySQL 8.0** - Primary database
- **Liquibase** - Database migration and versioning
- **H2 Database** - In-memory database for testing

### Security
- **JWT (jjwt 0.9.1)** - Token-based authentication
- **BCrypt** - Password hashing
- **Spring Security** - Security framework

### Build & Testing
- **Maven** - Build automation
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework

### Containerization
- **Docker** - Containerization platform
- **Docker Compose** - Multi-container orchestration

### Other
- **Lombok** - Reduce boilerplate code
- **MySQL Connector** - Database driver

---

## Quick Start

### Prerequisites

1. **Docker Desktop** installed and running
   - Download: https://www.docker.com/products/docker-desktop
   - Version: 20.10+ recommended

2. **Docker Compose** (included with Docker Desktop)
   - Version: 1.29+ recommended

### One-Command Deployment

#### Windows:
```cmd
cd server
build-and-run.bat
```

#### Linux/Mac:
```bash
cd server
./build-and-run.sh
```

#### Manual (All Platforms):
```bash
cd server
docker-compose up -d --build
```

### Verify Deployment

```bash
# Check container status
docker-compose ps

# Check application health
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}

# View logs
docker-compose logs -f app
```

### Access Points

- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Info**: http://localhost:8080/actuator/info
- **MySQL**: localhost:3306 (user: `schooluser`, password: `schoolpass`)

---

## Development Setup

### Option 1: Using Docker (Recommended)

This is the **recommended approach** - no need for local Java, Maven, or MySQL installation.

```bash
# Clone the repository
git clone <repository-url>
cd privateschool-rest-angular/server

# Start the application
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop the application
docker-compose down
```

### Option 2: Local Development (Without Docker)

If you prefer local development:

#### Prerequisites:
- Java 8 or higher
- Maven 3.6+
- MySQL 8.0

#### Steps:

1. **Set up MySQL database:**
```sql
CREATE DATABASE angularschool;
CREATE USER 'schooluser'@'localhost' IDENTIFIED BY 'schoolpass';
GRANT ALL PRIVILEGES ON angularschool.* TO 'schooluser'@'localhost';
FLUSH PRIVILEGES;
```

2. **Update `application.properties`:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/angularschool
spring.datasource.username=schooluser
spring.datasource.password=schoolpass
```

3. **Build and run:**
```bash
# Build the JAR
./mvnw clean package

# Run the application
java -jar target/server-0.0.1-SNAPSHOT.jar

# Or run directly with Maven
./mvnw spring-boot:run
```

4. **Access the application:**
```
http://localhost:8080
```

### Development Tools

#### Hot Reload
The application includes Spring Boot DevTools for automatic restart on code changes (when running locally).

#### IDE Setup
Import as Maven project in:
- **IntelliJ IDEA**: File → Open → Select `pom.xml`
- **Eclipse**: File → Import → Existing Maven Projects
- **VS Code**: Install Java Extension Pack, open folder

---

## Docker Deployment

### Understanding the Setup

The application uses a **multi-stage Docker build** for optimal image size and security:

#### Stage 1: Build (Maven + JDK)
```dockerfile
FROM maven:3.8.6-openjdk-8 AS builder
# Compiles source code
# Downloads dependencies
# Produces executable JAR
```

#### Stage 2: Runtime (JRE only)
```dockerfile
FROM openjdk:8-jre-alpine
# Minimal Alpine Linux base
# Only JRE (no build tools)
# Non-root user for security
# Final size: ~180MB
```

### Docker Files Overview

| File | Purpose |
|------|---------|
| `Dockerfile` | Multi-stage build configuration |
| `docker-compose.yml` | Orchestrates MySQL + App |
| `.dockerignore` | Excludes files from build context |
| `application-docker.properties` | Docker-specific configuration |
| `build-and-run.sh/bat` | Automated deployment scripts |

### Building the Docker Image

```bash
# Build using Docker Compose (recommended)
docker-compose build

# Or build manually
docker build -t privateschool-api:latest .

# Build without cache (fresh build)
docker-compose build --no-cache
```

### Running Containers

```bash
# Start all services
docker-compose up -d

# Start with rebuild
docker-compose up -d --build

# Start and view logs
docker-compose up

# Scale application instances
docker-compose up -d --scale app=3
```

### Managing Containers

```bash
# View running containers
docker-compose ps

# View logs (all services)
docker-compose logs -f

# View logs (specific service)
docker-compose logs -f app
docker-compose logs -f mysql

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v

# Restart a service
docker-compose restart app

# Execute command in container
docker-compose exec app sh
docker-compose exec mysql mysql -uroot -proot angularschool
```

### Environment Variables

Create a `.env` file in the server directory:

```bash
# JWT Configuration (CHANGE IN PRODUCTION!)
JWT_SECRET=YourVerySecureRandomSecretKey123456789

# MySQL Configuration
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=schooluser
MYSQL_PASSWORD=schoolpass
MYSQL_DATABASE=angularschool

# Application Configuration
SPRING_PROFILES_ACTIVE=docker
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_PRIVATESCHOOL=DEBUG

# JVM Configuration
JAVA_OPTS=-Xmx512m -Xms256m
```

See `.env.example` for a template.

---

## API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt-token>
```

### Public Endpoints

#### User Registration
```http
POST /api/user/registration
Content-Type: application/json

{
  "name": "John Doe",
  "username": "johndoe",
  "password": "password123"
}

Response: 201 Created
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "role": "STUDENT"
}
```

#### User Login
```http
GET /api/user/login
Authorization: Basic <base64(username:password)>

Response: 200 OK
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "role": "STUDENT",
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### Get All Courses
```http
GET /api/user/courses
Authorization: Bearer <jwt-token>

Response: 200 OK
[
  {
    "id": 1,
    "name": "Java Programming",
    "instructor": {
      "id": 2,
      "name": "Jane Teacher",
      "role": "TEACHER"
    }
  }
]
```

### Student Endpoints (Requires STUDENT role)

#### Get Student's Courses
```http
GET /api/student/courses/{studentId}
Authorization: Bearer <jwt-token>

Response: 200 OK
[
  {
    "id": 1,
    "name": "Java Programming",
    "instructor": {...}
  }
]
```

#### Enroll in Course
```http
POST /api/student/enroll
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "student": {"id": 1},
  "course": {"id": 1}
}

Response: 201 Created
```

### Teacher Endpoints (Requires TEACHER role)

#### Get Teacher's Students
```http
GET /api/teacher/students/{teacherId}
Authorization: Bearer <jwt-token>

Response: 200 OK
[
  {
    "id": 1,
    "name": "John Doe",
    "username": "johndoe",
    "role": "STUDENT"
  }
]
```

### Manager Endpoints (Requires MANAGER role)

#### Get All Enrollments
```http
GET /api/manager/enrollments
Authorization: Bearer <jwt-token>

Response: 200 OK
[
  {
    "id": 1,
    "student": {...},
    "course": {...}
  }
]
```

### Health & Monitoring Endpoints

#### Health Check
```http
GET /actuator/health

Response: 200 OK
{
  "status": "UP"
}
```

#### Application Metrics
```http
GET /actuator/metrics

Response: 200 OK
{
  "names": [
    "jvm.memory.used",
    "http.server.requests",
    ...
  ]
}
```

#### Specific Metric
```http
GET /actuator/metrics/jvm.memory.used

Response: 200 OK
{
  "name": "jvm.memory.used",
  "measurements": [...],
  ...
}
```

### Postman Collection

You can import this complete Postman collection to test all endpoints:

<details>
<summary>Click to expand Postman Collection JSON</summary>

```json
{
  "info": {
    "name": "Private School REST API",
    "description": "Complete API collection for Private School microservice",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080",
      "type": "string"
    },
    {
      "key": "token",
      "value": "",
      "type": "string"
    }
  ],
  "item": [
    {
      "name": "Health & Monitoring",
      "item": [
        {
          "name": "Health Check",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/actuator/health",
              "host": ["{{baseUrl}}"],
              "path": ["actuator", "health"]
            }
          }
        },
        {
          "name": "Application Metrics",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/actuator/metrics",
              "host": ["{{baseUrl}}"],
              "path": ["actuator", "metrics"]
            }
          }
        },
        {
          "name": "JVM Memory Metric",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/actuator/metrics/jvm.memory.used",
              "host": ["{{baseUrl}}"],
              "path": ["actuator", "metrics", "jvm.memory.used"]
            }
          }
        },
        {
          "name": "Application Info",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/actuator/info",
              "host": ["{{baseUrl}}"],
              "path": ["actuator", "info"]
            }
          }
        }
      ]
    },
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Register User (Student)",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "if (pm.response.code === 201) {",
                  "    var jsonData = pm.response.json();",
                  "    pm.environment.set(\"userId\", jsonData.id);",
                  "}"
                ]
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"John Student\",\n  \"username\": \"johnstudent\",\n  \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/user/registration",
              "host": ["{{baseUrl}}"],
              "path": ["api", "user", "registration"]
            }
          }
        },
        {
          "name": "Login User",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "if (pm.response.code === 200) {",
                  "    var jsonData = pm.response.json();",
                  "    pm.collectionVariables.set(\"token\", jsonData.token);",
                  "    pm.environment.set(\"authToken\", jsonData.token);",
                  "}"
                ]
              }
            }
          ],
          "request": {
            "method": "GET",
            "header": [],
            "auth": {
              "type": "basic",
              "basic": [
                {
                  "key": "username",
                  "value": "johnstudent",
                  "type": "string"
                },
                {
                  "key": "password",
                  "value": "password123",
                  "type": "string"
                }
              ]
            },
            "url": {
              "raw": "{{baseUrl}}/api/user/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "user", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "User Endpoints",
      "item": [
        {
          "name": "Get All Courses",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}",
                "type": "text"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/user/courses",
              "host": ["{{baseUrl}}"],
              "path": ["api", "user", "courses"]
            }
          }
        }
      ]
    },
    {
      "name": "Student Endpoints",
      "item": [
        {
          "name": "Get Student Courses",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}",
                "type": "text"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/student/courses/1",
              "host": ["{{baseUrl}}"],
              "path": ["api", "student", "courses", "1"]
            }
          }
        },
        {
          "name": "Enroll in Course",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}",
                "type": "text"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"student\": {\"id\": 1},\n  \"course\": {\"id\": 1}\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/student/enroll",
              "host": ["{{baseUrl}}"],
              "path": ["api", "student", "enroll"]
            }
          }
        }
      ]
    },
    {
      "name": "Teacher Endpoints",
      "item": [
        {
          "name": "Get Teacher's Students",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}",
                "type": "text"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/teacher/students/1",
              "host": ["{{baseUrl}}"],
              "path": ["api", "teacher", "students", "1"]
            }
          }
        }
      ]
    },
    {
      "name": "Manager Endpoints",
      "item": [
        {
          "name": "Get All Enrollments",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}",
                "type": "text"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/manager/enrollments",
              "host": ["{{baseUrl}}"],
              "path": ["api", "manager", "enrollments"]
            }
          }
        }
      ]
    }
  ]
}
```

</details>

#### How to Import in Postman:

1. **Open Postman**
2. **Click "Import"** button (top left)
3. **Select "Raw text"** tab
4. **Copy and paste** the JSON above
5. **Click "Import"**
6. **Set base URL**: The collection uses `{{baseUrl}}` variable set to `http://localhost:8080`

#### Quick Test Flow:

1. **Health Check** → Verify application is running
2. **Register User** → Creates a new student user (auto-saves user ID)
3. **Login User** → Get JWT token (auto-saves token to collection variable)
4. **Get All Courses** → Uses the token automatically
5. **Other endpoints** → Token is automatically included in Authorization header

**Note**: After running "Login User", the token is automatically saved to the `{{token}}` variable and will be used in all subsequent authenticated requests.

---

## Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│      User       │         │  CourseStudent   │         │     Course      │
├─────────────────┤         ├──────────────────┤         ├─────────────────┤
│ id (PK)         │◄───────┤ id (PK)          ├────────►│ id (PK)         │
│ name            │         │ student_id (FK)  │         │ name            │
│ username        │         │ course_id (FK)   │         │ instructor_id   │
│ password        │         └──────────────────┘         │ (FK → User)     │
│ role (ENUM)     │                                      └─────────────────┘
│ token (transient)│                                             ▲
└─────────────────┘                                              │
         │                                                       │
         └───────────────────────────────────────────────────────┘
              instructor relationship (one teacher, many courses)
```

### Tables

#### `user`
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key (auto-increment) |
| name | VARCHAR | Full name |
| username | VARCHAR | Unique username |
| password | VARCHAR | BCrypt hashed password |
| role | VARCHAR | STUDENT, TEACHER, or MANAGER |

#### `course`
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key (auto-increment) |
| name | VARCHAR | Course name |
| instructor_id | BIGINT | Foreign key to user table |

#### `course_student`
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key (auto-increment) |
| student_id | BIGINT | Foreign key to user table |
| course_id | BIGINT | Foreign key to course table |

### Database Migrations

Database schema is managed by **Liquibase**:

- Migration files: `src/main/resources/database/changelog/`
- Automatic migration on application startup
- Version-controlled schema changes
- Rollback support

---

## Testing

### Test Suite Overview

The application includes **140+ comprehensive tests** with **85%+ code coverage**:

- **Service Layer Tests**: 35+ tests
- **Controller Layer Tests**: 45+ tests
- **Security/JWT Tests**: 20+ tests
- **Configuration Tests**: 15+ tests
- **Model/Entity Tests**: 20+ tests
- **Integration Tests**: Context loading

### Running Tests

```bash
# Using Maven wrapper (all platforms)
./mvnw clean test

# Using Docker
docker-compose exec app ./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceImplTest

# Run specific test method
./mvnw test -Dtest=UserServiceImplTest#testSaveUser

# Skip tests during build
./mvnw clean package -DskipTests
```

### Test Coverage

The application maintains high test coverage to ensure code quality and reliability.

### Test Structure

```
src/test/java/com/privateschool/server/
├── ServerApplicationTests.java           # Context loading
├── config/
│   └── WebSecurityConfigTest.java       # Security config
├── controller/
│   ├── UserControllerTest.java          # User endpoints
│   ├── StudentControllerTest.java       # Student endpoints
│   ├── TeacherControllerTest.java       # Teacher endpoints
│   └── ManagerControllerTest.java       # Manager endpoints
├── jwt/
│   ├── JwtTokenProviderTest.java        # JWT generation
│   └── JwtAuthorizationFilterTest.java  # JWT filtering
├── model/
│   └── ModelEntityTest.java             # Entity validation
└── service/
    ├── UserServiceImplTest.java         # User service
    ├── CourseServiceImplTest.java       # Course service
    ├── CourseStudentServiceImplTest.java # Enrollment service
    └── UserDetailsServiceImplTest.java  # Security service
```

### Testing Best Practices

✅ **Arrange-Act-Assert** pattern used throughout
✅ **Mockito** for mocking dependencies
✅ **MockMvc** for controller testing
✅ **@WithMockUser** for security context
✅ **H2 in-memory** database for tests
✅ **Descriptive test names**: `test[Method]_[Scenario]`
✅ **Edge cases** covered (null, empty, invalid data)

---

## Security

### Authentication Flow

```
1. User Registration
   ↓
   Password → BCrypt Hash → Store in DB

2. User Login
   ↓
   Username + Password → Validate → Generate JWT Token
   ↓
   Return Token to Client

3. Authenticated Request
   ↓
   Client sends: Authorization: Bearer <token>
   ↓
   JwtAuthorizationFilter validates token
   ↓
   Extract user + roles from token
   ↓
   Set SecurityContext
   ↓
   Proceed to endpoint (if authorized)
```

### JWT Token Structure

```json
{
  "header": {
    "alg": "HS512",
    "typ": "JWT"
  },
  "payload": {
    "sub": "johndoe",
    "authorities": ["ROLE_STUDENT"],
    "iat": 1234567890,
    "exp": 1234654290
  },
  "signature": "..."
}
```

### Role-Based Access Control

| Endpoint Pattern | Required Role | Description |
|-----------------|---------------|-------------|
| `/api/user/**` | Public | Registration, login, courses |
| `/api/student/**` | STUDENT | Student operations |
| `/api/teacher/**` | TEACHER | Teacher operations |
| `/api/manager/**` | MANAGER | Manager operations |
| `/actuator/**` | Public | Health checks, metrics |
| `/error` | Public | Error handling |

### Security Features

✅ **JWT Authentication** - Stateless token-based auth
✅ **BCrypt Passwords** - Strong password hashing
✅ **Role Hierarchy** - Fine-grained access control
✅ **CORS Enabled** - Cross-origin requests allowed
✅ **CSRF Disabled** - Suitable for stateless REST API
✅ **Non-Root Docker User** - Container security
✅ **Network Isolation** - Docker network segregation

### Security Configuration

**Important**: Change these in production!

```yaml
# In .env file
JWT_SECRET=YourVerySecureRandomSecretKey123456789  # CHANGE THIS!
MYSQL_ROOT_PASSWORD=SecureRootPassword              # CHANGE THIS!
MYSQL_PASSWORD=SecureAppPassword                    # CHANGE THIS!
```

Generate secure JWT secret:
```bash
# Linux/Mac
openssl rand -base64 64

# Or use online generator
# https://www.grc.com/passwords.htm
```

---

## Configuration

### Application Profiles

The application supports multiple Spring profiles:

#### Default Profile (`application.properties`)
```properties
# For local development
spring.datasource.url=jdbc:mysql://localhost:3306/angularschool
```

#### Docker Profile (`application-docker.properties`)
```properties
# For Docker deployment
spring.datasource.url=jdbc:mysql://mysql:3306/angularschool
```

Activate profile:
```bash
# Via environment variable (in docker-compose.yml)
SPRING_PROFILES_ACTIVE=docker

# Via command line
java -jar app.jar --spring.profiles.active=docker
```

### Configuration Properties

#### Database
```properties
spring.datasource.url=jdbc:mysql://mysql:3306/angularschool
spring.datasource.username=schooluser
spring.datasource.password=schoolpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

#### JPA/Hibernate
```properties
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

#### Liquibase
```properties
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:/database/changelog/database.changelog-master.xml
```

#### JWT
```properties
app.jwt.secret=RandomSecretKey
app.jwt.token.prefix=Bearer
app.jwt.header.string=Authorization
app.jwt.expiration-in-ms=86400000  # 24 hours
```

#### Actuator
```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
```

#### Logging
```properties
logging.level.root=INFO
logging.level.com.privateschool.server=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Environment Variables

Override any property using environment variables:

```bash
# Docker Compose
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/angularschool
  APP_JWT_SECRET: ${JWT_SECRET}
  LOGGING_LEVEL_ROOT: INFO

# Command line
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/angularschool
java -jar app.jar
```

---

## Monitoring

### Health Checks

#### Application Health
```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

#### Container Health

Docker automatically monitors container health:

```bash
# Check health status
docker-compose ps

# Expected output:
# NAME                   STATUS
# privateschool-app      Up (healthy)
# privateschool-mysql    Up (healthy)
```

Health check configuration in `docker-compose.yml`:
```yaml
healthcheck:
  test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

### Metrics

#### Available Metrics
```bash
# List all metrics
curl http://localhost:8080/actuator/metrics

# JVM memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# HTTP requests
curl http://localhost:8080/actuator/metrics/http.server.requests

# Database connections
curl http://localhost:8080/actuator/metrics/hikaricp.connections
```

#### Application Info
```bash
curl http://localhost:8080/actuator/info
```

Response:
```json
{
  "app": {
    "name": "Private School REST API",
    "description": "Microservice for Private School Management",
    "version": "0.0.1-SNAPSHOT",
    "encoding": "UTF-8",
    "java": {
      "version": "1.8"
    }
  }
}
```

### Logging

#### View Logs
```bash
# All logs
docker-compose logs -f

# Application only
docker-compose logs -f app

# MySQL only
docker-compose logs -f mysql

# Last 100 lines
docker-compose logs --tail=100 app

# With timestamps
docker-compose logs --timestamps app
```

#### Export Logs
```bash
# Export to file
docker-compose logs app > application.log

# Export with date
docker-compose logs app > app-$(date +%Y%m%d).log
```

#### Log Levels

Configured in `application-docker.properties`:
```properties
logging.level.root=INFO
logging.level.com.privateschool.server=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.springframework.security=INFO
logging.level.org.hibernate.SQL=DEBUG
```

Change via environment variables:
```yaml
environment:
  LOGGING_LEVEL_ROOT: WARN
  LOGGING_LEVEL_COM_PRIVATESCHOOL: DEBUG
```

---

## Production Deployment

### Pre-Production Checklist

- [ ] **Change JWT Secret**
  ```bash
  JWT_SECRET=$(openssl rand -base64 64)
  echo "JWT_SECRET=$JWT_SECRET" > .env
  ```

- [ ] **Change Database Passwords**
  ```bash
  # Update in .env file
  MYSQL_ROOT_PASSWORD=<strong-password>
  MYSQL_PASSWORD=<strong-password>
  ```

- [ ] **Configure Resource Limits**
  ```yaml
  # In docker-compose.yml
  deploy:
    resources:
      limits:
        cpus: '1.0'
        memory: 1G
  ```

- [ ] **Enable HTTPS/TLS** (use reverse proxy)

- [ ] **Set Production Profile**
  ```yaml
  SPRING_PROFILES_ACTIVE=prod
  ```

- [ ] **Configure Log Aggregation** (ELK, Splunk, etc.)

- [ ] **Set Up Monitoring** (Prometheus, Grafana)

- [ ] **Configure Backups** (database, volumes)

- [ ] **Review Security Settings**

### Cloud Deployment

#### AWS ECS/EKS

```bash
# Build and tag for ECR
docker build -t privateschool-api:latest .
docker tag privateschool-api:latest \
  <account-id>.dkr.ecr.<region>.amazonaws.com/privateschool-api:latest

# Push to ECR
aws ecr get-login-password --region <region> | \
  docker login --username AWS --password-stdin \
  <account-id>.dkr.ecr.<region>.amazonaws.com

docker push <account-id>.dkr.ecr.<region>.amazonaws.com/privateschool-api:latest
```

#### Azure Container Instances

```bash
# Build and push to Azure Container Registry
az acr build --registry <registry-name> \
  --image privateschool-api:latest .

# Deploy to ACI
az container create \
  --resource-group <resource-group> \
  --name privateschool-api \
  --image <registry-name>.azurecr.io/privateschool-api:latest \
  --cpu 1 --memory 1 \
  --ports 8080 \
  --environment-variables \
    SPRING_PROFILES_ACTIVE=docker \
    JWT_SECRET=<secret>
```

#### Google Cloud Run

```bash
# Build and push to Google Container Registry
gcloud builds submit --tag gcr.io/<project-id>/privateschool-api

# Deploy to Cloud Run
gcloud run deploy privateschool-api \
  --image gcr.io/<project-id>/privateschool-api \
  --platform managed \
  --port 8080 \
  --set-env-vars SPRING_PROFILES_ACTIVE=docker,JWT_SECRET=<secret>
```

#### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: privateschool-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: privateschool-api
  template:
    metadata:
      labels:
        app: privateschool-api
    spec:
      containers:
      - name: app
        image: privateschool-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "docker"
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: jwt-secret
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
```

### Scaling

#### Horizontal Scaling

```bash
# Docker Compose (requires load balancer)
docker-compose up -d --scale app=3

# Kubernetes
kubectl scale deployment privateschool-api --replicas=5

# Auto-scaling in Kubernetes
kubectl autoscale deployment privateschool-api \
  --cpu-percent=70 --min=2 --max=10
```

#### Vertical Scaling

```yaml
# Increase memory/CPU in docker-compose.yml
environment:
  JAVA_OPTS: "-Xmx2048m -Xms1024m"

deploy:
  resources:
    limits:
      cpus: '2.0'
      memory: 4G
```

### Database Backups

```bash
# Manual backup
docker-compose exec mysql mysqldump -uroot -proot angularschool > backup.sql

# Restore
docker-compose exec -T mysql mysql -uroot -proot angularschool < backup.sql

# Automated backup (add to crontab)
0 2 * * * cd /path/to/server && \
  docker-compose exec -T mysql mysqldump -uroot -proot angularschool > \
  backups/backup-$(date +\%Y\%m\%d).sql
```

---

## Troubleshooting

### Common Issues

#### 1. Port Already in Use

**Error**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solution**:
```bash
# Find process using port
# Linux/Mac
lsof -i :8080
# Windows
netstat -ano | findstr :8080

# Kill the process or change port in docker-compose.yml
ports:
  - "8081:8080"  # Map to different host port
```

#### 2. MySQL Connection Failed

**Error**: `Communications link failure`

**Solutions**:
```bash
# Check MySQL is running
docker-compose ps

# View MySQL logs
docker-compose logs mysql

# Restart MySQL
docker-compose restart mysql

# Wait longer for startup (increase in docker-compose.yml)
healthcheck:
  start_period: 90s  # Increase from 60s
```

#### 3. Application Won't Start

**Solutions**:
```bash
# View detailed logs
docker-compose logs -f app

# Check environment variables
docker-compose config

# Rebuild from scratch
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

#### 4. "Out of Memory" Error

**Solution**:
```yaml
# Increase memory in docker-compose.yml
environment:
  JAVA_OPTS: "-Xmx1024m -Xms512m"

deploy:
  resources:
    limits:
      memory: 2G
```

#### 5. Liquibase Lock Error

**Solution**:
```bash
# Connect to MySQL
docker-compose exec mysql mysql -uroot -proot angularschool

# Clear lock
UPDATE DATABASECHANGELOGLOCK SET LOCKED=0;
```

#### 6. Changes Not Reflected

**Solution**:
```bash
# Rebuild image
docker-compose up -d --build

# Or force recreate
docker-compose up -d --force-recreate
```

#### 7. Docker Build Fails

**Solutions**:
```bash
# Check Docker disk space
docker system df

# Clean up
docker system prune -a

# Check Docker daemon is running
docker info
```

### Debug Commands

```bash
# Shell into application container
docker-compose exec app sh

# Check Java version
docker-compose exec app java -version

# View environment variables
docker-compose exec app env

# Check application process
docker-compose exec app ps aux

# Test database connection from app container
docker-compose exec app sh -c "apt-get update && apt-get install -y mysql-client && mysql -hmysql -uschooluser -pschoolpass -e 'SHOW DATABASES;'"

# Check disk space
docker-compose exec app df -h

# Check memory usage
docker stats
```

### Getting Help

1. **Check logs first**: `docker-compose logs -f`
2. **Verify health**: `curl http://localhost:8080/actuator/health`
3. **Check container status**: `docker-compose ps`
4. **Review environment**: `docker-compose config`
5. **Search issues**: Check GitHub issues
6. **Ask community**: Stack Overflow with tag `spring-boot`

---

## Project Structure

```
server/
├── src/
│   ├── main/
│   │   ├── java/com/privateschool/server/
│   │   │   ├── ServerApplication.java          # Main application class
│   │   │   ├── config/
│   │   │   │   └── WebSecurityConfig.java      # Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java         # User endpoints
│   │   │   │   ├── StudentController.java      # Student endpoints
│   │   │   │   ├── TeacherController.java      # Teacher endpoints
│   │   │   │   └── ManagerController.java      # Manager endpoints
│   │   │   ├── jwt/
│   │   │   │   ├── JwtTokenProvider.java       # JWT generation/validation
│   │   │   │   └── JwtAuthorizationFilter.java # JWT filter
│   │   │   ├── model/
│   │   │   │   ├── User.java                   # User entity
│   │   │   │   ├── Course.java                 # Course entity
│   │   │   │   ├── CourseStudent.java          # Enrollment entity
│   │   │   │   └── Role.java                   # Role enum
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java         # User data access
│   │   │   │   ├── CourseRepository.java       # Course data access
│   │   │   │   └── CourseStudentRepository.java # Enrollment data access
│   │   │   └── service/
│   │   │       ├── UserService.java            # User business logic
│   │   │       ├── UserServiceImpl.java
│   │   │       ├── CourseService.java          # Course business logic
│   │   │       ├── CourseServiceImpl.java
│   │   │       ├── CourseStudentService.java   # Enrollment logic
│   │   │       ├── CourseStudentServiceImpl.java
│   │   │       └── UserDetailsServiceImpl.java # Spring Security integration
│   │   └── resources/
│   │       ├── application.properties          # Default configuration
│   │       ├── application-docker.properties   # Docker configuration
│   │       └── database/
│   │           └── changelog/                  # Liquibase migrations
│   └── test/
│       └── java/com/privateschool/server/      # 140+ tests (85% coverage)
├── target/
│   └── server-0.0.1-SNAPSHOT.jar              # Built JAR (after mvn package)
├── Dockerfile                                  # Multi-stage Docker build
├── docker-compose.yml                          # Service orchestration
├── .dockerignore                               # Docker build optimization
├── .env.example                                # Environment variables template
├── build-and-run.sh                            # Linux/Mac deployment script
├── build-and-run.bat                           # Windows deployment script
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

---

## Key Differences from Traditional WAR Deployment

| Aspect | WAR (Old) | JAR + Docker (New) |
|--------|-----------|-------------------|
| **Application Server** | External Tomcat required | ❌ Not needed - Embedded Tomcat |
| **Deployment** | Manual WAR file deployment | ✅ `docker-compose up -d` |
| **Configuration** | Server-specific (server.xml, context.xml) | Environment variables |
| **Portability** | Environment-dependent | ✅ Runs anywhere (Docker) |
| **Startup** | Start Tomcat → Deploy WAR → Configure | One command |
| **Scaling** | Vertical only | ✅ Horizontal + Vertical |
| **Database Setup** | Manual installation | ✅ Included in Docker Compose |
| **Monitoring** | Manual setup | ✅ Built-in Actuator |
| **Cloud Deployment** | Complex | ✅ Simple (AWS, Azure, GCP) |
| **CI/CD** | Multi-step process | ✅ Single Docker build |
| **Isolation** | Shared Tomcat | ✅ Isolated containers |
| **Dependencies** | System-dependent | ✅ All in container |

### You Don't Need Tomcat Anymore!

The application now uses **embedded Tomcat** bundled inside the JAR:

```bash
# Old way (WAR)
1. Install Tomcat
2. Configure Tomcat
3. Deploy WAR file
4. Start Tomcat
5. Troubleshoot issues

# New way (JAR)
1. docker-compose up -d
✅ Done!
```

---

## Command Reference

### Docker Commands

```bash
# Build and Start
docker-compose up -d --build         # Build and start all services
docker-compose up                    # Start with console output
docker-compose build --no-cache      # Build from scratch

# Management
docker-compose down                  # Stop and remove containers
docker-compose down -v               # Stop and remove volumes
docker-compose restart app           # Restart application
docker-compose stop                  # Stop without removing
docker-compose start                 # Start stopped services

# Monitoring
docker-compose ps                    # Container status
docker-compose logs -f               # Follow all logs
docker-compose logs -f app           # Follow app logs only
docker-compose logs --tail=100 app   # Last 100 lines
docker-compose top                   # Running processes

# Scaling
docker-compose up -d --scale app=3   # Run 3 app instances

# Maintenance
docker-compose exec app sh           # Shell into app container
docker-compose exec mysql bash       # Shell into MySQL
docker system prune -a               # Clean up Docker
docker volume ls                     # List volumes
docker network ls                    # List networks
```

### Maven Commands

```bash
# Build
./mvnw clean package                 # Build JAR
./mvnw clean package -DskipTests     # Build without tests
./mvnw clean install                 # Build and install

# Testing
./mvnw test                          # Run all tests
./mvnw test -Dtest=ClassName         # Run specific test class

# Run
./mvnw spring-boot:run               # Run application with Maven
java -jar target/server-0.0.1-SNAPSHOT.jar  # Run JAR directly
```

### Health Check Commands

```bash
# Application health
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Info
curl http://localhost:8080/actuator/info

# Docker health
docker-compose ps                    # Check health status
```

---

## Contributing

### Development Workflow

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make changes and add tests**
4. **Run tests**: `./mvnw test`
5. **Commit changes**: `git commit -m 'Add amazing feature'`
6. **Push to branch**: `git push origin feature/amazing-feature`
7. **Open a Pull Request**

### Coding Standards

- Follow Java naming conventions
- Use Lombok for boilerplate reduction
- Write tests for all new features (maintain 80%+ coverage)
- Use meaningful commit messages
- Document public APIs with Javadoc
- Follow Spring Boot best practices

### Testing Requirements

- All new features must include tests
- Maintain minimum 80% code coverage
- Use descriptive test names: `test[Method]_[Scenario]`
- Follow Arrange-Act-Assert pattern
- Mock external dependencies

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Support

### Documentation

- **Quick Start**: See [Quick Start](#quick-start) section
- **Docker Guide**: See [Docker Deployment](#docker-deployment) section
- **API Reference**: See [API Documentation](#api-documentation) section
- **Troubleshooting**: See [Troubleshooting](#troubleshooting) section

### Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Docker Documentation](https://docs.docker.com/)
- [JWT Introduction](https://jwt.io/introduction)
- [MySQL Documentation](https://dev.mysql.com/doc/)

### Getting Help

1. **Check logs**: `docker-compose logs -f`
2. **Verify health**: `curl http://localhost:8080/actuator/health`
3. **Search issues**: Check repository issues
4. **Community support**: Stack Overflow with tag `spring-boot`

---

## Acknowledgments

- Spring Boot team for the excellent framework
- Docker for containerization platform
- All contributors and testers

---

## Summary

This is a **production-ready, cloud-native microservice** that:

✅ Runs as a **standalone JAR** (no external Tomcat needed)
✅ Fully **Dockerized** with one-command deployment
✅ Includes **MySQL database** in Docker Compose
✅ Has **health checks** and **monitoring** built-in
✅ Provides **140+ tests** with **85%+ coverage**
✅ Supports **JWT authentication** and **role-based access**
✅ Is **cloud-ready** (deploy to AWS, Azure, GCP, etc.)
✅ Includes **comprehensive documentation**

### Get Started in 30 Seconds

```bash
cd server
docker-compose up -d
curl http://localhost:8080/actuator/health
```

**That's it!** You now have a fully functional REST API running with MySQL, ready for development or production deployment.

---

## Connect to MySQL from IntelliJ IDEA (Databases tab)

You can browse and manage the project database directly from IntelliJ IDEA’s Database tool window. The stack already includes a MySQL 8 container with a ready schema seed.

Quick reference
- Host/Port: localhost:3306 (from your machine)
- Database: angularschool
- User: schooluser
- Password: schoolpass
- Root user (optional): root / root
- JDBC URL (copy/paste): jdbc:mysql://localhost:3306/angularschool?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

Prerequisites
1) Start MySQL (via Docker Compose)
   - In terminal from the server folder:
     - docker compose up -d mysql
   - Wait until the container is healthy.
   - The init scripts in server/init-db will run automatically on first startup.

2) Install the MySQL driver in IntelliJ
   - IntelliJ will prompt to download it on first connection, or you can add it via the driver settings in the Data Source dialog.

Step-by-step in IntelliJ IDEA
1) Open the Database tool window
   - View > Tool Windows > Database (or press Alt+1, then select the Database tab depending on keymap).

2) Add a new Data Source
   - Click + (Add) > Data Source > MySQL.

3) Enter connection details
   - Host: localhost
   - Port: 3306
   - User: schooluser
   - Password: schoolpass
   - Database: angularschool
   - URL: jdbc:mysql://localhost:3306/angularschool?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

4) Test connection
   - Click Test Connection. If prompted, download drivers.
   - Click OK to save.

Optional: Connect to the DB from inside the Docker network
- If you are attaching from another container or using the container name, use:
  - Host: privateschool-mysql
  - URL: jdbc:mysql://privateschool-mysql:3306/angularschool?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

Troubleshooting
- Port already in use (3306): Stop local MySQL or change the published port in server/docker-compose.yml.
- Access denied: Ensure the user/password match docker-compose.yml (schooluser/schoolpass). You can also use root/root.
- Driver issues: Ensure the MySQL 8 driver is selected. For older drivers, set useSSL=false and serverTimezone=UTC as above.
- Container not ready: Wait for the mysql container health check to pass. Run docker logs privateschool-mysql to inspect.

Notes for Spring Boot app
- The application in Docker uses the internal host name mysql. For local development outside Docker, use localhost as shown above. The same schema and credentials apply.

---