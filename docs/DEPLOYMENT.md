# 01Blog Deployment Guide

This guide covers deploying the 01Blog application using Docker containers.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Default Admin User](#default-admin-user)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Deployment Scripts](#deployment-scripts)
- [User Management](#user-management)
- [Docker Configuration](#docker-configuration)
- [Production Deployment](#production-deployment)
- [Backup and Restore](#backup-and-restore)
- [Monitoring and Health Checks](#monitoring-and-health-checks)
- [CI/CD Pipeline](#cicd-pipeline)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before deploying, ensure you have the following installed:

| Requirement | Minimum Version | Check Command |
|-------------|-----------------|---------------|
| Docker | 20.10+ | `docker --version` |
| Docker Compose | 2.0+ | `docker compose version` |
| Make (optional) | Any | `make --version` |

## Quick Start

```bash
# 1. Clone the repository
git clone <repository-url>
cd 01blog

# 2. Run initial setup
make setup
# or
./scripts/setup.sh

# 3. Start in development mode
make dev
# or
./scripts/deploy.sh dev

# 4. Access the application
# Frontend: http://localhost:4200
# Backend API: http://localhost:8080/api
# Swagger UI: http://localhost:8080/swagger-ui/swagger-ui.html

# 5. Login with default admin
# Username: admin
# Password: Admin123
```

---

## Default Admin User

On first deployment, a default administrator account is created automatically:

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `Admin123` |
| Email | `admin@01blog.local` |
| Role | `ADMIN` |

### Security Warning

**Change the default admin password immediately after first login!**

To create additional admin users or change roles, see [User Management](#user-management).

---

## Project Structure

```
01blog/
├── backend/
│   └── Dockerfile          # Backend multi-stage build
├── frontend/
│   ├── Dockerfile          # Frontend multi-stage build
│   ├── nginx.conf          # Nginx config (development)
│   └── nginx-ssl.conf      # Nginx config (production with SSL)
├── scripts/
│   ├── setup.sh            # Initial project setup
│   ├── deploy.sh           # Deploy services
│   ├── stop.sh             # Stop services
│   ├── restart.sh          # Restart services
│   ├── logs.sh             # View logs
│   ├── health-check.sh     # Health check
│   ├── backup.sh           # Create backups
│   ├── restore.sh          # Restore from backup
│   ├── cleanup.sh          # Clean Docker resources
│   ├── manage-users.sh     # User management (roles, admin)
│   └── init-db.sql         # Database initialization
├── nginx/
│   └── ssl/                # SSL certificates (production)
├── backups/                # Backup files
├── docker-compose.yml      # Base Docker Compose config
├── docker-compose.prod.yml # Production overrides
├── .env.example            # Environment template
├── .env                    # Environment variables (create from template)
└── Makefile                # Convenience commands
```

---

## Configuration

### Environment Variables

Create a `.env` file from the template:

```bash
cp .env.example .env
```

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `POSTGRES_DB` | Database name | `blog_db` | Yes |
| `POSTGRES_USER` | Database user | `postgres` | Yes |
| `POSTGRES_PASSWORD` | Database password | - | Yes |
| `POSTGRES_PORT` | Database port | `5432` | No |
| `BACKEND_PORT` | Backend API port | `8080` | No |
| `FRONTEND_PORT` | Frontend port | `4200` | No |
| `JWT_SECRET` | JWT signing secret (min 256 bits) | - | Yes |
| `JWT_EXPIRATION` | JWT expiration in ms | `86400000` | No |
| `SHOW_SQL` | Log SQL queries | `false` | No |

### Security Notes

- **Never commit `.env` to version control**
- Use strong, unique passwords in production
- Generate JWT secret with: `openssl rand -base64 48`
- Rotate secrets periodically
- **Change the default admin password after first login**

---

## Deployment Scripts

### setup.sh - Initial Setup

Prepares the project for first-time deployment.

```bash
./scripts/setup.sh
```

**What it does:**
- Checks Docker and Docker Compose are installed
- Creates `.env` from template with secure random values
- Makes all scripts executable
- Creates required directories (`backups/`, `nginx/ssl/`)
- Optionally generates self-signed SSL certificates

---

### deploy.sh - Deploy Services

Builds and starts all Docker containers.

```bash
# Development mode
./scripts/deploy.sh dev

# Production mode
./scripts/deploy.sh prod
```

**Development mode:**
- Exposes all ports (database: 5432, backend: 8080, frontend: 4200)
- Shows SQL queries if enabled
- Uses HTTP-only nginx config

**Production mode:**
- Only exposes ports 80 and 443
- Enables SSL/HTTPS
- Applies resource limits
- Configures log rotation

---

### stop.sh - Stop Services

Stops all running containers.

```bash
./scripts/stop.sh
```

---

### restart.sh - Restart Services

Restarts containers without rebuilding.

```bash
# Restart all services
./scripts/restart.sh

# Restart specific service
./scripts/restart.sh backend
./scripts/restart.sh frontend
./scripts/restart.sh postgres
```

---

### logs.sh - View Logs

Displays container logs with live follow.

```bash
# All services
./scripts/logs.sh

# Specific service
./scripts/logs.sh backend
./scripts/logs.sh frontend
./scripts/logs.sh postgres
```

---

### health-check.sh - Health Check

Checks the health status of all services.

```bash
./scripts/health-check.sh
```

**Output includes:**
- Docker daemon status
- Container status and ports
- PostgreSQL connectivity and stats
- Backend API health
- Frontend nginx health
- Resource usage (CPU, memory)
- Volume disk usage

---

### backup.sh - Create Backup

Creates backups of database and uploaded files.

```bash
./scripts/backup.sh
```

**Creates:**
- `backups/db_backup_YYYYMMDD_HHMMSS.sql.gz` - Database dump
- `backups/uploads_backup_YYYYMMDD_HHMMSS.tar.gz` - Uploaded files

**Retention:** Keeps last 7 backups automatically.

---

### restore.sh - Restore from Backup

Restores database or uploads from a backup file.

```bash
# Restore database
./scripts/restore.sh db backups/db_backup_20240101_120000.sql.gz

# Restore uploads
./scripts/restore.sh uploads backups/uploads_backup_20240101_120000.tar.gz
```

**Warning:** This will overwrite existing data. Confirmation required.

---

### cleanup.sh - Clean Docker Resources

Removes unused Docker resources to free disk space.

```bash
./scripts/cleanup.sh
```

**Actions:**
- Stops running services
- Removes stopped containers
- Removes dangling images
- Removes unused networks
- Optionally removes 01blog images
- Optionally removes volumes (**data loss!**)

---

## User Management

### manage-users.sh - User Management Script

Manage user accounts and roles from the command line.

```bash
./scripts/manage-users.sh <command> [options]
```

### Commands

#### List All Users

```bash
./scripts/manage-users.sh list
# or
make users
```

Output:
```
 id | username  |       email        | role  | banned |         created_at
----+-----------+--------------------+-------+--------+----------------------------
  1 | admin     | admin@01blog.local | ADMIN | f      | 2024-01-01 00:00:00
  2 | john      | john@example.com   | USER  | f      | 2024-01-02 10:30:00
```

#### Promote User to Admin

```bash
./scripts/manage-users.sh promote <username>
# or
make promote USER=<username>
```

Example:
```bash
./scripts/manage-users.sh promote john
# Output: [SUCCESS] User 'john' has been promoted to ADMIN
```

#### Demote Admin to Regular User

```bash
./scripts/manage-users.sh demote <username>
# or
make demote USER=<username>
```

Example:
```bash
./scripts/manage-users.sh demote john
# Output: [SUCCESS] User 'john' has been demoted to USER
```

**Note:** Cannot demote the last remaining admin.

#### Create New Admin User

```bash
./scripts/manage-users.sh create-admin <username> <email> <password>
# or
make create-admin USER=<username> EMAIL=<email> PASS=<password>
```

Example:
```bash
./scripts/manage-users.sh create-admin newadmin admin@example.com SecurePass123
# Output: [SUCCESS] Admin user 'newadmin' created successfully
```

### Using SQL Directly

For advanced operations, access the database directly:

```bash
# Open database shell
make db-shell

# Promote user to admin
UPDATE users SET role = 'ADMIN' WHERE username = 'someuser';

# Demote admin to user
UPDATE users SET role = 'USER' WHERE username = 'someuser';

# Ban a user
UPDATE users SET banned = TRUE WHERE username = 'baduser';

# Unban a user
UPDATE users SET banned = FALSE WHERE username = 'baduser';

# Delete a user
DELETE FROM users WHERE username = 'someuser';
```

---

## Docker Configuration

### Backend Dockerfile

Multi-stage build for Spring Boot application:

| Stage | Base Image | Purpose |
|-------|------------|---------|
| Builder | `maven:3.9-eclipse-temurin-17-alpine` | Compile and package |
| Runtime | `eclipse-temurin:17-jre-alpine` | Run application |

**Features:**
- Dependency caching for faster builds
- Non-root user for security
- JVM container optimizations
- Health check endpoint (`/actuator/health`)

---

### Frontend Dockerfile

Multi-stage build for Angular application:

| Stage | Base Image | Purpose |
|-------|------------|---------|
| Builder | `node:20-alpine` | Build Angular app |
| Runtime | `nginx:alpine` | Serve static files |

**Features:**
- npm dependency caching
- Production build optimization
- Configurable nginx config (SSL/non-SSL)
- Non-root user for security
- API proxy to backend

---

### Docker Compose Services

| Service | Image | Ports (Dev) | Ports (Prod) |
|---------|-------|-------------|--------------|
| postgres | `postgres:16-alpine` | 5432 | Internal only |
| backend | Custom build | 8080 | Internal only |
| frontend | Custom build | 4200 | 80, 443 |

---

## Production Deployment

### 1. Prepare SSL Certificates

Place your SSL certificates in the `nginx/ssl/` directory:

```bash
nginx/ssl/
├── cert.pem    # SSL certificate
└── key.pem     # Private key
```

**For Let's Encrypt:**
```bash
# Using certbot
certbot certonly --standalone -d yourdomain.com

# Copy certificates
cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem nginx/ssl/cert.pem
cp /etc/letsencrypt/live/yourdomain.com/privkey.pem nginx/ssl/key.pem
```

### 2. Configure Environment

Update `.env` with production values:

```bash
POSTGRES_PASSWORD=<strong-random-password>
JWT_SECRET=<256-bit-random-secret>
SHOW_SQL=false
FRONTEND_PORT=80
```

### 3. Deploy

```bash
./scripts/deploy.sh prod
```

### 4. Verify Deployment

```bash
./scripts/health-check.sh
```

### 5. Change Default Admin Password

Login with `admin` / `Admin123` and change the password immediately.

### Production Features

| Feature | Description |
|---------|-------------|
| HTTPS | TLS 1.2/1.3 with strong ciphers |
| Security Headers | X-Frame-Options, CSP, XSS protection |
| Gzip Compression | Enabled for text assets |
| Static Caching | 1-year cache for assets |
| Log Rotation | Prevents disk space issues |
| Resource Limits | CPU and memory constraints |
| Restart Policy | Auto-restart on failure |

---

## Backup and Restore

### Automated Backup Strategy

Set up a cron job for regular backups:

```bash
# Edit crontab
crontab -e

# Add daily backup at 2 AM
0 2 * * * cd /path/to/01blog && ./scripts/backup.sh >> /var/log/01blog-backup.log 2>&1
```

### Manual Backup

```bash
# Create backup
./scripts/backup.sh

# List backups
ls -la backups/
```

### Restore Procedure

1. Stop the application (optional but recommended)
2. Restore database and/or uploads
3. Restart the application

```bash
./scripts/stop.sh
./scripts/restore.sh db backups/db_backup_20240101_120000.sql.gz
./scripts/restore.sh uploads backups/uploads_backup_20240101_120000.tar.gz
./scripts/deploy.sh prod
```

---

## Monitoring and Health Checks

### Health Check Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /actuator/health` | Backend Spring Boot health |
| `GET /health` | Frontend nginx health |

### Container Health Checks

All containers have built-in health checks:

```bash
# View health status
docker-compose ps

# Detailed health info
docker inspect 01blog-backend | jq '.[0].State.Health'
```

### Resource Monitoring

```bash
# Real-time stats
docker stats

# One-time snapshot
make stats
```

---

## CI/CD Pipeline

The project includes a GitHub Actions workflow (`.github/workflows/ci.yml`).

### Pipeline Stages

| Stage | Trigger | Actions |
|-------|---------|---------|
| Backend Tests | Push, PR | Maven test with PostgreSQL |
| Frontend Tests | Push, PR | npm test with Chrome headless |
| Docker Build | Push to main | Build and cache images |
| Security Scan | Push to main | Trivy vulnerability scan |

### Running Locally

```bash
# Run all tests
make test

# Run backend tests only
make test-be

# Run frontend tests only
make test-fe
```

---

## Troubleshooting

### Common Issues

#### Containers won't start

```bash
# Check logs
./scripts/logs.sh

# Check container status
docker-compose ps

# Rebuild images
docker-compose build --no-cache
```

#### Database connection issues

```bash
# Check if PostgreSQL is ready
docker-compose exec postgres pg_isready

# Access database shell
make db-shell
```

#### Port already in use

```bash
# Find process using port
lsof -i :8080

# Change port in .env
BACKEND_PORT=8081
```

#### Port 80 permission denied

If you see "cannot expose privileged port 80", use a higher port:

```bash
# In .env file
FRONTEND_PORT=4200
```

#### Out of disk space

```bash
# Clean up Docker resources
./scripts/cleanup.sh

# Check disk usage
docker system df
```

#### Permission denied on scripts

```bash
# Make scripts executable
chmod +x scripts/*.sh
```

#### Registration/Login not working

Check if the nginx proxy is properly forwarding API requests:

```bash
# Test directly to backend
curl http://localhost:8080/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin123"}'

# Test through nginx proxy
curl http://localhost:4200/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin123"}'
```

### Useful Commands

```bash
# Access backend shell
docker-compose exec backend sh

# Access frontend shell
docker-compose exec frontend sh

# View environment variables
docker-compose exec backend env

# Force recreate containers
docker-compose up -d --force-recreate

# Remove everything and start fresh
make db-reset
make dev
```

---

## Makefile Reference

### Setup & Deployment

| Command | Description |
|---------|-------------|
| `make help` | Show all available commands |
| `make setup` | Run initial setup |
| `make dev` | Start development environment |
| `make prod` | Start production environment |
| `make stop` | Stop all services |
| `make restart` | Restart all services |

### Monitoring

| Command | Description |
|---------|-------------|
| `make logs` | View all logs |
| `make logs-be` | View backend logs |
| `make logs-fe` | View frontend logs |
| `make logs-db` | View database logs |
| `make health` | Run health check |
| `make ps` | Show running containers |
| `make stats` | Show resource usage |

### Development

| Command | Description |
|---------|-------------|
| `make test` | Run all tests |
| `make test-be` | Run backend tests |
| `make test-fe` | Run frontend tests |
| `make build` | Build Docker images |
| `make build-no-cache` | Build without cache |
| `make clean` | Clean Docker resources |

### Database

| Command | Description |
|---------|-------------|
| `make db-shell` | Open PostgreSQL shell |
| `make db-reset` | Reset database (destructive) |

### User Management

| Command | Description |
|---------|-------------|
| `make users` | List all users |
| `make promote USER=<name>` | Promote user to admin |
| `make demote USER=<name>` | Demote admin to user |
| `make create-admin USER=x EMAIL=y PASS=z` | Create new admin |

### Backup & Restore

| Command | Description |
|---------|-------------|
| `make backup` | Create backup |
| `make restore-db FILE=<path>` | Restore database |
| `make restore-uploads FILE=<path>` | Restore uploads |

### Utility

| Command | Description |
|---------|-------------|
| `make shell-be` | Access backend shell |
| `make shell-fe` | Access frontend shell |
| `make prune` | Remove all unused Docker resources |

---

## Support

For issues and questions:
- Check the [Troubleshooting](#troubleshooting) section
- Review container logs: `./scripts/logs.sh`
- Run health check: `./scripts/health-check.sh`
