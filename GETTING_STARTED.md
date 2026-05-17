# Getting Started with iyal

## 🎯 What You Have

You now have a **production-quality full-stack application** for family library management:

✅ **Backend:** Complete Spring Boot REST API  
✅ **Database:** PostgreSQL schema with 4 core tables  
✅ **Security:** JWT authentication + role-based access  
✅ **Features:** All user and admin functionality  
✅ **Documentation:** Comprehensive guides  
✅ **Frontend:** Setup guide + component templates  

---

## 📋 Prerequisites

Before starting, ensure you have:

- ✅ Java 17 or higher
- ✅ PostgreSQL 12 or higher
- ✅ Node.js 18 or higher (for frontend)
- ✅ Cloudinary account (free tier works)
- ✅ Git

---

## 🚀 Step-by-Step Setup

### Step 1: Backend Setup (15 minutes)

#### 1.1 Install PostgreSQL
```bash
# macOS
brew install postgresql@15
brew services start postgresql@15

# Ubuntu/Debian
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

#### 1.2 Create Database
```bash
# Access PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE iyal_db;

# Exit
\q
```

#### 1.3 Configure Environment
```bash
cd Downloads/library

# Copy environment template
cp .env.example .env

# Edit .env with your credentials
nano .env
```

Required values:
```env
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password
JWT_SECRET=create-a-random-256-bit-string-here
CLOUDINARY_CLOUD_NAME=get-from-cloudinary-dashboard
CLOUDINARY_API_KEY=get-from-cloudinary-dashboard
CLOUDINARY_API_SECRET=get-from-cloudinary-dashboard
ALLOWED_ORIGINS=http://localhost:3000
```

#### 1.4 Get Cloudinary Credentials
1. Sign up at [cloudinary.com](https://cloudinary.com) (free)
2. Go to Dashboard
3. Copy: Cloud Name, API Key, API Secret
4. Paste into `.env`

#### 1.5 Run Backend
```bash
# Make gradlew executable (Unix/Mac)
chmod +x gradlew

# Run application
./gradlew bootRun

# Or on Windows
gradlew.bat bootRun
```

✅ Backend should start on `http://localhost:8080`

#### 1.6 Verify Backend
```bash
# In a new terminal
curl http://localhost:8080/books

# Should return: {"timestamp":"...","status":401,"error":"Unauthorized"...}
# This is correct - it means the API is running and requiring auth!
```

---

### Step 2: Create Admin User (5 minutes)

Since the backend is running, you need to create an admin user.

#### Option A: Using curl (Quick Test)

```bash
# Register a user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@iyal.com",
    "password": "admin123"
  }'

# Note the response - it will include a JWT token
```

#### Option B: Using PostgreSQL (Make User Admin)

```bash
# Access PostgreSQL
psql -U postgres -d iyal_db

# Update user role to ADMIN
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@iyal.com';

# Verify
SELECT id, name, email, role FROM users;

# Exit
\q
```

---

### Step 3: Test Backend API (5 minutes)

Now test with the admin user token:

```bash
# Login to get a fresh token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@iyal.com",
    "password": "admin123"
  }'

# Copy the "token" from response

# Create a book (replace YOUR_TOKEN)
curl -X POST http://localhost:8080/books \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F 'book={"title":"The Great Gatsby","author":"F. Scott Fitzgerald","genre":"Fiction","description":"A classic novel","totalCount":1}' \
  -F 'coverImage=@/path/to/image.jpg'

# Get all books
curl http://localhost:8080/books \
  -H "Authorization: Bearer YOUR_TOKEN"
```

✅ If you see book data, backend is working perfectly!

---

### Step 4: Frontend Setup (20 minutes)

#### 4.1 Initialize Next.js Project
```bash
# Go to workspace
cd ~/Downloads

# Create Next.js app
npx create-next-app@latest iyal-frontend \
  --typescript \
  --tailwind \
  --app \
  --src-dir \
  --import-alias "@/*"

# Enter the project
cd iyal-frontend
```

