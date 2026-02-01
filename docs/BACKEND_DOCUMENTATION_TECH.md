# 01Blog Backend - Technical Documentation

## Table of Contents
1. [Spring Boot Overview](#1-spring-boot-overview)
2. [Project Architecture](#2-project-architecture)
3. [Request Lifecycle](#3-request-lifecycle)
4. [Security & Authentication](#4-security--authentication)
5. [Database Layer](#5-database-layer)
6. [REST API Design](#6-rest-api-design)
7. [Service Layer](#7-service-layer)
8. [Exception Handling](#8-exception-handling)
9. [Data Transfer Objects (DTOs)](#9-data-transfer-objects-dtos)
10. [Dependency Injection](#10-dependency-injection)
11. [Configuration](#11-configuration)
12. [Testing](#12-testing)
13. [Database Migrations](#13-database-migrations)
14. [File Upload](#14-file-upload)

---

## 1. Spring Boot Overview

### What is Spring Boot?
Spring Boot is a framework built on top of Spring Framework that simplifies the development of production-ready applications. It provides:
- **Auto-configuration**: Automatically configures components based on dependencies
- **Embedded server**: Runs without external application server (Tomcat embedded)
- **Starter dependencies**: Pre-configured dependency bundles
- **Production-ready features**: Health checks, metrics, externalized configuration

### Spring Boot vs Spring Framework

```
┌─────────────────────────────────────────────────────────────┐
│                      SPRING BOOT                            │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              AUTO-CONFIGURATION                     │    │
│  │  • Detects classpath dependencies                   │    │
│  │  • Configures beans automatically                   │    │
│  │  • Reduces boilerplate XML/Java config              │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              SPRING FRAMEWORK                       │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐             │    │
│  │  │Spring MVC│ │Spring    │ │Spring    │             │    │
│  │  │(Web)     │ │Security  │ │Data JPA  │             │    │
│  │  └──────────┘ └──────────┘ └──────────┘             │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐             │    │
│  │  │Spring AOP│ │Spring TX │ │Spring    │             │    │
│  │  │          │ │(Trans.)  │ │Context   │             │    │
│  │  └──────────┘ └──────────┘ └──────────┘             │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              EMBEDDED SERVER (Tomcat)               │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Key Annotations

| Annotation | Purpose |
|------------|---------|
| `@SpringBootApplication` | Main entry point, combines @Configuration, @EnableAutoConfiguration, @ComponentScan |
| `@RestController` | Marks class as REST API controller (returns JSON) |
| `@Service` | Marks class as service layer component |
| `@Repository` | Marks class as data access layer component |
| `@Entity` | Marks class as JPA entity (database table) |
| `@Autowired` | Injects dependencies automatically |
| `@Component` | Generic Spring-managed bean |

---

## 2. Project Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT (Angular)                        │
│                      http://localhost:4200                      │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTP Requests (JSON)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT APPLICATION                     │
│                      http://localhost:8080                      │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                   SECURITY LAYER                          │  │
│  │  ┌─────────────────┐  ┌─────────────────┐                 │  │
│  │  │ JwtAuthFilter   │  │ SecurityConfig  │                 │  │
│  │  │ (validates JWT) │  │ (defines rules) │                 │  │
│  │  └─────────────────┘  └─────────────────┘                 │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                  │
│                              ▼                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                   CONTROLLER LAYER                        │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │  │
│  │  │Auth      │ │User      │ │Post      │ │Admin     │      │  │
│  │  │Controller│ │Controller│ │Controller│ │Controller│      │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘      │  │
│  │  • Receives HTTP requests                                 │  │
│  │  • Validates input (@Valid)                               │  │
│  │  • Returns HTTP responses                                 │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                  │
│                              ▼                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    SERVICE LAYER                          │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │  │
│  │  │Auth      │ │User      │ │Post      │ │Admin     │      │  │
│  │  │Service   │ │Service   │ │Service   │ │Service   │      │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘      │  │
│  │  • Business logic                                         │  │
│  │  • Transaction management (@Transactional)                │  │
│  │  • Validation rules                                       │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                  │
│                              ▼                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                  REPOSITORY LAYER                         │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │  │
│  │  │User      │ │Post      │ │Comment   │ │Like      │      │  │
│  │  │Repository│ │Repository│ │Repository│ │Repository│      │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘      │  │
│  │  • Data access (CRUD operations)                          │  │
│  │  • Custom queries                                         │  │
│  │  • Extends JpaRepository                                  │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                  │
│                              ▼                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    ENTITY LAYER                           │  │
│  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐    │  │
│  │  │User  │ │Post  │ │Comment││Like  │ │Sub-  │ │Report│    │  │
│  │  │      │ │      │ │      │ │      │ │script│ │      │    │  │
│  │  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘    │  │
│  │  • JPA entities (mapped to database tables)               │  │
│  │  • Relationships (@OneToMany, @ManyToOne)                 │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      POSTGRESQL DATABASE                        │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐          │
│  │users │ │posts │ │comms │ │likes │ │subs  │ │reports│         │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘          │
└─────────────────────────────────────────────────────────────────┘
```

### Project Package Structure

```
backend/src/main/java/com/blog/backend/
├── BackendApplication.java          # Entry point (@SpringBootApplication)
├── config/
│   ├── SecurityConfig.java          # Spring Security configuration
│   └── OpenApiConfig.java           # Swagger/OpenAPI configuration
├── controller/
│   ├── AuthController.java          # /api/auth endpoints
│   ├── UserController.java          # /api/users endpoints
│   ├── PostController.java          # /api/posts endpoints
│   ├── SubscriptionController.java  # /api/subscriptions endpoints
│   ├── NotificationController.java  # /api/notifications endpoints
│   ├── AdminController.java         # /api/admin endpoints
│   ├── ReportController.java        # /api/reports endpoints
│   └── FileController.java          # /api/files endpoints
├── service/
│   ├── AuthService.java             # Authentication business logic
│   ├── UserService.java             # User management logic
│   ├── PostService.java             # Post CRUD + likes/comments
│   ├── SubscriptionService.java     # Follow/unfollow logic
│   ├── NotificationService.java     # Notification management
│   ├── AdminService.java            # Admin operations
│   └── FileStorageService.java      # File upload handling
├── repository/
│   ├── UserRepository.java          # User data access
│   ├── PostRepository.java          # Post data access
│   ├── CommentRepository.java       # Comment data access
│   ├── LikeRepository.java          # Like data access
│   ├── SubscriptionRepository.java  # Subscription data access
│   ├── NotificationRepository.java  # Notification data access
│   └── ReportRepository.java        # Report data access
├── entity/
│   ├── User.java                    # User table mapping
│   ├── Post.java                    # Post table mapping
│   ├── Comment.java                 # Comment table mapping
│   ├── Like.java                    # Like table mapping
│   ├── Subscription.java            # Subscription table mapping
│   ├── Notification.java            # Notification table mapping
│   └── Report.java                  # Report table mapping
├── dto/
│   ├── auth/                        # Authentication DTOs
│   ├── user/                        # User DTOs
│   ├── post/                        # Post DTOs
│   ├── admin/                       # Admin DTOs
│   └── notification/                # Notification DTOs
├── security/
│   ├── JwtTokenProvider.java        # JWT creation/validation
│   ├── JwtAuthenticationFilter.java # JWT filter for requests
│   ├── JwtAuthenticationEntryPoint.java  # Unauthorized handler
│   ├── UserPrincipal.java           # Custom UserDetails
│   └── CustomUserDetailsService.java # Loads user for auth
├── exception/
│   ├── GlobalExceptionHandler.java  # @ControllerAdvice
│   ├── UserNotFoundException.java   # 404 exception
│   ├── ForbiddenException.java      # 403 exception
│   └── ... (12 custom exceptions)
└── enums/
    └── Role.java                    # USER, ADMIN enum
```

---

## 3. Request Lifecycle

### Complete Request Flow

```
CLIENT                    SPRING BOOT APPLICATION                    DATABASE
  │                                                                      │
  │  1. HTTP Request                                                     │
  │  POST /api/posts                                                     │
  │  Headers: Authorization: Bearer <JWT>                                │
  │  Body: { "content": "Hello World" }                                  │
  │─────────────────────────────────────────►                            │
  │                                          │                           │
  │                    ┌─────────────────────▼─────────────────────┐     │
  │                    │         DISPATCHER SERVLET                │     │
  │                    │   (Front Controller - routes requests)    │     │
  │                    └─────────────────────┬─────────────────────┘     │
  │                                          │                           │
  │                    ┌─────────────────────▼─────────────────────┐     │
  │                    │          FILTER CHAIN                     │     │
  │                    │  ┌─────────────────────────────────────┐  │     │
  │                    │  │ 2. JwtAuthenticationFilter          │  │     │
  │                    │  │    • Extracts JWT from header       │  │     │
  │                    │  │    • Validates token signature      │  │     │
  │                    │  │    • Loads user from database       │  │     │
  │                    │  │    • Sets SecurityContext           │  │     │
  │                    │  └─────────────────────────────────────┘  │     │
  │                    │  ┌─────────────────────────────────────┐  │     │
  │                    │  │ 3. Security Filters                 │  │     │
  │                    │  │    • Check authentication           │  │     │
  │                    │  │    • Check authorization (roles)    │  │     │
  │                    │  └─────────────────────────────────────┘  │     │
  │                    └─────────────────────┬─────────────────────┘     │
  │                                          │                           │
  │                    ┌─────────────────────▼─────────────────────┐     │
  │                    │           CONTROLLER                      │     │
  │                    │  @PostMapping("/api/posts")               │     │
  │                    │  4. Receives request                      │     │
  │                    │  5. @Valid validates PostRequest          │     │
  │                    │  6. Calls service layer                   │     │
  │                    └─────────────────────┬─────────────────────┘     │
  │                                          │                           │
  │                    ┌─────────────────────▼─────────────────────┐     │
  │                    │            SERVICE                        │     │
  │                    │  @Transactional                           │     │
  │                    │  7. Business logic                        │     │
  │                    │     • Check if user is banned             │     │
  │                    │     • Create Post entity                  │     │
  │                    │     • Call repository                     │     │
  │                    └─────────────────────┬─────────────────────┘     │
  │                                          │                           │
  │                    ┌─────────────────────▼─────────────────────┐     │
  │                    │           REPOSITORY                      │     │
  │                    │  8. postRepository.save(post)             │     │
  │                    │     JPA/Hibernate converts to SQL         │     │
  │                    └─────────────────────┬─────────────────────┘     │
  │                                          │                           │
  │                                          │  9. INSERT INTO posts     │
  │                                          │─────────────────────────► │
  │                                          │                           │
  │                                          │  10. Return saved entity  │
  │                                          │◄───────────────────────── │
  │                                          │                           │
  │                    ┌─────────────────────▼─────────────────────┐     │
  │                    │      RESPONSE CONVERSION                  │     │
  │                    │  11. Entity → DTO (PostResponse)          │     │
  │                    │  12. DTO → JSON (Jackson)                 │     │
  │                    └─────────────────────┬─────────────────────┘     │
  │                                          │                           │
  │  13. HTTP Response                       │                           │
  │  200 OK                                  │                           │
  │  { "id": 1, "content": "Hello World" }   │                           │
  │◄─────────────────────────────────────────                            │
  │                                                                      │
```

### Code Flow Example: Creating a Post

```java
// 1. CLIENT sends POST /api/posts with JWT token

// 2. JwtAuthenticationFilter.java
@Override
protected void doFilterInternal(HttpServletRequest request, ...) {
    String jwt = getJwtFromRequest(request);      // Extract "Bearer <token>"
    if (tokenProvider.validateToken(jwt)) {        // Validate signature
        Long userId = tokenProvider.getUserIdFromToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserById(userId);
        // Set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);       // Continue to controller
}

// 3. PostController.java
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,    // 4. Validate input
            Authentication authentication) {            // 5. Injected by Spring

        PostResponse post = postService.createPost(request, authentication);
        return ResponseEntity.ok(post);                 // 6. Return response
    }
}

// 7. PostService.java
@Service
@Transactional
public class PostService {

    public PostResponse createPost(PostRequest request, Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new UserNotFoundException(...));

        if (user.getBanned()) {
            throw new BannedUserException(...);         // Business rule
        }

        Post post = new Post();
        post.setContent(request.getContent());
        post.setUser(user);

        Post saved = postRepository.save(post);         // 8. Save to DB
        return mapToResponse(saved);                    // 9. Convert to DTO
    }
}

// 10. PostRepository.java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // save() method inherited from JpaRepository
    // JPA converts to: INSERT INTO posts (content, user_id, ...) VALUES (?, ?, ...)
}
```

---

## 4. Security & Authentication

### JWT (JSON Web Token) Authentication

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           JWT STRUCTURE                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA0...                   │
│  ├──────────────────┘ └──────────────────────────────┘                  │
│  │                    │                                                  │
│  │  HEADER            │  PAYLOAD                      SIGNATURE         │
│  │  (Base64)          │  (Base64)                     (Base64)          │
│  │                    │                                                  │
│  │  {                 │  {                                               │
│  │    "alg": "HS256", │    "sub": "1",        ← User ID                 │
│  │    "typ": "JWT"    │    "iat": 1704067200, ← Issued at               │
│  │  }                 │    "exp": 1704153600  ← Expiration              │
│  │                    │  }                                               │
│  │                    │                                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

### Authentication Flow

```
┌──────────┐                    ┌──────────────────┐                ┌────────┐
│  CLIENT  │                    │   SPRING BOOT    │                │   DB   │
└────┬─────┘                    └────────┬─────────┘                └───┬────┘
     │                                   │                              │
     │  1. POST /api/auth/login          │                              │
     │  { "username": "john",            │                              │
     │    "password": "secret" }         │                              │
     │──────────────────────────────────►│                              │
     │                                   │                              │
     │                                   │  2. Find user by username    │
     │                                   │─────────────────────────────►│
     │                                   │                              │
     │                                   │  3. Return user              │
     │                                   │◄─────────────────────────────│
     │                                   │                              │
     │                                   │  4. Verify password          │
     │                                   │  BCrypt.matches(password,    │
     │                                   │                 user.hash)   │
     │                                   │                              │
     │                                   │  5. Generate JWT             │
     │                                   │  JwtTokenProvider            │
     │                                   │    .generateToken(user)      │
     │                                   │                              │
     │  6. Return JWT                    │                              │
     │  { "token": "eyJhbG...",          │                              │
     │    "userId": 1,                   │                              │
     │    "username": "john" }           │                              │
     │◄──────────────────────────────────│                              │
     │                                   │                              │
     │                                   │                              │
     │  7. POST /api/posts               │                              │
     │  Authorization: Bearer eyJhbG...  │                              │
     │──────────────────────────────────►│                              │
     │                                   │                              │
     │                                   │  8. JwtAuthFilter validates  │
     │                                   │     token and sets           │
     │                                   │     SecurityContext          │
     │                                   │                              │
     │  9. 200 OK (Authenticated)        │                              │
     │◄──────────────────────────────────│                              │
     │                                   │                              │
```

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)           // Disable CSRF (REST API)
            .cors(cors -> cors.configurationSource(...))     // Enable CORS
            .sessionManagement(session ->
                session.sessionCreationPolicy(STATELESS))    // No sessions (JWT)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Public
                .requestMatchers(GET, "/api/posts/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")  // Admin only
                .anyRequest().authenticated()                // All others need auth
            );

        // Add JWT filter before username/password filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### Role-Based Access Control

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      ENDPOINT ACCESS MATRIX                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ENDPOINT                      │ ANONYMOUS │ USER │ ADMIN                │
│  ─────────────────────────────────────────────────────────────────────  │
│  POST /api/auth/register       │    ✓      │  ✓   │   ✓                  │
│  POST /api/auth/login          │    ✓      │  ✓   │   ✓                  │
│  GET  /api/posts               │    ✓      │  ✓   │   ✓                  │
│  GET  /api/posts/{id}          │    ✓      │  ✓   │   ✓                  │
│  GET  /api/files/{filename}    │    ✓      │  ✓   │   ✓                  │
│  ─────────────────────────────────────────────────────────────────────  │
│  POST /api/posts               │    ✗      │  ✓   │   ✓                  │
│  PUT  /api/posts/{id}          │    ✗      │  ✓*  │   ✓                  │
│  DELETE /api/posts/{id}        │    ✗      │  ✓*  │   ✓                  │
│  POST /api/posts/{id}/like     │    ✗      │  ✓   │   ✓                  │
│  POST /api/files/upload        │    ✗      │  ✓   │   ✓                  │
│  ─────────────────────────────────────────────────────────────────────  │
│  GET  /api/admin/users         │    ✗      │  ✗   │   ✓                  │
│  POST /api/admin/users/{id}/ban│    ✗      │  ✗   │   ✓                  │
│  DELETE /api/admin/posts/{id}  │    ✗      │  ✗   │   ✓                  │
│                                                                          │
│  * = Only owner can modify their own resources                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Password Hashing

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      BCRYPT PASSWORD HASHING                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  REGISTRATION:                                                           │
│  ┌────────────────┐     BCrypt.encode()     ┌────────────────────────┐  │
│  │ "myPassword123"│ ───────────────────────►│ "$2a$10$N9qo8uLO..."    │  │
│  │ (plain text)   │                         │ (60-char hash)         │  │
│  └────────────────┘                         └────────────────────────┘  │
│                                                       │                  │
│                                                       ▼                  │
│                                              Stored in Database          │
│                                                                          │
│  LOGIN:                                                                  │
│  ┌────────────────┐                         ┌────────────────────────┐  │
│  │ "myPassword123"│                         │ "$2a$10$N9qo8uLO..."    │  │
│  │ (user input)   │                         │ (from database)        │  │
│  └───────┬────────┘                         └───────────┬────────────┘  │
│          │                                              │                │
│          └──────────────────┬───────────────────────────┘                │
│                             │                                            │
│                             ▼                                            │
│                   BCrypt.matches(input, hash)                            │
│                             │                                            │
│                    ┌────────┴────────┐                                   │
│                    ▼                 ▼                                   │
│                  TRUE              FALSE                                 │
│               (Login OK)        (Invalid password)                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Database Layer

### Entity-Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      DATABASE SCHEMA                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────┐         ┌──────────────┐         ┌──────────────┐     │
│  │    USERS     │         │    POSTS     │         │   COMMENTS   │     │
│  ├──────────────┤         ├──────────────┤         ├──────────────┤     │
│  │ id (PK)      │◄───────┐│ id (PK)      │◄───────┐│ id (PK)      │     │
│  │ username     │        ││ content      │        ││ content      │     │
│  │ email        │        ││ media_url    │        ││ post_id (FK) │────►│
│  │ password     │        ││ media_type   │        ││ user_id (FK) │────►│
│  │ display_name │        ││ user_id (FK) │────────┘│ created_at   │     │
│  │ bio          │        ││ created_at   │         └──────────────┘     │
│  │ role         │        ││ updated_at   │                              │
│  │ banned       │        │└──────────────┘         ┌──────────────┐     │
│  │ created_at   │        │                         │    LIKES     │     │
│  │ updated_at   │        │                         ├──────────────┤     │
│  └──────────────┘        │                         │ id (PK)      │     │
│         │                │                         │ post_id (FK) │────►│
│         │                │                         │ user_id (FK) │────►│
│         │                │                         │ created_at   │     │
│         │                │                         └──────────────┘     │
│         │                │                                              │
│  ┌──────▼───────┐        │                         ┌──────────────┐     │
│  │ SUBSCRIPTIONS│        │                         │   REPORTS    │     │
│  ├──────────────┤        │                         ├──────────────┤     │
│  │ id (PK)      │        │                         │ id (PK)      │     │
│  │ subscriber_id│────────┤                         │ reason       │     │
│  │ subscribed_to│────────┤                         │ resolved     │     │
│  │ created_at   │        │                         │ reporter_id  │────►│
│  └──────────────┘        │                         │ reported_user│────►│
│                          │                         │ created_at   │     │
│  ┌──────────────┐        │                         └──────────────┘     │
│  │NOTIFICATIONS │        │                                              │
│  ├──────────────┤        │                                              │
│  │ id (PK)      │        │                                              │
│  │ message      │        │                                              │
│  │ type         │        │                                              │
│  │ read         │        │                                              │
│  │ user_id (FK) │────────┤                                              │
│  │ related_post │────────┘                                              │
│  │ related_user │────────►                                              │
│  │ created_at   │                                                       │
│  └──────────────┘                                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### JPA Entity Mapping

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private Long id;

    @Column(unique = true, nullable = false)  // Unique constraint
    private String username;

    @Enumerated(EnumType.STRING)  // Store enum as string
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)  // One user → many posts
    private List<Post> posts = new ArrayList<>();

    @CreationTimestamp  // Auto-set on create
    private LocalDateTime createdAt;
}

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)  // Many posts → one user
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();
}
```

### JPA Relationship Types

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      JPA RELATIONSHIPS                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  @OneToMany / @ManyToOne (Most common)                                  │
│  ───────────────────────────────────────                                │
│  User ──────< Post                                                       │
│  "One user has many posts, each post belongs to one user"               │
│                                                                          │
│  // In User.java                        // In Post.java                  │
│  @OneToMany(mappedBy = "user")          @ManyToOne                       │
│  List<Post> posts;                      @JoinColumn(name = "user_id")    │
│                                         User user;                       │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  @ManyToMany (Not used in this project, but concept)                    │
│  ───────────────────────────────────────                                │
│  Student >────< Course                                                   │
│  "Many students take many courses"                                       │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  CASCADE Types:                                                          │
│  • CascadeType.ALL - All operations cascade to children                  │
│  • CascadeType.PERSIST - Save parent → save children                     │
│  • CascadeType.REMOVE - Delete parent → delete children                  │
│                                                                          │
│  orphanRemoval = true:                                                   │
│  • Removes child when removed from parent's collection                   │
│                                                                          │
│  FetchType:                                                              │
│  • LAZY (default for collections) - Load on access                       │
│  • EAGER - Load immediately with parent                                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Repository Pattern

```java
// JpaRepository provides CRUD operations automatically
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA generates SQL from method name
    Optional<User> findByUsername(String username);
    // → SELECT * FROM users WHERE username = ?

    Optional<User> findByEmail(String email);
    // → SELECT * FROM users WHERE email = ?

    Boolean existsByUsername(String username);
    // → SELECT COUNT(*) > 0 FROM users WHERE username = ?

    List<User> findByUsernameContainingIgnoreCase(String query);
    // → SELECT * FROM users WHERE LOWER(username) LIKE LOWER('%query%')

    // Custom JPQL query
    @Query("SELECT u FROM User u WHERE u.banned = false")
    List<User> findAllActiveUsers();
}

// Inherited methods from JpaRepository:
// - save(entity)      → INSERT or UPDATE
// - findById(id)      → SELECT by primary key
// - findAll()         → SELECT all
// - delete(entity)    → DELETE
// - count()           → COUNT(*)
```

---

## 6. REST API Design

### REST Principles

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      REST API CONVENTIONS                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  HTTP Method │ CRUD Operation │ Example                                  │
│  ────────────────────────────────────────────────────────────────────── │
│  GET         │ Read           │ GET /api/posts        (list all)        │
│              │                │ GET /api/posts/1      (get one)         │
│  POST        │ Create         │ POST /api/posts       (create new)      │
│  PUT         │ Update         │ PUT /api/posts/1      (update)          │
│  DELETE      │ Delete         │ DELETE /api/posts/1   (delete)          │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  HTTP Status Codes:                                                      │
│  • 200 OK           - Success                                            │
│  • 201 Created      - Resource created                                   │
│  • 400 Bad Request  - Invalid input                                      │
│  • 401 Unauthorized - Not authenticated                                  │
│  • 403 Forbidden    - Not authorized                                     │
│  • 404 Not Found    - Resource doesn't exist                             │
│  • 500 Internal Error - Server error                                     │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  Nested Resources:                                                       │
│  • GET /api/posts/1/comments    - Comments for post 1                    │
│  • POST /api/posts/1/comments   - Add comment to post 1                  │
│  • POST /api/posts/1/like       - Like post 1                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Controller Annotations

```java
@RestController  // Combines @Controller + @ResponseBody
@RequestMapping("/api/posts")  // Base path for all endpoints
@CrossOrigin(origins = "http://localhost:4200")  // CORS
public class PostController {

    @GetMapping  // GET /api/posts
    public ResponseEntity<List<PostResponse>> getAllPosts() { }

    @GetMapping("/{id}")  // GET /api/posts/1
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) { }

    @PostMapping  // POST /api/posts
    public ResponseEntity<PostResponse> createPost(
        @Valid @RequestBody PostRequest request,  // Validate JSON body
        Authentication authentication) { }        // Current user

    @PutMapping("/{id}")  // PUT /api/posts/1
    public ResponseEntity<PostResponse> updatePost(
        @PathVariable Long id,
        @Valid @RequestBody PostRequest request,
        Authentication authentication) { }

    @DeleteMapping("/{id}")  // DELETE /api/posts/1
    public ResponseEntity<?> deletePost(
        @PathVariable Long id,
        Authentication authentication) { }

    @PostMapping("/{id}/like")  // POST /api/posts/1/like
    public ResponseEntity<?> toggleLike(
        @PathVariable Long id,
        Authentication authentication) { }
}
```

### API Endpoints Summary

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      API ENDPOINTS (35 total)                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  AUTHENTICATION (/api/auth)                                              │
│  ├── POST /register     Create new user account                         │
│  └── POST /login        Authenticate and get JWT                        │
│                                                                          │
│  USERS (/api/users)                                                      │
│  ├── GET  /{id}         Get user profile                                │
│  ├── PUT  /{id}         Update own profile                              │
│  └── GET  /search       Search users by name                            │
│                                                                          │
│  POSTS (/api/posts)                                                      │
│  ├── POST /             Create new post                                  │
│  ├── GET  /{id}         Get single post                                  │
│  ├── PUT  /{id}         Update own post                                  │
│  ├── DELETE /{id}       Delete own post                                  │
│  ├── GET  /user/{id}    Get user's posts                                │
│  ├── GET  /feed         Get personalized feed                           │
│  ├── POST /{id}/like    Toggle like on post                             │
│  ├── POST /{id}/comments  Add comment                                    │
│  ├── GET  /{id}/comments  Get comments                                   │
│  └── DELETE /comments/{id} Delete own comment                           │
│                                                                          │
│  SUBSCRIPTIONS (/api/subscriptions)                                      │
│  ├── POST   /{userId}       Subscribe to user                           │
│  ├── DELETE /{userId}       Unsubscribe                                 │
│  └── GET    /{userId}/status Check subscription                         │
│                                                                          │
│  NOTIFICATIONS (/api/notifications)                                      │
│  ├── GET  /              Get all notifications                          │
│  ├── GET  /unread        Get unread notifications                       │
│  ├── GET  /unread-count  Get unread count                               │
│  ├── PUT  /{id}/read     Mark as read                                   │
│  ├── PUT  /read-all      Mark all as read                               │
│  └── DELETE /{id}        Delete notification                            │
│                                                                          │
│  FILES (/api/files)                                                      │
│  ├── POST   /upload      Upload file                                    │
│  ├── GET    /{filename}  Download file                                  │
│  └── DELETE /{filename}  Delete file (admin)                            │
│                                                                          │
│  ADMIN (/api/admin) - Requires ADMIN role                               │
│  ├── GET    /users           List all users                             │
│  ├── POST   /users/{id}/ban  Ban user                                   │
│  ├── POST   /users/{id}/unban Unban user                                │
│  ├── DELETE /users/{id}      Delete user                                │
│  ├── DELETE /posts/{id}      Delete any post                            │
│  ├── GET    /reports         View all reports                           │
│  └── DELETE /reports/{id}    Dismiss report                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Service Layer

### Service Layer Responsibilities

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  PURPOSE:                                                                │
│  • Contains business logic (rules, validations, calculations)            │
│  • Coordinates between controller and repository                         │
│  • Manages transactions                                                  │
│  • Converts between Entity and DTO                                       │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  @Service                                                                │
│  @Transactional  ← All methods run in database transaction              │
│  public class PostService {                                              │
│                                                                          │
│      // Dependencies injected by Spring                                  │
│      private final PostRepository postRepository;                        │
│      private final UserRepository userRepository;                        │
│      private final NotificationService notificationService;              │
│                                                                          │
│      // Business logic example                                           │
│      public PostResponse createPost(PostRequest req, Authentication a) { │
│                                                                          │
│          // 1. Get current user                                          │
│          User user = getCurrentUser(authentication);                     │
│                                                                          │
│          // 2. Business rule: banned users cannot post                   │
│          if (user.getBanned()) {                                         │
│              throw new BannedUserException("Cannot create posts");       │
│          }                                                               │
│                                                                          │
│          // 3. Create entity from request                                │
│          Post post = new Post();                                         │
│          post.setContent(request.getContent());                          │
│          post.setUser(user);                                             │
│                                                                          │
│          // 4. Save to database                                          │
│          Post saved = postRepository.save(post);                         │
│                                                                          │
│          // 5. Convert entity to DTO and return                          │
│          return mapToResponse(saved);                                    │
│      }                                                                   │
│  }                                                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Transaction Management

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      @TRANSACTIONAL                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  WHAT IT DOES:                                                           │
│  • Wraps method in database transaction                                  │
│  • COMMIT if method succeeds                                             │
│  • ROLLBACK if exception is thrown                                       │
│                                                                          │
│  EXAMPLE:                                                                │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ @Transactional                                                     │  │
│  │ public void transferMoney(Long from, Long to, BigDecimal amount) { │  │
│  │     Account fromAcc = accountRepo.findById(from);                  │  │
│  │     Account toAcc = accountRepo.findById(to);                      │  │
│  │                                                                    │  │
│  │     fromAcc.setBalance(fromAcc.getBalance().subtract(amount));     │  │
│  │     toAcc.setBalance(toAcc.getBalance().add(amount));              │  │
│  │                                                                    │  │
│  │     accountRepo.save(fromAcc);  // Not committed yet               │  │
│  │     accountRepo.save(toAcc);    // Not committed yet               │  │
│  │                                                                    │  │
│  │     // If exception here → both saves ROLLBACK                     │  │
│  │     // If success → both saves COMMIT together                     │  │
│  │ }                                                                  │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  WITHOUT @Transactional:                                                 │
│  • Each save() commits immediately                                       │
│  • If error after first save, data is inconsistent                       │
│                                                                          │
│  WITH @Transactional:                                                    │
│  • All operations in single transaction                                  │
│  • All-or-nothing (atomic)                                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Exception Handling

### Global Exception Handler

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      EXCEPTION HANDLING FLOW                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Controller                 Service                  Repository         │
│       │                         │                         │              │
│       │  calls                  │                         │              │
│       │────────────────────────►│  calls                  │              │
│       │                         │────────────────────────►│              │
│       │                         │                         │              │
│       │                         │     User not found      │              │
│       │                         │◄────────────────────────│              │
│       │                         │                         │              │
│       │   throws                │                         │              │
│       │   UserNotFoundException │                         │              │
│       │◄────────────────────────│                         │              │
│       │                         │                         │              │
│       │                                                                  │
│       ▼                                                                  │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │              GLOBAL EXCEPTION HANDLER                            │    │
│  │              @ControllerAdvice                                   │    │
│  │                                                                  │    │
│  │  @ExceptionHandler(UserNotFoundException.class)                  │    │
│  │  public ResponseEntity<ErrorResponse> handleUserNotFound(ex) {   │    │
│  │      return ResponseEntity                                       │    │
│  │          .status(HttpStatus.NOT_FOUND)  // 404                   │    │
│  │          .body(new ErrorResponse(                                │    │
│  │              "USER_NOT_FOUND",                                   │    │
│  │              ex.getMessage(),                                    │    │
│  │              LocalDateTime.now()                                 │    │
│  │          ));                                                     │    │
│  │  }                                                               │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│       │                                                                  │
│       ▼                                                                  │
│   HTTP Response:                                                         │
│   {                                                                      │
│     "status": 404,                                                       │
│     "error": "USER_NOT_FOUND",                                          │
│     "message": "User not found with id: 999",                           │
│     "timestamp": "2026-01-09T12:00:00",                                 │
│     "path": "/api/users/999"                                            │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Custom Exceptions

```java
// Base exception for 404 Not Found
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

// Usage in service:
User user = userRepository.findById(id)
    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

// All custom exceptions in this project:
//
// 404 NOT FOUND:
// - UserNotFoundException
// - PostNotFoundException
// - CommentNotFoundException
// - NotificationNotFoundException
// - ReportNotFoundException
//
// 400 BAD REQUEST:
// - UserAlreadyExistsException
// - EmailAlreadyExistsException
// - AlreadySubscribedException
// - NotSubscribedException
// - InvalidFileException
//
// 401 UNAUTHORIZED:
// - InvalidCredentialsException
// - UnauthorizedException
//
// 403 FORBIDDEN:
// - ForbiddenException
// - BannedUserException
```

---

## 9. Data Transfer Objects (DTOs)

### Why Use DTOs?

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      ENTITY vs DTO                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ENTITY (Database Model)          DTO (API Model)                        │
│  ┌────────────────────────┐       ┌────────────────────────┐            │
│  │ User                   │       │ UserProfileResponse    │            │
│  ├────────────────────────┤       ├────────────────────────┤            │
│  │ id                     │       │ id                     │            │
│  │ username               │       │ username               │            │
│  │ email                  │       │ displayName            │            │
│  │ password  ← SENSITIVE! │       │ bio                    │            │
│  │ displayName            │       │ postsCount             │ ← Computed │
│  │ bio                    │       │ subscribersCount       │ ← Computed │
│  │ role                   │       │ isSubscribed           │ ← Computed │
│  │ banned                 │       │ createdAt              │            │
│  │ createdAt              │       └────────────────────────┘            │
│  │ updatedAt              │                                             │
│  │ posts (List)           │       NO password!                          │
│  │ comments (List)        │       NO internal fields!                   │
│  │ likes (List)           │       Includes computed values!             │
│  └────────────────────────┘                                             │
│                                                                          │
│  BENEFITS OF DTOs:                                                       │
│  ✓ Hide sensitive data (password)                                       │
│  ✓ Control what data is exposed                                         │
│  ✓ Add computed fields (counts)                                         │
│  ✓ Different shape than database                                        │
│  ✓ API contract independent of DB schema                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### DTO Examples

```java
// REQUEST DTO - What client sends
public class PostRequest {
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    private String mediaUrl;
    private String mediaType;

    // Getters and setters
}

// RESPONSE DTO - What server returns
public class PostResponse {
    private Long id;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private Long userId;
    private String username;
    private String userDisplayName;
    private Integer likesCount;      // Computed from likes.size()
    private Integer commentsCount;   // Computed from comments.size()
    private Boolean isLiked;         // Computed: did current user like?
    private LocalDateTime createdAt;

    // Getters and setters
}

// Conversion in Service:
private PostResponse mapToResponse(Post post, Long currentUserId) {
    PostResponse response = new PostResponse();
    response.setId(post.getId());
    response.setContent(post.getContent());
    response.setUserId(post.getUser().getId());
    response.setUsername(post.getUser().getUsername());
    response.setLikesCount(post.getLikes().size());
    response.setCommentsCount(post.getComments().size());
    response.setIsLiked(
        post.getLikes().stream()
            .anyMatch(like -> like.getUser().getId().equals(currentUserId))
    );
    return response;
}
```

---

## 10. Dependency Injection

### How Spring DI Works

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      DEPENDENCY INJECTION                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  WITHOUT Dependency Injection (Bad):                                     │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ public class PostController {                                      │  │
│  │     private PostService postService = new PostService(            │  │
│  │         new PostRepository(...),   // Must create manually        │  │
│  │         new UserRepository(...),   // Hard to test                │  │
│  │         new NotificationService(...) // Tight coupling            │  │
│  │     );                                                             │  │
│  │ }                                                                  │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  WITH Dependency Injection (Good):                                       │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ @RestController                                                    │  │
│  │ public class PostController {                                      │  │
│  │                                                                    │  │
│  │     private final PostService postService;  // Just declare       │  │
│  │                                                                    │  │
│  │     // Constructor injection (recommended)                         │  │
│  │     public PostController(PostService postService) {               │  │
│  │         this.postService = postService; // Spring provides it     │  │
│  │     }                                                              │  │
│  │ }                                                                  │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  HOW SPRING CREATES BEANS:                                               │
│                                                                          │
│  1. Application starts                                                   │
│  2. Spring scans for @Component, @Service, @Repository, @Controller     │
│  3. Creates instances (beans) and stores in IoC Container                │
│  4. When creating a bean, injects its dependencies                       │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                    SPRING IoC CONTAINER                          │    │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐   │    │
│  │  │PostRepo    │ │UserRepo    │ │PostService │ │PostControll│   │    │
│  │  │(singleton) │ │(singleton) │ │(singleton) │ │(singleton) │   │    │
│  │  └─────┬──────┘ └─────┬──────┘ └─────┬──────┘ └─────┬──────┘   │    │
│  │        │              │              │              │          │    │
│  │        └──────────────┴──────────────┘              │          │    │
│  │                       │                             │          │    │
│  │                       └─────────────────────────────┘          │    │
│  │               (PostService injected into PostController)       │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Injection Types

```java
// 1. Constructor Injection (RECOMMENDED)
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Spring automatically injects when there's only one constructor
    public PostService(PostRepository postRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
}

// 2. Field Injection (works but not recommended)
@Service
public class PostService {
    @Autowired  // Spring injects directly into field
    private PostRepository postRepository;
}

// 3. Setter Injection (rarely used)
@Service
public class PostService {
    private PostRepository postRepository;

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
}

// WHY CONSTRUCTOR INJECTION IS BEST:
// ✓ Dependencies are required (not null)
// ✓ Fields can be final (immutable)
// ✓ Easy to test (pass mock in constructor)
// ✓ Clear dependencies in constructor
```

---

## 11. Configuration

### application.properties

```properties
# ═══════════════════════════════════════════════════════════════════════
# SERVER CONFIGURATION
# ═══════════════════════════════════════════════════════════════════════
server.port=8080  # HTTP port

# ═══════════════════════════════════════════════════════════════════════
# DATABASE CONFIGURATION
# ═══════════════════════════════════════════════════════════════════════
spring.datasource.url=jdbc:postgresql://localhost:5432/blog_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# ═══════════════════════════════════════════════════════════════════════
# JPA/HIBERNATE CONFIGURATION
# ═══════════════════════════════════════════════════════════════════════
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate  # Options: none, validate, update, create, create-drop
spring.jpa.show-sql=true                # Log SQL queries
spring.jpa.properties.hibernate.format_sql=true

# ═══════════════════════════════════════════════════════════════════════
# FLYWAY (Database Migrations)
# ═══════════════════════════════════════════════════════════════════════
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration

# ═══════════════════════════════════════════════════════════════════════
# FILE UPLOAD
# ═══════════════════════════════════════════════════════════════════════
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=./uploads
file.allowed-image-types=image/jpeg,image/png,image/gif,image/webp
file.allowed-video-types=video/mp4,video/webm,video/quicktime

# ═══════════════════════════════════════════════════════════════════════
# JWT CONFIGURATION
# ═══════════════════════════════════════════════════════════════════════
jwt.secret=your-256-bit-secret-key-here
jwt.expiration=86400000  # 24 hours in milliseconds
```

### Hibernate DDL-Auto Options

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      ddl-auto OPTIONS                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  none        │ Do nothing (production)                                   │
│  validate    │ Validate schema matches entities (production)             │
│  update      │ Update schema to match entities (development)             │
│  create      │ Drop and recreate schema on startup                       │
│  create-drop │ Create on startup, drop on shutdown (testing)             │
│                                                                          │
│  RECOMMENDED:                                                            │
│  • Development: update (or use Flyway)                                   │
│  • Production:  validate + Flyway migrations                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 12. Testing

### Test Types

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      TESTING PYRAMID                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                         ▲                                                │
│                        ╱ ╲                                               │
│                       ╱   ╲        E2E Tests                             │
│                      ╱     ╲       (Few, Slow)                           │
│                     ╱───────╲                                            │
│                    ╱         ╲                                           │
│                   ╱           ╲    Integration Tests                     │
│                  ╱             ╲   (Some, Medium)                        │
│                 ╱───────────────╲                                        │
│                ╱                 ╲                                       │
│               ╱                   ╲  Unit Tests                          │
│              ╱                     ╲ (Many, Fast)                        │
│             ╱───────────────────────╲                                    │
│                                                                          │
│  UNIT TESTS (100 tests in this project):                                 │
│  • Test single class in isolation                                        │
│  • Mock all dependencies                                                 │
│  • Fast execution                                                        │
│  • Example: PostServiceTest                                              │
│                                                                          │
│  INTEGRATION TESTS:                                                      │
│  • Test multiple components together                                     │
│  • Use real database (H2 in-memory)                                      │
│  • Test API endpoints with MockMvc                                       │
│  • Example: PostControllerTest                                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Unit Test Example

```java
@ExtendWith(MockitoExtension.class)  // Enable Mockito
class PostServiceTest {

    @Mock  // Create mock object
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks  // Inject mocks into this class
    private PostService postService;

    @Mock
    private Authentication authentication;

    @Test
    void createPost_Success() {
        // ARRANGE - Set up test data and mock behavior
        User user = new User();
        user.setId(1L);
        user.setBanned(false);

        UserPrincipal principal = new UserPrincipal(1L, "testuser", ...);

        when(authentication.getPrincipal()).thenReturn(principal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(1L);
            return post;
        });

        PostRequest request = new PostRequest();
        request.setContent("Test content");

        // ACT - Call the method being tested
        PostResponse response = postService.createPost(request, authentication);

        // ASSERT - Verify the result
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test content", response.getContent());

        // Verify mock interactions
        verify(postRepository).save(any(Post.class));
        verify(userRepository).findById(1L);
    }

    @Test
    void createPost_BannedUser_ThrowsException() {
        // ARRANGE
        User bannedUser = new User();
        bannedUser.setId(1L);
        bannedUser.setBanned(true);  // User is banned

        when(authentication.getPrincipal()).thenReturn(principal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(bannedUser));

        // ACT & ASSERT
        assertThrows(BannedUserException.class, () -> {
            postService.createPost(request, authentication);
        });

        // Verify save was never called
        verify(postRepository, never()).save(any(Post.class));
    }
}
```

### Test Annotations

```java
// JUnit 5 Annotations
@Test                    // Marks method as test
@BeforeEach             // Run before each test
@AfterEach              // Run after each test
@BeforeAll              // Run once before all tests (static)
@DisplayName("...")     // Custom test name
@Disabled               // Skip this test

// Mockito Annotations
@Mock                   // Create mock object
@InjectMocks           // Inject mocks into tested class
@Spy                    // Partial mock (real methods + can stub)

// Spring Boot Test Annotations
@SpringBootTest         // Full application context
@WebMvcTest            // Only web layer (controllers)
@DataJpaTest           // Only JPA layer (repositories)
@MockBean              // Spring-managed mock
```

---

## 13. Database Migrations

### Flyway

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      FLYWAY MIGRATIONS                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  WHAT IS FLYWAY?                                                         │
│  • Version control for database schema                                   │
│  • Tracks which migrations have been applied                             │
│  • Runs migrations automatically on application start                    │
│                                                                          │
│  MIGRATION FILES:                                                        │
│  src/main/resources/db/migration/                                        │
│  ├── V1__initial_schema.sql     ← Version 1                             │
│  ├── V2__add_notifications.sql  ← Version 2                             │
│  └── V3__add_indexes.sql        ← Version 3                             │
│                                                                          │
│  NAMING CONVENTION:                                                      │
│  V{version}__{description}.sql                                          │
│  │    │           │                                                      │
│  │    │           └── Description (underscores for spaces)              │
│  │    └── Version number (1, 2, 3...)                                   │
│  └── V = Versioned migration                                            │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  HOW IT WORKS:                                                           │
│                                                                          │
│  1. App starts                                                           │
│  2. Flyway checks 'flyway_schema_history' table                          │
│  3. Compares with migration files                                        │
│  4. Runs new migrations in order                                         │
│  5. Records in history table                                             │
│                                                                          │
│  flyway_schema_history:                                                  │
│  ┌─────────┬───────────────────────┬─────────────────────┐              │
│  │ version │ description           │ installed_on        │              │
│  ├─────────┼───────────────────────┼─────────────────────┤              │
│  │ 1       │ initial schema        │ 2026-01-09 10:00:00 │              │
│  └─────────┴───────────────────────┴─────────────────────┘              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Migration Example

```sql
-- V1__initial_schema.sql

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    bio VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    banned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Posts table with foreign key
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(5000) NOT NULL,
    media_url VARCHAR(500),
    media_type VARCHAR(50),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
```

---

## 14. File Upload

### File Upload Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      FILE UPLOAD FLOW                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  CLIENT                         SERVER                      FILE SYSTEM  │
│    │                               │                             │       │
│    │  1. POST /api/files/upload    │                             │       │
│    │  Content-Type: multipart/form-data                          │       │
│    │  [file binary data]           │                             │       │
│    │──────────────────────────────►│                             │       │
│    │                               │                             │       │
│    │                               │  2. Validate file           │       │
│    │                               │  • Check type (image/video) │       │
│    │                               │  • Check size (max 10MB)    │       │
│    │                               │  • Sanitize filename        │       │
│    │                               │                             │       │
│    │                               │  3. Generate unique name    │       │
│    │                               │  UUID + extension           │       │
│    │                               │  "a1b2c3d4.jpg"             │       │
│    │                               │                             │       │
│    │                               │  4. Save to disk            │       │
│    │                               │─────────────────────────────►│      │
│    │                               │                             │       │
│    │                               │       ./uploads/a1b2c3d4.jpg│       │
│    │                               │◄─────────────────────────────│      │
│    │                               │                             │       │
│    │  5. Return file URL           │                             │       │
│    │  {                            │                             │       │
│    │    "filename": "a1b2c3d4.jpg",│                             │       │
│    │    "fileUrl": "/api/files/...",                             │       │
│    │    "mediaType": "IMAGE"       │                             │       │
│    │  }                            │                             │       │
│    │◄──────────────────────────────│                             │       │
│    │                               │                             │       │
│    │  6. Create post with file URL │                             │       │
│    │  POST /api/posts              │                             │       │
│    │  {                            │                             │       │
│    │    "content": "My photo",     │                             │       │
│    │    "mediaUrl": "/api/files/a1b2c3d4.jpg",                   │       │
│    │    "mediaType": "IMAGE"       │                             │       │
│    │  }                            │                             │       │
│    │──────────────────────────────►│                             │       │
│    │                               │                             │       │
└─────────────────────────────────────────────────────────────────────────┘
```

### FileStorageService

```java
@Service
public class FileStorageService {

    private Path fileStorageLocation;  // ./uploads

    @PostConstruct  // Called after bean creation
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(this.fileStorageLocation);  // Create if not exists
    }

    public String storeFile(MultipartFile file) {
        // 1. Validate file
        validateFile(file);

        // 2. Generate unique filename
        String extension = getFileExtension(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 3. Save to disk
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, REPLACE_EXISTING);

        return uniqueFilename;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        String contentType = file.getContentType();
        if (!isImageType(contentType) && !isVideoType(contentType)) {
            throw new InvalidFileException("Invalid file type: " + contentType);
        }
    }

    private boolean isImageType(String contentType) {
        return List.of("image/jpeg", "image/png", "image/gif", "image/webp")
            .contains(contentType);
    }
}
```

---

## Quick Reference

### Common Interview Questions

1. **What is Spring Boot?**
   - Framework that simplifies Spring development with auto-configuration

2. **What is Dependency Injection?**
   - Design pattern where dependencies are provided (injected) rather than created

3. **What is JPA/Hibernate?**
   - JPA = specification for ORM, Hibernate = implementation

4. **What is @Transactional?**
   - Wraps method in database transaction, enables rollback on error

5. **How does JWT authentication work?**
   - Client sends credentials → Server returns signed token → Client sends token in header → Server validates and authorizes

6. **What is REST?**
   - Architectural style using HTTP methods (GET, POST, PUT, DELETE) for CRUD

7. **What is @ControllerAdvice?**
   - Global exception handler for all controllers

8. **What is the difference between Entity and DTO?**
   - Entity = database model, DTO = API model (controls what's exposed)

---

## Running the Application

```bash
# Start PostgreSQL database
docker compose up -d

# Run the application
cd backend
mvn spring-boot:run

# Run tests
mvn test

# Access Swagger UI
open http://localhost:8080/swagger-ui.html

# Access API
curl http://localhost:8080/api/posts
```

---

*Generated for 01Blog Backend Audit - January 2026*
