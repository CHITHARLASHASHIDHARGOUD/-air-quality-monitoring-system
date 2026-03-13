# Quick Start Guide
## Get Your Backend Running in 5 Minutes

---

## ⚡ Super Fast Setup

### Step 1: Prerequisites Check (1 minute)

```bash
# Check Java version (must be 17+)
java -version

# Check Maven
mvn -version

# Check MySQL is running
mysql -V
```

**Don't have them?**
- Java 17: [Download here](https://www.oracle.com/java/technologies/downloads/#java17)
- Maven: [Download here](https://maven.apache.org/download.cgi)
- MySQL: [Download here](https://dev.mysql.com/downloads/installer/)

---

### Step 2: Database Setup (1 minute)

```bash
# Open MySQL
mysql -u root -p

# Create database
CREATE DATABASE air_quality_db;

# Verify
SHOW DATABASES;

# Exit
EXIT;
```

---

### Step 3: Configure Application (30 seconds)

Open `air-quality-backend/src/main/resources/application.properties`

**Change only this line:**
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**That's it!** Everything else is pre-configured.

---

### Step 4: Build and Run (2 minutes)

```bash
# Navigate to project
cd air-quality-backend

# Build (first time takes 2-3 minutes to download dependencies)
mvn clean install

# Run
mvn spring-boot:run
```

**Look for this message:**
```
═══════════════════════════════════════════════════════════════
  Urban Air Quality Monitoring System - Backend Started
  API Base URL: http://localhost:8080/api
  Health Check: http://localhost:8080/actuator/health
═══════════════════════════════════════════════════════════════
```

---

### Step 5: Test It! (30 seconds)

**Option 1: Browser**

Open in browser:
```
http://localhost:8080/api/air-quality/current
```

**Option 2: Command Line**

```bash
# Test health
curl http://localhost:8080/actuator/health

# Wait 30 seconds for first data generation, then:
curl http://localhost:8080/api/air-quality/current
```

**Option 3: Postman**

1. Open Postman
2. Create GET request to: `http://localhost:8080/api/air-quality/current`
3. Click Send

---

## 🎉 Success! What Now?

### See Your Data in Database

```sql
mysql -u root -p
USE air_quality_db;

-- View air quality readings
SELECT aqi, category, pm25, recorded_at 
FROM air_quality 
ORDER BY recorded_at DESC 
LIMIT 5;

-- View subscribers (none yet, add via API)
SELECT * FROM subscribers;
```

### Test Subscription

```bash
curl -X POST http://localhost:8080/api/subscribe \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Your Name",
    "email": "your.email@gmail.com",
    "threshold": 150
  }'
```

### Test Alert System

```bash
# Generate pollution spike (AQI will go high)
curl -X POST http://localhost:8080/api/test/pollution-spike

# Wait 30 seconds for next data generation

# Check alert logs
mysql -u root -p
USE air_quality_db;
SELECT * FROM alert_logs ORDER BY sent_at DESC LIMIT 5;
```

---

## 🔗 Connect Your Frontend

### Update your HTML file

Replace the API_BASE_URL in your `air2.html`:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';

// Fetch current data
async function fetchAirQuality() {
  const response = await fetch(`${API_BASE_URL}/air-quality/current`);
  const data = await response.json();
  console.log(data);
  // Update your UI with data
}

// Call every 30 seconds
setInterval(fetchAirQuality, 30000);
fetchAirQuality(); // Initial call
```

**That's it!** Your frontend will now show **real, live data**.

---

## 🐛 Troubleshooting

### Problem: "Port 8080 already in use"

**Solution:** Kill the process or use different port

```bash
# Option 1: Kill process using port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux:
lsof -i :8080
kill -9 <PID>

# Option 2: Use different port
# In application.properties:
server.port=8081
```

### Problem: "Access denied for user 'root'@'localhost'"

**Solution:** Wrong password

Edit `application.properties`:
```properties
spring.datasource.password=YOUR_CORRECT_PASSWORD
```

### Problem: "No data available"

**Solution:** Wait 30 seconds for scheduler to generate first data

Or force generation:
```bash
curl -X POST http://localhost:8080/api/test/generate-now
```

### Problem: CORS error in browser

**Solution:** Add your frontend origin to `application.properties`:

```properties
airquality.cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500
```

---

## 📁 File Locations

```
Where is everything?

Configuration:
→ air-quality-backend/src/main/resources/application.properties

Main Application:
→ air-quality-backend/src/main/java/com/airquality/AirQualityApplication.java

Controllers (API endpoints):
→ air-quality-backend/src/main/java/com/airquality/controller/

Services (business logic):
→ air-quality-backend/src/main/java/com/airquality/service/

Database entities:
→ air-quality-backend/src/main/java/com/airquality/entity/

Logs:
→ Console output (or check logs/ folder if configured)
```

---

## 🚀 Next Steps

1. ✅ **Backend is running** - You're done with this guide!

2. 📱 **Connect Frontend** - See [FRONTEND_INTEGRATION.md](FRONTEND_INTEGRATION.md)

3. ☁️ **Deploy to Cloud** - See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

4. 🌐 **Use Real API** - See [REAL_API_INTEGRATION.md](REAL_API_INTEGRATION.md)

5. 📧 **Add Email Alerts** - Uncomment email code in `AlertService.java`

---

## 🎯 Quick Reference

### Start Backend
```bash
mvn spring-boot:run
```

### Stop Backend
```
Ctrl + C
```

### Rebuild After Changes
```bash
mvn clean install
mvn spring-boot:run
```

### Check if Running
```bash
curl http://localhost:8080/actuator/health
```

### View Logs
Check console where you ran `mvn spring-boot:run`

---

## 💡 Tips

1. **First run takes longer** - Maven downloads dependencies
2. **Data generates every 30 seconds** - Be patient
3. **Keep backend running** - Stop with Ctrl+C
4. **Test with Postman** - Easier than curl for complex requests
5. **Check database** - Verify data is being saved

---

## ✅ Checklist

- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] MySQL installed and running
- [ ] Database `air_quality_db` created
- [ ] Password configured in `application.properties`
- [ ] `mvn spring-boot:run` executed successfully
- [ ] API responding at http://localhost:8080
- [ ] Data generating every 30 seconds
- [ ] Frontend connected (optional)

---

## 🎊 Congratulations!

Your **Urban Air Quality Monitoring Backend** is now running!

**What you have:**
- ✅ REST API with air quality data
- ✅ Real-time data generation (30s intervals)  
- ✅ Subscription system
- ✅ Alert checking
- ✅ Database with automatic cleanup
- ✅ Production-ready architecture

**Ready for the next level?** Check out the `/docs` folder for:
- Frontend integration
- Cloud deployment
- Real API integration
- Email notifications

---

**Need Help?** Check the [README.md](../README.md) or [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
