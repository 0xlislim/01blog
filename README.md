# 01blog - Social Blogging Platform

A fullstack social blogging platform where students can share their learning experiences, discoveries, and progress. The platform supports user-generated content, subscriptions, social interactions, and content moderation.

## Tech Stack

### Backend
- **Framework:** Java Spring Boot
- **Database:** PostgreSQL / MySQL
- **Authentication:** JWT (JSON Web Tokens)
- **ORM:** JPA / Hibernate
- **Build Tool:** Maven

### Frontend
- **Framework:** Angular 17+
- **UI Library:** Angular Material
- **State Management:** RxJS
- **Language:** TypeScript
- **Styling:** SCSS

## Features

- **User Authentication:** Registration, login, JWT-based authentication
- **User Profiles:** Public profiles with bio, stats, and post history
- **Posts:** Create, edit, delete posts with text and media (images/videos)
- **Social Features:** Like posts, comment, subscribe to users
- **Notifications:** Real-time notifications for new posts from subscribed users
- **Reporting:** Report inappropriate users/content
- **Admin Dashboard:** User management, post moderation, report handling

## Prerequisites

- **Java:** JDK 17 or higher
- **Node.js:** v18 or higher
- **npm:** v9 or higher
- **PostgreSQL:** v14 or higher (or MySQL v8+)
- **Maven:** v3.8 or higher

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/01blog.git
cd 01blog
```

### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE blog01db;
```

### 3. Backend Setup

Navigate to the backend directory:

```bash
cd backend
```

Configure the database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog01db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Run the backend application:

```bash
./mvnw spring-boot:run
```

The backend will start at `http://localhost:8080`.

### 4. Frontend Setup

Navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm start
# or
ng serve
```

The frontend will start at `http://localhost:4200`.

## Development Commands

### Backend

| Command | Description |
|---------|-------------|
| `./mvnw spring-boot:run` | Run the application |
| `./mvnw clean install` | Build the project |
| `./mvnw test` | Run tests |
| `./mvnw package` | Package as JAR |

### Frontend

| Command | Description |
|---------|-------------|
| `npm start` | Start development server |
| `npm run build` | Build for production |
| `npm test` | Run unit tests |
| `ng lint` | Run linter |

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
│   ├── angular.json
│   └── package.json
└── README.md
```

## Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Deployment

### Backend
```bash
cd backend
./mvnw package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
# Serve the dist/frontend folder with a web server
```

## Security

- All API endpoints (except `/api/auth/*`) require JWT authentication
- Admin endpoints (`/api/admin/*`) require ADMIN role
- Passwords are hashed using bcrypt
- CORS configured for frontend origin
- Input validation and sanitization implemented

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## License

This project is for educational purposes.
