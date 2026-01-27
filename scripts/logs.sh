#!/bin/bash
# ============================================
# 01Blog - View Service Logs
# ============================================
# Usage: ./logs.sh [service_name]
# ============================================

cd "$(dirname "$(dirname "${BASH_SOURCE[0]}")")"

SERVICE="${1:-}"

if [ -n "$SERVICE" ]; then
    docker-compose logs -f --tail=100 "$SERVICE"
else
    docker-compose logs -f --tail=100
fi
