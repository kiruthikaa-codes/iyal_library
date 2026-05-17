# Database Schema Documentation

## Overview

The iyal application uses PostgreSQL with 4 main tables managed by Hibernate with automatic schema generation.

## Entity Relationship Diagram

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│    users    │────────<│ reading_statuses │>────────│    books    │
└─────────────┘         └──────────────────┘         └─────────────┘
       │                                                      │
       │                                                      │
       │                ┌──────────────┐                     │
       └───────────────<│  borrowings  │>────────────────────┘
                        └──────────────┘
```

## Tables

### users

Stores user account information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user ID |
| name | VARCHAR(100) | NOT NULL | User's full name |
| email | VARCHAR(150) | NOT NULL, UNIQUE | User's email (username) |
| password | VARCHAR(255) | NOT NULL | Bcrypt hashed password |
| role | VARCHAR(20) | NOT NULL | USER or ADMIN |
| is_profile_public | BOOLEAN | NOT NULL, DEFAULT true | Profile visibility |
| is_active | BOOLEAN | NOT NULL, DEFAULT true | Soft delete flag |
| created_at | TIMESTAMP | NOT NULL | Account creation timestamp |

**Indexes:**
- Unique index on `email`

### books

Stores the library's book catalog.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique book ID |
| title | VARCHAR(255) | NOT NULL | Book title |
| author | VARCHAR(200) | NOT NULL | Author name |
| genre | VARCHAR(100) | NOT NULL | Book genre/category |
| description | TEXT | | Book description |
| cover_image_url | VARCHAR(500) | | Cloudinary image URL |
| total_count | INTEGER | NOT NULL, DEFAULT 1 | Total physical copies |
| available_count | INTEGER | NOT NULL, DEFAULT 1 | Available copies |
| created_at | TIMESTAMP | NOT NULL | Book addition timestamp |

**Business Rules:**
- `available_count` ≤ `total_count`
- `available_count` decrements when borrowed
- `available_count` increments when returned

### reading_statuses

Tracks user reading progress for books.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique status ID |
| user_id | BIGINT | NOT NULL, FOREIGN KEY → users(id) | User reference |
| book_id | BIGINT | NOT NULL, FOREIGN KEY → books(id) | Book reference |
| status | VARCHAR(20) | NOT NULL | WANT_TO_READ, READING, FINISHED |
| updated_at | TIMESTAMP | NOT NULL | Last status update |

**Constraints:**
- Unique constraint on `(user_id, book_id)` - one status per user per book
- Cascade delete when user or book is deleted

**Indexes:**
- Index on `user_id`
- Index on `book_id`
- Composite index on `(user_id, book_id)`

### borrowings

Tracks physical book borrowing history.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique borrowing ID |
| user_id | BIGINT | NOT NULL, FOREIGN KEY → users(id) | Borrower reference |
| book_id | BIGINT | NOT NULL, FOREIGN KEY → books(id) | Borrowed book reference |
| borrowed_at | TIMESTAMP | NOT NULL | Borrowing timestamp |
| returned_at | TIMESTAMP | | Return timestamp |
| is_returned | BOOLEAN | NOT NULL, DEFAULT false | Return status flag |

**Business Rules:**
- User cannot borrow the same book twice simultaneously
- Book's `available_count` must be > 0 to borrow
- Setting `is_returned = true` also sets `returned_at`

**Indexes:**
- Index on `user_id`
- Index on `book_id`
- Index on `is_returned` for active borrowing queries

## Sample Queries

### Get all available books
```sql
SELECT * FROM books WHERE available_count > 0;
```

### Get user's currently reading books
```sql
SELECT b.* 
FROM books b
JOIN reading_statuses rs ON b.id = rs.book_id
WHERE rs.user_id = 1 AND rs.status = 'READING';
```

### Get currently borrowed books
```sql
SELECT u.name, b.title, br.borrowed_at
FROM borrowings br
JOIN users u ON br.user_id = u.id
JOIN books b ON br.book_id = b.id
WHERE br.is_returned = false
ORDER BY br.borrowed_at DESC;
```

### Get most popular genres
```sql
SELECT genre, COUNT(*) as count
FROM books
GROUP BY genre
ORDER BY count DESC;
```

### Get user's reading statistics
```sql
SELECT 
  COUNT(CASE WHEN status = 'READING' THEN 1 END) as currently_reading,
  COUNT(CASE WHEN status = 'FINISHED' THEN 1 END) as finished,
  COUNT(CASE WHEN status = 'WANT_TO_READ' THEN 1 END) as want_to_read
FROM reading_statuses
WHERE user_id = 1;
```

### Get family reading activity (public profiles only)
```sql
SELECT u.name, b.title, rs.status, rs.updated_at
FROM reading_statuses rs
JOIN users u ON rs.user_id = u.id
JOIN books b ON rs.book_id = b.id
WHERE u.is_profile_public = true 
  AND u.is_active = true
  AND rs.status = 'READING'
ORDER BY rs.updated_at DESC;
```

## Initial Setup

### Create Database
```sql
CREATE DATABASE iyal_db;
```

### Create First Admin User (after app creates tables)
```sql
-- First register via API, then update role
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'admin@iyal.com';
```

## Data Integrity

- **Referential Integrity:** All foreign keys use `ON DELETE CASCADE`
- **Check Constraints:** Added via JPA validation annotations
- **Unique Constraints:** Email uniqueness, (user_id, book_id) in reading_statuses
- **Not Null:** All critical fields are non-nullable

## Performance Considerations

- Indexes on foreign keys for JOIN performance
- Composite index on reading_statuses for common queries
- `is_returned` index for active borrowing queries
- `genre` could be indexed for filtering (consider if collection grows large)

## Backup Recommendations

```bash
# Daily backup
pg_dump -U postgres iyal_db > backup_$(date +%Y%m%d).sql

# Restore
psql -U postgres iyal_db < backup_20240120.sql
```
