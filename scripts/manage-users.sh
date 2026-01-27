#!/bin/bash
# ============================================
# 01Blog - User Management Script
# ============================================
# Usage:
#   ./manage-users.sh list
#   ./manage-users.sh promote <username>
#   ./manage-users.sh demote <username>
#   ./manage-users.sh create-admin <username> <email> <password>
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
BACKEND_PORT="${BACKEND_PORT:-8080}"

ACTION="${1:-}"

run_sql() {
    docker-compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" -t -A -c "$1"
}

case "$ACTION" in
    list)
        echo ""
        echo "============================================"
        echo "  01Blog - User List"
        echo "============================================"
        echo ""
        echo "ID | Username | Email | Role | Banned | Created"
        echo "---|----------|-------|------|--------|--------"
        run_sql "SELECT id, username, email, role, banned, created_at FROM users ORDER BY id;" | \
            while IFS='|' read -r id username email role banned created; do
                printf "%-3s| %-15s| %-25s| %-6s| %-7s| %s\n" "$id" "$username" "$email" "$role" "$banned" "$created"
            done
        echo ""
        ;;

    promote)
        USERNAME="${2:-}"
        if [ -z "$USERNAME" ]; then
            echo "Usage: ./manage-users.sh promote <username>"
            exit 1
        fi

        echo "Promoting user '$USERNAME' to ADMIN..."

        RESULT=$(run_sql "UPDATE users SET role = 'ADMIN' WHERE username = '$USERNAME' RETURNING username;")
        if [ -n "$RESULT" ]; then
            echo "User '$USERNAME' promoted to ADMIN."
        else
            echo "ERROR: User '$USERNAME' not found."
            exit 1
        fi
        ;;

    demote)
        USERNAME="${2:-}"
        if [ -z "$USERNAME" ]; then
            echo "Usage: ./manage-users.sh demote <username>"
            exit 1
        fi

        echo "Demoting user '$USERNAME' to USER..."

        RESULT=$(run_sql "UPDATE users SET role = 'USER' WHERE username = '$USERNAME' RETURNING username;")
        if [ -n "$RESULT" ]; then
            echo "User '$USERNAME' demoted to USER."
        else
            echo "ERROR: User '$USERNAME' not found."
            exit 1
        fi
        ;;

    create-admin)
        USERNAME="${2:-}"
        EMAIL="${3:-}"
        PASSWORD="${4:-}"

        if [ -z "$USERNAME" ] || [ -z "$EMAIL" ] || [ -z "$PASSWORD" ]; then
            echo "Usage: ./manage-users.sh create-admin <username> <email> <password>"
            exit 1
        fi

        echo "Creating admin user '$USERNAME'..."

        # Use the backend API to create the user (so password gets properly hashed)
        REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
            "http://localhost:${BACKEND_PORT}/api/auth/register" \
            -H "Content-Type: application/json" \
            -d "{\"username\":\"$USERNAME\",\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\",\"displayName\":\"$USERNAME\"}")

        HTTP_CODE=$(echo "$REGISTER_RESPONSE" | tail -1)
        BODY=$(echo "$REGISTER_RESPONSE" | head -1)

        if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ]; then
            # Promote to admin
            run_sql "UPDATE users SET role = 'ADMIN' WHERE username = '$USERNAME';" > /dev/null
            echo "Admin user '$USERNAME' created successfully."
        else
            echo "ERROR: Failed to create user."
            echo "Response ($HTTP_CODE): $BODY"

            # Check if user already exists, offer to promote
            EXISTING=$(run_sql "SELECT username FROM users WHERE username = '$USERNAME';")
            if [ -n "$EXISTING" ]; then
                echo ""
                read -p "User '$USERNAME' already exists. Promote to admin? (y/N) " confirm
                if [ "$confirm" = "y" ]; then
                    run_sql "UPDATE users SET role = 'ADMIN' WHERE username = '$USERNAME';" > /dev/null
                    echo "User '$USERNAME' promoted to ADMIN."
                fi
            fi
        fi
        ;;

    *)
        echo "Usage: ./manage-users.sh <list|promote|demote|create-admin> [args...]"
        echo ""
        echo "Commands:"
        echo "  list                                    - List all users"
        echo "  promote <username>                      - Promote user to ADMIN"
        echo "  demote <username>                       - Demote user to USER"
        echo "  create-admin <username> <email> <pass>  - Create admin user"
        exit 1
        ;;
esac
