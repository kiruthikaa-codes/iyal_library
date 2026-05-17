# 🔧 Update API URL for Deployment

After deploying your backend to Railway, you need to update the frontend to point to it.

## Step 1: Get Your Railway Backend URL

After deploying to Railway, you'll get a URL like:
```
https://iyal-backend-production-a1b2c3d4.up.railway.app
```

Copy this URL.

## Step 2: Update app.js

Open `app.js` and change line 1:

**Before (local development):**
```javascript
const API_URL = 'http://localhost:8080';
```

**After (production):**
```javascript
const API_URL = 'https://iyal-backend-production-a1b2c3d4.up.railway.app';
```

Replace with your actual Railway URL!

## Step 3: Save and Deploy

```bash
git add app.js
git commit -m "Update API URL for production"
git push
```

Vercel will automatically redeploy with the new URL.

---

## 🔄 Switching Between Local and Production

**Option 1: Environment Detection (Recommended)**

Update line 1 in `app.js` to:

```javascript
const API_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8080' 
    : 'https://iyal-backend-production-a1b2c3d4.up.railway.app';
```

This way it works both locally AND in production!

**Option 2: Two Branches**

- `main` branch: Production URL (auto-deploys to Vercel)
- `dev` branch: Local URL (for development)

---

## ✅ Verify It's Working

After deployment:
1. Visit your Vercel URL
2. Open browser DevTools (F12)
3. Go to Network tab
4. Try to login
5. Check that requests go to your Railway URL, not localhost

---

## 🆘 Troubleshooting

**CORS Error?**
Make sure Railway has this variable:
```
ALLOWED_ORIGINS=https://your-vercel-url.vercel.app
```

**404 Not Found?**
- Verify Railway backend is running
- Check Railway URL is correct
- Try accessing Railway URL directly in browser

**Backend is sleeping?**
- Railway free tier apps sleep after 30min inactivity
- First request might take 30 seconds to wake up
- Subsequent requests will be fast
