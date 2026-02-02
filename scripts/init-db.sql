-- ============================================
-- 01Blog - Database Initialization Script
-- ============================================
-- Tables are created automatically by Hibernate (ddl-auto=update)
-- ============================================

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE blog_db TO postgres;
