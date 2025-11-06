# 01Blog Project - Daily TODO Backlog

A 5-week (25-day) task list to track daily progress. Weekends are reserved for catch-up.

---

## Sprint 1: The Foundation (Authentication & Setup)
**Goal:** A user can register, log in, and be authenticated. Project structures are in place.

### Day 1 (Mon): Project Initialization
> **Daily Goal:** Get both projects running locally.

- [✅] `[Backend]` Initialize Spring Boot project (Web, JPA, Spring Security, PostgreSQL/MySQL Driver).
- [✅] `[Backend]` Configure `application.properties` to connect to your local database.
- [✅] `[Frontend]` Initialize Angular project and add your chosen UI framework (Angular Material or Bootstrap).
- [✅] `[Git]` Create a new GitHub repository.
- [✅] `[Git]` `git init`, create `main` and `develop` branches, and make your first commit.

### Day 2 (Tues): User & Role Modeling
> **Daily Goal:** Define what a "user" is in your system.

- [✅] `[Backend]` Create the `User` entity (e.g., `id`, `username`, `email`, `password`, `role`).
- [✅] `[Backend]` Create the `Role` enum or entity (e.g., `ROLE_USER`, `ROLE_ADMIN`).
- [ ] `[Backend]` Create the `UserRepository`.
- [ ] `[Frontend]` Create basic app routing: `/login`, `/register`, and `/` (Homepage).

### Day 3 (Wed): Registration
> **Daily Goal:** A new user can create an account.

- [ ] `[Backend]` Create an `AuthService` with a `registerUser` method (include password hashing with `PasswordEncoder`).
- [ ] `[Backend]` Create an `AuthController` with a `/api/auth/register` endpoint.
- [ ] `[Frontend]` Build the **Registration** page component (a simple HTML form).
- [ ] `[Frontend]` Create an `AuthService` (in Angular) with a `register()` method.
- [ ] `[Test]` **Verify:** Can you create a user from the frontend and see them in your database?

### Day 4 (Thurs): Login & JWT
> **Daily Goal:** A registered user can log in and receive a token.

- [ ] `[Backend]` Configure `SpringSecurity` (e.g., `SecurityFilterChain` bean).
- [ ] `[Backend]` Create a `JwtTokenProvider` utility class to generate and validate tokens.
- [ ] `[Backend]` Implement the `/api/auth/login` endpoint (authenticates and returns a JWT).
- [ ] `[Frontend]` Build the **Login** page component (HTML form).
- [ ] `[Frontend]` Add a `login()` method to your Angular `AuthService` to call the API.
- [ ] `[Frontend]` Implement token storage (e.g., in `localStorage`).
- [ ] `[Test]` **Verify:** Can you log in with the user you created yesterday? Do you see a JWT in your browser's dev tools?

### Day 5 (Fri): Route Protection
> **Daily Goal:** Secure all app routes (frontend and backend).

- [ ] `[Backend]` Add security rules to protect all future API routes (e.g., `/api/**`).
- [ ] `[Frontend]` Create an Angular **Auth Guard** (`canActivate`).
- [ ] `[Frontend]` Add the guard to your "Homepage" route (and all future protected routes).
- [ ] `[Frontend]` Add an `HttpInterceptor` to attach the JWT to all outgoing requests.
- [ ] `[Sprint Review]` **Demo to yourself:** Register. Log out. Try to access the homepage (should be blocked). Log in. Access the homepage (should work).

---

## Sprint 2: Core Content (Posts & Profiles)
**Goal:** Users can create posts with media and view user "Blocks" (profiles).

### Day 6 (Mon): Post Backend
> **Daily Goal:** Create the backend for text-only posts.

- [ ] `[Backend]` Create the `Post` entity (with `@ManyToOne` relationship to `User`, `description`, `timestamp`).
- [ ] `[Backend]` Create the `PostRepository`.
- [ ] `[Backend]` Create `PostService` and `PostController`.
- [ ] `[Backend]` Implement the `createPost` endpoint (text-only for now). Secure it.

### Day 7 (Tues): Post Frontend
> **Daily Goal:** A user can create a post from the UI.

- [ ] `[Frontend]` Create a "New Post" component (e.g., a modal or a new page).
- [ ] `[Frontend]` Build the form (e.g., a `textarea` and "Submit" button).
- [ ] `[Frontend]` Hook up the form to the `createPost` API endpoint.
- [ ] `[Test]` **Verify:** Can you log in and create a new post? Do you see it in the database?

### Day 8 (Wed): Profile "Block" Page
> **Daily Goal:** Users can view a public profile page showing all posts by that user.

- [ ] `[Backend]` Implement the `getPostsByUsername` endpoint.
- [ ] `[Frontend]` Create a "User Block" page component (e.g., at `/block/:username`).
- [ ] `[Frontend]` On page load, get the username from the URL, call the API, and fetch the posts.
- [ ] `[Frontend]` Create a reusable `Post` component (to display the post content).
- [ ] `[Frontend]` Render the list of posts on the "User Block" page using your `Post` component.

