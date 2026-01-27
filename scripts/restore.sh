#!/bin/bash
# ============================================
# 01Blog - Restore Script
# ============================================
# Usage: ./restore.sh db <file>
#        ./restore.sh uploads <file>
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# Load environment variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

DB_NAME="${POSTGRES_DB:-blog_db}"
DB_USER="${POSTGRES_USER:-postgres}"

TYPE="${1:-}"
FILE="${2:-}"

if [ -z "$TYPE" ] || [ -z "$FILE" ]; then
    echo "Usage: ./restore.sh <db|uploads> <file>"
    exit 1
fi

if [ ! -f "$FILE" ]; then
    echo "ERROR: File not found: $FILE"
    exit 1
fi

echo ""
echo "============================================"
echo "  01Blog - Restore ($TYPE)"
echo "============================================"
echo ""

case "$TYPE" in
    db)
        echo "Restoring database from: $FILE"
        echo ""
        read -p "WARNING: This will overwrite the current database. Continue? (y/N) " confirm
        if [ "$confirm" != "y" ]; then
            echo "Aborted."
            exit 0
        fi
        docker-compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" < "$FILE"
        echo ""
        echo "Database restored successfully."
        ;;
    uploads)
        echo "Restoring uploads from: $FILE"
        echo ""
        read -p "WARNING: This will overwrite current uploads. Continue? (y/N) " confirm
        if [ "$confirm" != "y" ]; then
            echo "Aborted."
            exit 0
        fi
        docker-compose exec -T backend sh -c "rm -rf /app/uploads/*"
        cat "$FILE" | docker-compose exec -T backend tar xzf - -C /app
        echo ""
        echo "Uploads restored successfully."
        ;;
    *)
        echo "ERROR: Unknown type '$TYPE'. Use 'db' or 'uploads'."
        exit 1
        ;;
esac

echo ""
