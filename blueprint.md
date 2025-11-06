### Project Blueprint: 01Blog

**1. Core Technologies**

*   **Backend:** Java Spring Boot (JPA, Spring Security, Web)
*   **Frontend:** Angular
*   **Database:** PostgreSQL or MySQL
*   **Authentication:** JWT (JSON Web Tokens)

**2. High-Level Feature Breakdown**

The application will be built around these core concepts:

*   **Users & Authentication:** Secure registration, login, and role-based access (USER, ADMIN).
*   **Content Creation:** Users can create, edit, and delete posts, which can contain both text and media.
*   **Social Interaction:** Users can follow each other, creating a personalized feed. They can also like and comment on posts.
*   **Moderation & Administration:** A reporting system for inappropriate content and an admin dashboard for user and content management.
*   **Notifications:** Real-time alerts for new content from followed users.

**3. Development Sprints & Task List**

This project will be developed over five sprints, each lasting one week (five days).

**Sprint 1: The Foundation (Authentication & Setup)**

*   **Goal:** Establish the project structure and implement a complete authentication system.
*   **Tasks:**
    1.  **Project Setup:**
        *   **Backend:** Configure the Spring Boot application, connect it to the database, and verify all dependencies.
        *   **Frontend:** Initialize the Angular project and integrate a UI framework like Angular Material or Bootstrap.
    2.  **Data Modeling:**
        *   **Backend:** Create the `User` and `Role` entities.
    3.  **Registration:**
        *   **Backend:** Build the `/api/auth/register` endpoint with password hashing.
        *   **Frontend:** Create the registration page and form.
    4.  **Login & JWT:**
        *   **Backend:** Build the `/api/auth/login` endpoint and generate a JWT.
        *   **Frontend:** Create the login page and handle JWT storage.
    5.  **Route Protection:**
        *   **Backend:** Secure all API endpoints.
        *   **Frontend:** Implement an `AuthGuard` and an `HttpInterceptor` to protect routes and attach the JWT to requests.

**Sprint 2: Core Content (Posts & Profiles)**

*   **Goal:** Enable users to create and manage content.
*   **Tasks:**
    1.  **Post Backend:** Create the `Post` entity and the basic API for creating text-only posts.
    2.  **Post Frontend:** Build the UI for creating a new post.
    3.  **Profile "Block" Page:** Develop the user profile page (`/block/:username`) that displays all posts by a specific user.
    4.  **Post Management:** Implement the backend and frontend for editing and deleting posts.
    5.  **Media Uploads:** Enhance the post creation feature to allow image and video uploads.

**Sprint 3: Making it "Social"**

*   **Goal:** Introduce features that allow users to interact with each other.
*   **Tasks:**
    1.  **Subscriptions (Follows):** Implement the ability for users to follow and unfollow each other.
    2.  **Homepage Feed:** Create a personalized homepage feed that shows posts from followed users.
    3.  **Likes:** Allow users to like and unlike posts.
    4.  **Comments:** Implement a commenting system for posts.
    5.  **Social Polish:** Refine and test all social features to ensure they work together seamlessly.

**Sprint 4: Admin & Moderation**

*   **Goal:** Build tools for administrators to manage the platform and for users to report content.
*   **Tasks:**
    1.  **User Reporting:** Create a system for users to report inappropriate profiles.
    2.  **Admin Backend:** Develop a secure admin-only API for managing users, posts, and reports.
    3.  **Admin Dashboard:** Build the frontend dashboard for administrators.
    4.  **Admin Actions:** Connect the admin dashboard UI to the backend API to enable actions like banning users and deleting posts.

**Sprint 5: Notifications, Polish & Deployment Prep**

*   **Goal:** Add final features, polish the UI/UX, and prepare the project for deployment.
*   **Tasks:**
    1.  **Notifications:** Implement a notification system to alert users of new posts from people they follow.
    2.  **UI/UX Polish:** Improve the user experience by adding loading indicators, success/error messages, and form validation.
    3.  **Responsiveness:** Ensure the application is fully responsive and works well on mobile devices.
    4.  **Documentation:** Write a comprehensive `README.md` file with instructions on how to set up and run the project.
    5.  **Final Testing:** Conduct a full end-to-end test of the application's critical features.
