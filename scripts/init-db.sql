-- ============================================
-- 01Blog - Database Initialization Script
-- ============================================
-- This script runs on first container startup
-- ============================================

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE blog_db TO postgres;

-- Note: Flyway will handle table creation
-- This script is for any additional initialization
