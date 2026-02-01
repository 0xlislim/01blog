# 01blog Backend Documentation - Spring Boot

## Table of Contents

1. [Introduction to Spring Boot](#1-introduction-to-spring-boot)
2. [Project Architecture](#2-project-architecture)
3. [Spring Boot Core Concepts](#3-spring-boot-core-concepts)
4. [Dependency Injection & IoC Container](#4-dependency-injection--ioc-container)
5. [Layered Architecture Pattern](#5-layered-architecture-pattern)
6. [JPA & Hibernate (ORM)](#6-jpa--hibernate-orm)
7. [Spring Data JPA Repositories](#7-spring-data-jpa-repositories)
8. [REST Controllers](#8-rest-controllers)
9. [Service Layer](#9-service-layer)
10. [Spring Security & JWT Authentication](#10-spring-security--jwt-authentication)
11. [Exception Handling](#11-exception-handling)
12. [Data Transfer Objects (DTOs)](#12-data-transfer-objects-dtos)
13. [Validation](#13-validation)
14. [Database Configuration & Migrations](#14-database-configuration--migrations)
15. [File Upload Handling](#15-file-upload-handling)
16. [CORS Configuration](#16-cors-configuration)
17. [Testing](#17-testing)
18. [API Documentation (OpenAPI/Swagger)](#18-api-documentation-openapiswagger)
19. [Build & Deployment](#19-build--deployment)
20. [API Reference](#20-api-reference)

---

## 1. Introduction to Spring Boot

### 1.1 What is Spring Boot?

Spring Boot is an open-source Java framework that simplifies the development of production-ready Spring applications. It provides:

- **Auto-configuration**: Automatically configures Spring and third-party libraries
- **Standalone**: Creates stand-alone applications that can run independently
- **Opinionated defaults**: Provides sensible defaults to get started quickly
- **No code generation**: No need for XML configuration
- **Embedded server**: Includes embedded Tomcat, Jetty, or Undertow

### 1.2 Spring Boot vs Traditional Spring

| Feature | Traditional Spring | Spring Boot |
|---------|-------------------|-------------|
| Configuration | XML files | Java annotations + properties |
| Server | External (Tomcat WAR) | Embedded (JAR) |
| Setup time | Hours | Minutes |
| Dependency management | Manual | Starter POMs |
| Production-ready | Manual setup | Built-in (actuator) |

### 1.3 Project Tech Stack

```
┌─────────────────────────────────────────────────────┐
│                   01blog Backend                     │
├─────────────────────────────────────────────────────┤
│  Framework:      Spring Boot 3.2.1                  │
│  Language:       Java 17                            │
│  Build Tool:     Maven                              │
│  Database:       PostgreSQL                         │
│  ORM:            Hibernate (JPA)                    │
│  Security:       Spring Security + JWT              │
│  Migrations:     Flyway                             │
│  Documentation:  SpringDoc OpenAPI 2.3.0            │
│  Testing:        JUnit 5 + Mockito                  │
└─────────────────────────────────────────────────────┘
```

### 1.4 Key Dependencies (pom.xml)

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Lombok (reduces boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- API Documentation -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

---

## 2. Project Architecture

### 2.1 Directory Structure

```
backend/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/blog/backend/
│   │   │   ├── BackendApplication.java    # Entry point
│   │   │   │
│   │   │   ├── config/                    # Configuration classes
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   │
│   │   │   ├── security/                  # Security components
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── UserPrincipal.java
│   │   │   │
│   │   │   ├── entity/                    # JPA Entities (Database models)
│   │   │   │   ├── User.java
│   │   │   │   ├── Post.java
│   │   │   │   ├── Comment.java
│   │   │   │   ├── Like.java
│   │   │   │   ├── Subscription.java
│   │   │   │   ├── Report.java
│   │   │   │   ├── Notification.java
│   │   │   │   └── Role.java (enum)
│   │   │   │
│   │   │   ├── repository/                # Data Access Layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── PostRepository.java
│   │   │   │   ├── CommentRepository.java
│   │   │   │   ├── LikeRepository.java
│   │   │   │   ├── SubscriptionRepository.java
│   │   │   │   ├── NotificationRepository.java
│   │   │   │   └── ReportRepository.java
│   │   │   │
│   │   │   ├── service/                   # Business Logic Layer
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── PostService.java
│   │   │   │   ├── SubscriptionService.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── AdminService.java
│   │   │   │   └── FileStorageService.java
│   │   │   │
│   │   │   ├── controller/                # REST API Layer
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── PostController.java
│   │   │   │   ├── SubscriptionController.java
│   │   │   │   ├── NotificationController.java
│   │   │   │   ├── FileController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   └── ReportController.java
│   │   │   │
│   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   ├── auth/
│   │   │   │   ├── post/
│   │   │   │   ├── user/
│   │   │   │   ├── admin/
│   │   │   │   ├── notification/
│   │   │   │   └── error/
│   │   │   │
│   │   │   └── exception/                 # Exception Handling
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── [Custom Exceptions]
│   │   │
│   │   └── resources/
│   │       ├── application.properties     # Configuration
│   │       └── db/migration/              # Flyway migrations
│   │           └── V1__initial_schema.sql
│   │
│   └── test/                              # Unit & Integration tests
│       └── java/com/blog/backend/
```

### 2.2 Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                        CLIENT (Angular)                          │
└───────────────────────────────┬──────────────────────────────────┘
                                │ HTTP Requests
                                ▼
┌──────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT APPLICATION                       │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    SECURITY LAYER                            │ │
│  │  JwtAuthenticationFilter → JwtTokenProvider                  │ │
│  │  CustomUserDetailsService → UserPrincipal                    │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                │                                  │
│                                ▼                                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                   CONTROLLER LAYER                           │ │
│  │  AuthController, UserController, PostController, etc.        │ │
│  │  Handles HTTP requests, validates input, returns responses   │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                │                                  │
│                                ▼                                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    SERVICE LAYER                             │ │
│  │  AuthService, UserService, PostService, etc.                 │ │
│  │  Contains business logic, transaction management             │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                │                                  │
│                                ▼                                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                  REPOSITORY LAYER                            │ │
│  │  UserRepository, PostRepository, etc.                        │ │
│  │  Spring Data JPA interfaces for database operations          │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                │                                  │
│                                ▼                                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    ENTITY LAYER                              │ │
│  │  User, Post, Comment, Like, Subscription, etc.               │ │
│  │  JPA entities mapped to database tables                      │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                   │
└───────────────────────────────┬──────────────────────────────────┘
                                │ JDBC/Hibernate
                                ▼
┌──────────────────────────────────────────────────────────────────┐
│                      PostgreSQL DATABASE                          │
└──────────────────────────────────────────────────────────────────┘
```

---

## 3. Spring Boot Core Concepts

### 3.1 Application Entry Point

Every Spring Boot application starts with a main class annotated with `@SpringBootApplication`:

```java
// BackendApplication.java
@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
```

**@SpringBootApplication** combines three annotations:
- `@Configuration` - Marks class as configuration source
- `@EnableAutoConfiguration` - Enables auto-configuration
- `@ComponentScan` - Scans for components in package and sub-packages

### 3.2 Key Spring Annotations

| Annotation | Purpose | Layer |
|------------|---------|-------|
| `@Component` | Generic Spring-managed component | Any |
| `@Service` | Business logic component | Service |
| `@Repository` | Data access component | Repository |
| `@Controller` | Web controller (returns views) | Controller |
| `@RestController` | REST API controller (returns JSON) | Controller |
| `@Configuration` | Configuration class | Config |
| `@Bean` | Defines a bean in configuration | Config |
| `@Autowired` | Injects dependencies | Any |

### 3.3 Bean Lifecycle

```
1. Spring scans for @Component, @Service, @Repository, @Controller
                            │
                            ▼
2. Creates bean instances (constructor injection)
                            │
                            ▼
3. Injects dependencies (@Autowired)
                            │
                            ▼
4. Calls @PostConstruct methods
                            │
                            ▼
5. Bean is ready to use
                            │
                            ▼
6. Application shutdown → @PreDestroy
```

### 3.4 Configuration Properties

`application.properties` configures the application:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/blog_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# JWT
app.jwt.secret=mySecretKey
app.jwt.expiration=86400000

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

---

## 4. Dependency Injection & IoC Container

### 4.1 What is Dependency Injection?

Dependency Injection (DI) is a design pattern where objects receive their dependencies from an external source rather than creating them internally.

**Without DI (tight coupling):**
```java
public class PostService {
    private PostRepository repository = new PostRepository(); // Creates dependency
}
```

**With DI (loose coupling):**
```java
@Service
public class PostService {
    private final PostRepository repository;

    @Autowired  // Spring injects the dependency
    public PostService(PostRepository repository) {
        this.repository = repository;
    }
}
```

### 4.2 Inversion of Control (IoC)

The IoC Container (Spring Container) manages:
- Object creation
- Dependency injection
- Object lifecycle
- Configuration

```
Traditional:                          IoC:
┌───────────┐                        ┌───────────┐
│  Object A │                        │   Spring  │
│  creates  │──────────────►         │ Container │
│  Object B │                        │  creates  │
└───────────┘                        │   both    │
                                     │  injects  │
                                     └─────┬─────┘
                                           │
                                     ┌─────┴─────┐
                                     ▼           ▼
                                 Object A    Object B
```

### 4.3 Types of Injection

**Constructor Injection (Recommended):**
```java
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Spring injects both repositories
    public PostService(PostRepository postRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
}
```

**Field Injection:**
```java
@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
}
```

**Setter Injection:**
```java
@Service
public class PostService {
    private PostRepository postRepository;

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
}
```

### 4.4 Bean Scopes

| Scope | Description |
|-------|-------------|
| `singleton` (default) | One instance per Spring container |
| `prototype` | New instance each time requested |
| `request` | One instance per HTTP request |
| `session` | One instance per HTTP session |

```java
@Service
@Scope("prototype")  // New instance each time
public class MyService { }
```

---

## 5. Layered Architecture Pattern

### 5.1 Three-Layer Architecture

```
┌─────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                      │
│              (Controllers)                           │
│  • Handles HTTP requests/responses                  │
│  • Input validation                                 │
│  • DTO conversion                                   │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│              BUSINESS LAYER                          │
│              (Services)                              │
│  • Business logic                                   │
│  • Transaction management                           │
│  • Orchestrates data access                         │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│              DATA ACCESS LAYER                       │
│              (Repositories)                          │
│  • Database operations                              │
│  • Query execution                                  │
│  • Entity mapping                                   │
└─────────────────────────────────────────────────────┘
```

### 5.2 Request Flow Example

```
POST /api/posts (Create Post)
        │
        ▼
┌─────────────────────────────────────────────────────┐
│ PostController.createPost()                          │
│ • Receives PostRequest DTO                          │
│ • Validates input (@Valid)                          │
│ • Calls service                                     │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│ PostService.createPost()                             │
│ • Gets current user from Authentication             │
│ • Creates Post entity                               │
│ • Notifies subscribers                              │
│ • Calls repository to save                          │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│ PostRepository.save()                                │
│ • Hibernate generates SQL                           │
│ • Inserts into posts table                          │
│ • Returns saved entity with ID                      │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│ Response: PostResponse DTO                           │
│ • Entity converted to DTO                           │
│ • Returns 201 Created                               │
└─────────────────────────────────────────────────────┘
```

### 5.3 Why Layered Architecture?

| Benefit | Description |
|---------|-------------|
| Separation of Concerns | Each layer has specific responsibility |
| Testability | Each layer can be tested independently |
| Maintainability | Changes in one layer don't affect others |
| Reusability | Services can be reused by multiple controllers |
| Security | Business logic protected from direct access |

---

## 6. JPA & Hibernate (ORM)

### 6.1 What is JPA?

**Java Persistence API (JPA)** is a specification for Object-Relational Mapping (ORM). It defines how Java objects (entities) map to database tables.

**Hibernate** is the most popular JPA implementation.

```
┌──────────────┐         ┌──────────────┐
│  Java Object │  ◄────► │ Database Row │
│  (Entity)    │   JPA   │   (Table)    │
└──────────────┘         └──────────────┘
```

### 6.2 Entity Definition

```java
@Entity
@Table(name = "users")
@Data  // Lombok: generates getters, setters, etc.
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Boolean banned = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 6.3 JPA Annotations Explained

| Annotation | Purpose |
|------------|---------|
| `@Entity` | Marks class as JPA entity (database table) |
| `@Table` | Specifies table name |
| `@Id` | Primary key field |
| `@GeneratedValue` | Auto-generate primary key |
| `@Column` | Column configuration (nullable, length, unique) |
| `@Enumerated` | How to persist enums (STRING or ORDINAL) |
| `@PrePersist` | Callback before INSERT |
| `@PreUpdate` | Callback before UPDATE |
| `@Transient` | Field not persisted to database |

### 6.4 Entity Relationships

**One-to-Many (User has many Posts):**
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
}

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

**Many-to-Many (through join table - Subscriptions):**
```java
@Entity
@Table(name = "subscriptions",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"subscriber_id", "subscribed_to_id"}))
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscribed_to_id", nullable = false)
    private User subscribedTo;
}
```

### 6.5 Fetch Types

| Type | Behavior | Use Case |
|------|----------|----------|
| `LAZY` | Load on demand | Default for collections |
| `EAGER` | Load immediately | Small, always-needed data |

```java
@ManyToOne(fetch = FetchType.LAZY)  // Load user only when accessed
private User user;

@OneToMany(fetch = FetchType.EAGER)  // Always load with parent
private List<Comment> comments;
```

### 6.6 Cascade Types

| Type | Behavior |
|------|----------|
| `ALL` | All operations cascade |
| `PERSIST` | Save cascades |
| `MERGE` | Update cascades |
| `REMOVE` | Delete cascades |
| `REFRESH` | Refresh cascades |
| `DETACH` | Detach cascades |

```java
@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Comment> comments;
// Deleting a post also deletes all its comments
```

### 6.7 Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│     User     │       │     Post     │       │   Comment    │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id           │◄──┐   │ id           │◄──┐   │ id           │
│ username     │   │   │ content      │   │   │ content      │
│ email        │   │   │ mediaUrl     │   │   │ post_id      │──►
│ password     │   │   │ mediaType    │   │   │ user_id      │──►
│ displayName  │   │   │ user_id      │──►│   │ createdAt    │
│ bio          │   │   │ createdAt    │   │   └──────────────┘
│ role         │   │   │ updatedAt    │   │
│ banned       │   │   └──────────────┘   │   ┌──────────────┐
│ createdAt    │   │                      │   │     Like     │
│ updatedAt    │   │   ┌──────────────┐   │   ├──────────────┤
└──────────────┘   │   │ Subscription │   │   │ id           │
        ▲          │   ├──────────────┤   │   │ post_id      │──►
        │          │   │ id           │   │   │ user_id      │──►
        │          │   │ subscriber_id│──►│   │ createdAt    │
        │          │   │ subscribedTo │──►│   └──────────────┘
        │          │   │ createdAt    │   │
        │          │   └──────────────┘   │   ┌──────────────┐
        │          │                      │   │    Report    │
        │          │   ┌──────────────┐   │   ├──────────────┤
        │          │   │ Notification │   │   │ id           │
        │          │   ├──────────────┤   │   │ reason       │
        │          │   │ id           │   │   │ resolved     │
        │          │   │ message      │   │   │ reporter_id  │──►
        │          │   │ type         │   │   │ reportedUser │──►
        │          │   │ read         │   │   │ createdAt    │
        │          └───│ user_id      │   │   └──────────────┘
        │              │ relatedPost  │───┘
        └──────────────│ relatedUser  │
                       │ createdAt    │
                       └──────────────┘
```

---

## 7. Spring Data JPA Repositories

### 7.1 What is Spring Data JPA?

Spring Data JPA provides a repository abstraction over JPA. It automatically implements common database operations.

### 7.2 Repository Interface

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository provides: save, findById, findAll, delete, etc.

    // Custom query methods (Spring generates implementation)
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByBanned(Boolean banned);
}
```

### 7.3 Query Method Naming Convention

Spring Data JPA derives queries from method names:

| Method Name | Generated SQL |
|-------------|---------------|
| `findByUsername(String)` | `WHERE username = ?` |
| `findByEmailAndPassword(String, String)` | `WHERE email = ? AND password = ?` |
| `findByRoleOrBanned(Role, Boolean)` | `WHERE role = ? OR banned = ?` |
| `findByUsernameContaining(String)` | `WHERE username LIKE %?%` |
| `findByCreatedAtAfter(Date)` | `WHERE created_at > ?` |
| `findByAgeGreaterThan(int)` | `WHERE age > ?` |
| `countByRole(Role)` | `SELECT COUNT(*) WHERE role = ?` |
| `deleteByUsername(String)` | `DELETE WHERE username = ?` |

### 7.4 Custom Queries with @Query

```java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // JPQL Query (uses entity names)
    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
    List<Post> findByUserIdInOrderByCreatedAtDesc(@Param("userIds") List<Long> userIds);

    // Native SQL Query
    @Query(value = "SELECT * FROM posts WHERE user_id = :userId", nativeQuery = true)
    List<Post> findPostsByUserId(@Param("userId") Long userId);

    // Modifying Query
    @Modifying
    @Query("UPDATE Post p SET p.content = :content WHERE p.id = :id")
    int updateContent(@Param("id") Long id, @Param("content") String content);
}
```

### 7.5 JpaRepository Methods

| Method | Description |
|--------|-------------|
| `save(entity)` | Insert or update entity |
| `saveAll(entities)` | Save multiple entities |
| `findById(id)` | Find by primary key |
| `findAll()` | Find all entities |
| `findAllById(ids)` | Find by multiple IDs |
| `count()` | Count all entities |
| `deleteById(id)` | Delete by ID |
| `delete(entity)` | Delete entity |
| `deleteAll()` | Delete all entities |
| `existsById(id)` | Check if exists |

### 7.6 Pagination and Sorting

```java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Pagination
    Page<Post> findByUserId(Long userId, Pageable pageable);

    // Sorting
    List<Post> findByUserId(Long userId, Sort sort);
}

// Usage in service
Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
Page<Post> posts = postRepository.findByUserId(userId, pageable);
```

---

## 8. REST Controllers

### 8.1 What is a REST Controller?

REST Controllers handle HTTP requests and return JSON responses. They map URLs to Java methods.

### 8.2 Controller Structure

```java
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            Authentication authentication) {
        PostResponse response = postService.createPost(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId,
            Authentication authentication) {
        PostResponse response = postService.getPost(postId, authentication);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request,
            Authentication authentication) {
        PostResponse response = postService.updatePost(postId, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication) {
        postService.deletePost(postId, authentication);
        return ResponseEntity.noContent().build();
    }
}
```

### 8.3 Controller Annotations

| Annotation | Purpose |
|------------|---------|
| `@RestController` | Combines `@Controller` + `@ResponseBody` |
| `@RequestMapping` | Base URL path for controller |
| `@GetMapping` | HTTP GET endpoint |
| `@PostMapping` | HTTP POST endpoint |
| `@PutMapping` | HTTP PUT endpoint |
| `@DeleteMapping` | HTTP DELETE endpoint |
| `@PatchMapping` | HTTP PATCH endpoint |
| `@PathVariable` | Extract value from URL path |
| `@RequestParam` | Extract query parameter |
| `@RequestBody` | Deserialize request body to object |
| `@Valid` | Trigger validation on request body |
| `@CrossOrigin` | Enable CORS for controller |

### 8.4 HTTP Methods & Status Codes

| Method | Purpose | Success Code |
|--------|---------|--------------|
| GET | Retrieve resource | 200 OK |
| POST | Create resource | 201 Created |
| PUT | Update resource | 200 OK |
| DELETE | Delete resource | 204 No Content |
| PATCH | Partial update | 200 OK |

### 8.5 ResponseEntity Usage

```java
// 200 OK with body
return ResponseEntity.ok(data);

// 201 Created with body
return ResponseEntity.status(HttpStatus.CREATED).body(data);

// 204 No Content
return ResponseEntity.noContent().build();

// 400 Bad Request
return ResponseEntity.badRequest().body(error);

// 404 Not Found
return ResponseEntity.notFound().build();

// Custom status
return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
```

### 8.6 Request/Response Flow

```
HTTP Request: POST /api/posts
Headers: Content-Type: application/json
         Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Body: {"content": "Hello World!", "mediaUrl": null}
                │
                ▼
┌───────────────────────────────────────────────────────────────┐
│ JwtAuthenticationFilter                                        │
│ • Extracts JWT from Authorization header                      │
│ • Validates token                                             │
│ • Sets SecurityContext with UserPrincipal                     │
└───────────────────────────────────────────────────────────────┘
                │
                ▼
┌───────────────────────────────────────────────────────────────┐
│ PostController.createPost()                                    │
│ • @RequestBody deserializes JSON to PostRequest               │
│ • @Valid triggers validation                                  │
│ • Authentication contains current user                        │
└───────────────────────────────────────────────────────────────┘
                │
                ▼
┌───────────────────────────────────────────────────────────────┐
│ PostService.createPost()                                       │
│ • Business logic execution                                    │
│ • Returns PostResponse                                        │
└───────────────────────────────────────────────────────────────┘
                │
                ▼
HTTP Response: 201 Created
Body: {"id": 1, "content": "Hello World!", ...}
```

---

## 9. Service Layer

### 9.1 Service Purpose

Services contain business logic and orchestrate data access. They:
- Implement business rules
- Manage transactions
- Transform data between entities and DTOs
- Call multiple repositories
- Handle cross-cutting concerns

### 9.2 Service Example

```java
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       LikeRepository likeRepository,
                       CommentRepository commentRepository,
                       SubscriptionRepository subscriptionRepository,
                       NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PostResponse createPost(PostRequest request, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Post post = new Post();
        post.setContent(request.getContent());
        post.setMediaUrl(request.getMediaUrl());
        post.setMediaType(request.getMediaType());
        post.setUser(user);

        Post savedPost = postRepository.save(post);

        // Notify subscribers
        notificationService.notifyNewPost(savedPost);

        return mapToResponse(savedPost, userPrincipal.getId());
    }

    public PostResponse getPost(Long postId, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        Long currentUserId = getCurrentUserId(authentication);
        return mapToResponse(post, currentUserId);
    }

    @Transactional
    public PostResponse toggleLike(Long postId, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Like> existingLike = likeRepository
                .findByPostIdAndUserId(postId, userPrincipal.getId());

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);

            // Notify post author
            if (!post.getUser().getId().equals(userPrincipal.getId())) {
                notificationService.notifyNewLike(post, user);
            }
        }

        return mapToResponse(post, userPrincipal.getId());
    }

    private PostResponse mapToResponse(Post post, Long currentUserId) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setMediaUrl(post.getMediaUrl());
        response.setMediaType(post.getMediaType());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setUserId(post.getUser().getId());
        response.setUsername(post.getUser().getUsername());
        response.setUserDisplayName(post.getUser().getDisplayName());
        response.setLikeCount(likeRepository.countByPostId(post.getId()));
        response.setCommentCount(commentRepository.countByPostId(post.getId()));
        response.setLikedByCurrentUser(
            currentUserId != null &&
            likeRepository.existsByPostIdAndUserId(post.getId(), currentUserId)
        );
        return response;
    }
}
```

### 9.3 @Transactional Annotation

```java
@Transactional
public PostResponse createPost(PostRequest request, Authentication auth) {
    // All database operations in this method are in ONE transaction
    // If any operation fails, ALL changes are rolled back
}

@Transactional(readOnly = true)
public List<PostResponse> getFeed(Authentication auth) {
    // Optimization for read-only operations
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void logAction() {
    // Always creates a new transaction
}
```

### 9.4 Transaction Propagation

| Type | Behavior |
|------|----------|
| `REQUIRED` (default) | Join existing or create new |
| `REQUIRES_NEW` | Always create new transaction |
| `SUPPORTS` | Join if exists, else non-transactional |
| `NOT_SUPPORTED` | Run non-transactional |
| `MANDATORY` | Must have existing transaction |
| `NEVER` | Must not have existing transaction |

---

## 10. Spring Security & JWT Authentication

### 10.1 Security Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        HTTP Request                              │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│              JwtAuthenticationFilter                             │
│  • Extracts JWT from Authorization header                       │
│  • Validates token using JwtTokenProvider                       │
│  • Loads user details via CustomUserDetailsService              │
│  • Sets Authentication in SecurityContext                       │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│              SecurityConfig Authorization Rules                  │
│  • Check if endpoint requires authentication                    │
│  • Check if user has required role                              │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
                Authorized              Unauthorized
                    │                       │
                    ▼                       ▼
              Controller           JwtAuthenticationEntryPoint
                                   (401 Unauthorized)
```

### 10.2 Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (not needed for stateless JWT)
            .csrf(csrf -> csrf.disable())

            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Handle unauthorized requests
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

            // Stateless session (no server-side session)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/files/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Admin-only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // All other endpoints require authentication
                .anyRequest().authenticated())

            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### 10.3 JWT Token Provider

```java
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // Generate signing key from secret
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .claim("username", userPrincipal.getUsername())
                .claim("role", userPrincipal.getAuthorities().iterator().next().getAuthority())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 10.4 JWT Authentication Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 10.5 User Principal

```java
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean banned;

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        UserPrincipal principal = new UserPrincipal();
        principal.id = user.getId();
        principal.username = user.getUsername();
        principal.email = user.getEmail();
        principal.password = user.getPassword();
        principal.authorities = authorities;
        principal.banned = user.getBanned();
        return principal;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !banned;  // Banned users are locked
    }

    @Override
    public boolean isEnabled() {
        return !banned;
    }

    // ... other UserDetails methods
}
```

### 10.6 Authentication Flow

```
┌─────────────┐                                          ┌─────────────┐
│   Client    │                                          │   Server    │
└──────┬──────┘                                          └──────┬──────┘
       │                                                        │
       │  1. POST /api/auth/login                              │
       │     {username: "john", password: "secret"}            │
       │───────────────────────────────────────────────────────►
       │                                                        │
       │                                           2. Validate credentials
       │                                           3. Generate JWT token
       │                                                        │
       │  4. Response: {accessToken: "eyJ...", userId: 1, ...} │
       │◄───────────────────────────────────────────────────────
       │                                                        │
       │  5. Store token in localStorage                       │
       │                                                        │
       │  6. GET /api/posts/feed                               │
       │     Authorization: Bearer eyJ...                       │
       │───────────────────────────────────────────────────────►
       │                                                        │
       │                                           7. JwtAuthenticationFilter
       │                                              validates token
       │                                           8. Sets SecurityContext
       │                                           9. Controller executes
       │                                                        │
       │  10. Response: [{post1}, {post2}, ...]                │
       │◄───────────────────────────────────────────────────────
```

### 10.7 Method-Level Security

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }
}
```

---

## 11. Exception Handling

### 11.1 Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle specific exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            "USER_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(
            PostNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            "POST_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            "FORBIDDEN",
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ValidationErrorResponse error = new ValidationErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed",
            LocalDateTime.now(),
            request.getRequestURI(),
            fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### 11.2 Custom Exceptions

```java
// 404 Not Found
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}

// 400 Bad Request
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message) {
        super(message);
    }
}

// 401 Unauthorized
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

// 403 Forbidden
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}

public class BannedUserException extends RuntimeException {
    public BannedUserException(String message) {
        super(message);
    }
}
```

### 11.3 Error Response DTOs

```java
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private String path;
}

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> fieldErrors;
}
```

### 11.4 Exception Hierarchy

```
RuntimeException
├── UserNotFoundException (404)
├── PostNotFoundException (404)
├── CommentNotFoundException (404)
├── NotificationNotFoundException (404)
├── ReportNotFoundException (404)
├── UserAlreadyExistsException (400)
├── AlreadySubscribedException (400)
├── NotSubscribedException (400)
├── InvalidFileException (400)
├── UnauthorizedException (401)
├── ForbiddenException (403)
└── BannedUserException (403)
```

---

## 12. Data Transfer Objects (DTOs)

### 12.1 Why DTOs?

DTOs (Data Transfer Objects) separate the API contract from internal entities:

| Benefit | Description |
|---------|-------------|
| **Security** | Hide sensitive fields (password, internal IDs) |
| **Flexibility** | Change entity without affecting API |
| **Validation** | Apply different validation rules |
| **Optimization** | Return only needed fields |
| **Documentation** | Clear API contracts |

### 12.2 Entity vs DTO

```
Entity (User.java)                    DTO (UserProfileResponse.java)
┌─────────────────────┐              ┌─────────────────────┐
│ id                  │              │ id                  │
│ username            │──────────────│ username            │
│ email               │      X       │ displayName         │
│ password            │──────────────│ bio                 │
│ displayName         │              │ postsCount          │
│ bio                 │              │ subscribersCount    │
│ role                │              │ subscriptionsCount  │
│ banned              │              │ isSubscribed        │
│ createdAt           │              │ createdAt           │
│ updatedAt           │              └─────────────────────┘
│ posts (List)        │
│ comments (List)     │              Sensitive fields hidden!
│ likes (List)        │              Computed fields added!
└─────────────────────┘
```

### 12.3 Request DTOs

```java
// Login Request
@Data
public class LoginRequest {
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}

// Register Request
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 100, message = "Display name must be under 100 characters")
    private String displayName;

    @Size(max = 500, message = "Bio must be under 500 characters")
    private String bio;
}

// Post Request
@Data
public class PostRequest {
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must be under 5000 characters")
    private String content;

    @Size(max = 500, message = "Media URL must be under 500 characters")
    private String mediaUrl;

    @Size(max = 50, message = "Media type must be under 50 characters")
    private String mediaType;
}
```

### 12.4 Response DTOs

```java
// JWT Response
@Data
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String role;
}

// Post Response
@Data
public class PostResponse {
    private Long id;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
    private String userDisplayName;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;
}

// User Profile Response
@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String displayName;
    private String bio;
    private int postsCount;
    private int subscribersCount;
    private int subscriptionsCount;
    private boolean isSubscribed;
    private LocalDateTime createdAt;
}
```

### 12.5 DTO Mapping Pattern

```java
// Manual mapping in service
private PostResponse mapToResponse(Post post, Long currentUserId) {
    PostResponse response = new PostResponse();
    response.setId(post.getId());
    response.setContent(post.getContent());
    response.setMediaUrl(post.getMediaUrl());
    response.setMediaType(post.getMediaType());
    response.setCreatedAt(post.getCreatedAt());
    response.setUpdatedAt(post.getUpdatedAt());
    response.setUserId(post.getUser().getId());
    response.setUsername(post.getUser().getUsername());
    response.setUserDisplayName(post.getUser().getDisplayName());

    // Computed fields
    response.setLikeCount(likeRepository.countByPostId(post.getId()));
    response.setCommentCount(commentRepository.countByPostId(post.getId()));
    response.setLikedByCurrentUser(
        currentUserId != null &&
        likeRepository.existsByPostIdAndUserId(post.getId(), currentUserId)
    );

    return response;
}
```

---

## 13. Validation

### 13.1 Bean Validation (JSR-380)

Spring Boot uses Jakarta Bean Validation for input validation.

### 13.2 Common Validation Annotations

| Annotation | Purpose |
|------------|---------|
| `@NotNull` | Value cannot be null |
| `@NotBlank` | String not null, not empty, not whitespace |
| `@NotEmpty` | Collection/String not null and not empty |
| `@Size(min, max)` | Size constraints |
| `@Min(value)` | Minimum numeric value |
| `@Max(value)` | Maximum numeric value |
| `@Email` | Valid email format |
| `@Pattern(regexp)` | Matches regex pattern |
| `@Positive` | Positive number |
| `@PositiveOrZero` | Zero or positive |
| `@Past` | Date in the past |
| `@Future` | Date in the future |

### 13.3 Validation in DTOs

```java
@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
             message = "Username can only contain letters, numbers, underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be 6-100 characters")
    private String password;
}
```

### 13.4 Triggering Validation

```java
@PostMapping("/register")
public ResponseEntity<JwtResponse> register(
        @Valid @RequestBody RegisterRequest request) {  // @Valid triggers validation
    return ResponseEntity.ok(authService.register(request));
}
```

### 13.5 Validation Error Response

When validation fails, `MethodArgumentNotValidException` is thrown and caught by GlobalExceptionHandler:

```json
{
    "errorCode": "VALIDATION_ERROR",
    "message": "Validation failed",
    "timestamp": "2024-01-15T10:30:00",
    "path": "/api/auth/register",
    "fieldErrors": {
        "username": "Username must be 3-50 characters",
        "email": "Invalid email format",
        "password": "Password is required"
    }
}
```

### 13.6 Entity Validation

```java
@Entity
@Table(name = "users")
public class User {

    @Column(nullable = false, unique = true, length = 50)
    @Size(min = 3, max = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @Email
    private String email;

    @Column(nullable = false)
    @Size(min = 6)
    private String password;
}
```

---

## 14. Database Configuration & Migrations

### 14.1 Application Properties

```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/blog_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Flyway Migration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

### 14.2 DDL Auto Options

| Value | Behavior |
|-------|----------|
| `none` | No schema changes |
| `validate` | Validate schema matches entities |
| `update` | Update schema (don't drop) |
| `create` | Create schema, drop previous |
| `create-drop` | Create, drop on shutdown |

**Production**: Use `validate` with Flyway migrations.

### 14.3 Flyway Database Migrations

Flyway manages database schema changes through versioned SQL scripts.

**Migration File**: `src/main/resources/db/migration/V1__initial_schema.sql`

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    bio VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    banned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Posts table
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    media_url VARCHAR(500),
    media_type VARCHAR(50),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Comments table
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Likes table
CREATE TABLE likes (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(post_id, user_id)
);

-- Subscriptions table
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    subscriber_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    subscribed_to_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(subscriber_id, subscribed_to_id)
);

-- Reports table
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    reason VARCHAR(1000) NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    reporter_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    related_post_id BIGINT REFERENCES posts(id) ON DELETE SET NULL,
    related_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_likes_post_id ON likes(post_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(read);
```

### 14.4 Flyway Naming Convention

```
V{version}__{description}.sql

Examples:
V1__initial_schema.sql
V2__add_user_avatar.sql
V3__add_post_tags.sql
```

### 14.5 Migration Benefits

| Benefit | Description |
|---------|-------------|
| **Version Control** | Schema changes tracked in Git |
| **Reproducibility** | Same schema across environments |
| **Rollback** | Can revert changes if needed |
| **Documentation** | SQL files document schema |
| **Team Collaboration** | Multiple developers, one schema |

---

## 15. File Upload Handling

### 15.1 Configuration

```properties
# Maximum file sizes
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# File storage path
app.file.upload-dir=./uploads

# Allowed media types
app.file.allowed-image-types=image/jpeg,image/png,image/gif,image/webp
app.file.allowed-video-types=video/mp4,video/webm,video/quicktime
```

### 15.2 File Storage Service

```java
@Service
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.allowed-image-types}")
    private String allowedImageTypes;

    @Value("${app.file.allowed-video-types}")
    private String allowedVideoTypes;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Validate file
        validateFile(file);

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "." + extension;

        try {
            // Security check for path traversal
            if (filename.contains("..")) {
                throw new InvalidFileException("Invalid filename: " + filename);
            }

            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation,
                       StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file", ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + filename, ex);
        }
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file", ex);
        }
    }

    public String getMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (allowedImageTypes.contains(contentType)) {
            return "IMAGE";
        } else if (allowedVideoTypes.contains(contentType)) {
            return "VIDEO";
        }
        throw new InvalidFileException("Unsupported file type: " + contentType);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        String contentType = file.getContentType();
        if (!allowedImageTypes.contains(contentType) &&
            !allowedVideoTypes.contains(contentType)) {
            throw new InvalidFileException("File type not allowed: " + contentType);
        }
    }
}
```

### 15.3 File Controller

```java
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        String filename = fileStorageService.storeFile(file);
        String mediaType = fileStorageService.getMediaType(file);

        Map<String, Object> response = new HashMap<>();
        response.put("filename", filename);
        response.put("fileUrl", "/api/files/" + filename);
        response.put("mediaType", mediaType);
        response.put("size", file.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadFileAsResource(filename);

        String contentType = fileStorageService.getContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{filename:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
        fileStorageService.deleteFile(filename);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 16. CORS Configuration

### 16.1 What is CORS?

**Cross-Origin Resource Sharing (CORS)** is a security mechanism that allows or restricts web pages from making requests to a different domain than the one serving the page.

```
Frontend (http://localhost:4200)
        │
        │ HTTP Request to different origin
        ▼
Backend (http://localhost:8080)
        │
        │ CORS headers determine if allowed
        ▼
Response with Access-Control headers
```

### 16.2 CORS Configuration

```java
@Configuration
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200"
        ));

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With"
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // How long browser caches preflight response
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
```

### 16.3 Controller-Level CORS

```java
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    // All endpoints in this controller allow CORS
}
```

---

## 17. Testing

### 17.1 Testing Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                     Unit Tests                                   │
│  • Test individual classes in isolation                         │
│  • Mock dependencies                                            │
│  • Fast execution                                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Integration Tests                              │
│  • Test multiple components together                            │
│  • Use real database (H2 in-memory)                             │
│  • Test Spring context                                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     End-to-End Tests                             │
│  • Test full application flow                                   │
│  • HTTP requests/responses                                      │
│  • Real database                                                │
└─────────────────────────────────────────────────────────────────┘
```

### 17.2 Service Unit Test

```java
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setDisplayName("Test User");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setContent("Test content");
        testPost.setUser(testUser);
    }

    @Test
    void createPost_Success() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setContent("New post content");

        UserPrincipal userPrincipal = UserPrincipal.create(testUser);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userPrincipal);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(likeRepository.countByPostId(1L)).thenReturn(0);

        // Act
        PostResponse response = postService.createPost(request, auth);

        // Assert
        assertNotNull(response);
        assertEquals(testPost.getId(), response.getId());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getPost_NotFound_ThrowsException() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PostNotFoundException.class, () ->
            postService.getPost(999L, null)
        );
    }

    @Test
    void toggleLike_AddLike_Success() {
        // Arrange
        UserPrincipal userPrincipal = UserPrincipal.create(testUser);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userPrincipal);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(likeRepository.findByPostIdAndUserId(1L, 1L))
            .thenReturn(Optional.empty());

        // Act
        PostResponse response = postService.toggleLike(1L, auth);

        // Assert
        verify(likeRepository).save(any(Like.class));
    }
}
```

### 17.3 Controller Integration Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createPost_Success() throws Exception {
        // Arrange
        PostRequest request = new PostRequest();
        request.setContent("Test content");

        PostResponse response = new PostResponse();
        response.setId(1L);
        response.setContent("Test content");

        when(postService.createPost(any(), any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    void createPost_Unauthorized() throws Exception {
        // No authentication
        PostRequest request = new PostRequest();
        request.setContent("Test content");

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
```