When prompted:
- ✅ TypeScript: Yes
- ✅ ESLint: Yes
- ✅ Tailwind CSS: Yes
- ✅ `src/` directory: Yes
- ✅ App Router: Yes
- ✅ Import alias: Yes (@/*)

#### 4.2 Install Dependencies
```bash
# Core dependencies
npm install axios zustand @tanstack/react-query framer-motion lucide-react clsx tailwind-merge class-variance-authority date-fns

# shadcn/ui
npx shadcn-ui@latest init

# When prompted, accept defaults
```

#### 4.3 Configure Environment
```bash
# Create environment file
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > .env.local
```

#### 4.4 Copy Template Files

Copy files from `Downloads/library/frontend-templates/` to your Next.js project:

```bash
# Create directories
mkdir -p src/lib src/store src/components/books

# Copy files (adjust paths as needed)
cp ../library/frontend-templates/lib-api.ts src/lib/api.ts
cp ../library/frontend-templates/lib-types.ts src/lib/types.ts
cp ../library/frontend-templates/store-authStore.ts src/store/authStore.ts
cp ../library/frontend-templates/globals.css src/app/globals.css
cp ../library/frontend-templates/components-BookCard.tsx src/components/books/BookCard.tsx
```

#### 4.5 Install shadcn Components
```bash
npx shadcn-ui@latest add button input card dialog form label select textarea toast tabs avatar badge skeleton alert
```

#### 4.6 Run Frontend
```bash
npm run dev
```

✅ Frontend should start on `http://localhost:3000`

---

### Step 5: Test Full Stack (5 minutes)

With both backend and frontend running:

1. Open browser: `http://localhost:3000`
2. Register a new user
3. Login
4. Browse books (should be empty unless you added via curl)
5. If admin, add a book with cover image

---

## 🎉 Success! What's Next?

### Immediate Next Steps

1. **Build Remaining Pages** (using templates as reference):
   - Dashboard (`src/app/(dashboard)/page.tsx`)
   - Browse Books (`src/app/(dashboard)/books/page.tsx`)
   - Book Detail (`src/app/(dashboard)/books/[id]/page.tsx`)
   - My Books (`src/app/(dashboard)/my-books/page.tsx`)
   - Settings (`src/app/(dashboard)/settings/page.tsx`)
   - Admin pages (`src/app/(admin)/...`)

2. **Complete Components**:
   - BookGrid
   - BookFilters
   - Navbar
   - Sidebar
   - Dashboard sections

3. **Add React Query Hooks**:
   - `useBooks`
   - `useDashboard`
   - `useReadingStatus`

### Reference Documentation

- **Backend API:** `API_DOCUMENTATION.md`
- **Database Schema:** `DATABASE_SCHEMA.md`
- **Frontend Guide:** `FRONTEND_SETUP.md`
- **Deployment:** `DEPLOYMENT.md`
- **Project Overview:** `PROJECT_SUMMARY.md`

---

## 🐛 Troubleshooting

### Backend won't start
```bash
# Check Java version
java -version  # Should be 17+

# Check PostgreSQL
psql -U postgres -c "SELECT version();"

# View logs
./gradlew bootRun --info
```

### Database connection fails
```bash
# Verify PostgreSQL is running
pg_isready

# Check credentials in .env
cat .env | grep DB_
```

### Frontend build errors
```bash
# Clear cache
rm -rf .next node_modules package-lock.json

# Reinstall
npm install

# Rebuild
npm run dev
```

### CORS errors
- Verify backend `ALLOWED_ORIGINS` includes `http://localhost:3000`
- Restart backend after changing .env

---

## 📚 Learn More

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Next.js Docs](https://nextjs.org/docs)
- [Tailwind CSS](https://tailwindcss.com)
- [shadcn/ui](https://ui.shadcn.com)

---

## 💬 Need Help?

1. Check `PROJECT_SUMMARY.md` for architecture overview
2. Review `API_DOCUMENTATION.md` for endpoint details
3. See `DATABASE_SCHEMA.md` for data structure
4. Consult `DEPLOYMENT.md` for production setup

---

**You're all set to build a beautiful family library! 📚✨**
