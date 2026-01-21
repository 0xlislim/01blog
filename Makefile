# ============================================
# 01Blog - Makefile
# ============================================
# Convenience commands for development and deployment
# Usage: make <target>
# ============================================

.PHONY: help setup dev prod stop restart logs health backup restore clean test build

# Default target
help:
	@echo ""
	@echo "01Blog - Available Commands"
	@echo "============================================"
	@echo ""
	@echo "Setup & Deployment:"
	@echo "  make setup      - Initial project setup"
	@echo "  make dev        - Start in development mode"
	@echo "  make stop       - Stop all services"
	@echo "  make restart    - Restart all services"
	@echo ""
	@echo "Monitoring:"
	@echo "  make logs       - View all logs"
	@echo "  make logs-be    - View backend logs"
	@echo "  make logs-fe    - View frontend logs"
	@echo "  make logs-db    - View database logs"
	@echo "  make health     - Run health check"
	@echo "  make ps         - Show running containers"
	@echo ""
	@echo "Backup & Restore:"
	@echo "  make backup     - Create backup"
	@echo "  make restore-db FILE=<file>     - Restore database"
	@echo "  make restore-uploads FILE=<file> - Restore uploads"
	@echo ""
	@echo "Development:"
	@echo "  make test       - Run all tests"
	@echo "  make test-be    - Run backend tests"
	@echo "  make test-fe    - Run frontend tests"
	@echo "  make build      - Build Docker images"
	@echo "  make clean      - Clean up Docker resources"
	@echo ""
	@echo "Database:"
	@echo "  make db-shell   - Open PostgreSQL shell"
	@echo "  make db-reset   - Reset database (DESTRUCTIVE)"
	@echo ""
	@echo "User Management:"
	@echo "  make users                          - List all users"
	@echo "  make promote USER=<username>        - Promote user to admin"
	@echo "  make demote USER=<username>         - Demote admin to user"
	@echo "  make create-admin USER=x EMAIL=y PASS=z - Create admin"
	@echo ""

# ==========================================
# Setup & Deployment
# ==========================================

setup:
	@./scripts/setup.sh

dev:
	@./scripts/deploy.sh

stop:
	@./scripts/stop.sh

restart:
	@./scripts/restart.sh

# ==========================================
# Monitoring
# ==========================================

logs:
	@./scripts/logs.sh

logs-be:
	@./scripts/logs.sh backend

logs-fe:
	@./scripts/logs.sh frontend

logs-db:
	@./scripts/logs.sh postgres

health:
	@./scripts/health-check.sh

ps:
	@docker-compose ps

# ==========================================
# Backup & Restore
# ==========================================

backup:
	@./scripts/backup.sh

restore-db:
	@./scripts/restore.sh db $(FILE)

restore-uploads:
	@./scripts/restore.sh uploads $(FILE)

# ==========================================
# Development
# ==========================================

test: test-be test-fe

test-be:
	@echo "Running backend tests..."
	@cd backend && ./mvnw test -B

test-fe:
	@echo "Running frontend tests..."
	@cd frontend && npm test -- --watch=false --browsers=ChromeHeadless

build:
	@echo "Building Docker images..."
	@docker-compose build

build-no-cache:
	@echo "Building Docker images (no cache)..."
	@docker-compose build --no-cache

clean:
	@./scripts/cleanup.sh

# ==========================================
# Database
# ==========================================

db-shell:
	@docker-compose exec postgres psql -U postgres -d blog_db

db-reset:
	@echo "WARNING: This will delete all data!"
	@read -p "Are you sure? (y/N) " confirm && [ "$$confirm" = "y" ] && \
		docker-compose down -v && \
		docker volume rm 01blog-postgres-data 2>/dev/null || true && \
		echo "Database reset. Run 'make dev' to start fresh."

# ==========================================
# User Management
# ==========================================

users:
	@./scripts/manage-users.sh list

promote:
	@./scripts/manage-users.sh promote $(USER)

demote:
	@./scripts/manage-users.sh demote $(USER)

create-admin:
	@./scripts/manage-users.sh create-admin $(USER) $(EMAIL) $(PASS)

# ==========================================
# Utility
# ==========================================

shell-be:
	@docker-compose exec backend sh

shell-fe:
	@docker-compose exec frontend sh

prune:
	@docker system prune -af

stats:
	@docker stats --no-stream
