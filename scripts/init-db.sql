-- ============================================
-- 01Blog - Database Initialization Script
-- ============================================
-- This script contains the complete database schema
-- Consolidated from all migration versions (V1-V4)
-- ============================================

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE blog_db TO postgres;

-- ============================================
-- TABLES
-- ============================================

-- Users table (V1 + V4: avatar_url)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    bio VARCHAR(500),
    role VARCHAR(255) NOT NULL DEFAULT 'USER',
    banned BOOLEAN NOT NULL DEFAULT FALSE,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

-- Posts table (V1 + V2: hidden)
CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(5000) NOT NULL,
    media_url VARCHAR(255),
    media_type VARCHAR(255),
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id)
);

-- Comments table (V1)
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    post_id BIGINT NOT NULL REFERENCES posts(id),
    user_id BIGINT NOT NULL REFERENCES users(id)
);

-- Likes table (V1)
CREATE TABLE IF NOT EXISTS likes (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    post_id BIGINT NOT NULL REFERENCES posts(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    UNIQUE (post_id, user_id)
);

-- Subscriptions table (V1)
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    subscriber_id BIGINT NOT NULL REFERENCES users(id),
    subscribed_to_id BIGINT NOT NULL REFERENCES users(id),
    UNIQUE (subscriber_id, subscribed_to_id)
);

-- Reports table (V1 + V3: reported_post_id, nullable reported_user_id)
CREATE TABLE IF NOT EXISTS reports (
    id BIGSERIAL PRIMARY KEY,
    reason VARCHAR(1000) NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    reported_user_id BIGINT REFERENCES users(id),
    reported_post_id BIGINT REFERENCES posts(id),
    reporter_id BIGINT NOT NULL REFERENCES users(id)
);

-- Notifications table (V1)
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(255) NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    user_id BIGINT NOT NULL REFERENCES users(id),
    related_post_id BIGINT REFERENCES posts(id),
    related_user_id BIGINT REFERENCES users(id)
);
