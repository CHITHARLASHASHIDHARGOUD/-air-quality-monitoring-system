# 🌍 Urban Air Quality Monitoring System

### Real-Time Air Quality & Weather Monitoring Platform

![Java](https://img.shields.io/badge/Java-21_LTS-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.2-green?style=for-the-badge&logo=springboot)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

> A comprehensive full-stack application for monitoring urban air quality with weather integration, real-time alerts, and citizen subscriptions.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Screenshots](#screenshots)
- [API Endpoints](#api-endpoints)
- [Documentation](#documentation)
- [Development](#development)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

The Urban Air Quality Monitoring System is a production-ready application that provides real-time air quality data, weather information, and automated citizen alerts. Built with Java 21 and Spring Boot 3, it features a modern web interface, RESTful APIs, and comprehensive monitoring capabilities.

### Key Highlights

- ✅ **Java 21 LTS** - Latest long-term support version with modern features
- 🌦️ **Weather Integration** - Real-time weather data from OpenWeatherMap API
- 📊 **Real-Time Monitoring** - Live AQI updates with pollutant measurements
- 🔔 **Alert System** - Citizen subscriptions with email notifications
- 🎨 **Modern UI** - Responsive dashboard with glassmorphic design
- 🐳 **Docker Ready** - One-command deployment with Docker Compose
- 📱 **Mobile Responsive** - Works seamlessly on all devices
- 🔒 **Production Ready** - Health checks, logging, and monitoring built-in

---

## ✨ Features

### Air Quality Monitoring

- **Real-time AQI Calculation** - Instant air quality index with color-coded categories
- **Pollutant Tracking** - PM2.5, PM10, CO, NO₂, O₃ measurements
- **Historical Data** - Track air quality trends over time
- **Health Recommendations** - Automated health advisories based on AQI levels

### Weather Integration

- **Current Weather** - Temperature, humidity, wind speed, pressure
- **City Search** - Get weather for any city worldwide
- **Coordinate-based** - Weather data by latitude/longitude
- **Combined Data** - Merged air quality and weather information

### Citizen Services

- **Alert Subscriptions** - Users can subscribe for AQI threshold alerts
- **Email Notifications** - Automatic alerts when air quality deteriorates
- **Customizable Thresholds** - Subscribe to specific AQI levels
- **Active/Inactive Management** - User-controlled subscription status

### Data Simulation

- **Realistic Data Generation** - Simulated sensor data for testing
- **Configurable Intervals** - Adjustable data generation frequency
- **Automatic Cleanup** - Maintains optimal database size

### Developer Features

- **RESTful APIs** - Comprehensive JSON-based endpoints
- **Swagger Documentation** - Interactive API documentation
- **Actuator Endpoints** - Health checks and metrics
- **CORS Support** - Frontend integration ready
- **Detailed Logging** - Debug-level logging for development

---

## 🛠️ Tech Stack

### Backend

- **Java 21 LTS** - Modern Java with latest features
- **Spring Boot 3.2.2** - Enterprise application framework
- **Spring Data JPA** - Database abstraction layer
- **Spring WebFlux** - Reactive HTTP client for external APIs
- **Hibernate** - ORM for database operations
- **Lombok** - Reduced boilerplate code
- **Maven** - Dependency management and build tool

### Database

- **H2 Database** - In-memory database for development
- **MySQL 8** - Production-ready relational database (optional)
- **JPA/Hibernate** - Database migrations and schema management

### Frontend

- **HTML5** - Modern semantic markup
- **CSS3** - Glassmorphism design with animations
- **Vanilla JavaScript** - No frameworks, pure ES6+
- **Fetch API** - RESTful API consumption
- **Responsive Design** - Mobile-first approach

### External APIs

- **OpenWeatherMap API** - Real-time weather data
- **RESTful Integration** - Weather data for 200,000+ cities

### DevOps & Tools

- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Git** - Version control
- **Maven Wrapper** - Consistent build environment
- **Actuator** - Application monitoring

---

## 📁 Project Structure

```
d:/IP/
├── air-quality-backend/          # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/airquality/
│   │   │   │   ├── AirQualityApplication.java
│   │   │   │   ├── config/               # Configuration classes
│   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   └── WebClientConfig.java
│   │   │   │   ├── controller/           # REST Controllers
│   │   │   │   │   ├── AirQualityController.java
│   │   │   │   │   ├── SubscriptionController.java
│   │   │   │   │   ├── WeatherController.java
│   │   │   │   │   └── TestController.java
│   │   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   │   │   ├── AirQualityResponse.java
│   │   │   │   │   ├── WeatherResponse.java
│   │   │   │   │   ├── CombinedDataResponse.java
│   │   │   │   │   └── SubscriptionRequest.java
│   │   │   │   ├── entity/               # JPA Entities
│   │   │   │   │   ├── AirQuality.java
│   │   │   │   │   ├── Subscriber.java
│   │   │   │   │   └── AlertLog.java
│   │   │   │   ├── repository/           # Data Repositories
│   │   │   │   │   ├── AirQualityRepository.java
│   │   │   │   │   ├── SubscriberRepository.java
│   │   │   │   │   └── AlertLogRepository.java
│   │   │   │   ├── service/              # Business Logic
│   │   │   │   │   ├── AirQualityService.java
│   │   │   │   │   ├── WeatherService.java
│   │   │   │   │   ├── AlertService.java
│   │   │   │   │   ├── SubscriberService.java
│   │   │   │   │   ├── DataSimulatorService.java
│   │   │   │   │   └── AQICategoryService.java
│   │   │   │   └── exception/            # Exception Handling
│   │   │   │       ├── GlobalExceptionHandler.java
│   │   │   │       ├── ResourceNotFoundException.java
│   │   │   │       └── DuplicateResourceException.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── schema.sql
│   │   └── test/                         # Unit Tests
│   ├── docs/                             # Documentation
│   │   ├── API_DOCUMENTATION.md
│   │   ├── DEPLOYMENT_GUIDE.md
│   │   ├── FRONTEND_INTEGRATION.md
│   │   ├── REAL_API_INTEGRATION.md
│   │   ├── WEATHER_API_INTEGRATION.md
│   │   └── WEATHER_QUICK_START.md
│   ├── pom.xml                           # Maven configuration
│   ├── Dockerfile                        # Docker build
│   ├── .dockerignore
│   └── README.md
│
├── index.html                            # Modern Frontend Dashboard
├── air2.html                             # Alternative UI
├── docker-compose.yml                    # Docker orchestration
├── DEPLOYMENT_COMPLETE.md                # Deployment guide
└── README.md                             # This file
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 21 LTS** ([Download](https://adoptium.net/))
- **Maven 3.8+** (or use included Maven Wrapper)
- **Docker Desktop** (optional, for containerized deployment)

### Option 1: Docker (Recommended)

```bash
# Clone or navigate to project
cd d:/IP

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080/api
# H2 Console: http://localhost:8080/h2-console
```

### Option 2: Manual Setup

```bash
# Navigate to backend
cd air-quality-backend

# Build the application
mvn clean package -DskipTests

# Run the backend
java -jar target/air-quality-backend-1.0.0.jar

# In another terminal, serve the frontend
cd ..
python -m http.server 5500

# Access frontend at: http://localhost:5500/index.html
```

### Verify Installation

1. **Backend Health Check**: http://localhost:8080/actuator/health
   - Should return: `{"status":"UP"}`

2. **Get Latest AQI**: http://localhost:8080/api/air-quality/latest
   - Should return JSON with air quality data

3. **Get Weather**: http://localhost:8080/api/weather/city/London
   - Should return weather data for London

4. **Open Dashboard**: http://localhost or http://localhost:5500/index.html
   - Should display real-time dashboard

---

## 📸 Screenshots

### Main Dashboard

Modern, responsive interface with real-time air quality and weather data.

**Features shown:**

- Large AQI display with color-coded categories
- Pollutant measurements grid (PM2.5, PM10, CO, NO₂, O₃)
- Weather widget with current conditions
- Recent readings table
- Alert subscription form

### Key UI Elements

- **Glassmorphic Design** - Modern, translucent card-based layout
- **Gradient Backgrounds** - Eye-catching purple gradient theme
- **Real-time Updates** - Auto-refresh every 30 seconds
- **Responsive Layout** - Adapts to mobile, tablet, and desktop
- **Interactive Elements** - Hover effects and smooth animations

---

## 🔌 API Endpoints

### Air Quality Endpoints

```http
GET /api/air-quality/latest
# Returns the most recent air quality reading

GET /api/air-quality/recent?limit=10
# Returns recent air quality readings (default: 10)

GET /api/air-quality/{id}
# Get specific air quality reading by ID
```

### Weather Endpoints

```http
GET /api/weather/current
# Get current weather for configured default location

GET /api/weather/city/{cityName}
# Get weather by city name (e.g., London, Paris, Tokyo)

GET /api/weather/coordinates?latitude=40.7128&longitude=-74.0060
# Get weather by coordinates

GET /api/weather/combined
# Get combined air quality + weather data

GET /api/weather/combined/city/{cityName}
# Get combined data for specific city
```

### Subscription Endpoints

```http
POST /api/subscriptions
Content-Type: application/json
{
  "name": "John Doe",
  "email": "john@example.com",
  "threshold": 100
}
# Subscribe for alerts

GET /api/subscriptions
# Get all active subscriptions

DELETE /api/subscriptions/{id}
# Unsubscribe
```

### Health & Monitoring

```http
GET /actuator/health
# Application health status

GET /actuator/info
# Application information

GET /actuator/metrics
# Application metrics
```

### Example Responses

**Air Quality Response:**

```json
{
  "success": true,
  "message": "Air quality data retrieved successfully",
  "data": {
    "id": 1,
    "aqi": 85,
    "category": "Moderate",
    "healthMessage": "Acceptable; sensitive groups should take caution.",
    "pm25": 28.5,
    "pm10": 45.2,
    "co": 0.75,
    "no2": 35.8,
    "o3": 42.1,
    "temperature": 22.5,
    "humidity": 65.0,
    "recordedAt": "2026-02-20T14:26:29"
  }
}
```

**Weather Response:**

```json
{
  "success": true,
  "message": "Weather data retrieved successfully",
  "data": {
    "location": "London, GB",
    "temperature": 12.5,
    "feelsLike": 10.8,
    "humidity": 78,
    "pressure": 1013,
    "windSpeed": 5.2,
    "windDirection": 245,
    "description": "scattered clouds",
    "main": "Clouds",
    "icon": "03d"
  }
}
```

---

## 📚 Documentation

Comprehensive documentation is available in the `docs/` directory:

### Backend Documentation

- **[API Documentation](air-quality-backend/docs/API_DOCUMENTATION.md)** - Complete API reference
- **[Deployment Guide](air-quality-backend/docs/DEPLOYMENT_GUIDE.md)** - Step-by-step deployment instructions
- **[Weather API Integration](air-quality-backend/docs/WEATHER_API_INTEGRATION.md)** - Weather API setup and usage
- **[Weather Quick Start](air-quality-backend/docs/WEATHER_QUICK_START.md)** - Quick testing guide
- **[Frontend Integration](air-quality-backend/docs/FRONTEND_INTEGRATION.md)** - Frontend API consumption guide

### Root Documentation

- **[Complete Deployment Guide](DEPLOYMENT_COMPLETE.md)** - Comprehensive deployment instructions
- **[Docker Setup](docker-compose.yml)** - Container orchestration configuration

---

## 💻 Development

### Setting Up Development Environment

1. **Install Java 21**:

   ```bash
   # Verify installation
   java --version
   # Should show: openjdk 21.x.x
   ```

2. **Install Maven** (or use wrapper):

   ```bash
   mvn --version
   # Maven 3.8.9 or higher
   ```

3. **Clone Repository**:

   ```bash
   git clone https://github.com/your-repo/air-quality-monitoring.git
   cd air-quality-monitoring
   ```

4. **Configure Application**:
   Edit `air-quality-backend/src/main/resources/application.properties`:

   ```properties
   # Add your OpenWeatherMap API key
   weather.api.key=YOUR_API_KEY_HERE
   ```

5. **Run in Development Mode**:
   ```bash
   cd air-quality-backend
   mvn spring-boot:run
   ```

### Building from Source

```bash
# Clean and build
mvn clean install

# Run tests
mvn test

# Package without tests
mvn clean package -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Hot Reload

For automatic restart on code changes:

```bash
# Add spring-boot-devtools dependency (already included)
mvn spring-boot:run
```

Change Java files - the application will restart automatically!

### Database Management

**H2 Console (Development)**:

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:airqualitydb`
- Username: `sa`
- Password: (empty)

**Switching to MySQL**:

1. Start MySQL server
2. Create database: `CREATE DATABASE air_quality_db;`
3. Update `application.properties` (see comments in file)
4. Restart application

### Testing APIs

**Using curl**:

```bash
# Get latest air quality
curl http://localhost:8080/api/air-quality/latest

# Get weather for London
curl http://localhost:8080/api/weather/city/London

# Subscribe to alerts
curl -X POST http://localhost:8080/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","threshold":100}'
```

**Using Postman**: Import the API endpoints and test interactively.

---

## 🌐 Deployment

### Production Deployment

See **[DEPLOYMENT_COMPLETE.md](DEPLOYMENT_COMPLETE.md)** for detailed instructions.

### Quick Production Checklist

- [ ] Set production API keys in environment variables
- [ ] Configure production database (MySQL recommended)
- [ ] Update CORS origins for your domain
- [ ] Disable H2 console: `spring.h2.console.enabled=false`
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Enable HTTPS/SSL
- [ ] Set up logging to external service
- [ ] Configure email service for real alerts
- [ ] Set up monitoring and alerting
- [ ] Implement rate limiting
- [ ] Enable Spring Security (if needed)

### Environment Variables

```bash
# Required
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/air_quality_db
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
WEATHER_API_KEY=your_openweather_api_key

# Optional
SERVER_PORT=8080
AIRQUALITY_SIMULATION_ENABLED=false
AIRQUALITY_CORS_ALLOWED_ORIGINS=https://your-domain.com
```

### Cloud Platforms

**AWS Elastic Beanstalk**:

```bash
eb init -p corretto-21
eb create production-env
eb deploy
```

**Heroku**:

```bash
heroku create air-quality-monitor
git push heroku main
```

**Azure**:

```bash
az webapp up --name air-quality-monitor --runtime "JAVA:21"
```

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

### Ways to Contribute

1. **Report Bugs** - Open an issue with detailed reproduction steps
2. **Suggest Features** - Propose new features or improvements
3. **Submit Pull Requests** - Fix bugs or implement features
4. **Improve Documentation** - Help make guides clearer
5. **Share Feedback** - Tell us how you're using the project

### Development Guidelines

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Write/update tests
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Write meaningful commit messages
- Add JavaDoc for public methods
- Keep methods focused and concise
- Write unit tests for new features

---

## 📊 System Architecture

```
┌─────────────────┐
│  Web Browser    │
│  (Frontend)     │
└────────┬────────┘
         │ HTTP/HTTPS
         │
┌────────▼────────────────────────────────┐
│      Spring Boot Application            │
│  ┌──────────────────────────────────┐   │
│  │  Controllers (REST API)          │   │
│  │  - AirQualityController          │   │
│  │  - WeatherController             │   │
│  │  - SubscriptionController        │   │
│  └─────────┬────────────────────────┘   │
│            │                             │
│  ┌─────────▼────────────────────────┐   │
│  │  Services (Business Logic)       │   │
│  │  - AirQualityService             │   │
│  │  - WeatherService ◄──────┐       │   │
│  │  - AlertService          │       │   │
│  │  - DataSimulatorService  │       │   │
│  └─────────┬────────────────┘       │   │
│            │                  External   │
│  ┌─────────▼────────────────────────┐   │
│  │  Repositories (Data Access)      │   │
│  │  - AirQualityRepository          │   │
│  │  - SubscriberRepository          │   │
│  └─────────┬────────────────────────┘   │
│            │                             │
└────────────┼─────────────────────────────┘
             │
    ┌────────▼────────┐      ┌────────────────┐
    │   Database      │      │ OpenWeatherMap │
    │   (H2/MySQL)    │      │      API       │
    └─────────────────┘      └────────────────┘
```

---

## 🔐 Security

### Best Practices Implemented

- ✅ Environment-based configuration
- ✅ CORS configuration for frontend security
- ✅ Input validation on all endpoints
- ✅ Exception handling with proper error messages
- ✅ Health check endpoints for monitoring
- ✅ No sensitive data in logs
- ✅ Database connections use connection pooling

### Security Recommendations

For production deployment:

- Use HTTPS/SSL certificates
- Implement Spring Security with authentication
- Enable rate limiting
- Use secrets management (AWS Secrets Manager, Azure Key Vault)
- Regular dependency updates
- Enable security headers
- Implement API key authentication
- Set up Web Application Firewall (WAF)

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

```
MIT License

Copyright (c) 2026 Urban Air Quality Monitoring System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 🙏 Acknowledgments

- **Spring Boot** - Amazing framework for Java applications
- **OpenWeatherMap** - Weather data API provider
- **H2 Database** - Fast in-memory database for development
- **Docker** - Containerization platform
- **Maven** - Build and dependency management
- **Java Community** - For the excellent ecosystem

---

## 📞 Support

### Getting Help

- **Documentation**: Check the `/docs` folder
- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-repo/discussions)

### Useful Commands Reference

```bash
# Start application
mvn spring-boot:run

# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/air-quality-backend-1.0.0.jar

# Docker build
docker build -t air-quality-backend .

# Docker Compose
docker-compose up -d          # Start
docker-compose logs -f        # View logs
docker-compose down           # Stop

# Maven commands
mvn clean                     # Clean
mvn test                      # Run tests
mvn install                   # Install to local repo
mvn dependency:tree           # View dependencies
```

---

## 🎯 Project Status

- ✅ **Core Features**: Complete
- ✅ **Weather Integration**: Complete
- ✅ **Frontend Dashboard**: Complete
- ✅ **Docker Support**: Complete
- ✅ **Documentation**: Complete
- ✅ **Production Ready**: Yes

### Roadmap

Future enhancements:

- [ ] Mobile application (React Native/Flutter)
- [ ] Real-time WebSocket updates
- [ ] Historical data visualization charts
- [ ] Machine learning predictions
- [ ] Multi-language support
- [ ] Admin dashboard
- [ ] User authentication system
- [ ] SMS alerts (Twilio integration)
- [ ] Export data to CSV/PDF
- [ ] Integration with real IoT sensors

---

## ⭐ Star This Repository

If you find this project useful, please consider giving it a star! It helps others discover the project.

---

**Built with ❤️ using Java 21 and Spring Boot 3**

**Happy Monitoring! 🌍🌱**
