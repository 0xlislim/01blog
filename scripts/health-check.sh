#!/bin/bash
# ============================================
# 01Blog - Health Check Script
# ============================================

set -e

cd "$(dirname "$(dirname "${BASH_SOURCE[0]}")")"

echo ""
echo "============================================"
echo "  01Blog - Health Check"
echo "============================================"
echo ""

# Load environment variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-4200}"
POSTGRES_PORT="${POSTGRES_PORT:-5432}"

# Check Docker containers
echo "Container Status:"
echo "--------------------------------------------"
docker-compose ps
echo ""

# Check PostgreSQL
echo "PostgreSQL (port $POSTGRES_PORT):"
if docker-compose exec -T postgres pg_isready -U postgres -d blog_db > /dev/null 2>&1; then
    echo "  Status: HEALTHY"
else
    echo "  Status: UNHEALTHY"
fi
echo ""

# Check Backend
echo "Backend (port $BACKEND_PORT):"
if curl -sf "http://localhost:${BACKEND_PORT}/actuator/health" > /dev/null 2>&1; then
    HEALTH=$(curl -s "http://localhost:${BACKEND_PORT}/actuator/health")
    echo "  Status: HEALTHY"
    echo "  Response: $HEALTH"
else
    echo "  Status: UNHEALTHY or UNREACHABLE"
fi
echo ""

# Check Frontend
echo "Frontend (port $FRONTEND_PORT):"
if curl -sf "http://localhost:${FRONTEND_PORT}/" > /dev/null 2>&1; then
    echo "  Status: HEALTHY"
else
    echo "  Status: UNHEALTHY or UNREACHABLE"
fi
echo ""

# Disk usage
echo "Volume Disk Usage:"
echo "--------------------------------------------"
docker system df -v 2>/dev/null | grep "01blog" || echo "  No 01blog volumes found."
echo ""
