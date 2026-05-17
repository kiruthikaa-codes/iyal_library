# iyal API Documentation

## Authentication

All requests (except `/auth/**`) require a JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

---

## Authentication Endpoints

### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securepassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securepassword123"
}
```

**Response:** Same as register

---

## Book Endpoints

### Get All Books
```http
GET /books
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "genre": "Fiction",
    "description": "A classic American novel...",
    "coverImageUrl": "https://res.cloudinary.com/.../cover.jpg",
    "totalCount": 2,
    "availableCount": 1,
    "createdAt": "2024-01-15T10:30:00",
    "isAvailable": true
  }
]
```

### Get Book by ID
```http
GET /books/1
Authorization: Bearer <token>
```

### Search Books
```http
GET /books/search?title=gatsby&genre=Fiction&available=true
Authorization: Bearer <token>
```

Query parameters (all optional):
- `title` - Search in title (case-insensitive, partial match)
- `author` - Search in author (case-insensitive, partial match)
- `genre` - Filter by exact genre (case-insensitive)
- `available` - Filter by availability (true/false)

### Get All Genres
```http
GET /books/genres
Authorization: Bearer <token>
```

**Response:** `["Fiction", "Non-Fiction", "Science Fiction", ...]`

### Get Recently Added Books
```http
GET /books/recent
Authorization: Bearer <token>
```

### Create Book (Admin Only)
```http
POST /books
Authorization: Bearer <token>
Content-Type: multipart/form-data

book: {
  "title": "1984",
  "author": "George Orwell",
  "genre": "Dystopian Fiction",
  "description": "A dystopian social science fiction...",
  "totalCount": 1
}
coverImage: <file>
```

### Update Book (Admin Only)
```http
PUT /books/1
Authorization: Bearer <token>
Content-Type: multipart/form-data

book: {
  "title": "1984 (Updated)",
  "author": "George Orwell",
  "genre": "Fiction",
  "description": "Updated description",
  "totalCount": 2
}
coverImage: <file> (optional)
```

### Delete Book (Admin Only)
```http
DELETE /books/1
Authorization: Bearer <token>
```

**Response:** 204 No Content

---

## Borrowing Endpoints

### Borrow Book
```http
POST /books/1/borrow
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 2,
  "userName": "John Doe",
  "bookId": 1,
  "bookTitle": "The Great Gatsby",
  "bookCoverImageUrl": "https://...",
  "borrowedAt": "2024-01-20T14:30:00",
  "returnedAt": null,
  "isReturned": false
}
```

### Return Book
```http
POST /books/1/return
Authorization: Bearer <token>
```

### Get My Borrowings
```http
GET /books/borrowings/my
Authorization: Bearer <token>
```

---

## Reading Status Endpoints

### Set Reading Status
```http
POST /books/1/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "READING"
}
```

Status values: `WANT_TO_READ`, `READING`, `FINISHED`

### Get My Reading Status for a Book
```http
GET /books/1/status/my
Authorization: Bearer <token>
```

### Get All My Reading Statuses
```http
GET /books/reading-status/my
Authorization: Bearer <token>
```

### Get Currently Reading by Family
```http
GET /books/reading-status/currently-reading
Authorization: Bearer <token>
```

Returns reading statuses of users with public profiles who are currently reading.

### Filter Reading Statuses by Status
```http
GET /books/reading-status/filter?status=FINISHED
Authorization: Bearer <token>
```

---

## User Endpoints

### Get Current User Info
```http
GET /users/me
Authorization: Bearer <token>
```

### Get User Profile
```http
GET /users/2/profile
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 2,
  "name": "Jane Smith",
  "isProfilePublic": true,
  "createdAt": "2024-01-10T09:00:00",
  "currentlyReading": [...],
  "recentlyFinished": [...],
  "totalBooksRead": 15
}
```

### Update Profile Visibility
```http
PATCH /users/profile-visibility
Authorization: Bearer <token>
Content-Type: application/json

{
  "isPublic": false
}
```

---

## Dashboard Endpoint

### Get Dashboard
```http
GET /dashboard
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "continueReading": [...],
  "currentlyReadingByFamily": [...],
  "recentlyAdded": [...],
  "popularBooks": [...]
}
```

---

## Admin Endpoints

All admin endpoints require `ADMIN` role.

### Get All Users
```http
GET /admin/users
Authorization: Bearer <token>
```

### Soft Delete User
```http
DELETE /admin/users/2
Authorization: Bearer <token>
```

### Get Currently Borrowed Books
```http
GET /admin/borrowings
Authorization: Bearer <token>
```

### Get Analytics
```http
GET /admin/analytics
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "totalBooks": 150,
  "totalUsers": 5,
  "currentlyBorrowedBooks": 8,
  "activeUsers": 5,
  "popularGenres": {
    "Fiction": 50,
    "Non-Fiction": 30,
    "Science Fiction": 20
  },
  "mostReadBooks": {
    "The Great Gatsby": 3,
    "1984": 2
  }
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already registered",
  "path": "/auth/register"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-20T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/auth/login"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found with id: 999",
  "path": "/books/999"
}
```

### Validation Errors
```json
{
  "timestamp": "2024-01-20T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid input",
  "path": "/auth/register",
  "validationErrors": {
    "email": "Email must be valid",
    "password": "Password must be between 6 and 100 characters"
  }
}
```
