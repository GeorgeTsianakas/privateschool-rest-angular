@echo off
REM Build and Run Script for Private School Microservice (Windows)
REM This script builds the Docker image and starts the application with Docker Compose

echo ==========================================
echo Private School Microservice - Build ^& Run
echo ==========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop and try again.
    exit /b 1
)
echo [OK] Docker is running

REM Ask for cleanup
set /p CLEANUP="Do you want to clean up old containers and volumes? (y/n): "
if /i "%CLEANUP%"=="y" (
    echo [INFO] Stopping and removing old containers...
    docker-compose down -v
    echo [OK] Cleanup complete
)

REM Build the Docker image
echo.
echo Building Docker image...
docker-compose build --no-cache

if errorlevel 1 (
    echo [ERROR] Failed to build Docker image
    exit /b 1
)
echo [OK] Docker image built successfully

REM Start the application
echo.
echo Starting application with Docker Compose...
docker-compose up -d

if errorlevel 1 (
    echo [ERROR] Failed to start application
    exit /b 1
)
echo [OK] Application started successfully

REM Wait for services
echo.
echo Waiting for services to be ready...
timeout /t 10 /nobreak >nul

REM Display running containers
echo.
echo Running containers:
docker-compose ps

REM Display useful information
echo.
echo ==========================================
echo [OK] Application is running!
echo ==========================================
echo.
echo Application URL: http://localhost:8080
echo Health Check:    http://localhost:8080/actuator/health
echo MySQL:           localhost:3306
echo.
echo Useful commands:
echo   - View logs:           docker-compose logs -f
echo   - View app logs:       docker-compose logs -f app
echo   - View MySQL logs:     docker-compose logs -f mysql
echo   - Stop application:    docker-compose down
echo   - Restart app:         docker-compose restart app
echo   - Shell into app:      docker-compose exec app sh
echo   - Shell into MySQL:    docker-compose exec mysql mysql -uroot -proot angularschool
echo.