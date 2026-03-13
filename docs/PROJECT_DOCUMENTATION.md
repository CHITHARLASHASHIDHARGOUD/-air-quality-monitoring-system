# 🌍 Urban Air Quality Monitoring & Citizen Alert System

## Complete Project Documentation

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Technology Stack & Justification](#3-technology-stack--justification)
4. [Database Design](#4-database-design)
5. [Backend Structure](#5-backend-structure)
6. [REST API Endpoints](#6-rest-api-endpoints)
7. [Frontend](#7-frontend)
8. [External API Integration](#8-external-api-integration)
9. [Real-Time Data Pipeline & Workflow](#9-real-time-data-pipeline--workflow)
10. [Alert & Notification System](#10-alert--notification-system)
11. [Configuration Reference](#11-configuration-reference)
12. [Project File Structure](#12-project-file-structure)
13. [How to Run](#13-how-to-run)

---

## 1. Project Overview

**Project Name:** Urban Air Quality Monitoring & Citizen Alert System  
**Version:** 1.0.0  
**Type:** Full-Stack Web Application

### What It Does

This system monitors urban air quality in real time and alerts citizens when pollution levels exceed safe thresholds. It collects live Air Quality Index (AQI) data and weather information from the OpenWeatherMap API, stores historical readings in a database, and presents everything through a modern, responsive dashboard. Citizens can subscribe with their email and a custom AQI threshold — the system checks incoming data against those thresholds and triggers alerts automatically.

### Key Features

- **Real-Time AQI Monitoring** — Live pollutant readings (PM2.5, PM10, CO, NO₂, O₃) updated every 30–60 seconds
- **Weather Integration** — Current temperature, humidity, wind speed, and conditions alongside air quality
- **City-Based Tracking** — Search and monitor air quality for any city worldwide
- **Citizen Alert Subscriptions** — Users set a personal AQI threshold and receive alerts when it's exceeded
- **Historical Data** — Stores and displays past readings for trend analysis
- **Simulated Data Mode** — Built-in data simulator for testing and demos without external API calls
- **Responsive Dashboard** — Works across desktop, tablet, and mobile devices

---

## 2. Architecture

```
┌───────────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                          │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  index.html — Dashboard (HTML5 + CSS3 + Vanilla JavaScript)│ │
│  │                     │ │
│  └──────────────────────────┬──────────────────────────────────┘ │
└─────────────────────────────┼───────────────────────────────────┘
                              │ HTTP (Fetch API)
                              ▼
┌───────────────────────────────────────────────────────────────────┐
│                     BACKEND (Spring Boot 3.2.2)                  │
│                                                                   │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│  │ Controller │→ │  Service   │→ │ Repository │→ │  Database  │ │
│  │   Layer    │  │   Layer    │  │   Layer    │  │  (JPA)     │ │
│  └────────────┘  └─────┬──────┘  └────────────┘  └────────────┘ │
│                        │                                          │
│                        ▼                                          │
│             ┌─────────────────────┐                              │
│             │  External APIs      │                              │
│             │  (OpenWeatherMap)   │                              │
│             └─────────────────────┘                              │
│                                                                   │
│  ┌──────────────────────────────────────┐                        │
│  │  Scheduled Tasks (Background)        │                        │
│  │  • Data collection every 60s         │                        │
│  │  • Data simulation every 30s         │                        │
│  │  • Alert checking every 30s          │                        │
│  └──────────────────────────────────────┘                        │
└───────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────────────┐
│                        DATABASE                                   │
│  Development : H2 (in-memory)                                    │
│  Production  : MySQL 8.0                                         │
│  Tables      : air_quality, subscribers, alert_logs              │
└───────────────────────────────────────────────────────────────────┘
```

### Design Pattern

The application follows a **layered architecture**:

| Layer          | Responsibility                                                |
| -------------- | ------------------------------------------------------------- |
| **Controller** | Accepts HTTP requests, validates input, delegates to services |
| **Service**    | Business logic, external API calls, scheduled tasks           |
| **Repository** | Data access via Spring Data JPA                               |
| **Entity**     | Database table mappings                                       |
| **DTO**        | Data transfer objects for API request/response shaping        |
| **Exception**  | Centralized error handling with `@RestControllerAdvice`       |
| **Config**     | CORS configuration, WebClient setup                           |

---

## 3. Technology Stack & Justification

### Backend

| Technology                     | Version        | Why It Was Chosen                                                                                                                                                                 |
| ------------------------------ | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Java**                       | 21 LTS         | Long-term support, modern language features (records, pattern matching, virtual threads), strong ecosystem for enterprise applications                                            |
| **Spring Boot**                | 3.2.2          | De-facto standard for building production-grade Java REST APIs — provides auto-configuration, embedded server, dependency injection, and production-ready features out of the box |
| **Spring Data JPA**            | (via starter)  | Eliminates boilerplate database code — custom queries are just method signatures; supports complex queries via `@Query` annotations                                               |
| **Spring WebFlux (WebClient)** | (via starter)  | Non-blocking HTTP client for calling external APIs (OpenWeatherMap) efficiently without tying up server threads                                                                   |
| **Spring Boot Actuator**       | (via starter)  | Production monitoring — exposes `/health`, `/metrics`, `/info` endpoints for health checks and operational visibility                                                             |
| **Spring Validation**          | (via starter)  | Declarative input validation with annotations (`@NotBlank`, `@Email`, `@Min`, `@Max`) — keeps controllers clean                                                                   |
| **Lombok**                     | (compile-time) | Eliminates repetitive Java boilerplate (getters, setters, constructors, builder pattern) via annotations                                                                          |
| **Maven**                      | 3.8.9          | Industry-standard build tool — dependency management, build lifecycle, plugins, and reproducible builds                                                                           |

### Database

| Technology    | Version   | Why It Was Chosen                                                                                                                                        |
| ------------- | --------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **MySQL**     | 8.0       | Production-grade relational database — ACID compliant, supports complex indexing needed for time-series AQI queries, widely supported by cloud providers |
| **H2**        | (runtime) | Lightweight in-memory database for local development/testing — zero setup, auto-created on app start, perfect for demos                                  |
| **Hibernate** | (via JPA) | ORM that maps Java objects to database tables — handles schema generation (`ddl-auto=update`), relationship mapping, and query generation                |

### Frontend

| Technology             | Why It Was Chosen                                                                                                                           |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| **HTML5**              | Semantic markup, native form validation, accessibility features                                                                             |
| **CSS3**               | Glassmorphism design, CSS Grid/Flexbox for responsive layouts, CSS animations for real-time data feel                                       |
| **Vanilla JavaScript** | No framework overhead — the dashboard is a single-page read-mostly display with periodic API polling; a framework would be over-engineering |
| **Fetch API**          | Modern browser-native HTTP client for AJAX calls to the backend — no need for external libraries like Axios                                 |

### External APIs

| API                                  | Why It Was Chosen                                                                                                        |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------ |
| **OpenWeatherMap Air Pollution API** | Free tier available, provides global AQI data with individual pollutant breakdowns (PM2.5, PM10, CO, NO₂, O₃, SO₂)       |
| **OpenWeatherMap Weather API**       | Same provider as air pollution — consistent API design, single API key, provides temperature, humidity, wind, conditions |
| **OpenWeatherMap Geocoding API**     | Converts city names to coordinates required by the Air Pollution API                                                     |

---

## 4. Database Design

### Entity Relationship

```
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│   air_quality    │       │   subscribers    │       │   alert_logs     │
├──────────────────┤       ├──────────────────┤       ├──────────────────┤
│ id (PK)          │       │ id (PK)          │       │ id (PK)          │
│ aqi              │       │ name             │       │ subscriber_email │
│ category         │       │ email (UNIQUE)   │───┐   │ subscriber_name  │
│ health_message   │       │ threshold        │   │   │ threshold        │
│ pm25             │       │ city             │   └──→│ current_aqi      │
│ pm10             │       │ is_active        │       │ category         │
│ co               │       │ created_at       │       │ health_message   │
│ no2              │       └──────────────────┘       │ sent_at          │
│ o3               │                                   │ email_sent       │
│ temperature      │                                   └──────────────────┘
│ humidity         │
│ city             │
│ recorded_at      │
└──────────────────┘
```

### Table Details

#### `air_quality` — Stores pollutant readings

| Column           | Type              | Description                                                                               |
| ---------------- | ----------------- | ----------------------------------------------------------------------------------------- |
| `id`             | BIGINT (PK, Auto) | Primary key                                                                               |
| `aqi`            | INT               | Air Quality Index (0–500 scale, US EPA standard)                                          |
| `category`       | VARCHAR(50)       | Good / Moderate / Unhealthy for Sensitive Groups / Unhealthy / Very Unhealthy / Hazardous |
| `health_message` | VARCHAR(500)      | Human-readable health advisory                                                            |
| `pm25`           | DOUBLE            | Particulate Matter ≤2.5µm (µg/m³)                                                         |
| `pm10`           | DOUBLE            | Particulate Matter ≤10µm (µg/m³)                                                          |
| `co`             | DOUBLE            | Carbon Monoxide (ppm)                                                                     |
| `no2`            | DOUBLE            | Nitrogen Dioxide (ppb)                                                                    |
| `o3`             | DOUBLE            | Ozone (ppb)                                                                               |
| `temperature`    | DOUBLE            | Temperature (°C)                                                                          |
| `humidity`       | DOUBLE            | Relative Humidity (%)                                                                     |
| `city`           | VARCHAR(100)      | City name                                                                                 |
| `recorded_at`    | DATETIME          | Timestamp of reading                                                                      |

**Indexes:** `idx_recorded_at` (query performance), `idx_aqi` (threshold filtering)

#### `subscribers` — Citizen alert subscriptions

| Column       | Type                | Description                              |
| ------------ | ------------------- | ---------------------------------------- |
| `id`         | BIGINT (PK, Auto)   | Primary key                              |
| `name`       | VARCHAR(100)        | Subscriber's name                        |
| `email`      | VARCHAR(150) UNIQUE | Email address for alerts                 |
| `threshold`  | INT                 | AQI value that triggers an alert (0–500) |
| `city`       | VARCHAR(100)        | City to monitor                          |
| `is_active`  | BOOLEAN             | Whether subscription is active           |
| `created_at` | DATETIME            | Subscription creation time               |

**Indexes:** `idx_email` (unique lookup), `idx_threshold` (alert matching), `idx_city`, `idx_active`

#### `alert_logs` — History of triggered alerts

| Column             | Type              | Description                             |
| ------------------ | ----------------- | --------------------------------------- |
| `id`               | BIGINT (PK, Auto) | Primary key                             |
| `subscriber_email` | VARCHAR(150)      | Who was alerted                         |
| `subscriber_name`  | VARCHAR(100)      | Subscriber's name                       |
| `threshold`        | INT               | Subscriber's threshold at time of alert |
| `current_aqi`      | INT               | AQI value that triggered the alert      |
| `category`         | VARCHAR(50)       | AQI category at trigger time            |
| `health_message`   | VARCHAR(500)      | Health advisory sent                    |
| `sent_at`          | DATETIME          | When alert was triggered                |
| `email_sent`       | BOOLEAN           | Whether email was actually delivered    |

**Indexes:** `idx_sent_at`, `idx_subscriber_email`

---

## 5. Backend Structure

### Package Layout

```
com.airquality/
├── AirQualityApplication.java        ← Entry point (@SpringBootApplication)
├── config/
│   ├── CorsConfig.java               ← CORS configuration for /api/**
│   └── WebClientConfig.java          ← WebClient bean (16MB buffer)
├── controller/
│   ├── AirQualityController.java     ← Main AQI endpoints
│   ├── WeatherController.java        ← Weather data endpoints
│   ├── SubscriptionController.java   ← Alert subscription management
│   ├── HistoryController.java        ← Historical data access
│   └── TestController.java           ← Testing & simulation triggers
├── service/
│   ├── AirQualityService.java        ← Core AQI business logic
│   ├── AirPollutionService.java      ← OpenWeatherMap Air Pollution API client
│   ├── WeatherService.java           ← OpenWeatherMap Weather API client
│   ├── AlertService.java             ← Alert threshold checking & logging
│   ├── SubscriberService.java        ← Subscription CRUD operations
│   ├── DataSimulatorService.java     ← Scheduled data generation & collection
│   ├── AQICategoryService.java       ← AQI calculation (US EPA breakpoints)
│   └── EmailService.java             ← Email alert delivery (future)
├── dto/
│   ├── AirQualityResponse.java       ← AQI data response shape
│   ├── RecentReadingResponse.java    ← Recent readings list
│   ├── WeatherResponse.java          ← Weather data (includes OWM parser)
│   ├── CombinedDataResponse.java     ← Air quality + weather merged
│   ├── SubscriptionRequest.java      ← Subscription input (validated)
│   ├── SubscriptionResponse.java     ← Subscription confirmation
│   └── ApiResponse.java              ← Generic wrapper (success/error)
├── entity/
│   ├── AirQuality.java               ← JPA entity → air_quality table
│   ├── Subscriber.java               ← JPA entity → subscribers table
│   └── AlertLog.java                 ← JPA entity → alert_logs table
├── repository/
│   ├── AirQualityRepository.java     ← JPA queries for air quality
│   ├── SubscriberRepository.java     ← JPA queries for subscribers
│   └── AlertLogRepository.java       ← JPA queries for alert logs
└── exception/
    ├── GlobalExceptionHandler.java   ← @RestControllerAdvice error handler
    ├── ResourceNotFoundException.java
    └── DuplicateResourceException.java
```

### Service Responsibilities

| Service                  | Role                                                                                                                                                            |
| ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **AirQualityService**    | Fetches current/recent readings from DB, saves new readings, converts entities to DTOs, cleans up old records                                                   |
| **AirPollutionService**  | Calls OpenWeatherMap Air Pollution API, geocodes city names to coordinates, parses JSON responses into domain objects                                           |
| **WeatherService**       | Calls OpenWeatherMap Weather API by city name or coordinates, parses temperature/humidity/wind/conditions                                                       |
| **AlertService**         | Checks if latest AQI exceeds any subscriber's threshold, finds eligible subscribers, logs alerts to DB, enforces 30-minute cooldown to prevent duplicate alerts |
| **SubscriberService**    | Creates new subscriptions (with duplicate email check), soft-deletes for unsubscribe, finds subscribers by city and threshold                                   |
| **DataSimulatorService** | Scheduled tasks — generates realistic simulated AQI data every 30s, collects real city data every 60s, triggers alert checks after data collection              |
| **AQICategoryService**   | Calculates AQI from PM2.5 using US EPA breakpoint formula, maps AQI to categories, generates health advisory messages                                           |
| **EmailService**         | Placeholder for email delivery via Spring Mail (SMTP configuration required)                                                                                    |

---

## 6. REST API Endpoints

### Air Quality

| Method | Endpoint                               | Description                        | Response                  |
| ------ | -------------------------------------- | ---------------------------------- | ------------------------- |
| `GET`  | `/api/air-quality/current`             | Latest AQI reading                 | `AirQualityResponse`      |
| `GET`  | `/api/air-quality/recent`              | Last 12 readings                   | `RecentReadingResponse[]` |
| `GET`  | `/api/air-quality/recent?city={name}`  | Last 12 readings for a city        | `RecentReadingResponse[]` |
| `GET`  | `/api/air-quality/city/{cityName}`     | Live AQI from OpenWeatherMap       | `AirQualityResponse`      |
| `GET`  | `/api/air-quality/history/{city}`      | All historical readings for a city | `AirQualityResponse[]`    |
| `GET`  | `/api/air-quality/readings/{city}`     | Top 10 readings for a city         | `AirQualityResponse[]`    |
| `GET`  | `/api/air-quality/simulate/{cityName}` | Simulated data for a city          | `AirQualityResponse`      |
| `POST` | `/api/air-quality`                     | Save a new reading                 | `AirQualityResponse`      |

### Weather

| Method | Endpoint                                       | Description                       | Response               |
| ------ | ---------------------------------------------- | --------------------------------- | ---------------------- |
| `GET`  | `/api/weather/current`                         | Weather for default city (London) | `WeatherResponse`      |
| `GET`  | `/api/weather/city/{cityName}`                 | Weather for a specific city       | `WeatherResponse`      |
| `GET`  | `/api/weather/coordinates?lat={lat}&lon={lon}` | Weather by GPS coordinates        | `WeatherResponse`      |
| `GET`  | `/api/weather/combined`                        | Air quality + weather merged      | `CombinedDataResponse` |

### Subscriptions

| Method   | Endpoint                   | Description              | Request Body                       |
| -------- | -------------------------- | ------------------------ | ---------------------------------- |
| `POST`   | `/api/subscribe`           | Subscribe for AQI alerts | `{ name, email, threshold, city }` |
| `DELETE` | `/api/unsubscribe/{email}` | Unsubscribe from alerts  | —                                  |

### Testing & Control

| Method | Endpoint                    | Description                           |
| ------ | --------------------------- | ------------------------------------- |
| `POST` | `/api/test/pollution-spike` | Generate a test pollution spike event |
| `POST` | `/api/test/reset-quality`   | Reset AQI to good levels              |
| `POST` | `/api/test/generate-now`    | Force immediate data generation       |

### System Health (Actuator)

| Endpoint            | Description                                   |
| ------------------- | --------------------------------------------- |
| `/actuator/health`  | Application health status                     |
| `/actuator/metrics` | Runtime metrics (memory, threads, HTTP stats) |
| `/actuator/info`    | Application metadata                          |

---

## 7. Frontend

### Dashboard (`index.html`)

The main user-facing interface — a single-page dashboard that auto-refreshes every 30 seconds.

**Design Approach:** Glassmorphism with animated gradients, smooth transitions, and color-coded AQI categories.

#### Sections

1. **Navigation Bar** — Logo, live status indicator with pulse animation, city search
2. **Hero / Main AQI Display** — Large animated AQI number with glow effect, color changes based on category, health advisory text
3. **Pollutant Metrics Grid** — Individual cards for PM2.5, PM10, CO, NO₂, O₃, Temperature, Humidity
4. **Recent Readings Table** — Last 12 readings with time, AQI, pollutant values; auto-updates live
5. **Subscription Form** — Name, email, threshold selector (100/150/200/300), real-time validation, success feedback

#### Frontend Logic

- **Data Fetching:** Uses browser `Fetch API` to call `/api/air-quality/current` and `/api/air-quality/recent`
- **Auto-Refresh:** `setInterval` polls the backend every 30 seconds
- **Dynamic Theming:** Background gradients and card colors change based on AQI category:
  - 🟢 Good (0–50) → Green
  - 🟡 Moderate (51–100) → Yellow
  - 🟠 Unhealthy for Sensitive Groups (101–150) → Orange
  - 🔴 Unhealthy (151–200) → Red
  - 🟣 Very Unhealthy (201–300) → Purple
  - 🟤 Hazardous (301–500) → Maroon
- **Error Handling:** Graceful fallback messages when backend is unreachable
- **Subscription Submission:** POSTs to `/api/subscribe` with input validation

### Simulated Demo (`air2.html`)

A standalone page with **client-side data simulation** — works without the backend. Used for demonstrations, UI testing, and offline development. Generates random but realistic pollutant values entirely in JavaScript.

---

## 8. External API Integration

### OpenWeatherMap APIs

The system calls three OpenWeatherMap endpoints:

#### 1. Geocoding API

```
GET http://api.openweathermap.org/geo/1.0/direct?q={city}&limit=1&appid={key}
```

Converts a city name into latitude/longitude coordinates.

#### 2. Air Pollution API

```
GET http://api.openweathermap.org/data/2.5/air_pollution?lat={lat}&lon={lon}&appid={key}
```

Returns pollutant concentrations: PM2.5, PM10, CO, NO₂, O₃, SO₂, NH₃.

#### 3. Weather API

```
GET http://api.openweathermap.org/data/2.5/weather?q={city}&appid={key}&units=metric
```

Returns temperature, humidity, wind speed, weather description, and icon.

### Data Flow from API to Database

```
City Name
    │
    ▼
Geocoding API → (lat, lon)
    │
    ├──→ Air Pollution API → raw pollutant concentrations
    │       │
    │       ▼
    │    AQICategoryService → calculates AQI (US EPA formula)
    │       │                  determines category
    │       │                  generates health message
    │       ▼
    │    AirQualityRepository.save() → stored in DB
    │
    └──→ Weather API → temperature, humidity, wind, conditions
            │
            ▼
         WeatherResponse → returned to frontend
```

---

## 9. Real-Time Data Pipeline & Workflow

### Application Startup Sequence

```
1. Spring Boot starts → loads application.properties
2. JPA/Hibernate connects to database
3. schema.sql runs → creates tables if not present
4. Scheduled tasks initialize:
   a. DataSimulatorService starts (if simulation enabled)
   b. Real city data collection starts (if collection enabled)
   c. Alert checking starts (if alerts enabled)
5. REST API ready on port 8080
6. Frontend served on port 80 (Nginx) or port 5500 (Python dev server)
```

### Data Collection Cycle (Every 60 Seconds)

```
┌─────────────────────────────────┐
│  @Scheduled (every 60 seconds)  │
│  DataSimulatorService           │
└───────────────┬─────────────────┘
                │
                ▼
    ┌───────────────────────┐
    │ AirPollutionService   │
    │ .fetchRealCityData()  │
    └───────────┬───────────┘
                │
      ┌─────────┴─────────┐
      ▼                   ▼
  Geocoding API      Air Pollution API
  (city→coords)      (coords→pollutants)
      │                   │
      └─────────┬─────────┘
                │
                ▼
    ┌───────────────────────┐
    │ AQICategoryService    │
    │ .calculateAQI(pm25)   │
    │ .getCategory(aqi)     │
    │ .getHealthMessage()   │
    └───────────┬───────────┘
                │
                ▼
    ┌───────────────────────┐
    │ AirQualityRepository  │
    │ .save(entity)         │
    └───────────┬───────────┘
                │
                ▼
    ┌───────────────────────┐
    │ AlertService          │
    │ .checkAndSendAlerts() │
    └───────────────────────┘
```

### Alert Checking Workflow

```
New AQI Reading Saved
        │
        ▼
  AlertService.checkAlerts()
        │
        ▼
  Get latest AQI from DB
        │
        ▼
  Query subscribers WHERE:
    - is_active = true
    - threshold <= current_aqi
    - city matches (if specified)
        │
        ▼
  For each eligible subscriber:
    │
    ├── Check cooldown (no alert in last 30 mins?)
    │       │
    │       ├── YES (cooldown active) → Skip
    │       │
    │       └── NO (eligible) → Continue
    │               │
    │               ▼
    │         Log alert to alert_logs table
    │               │
    │               ▼
    │         Send email (when EmailService configured)
    │
    └── Next subscriber
```

### Frontend Data Refresh Cycle

```
┌──────────────────────┐
│ Browser loads page    │
│ index.html            │
└──────────┬───────────┘
           │
           ▼
   fetchCurrentData()
   fetchRecentData()
           │
           ├── GET /api/air-quality/current
           │       → Update AQI display, pollutant cards
           │
           ├── GET /api/air-quality/recent
           │       → Update readings table
           │
           └── setInterval(30000)
                   │
                   └── Repeat every 30 seconds
```

---

## 10. Alert & Notification System

### How It Works

1. **Subscription:** A citizen fills out the form with name, email, city, and an AQI threshold (e.g., 150)
2. **Storage:** The subscription is saved to the `subscribers` table with `is_active = true`
3. **Monitoring:** Every 30 seconds, `AlertService` checks the latest AQI against all active subscribers
4. **Matching:** If the AQI exceeds a subscriber's threshold, they are flagged for an alert
5. **Cooldown:** A 30-minute cooldown prevents sending duplicate alerts to the same subscriber
6. **Logging:** Every triggered alert is recorded in the `alert_logs` table
7. **Notification:** Email delivery via SMTP (requires configuration in `application.properties`)

### AQI Categories (US EPA Standard)

| AQI Range | Category                       | Color  | Health Advisory                                        |
| --------- | ------------------------------ | ------ | ------------------------------------------------------ |
| 0–50      | Good                           | Green  | Air quality is satisfactory                            |
| 51–100    | Moderate                       | Yellow | Acceptable; moderate concern for sensitive individuals |
| 101–150   | Unhealthy for Sensitive Groups | Orange | Sensitive groups may experience health effects         |
| 151–200   | Unhealthy                      | Red    | Everyone may begin to experience health effects        |
| 201–300   | Very Unhealthy                 | Purple | Health alert — everyone may experience serious effects |
| 301–500   | Hazardous                      | Maroon | Emergency conditions — entire population affected      |

### AQI Calculation Formula

The system uses the **US EPA linear interpolation** formula based on PM2.5 concentration:

```
AQI = ((AQI_high - AQI_low) / (C_high - C_low)) × (C - C_low) + AQI_low
```

Where C is the PM2.5 concentration and the breakpoints follow the EPA standard table.

---

## 11. Configuration Reference

### `application.properties`

```properties
# ─── Server ───
spring.application.name=Air Quality Monitoring System
server.port=8080

# ─── Database (MySQL - Production) ───
spring.datasource.url=jdbc:mysql://localhost:3306/air_quality_db
spring.datasource.username=aqi_user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# ─── Data Simulation (for demos/testing) ───
airquality.simulation.enabled=false
airquality.simulation.interval=30000          # milliseconds
airquality.simulation.max-records=100

# ─── Real City Data Collection ───
airquality.collection.enabled=true
airquality.collection.city=Hyderabad
airquality.collection.interval=60000          # milliseconds
airquality.collection.initial-delay=5000

# ─── Alert System ───
airquality.alert.enabled=true
airquality.alert.check-interval=30000         # milliseconds

# ─── CORS ───
airquality.cors.allowed-origins=*
airquality.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS

# ─── OpenWeatherMap API ───
weather.api.key=<API_KEY>
weather.api.city=London
weather.api.units=metric

# ─── Logging ───
logging.level.root=INFO
logging.level.com.airquality=DEBUG

# ─── Actuator (health/metrics) ───
management.endpoints.web.exposure.include=health,info,metrics
```

---

## 12. Project File Structure

```
d:\IP\
│
├── index.html                          ← Main dashboard (frontend)
├── air2.html                           ← Standalone simulated demo
│
├── build.bat                           ← Build backend JAR
├── start-app.bat                       ← Start full application
├── start-backend.bat                   ← Start backend only
├── start-frontend.bat                  ← Start frontend dev server
├── stop-all.bat                        ← Stop everything
├── test-api.bat                        ← API endpoint testing
├── open-dashboard.bat                  ← Open browser to dashboard
│
├── README.md                           ← Project readme
├── PROJECT_OVERVIEW.md                 ← High-level overview
├── GETTING_STARTED.md                  ← Quick start guide
├── CHECKLIST.md                        ← Development checklist
├── LICENSE                             ← License file
│
└── air-quality-backend/                ← Backend module
    ├── pom.xml                         ← Maven dependencies & build config
    ├── mvnw / mvnw.cmd                 ← Maven wrapper scripts
    │
    ├── docs/                           ← Backend documentation
    │   ├── API_DOCUMENTATION.md
    │   ├── DEPLOYMENT_GUIDE.md
    │   ├── FRONTEND_INTEGRATION.md
    │   ├── QUICK_START.md
    │   ├── REAL_API_INTEGRATION.md
    │   ├── WEATHER_API_INTEGRATION.md
    │   ├── WEATHER_QUICK_START.md
    │   └── Postman_Collection.json
    │
    └── src/main/
        ├── java/com/airquality/
        │   ├── AirQualityApplication.java
        │   ├── config/         (2 files)
        │   ├── controller/     (5 files)
        │   ├── service/        (8 files)
        │   ├── dto/            (7 files)
        │   ├── entity/         (3 files)
        │   ├── repository/     (3 files)
        │   └── exception/      (3 files)
        │
        └── resources/
            ├── application.properties
            └── schema.sql
```

---

## 13. How to Run

### Prerequisites

- Java 21 (JDK)
- Maven 3.8+
- MySQL 8.0 (or use H2 for development)
- Python 3.x (optional — for frontend dev server)
- OpenWeatherMap API key (free tier at [openweathermap.org](https://openweathermap.org/api))

### Option 1: Local Development

```bash
# 1. Clone the repository
git clone <repository-url>
cd IP

# 2. Set up MySQL database
mysql -u root -p
CREATE DATABASE air_quality_db;
CREATE USER 'aqi_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON air_quality_db.* TO 'aqi_user'@'localhost';

# 3. Build the backend
cd air-quality-backend
mvn clean package -DskipTests

# 4. Start the backend
java -jar target/air-quality-backend-1.0.0.jar

# 5. Open index.html in a browser (or use Python server)
python -m http.server 5500
# Navigate to http://localhost:5500
```

### Option 2: Using Batch Scripts (Windows)

```cmd
:: Build and start everything
start-app.bat

:: Or step by step:
build.bat               :: Compile the JAR
start-backend.bat       :: Start Spring Boot on port 8080
start-frontend.bat      :: Start frontend on port 5500
open-dashboard.bat      :: Open browser
```

### Verify It's Working

```cmd
:: Test backend health
curl http://localhost:8080/actuator/health

:: Test AQI data
curl http://localhost:8080/api/air-quality/current

:: Test weather
curl http://localhost:8080/api/weather/current

:: Or use the test script
test-api.bat
```

---

> **Built with** Java 21 + Spring Boot 3.2.2 + MySQL 8.0 + HTML5/CSS3/JavaScript + OpenWeatherMap API
