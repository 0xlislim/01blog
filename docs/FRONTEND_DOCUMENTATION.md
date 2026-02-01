# 01blog Frontend Documentation

## Table of Contents

1. [Introduction](#1-introduction)
2. [Angular Core Concepts](#2-angular-core-concepts)
3. [Project Architecture](#3-project-architecture)
4. [Module System](#4-module-system)
5. [Routing & Navigation](#5-routing--navigation)
6. [Components](#6-components)
7. [Services & Dependency Injection](#7-services--dependency-injection)
8. [Guards & Route Protection](#8-guards--route-protection)
9. [HTTP Interceptors](#9-http-interceptors)
10. [Authentication Flow](#10-authentication-flow)
11. [Reactive Programming with RxJS](#11-reactive-programming-with-rxjs)
12. [Forms & Validation](#12-forms--validation)
13. [Angular Material Integration](#13-angular-material-integration)
14. [State Management](#14-state-management)
15. [Feature Modules Breakdown](#15-feature-modules-breakdown)
16. [Shared Module Pattern](#16-shared-module-pattern)
17. [Environment Configuration](#17-environment-configuration)
18. [Testing](#18-testing)
19. [Build & Deployment](#19-build--deployment)
20. [API Reference](#20-api-reference)

---

## 1. Introduction

### What is Angular?

Angular is a TypeScript-based open-source web application framework developed by Google. It provides a complete solution for building single-page applications (SPAs) with features like:

- **Component-based architecture**: UI is built from reusable, self-contained components
- **Two-way data binding**: Automatic synchronization between model and view
- **Dependency Injection**: Built-in DI system for managing service instances
- **Routing**: Client-side navigation without page reloads
- **Reactive Extensions**: RxJS integration for handling asynchronous operations
- **TypeScript**: Strongly typed programming for better tooling and error detection

### Project Overview

**01blog** is a social blogging platform built with Angular 17.3.0 that allows students to:
- Create and share posts with media attachments
- Subscribe to other users
- Like and comment on posts
- Receive notifications
- Manage their profile

**Tech Stack:**
- Angular 17.3.0
- Angular Material 17.3.10 (UI Components)
- RxJS 7.8.0 (Reactive Programming)
- TypeScript 5.4.2

---

## 2. Angular Core Concepts

### 2.1 Components

Components are the fundamental building blocks of Angular applications. Each component consists of:

```
Component = Template (HTML) + Class (TypeScript) + Styles (CSS/SCSS)
```

**Example from our project** (`post-card.component.ts`):

```typescript
@Component({
  selector: 'app-post-card',           // HTML tag name
  templateUrl: './post-card.component.html',  // Template file
  styleUrls: ['./post-card.component.scss']   // Styles file
})
export class PostCardComponent implements OnInit {
  @Input() post!: Post;                // Input property from parent
  @Output() postDeleted = new EventEmitter<number>();  // Output event to parent

  constructor(private postService: PostService) {}  // Dependency injection

  ngOnInit(): void {
    // Lifecycle hook - runs when component initializes
  }
}
```

**Key Decorators:**
- `@Component()` - Declares a class as a component
- `@Input()` - Receives data from parent component
- `@Output()` - Emits events to parent component

### 2.2 Modules

Modules (`NgModule`) organize the application into cohesive blocks of functionality:

```typescript
@NgModule({
  declarations: [ComponentA, ComponentB],  // Components, pipes, directives
  imports: [CommonModule, FormsModule],    // Other modules needed
  exports: [ComponentA],                   // What other modules can use
  providers: [MyService]                   // Services for this module
})
export class FeatureModule { }
```

### 2.3 Services

Services are classes that handle business logic, data fetching, and state management:

```typescript
@Injectable({
  providedIn: 'root'  // Singleton across entire application
})
export class AuthService {
  constructor(private http: HttpClient) {}
}
```

### 2.4 Dependency Injection (DI)

Angular's DI system automatically provides instances of services to components:

```typescript
// Angular creates and injects PostService instance automatically
constructor(
  private postService: PostService,
  private authService: AuthService
) {}
```

### 2.5 Lifecycle Hooks

Components have lifecycle hooks that allow you to act at specific moments:

| Hook | When it's called |
|------|-----------------|
| `ngOnInit` | After component initialization |
| `ngOnChanges` | When input properties change |
| `ngOnDestroy` | Before component is destroyed |
| `ngAfterViewInit` | After view is fully initialized |

---

## 3. Project Architecture

### 3.1 Directory Structure

```
frontend/src/app/
├── core/                    # Singleton services, guards, interceptors
│   ├── guards/             # Route protection
│   │   ├── auth.guard.ts
│   │   ├── admin.guard.ts
│   │   └── guest.guard.ts
│   ├── interceptors/       # HTTP request/response handling
│   │   ├── jwt.interceptor.ts
│   │   └── error.interceptor.ts
│   ├── models/             # TypeScript interfaces
│   │   ├── user.model.ts
│   │   ├── post.model.ts
│   │   └── ...
│   ├── services/           # Business logic services
│   │   ├── auth.service.ts
│   │   ├── post.service.ts
│   │   └── ...
│   └── core.module.ts
│
├── features/               # Feature modules (lazy-loaded)
│   ├── auth/              # Login/Register
│   ├── posts/             # Feed, post CRUD
│   ├── users/             # Profiles, search
│   ├── notifications/     # Notification management
│   └── admin/             # Admin dashboard
│
├── shared/                 # Reusable components, pipes, directives
│   ├── components/
│   │   ├── confirm-dialog/
│   │   ├── file-upload/
│   │   └── post-card-skeleton/
│   └── shared.module.ts
│
├── layout/                 # Main layout components
│   └── components/
│       └── main-layout/
│
├── app.module.ts          # Root module
├── app-routing.module.ts  # Root routing configuration
└── app.component.ts       # Root component
```

### 3.2 Architectural Patterns

**Core Module Pattern:**
- Contains singleton services that should only be instantiated once
- Imported only in AppModule
- Provides guards and interceptors

**Shared Module Pattern:**
- Contains reusable components, directives, and pipes
- Imported by feature modules that need shared functionality
- Re-exports commonly used Angular modules

**Feature Module Pattern:**
- Self-contained features with their own routes
- Lazy-loaded for better performance
- Independent and can be developed/tested in isolation

---

## 4. Module System

### 4.1 AppModule (Root Module)

`src/app/app.module.ts` - The root module that bootstraps the application:

```typescript
@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,      // Routing configuration
    CoreModule,            // Singleton services
    SharedModule,          // Shared components
    LayoutModule           // Layout components
  ],
  bootstrap: [AppComponent]  // Entry component
})
export class AppModule { }
```

### 4.2 CoreModule

`src/app/core/core.module.ts` - Contains application-wide singleton services:

```typescript
@NgModule({
  providers: [
    // HTTP Interceptors - order matters!
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ]
})
export class CoreModule { }
```

**Why Core Module?**
- Ensures services are instantiated only once (singleton pattern)
- Centralizes interceptor and guard registration
- Keeps AppModule clean

### 4.3 SharedModule

`src/app/shared/shared.module.ts` - Contains reusable components and re-exports common modules:

```typescript
@NgModule({
  declarations: [
    ConfirmDialogComponent,
    FileUploadComponent,
    PostCardSkeletonComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    // Angular Material modules...
  ],
  exports: [
    // Re-export for feature modules to use
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    // All Angular Material modules...
    // Shared components
    ConfirmDialogComponent,
    FileUploadComponent,
    PostCardSkeletonComponent
  ]
})
export class SharedModule { }
```

### 4.4 Feature Modules

Each feature has its own module with routing:

```typescript
// features/posts/posts.module.ts
@NgModule({
  declarations: [
    FeedComponent,
    PostCardComponent,
    CommentsSectionComponent,
    CreatePostDialogComponent,
    EditPostDialogComponent,
    UserPostsListComponent
  ],
  imports: [
    SharedModule,
    PostsRoutingModule
  ]
})
export class PostsModule { }
```

---

## 5. Routing & Navigation

### 5.1 Root Routing Configuration

`src/app/app-routing.module.ts`:

```typescript
const routes: Routes = [
  {
    path: 'auth',
    canActivate: [GuestGuard],  // Only non-authenticated users
    loadChildren: () => import('./features/auth/auth.module')
      .then(m => m.AuthModule)
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],   // Only authenticated users
    children: [
      { path: '', redirectTo: 'feed', pathMatch: 'full' },
      {
        path: 'feed',
        loadChildren: () => import('./features/posts/posts.module')
          .then(m => m.PostsModule)
      },
      {
        path: 'users',
        loadChildren: () => import('./features/users/users.module')
          .then(m => m.UsersModule)
      },
      {
        path: 'notifications',
        loadChildren: () => import('./features/notifications/notifications.module')
          .then(m => m.NotificationsModule)
      },
      {
        path: 'admin',
        canActivate: [AdminGuard],  // Only admins
        loadChildren: () => import('./features/admin/admin.module')
          .then(m => m.AdminModule)
      }
    ]
  },
  { path: '**', redirectTo: 'feed' }  // Catch-all redirect
];
```

### 5.2 Lazy Loading

**What is Lazy Loading?**
Lazy loading delays the loading of feature modules until they're needed, improving initial load time.

```typescript
loadChildren: () => import('./features/posts/posts.module')
  .then(m => m.PostsModule)
```

**Benefits:**
- Faster initial page load
- Code splitting into smaller bundles
- Users only download code they need

### 5.3 Route Parameters

Accessing route parameters in components:

```typescript
// Route: /users/:id
constructor(private route: ActivatedRoute) {}

ngOnInit() {
  // Get parameter reactively (responds to changes)
  this.route.paramMap.subscribe(params => {
    const userId = params.get('id');
    this.loadProfile(userId);
  });

  // Or get snapshot (one-time value)
  const userId = this.route.snapshot.paramMap.get('id');
}
```

### 5.4 Programmatic Navigation

```typescript
constructor(private router: Router) {}

// Navigate to route
this.router.navigate(['/users', userId]);

// Navigate with query parameters
this.router.navigate(['/users/search'], {
  queryParams: { query: 'john' }
});

// Navigate after action
this.authService.login(credentials).subscribe(() => {
  this.router.navigate(['/feed']);
});
```

### 5.5 Route Structure Summary

| Route | Module | Guard | Description |
|-------|--------|-------|-------------|
| `/auth/login` | AuthModule | GuestGuard | User login |
| `/auth/register` | AuthModule | GuestGuard | User registration |
| `/feed` | PostsModule | AuthGuard | Main feed |
| `/users/profile` | UsersModule | AuthGuard | Own profile |
| `/users/:id` | UsersModule | AuthGuard | Other user profile |
| `/users/search` | UsersModule | AuthGuard | Search users |
| `/notifications` | NotificationsModule | AuthGuard | Notification list |
| `/admin` | AdminModule | AdminGuard | Admin dashboard |

---

## 6. Components

### 6.1 Component Architecture

Each component follows this structure:

```
component-name/
├── component-name.component.ts      # Logic (TypeScript)
├── component-name.component.html    # Template (HTML)
├── component-name.component.scss    # Styles (SCSS)
└── component-name.component.spec.ts # Tests (optional)
```

### 6.2 Component Communication

**Parent to Child (Input):**

```typescript
// Parent template
<app-post-card [post]="currentPost" [showAuthor]="true"></app-post-card>

// Child component
@Input() post!: Post;
@Input() showAuthor: boolean = true;
```

**Child to Parent (Output):**

```typescript
// Child component
@Output() postDeleted = new EventEmitter<number>();

deletePost() {
  this.postDeleted.emit(this.post.id);
}

// Parent template
<app-post-card
  [post]="post"
  (postDeleted)="onPostDeleted($event)">
</app-post-card>
```

### 6.3 Smart vs Presentational Components

**Smart Components (Container):**
- Handle data fetching and state
- Connect to services
- Pass data to presentational components
- Example: `FeedComponent`

**Presentational Components (Dumb):**
- Receive data via @Input
- Emit events via @Output
- Focus on UI rendering
- Example: `PostCardComponent`

### 6.4 Key Components Reference

| Component | Type | Purpose |
|-----------|------|---------|
| `MainLayoutComponent` | Layout | App shell with navigation |
| `FeedComponent` | Smart | Main feed page |
| `PostCardComponent` | Presentational | Displays single post |
| `CommentsSectionComponent` | Smart | Comments for a post |
| `ProfileComponent` | Smart | User profile page |
| `FileUploadComponent` | Presentational | Drag & drop file upload |
| `ConfirmDialogComponent` | Presentational | Confirmation modal |

---

## 7. Services & Dependency Injection

### 7.1 Service Registration

Services are registered using the `@Injectable` decorator:

```typescript
@Injectable({
  providedIn: 'root'  // Application-wide singleton
})
export class AuthService {
  constructor(private http: HttpClient) {}
}
```

**Registration Options:**

| `providedIn` | Scope |
|-------------|-------|
| `'root'` | Application singleton (most common) |
| `'any'` | New instance per lazy module |
| `FeatureModule` | Scoped to specific module |

### 7.2 Core Services

#### AuthService (`core/services/auth.service.ts`)

Manages authentication state and operations:

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  login(credentials: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(tap(response => this.handleAuthSuccess(response)));
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    // Check token expiry
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 > Date.now();
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'ADMIN';
  }
}
```

#### PostService (`core/services/post.service.ts`)

Handles all post-related API operations:

```typescript
@Injectable({ providedIn: 'root' })
export class PostService {
  getFeed(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts/feed`);
  }

  createPost(request: PostRequest): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/posts`, request);
  }

  toggleLike(postId: number): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/posts/${postId}/like`, {});
  }

  addComment(postId: number, request: CommentRequest): Observable<Comment> {
    return this.http.post<Comment>(
      `${this.apiUrl}/posts/${postId}/comments`,
      request
    );
  }
}
```

#### NotificationService (`core/services/notification.service.ts`)

Manages notifications with reactive state:

```typescript
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private unreadCountSubject = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCountSubject.asObservable();

  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/notifications/unread-count`)
      .pipe(tap(count => this.unreadCountSubject.next(count)));
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/notifications/${id}/read`, {})
      .pipe(tap(() => this.decrementUnreadCount()));
  }
}
```

### 7.3 Service Dependencies

```
┌─────────────────┐
│  Component      │
│  (e.g., Feed)   │
└────────┬────────┘
         │ injects
         ▼
┌─────────────────┐
│   PostService   │
└────────┬────────┘
         │ injects
         ▼
┌─────────────────┐
│   HttpClient    │
└────────┬────────┘
         │ uses
         ▼
┌─────────────────┐
│  Interceptors   │
│ (JWT, Error)    │
└─────────────────┘
```

---

## 8. Guards & Route Protection

### 8.1 What are Guards?

Guards are services that control access to routes. They implement interfaces like `CanActivate`:

```typescript
interface CanActivate {
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | Observable<boolean> | Promise<boolean>;
}
```

### 8.2 AuthGuard

Protects routes requiring authentication:

```typescript
// core/guards/auth.guard.ts
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }
    this.router.navigate(['/auth/login']);
    return false;
  }
}
```

### 8.3 AdminGuard

Protects admin-only routes:

```typescript
// core/guards/admin.guard.ts
@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isLoggedIn() && this.authService.isAdmin()) {
      return true;
    }
    this.router.navigate(['/feed']);
    return false;
  }
}
```

### 8.4 GuestGuard

Redirects authenticated users away from auth pages:

```typescript
// core/guards/guest.guard.ts
@Injectable({ providedIn: 'root' })
export class GuestGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) {
      return true;
    }
    this.router.navigate(['/feed']);
    return false;
  }
}
```

### 8.5 Guard Flow Diagram

```
User navigates to /admin
         │
         ▼
┌─────────────────────┐
│ AuthGuard.canActivate()
│ Is user logged in?  │
└─────────┬───────────┘
          │
    ┌─────┴─────┐
    │           │
   Yes         No
    │           │
    ▼           ▼
┌────────┐  ┌──────────────┐
│AdminGuard│  │Redirect to   │
│Is admin? │  │/auth/login   │
└────┬────┘  └──────────────┘
     │
 ┌───┴───┐
 │       │
Yes     No
 │       │
 ▼       ▼
Allow  Redirect
       to /feed
```

---

## 9. HTTP Interceptors

### 9.1 What are Interceptors?

Interceptors are middleware for HTTP requests/responses. They can:
- Add headers to requests
- Handle errors globally
- Log requests
- Transform responses

### 9.2 JwtInterceptor

Automatically adds JWT token to API requests:

```typescript
// core/interceptors/jwt.interceptor.ts
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    const isApiUrl = request.url.startsWith(environment.apiUrl);

    if (token && isApiUrl) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request);
  }
}
```

**How it works:**
1. Check if user has a token
2. Check if request is to our API
3. Clone request and add Authorization header
4. Pass modified request down the chain

### 9.3 ErrorInterceptor

Handles HTTP errors globally:

```typescript
// core/interceptors/error.interceptor.ts
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An error occurred';

        switch (error.status) {
          case 400:
            errorMessage = error.error?.message || 'Bad request';
            break;
          case 401:
            errorMessage = 'Session expired. Please login again.';
            this.authService.logout();
            break;
          case 403:
            errorMessage = 'Access denied';
            break;
          case 404:
            errorMessage = 'Resource not found';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
        }

        this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
        return throwError(() => error);
      })
    );
  }
}
```

### 9.4 Interceptor Registration

Interceptors are registered in CoreModule:

```typescript
@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true  // Allows multiple interceptors
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ]
})
export class CoreModule { }
```

**Note:** Order matters! JwtInterceptor runs first, then ErrorInterceptor.

### 9.5 Request/Response Flow

```
Component
    │
    ▼ HTTP Request
┌──────────────────┐
│  JwtInterceptor  │ → Adds Authorization header
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ ErrorInterceptor │ → Catches errors on response
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│    HttpClient    │ → Makes actual HTTP request
└────────┬─────────┘
         │
         ▼
    Backend API
```

---

## 10. Authentication Flow

### 10.1 Complete Authentication Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   User      │     │  Angular    │     │  Backend    │
│   Browser   │     │  Frontend   │     │    API      │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │
       │ 1. Enter credentials                  │
       │──────────────────>│                   │
       │                   │                   │
       │                   │ 2. POST /auth/login
       │                   │──────────────────>│
       │                   │                   │
       │                   │ 3. JWT + User data│
       │                   │<──────────────────│
       │                   │                   │
       │                   │ 4. Store in localStorage
       │                   │ • auth_token      │
       │                   │ • auth_user       │
       │                   │                   │
       │ 5. Redirect to /feed                  │
       │<──────────────────│                   │
       │                   │                   │
       │ 6. Request protected resource         │
       │──────────────────>│                   │
       │                   │                   │
       │                   │ 7. GET /posts/feed
       │                   │ + Authorization: Bearer {token}
       │                   │──────────────────>│
       │                   │                   │
       │                   │ 8. Data response  │
       │                   │<──────────────────│
       │                   │                   │
       │ 9. Display feed   │                   │
       │<──────────────────│                   │
```

### 10.2 JWT Token Structure

A JWT token has three parts separated by dots:

```
xxxxx.yyyyy.zzzzz
  │      │      │
  │      │      └── Signature
  │      └── Payload (claims)
  └── Header (algorithm)
```

**Payload Example (decoded):**
```json
{
  "sub": "1",          // User ID
  "username": "john",
  "role": "USER",
  "iat": 1700000000,   // Issued at
  "exp": 1700086400    // Expiration (Unix timestamp)
}
```

### 10.3 Token Validation

```typescript
isLoggedIn(): boolean {
  const token = this.getToken();
  if (!token) return false;

  try {
    // Decode JWT payload (middle part)
    const payload = JSON.parse(atob(token.split('.')[1]));
    // Check if token is expired
    // exp is in seconds, Date.now() is in milliseconds
    return payload.exp * 1000 > Date.now();
  } catch {
    return false;
  }
}
```

### 10.4 Storage Strategy

**localStorage** is used for persistent storage:

```typescript
// After login
localStorage.setItem('auth_token', response.accessToken);
localStorage.setItem('auth_user', JSON.stringify(user));

// On app load
const token = localStorage.getItem('auth_token');
const user = JSON.parse(localStorage.getItem('auth_user') || 'null');

// On logout
localStorage.removeItem('auth_token');
localStorage.removeItem('auth_user');
```

**Why localStorage?**
- Persists across browser sessions
- Available in all tabs
- Simple API

---

## 11. Reactive Programming with RxJS

### 11.1 What is RxJS?

RxJS (Reactive Extensions for JavaScript) is a library for composing asynchronous and event-based programs using observable sequences.

**Key Concepts:**
- **Observable**: A stream of data over time
- **Observer**: Subscribes to and reacts to Observable
- **Operators**: Transform, filter, combine observables
- **Subject**: Both Observable and Observer

### 11.2 Observable vs Promise

| Observable | Promise |
|------------|---------|
| Multiple values over time | Single value |
| Lazy (doesn't execute until subscribed) | Eager (executes immediately) |
| Cancellable | Not cancellable |
| Rich operators | Limited methods |

### 11.3 Common RxJS Patterns in This Project

**HTTP Requests:**
```typescript
// Service
getPosts(): Observable<Post[]> {
  return this.http.get<Post[]>(`${this.apiUrl}/posts`);
}

// Component
this.postService.getPosts().subscribe({
  next: (posts) => this.posts = posts,
  error: (err) => console.error(err),
  complete: () => console.log('Done')
});
```

**BehaviorSubject for State:**
```typescript
// Service
private currentUserSubject = new BehaviorSubject<User | null>(null);
currentUser$ = this.currentUserSubject.asObservable();

// Update value
this.currentUserSubject.next(user);

// Component
this.authService.currentUser$.subscribe(user => {
  this.currentUser = user;
});
```

**Debounce Search:**
```typescript
// User search with debounce
searchControl = new FormControl('');

ngOnInit() {
  this.searchControl.valueChanges.pipe(
    debounceTime(300),           // Wait 300ms after last keystroke
    distinctUntilChanged(),       // Only if value changed
    filter(query => query.length >= 2),  // Minimum 2 characters
    switchMap(query => this.userService.searchUsers(query))
  ).subscribe(users => this.users = users);
}
```

### 11.4 Key RxJS Operators Used

| Operator | Purpose | Example |
|----------|---------|---------|
| `map` | Transform data | `map(user => user.name)` |
| `tap` | Side effects | `tap(data => console.log(data))` |
| `catchError` | Handle errors | `catchError(err => of([]))` |
| `switchMap` | Cancel previous, use latest | Search requests |
| `debounceTime` | Wait for pause in events | User input |
| `distinctUntilChanged` | Only emit when changed | Search |
| `filter` | Filter emissions | `filter(x => x > 0)` |
| `take` | Take n emissions then complete | `take(1)` |
| `takeUntil` | Complete when another emits | Unsubscribe pattern |

### 11.5 Subscription Management

**Problem:** Memory leaks from unsubscribed observables

**Solution 1: Unsubscribe in ngOnDestroy**
```typescript
export class MyComponent implements OnDestroy {
  private subscription: Subscription;

  ngOnInit() {
    this.subscription = this.service.getData().subscribe(...);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
```

**Solution 2: takeUntil Pattern**
```typescript
export class MyComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  ngOnInit() {
    this.service.getData()
      .pipe(takeUntil(this.destroy$))
      .subscribe(...);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

**Solution 3: Async Pipe (Recommended)**
```typescript
// Component
posts$ = this.postService.getPosts();

// Template
<div *ngFor="let post of posts$ | async">
  {{ post.content }}
</div>
```

---

## 12. Forms & Validation

### 12.1 Two Types of Forms

Angular provides two approaches to forms:

| Template-Driven | Reactive Forms |
|-----------------|----------------|
| Forms logic in template | Forms logic in component |
| Uses ngModel | Uses FormControl/FormGroup |
| Less code | More control |
| Harder to test | Easier to test |
| Simple forms | Complex forms |

**This project uses Reactive Forms** for better control and validation.

### 12.2 Reactive Forms Setup

```typescript
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

export class LoginComponent implements OnInit {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      usernameOrEmail: ['', [
        Validators.required,
        Validators.minLength(3)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(6)
      ]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const credentials = this.loginForm.value;
      // Submit...
    }
  }
}
```

**Template:**
```html
<form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
  <mat-form-field>
    <mat-label>Username or Email</mat-label>
    <input matInput formControlName="usernameOrEmail">
    <mat-error *ngIf="loginForm.get('usernameOrEmail')?.hasError('required')">
      Username is required
    </mat-error>
  </mat-form-field>

  <mat-form-field>
    <mat-label>Password</mat-label>
    <input matInput type="password" formControlName="password">
    <mat-error *ngIf="loginForm.get('password')?.hasError('minlength')">
      Password must be at least 6 characters
    </mat-error>
  </mat-form-field>

  <button mat-raised-button type="submit" [disabled]="loginForm.invalid">
    Login
  </button>
</form>
```

### 12.3 Built-in Validators

```typescript
Validators.required          // Field required
Validators.minLength(n)      // Minimum length
Validators.maxLength(n)      // Maximum length
Validators.email             // Email format
Validators.pattern(regex)    // Regex pattern
Validators.min(n)            // Minimum number
Validators.max(n)            // Maximum number
```

### 12.4 Custom Validators

**Password Match Validator (Register Form):**

```typescript
// Custom validator function
function passwordMatchValidator(group: FormGroup): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirmPassword = group.get('confirmPassword')?.value;
  return password === confirmPassword ? null : { passwordMismatch: true };
}

// Usage in form
this.registerForm = this.fb.group({
  username: ['', [Validators.required, Validators.minLength(3)]],
  email: ['', [Validators.required, Validators.email]],
  password: ['', [Validators.required, Validators.minLength(6)]],
  confirmPassword: ['', Validators.required]
}, { validators: passwordMatchValidator });

// Template
<mat-error *ngIf="registerForm.hasError('passwordMismatch')">
  Passwords do not match
</mat-error>
```

### 12.5 Form States

| State | Description |
|-------|-------------|
| `valid` | All validators pass |
| `invalid` | At least one validator fails |
| `pristine` | User hasn't changed value |
| `dirty` | User has changed value |
| `touched` | User has focused and blurred |
| `untouched` | User hasn't focused field |

```typescript
// Check states
this.loginForm.valid
this.loginForm.get('email')?.dirty
this.loginForm.get('password')?.touched
```

---

## 13. Angular Material Integration

### 13.1 What is Angular Material?

Angular Material is the official component library for Angular implementing Google's Material Design. It provides:
- Pre-built, accessible UI components
- Consistent styling
- Theming support
- Responsive behavior

### 13.2 Module Import Pattern

Components are imported through their respective modules:

```typescript
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
// ... etc

@NgModule({
  imports: [
    MatButtonModule,
    MatInputModule,
    MatCardModule,
    // ... all needed modules
  ],
  exports: [
    // Re-export for other modules
    MatButtonModule,
    MatInputModule,
    MatCardModule,
  ]
})
export class SharedModule { }
```

### 13.3 Components Used in This Project

| Component | Module | Usage |
|-----------|--------|-------|
| `mat-toolbar` | MatToolbarModule | Top navigation bar |
| `mat-sidenav` | MatSidenavModule | Side navigation |
| `mat-button` | MatButtonModule | Buttons throughout app |
| `mat-form-field` | MatFormFieldModule | Form input containers |
| `mat-input` | MatInputModule | Text inputs |
| `mat-card` | MatCardModule | Post cards, profile cards |
| `mat-dialog` | MatDialogModule | Modals/dialogs |
| `mat-snackbar` | MatSnackBarModule | Toast notifications |
| `mat-icon` | MatIconModule | Material icons |
| `mat-progress-spinner` | MatProgressSpinnerModule | Loading indicators |
| `mat-progress-bar` | MatProgressBarModule | File upload progress |
| `mat-table` | MatTableModule | Admin data tables |
| `mat-tabs` | MatTabsModule | Tab navigation |
| `mat-menu` | MatMenuModule | Dropdown menus |
| `mat-badge` | MatBadgeModule | Notification count |
| `mat-list` | MatListModule | Navigation lists |

### 13.4 Dialog Service Pattern

```typescript
// Opening a dialog
constructor(private dialog: MatDialog) {}

openCreatePostDialog() {
  const dialogRef = this.dialog.open(CreatePostDialogComponent, {
    width: '600px',
    data: { someData: 'value' }  // Pass data to dialog
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      // Handle dialog result
      this.posts.unshift(result);
    }
  });
}
```

**Dialog Component:**
```typescript
export class CreatePostDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<CreatePostDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  onSubmit() {
    // Return data when closing
    this.dialogRef.close(this.newPost);
  }

  onCancel() {
    this.dialogRef.close();  // Close without data
  }
}
```

### 13.5 Snackbar for Notifications

```typescript
constructor(private snackBar: MatSnackBar) {}

showSuccess(message: string) {
  this.snackBar.open(message, 'Close', {
    duration: 3000,
    panelClass: ['success-snackbar'],
    horizontalPosition: 'end',
    verticalPosition: 'top'
  });
}

showError(message: string) {
  this.snackBar.open(message, 'Close', {
    duration: 5000,
    panelClass: ['error-snackbar']
  });
}
```

### 13.6 Responsive Design with CDK

```typescript
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

constructor(private breakpointObserver: BreakpointObserver) {}

ngOnInit() {
  this.breakpointObserver
    .observe([Breakpoints.Handset])
    .subscribe(result => {
      this.isMobile = result.matches;
    });
}
```

---

## 14. State Management

### 14.1 State Management Approach

This project uses **service-based state management** with RxJS BehaviorSubjects, a simpler alternative to NgRx/Store suitable for medium-sized applications.

### 14.2 State Types

| State Type | Storage | Example |
|------------|---------|---------|
| UI State | Component property | `isLoading`, `showComments` |
| Session State | Service BehaviorSubject | `currentUser$` |
| Persistent State | localStorage | `auth_token`, `auth_user` |
| Server State | API responses | Posts, notifications |

### 14.3 BehaviorSubject Pattern

```typescript
@Injectable({ providedIn: 'root' })
export class NotificationService {
  // Private subject - only this service can emit
  private unreadCountSubject = new BehaviorSubject<number>(0);

  // Public observable - components subscribe to this
  unreadCount$ = this.unreadCountSubject.asObservable();

  // Method to update state
  updateUnreadCount(count: number) {
    this.unreadCountSubject.next(count);
  }

  // Get current value synchronously
  getCurrentCount(): number {
    return this.unreadCountSubject.getValue();
  }
}
```

**Component Usage:**
```typescript
export class MainLayoutComponent implements OnInit {
  unreadCount = 0;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    // Subscribe to state changes
    this.notificationService.unreadCount$.subscribe(
      count => this.unreadCount = count
    );
  }
}
```

**Template:**
```html
<mat-icon [matBadge]="unreadCount" matBadgeColor="warn">
  notifications
</mat-icon>
```

### 14.4 State Flow Diagram

```
┌─────────────────────────────────────────────────┐
│                 Application State               │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────┐    ┌─────────────┐            │
│  │ AuthService │    │NotifyService│            │
│  │ currentUser$│    │ unreadCount$│            │
│  └──────┬──────┘    └──────┬──────┘            │
│         │                   │                   │
│         └───────┬───────────┘                   │
│                 │                               │
│                 ▼                               │
│      Components subscribe to observables        │
│                                                 │
│   ┌─────────┐  ┌─────────┐  ┌─────────┐       │
│   │ Header  │  │ Profile │  │  Feed   │       │
│   └─────────┘  └─────────┘  └─────────┘       │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 15. Feature Modules Breakdown

### 15.1 Auth Module

**Path:** `/features/auth/`

**Purpose:** Handles user authentication (login/register)

**Components:**
- `LoginComponent` - User login form
- `RegisterComponent` - New user registration

**Routes:**
```typescript
{ path: 'login', component: LoginComponent }
{ path: 'register', component: RegisterComponent }
```

**Login Flow:**
1. User enters credentials
2. Form validation
3. `AuthService.login()` called
4. JWT stored in localStorage
5. Redirect to `/feed`

### 15.2 Posts Module

**Path:** `/features/posts/`

**Purpose:** Main feed and post management

**Components:**
- `FeedComponent` - Main feed page
- `PostCardComponent` - Individual post display
- `CommentsSectionComponent` - Comments for a post
- `CreatePostDialogComponent` - Create new post modal
- `EditPostDialogComponent` - Edit existing post
- `UserPostsListComponent` - List posts for a user

**Features:**
- View feed of posts from subscribed users
- Create posts with optional media
- Like/unlike posts (optimistic UI)
- View and add comments
- Edit/delete own posts

### 15.3 Users Module

**Path:** `/features/users/`

**Purpose:** User profiles and discovery

**Components:**
- `ProfileComponent` - User profile page
- `UserSearchComponent` - Search for users
- `EditProfileDialogComponent` - Edit own profile
- `ReportDialogComponent` - Report a user

**Features:**
- View own or other user's profile
- Edit display name and bio
- Subscribe/unsubscribe to users
- Search users by name
- Report users

### 15.4 Notifications Module

**Path:** `/features/notifications/`

**Purpose:** Notification management

**Components:**
- `NotificationListComponent` - List all notifications
- `NotificationItemComponent` - Single notification display

**Features:**
- View all notifications
- Filter by read/unread
- Mark as read (single/all)
- Delete notifications
- Navigate to related content

### 15.5 Admin Module

**Path:** `/features/admin/`

**Purpose:** Administrative functions

**Components:**
- `DashboardComponent` - Admin dashboard

**Features:**
- View all users (ban/unban/delete)
- View all posts (delete)
- View all reports (dismiss)
- Data tables with sorting

---

## 16. Shared Module Pattern

### 16.1 Purpose

The Shared Module contains reusable components, pipes, and directives that are used across multiple feature modules.

### 16.2 Shared Components

#### ConfirmDialogComponent

Generic confirmation dialog:

```typescript
// Opening
const dialogRef = this.dialog.open(ConfirmDialogComponent, {
  data: {
    title: 'Delete Post',
    message: 'Are you sure you want to delete this post?',
    confirmText: 'Delete',
    cancelText: 'Cancel'
  }
});

dialogRef.afterClosed().subscribe(confirmed => {
  if (confirmed) {
    this.deletePost();
  }
});
```

#### FileUploadComponent

Drag & drop file upload:

```typescript
// Template usage
<app-file-upload
  [acceptedTypes]="['image/*', 'video/*']"
  [maxSizeMB]="10"
  (fileSelected)="onFileSelected($event)"
  (fileRemoved)="onFileRemoved()">
</app-file-upload>
```

**Features:**
- Drag & drop zone
- Click to browse
- File type validation
- Size validation
- Image/video preview
- Progress indicator

#### PostCardSkeletonComponent

Loading placeholder for posts:

```html
<app-post-card-skeleton [count]="3" *ngIf="isLoading">
</app-post-card-skeleton>
```

### 16.3 Module Exports

```typescript
@NgModule({
  exports: [
    // Angular modules
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // All Angular Material modules
    MatButtonModule,
    MatCardModule,
    // ... etc

    // Shared components
    ConfirmDialogComponent,
    FileUploadComponent,
    PostCardSkeletonComponent
  ]
})
export class SharedModule { }
```

---

## 17. Environment Configuration

### 17.1 Environment Files

Angular supports different environments through environment files:

**Development** (`src/environments/environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  fileUrl: 'http://localhost:8080/api/files'
};
```

**Production** (`src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: '/api',
  fileUrl: '/api/files'
};
```

### 17.2 Usage in Services

```typescript
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = environment.apiUrl;

  getPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts`);
  }
}
```

### 17.3 Build Configuration

The build process automatically replaces environment files:

```bash
# Development build (uses environment.ts)
ng build

# Production build (uses environment.prod.ts)
ng build --configuration=production
```

---

## 18. Testing

### 18.1 Testing Framework

- **Jasmine**: Testing framework (describe, it, expect)
- **Karma**: Test runner
- **TestBed**: Angular testing utility

### 18.2 Test File Structure

```typescript
// auth.service.spec.ts
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  describe('login', () => {
    it('should store token on successful login', () => {
      const credentials: LoginRequest = {
        usernameOrEmail: 'test',
        password: 'password'
      };
      const mockResponse: JwtResponse = {
        accessToken: 'mock-token',
        tokenType: 'Bearer',
        userId: 1,
        username: 'test',
        role: 'USER'
      };

      service.login(credentials).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(localStorage.getItem('auth_token')).toBe('mock-token');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);
    });
  });
});
```

### 18.3 Component Testing

```typescript
// login.component.spec.ts
describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
        // Material modules...
      ],
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should call authService.login on submit', () => {
    authService.login.and.returnValue(of(mockResponse));

    component.loginForm.setValue({
      usernameOrEmail: 'test',
      password: 'password123'
    });
    component.onSubmit();

    expect(authService.login).toHaveBeenCalled();
  });
});
```

### 18.4 Running Tests

```bash
# Run all tests
npm test
# or
ng test

# Run tests with code coverage
ng test --code-coverage

# Run specific test file
ng test --include=**/auth.service.spec.ts
```

---

## 19. Build & Deployment

### 19.1 Development Server

```bash
# Start development server
npm start
# or
ng serve

# With specific port
ng serve --port 4201

# Open browser automatically
ng serve --open
```

Development server runs at `http://localhost:4200` with hot reload.

### 19.2 Production Build

```bash
# Production build
npm run build
# or
ng build --configuration=production
```

Output is generated in `dist/frontend/`.

### 19.3 Build Optimization

Production build includes:
- **AOT Compilation**: Ahead-of-Time compilation
- **Tree Shaking**: Remove unused code
- **Minification**: Compress JavaScript
- **Bundling**: Combine files

### 19.4 Build Budgets

Configured in `angular.json`:

```json
"budgets": [
  {
    "type": "initial",
    "maximumWarning": "1mb",
    "maximumError": "2mb"
  },
  {
    "type": "anyComponentStyle",
    "maximumWarning": "2kb",
    "maximumError": "4kb"
  }
]
```

### 19.5 Deployment Checklist

1. ✅ Update environment.prod.ts with production API URL
2. ✅ Run `ng build --configuration=production`
3. ✅ Copy `dist/frontend/` contents to web server
4. ✅ Configure server for SPA routing (redirect all routes to index.html)
5. ✅ Enable HTTPS
6. ✅ Set proper CORS headers on backend

---

## 20. API Reference

### 20.1 Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | User login | No |
| POST | `/auth/register` | User registration | No |

### 20.2 User Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/users/{id}` | Get user profile | Yes |
| PUT | `/users/{id}` | Update user profile | Yes (own) |
| GET | `/users/search?query=` | Search users | Yes |

### 20.3 Post Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/posts/feed` | Get feed posts | Yes |
| GET | `/posts/{id}` | Get single post | Yes |
| GET | `/posts/user/{userId}` | Get user's posts | Yes |
| POST | `/posts` | Create post | Yes |
| PUT | `/posts/{id}` | Update post | Yes (own) |
| DELETE | `/posts/{id}` | Delete post | Yes (own) |
| POST | `/posts/{id}/like` | Toggle like | Yes |
| GET | `/posts/{id}/comments` | Get comments | Yes |
| POST | `/posts/{id}/comments` | Add comment | Yes |
| DELETE | `/comments/{id}` | Delete comment | Yes (own) |

### 20.4 Subscription Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/subscriptions/{userId}` | Subscribe to user | Yes |
| DELETE | `/subscriptions/{userId}` | Unsubscribe | Yes |
| GET | `/subscriptions/{userId}/status` | Check subscription | Yes |

### 20.5 Notification Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/notifications` | Get all notifications | Yes |
| GET | `/notifications/unread` | Get unread notifications | Yes |
| GET | `/notifications/unread-count` | Get unread count | Yes |
| PUT | `/notifications/{id}/read` | Mark as read | Yes |
| PUT | `/notifications/read-all` | Mark all as read | Yes |
| DELETE | `/notifications/{id}` | Delete notification | Yes |

### 20.6 File Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/files/upload` | Upload file | Yes |
| GET | `/files/{filename}` | Get file | Yes |
| DELETE | `/files/{filename}` | Delete file | Yes |

### 20.7 Report Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/reports` | Create report | Yes |

### 20.8 Admin Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/admin/users` | Get all users | Admin |
| PUT | `/admin/users/{id}/ban` | Ban user | Admin |
| PUT | `/admin/users/{id}/unban` | Unban user | Admin |
| DELETE | `/admin/users/{id}` | Delete user | Admin |
| GET | `/admin/posts` | Get all posts | Admin |
| DELETE | `/admin/posts/{id}` | Delete post | Admin |
| GET | `/admin/reports` | Get all reports | Admin |
| DELETE | `/admin/reports/{id}` | Dismiss report | Admin |

---

## Appendix A: Angular CLI Commands

```bash
# Generate new component
ng generate component features/module-name/components/component-name
ng g c features/module-name/components/component-name

# Generate new service
ng generate service core/services/service-name
ng g s core/services/service-name

# Generate new module with routing
ng generate module features/module-name --routing
ng g m features/module-name --routing

# Generate guard
ng generate guard core/guards/guard-name
ng g guard core/guards/guard-name

# Lint code
ng lint

# Run tests
ng test

# Build
ng build
ng build --configuration=production
```

---

## Appendix B: Project Dependencies

### Production Dependencies

```json
{
  "@angular/animations": "^17.3.0",
  "@angular/cdk": "^17.3.10",
  "@angular/common": "^17.3.0",
  "@angular/compiler": "^17.3.0",
  "@angular/core": "^17.3.0",
  "@angular/forms": "^17.3.0",
  "@angular/material": "^17.3.10",
  "@angular/platform-browser": "^17.3.0",
  "@angular/platform-browser-dynamic": "^17.3.0",
  "@angular/router": "^17.3.0",
  "rxjs": "~7.8.0",
  "tslib": "^2.3.0",
  "zone.js": "~0.14.3"
}
```

### Development Dependencies

```json
{
  "@angular/cli": "~17.3.17",
  "@angular/compiler-cli": "^17.3.0",
  "@types/jasmine": "~5.1.0",
  "jasmine-core": "~5.1.0",
  "karma": "~6.4.0",
  "karma-chrome-launcher": "~3.2.0",
  "karma-coverage": "~2.2.0",
  "karma-jasmine": "~5.1.0",
  "karma-jasmine-html-reporter": "~2.1.0",
  "typescript": "~5.4.2"
}
```

---

## Appendix C: Glossary

| Term | Definition |
|------|------------|
| **AOT** | Ahead-of-Time compilation - compiles during build |
| **JIT** | Just-in-Time compilation - compiles in browser |
| **DI** | Dependency Injection - design pattern for managing dependencies |
| **SPA** | Single Page Application - app loads once, navigates without reload |
| **Observable** | RxJS data stream that emits values over time |
| **Subject** | Observable that can emit values to subscribers |
| **BehaviorSubject** | Subject that stores current value and emits to new subscribers |
| **Guard** | Service that controls route access |
| **Interceptor** | Middleware for HTTP requests/responses |
| **Decorator** | TypeScript feature that adds metadata to classes |
| **NgModule** | Angular module that organizes application parts |
| **Component** | Building block with template, class, and styles |
| **Service** | Class that handles business logic and data |
| **Pipe** | Transforms data in templates |
| **Directive** | Adds behavior to DOM elements |

---

*Documentation generated for 01blog Frontend - Angular 17.3.0*
*Last updated: January 2026*