### 17.4 Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PostServiceTest

# Run with coverage report
./mvnw test jacoco:report

# Skip tests during build
./mvnw package -DskipTests
```

---

## 18. API Documentation (OpenAPI/Swagger)

### 18.1 OpenAPI Configuration

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("01Blog API")
                        .description("REST API for 01Blog social blogging platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support")
                                .email("support@01blog.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")));
    }
}
```

### 18.2 Accessing Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### 18.3 Controller Documentation

```java
@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post management endpoints")
public class PostController {

    @Operation(
        summary = "Create a new post",
        description = "Creates a new post with optional media attachment"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request,
            Authentication authentication) {
        // ...
    }
}
```

---

## 19. Build & Deployment

### 19.1 Maven Commands

```bash
# Clean and build
./mvnw clean install

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Package as JAR (skip tests)
./mvnw package -DskipTests

# Run packaged JAR
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 19.2 Application Profiles

```properties
# application.properties (default)
spring.profiles.active=dev

# application-dev.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog_db
spring.jpa.show-sql=true

# application-prod.properties
spring.datasource.url=${DATABASE_URL}
spring.jpa.show-sql=false
```

```bash
# Run with specific profile
java -jar app.jar --spring.profiles.active=prod
```

### 19.3 Environment Variables

```bash
# Set environment variables for production
export DATABASE_URL=jdbc:postgresql://prod-db:5432/blog
export DATABASE_USERNAME=prod_user
export DATABASE_PASSWORD=prod_secret
export JWT_SECRET=production_secret_key_here
```

### 19.4 Deployment Checklist

1. ✅ Set `spring.jpa.hibernate.ddl-auto=validate`
2. ✅ Configure production database credentials
3. ✅ Set strong JWT secret
4. ✅ Configure CORS for production domain
5. ✅ Enable HTTPS
6. ✅ Run Flyway migrations
7. ✅ Set appropriate logging levels
8. ✅ Configure file storage (S3 for production)

---

## 20. API Reference

### 20.1 Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login user | No |

### 20.2 Users

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/users/{id}` | Get user profile | Yes |
| PUT | `/api/users/{id}` | Update own profile | Yes |
| GET | `/api/users/search?query=` | Search users | Yes |

