# 01blog - Social Blogging Platform

A fullstack social blogging platform where students can share their learning experiences, discoveries, and progress. The platform supports user-generated content, subscriptions, social interactions, and content moderation.

## Tech Stack

### Backend
- **Framework:** Java Spring Boot
- **Database:** PostgreSQL
- **Authentication:** JWT (JSON Web Tokens)
- **ORM:** JPA / Hibernate
- **Build Tool:** Maven

### Frontend
- **Framework:** Angular 17+
- **UI Library:** Angular Material
- **State Management:** RxJS
- **Language:** TypeScript
- **Styling:** SCSS

### DevOps
- **Containerization:** Docker & Docker Compose
- **Automation:** Make, Bash scripts
- **Database Init:** SQL scripts

## Features

- **User Authentication:** Registration, login, JWT-based authentication
- **User Profiles:** Public profiles with bio, stats, and post history
- **Posts:** Create, edit, delete posts with text and media (images/videos)
- **Social Features:** Like posts, comment, subscribe to users
- **Notifications:** Real-time notifications for new posts from subscribed users
- **Reporting:** Report inappropriate users/content
- **Admin Dashboard:** User management, post moderation, report handling

## Prerequisites

- **Docker:** v20 or higher
- **Docker Compose:** v2 or higher
- **Make:** GNU Make

> Run `make help` to see all available commands.

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/01blog.git
cd 01blog
```

### 2. Initial Setup

Run the setup script to configure the environment:

```bash
make setup
```

This will:
- Create necessary configuration files
- Set up environment variables
- Initialize the database

### 3. Start Development Server

Start all services (backend, frontend, database) with Docker:

```bash
make dev
```

- Backend will start at `http://localhost:8080`
- Frontend will start at `http://localhost:4200`

### 4. Verify Services

Check that all services are running:

```bash
make health
```

View running containers:

```bash
make ps
```

## Development Commands

### Setup & Deployment

| Command | Description |
|---------|-------------|
| `make setup` | Initial project setup |
| `make dev` | Start all services in development mode |
| `make stop` | Stop all services |
| `make restart` | Restart all services |
| `make build` | Build Docker images |
| `make build-no-cache` | Build Docker images without cache |
| `make clean` | Clean up Docker resources |

### Monitoring

| Command | Description |
|---------|-------------|
| `make logs` | View all logs |
| `make logs-be` | View backend logs |
| `make logs-fe` | View frontend logs |
| `make logs-db` | View database logs |
| `make health` | Run health check |
| `make ps` | Show running containers |
| `make stats` | Show container resource usage |

### Testing

| Command | Description |
|---------|-------------|
| `make test` | Run all tests |
| `make test-be` | Run backend tests only |
| `make test-fe` | Run frontend tests only |

### Database

| Command | Description |
|---------|-------------|
| `make db-shell` | Open PostgreSQL shell |
| `make db-reset` | Reset database (DESTRUCTIVE) |

### User Management

| Command | Description |
|---------|-------------|
| `make users` | List all users |
| `make promote USER=<username>` | Promote user to admin |
| `make demote USER=<username>` | Demote admin to user |
| `make create-admin USER=x EMAIL=y PASS=z` | Create admin user |

### Backup & Restore

| Command | Description |
|---------|-------------|
| `make backup` | Create backup |
| `make restore-db FILE=<file>` | Restore database from backup |
| `make restore-uploads FILE=<file>` | Restore uploads from backup |

### Utility

| Command | Description |
|---------|-------------|
| `make shell-be` | Open shell in backend container |
| `make shell-fe` | Open shell in frontend container |
| `make prune` | Remove all unused Docker resources |

## API Documentation

The backend exposes REST APIs at `http://localhost:8080/api`:

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Users
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update profile
- `GET /api/users/search` - Search users

### Posts
- `GET /api/posts/feed` - Get personalized feed
- `POST /api/posts` - Create new post
- `PUT /api/posts/{id}` - Update post
- `DELETE /api/posts/{id}` - Delete post
- `POST /api/posts/{id}/like` - Toggle like

### Subscriptions
- `POST /api/subscriptions/{userId}` - Subscribe to user
- `DELETE /api/subscriptions/{userId}` - Unsubscribe

