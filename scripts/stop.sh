#!/bin/bash
# ============================================
# 01Blog - Stop All Services
# ============================================

set -e

cd "$(dirname "$(dirname "${BASH_SOURCE[0]}")")"

echo "Stopping all 01Blog services..."
docker-compose down
echo "All services stopped."
