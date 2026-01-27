#!/bin/bash
# ============================================
# 01Blog - Deployment Script
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

echo ""
echo "============================================"
echo "  01Blog - Starting"
echo "============================================"
echo ""

# Load environment variables
if [ -f "$PROJECT_DIR/.env" ]; then
    export $(grep -v '^#' "$PROJECT_DIR/.env" | xargs)
    echo "Loaded environment from .env"
else
    echo "WARNING: No .env file found. Using default values."
    echo "Run 'make setup' to create one."
fi

echo ""

# Stop existing containers
echo "Stopping existing containers..."
docker-compose down 2>/dev/null || true

echo ""

# Build images
echo "Building Docker images..."
docker-compose build

echo ""

# Start services
echo "Starting services..."
docker-compose up -d

echo ""
echo "Deployment started."
echo "  Frontend: http://localhost:${FRONTEND_PORT:-4200}"
echo "  Backend API: http://localhost:${BACKEND_PORT:-8080}/api"
echo "  Database: localhost:${POSTGRES_PORT:-5432}"

echo ""

# Wait for services to be ready
echo "Waiting for services to be ready..."
RETRIES=30
until docker-compose ps | grep -q "healthy" || [ $RETRIES -eq 0 ]; do
    echo "  Waiting... ($RETRIES attempts remaining)"
    sleep 5
    RETRIES=$((RETRIES - 1))
done

if [ $RETRIES -eq 0 ]; then
    echo ""
    echo "WARNING: Services may not be fully healthy yet."
    echo "Check logs with: make logs"
else
    echo ""
    echo "All services are running!"
fi

echo ""
docker-compose ps
echo ""
