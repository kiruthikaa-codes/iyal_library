# ⚡ Simplest Railway Deployment (Everything in One)

## 5 Steps to Deploy

### Step 1: Copy Frontend into Backend

```bash
cd ~/Downloads/library
mkdir -p frontend
cp -r ~/Downloads/iyal-frontend/* frontend/
```

### Step 2: Update Frontend to Use Relative URLs

Edit `frontend/app.js` line 1:

```javascript
// Change from:
const API_URL = 'http://localhost:8080';

// To:
const API_URL = window.location.origin;
```

Save the file.

### Step 3: Push to GitHub

```bash
# Make sure you're in ~/Downloads/library
cd ~/Downloads/library

# Stage all files
git add .
git commit -m "Ready for Railway deployment"

# Create new repo on GitHub: https://github.com/new
# Name it: iyal-library

# Push (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/iyal-library.git
git push -u origin main
```

### Step 4: Deploy on Railway

1. Go to https://railway.app
2. Sign up with GitHub
3. Click "New Project"
4. Click "Deploy from GitHub repo"
5. Select `iyal-library`
6. Wait for it to build (~2 minutes)

### Step 5: Add Database & Environment Variables

**Add PostgreSQL:**
1. In your project, click "+ New"
2. Select "Database" → "PostgreSQL"
3. It will auto-connect

**Add Environment Variables:**
1. Click on your service (not the database)
2. Go to "Variables" tab
3. Click "Raw Editor"
4. Paste this (replace the JWT secret with any long random string):

```env
JWT_SECRET=change-this-to-a-very-long-random-string-at-least-32-characters-long
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

5. Click "Deploy" (or it will auto-deploy)

---

## 🎉 That's It!

Railway will give you a URL like:
```
https://iyal-library-production-a1b2.up.railway.app
```

Visit it - your app is live! Both frontend and backend on the same URL.

---

## 📝 Create Your Admin Account

1. Visit your Railway URL
2. Click "Register"
3. Create an account (email: admin@iyal.com, password: admin123)
4. Go to Railway → Your Database → "Data" tab
5. Click "Query" and run:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'admin@iyal.com';
   ```
6. Refresh your app and login - you're now admin!

---

## ✅ What You Get

- **One URL** for everything
- **Auto-deploys** when you push to GitHub
- **Free tier**: 500 hours/month (plenty for personal use)
- **No CORS issues** (frontend & backend on same domain)
- **PostgreSQL database** included

---

## 🔄 Updating Your App

Just push to GitHub:
```bash
# Make changes to code
git add .
git commit -m "Updated feature X"
git push

# Railway auto-deploys in ~2 minutes
```

---

## 🆘 Troubleshooting

**Build failed?**
- Check Railway logs in the "Deployments" tab
- Make sure all files are committed and pushed

**Can't see the app?**
- Wait 2-3 minutes for first deployment
- Check if service is "Active" in Railway dashboard

**Database connection error?**
- Make sure PostgreSQL is added to the project
- Railway auto-sets DATABASE_URL variable

**500 Error?**
- Check logs in Railway dashboard
- Verify environment variables are set correctly
