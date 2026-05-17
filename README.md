# iyal - Family Library Management Platform (Backend)

A minimalistic, aesthetic family library management and social reading platform built with Spring Boot.

## 🎯 Overview

**iyal** is a production-quality web application for home and family use, allowing multiple family members to:
- Maintain a shared library of physical books
- Track who is reading what
- Discover each other's reading activity
- Browse and search the family collection
- Manage borrowing and reading statuses

## 🛠 Tech Stack

- **Framework:** Spring Boot 4.0.6
- **Language:** Java 17
- **Security:** Spring Security + JWT
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA + Hibernate
- **Image Upload:** Cloudinary
- **Build Tool:** Gradle 9.4.1

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Cloudinary account (free tier works)
- Gradle (or use included wrapper)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd library
```

### 2. Set Up PostgreSQL

```bash
# Create database
createdb iyal_db

# Or using psql
psql -U postgres
CREATE DATABASE iyal_db;
```

### 3. Configure Environment Variables

```bash
# Copy example env file
cp .env.example .env

# Edit .env with your values
```

Required environment variables:
- `DB_USERNAME` - PostgreSQL username
- `DB_PASSWORD` - PostgreSQL password
- `JWT_SECRET` - Secret key for JWT (min 256 bits)
- `CLOUDINARY_CLOUD_NAME` - Cloudinary cloud name
- `CLOUDINARY_API_KEY` - Cloudinary API key
- `CLOUDINARY_API_SECRET` - Cloudinary API secret
- `ALLOWED_ORIGINS` - Frontend URL (e.g., http://localhost:3000)

### 4. Run the Application

```bash
# Using Gradle wrapper
./gradlew bootRun

# Or build and run JAR
./gradlew build
java -jar build/libs/library-0.0.1-SNAPSHOT.jar
```

The server will start on `http://localhost:8080`

## 📦 Database Schema

The application automatically creates these tables:

- **users** - User accounts with roles (USER, ADMIN)
- **books** - Book catalog with inventory tracking
- **reading_statuses** - User reading progress
- **borrowings** - Physical book borrowing records

## 🔑 User Roles

### USER (Default)
- Browse and search books
- Borrow/return books
- Mark reading status
- View public profiles
- Toggle own profile visibility

### ADMIN
- All USER permissions
- Add/edit/delete books
- Upload book covers
- View all users
- Soft delete users
- View analytics dashboard

## 📚 API Endpoints

### Authentication
```
POST /auth/register    - Register new user
POST /auth/login       - Login and get JWT token
```

### Books
```
GET    /books                - Get all books
GET    /books/{id}           - Get book by ID
GET    /books/search         - Search books (title, author, genre, availability)
GET    /books/genres         - Get all genres
GET    /books/recent         - Get recently added books
POST   /books                - Create book (ADMIN)
PUT    /books/{id}           - Update book (ADMIN)
DELETE /books/{id}           - Delete book (ADMIN)
```

### Borrowing
```
POST /books/{id}/borrow     - Borrow a book
POST /books/{id}/return     - Return a book
GET  /books/borrowings/my   - Get my borrowing history
```

### Reading Status
```
POST /books/{id}/status                    - Set reading status
GET  /books/{id}/status/my                 - Get my status for book
GET  /books/reading-status/my              - Get all my reading statuses
GET  /books/reading-status/currently-reading - Get family currently reading
GET  /books/reading-status/filter          - Filter by status
```

### Users
```
GET   /users/me                  - Get current user info
GET   /users/{id}/profile        - Get user profile (public only)
PATCH /users/profile-visibility  - Update profile visibility
```

### Dashboard
```
GET /dashboard - Get personalized dashboard data
```

### Admin
```
GET    /admin/users            - Get all users (ADMIN)
DELETE /admin/users/{id}       - Soft delete user (ADMIN)
GET    /admin/borrowings       - Get currently borrowed books (ADMIN)
GET    /admin/analytics        - Get analytics dashboard (ADMIN)
```

## 🔒 Authentication

All endpoints except `/auth/**` require JWT authentication.

### Login Flow
1. Register: `POST /auth/register`
2. Login: `POST /auth/login`
3. Receive JWT token
4. Include token in requests: `Authorization: Bearer <token>`

## 📸 Image Upload

Book covers are uploaded to Cloudinary.

**Supported formats:** JPG, PNG, WebP, GIF  
**Max size:** 10MB  
**Folder:** iyal/books

## 🗃 Sample Data

Create an admin user programmatically or register and manually update the role in the database:

```sql
-- Update user role to ADMIN
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@iyal.com';
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

## 🏗 Project Structure

```
src/main/java/com/example/library/
├── config/            # Security, CORS, Cloudinary configs
├── controller/        # REST controllers
├── dto/              # Request/Response DTOs
├── entity/           # JPA entities
├── exception/        # Custom exceptions & global handler
├── repository/       # JPA repositories
├── security/         # JWT utilities & filters
└── service/          # Business logic layer
```

## 🌍 Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_USERNAME` | PostgreSQL username | `postgres` |
| `DB_PASSWORD` | PostgreSQL password | `secret123` |
| `JWT_SECRET` | JWT signing secret (256+ bits) | `your-very-long-secret-key` |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name | `your-cloud` |
| `CLOUDINARY_API_KEY` | Cloudinary API key | `123456789012345` |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret | `your-api-secret` |
| `ALLOWED_ORIGINS` | CORS allowed origins | `http://localhost:3000` |

## 🚨 Common Issues

### Database Connection
```
Error: Connection refused
Solution: Ensure PostgreSQL is running and credentials are correct
```

### JWT Secret Too Short
```
Error: JWT secret must be at least 256 bits
Solution: Use a longer secret key (32+ characters)
```

### Cloudinary Upload Fails
```
Error: Invalid credentials
Solution: Verify Cloudinary API keys in .env
```

## 📄 License

MIT License - feel free to use this project for personal or commercial purposes.

## 🤝 Contributing

This is a personal/family project, but suggestions are welcome!

---

Built with ❤️ for families who love books
