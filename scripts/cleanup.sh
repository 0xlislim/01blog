#!/bin/bash
# ============================================
# 01Blog - Cleanup Script
# ============================================
# Removes containers, images, and volumes
# ============================================

set -e

cd "$(dirname "$(dirname "${BASH_SOURCE[0]}")")"

echo ""
echo "============================================"
echo "  01Blog - Cleanup"
echo "============================================"
echo ""

echo "Stopping containers..."
docker-compose down 2>/dev/null || true

echo ""
echo "Removing 01blog Docker images..."
docker images --filter "reference=*01blog*" -q | xargs -r docker rmi -f 2>/dev/null || true

echo ""
echo "Removing dangling images..."
docker image prune -f 2>/dev/null || true

echo ""
echo "Removing unused networks..."
docker network prune -f 2>/dev/null || true

echo ""
read -p "Remove data volumes (database + uploads)? This is DESTRUCTIVE. (y/N) " confirm
if [ "$confirm" = "y" ]; then
    echo "Removing volumes..."
    docker volume rm 01blog-postgres-data 2>/dev/null || true
    docker volume rm 01blog-uploads-data 2>/dev/null || true
    echo "Volumes removed."
else
    echo "Volumes kept."
fi

echo ""
echo "Cleanup complete!"
echo ""
