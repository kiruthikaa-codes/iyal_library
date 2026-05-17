# iyal - Deployment Guide

## Production Deployment Overview

### Backend (Spring Boot)
**Recommended:** Railway, Render, or AWS Elastic Beanstalk

### Frontend (Next.js)
**Recommended:** Vercel (easiest), Netlify, or Cloudflare Pages

### Database
**Recommended:** Railway PostgreSQL, Supabase, or AWS RDS

---

## Option 1: Railway (Easiest Full-Stack)

### Deploy Backend

1. **Create Railway Account**
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub

2. **Create New Project**
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Connect your repository
   - Select the `library` folder

3. **Add PostgreSQL**
   - Click "New" → "Database" → "PostgreSQL"
   - Railway will automatically provision a database

4. **Configure Environment Variables**
   ```
   DB_USERNAME=${{Postgres.PGUSER}}
   DB_PASSWORD=${{Postgres.PGPASSWORD}}
   JWT_SECRET=your-production-jwt-secret-at-least-256-bits-long
   CLOUDINARY_CLOUD_NAME=your-cloud-name
   CLOUDINARY_API_KEY=your-api-key
   CLOUDINARY_API_SECRET=your-api-secret
   ALLOWED_ORIGINS=https://your-frontend-url.vercel.app
   ```

5. **Update Database URL**
   - Railway auto-connects, but verify in `application.yaml`:
   ```yaml
   spring:
     datasource:
       url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/iyal_db}
   ```

6. **Deploy**
   - Railway auto-deploys on push
   - Get your backend URL: `https://your-app.railway.app`

### Deploy Frontend

1. **Push to GitHub**
   ```bash
   cd iyal-frontend
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin <your-repo-url>
   git push -u origin main
   ```

2. **Deploy to Vercel**
   - Go to [vercel.com](https://vercel.com)
   - Click "New Project"
   - Import from GitHub
   - Configure:
     - Framework: Next.js
     - Root Directory: `iyal-frontend`
     - Build Command: `npm run build`
     - Environment Variables:
       ```
       NEXT_PUBLIC_API_URL=https://your-backend.railway.app
       ```

3. **Deploy**
   - Click "Deploy"
   - Get your URL: `https://your-app.vercel.app`

4. **Update Backend CORS**
   - Update Railway backend env:
   ```
   ALLOWED_ORIGINS=https://your-app.vercel.app
   ```

---

## Option 2: Render

### Backend

1. **Create Render Account** at [render.com](https://render.com)

2. **Create PostgreSQL Database**
   - New → PostgreSQL
   - Name: `iyal-db`
   - Copy the "Internal Database URL"

3. **Create Web Service**
   - New → Web Service
   - Connect GitHub repository
   - Configure:
     - Name: `iyal-backend`
     - Environment: `Java`
     - Build Command: `./gradlew build`
     - Start Command: `java -jar build/libs/library-0.0.1-SNAPSHOT.jar`
     - Instance Type: Free

4. **Environment Variables**
   ```
   DB_USERNAME=iyal_db_user
   DB_PASSWORD=<from-render-postgres>
   JWT_SECRET=<your-secret>
   CLOUDINARY_CLOUD_NAME=<your-cloud>
   CLOUDINARY_API_KEY=<your-key>
   CLOUDINARY_API_SECRET=<your-secret>
   ALLOWED_ORIGINS=https://your-frontend.vercel.app
   ```

---

## Option 3: Docker Deployment

### Backend Dockerfile

```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/library-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose (Local Development)

```yaml
# docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: iyal_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/iyal_db
      JWT_SECRET: development-secret-key-at-least-256-bits-long
      CLOUDINARY_CLOUD_NAME: ${CLOUDINARY_CLOUD_NAME}
      CLOUDINARY_API_KEY: ${CLOUDINARY_API_KEY}
      CLOUDINARY_API_SECRET: ${CLOUDINARY_API_SECRET}
      ALLOWED_ORIGINS: http://localhost:3000
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Run with:
```bash
docker-compose up -d
```

---

## Production Checklist

### Backend
- [ ] Change JWT_SECRET to strong random value
- [ ] Update ALLOWED_ORIGINS to production frontend URL
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` in production
- [ ] Enable HTTPS
- [ ] Set up database backups
- [ ] Configure logging
- [ ] Set up monitoring (e.g., Sentry)
- [ ] Review security headers

### Frontend
- [ ] Update NEXT_PUBLIC_API_URL to production backend
- [ ] Enable production optimizations
- [ ] Set up analytics (optional)
- [ ] Configure error tracking
- [ ] Test all user flows
- [ ] Optimize images

### Database
- [ ] Set up automated backups
- [ ] Configure connection pooling
- [ ] Set up read replicas (if needed)
- [ ] Monitor query performance

### Cloudinary
- [ ] Review storage limits
- [ ] Set up image transformations
- [ ] Configure upload presets
- [ ] Enable auto-moderation (optional)

---

## Environment Variables Summary

### Backend (.env)
```bash
DB_USERNAME=postgres
DB_PASSWORD=<secure-password>
JWT_SECRET=<256-bit-random-string>
CLOUDINARY_CLOUD_NAME=<your-cloud>
CLOUDINARY_API_KEY=<your-key>
CLOUDINARY_API_SECRET=<your-secret>
ALLOWED_ORIGINS=https://your-frontend.com
```

### Frontend (.env.local)
```bash
NEXT_PUBLIC_API_URL=https://your-backend.com
```

---

## Post-Deployment

### Create Admin User

1. Register a user via the app
2. Connect to production database
3. Run:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@yourdomain.com';
```

### Test Everything

- [ ] User registration
- [ ] User login
- [ ] Browse books
- [ ] Search functionality
- [ ] Borrow/return books
- [ ] Reading status updates
- [ ] Profile visibility
- [ ] Admin: Add book with image
- [ ] Admin: Edit book
- [ ] Admin: View analytics
- [ ] Admin: Manage users

---

## Monitoring

### Backend Health Check
```bash
curl https://your-backend.com/actuator/health
```

### Frontend
- Monitor Vercel dashboard
- Check build logs
- Review runtime logs

### Database
- Monitor connection count
- Check query performance
- Review slow query logs

---

## Scaling Considerations

### When to Scale

- **Backend:** When response times > 500ms consistently
- **Database:** When connection pool is exhausted
- **Frontend:** Vercel auto-scales

### How to Scale

1. **Vertical:** Increase instance size on Railway/Render
2. **Horizontal:** Add more backend instances with load balancer
3. **Database:** Enable connection pooling, add read replicas
4. **CDN:** Cloudinary already provides CDN for images

---

## Troubleshooting

### CORS Errors
- Verify `ALLOWED_ORIGINS` includes frontend URL
- Check protocol (http vs https)
- Ensure no trailing slashes

### Database Connection
- Verify DATABASE_URL is correct
- Check firewall rules
- Confirm database is running

### Image Upload Fails
- Verify Cloudinary credentials
- Check file size limits
- Review Cloudinary dashboard

---

**Ready for production!** 🚀

For support, check the main README.md or open an issue.
