# Private School Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3%20LTS-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-10-red.svg)](https://angular.io/)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> Note: This branch is being updated to target Spring Boot 3.3 LTS. Minimum required Java is 17 (Java 21 LTS recommended). Some instructions may differ until the migration is fully complete.

A modern, full-stack web application for private school management built with **Spring Boot** (backend) and **Angular** (frontend). This project is an evolution of [privateschool](https://github.com/GeorgeTsianakas/privateschool) and [trainersCRUD](https://github.com/GeorgeTsianakas/trainersCRUD), now featuring a production-ready microservice architecture with Docker containerization.

---

## 🌟 Key Features

### Backend (Spring Boot Microservice)
✅ **Standalone JAR** - No external Tomcat required (embedded server)
✅ **Dockerized** - One-command deployment with Docker Compose
✅ **JWT Authentication** - Secure, stateless token-based authentication
✅ **Role-Based Access** - STUDENT, TEACHER, MANAGER roles with fine-grained permissions
✅ **RESTful API** - Well-documented endpoints with Postman collection
✅ **Comprehensive Testing** - 140+ unit and integration tests
✅ **Production-Ready** - Health checks, metrics, monitoring via Spring Actuator
✅ **Cloud-Native** - Deploy to AWS, Azure, GCP, Kubernetes

### Frontend (Angular)
✅ **Modern UI** - Angular 10 with responsive design
✅ **Role-Based Views** - Different interfaces for students, teachers, and managers
✅ **JWT Integration** - Seamless authentication with backend
✅ **Real-time Updates** - Dynamic data binding and state management

---

## 📁 Project Structure

```
privateschool-rest-angular/
├── server/                    # Spring Boot REST API (Microservice)
│   ├── src/                   # Source code
│   ├── Dockerfile             # Multi-stage Docker build
│   ├── docker-compose.yml     # MySQL + App orchestration
│   ├── pom.xml                # Maven configuration
│   └── README.md              # 📖 Detailed server documentation
│
└── client/                    # Angular frontend application
    ├── src/                   # Angular source code
    ├── package.json           # Node dependencies
    └── angular.json           # Angular configuration
```

---

## 🚀 Quick Start

### Option 1: Docker Deployment (Recommended)

**Prerequisites**: Docker Desktop installed and running

```bash
# Start the backend (MySQL + Spring Boot)
cd server
docker-compose up -d

# Verify backend is running
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}

# Start the frontend (in a new terminal)
cd client
npm install
ng serve

# Access the application
# Frontend: http://localhost:4200
# Backend:  http://localhost:8080
```

**That's it!** The database is auto-configured, schema is created via Liquibase, and the application is ready to use.

### Option 2: Local Development (Without Docker)

**Prerequisites**: Java 17+ (21 LTS recommended), Maven 3.9+, Node.js 14+, MySQL 8.0

#### 1. Setup MySQL Database
```sql
CREATE DATABASE angularschool;
CREATE USER 'schooluser'@'localhost' IDENTIFIED BY 'schoolpass';
GRANT ALL PRIVILEGES ON angularschool.* TO 'schooluser'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. Build and Run Backend
```bash
cd server
./mvnw clean package
java -jar target/server-0.0.1-SNAPSHOT.jar

# Or run with Maven
./mvnw spring-boot:run
```

#### 3. Build and Run Frontend
```bash
cd client
npm install
ng serve
```

#### 4. Access Applications
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **API Health**: http://localhost:8080/actuator/health

---

## 📚 Documentation

### Spring Boot LTS Upgrade Notes (this branch)
- Targeting Spring Boot 3.3 LTS and Java 17+ (Java 21 LTS recommended).
- Jakarta namespace migration: jakarta.* replaces javax.* in Boot 3.x.
- Spring Security changes: WebSecurityConfigurerAdapter removed; configuration will be via SecurityFilterChain beans.
- Verify third-party dependencies for 3.x compatibility.
- Some instructions below may still show 2.x-era examples until migration is finished.

### Backend (Server)
**📖 [Complete Server Documentation](server/README.md)** - Detailed guide including:
- Architecture and design patterns
- API documentation with all endpoints
- **Postman collection** for testing
- Docker deployment guide
- Security configuration
- Testing strategy (140+ tests)
- Production deployment (AWS, Azure, GCP, Kubernetes)
- Troubleshooting and debugging

### Frontend (Client)
- Angular 10 application
- Role-based routing and guards
- JWT token management
- HTTP interceptors for authentication
- Reactive forms and validation

---

## 🔐 Authentication & Authorization

### User Roles
- **STUDENT** - Can view enrolled courses, enroll in new courses
- **TEACHER** - Can view assigned courses and enrolled students
- **MANAGER** - Can view all enrollments and system-wide data

### Authentication Flow
1. **Register** - Create account via `/api/user/registration`
2. **Login** - Get JWT token via `/api/user/login`
3. **Access** - Use token in `Authorization: Bearer <token>` header
4. **Frontend** - Angular automatically manages token in localStorage

---

## 🧪 Testing the API

### Using Postman

Import the complete Postman collection from [server/README.md](server/README.md#postman-collection) which includes:
- Health and monitoring endpoints
- User registration and login
- Student, Teacher, Manager endpoints
- Automatic token management

### Using cURL

```bash
# Health check
curl http://localhost:8080/actuator/health

# Register a new user
curl -X POST http://localhost:8080/api/user/registration \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","username":"johndoe","password":"password123"}'

# Login and get token
curl -X GET http://localhost:8080/api/user/login \
  -u johndoe:password123

# Use token to access protected endpoints
curl -X GET http://localhost:8080/api/user/courses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## 🏗️ Technology Stack

### Backend
- **Spring Boot 3.3.x (LTS target)** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database persistence
- **JWT (jjwt)** - Token-based authentication
- **MySQL 8.0** - Database
- **Liquibase** - Database migrations
- **Docker** - Containerization
- **JUnit 5 + Mockito** - Testing framework

### Frontend
- **Angular 10** - Frontend framework
- **TypeScript** - Programming language
- **RxJS** - Reactive programming
- **Angular Material** - UI components
- **Angular Router** - Navigation and routing

---

## 🐳 Docker Deployment

The server uses a **multi-stage Docker build** for optimal performance:

### Stage 1: Builder
- Uses Maven + JDK image
- Downloads dependencies
- Compiles source code
- Produces executable JAR

### Stage 2: Runtime
- Uses minimal JRE Alpine image
- Copies only the JAR
- Runs as non-root user
- Final image size: ~180MB

### One-Command Deployment
```bash
cd server
docker-compose up -d --build

# Check status
docker-compose ps

# View logs
docker-compose logs -f app

# Stop everything
docker-compose down
```

---

## 📊 Database Schema

```
┌─────────────┐         ┌────────────────┐         ┌─────────────┐
│    User     │         │ CourseStudent  │         │   Course    │
├─────────────┤         ├────────────────┤         ├─────────────┤
│ id (PK)     │◄───────┤ id (PK)        ├────────►│ id (PK)     │
│ name        │         │ student_id(FK) │         │ name        │
│ username    │         │ course_id (FK) │         │ instructor  │
│ password    │         └────────────────┘         │   _id (FK)  │
│ role (ENUM) │                                    └─────────────┘
└─────────────┘
```

**Tables**:
- `user` - Students, teachers, and managers
- `course` - Course information and assigned instructor
- `course_student` - Student enrollments (many-to-many)

**Migrations**: Managed by Liquibase in `server/src/main/resources/database/changelog/`

---

## 🔧 Configuration

### Backend Configuration

**Default** (`application.properties`):
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/angularschool
```

**Docker** (`application-docker.properties`):
```properties
spring.datasource.url=jdbc:mysql://mysql:3306/angularschool
```

**Environment Variables** (`.env` file):
```bash
JWT_SECRET=YourSecretKey123
MYSQL_ROOT_PASSWORD=root
MYSQL_PASSWORD=schoolpass
```

### Frontend Configuration

**API Endpoint** (`src/environments/environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

---

## 📈 Monitoring & Health Checks

### Spring Boot Actuator Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application metrics
curl http://localhost:8080/actuator/metrics

# JVM memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Application info
curl http://localhost:8080/actuator/info
```

### Docker Health Checks

Docker automatically monitors container health:
```bash
docker-compose ps
# Shows: Up (healthy) or Up (unhealthy)
```

---

## 🧩 API Endpoints Overview

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/user/registration` | POST | Public | Register new user |
| `/api/user/login` | GET | Basic Auth | Login and get JWT token |
| `/api/user/courses` | GET | JWT | Get all courses |
| `/api/student/courses/{id}` | GET | JWT (STUDENT) | Get student's courses |
| `/api/student/enroll` | POST | JWT (STUDENT) | Enroll in course |
| `/api/teacher/students/{id}` | GET | JWT (TEACHER) | Get teacher's students |
| `/api/manager/enrollments` | GET | JWT (MANAGER) | Get all enrollments |
| `/actuator/health` | GET | Public | Health check |
| `/actuator/metrics` | GET | Public | Application metrics |

**Full API Documentation**: See [server/README.md](server/README.md#api-documentation)

---

## 🚢 Production Deployment

### Cloud Platforms

The backend is ready to deploy to:
- **AWS** (ECS, EKS, Elastic Beanstalk)
- **Azure** (Container Instances, AKS)
- **Google Cloud** (Cloud Run, GKE)
- **Kubernetes** (any provider)

### Deployment Checklist
- [ ] Change JWT secret to a strong random value
- [ ] Update database passwords
- [ ] Configure HTTPS/TLS (use reverse proxy)
- [ ] Set up log aggregation (ELK, Splunk)
- [ ] Configure monitoring (Prometheus, Grafana)
- [ ] Enable database backups
- [ ] Review security settings
- [ ] Set resource limits (CPU, memory)

**Detailed deployment guides**: See [server/README.md#production-deployment](server/README.md#production-deployment)

---

## 🧪 Testing

### Backend Tests
```bash
cd server
./mvnw test

# Run with coverage report
./mvnw clean test
```

**Test Suite**:
- 140+ comprehensive tests
- Unit tests for all services
- Integration tests for controllers
- Security and JWT tests
- Entity validation tests

### Frontend Tests
```bash
cd client
npm test

# Run with coverage
npm run test:coverage
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and add tests
4. Run tests: `./mvnw test` (backend) or `npm test` (frontend)
5. Commit changes: `git commit -m 'Add amazing feature'`
6. Push to branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Built upon [privateschool](https://github.com/GeorgeTsianakas/privateschool) and [trainersCRUD](https://github.com/GeorgeTsianakas/trainersCRUD)
- Spring Boot team for the excellent framework
- Angular team for the powerful frontend framework
- Docker for containerization platform

---

## 📞 Support

- **Server Documentation**: [server/README.md](server/README.md)
- **Issues**: [GitHub Issues](https://github.com/yourusername/privateschool-rest-angular/issues)
- **Stack Overflow**: Tag with `spring-boot` and `angular`

---

## 🎯 Quick Links

- **[Server Documentation](server/README.md)** - Complete backend guide
- **[API Endpoints](server/README.md#api-documentation)** - Full API reference
- **[Postman Collection](server/README.md#postman-collection)** - Ready-to-import API tests
- **[Docker Guide](server/README.md#docker-deployment)** - Containerization details
- **[Production Deployment](server/README.md#production-deployment)** - Cloud deployment guides

---

**Ready to get started?** 🚀

```bash
cd server && docker-compose up -d
cd ../client && npm install && ng serve
```

Open http://localhost:4200 and enjoy your private school management system!