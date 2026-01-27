#!/bin/bash
# ============================================
# 01Blog - Initial Project Setup
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo ""
echo "============================================"
echo "  01Blog - Project Setup"
echo "============================================"
echo ""

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed."
    echo "Please install Docker: https://docs.docker.com/get-docker/"
    exit 1
fi
echo "  Docker: OK"

if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "ERROR: Docker Compose is not installed."
    echo "Please install Docker Compose: https://docs.docker.com/compose/install/"
    exit 1
fi
echo "  Docker Compose: OK"

if ! command -v java &> /dev/null; then
    echo "WARNING: Java is not installed (needed for local backend development)."
else
    echo "  Java: OK ($(java -version 2>&1 | head -1))"
fi

if ! command -v node &> /dev/null; then
    echo "WARNING: Node.js is not installed (needed for local frontend development)."
else
    echo "  Node.js: OK ($(node -v))"
fi

echo ""

# Create .env file if it doesn't exist
if [ ! -f "$PROJECT_DIR/.env" ]; then
    echo "Creating .env file..."
    cat > "$PROJECT_DIR/.env" << 'EOF'
# ============================================
# 01Blog - Environment Configuration
# ============================================

# Database Configuration
POSTGRES_DB=blog_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5432

# Backend Configuration
BACKEND_PORT=8080

# JWT Configuration
JWT_SECRET=mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong
JWT_EXPIRATION=86400000

# Show SQL queries
SHOW_SQL=true

# Frontend Configuration
FRONTEND_PORT=4200
EOF
    echo "  .env file created."
else
    echo "  .env file already exists, skipping."
fi

echo ""

# Create required directories
echo "Creating required directories..."
mkdir -p "$PROJECT_DIR/backups"
echo "  backups/ created."

echo ""

# Build Docker images
echo "Building Docker images..."
cd "$PROJECT_DIR"
docker-compose build

echo ""
echo "============================================"
echo "  Setup complete!"
echo "============================================"
echo ""
echo "Next steps:"
echo "  1. Review the .env file and adjust settings if needed"
echo "  2. Run 'make dev' to start the application"
echo "  3. Access the frontend at http://localhost:4200"
echo "  4. Access the backend API at http://localhost:8080/api"
echo ""