### 20.3 Posts

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/posts/feed` | Get feed | Yes |
| POST | `/api/posts` | Create post | Yes |
| GET | `/api/posts/{id}` | Get post | No |
| PUT | `/api/posts/{id}` | Update post | Yes (owner) |
| DELETE | `/api/posts/{id}` | Delete post | Yes (owner) |
| GET | `/api/posts/user/{userId}` | Get user posts | Yes |
| POST | `/api/posts/{id}/like` | Toggle like | Yes |
| GET | `/api/posts/{id}/comments` | Get comments | No |
| POST | `/api/posts/{id}/comments` | Add comment | Yes |
| DELETE | `/api/posts/comments/{id}` | Delete comment | Yes (author) |

### 20.4 Subscriptions

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/subscriptions/{userId}` | Subscribe | Yes |
| DELETE | `/api/subscriptions/{userId}` | Unsubscribe | Yes |
| GET | `/api/subscriptions/{userId}/status` | Check status | Yes |

### 20.5 Notifications

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/notifications` | Get all | Yes |
| GET | `/api/notifications/unread` | Get unread | Yes |
| GET | `/api/notifications/unread-count` | Get count | Yes |
| PUT | `/api/notifications/{id}/read` | Mark read | Yes |
| PUT | `/api/notifications/read-all` | Mark all read | Yes |
| DELETE | `/api/notifications/{id}` | Delete | Yes |

### 20.6 Files

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/files/upload` | Upload file | Yes |
| GET | `/api/files/{filename}` | Get file | No |
| DELETE | `/api/files/{filename}` | Delete file | Admin |

