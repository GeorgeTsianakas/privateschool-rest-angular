#!/bin/bash

# Build and Run Script for Private School Microservice
# This script builds the Docker image and starts the application with Docker Compose

set -e

echo "=========================================="
echo "Private School Microservice - Build & Run"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

print_success "Docker is running"

# Clean up old containers and volumes (optional)
read -p "Do you want to clean up old containers and volumes? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    print_warning "Stopping and removing old containers..."
    docker-compose down -v
    print_success "Cleanup complete"
fi

# Build the Docker image
echo ""
echo "Building Docker image..."
docker-compose build --no-cache

if [ $? -eq 0 ]; then
    print_success "Docker image built successfully"
else
    print_error "Failed to build Docker image"
    exit 1
fi

# Start the application
echo ""
echo "Starting application with Docker Compose..."
docker-compose up -d

if [ $? -eq 0 ]; then
    print_success "Application started successfully"
else
    print_error "Failed to start application"
    exit 1
fi

# Wait for services to be healthy
echo ""
echo "Waiting for services to be ready..."
sleep 10

# Check MySQL health
echo "Checking MySQL health..."
for i in {1..30}; do
    if docker-compose exec -T mysql mysqladmin ping -h localhost -uroot -proot --silent > /dev/null 2>&1; then
        print_success "MySQL is healthy"
        break
    fi
    if [ $i -eq 30 ]; then
        print_error "MySQL failed to start"
        docker-compose logs mysql
        exit 1
    fi
    echo -n "."
    sleep 2
done

# Check Application health
echo "Checking Application health..."
for i in {1..30}; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        print_success "Application is healthy"
        break
    fi
    if [ $i -eq 30 ]; then
        print_error "Application failed to start"
        docker-compose logs app
        exit 1
    fi
    echo -n "."
    sleep 2
done

# Display running containers
echo ""
echo "Running containers:"
docker-compose ps

# Display useful information
echo ""
echo "=========================================="
print_success "Application is running!"
echo "=========================================="
echo ""
echo "Application URL: http://localhost:8080"
echo "Health Check:    http://localhost:8080/actuator/health"
echo "MySQL:           localhost:3306"
echo ""
echo "Useful commands:"
echo "  - View logs:           docker-compose logs -f"
echo "  - View app logs:       docker-compose logs -f app"
echo "  - View MySQL logs:     docker-compose logs -f mysql"
echo "  - Stop application:    docker-compose down"
echo "  - Restart app:         docker-compose restart app"
echo "  - Shell into app:      docker-compose exec app sh"
echo "  - Shell into MySQL:    docker-compose exec mysql mysql -uroot -proot angularschool"
echo ""