### Day 9 (Thurs): Post Management (Edit/Delete)
> **Daily Goal:** A user can edit and delete their *own* posts.

- [ ] `[Backend]` Implement the `deletePost` endpoint. Add security (only post owner or admin).
- [ ] `[Backend]` Implement the `updatePost` endpoint. Add security (only post owner).
- [ ] `[Frontend]` Add "Edit" and "Delete" buttons to your `Post` component.
- [ ] `[Frontend]` Conditionally show these buttons *only if* the logged-in user is the post's author.
- [ ] `[Frontend]` Hook up the buttons to the API.

### Day 10 (Fri): Media Upload
> **Daily Goal:** Users can attach an image or video to their post.

- [ ] `[Backend]` Create a `FileStorageService` (to save files to the file system).
- [ ] `[Backend]` Update `createPost` to handle `MultipartFile` and save the file.
- [ ] `[Backend]` Configure Spring to serve the uploaded files (e.g., as `/media/filename.jpg`).
- [ ] `[Frontend]` Add a file input (`<input type="file">`) to your "New Post" form.
- [ ] `[Frontend]` Update your `Post` component to display the image/video if it exists.
- [ ] `[Sprint Review]` **Demo to yourself:** Create a post with an image. Edit the text. Delete the post. View another user's profile.

---

## Sprint 3: Making it "Social"
**Goal:** Users can interact with each other (follow, like, comment) and see a personalized feed.

### Day 11 (Mon): Subscriptions (Follows)
> **Daily Goal:** A user can follow/unfollow another user.

- [ ] `[Backend]` Design the `Subscription` entity (e.g., a table with `follower_id` and `following_id`).
- [ ] `[Backend]` Implement `subscribeToUser` and `unsubscribeFromUser` endpoints.
- [ ] `[Frontend]` Add "Follow" / "Unfollow" buttons to the "User Block" page.
- [ ] `[Frontend]` Conditionally show the correct button based on subscription status.
- [ ] `[Frontend]` Hook up the buttons to the new API endpoints.

### Day 12 (Tues): Homepage Feed
> **Daily Goal:** The homepage shows a feed of posts *only* from subscribed users.

- [ ] `[Backend]` Create a `FeedService`.
- [ ] `[Backend]` Implement the `getHomepageFeed` endpoint (this is a key query: get posts from users you follow, sorted by date).
- [ ] `[Frontend]` Build the **Homepage** component.
- [ ] `[Frontend]` On page load, call the `/api/feed` endpoint and render the list of posts.

### Day 13 (Wed): Likes
> **Daily Goal:** A user can like and unlike a post.

- [ ] `[Backend]` Create the `Like` entity (`@ManyToOne` User, `@ManyToOne` Post).
- [ ] `[Backend]` Implement `likePost` and `unlikePost` endpoints.
- [ ] `[Backend]` Update your Post DTOs (Data Transfer Objects) to include `likeCount` and `isLikedByCurrentUser`.
- [ ] `[Frontend]` Add a "Like" button (and like count) to your `Post` component.
- [ ] `[Frontend]` Make the button's state (liked/not liked) dynamic based on the DTO.
- [ ] `[Frontend]` Hook up the button to the API.

### Day 14 (Thurs): Comments
> **Daily Goal:** A user can add and view comments on a post.

- [ ] `[Backend]` Create the `Comment` entity (`@ManyToOne` User, `@ManyToOne` Post, `text`).
- [ ] `[Backend]` Implement `addCommentToPost` endpoint.
- [ ] `[Backend]` Implement `getCommentsForPost` endpoint.
- [ ] `[Frontend]` Add a "Comment" section under each post.
- [ ] `[Frontend]` Add a small form (`<input>`, "Submit") to add a new comment.
- [ ] `[Frontend]` Add logic to fetch and display the list of existing comments.

### Day 15 (Fri): Social Polish
> **Daily Goal:** Clean up the social features and test them together.

- [ ] `[Frontend]` Make the comment list update after a user posts a new one (a simple refresh is fine).
- [ ] `[Frontend]` Ensure like counts update correctly.
- [ ] `[Test]` **Verify:** Can you follow a user? Do their posts appear on your feed? Can you like a post? Can you comment?
- [ ] `[Sprint Review]` **Demo to yourself:** The full social loop.

---

## Sprint 4: Admin & Moderation
**Goal:** Admins can manage the platform and users can report bad content.

### Day 16 (Mon): User Reporting
> **Daily Goal:** A user can report another user's profile for bad content.

- [ ] `[Backend]` Create the `Report` entity (`@ManyToOne` reporter, `@ManyToOne` reportedUser, `reason`, `status` enum).
- [ ] `[Backend]` Implement the `reportUser` endpoint.
- [ ] `[Frontend]` Create a "Report User" modal component.
- [ ] `[Frontend]` Add a "Report" button to the "User Block" page that opens the modal.
- [ ] `[Frontend]` Hook up the modal's "Submit" button to the API.