### Notifications
- `GET /api/notifications` - Get all notifications
- `PUT /api/notifications/{id}/read` - Mark as read

### Admin (requires ADMIN role)
- `GET /api/admin/users` - List all users
- `POST /api/admin/users/{id}/ban` - Ban user
- `DELETE /api/admin/posts/{id}` - Delete post
- `GET /api/admin/reports` - View reports

## Project Structure

```
01blog/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java source files
│   │   │   └── resources/  # Configuration files
│   │   └── test/           # Test files
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/       # Services, guards, interceptors
│   │   │   ├── features/   # Feature modules
│   │   │   ├── shared/     # Shared components
│   │   │   └── layout/     # Layout components
│   │   ├── assets/
│   │   └── environments/
│   ├── Dockerfile
│   ├── angular.json
│   └── package.json
├── scripts/                 # Automation scripts
│   ├── setup.sh            # Initial project setup
│   ├── deploy.sh           # Start services
│   ├── stop.sh             # Stop services
│   ├── restart.sh          # Restart services
│   ├── backup.sh           # Create backups
│   ├── restore.sh          # Restore from backup
│   ├── health-check.sh     # Health check
│   ├── logs.sh             # View logs
│   ├── manage-users.sh     # User management
│   ├── cleanup.sh          # Clean Docker resources
│   └── init-db.sql         # Database initialization
├── docs/                    # Documentation
│   ├── BACKEND_DOCUMENTATION.md
│   ├── BACKEND_DOCUMENTATION_TECH.md
│   ├── FRONTEND_DOCUMENTATION.md
│   └── DEPLOYMENT.md
├── backups/                 # Backup storage
├── docker-compose.yml       # Docker services configuration
├── Makefile                 # Make commands
├── .env                     # Environment variables
└── README.md
```

## Testing

### Run All Tests
```bash
make test
```

### Backend Tests Only
```bash
make test-be
```

### Frontend Tests Only
```bash
make test-fe
```

## Deployment

### Build and Deploy with Docker

```bash
# Build Docker images
make build

# Start all services
make dev

# Check health status
make health
```

### Create Backups

```bash
# Create a full backup (database + uploads)
make backup
```

### Restore from Backup

```bash
# Restore database
make restore-db FILE=backups/db_backup_2024-01-15.sql

# Restore uploads
make restore-uploads FILE=backups/uploads_backup_2024-01-15.tar.gz
```

## Default Accounts

After setup, you can create an admin user:

```bash
make create-admin USER=admin EMAIL=admin@example.com PASS=admin123
```

Or promote an existing user:

```bash
make promote USER=username
```

## Environment Variables

The `.env` file contains configuration for all services:

| Variable | Description | Default |
|----------|-------------|---------|
| `POSTGRES_DB` | Database name | `blog_db` |
| `POSTGRES_USER` | Database user | `postgres` |
| `POSTGRES_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing secret | - |
| `BACKEND_PORT` | Backend server port | `8080` |
| `FRONTEND_PORT` | Frontend server port | `4200` |

> The `make setup` command will create the `.env` file with default values.

## Security

- All API endpoints (except `/api/auth/*`) require JWT authentication
- Admin endpoints (`/api/admin/*`) require ADMIN role
- Passwords are hashed using bcrypt
- CORS configured for frontend origin
- Input validation and sanitization implemented

## Troubleshooting

### Services won't start
```bash
# Check container status
make ps

# View logs for errors
make logs

# Rebuild containers
make build-no-cache
make dev
```

### Database connection issues
```bash
# Check database logs
make logs-db

# Reset database (WARNING: deletes all data)
make db-reset
make dev
```

### Port already in use
```bash
# Stop all services
make stop

# Check what's using the port
lsof -i :8080
lsof -i :4200
```

### Clean restart
```bash
make stop
make clean
make build
make dev
```

## Documentation

- [Backend API Documentation](docs/BACKEND_DOCUMENTATION.md)
- [Backend Technical Details](docs/BACKEND_DOCUMENTATION_TECH.md)
- [Frontend Documentation](docs/FRONTEND_DOCUMENTATION.md)
- [Deployment Guide](docs/DEPLOYMENT.md)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## License

This project is for educational purposes.