### 20.7 Reports

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/reports` | Create report | Yes |

### 20.8 Admin

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/users` | List users | Admin |
| POST | `/api/admin/users/{id}/ban` | Ban user | Admin |
| POST | `/api/admin/users/{id}/unban` | Unban user | Admin |
| DELETE | `/api/admin/users/{id}` | Delete user | Admin |
| DELETE | `/api/admin/posts/{id}` | Delete post | Admin |
| GET | `/api/admin/reports` | List reports | Admin |
| DELETE | `/api/admin/reports/{id}` | Dismiss report | Admin |

---

## Appendix A: Glossary

| Term | Definition |
|------|------------|
| **Bean** | Object managed by Spring container |
| **IoC** | Inversion of Control - container manages object lifecycle |
| **DI** | Dependency Injection - objects receive dependencies externally |
| **JPA** | Java Persistence API - ORM specification |
| **ORM** | Object-Relational Mapping - maps objects to database |
| **DTO** | Data Transfer Object - carries data between layers |
| **CORS** | Cross-Origin Resource Sharing - browser security mechanism |
| **JWT** | JSON Web Token - stateless authentication token |
| **CRUD** | Create, Read, Update, Delete operations |
| **REST** | Representational State Transfer - API architecture |
| **Transaction** | Unit of work that succeeds or fails atomically |
| **Repository** | Data access abstraction layer |
| **Service** | Business logic layer |
| **Controller** | HTTP request handling layer |
| **Entity** | JPA class mapped to database table |
| **Migration** | Version-controlled database schema change |

---

## Appendix B: Common Issues & Solutions

### Issue: CORS Error
```
Access-Control-Allow-Origin header missing
```
**Solution**: Ensure CORS is configured in SecurityConfig and controller.

### Issue: 401 Unauthorized
**Solution**: Check JWT token is valid and not expired. Verify Authorization header format: `Bearer <token>`.

### Issue: LazyInitializationException
```
could not initialize proxy - no Session
```
**Solution**: Use `@Transactional` on service method or change to `EAGER` fetch (not recommended).

### Issue: Circular Dependency
**Solution**: Use `@Lazy` annotation or constructor injection with setter.

### Issue: Database Connection Failed
**Solution**: Verify PostgreSQL is running and credentials match `application.properties`.

---

*Documentation generated for 01blog Backend - Spring Boot 3.2.1*
*Last updated: January 2026*
