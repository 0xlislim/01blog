#!/bin/bash
# ============================================
# 01Blog - Restart All Services
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

echo "Restarting all 01Blog services..."

docker-compose down
docker-compose up -d

echo ""
echo "All services restarted."
echo ""
docker-compose ps
