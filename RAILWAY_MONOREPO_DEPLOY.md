# 🚂 Deploy Everything on Railway (Monorepo)

## Setup: Combine Frontend & Backend

### Step 1: Move Frontend into Backend Repo

```bash
cd ~/Downloads/library

# Create frontend directory
mkdir -p frontend

# Copy frontend files
cp -r ~/Downloads/iyal-frontend/* frontend/

# Verify structure
ls -la frontend/
# Should see: index.html, app.js, etc.
```

Now your structure is:
```
library/
├── frontend/           # HTML, CSS, JS
│   ├── index.html
│   ├── app.js
│   └── styles.css
├── src/               # Spring Boot backend
│   └── main/
├── build.gradle
└── railway.json       # We'll create this
```

---

### Step 2: Create Railway Configuration

Create `railway.json` in the root:

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "./gradlew clean build -x test"
  },
  "deploy": {
    "startCommand": "java -jar build/libs/*.jar",
    "healthcheckPath": "/",
    "healthcheckTimeout": 100,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

---

### Step 3: Update Spring Boot to Serve Frontend

Add this configuration class to serve static files:

Create file: `src/main/java/com/example/library/config/WebConfig.java`

```java
package com.example.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve frontend files from /frontend directory
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "file:frontend/")
                .setCachePeriod(0);
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward root to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
```

---

### Step 4: Update Frontend API URL

In `frontend/app.js`, change line 1 to use relative URLs:

```javascript
// Use relative URL so it works in any environment
const API_URL = window.location.origin;
```

This way:
- Local: `http://localhost:8080`
- Railway: `https://your-app.up.railway.app`

---

### Step 5: Push to GitHub

```bash
cd ~/Downloads/library

# Stage all files
git add .
git commit -m "Monorepo setup for Railway deployment"

# Create GitHub repo: https://github.com/new
# Name: iyal-library

# Push
git remote add origin https://github.com/YOUR_USERNAME/iyal-library.git
git push -u origin main
```

---

### Step 6: Deploy on Railway

1. Go to https://railway.app/new
2. Click "Deploy from GitHub repo"
3. Select `iyal-library`
4. Click "+ New" → "Database" → "PostgreSQL"
5. Add environment variables:
   ```
   JWT_SECRET=your-long-random-secret-key
   CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
   CLOUDINARY_API_KEY=your_cloudinary_api_key
   CLOUDINARY_API_SECRET=your_cloudinary_api_secret
   ALLOWED_ORIGINS=*
   ```
6. Railway will build and deploy everything!

---

### Step 7: Access Your App

Railway will give you a URL like:
```
https://iyal-library-production-xxxx.up.railway.app
```

Visit it - both frontend AND backend are served from the same URL!

- Frontend: `https://your-app.up.railway.app/`
- API: `https://your-app.up.railway.app/books`

---

## ✅ Benefits of Monorepo on Railway

- ✅ Single deployment
- ✅ Single URL for everything
- ✅ No CORS issues (same origin)
- ✅ Simpler to manage
- ✅ Auto-deploys on git push
- ✅ Free tier: 500 hours/month

---

## 🔧 Alternative: Keep Separate Repos

If you prefer separate repos, you can deploy them as separate services:

1. Deploy backend: `iyal-backend` repo
2. In same Railway project, add frontend: `iyal-frontend` repo
3. Update frontend API_URL to point to backend service
4. Update backend CORS to allow frontend domain

---

## 🎯 Which Approach?

**Monorepo (Recommended):**
- ✅ Simpler
- ✅ One deployment
- ✅ No CORS configuration needed

**Separate Repos:**
- ✅ Cleaner separation
- ✅ Can deploy frontend elsewhere (Vercel, Netlify)
- ❌ Need to configure CORS
- ❌ Two deployments to manage

Choose what works best for you!
