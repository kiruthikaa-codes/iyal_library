# 🚀 Deployment Guide - Iyal Library

## Overview
Deploy your family library with backend on Railway and frontend on Vercel.

---

## Prerequisites
- [ ] GitHub account
- [ ] Railway account (sign up at https://railway.app with GitHub)
- [ ] Vercel account (sign up at https://vercel.com with GitHub)
- [ ] Cloudinary account (already set up)

---

## Part 1: Deploy Backend (Railway)

### Step 1: Push Code to GitHub

```bash
# Navigate to backend directory
cd ~/Downloads/library

# Create a new GitHub repository at https://github.com/new
# Name it: iyal-backend

# Add remote and push
git remote add origin https://github.com/YOUR_USERNAME/iyal-backend.git
git add .
git commit -m "Initial commit - Iyal Library Backend"
git push -u origin main
```

### Step 2: Deploy to Railway

1. Go to https://railway.app
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Choose your `iyal-backend` repository
5. Railway will auto-detect Spring Boot and deploy

### Step 3: Add PostgreSQL Database

1. In your Railway project, click "+ New"
2. Select "Database" → "PostgreSQL"
3. Railway will create and link the database automatically

### Step 4: Configure Environment Variables

In Railway project → Variables tab, add:

```env
# Database (Railway provides these automatically)
DB_USERNAME=${PGUSER}
DB_PASSWORD=${PGPASSWORD}

# JWT Secret (generate a secure random string)
JWT_SECRET=your-super-secret-jwt-key-change-this-to-something-random-and-long

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

# CORS (will update after deploying frontend)
ALLOWED_ORIGINS=https://your-frontend-url.vercel.app
```

### Step 5: Update application.yaml

Your `application.yaml` should use Railway's PostgreSQL variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}
    username: ${PGUSER}
    password: ${PGPASSWORD}
```

### Step 6: Get Backend URL

- Railway will provide a URL like: `https://iyal-backend-production-xxxx.up.railway.app`
- Copy this URL - you'll need it for the frontend

---

## Part 2: Deploy Frontend (Vercel)

### Step 1: Create Frontend Repository

```bash
# Navigate to frontend directory
cd ~/Downloads/iyal-frontend

# Initialize git if not already done
git init

# Create a new GitHub repository at https://github.com/new
# Name it: iyal-frontend

# Add files and push
git add .
git commit -m "Initial commit - Iyal Library Frontend"
git remote add origin https://github.com/YOUR_USERNAME/iyal-frontend.git
git push -u origin main
```

### Step 2: Deploy to Vercel

1. Go to https://vercel.com
2. Click "Add New" → "Project"
3. Import your `iyal-frontend` repository
4. Configure:
   - Framework Preset: Other
   - Build Command: (leave empty)
   - Output Directory: .
   - Install Command: (leave empty)
5. Click "Deploy"

### Step 3: Update Frontend API URL

After deployment, update `app.js`:

```javascript
// Change from:
const API_URL = 'http://localhost:8080';

// To your Railway backend URL:
const API_URL = 'https://iyal-backend-production-xxxx.up.railway.app';
```

Commit and push:
```bash
git add app.js
git commit -m "Update API URL for production"
git push
```

Vercel will auto-deploy the update.

### Step 4: Update CORS in Backend

Go back to Railway → Variables and update:
```env
ALLOWED_ORIGINS=https://your-actual-frontend.vercel.app
```

---

## Part 3: Create Admin User

After deployment, create your admin user:

```bash
# Use Railway CLI or the web terminal
railway run bash

# Then inside the container:
psql $DATABASE_URL -c "UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';"
```

Or register via the frontend and then update in Railway web interface.

---

## 🎉 You're Done!

Your app is now live at:
- **Frontend:** https://your-app.vercel.app
- **Backend:** https://iyal-backend-production-xxxx.up.railway.app

---

## Post-Deployment

### Monitor Your App
- Railway Dashboard: Check logs and metrics
- Vercel Dashboard: Check deployments and analytics

### Custom Domain (Optional)
- Vercel: Settings → Domains → Add your domain
- Railway: Settings → Domains → Generate domain or add custom

### Costs
- Railway: Free tier (500 hours/month)
- Vercel: Free tier (unlimited)
- Cloudinary: Free tier (25GB)
