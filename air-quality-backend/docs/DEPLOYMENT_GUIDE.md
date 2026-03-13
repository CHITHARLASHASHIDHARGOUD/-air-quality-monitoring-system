# Deployment Guide
## Urban Air Quality Monitoring System

---

## 🎯 Deployment Options

1. **Local Development** (Localhost)
2. **Render.com** (Recommended - Free tier available)
3. **Railway.app** (Alternative - Free tier)
4. **Heroku** (Paid)
5. **AWS / Azure / GCP** (Enterprise)

---

## 🏠 Option 1: Local Development

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### Step 1: Install MySQL

**Windows:**
1. Download MySQL Installer from [mysql.com](https://dev.mysql.com/downloads/installer/)
2. Install MySQL Server 8.0
3. Set root password during installation
4. Start MySQL service

**macOS:**
```bash
brew install mysql
brew services start mysql
```

**Linux:**
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
```

### Step 2: Create Database

```sql
mysql -u root -p

CREATE DATABASE air_quality_db;
USE air_quality_db;

-- Tables will be created automatically by Hibernate
EXIT;
```

### Step 3: Configure Application

Edit `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/air_quality_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 4: Build and Run

```bash
cd air-quality-backend

# Clean and build
mvn clean install

# Run application
mvn spring-boot:run
```

### Step 5: Verify

Open browser:
- API: http://localhost:8080/api/air-quality/current
- Health: http://localhost:8080/actuator/health

---

## ☁️ Option 2: Deploy to Render.com (Recommended)

### Why Render?
✅ Free tier available  
✅ Automatic deployments from Git  
✅ Built-in MySQL database  
✅ Easy SSL/HTTPS  
✅ No credit card required for free tier  

### Step 1: Prepare Your Project

Create `.gitignore`:
```
target/
*.class
*.jar
.idea/
.vscode/
*.log
application-local.properties
```

Commit to GitHub:
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/air-quality-backend.git
git push -u origin main
```

### Step 2: Create Render Account

1. Go to [render.com](https://render.com)
2. Sign up with GitHub
3. Authorize Render to access your repositories

### Step 3: Create MySQL Database

1. Click "New +" → "MySQL"
2. Name: `air-quality-db`
3. Database: `air_quality_db`
4. User: `airquality_user`
5. Region: Choose closest to your users
6. Plan: Free
7. Click "Create Database"

**Save these values:**
- Internal Database URL (starts with `mysql://`)
- Host
- Port
- Database
- Username
- Password

### Step 4: Create Web Service

1. Click "New +" → "Web Service"
2. Connect your GitHub repository
3. Configure:
   - **Name:** `air-quality-backend`
   - **Region:** Same as database
   - **Branch:** `main`
   - **Root Directory:** Leave empty
   - **Build Command:** `mvn clean install`
   - **Start Command:** `java -jar target/air-quality-backend-1.0.0.jar`

### Step 5: Add Environment Variables

In Render dashboard, go to "Environment" tab:

```
SPRING_DATASOURCE_URL=<Internal_Database_URL>
SPRING_DATASOURCE_USERNAME=<Database_Username>
SPRING_DATASOURCE_PASSWORD=<Database_Password>
SPRING_JPA_HIBERNATE_DDL_AUTO=update
AIRQUALITY_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

### Step 6: Deploy

1. Click "Create Web Service"
2. Wait for build and deployment (5-10 minutes)
3. Your API will be available at: `https://your-service-name.onrender.com`

### Step 7: Update Frontend

Update API base URL in your HTML:

```javascript
const API_BASE_URL = 'https://your-service-name.onrender.com/api';
```

---

## 🚂 Option 3: Deploy to Railway.app

### Step 1: Install Railway CLI

```bash
npm install -g @railway/cli
```

### Step 2: Login

```bash
railway login
```

### Step 3: Initialize Project

```bash
cd air-quality-backend
railway init
```

### Step 4: Add MySQL Database

```bash
railway add mysql
```

### Step 5: Deploy

```bash
railway up
```

### Step 6: Set Environment Variables

```bash
railway variables set SPRING_DATASOURCE_URL=<mysql-url>
railway variables set AIRQUALITY_CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

### Step 7: Generate Domain

```bash
railway domain
```

Your API is now live!

---

## 🐳 Option 4: Docker Deployment

### Create Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/air-quality-backend-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Create docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: air_quality_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/air_quality_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword
    depends_on:
      - mysql

volumes:
  mysql_data:
```

### Build and Run

```bash
# Build JAR
mvn clean package

# Build and start containers
docker-compose up -d

# Check logs
docker-compose logs -f backend

# Stop
docker-compose down
```

---

## 🌐 Frontend Deployment Options

### Option 1: GitHub Pages (Static)

1. Push `air2.html` to GitHub repository
2. Go to Settings → Pages
3. Select branch and folder
4. Your frontend will be at: `https://username.github.io/repo-name`

### Option 2: Netlify (Recommended)

1. Go to [netlify.com](https://netlify.com)
2. Drag and drop your HTML file
3. Instant deployment with custom domain support

### Option 3: Vercel

```bash
npm install -g vercel
vercel
```

---

## 🔒 Production Checklist

### Security

- [ ] Change default MySQL password
- [ ] Configure specific CORS origins (remove `*`)
- [ ] Enable HTTPS/SSL
- [ ] Set up environment variables (never commit passwords)
- [ ] Configure firewall rules
- [ ] Enable Spring Security (optional)

### Performance

- [ ] Configure connection pooling
- [ ] Set up database indexes (already done)
- [ ] Enable response compression
- [ ] Configure caching headers

### Monitoring

- [ ] Set up application logs
- [ ] Monitor Actuator endpoints
- [ ] Set up error alerting
- [ ] Database backup strategy

### Configuration

Update `application.properties` for production:

```properties
# Production database
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# CORS - specific origins only
airquality.cors.allowed-origins=${ALLOWED_ORIGINS}

# Logging
logging.level.root=INFO
logging.level.com.airquality=INFO

# Actuator - restrict endpoints
management.endpoints.web.exposure.include=health,info
```

---

## 📊 Database Migration (Development to Production)

### Export Data

```bash
mysqldump -u root -p air_quality_db > backup.sql
```

### Import to Production

```bash
mysql -u username -p production_db < backup.sql
```

---

## 🧪 Testing Deployment

### 1. Test API Endpoints

```bash
# Health check
curl https://your-api-url.com/actuator/health

# Get current air quality
curl https://your-api-url.com/api/air-quality/current

# Test subscription
curl -X POST https://your-api-url.com/api/subscribe \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","threshold":100}'
```

### 2. Test from Frontend

Open browser console and run:

```javascript
fetch('https://your-api-url.com/api/air-quality/current')
  .then(r => r.json())
  .then(d => console.log(d));
```

### 3. Verify Database

```sql
-- Check data is being generated
SELECT COUNT(*) FROM air_quality;

-- Check recent readings
SELECT * FROM air_quality ORDER BY recorded_at DESC LIMIT 5;

-- Check subscribers
SELECT * FROM subscribers;

-- Check alerts
SELECT * FROM alert_logs ORDER BY sent_at DESC LIMIT 10;
```

---

## 🆘 Troubleshooting

### Problem: Port already in use

```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Problem: Database connection failed

1. Check MySQL is running: `systemctl status mysql`
2. Verify credentials in `application.properties`
3. Test connection: `mysql -u root -p`
4. Check firewall: `sudo ufw allow 3306`

### Problem: Build fails

```bash
# Clear Maven cache
mvn clean

# Rebuild
mvn clean install -DskipTests

# Check Java version
java -version  # Should be 17+
```

### Problem: CORS errors in production

Update allowed origins:

```properties
airquality.cors.allowed-origins=https://your-frontend-domain.com,https://www.your-frontend-domain.com
```

---

## 📈 Scaling for Production

### Increase Database Connections

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### Add Caching

```java
@EnableCaching
public class AirQualityApplication { }

@Cacheable("airQuality")
public AirQualityResponse getCurrentAirQuality() { }
```

### Load Balancing

Use Nginx as reverse proxy:

```nginx
upstream backend {
    server localhost:8080;
    server localhost:8081;
}

server {
    location /api {
        proxy_pass http://backend;
    }
}
```

---

## ✅ Deployment Success!

Your Urban Air Quality Monitoring System is now live! 🎉

**Next Steps:**
1. Monitor application logs
2. Test all features
3. Set up email alerts (see Email Integration guide)
4. Integrate with real AQI API (see API Integration guide)
