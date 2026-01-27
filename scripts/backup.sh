#!/bin/bash
# ============================================
# 01Blog - Backup Script
# ============================================
# Creates backups of database and uploads
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
BACKUP_DIR="$PROJECT_DIR/backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

cd "$PROJECT_DIR"

# Load environment variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

DB_NAME="${POSTGRES_DB:-blog_db}"
DB_USER="${POSTGRES_USER:-postgres}"

echo ""
echo "============================================"
echo "  01Blog - Backup"
echo "============================================"
echo ""

mkdir -p "$BACKUP_DIR"

# Database backup
echo "Backing up database..."
DB_BACKUP="$BACKUP_DIR/db_${TIMESTAMP}.sql"
docker-compose exec -T postgres pg_dump -U "$DB_USER" "$DB_NAME" > "$DB_BACKUP"
echo "  Database backup: $DB_BACKUP ($(du -h "$DB_BACKUP" | cut -f1))"

# Uploads backup
echo "Backing up uploads..."
UPLOADS_BACKUP="$BACKUP_DIR/uploads_${TIMESTAMP}.tar.gz"
docker-compose exec -T backend tar czf - -C /app uploads 2>/dev/null > "$UPLOADS_BACKUP" || true
if [ -s "$UPLOADS_BACKUP" ]; then
    echo "  Uploads backup: $UPLOADS_BACKUP ($(du -h "$UPLOADS_BACKUP" | cut -f1))"
else
    rm -f "$UPLOADS_BACKUP"
    echo "  No uploads to backup."
fi

echo ""
echo "Backup complete!"
echo ""
