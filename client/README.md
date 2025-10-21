# Private School Management - Angular Client

[![Angular](https://img.shields.io/badge/Angular-10.0.5-red.svg)](https://angular.io/)
[![TypeScript](https://img.shields.io/badge/TypeScript-3.9.5-blue.svg)](https://www.typescriptlang.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-4.5.0-purple.svg)](https://getbootstrap.com/)
[![Node](https://img.shields.io/badge/Node-14+-green.svg)](https://nodejs.org/)

The **Angular 10** frontend application for the Private School Management System. This client provides a role-based user interface for students, teachers, and managers to interact with the backend REST API.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Development](#development)
- [Building](#building)
- [Testing](#testing)
- [Deployment](#deployment)
- [User Roles & Routes](#user-roles--routes)
- [Troubleshooting](#troubleshooting)

---

## 🌟 Overview

This Angular application serves as the user interface for the Private School Management System. It communicates with the Spring Boot backend via RESTful APIs and provides distinct views and functionalities based on user roles (STUDENT, TEACHER, MANAGER).

### Key Features

✅ **Role-Based Access** - Different interfaces for Students, Teachers, and Managers
✅ **JWT Authentication** - Secure token-based authentication with auto-refresh
✅ **Responsive Design** - Bootstrap 4 for mobile-friendly UI
✅ **Route Guards** - Protected routes based on authentication and authorization
✅ **HTTP Interceptors** - Automatic token injection and error handling
✅ **Reactive Forms** - Form validation with Angular Reactive Forms
✅ **Type-Safe** - Full TypeScript implementation with models

---

## ✨ Features

### Authentication & Authorization
- User registration with automatic STUDENT role assignment
- Login with JWT token generation
- Token storage in localStorage
- Automatic token injection in API requests
- Route protection based on authentication status
- Role-based navigation and access control

### Student Features
- View enrolled courses
- Enroll in new courses
- View course details and instructors
- View personal profile

### Teacher Features
- View assigned courses
- View enrolled students per course
- Manage course information
- View student profiles

### Manager Features
- View all enrollments across the system
- System-wide statistics and reporting
- User management overview

### Error Handling
- Custom 404 Not Found page
- Custom 401 Unauthorized page
- User-friendly error messages
- HTTP error interceptor

---

## 🔧 Prerequisites

Before running the application, ensure you have the following installed:

- **Node.js**: Version 14.x or higher
  - Download: https://nodejs.org/
  - Verify: `node --version`

- **npm**: Version 6.x or higher (comes with Node.js)
  - Verify: `npm --version`

- **Angular CLI**: Version 10.0.4
  - Install globally: `npm install -g @angular/cli@10.0.4`
  - Verify: `ng version`

- **Backend Server**: The Spring Boot API must be running
  - Default URL: http://localhost:8080
  - See [server/README.md](../server/README.md) for setup instructions

---

## 🚀 Quick Start

### 1. Install Dependencies

```bash
cd client
npm install
```

This will install all required packages defined in `package.json`:
- Angular 10.0.5
- Bootstrap 4.5.0
- RxJS 6.5.5
- TypeScript 3.9.5
- And all development dependencies

### 2. Start the Backend

The Angular app requires the backend API to be running:

```bash
# In a separate terminal
cd server
docker-compose up -d

# Verify backend is running
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### 3. Start the Development Server

```bash
npm start
# Or: ng serve
```

The application will automatically open at: **http://localhost:4200**

### 4. Login or Register

- **Register**: http://localhost:4200/register
  - Create a new account (automatically assigned STUDENT role)

- **Login**: http://localhost:4200/login
  - Use your credentials to get a JWT token

---

## 📁 Project Structure

```
client/
├── src/
│   ├── app/
│   │   ├── components/           # UI Components
│   │   │   ├── user/            # User-related components
│   │   │   │   ├── home/        # Landing page
│   │   │   │   ├── login/       # Login form
│   │   │   │   ├── register/    # Registration form
│   │   │   │   └── profile/     # User profile
│   │   │   ├── student/         # Student dashboard
│   │   │   ├── teacher/         # Teacher dashboard
│   │   │   ├── manager/         # Manager dashboard
│   │   │   └── error/           # Error pages (404, 401)
│   │   │       ├── not-found/   # 404 page
│   │   │       └── unauthorized/# 401 page
│   │   │
│   │   ├── models/              # TypeScript interfaces
│   │   │   ├── user.ts          # User model
│   │   │   ├── course.ts        # Course model
│   │   │   ├── coursestudent.ts # Enrollment model
│   │   │   └── role.ts          # Role enum
│   │   │
│   │   ├── services/            # API Services
│   │   │   ├── user.service.ts  # User operations
│   │   │   ├── student.service.ts  # Student operations
│   │   │   ├── teacher.service.ts  # Teacher operations
│   │   │   └── manager.service.ts  # Manager operations
│   │   │
│   │   ├── guard/               # Route Guards
│   │   │   └── auth.guard.ts    # Authentication guard
│   │   │
│   │   ├── app-routing.module.ts  # Route definitions
│   │   ├── app.module.ts          # Main app module
│   │   └── app.component.ts       # Root component
│   │
│   ├── environments/            # Environment configs
│   │   ├── environment.ts       # Development config
│   │   └── environment.prod.ts  # Production config
│   │
│   ├── assets/                  # Static assets
│   ├── index.html               # Main HTML file
│   └── styles.css               # Global styles
│
├── angular.json                 # Angular CLI configuration
├── package.json                 # Dependencies
├── tsconfig.json                # TypeScript configuration
└── README.md                    # This file
```

---

## ⚙️ Configuration

### API Endpoint Configuration

The application connects to the backend API. Configure the endpoint in the environment files:

#### Development (`src/environments/environment.ts`)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'  // Backend API URL
};
```

#### Production (`src/environments/environment.prod.ts`)

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-production-domain.com/api'  // Production API URL
};
```

### Port Configuration

To change the default port (4200):

```bash
# Method 1: Command line
ng serve --port 4300

# Method 2: angular.json
# Edit angular.json → projects → client → architect → serve → options
{
  "options": {
    "port": 4300
  }
}
```

### Proxy Configuration (Optional)

To avoid CORS issues during development, create `proxy.conf.json`:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

Then run:
```bash
ng serve --proxy-config proxy.conf.json
```

Update `environment.ts`:
```typescript
apiUrl: '/api'  // No localhost needed with proxy
```

---

## 💻 Development

### Development Server

Start the development server with live reload:

```bash
ng serve
# or
npm start
```

Navigate to http://localhost:4200. The app will automatically reload when you make changes to source files.

### Code Scaffolding

Generate new components, services, and more:

```bash
# Generate a new component
ng generate component components/my-component

# Generate a new service
ng generate service services/my-service

# Generate a new guard
ng generate guard guards/my-guard

# Generate a new model (class)
ng generate class models/my-model

# Generate a new module
ng generate module modules/my-module
```

**Available schematics:**
- `component` - UI component
- `service` - Injectable service
- `directive` - Custom directive
- `pipe` - Custom pipe
- `guard` - Route guard
- `interface` - TypeScript interface
- `enum` - TypeScript enum
- `module` - Feature module
- `class` - TypeScript class

### Hot Module Replacement

The development server uses Hot Module Replacement (HMR) for instant updates without full page reload.

### Browser Support

This application supports the following browsers:
- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

---

## 🏗️ Building

### Development Build

```bash
ng build
```

Build artifacts will be stored in the `dist/` directory.

### Production Build

```bash
ng build --prod
```

Production build includes:
- **Ahead-of-Time (AOT) compilation** - Faster rendering
- **Minification** - Smaller bundle size
- **Uglification** - Obfuscated code
- **Tree shaking** - Removes unused code
- **Build optimization** - Optimized for performance

**Output**: `dist/client/`

### Build Options

```bash
# Build with source maps (for debugging)
ng build --prod --source-map

# Build with custom output path
ng build --prod --output-path=build/

# Build and serve
ng build --prod && npx http-server dist/client -p 8081
```

### Bundle Analysis

Analyze bundle size and composition:

```bash
# Install analyzer
npm install --save-dev webpack-bundle-analyzer

# Build with stats
ng build --prod --stats-json

# Analyze bundle
npx webpack-bundle-analyzer dist/client/stats.json
```

---

## 🧪 Testing

### Unit Tests

Run unit tests using Karma and Jasmine:

```bash
ng test
```

This will:
- Launch Chrome browser
- Execute all `.spec.ts` files
- Watch for changes and re-run tests
- Display test results in terminal and browser

**Run tests once (CI mode):**
```bash
ng test --watch=false --browsers=ChromeHeadless
```

**Run with code coverage:**
```bash
ng test --code-coverage
```

Coverage report will be in `coverage/` directory. Open `coverage/index.html` in browser.

### End-to-End Tests

Run E2E tests using Protractor:

```bash
ng e2e
```

**Prerequisites**:
- Backend server must be running
- Frontend dev server must be running

### Linting

Check code quality and style:

```bash
ng lint
```

Fix linting issues automatically:
```bash
ng lint --fix
```

---

## 🚢 Deployment

### Deploy to Static Hosting

#### 1. Build for Production

```bash
ng build --prod
```

#### 2. Deploy to Hosting Provider

**Netlify:**
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Deploy
cd dist/client
netlify deploy --prod
```

**Vercel:**
```bash
# Install Vercel CLI
npm install -g vercel

# Deploy
vercel --prod
```

**AWS S3 + CloudFront:**
```bash
# Build
ng build --prod

# Upload to S3
aws s3 sync dist/client/ s3://your-bucket-name --delete

# Invalidate CloudFront cache
aws cloudfront create-invalidation --distribution-id YOUR_DIST_ID --paths "/*"
```

**Firebase Hosting:**
```bash
# Install Firebase tools
npm install -g firebase-tools

# Initialize
firebase init hosting

# Deploy
firebase deploy
```

### Deploy with Docker

Create `Dockerfile` in client directory:

```dockerfile
# Stage 1: Build
FROM node:14-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --prod

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=builder /app/dist/client /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Create `nginx.conf`:

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

Build and run:
```bash
docker build -t privateschool-client .
docker run -p 80:80 privateschool-client
```

---

## 👥 User Roles & Routes

### Public Routes (No Authentication Required)

| Route | Component | Description |
|-------|-----------|-------------|
| `/` | Home | Landing page |
| `/login` | Login | User login |
| `/register` | Register | New user registration |

### Protected Routes (Authentication Required)

| Route | Component | Role | Description |
|-------|-----------|------|-------------|
| `/profile` | Profile | All | User profile page |
| `/student` | Student | STUDENT | Student dashboard |
| `/teacher` | Teacher | TEACHER | Teacher dashboard |
| `/manager` | Manager | MANAGER | Manager dashboard |

### Error Routes

| Route | Component | Description |
|-------|-----------|-------------|
| `/401` | Unauthorized | Access denied |
| `/404` | Not Found | Page not found |
| `**` | Not Found | Catch-all route |

### Route Guards

**AuthGuard** (`src/app/guard/auth.guard.ts`):
- Checks if user is authenticated (has valid JWT token)
- Redirects to `/login` if not authenticated
- Protects: `/profile`, `/student`, `/teacher`, `/manager`

---

## 🐛 Troubleshooting

### Common Issues

#### 1. "ng: command not found"

**Solution**: Install Angular CLI globally
```bash
npm install -g @angular/cli@10.0.4
```

#### 2. CORS Errors

**Problem**: Browser blocks requests to backend

**Solution**: Use proxy configuration (see [Proxy Configuration](#proxy-configuration-optional))

Or ensure backend allows CORS:
```java
// In Spring Boot (already configured)
@CrossOrigin(origins = "http://localhost:4200")
```

#### 3. "Cannot GET /" on Refresh

**Problem**: Routing issue on production server

**Solution**: Configure server to redirect all requests to `index.html`

**Nginx example:**
```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

#### 4. API Connection Failed

**Problem**: Cannot connect to backend

**Check:**
```bash
# Verify backend is running
curl http://localhost:8080/actuator/health

# Check environment.ts has correct URL
cat src/environments/environment.ts
```

#### 5. Port 4200 Already in Use

**Solution**: Kill process or use different port
```bash
# Windows
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:4200 | xargs kill

# Or use different port
ng serve --port 4300
```

#### 6. Module Not Found Errors

**Solution**: Reinstall dependencies
```bash
rm -rf node_modules package-lock.json
npm install
```

#### 7. Build Fails with Memory Error

**Solution**: Increase Node.js memory
```bash
# Windows
set NODE_OPTIONS=--max_old_space_size=4096
ng build --prod

# Linux/Mac
NODE_OPTIONS=--max_old_space_size=4096 ng build --prod
```

### Debug Mode

Enable debug logs:

```bash
# Run with verbose logging
ng serve --verbose

# Build with detailed stats
ng build --prod --verbose
```

---

## 📚 Additional Resources

### Angular Documentation
- [Angular Official Docs](https://angular.io/docs)
- [Angular CLI Reference](https://angular.io/cli)
- [RxJS Documentation](https://rxjs.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

### Bootstrap
- [Bootstrap 4 Docs](https://getbootstrap.com/docs/4.5/)
- [Bootstrap Components](https://getbootstrap.com/docs/4.5/components/)

### Development Tools
- [Angular DevTools](https://angular.io/guide/devtools) - Chrome/Firefox extension
- [VS Code Angular Extension](https://marketplace.visualstudio.com/items?itemName=Angular.ng-template)
- [Augury](https://augury.rangle.io/) - Angular debugging tool

---

## 🔗 Related Documentation

- **[Root README](../README.md)** - Project overview
- **[Server README](../server/README.md)** - Backend API documentation
- **[API Documentation](../server/README.md#api-documentation)** - REST endpoints
- **[Postman Collection](../server/README.md#postman-collection)** - API testing

---

## 📝 Scripts Reference

All available npm scripts:

```bash
npm start          # Start dev server (ng serve)
npm run build      # Build for production
npm test           # Run unit tests
npm run lint       # Lint code
npm run e2e        # Run E2E tests
```

---

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Add/update tests
4. Run linter: `npm run lint`
5. Run tests: `npm test`
6. Commit changes
7. Push and create PR

---

## 📄 License

This project is licensed under the MIT License.

---

## 🎯 Quick Reference

### First Time Setup
```bash
cd client
npm install
ng serve
```

### Daily Development
```bash
# Terminal 1: Backend
cd server && docker-compose up

# Terminal 2: Frontend
cd client && npm start
```

### Before Committing
```bash
npm run lint       # Check code quality
npm test           # Run tests
ng build --prod    # Verify production build works
```

---