### Day 17 (Tues): Admin Backend (Setup)
> **Daily Goal:** Create secure API endpoints for admin actions.

- [ ] `[Backend]` Create an `AdminController` and secure all its routes with `hasRole('ADMIN')`.
- [ ] `[Backend]` Implement `getAllUsers` endpoint.
- [ ] `[Backend]` Implement `banUser` endpoint (e.g., set a `isBanned` flag on the `User`).
- [ ] `[Backend]` Update Spring Security to check for the `isBanned` flag during login.
- [ ] `[Backend]` Implement `getAllReports` endpoint.

### Day 18 (Wed): Admin Backend (Content)
> **Daily Goal:** Admins can manage content and reports.

- [ ] `[Backend]` Implement `deleteAnyPost` endpoint (in `AdminController`).
- [ ] `[Backend]` Implement `handleReport` endpoint (e.g., changes report `status` to "HANDLED").
- [ ] `[Backend]` Manually create an ADMIN user in your database (or an endpoint to do it) for testing.

### Day 19 (Thurs): Admin Frontend (Dashboard)
> **Daily Goal:** Build the UI for the admin dashboard.

- [ ] `[Frontend]` Create an **Admin Guard** (`canActivate`) that checks for `ROLE_ADMIN`.
- [ ] `[Frontend]` Create an `AdminDashboard` component, protected by the guard.
- [ ] `[Frontend]` On the dashboard, create tabs/sections: "User Management," "Post Management," and "Reports."
- [ ] `[Frontend]` Fetch and display all users in a table.
- [ ] `[Frontend]` Fetch and display all pending reports in a table.

### Day 20 (Fri): Admin Actions
> **Daily Goal:** Connect the admin UI to the backend.

- [ ] `[Frontend]` Add "Ban" / "Delete" buttons to the users table and hook them up.
- [ ] `[Frontend]` Add "Handle Report," "Ban User," and "Delete Post" buttons to the reports table.
- [ ] `[Frontend]` Hook up all the actions to the `AdminController` endpoints.
- [ ] `[Sprint Review]` **Demo to yourself:** Log in as a user, report someone. Log out. Log in as an admin, see the report, and ban the user.

---

## Sprint 5: Notifications, Polish & Deployment Prep
**Goal:** Finish all mandatory features, clean up the UI, and write the documentation.

### Day 21 (Mon): Notifications
> **Daily Goal:** Notify users when someone they follow posts.

- [ ] `[Backend]` Create `Notification` entity (`@ManyToOne` user, `text`, `link`, `isRead`).
- [ ] `[Backend]` In `PostService`, when a post is created, create notifications for all subscribers.
- [ ] `[Backend]` Implement `getNotificationsForUser` and `markNotificationAsRead` endpoints.
- [ ] `[Frontend]` Create a `NotificationBell` component in the navbar.
- [ ] `[Frontend]` Fetch unread notifications and show a count.
- [ ] `[Frontend]` Create a dropdown to display notifications.

### Day 22 (Tues): UI/UX Polish
> **Daily Goal:** Improve the user experience.

- [ ] `[Frontend]` Add loading spinners to all pages/actions that fetch data.
- [ ] `[Frontend]` Add "toast" or "snackbar" notifications for success ("Post created!") and errors ("Invalid username").
- [ ] `[Frontend]` Check all forms for basic validation (e.g., "Password must be 8 characters").

### Day 23 (Wed): Responsiveness
> **Daily Goal:** Make the app usable on mobile.

- [ ] `[Frontend]` Resize your browser to a mobile width.
- [ ] `[Frontend]` Go through every single page (Login, Register, Feed, Profile, Admin).
- [ ] `[Frontend]` Fix all major layout breaks (e.g., using your UI framework's grid system or CSS flexbox/grid).

### Day 24 (Thurs): README (Part 1)
> **Daily Goal:** Start the project documentation.

- [ ] `[Docs]` Create your `README.md` file.
- [ ] `[Docs]` Write the "Overview" and "Technologies Used" sections.
- [ ] `[Docs]` Write the "How to Run - Backend" section (detailing Java version, DB setup, and how to start the server).

### Day 25 (Fri): README (Part 2) & Final Demo
> **Daily Goal:** Finish the documentation and prepare for evaluation.

- [ ] `[Docs]` Write the "How to Run - Frontend" section (detailing Node.js version, `npm install`, and `ng serve`).
- [ ] `[Test]` Do one final, full-app test (the "critical path"): Register -> Log In -> Create Post -> Follow User -> Like/Comment -> Report -> Log in as Admin -> Handle Report.
- [ ] `[Project]` Clean up your code, remove `console.log` statements, and make your final commit.