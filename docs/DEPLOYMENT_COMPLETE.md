# 🚀 Complete Deployment Guide

## Urban Air Quality Monitoring System

This guide provides step-by-step instructions for deploying the complete Air Quality Monitoring System with weather integration.

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Quick Start with Docker](#quick-start-with-docker)
3. [Manual Deployment](#manual-deployment)
4. [Production Deployment](#production-deployment)
5. [Environment Configuration](#environment-configuration)
6. [Troubleshooting](#troubleshooting)
7. [Monitoring & Maintenance](#monitoring--maintenance)

---

## Prerequisites

### Required Software

- **Java 21 LTS** - `java --version` should show version 21
- **Maven 3.8+** - For building from source
- **Docker & Docker Compose** - For containerized deployment
- **Git** - For cloning the repository

### Optional

- **MySQL 8.0+** - For production database (H2 is used by default for development)
- **Node.js** - If you want to serve frontend with a dev server

---

## 🐳 Quick Start with Docker

The fastest way to get everything running!

### Step 1: Clone or Navigate to Project

```bash
cd d:/IP
```

### Step 2: Start All Services

```bash
docker-compose up -d
```

This will:

- Build the Spring Boot backend (Java 21)
- Start the backend API on port 8080
- Start Nginx web server on port 80
- Configure all necessary environment variables

### Step 3: Access the Application

- **Frontend Dashboard**: http://localhost
- **Alternative UI**: http://localhost/air2.html
- **Backend API**: http://localhost:8080/api
- **H2 Database Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health

### Step 4: Verify It's Working

```bash
# Check running containers
docker-compose ps

# View backend logs
docker-compose logs -f air-quality-backend

# View nginx logs
docker-compose logs -f nginx
```

### Stop Services

```bash
docker-compose down
```

---

## 🛠️ Manual Deployment

If you prefer to run services manually without Docker.

### Backend Deployment

#### Step 1: Build the Application

```bash
cd air-quality-backend

# Set JAVA_HOME to Java 21
$env:JAVA_HOME="C:\Users\Shashidhar\.jdk\jdk-21.0.8"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"

# Build with Maven
mvn clean package -DskipTests
```

#### Step 2: Run the Application

```bash
# Run the JAR file
java -jar target/air-quality-backend-1.0.0.jar
```

Or use Maven:

```bash
mvn spring-boot:run
```

#### Step 3: Verify Backend is Running

Open browser to:

- http://localhost:8080/api/air-quality/latest
- http://localhost:8080/api/weather/city/London

You should see JSON responses.

### Frontend Deployment

#### Option 1: Simple HTTP Server (Python)

```bash
cd d:/IP
python -m http.server 5500
```

Access at: http://localhost:5500/index.html

#### Option 2: Live Server (VS Code Extension)

1. Install "Live Server" extension in VS Code
2. Right-click `index.html` → "Open with Live Server"
3. Automatically opens at: http://127.0.0.1:5500/index.html

#### Option 3: Nginx (Production)

```bash
# Copy files to nginx html directory
cp index.html /usr/share/nginx/html/
cp air2.html /usr/share/nginx/html/

# Restart nginx
nginx -s reload
```

---

## 🌐 Production Deployment

### Using MySQL Database

1. **Install MySQL**:

   ```bash
   docker run -d \
     --name mysql \
     -e MYSQL_ROOT_PASSWORD=rootpassword \
     -e MYSQL_DATABASE=air_quality_db \
     -e MYSQL_USER=airquality \
     -e MYSQL_PASSWORD=airquality123 \
     -p 3306:3306 \
     mysql:8.0
   ```

2. **Update `application.properties`**:

   ```properties
   # Comment out H2 configuration
   # spring.datasource.url=jdbc:h2:mem:airqualitydb

   # Uncomment MySQL configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/air_quality_db
   spring.datasource.username=airquality
   spring.datasource.password=airquality123
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   ```

3. **Rebuild and run**:
   ```bash
   mvn clean package -DskipTests
   java -jar target/air-quality-backend-1.0.0.jar
   ```

### Environment Variables

For production, use environment variables instead of hardcoding:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/air_quality_db
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export WEATHER_API_KEY=your_openweather_api_key
export AIRQUALITY_CORS_ALLOWED_ORIGINS=https://your-domain.com

java -jar air-quality-backend-1.0.0.jar
```

### Cloud Deployment

#### AWS Elastic Beanstalk

1. Create application.yml for environment-specific config
2. Create Procfile:
   ```
   web: java -jar target/air-quality-backend-1.0.0.jar
   ```
3. Deploy:
   ```bash
   eb init
   eb create production-env
   eb deploy
   ```

#### Heroku

1. Create Procfile:
   ```
   web: java -Dserver.port=$PORT -jar target/air-quality-backend-1.0.0.jar
   ```
2. Deploy:
   ```bash
   heroku create air-quality-monitor
   git push heroku main
   ```

#### Azure App Service

1. Build JAR file
2. Create App Service
3. Deploy using Azure CLI:
   ```bash
   az webapp up --name air-quality-monitor --resource-group myResourceGroup
   ```

---

## ⚙️ Environment Configuration

### Backend Configuration Options

| Environment Variable              | Default                          | Description                      |
| --------------------------------- | -------------------------------- | -------------------------------- |
| `SERVER_PORT`                     | 8080                             | Backend server port              |
| `SPRING_DATASOURCE_URL`           | jdbc:h2:mem:airqualitydb         | Database connection URL          |
| `WEATHER_API_KEY`                 | da40a01545734ca7e86b2574509f30f1 | OpenWeatherMap API key           |
| `AIRQUALITY_SIMULATION_ENABLED`   | true                             | Enable simulated data generation |
| `AIRQUALITY_SIMULATION_INTERVAL`  | 30000                            | Data generation interval (ms)    |
| `AIRQUALITY_ALERT_ENABLED`        | true                             | Enable alert checking            |
| `AIRQUALITY_CORS_ALLOWED_ORIGINS` | \*                               | Allowed CORS origins             |

### Frontend Configuration

Edit `index.html` line ~673:

```javascript
const API_BASE_URL = "http://localhost:8080"; // Change to your backend URL
```

For production, set to your actual backend URL:

```javascript
const API_BASE_URL = "https://api.your-domain.com";
```

---

## 🔧 Troubleshooting

### Backend Won't Start

**Issue**: `UnsupportedClassVersionError`

**Solution**: Ensure you're using Java 21

```bash
java --version
# Should show: openjdk 21.x.x
```

Set JAVA_HOME:

```powershell
$env:JAVA_HOME="C:\Users\Shashidhar\.jdk\jdk-21.0.8"
```

---

**Issue**: `Port 8080 is already in use`

**Solution**: Change port or kill process

```bash
# Change port in application.properties
server.port=8081

# Or find and kill process using port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

**Issue**: Database connection failed

**Solution for H2**:

- Ensure `spring.sql.init.mode=never` in application.properties
- Delete `schema.sql` or comment out MySQL-specific SQL

**Solution for MySQL**:

- Verify MySQL is running: `mysql -u root -p`
- Check connection details in application.properties
- Create database: `CREATE DATABASE air_quality_db;`

---

### Frontend Shows No Data

**Issue**: API requests failing with CORS errors

**Solution**: Update CORS configuration in `application.properties`

```properties
airquality.cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500,YOUR_FRONTEND_URL
```

---

**Issue**: "Failed to load air quality data"

**Solution**:

1. Check backend is running: http://localhost:8080/actuator/health
2. Test API directly: http://localhost:8080/api/air-quality/latest
3. Check browser console for errors (F12)
4. Verify API_BASE_URL in index.html matches your backend URL

---

### Docker Issues

**Issue**: Docker build fails

**Solution**:

```bash
# Clean rebuild
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

---

**Issue**: Container keeps restarting

**Solution**: Check logs

```bash
docker-compose logs -f air-quality-backend
```

Common causes:

- Database connection issues
- Port conflicts
- Missing environment variables

---

### Weather API Issues

**Issue**: Weather data not loading

**Solution**:

1. Verify API key is set: Check `application.properties`
2. Test API directly:
   ```bash
   curl "http://api.openweathermap.org/data/2.5/weather?q=London&appid=YOUR_API_KEY&units=metric"
   ```
3. Check rate limits (60 calls/minute for free tier)
4. Ensure internet connectivity from backend

---

## 📊 Monitoring & Maintenance

### Health Checks

**Actuator Endpoints**:

- `/actuator/health` - Overall health status
- `/actuator/info` - Application information
- `/actuator/metrics` - System metrics

```bash
curl http://localhost:8080/actuator/health
```

### Database Monitoring

**H2 Console**:

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:airqualitydb`
- Username: `sa`
- Password: (leave empty)

**MySQL**:

```bash
mysql -u airquality -p
USE air_quality_db;
SHOW TABLES;
SELECT COUNT(*) FROM air_quality;
```

### Log Management

**View Logs**:

```bash
# Real-time logs
tail -f logs/application.log

# Docker logs
docker-compose logs -f air-quality-backend

# Last 100 lines
docker-compose logs --tail=100 air-quality-backend
```

**Log Configuration** in `application.properties`:

```properties
logging.level.root=INFO
logging.level.com.airquality=DEBUG
logging.file.name=logs/application.log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n
```

### Performance Monitoring

1. **JVM Metrics**:

   ```bash
   curl http://localhost:8080/actuator/metrics/jvm.memory.used
   curl http://localhost:8080/actuator/metrics/http.server.requests
   ```

2. **Database Queries**:
   - Enable SQL logging: `spring.jpa.show-sql=true`
   - View slow queries in logs

3. **API Response Times**:
   - Check browser Network tab
   - Use tools like Postman or curl with `-w` option

### Backup & Recovery

**H2 Database** (Development):

- Data is in-memory, lost on restart
- For persistence, use file-based H2:
  ```properties
  spring.datasource.url=jdbc:h2:file:./data/airqualitydb
  ```

**MySQL Database** (Production):

```bash
# Backup
mysqldump -u airquality -p air_quality_db > backup_$(date +%Y%m%d).sql

# Restore
mysql -u airquality -p air_quality_db < backup_20260220.sql
```

### Updating the Application

1. **Pull latest code**:

   ```bash
   git pull origin main
   ```

2. **Rebuild**:

   ```bash
   mvn clean package -DskipTests
   ```

3. **Restart**:

   ```bash
   # Manual
   # Stop: Ctrl+C
   # Start: java -jar target/air-quality-backend-1.0.0.jar

   # Docker
   docker-compose down
   docker-compose build
   docker-compose up -d
   ```

---

## 🔒 Security Considerations

### Production Checklist

- [ ] Change default database passwords
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS/SSL certificates
- [ ] Configure proper CORS origins (not `*`)
- [ ] Disable H2 console in production
- [ ] Set `spring.jpa.show-sql=false` in production
- [ ] Use strong API keys
- [ ] Implement rate limiting
- [ ] Enable Spring Security
- [ ] Regular security updates

### Environment Variables for Secrets

Never commit sensitive data to Git. Use:

```bash
# .env file (add to .gitignore)
SPRING_DATASOURCE_PASSWORD=your_secure_password
WEATHER_API_KEY=your_api_key
MAIL_PASSWORD=your_mail_app_password
```

Load in Spring Boot:

```properties
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
weather.api.key=${WEATHER_API_KEY}
```

---

## 📞 Support & Resources

### Documentation

- [Backend API Documentation](./docs/API_DOCUMENTATION.md)
- [Weather API Integration](./docs/WEATHER_API_INTEGRATION.md)
- [Frontend Integration Guide](./docs/FRONTEND_INTEGRATION.md)

### Useful Commands

```bash
# Check application is running
curl http://localhost:8080/actuator/health

# Test air quality API
curl http://localhost:8080/api/air-quality/latest

# Test weather API
curl http://localhost:8080/api/weather/city/London

# Check Docker status
docker-compose ps

# View all logs
docker-compose logs

# Restart single service
docker-compose restart air-quality-backend
```

### Getting Help

If you encounter issues:

1. Check the logs (see Monitoring section)
2. Verify all prerequisites are installed
3. Ensure ports are not in use
4. Check environment variables are set correctly
5. Review the troubleshooting section above

---

## 🎉 Success Indicators

You'll know the deployment is successful when:

- ✅ Backend health check returns: `{"status":"UP"}`
- ✅ Frontend loads at http://localhost or http://localhost:5500
- ✅ Dashboard shows real-time AQI data
- ✅ Weather widget displays current weather
- ✅ Recent readings table populates with data
- ✅ Subscription form submits successfully
- ✅ No errors in browser console (F12)
- ✅ No errors in backend logs

**Congratulations! Your Air Quality Monitoring System is now deployed! 🚀**
