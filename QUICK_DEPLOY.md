# ⚡ Quick Deploy Checklist

## 🎯 Goal
Get your Iyal Library live in ~30 minutes!

---

## ✅ Checklist

### Preparation (5 min)
- [ ] Create GitHub account (if you don't have one)
- [ ] Create Railway account: https://railway.app (sign up with GitHub)
- [ ] Create Vercel account: https://vercel.com (sign up with GitHub)
- [ ] Have your Cloudinary credentials ready (already in `.env`)

---

### Backend Deployment (10 min)

#### 1. Push Backend to GitHub
```bash
cd ~/Downloads/library

# Create repository on GitHub: https://github.com/new
# Name: iyal-backend

git add .
git commit -m "Ready for deployment"
git remote add origin https://github.com/YOUR_USERNAME/iyal-backend.git
git push -u origin main
```

#### 2. Deploy on Railway
1. Go to https://railway.app/new
2. Click "Deploy from GitHub repo"
3. Select `iyal-backend`
4. Click "+ New" → "Database" → "PostgreSQL"
5. Go to Variables tab, add:
   ```
   JWT_SECRET=paste-a-long-random-string-here
   CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
   CLOUDINARY_API_KEY=your_cloudinary_api_key
   CLOUDINARY_API_SECRET=your_cloudinary_api_secret
   ```
6. Copy your Railway backend URL (e.g., `https://xxx.up.railway.app`)

---

### Frontend Deployment (10 min)

#### 3. Update Frontend API URL
```bash
cd ~/Downloads/iyal-frontend

# Edit app.js line 1:
# Change: const API_URL = 'http://localhost:8080';
# To:     const API_URL = 'https://YOUR-RAILWAY-URL.up.railway.app';
```

#### 4. Push Frontend to GitHub
```bash
# Create repository on GitHub: https://github.com/new
# Name: iyal-frontend

git init
git add .
git commit -m "Ready for deployment"
git remote add origin https://github.com/YOUR_USERNAME/iyal-frontend.git
git push -u origin main
```

#### 5. Deploy on Vercel
1. Go to https://vercel.com/new
2. Import `iyal-frontend` repository
3. Framework Preset: **Other**
4. Leave all other settings as default
5. Click "Deploy"
6. Copy your Vercel URL (e.g., `https://iyal-frontend.vercel.app`)

---

### Final Steps (5 min)

#### 6. Update CORS
Go back to Railway → Your Backend → Variables → Add:
```
ALLOWED_ORIGINS=https://your-vercel-url.vercel.app
```

#### 7. Create Admin Account
1. Visit your Vercel URL
2. Click "Register"
3. Email: `admin@iyal.com`
4. Password: `admin123` (or your choice)
5. Name: Your Name

Then update role to ADMIN:
- Railway → Database → Connect
- Run: `UPDATE users SET role = 'ADMIN' WHERE email = 'admin@iyal.com';`

---

## 🎉 Done!

Your app is live at:
- **Frontend:** https://your-app.vercel.app
- **Backend:** https://your-backend.railway.app

---

## 💡 Tips

**Free Tier Limits:**
- Railway: 500 hours/month (plenty for personal use)
- Vercel: Unlimited deployments
- Cloudinary: 25GB storage

**Auto-Deployment:**
- Push to GitHub → Auto-deploys to Railway/Vercel
- No manual deployment needed!

**Monitoring:**
- Railway: Check logs in dashboard
- Vercel: Check deployments tab

**Custom Domain (Optional):**
- Buy domain on Namecheap/GoDaddy
- Add to Vercel: Settings → Domains
- Point to your Vercel app

---

## 🆘 Troubleshooting

**Backend won't start?**
- Check Railway logs for errors
- Verify all environment variables are set
- Make sure PostgreSQL is connected

**Frontend can't connect to backend?**
- Verify API_URL in app.js matches Railway URL
- Check CORS settings in Railway variables
- Look at browser console for errors

**Database issues?**
- Railway auto-creates DATABASE_URL variable
- Check Postgres is running in Railway dashboard

---

## 📧 Need Help?
Check the full `DEPLOY_GUIDE.md` for detailed steps!
