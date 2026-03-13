# API Documentation

## Urban Air Quality Monitoring System REST API

---

## Base URL

```
Development: http://localhost:8080/api
Production: https://your-domain.com/api
```

---

## 📋 Endpoints

### 1. Get Current Air Quality

**Endpoint:** `GET /api/air-quality/current`

**Description:** Returns the most recent air quality reading with all pollutant measurements.

**Request:** No parameters required

**Response:** `200 OK`

```json
{
  "aqi": 120,
  "category": "Unhealthy",
  "healthMessage": "Some members of the general public may experience health effects; members of sensitive groups may experience more serious health effects.",
  "pm25": 55.2,
  "pm10": 80.3,
  "co": 1.5,
  "no2": 60.0,
  "o3": 70.0,
  "temperature": 32.0,
  "humidity": 65.0,
  "time": "10:45:21"
}
```

**Error Response:** `404 Not Found`

```json
{
  "success": false,
  "message": "No air quality data available. Please wait for data collection to begin.",
  "data": null,
  "timestamp": 1708438421000
}
```

---

### 2. Get Recent Readings

**Endpoint:** `GET /api/air-quality/recent`

**Description:** Returns the last 12 air quality readings for table display.

**Request:** No parameters required

**Response:** `200 OK`

```json
[
  {
    "time": "10:45:21",
    "aqi": 120,
    "pm25": 55.2,
    "pm10": 80.3,
    "temperature": 32.0,
    "humidity": 65.0
  },
  {
    "time": "10:44:51",
    "aqi": 118,
    "pm25": 54.8,
    "pm10": 79.5,
    "temperature": 31.8,
    "humidity": 64.8
  }
  // ... 10 more readings
]
```

---

### 3. Subscribe to Alerts

**Endpoint:** `POST /api/subscribe`

**Description:** Subscribe a citizen to receive air quality alerts when AQI exceeds their threshold.

**Request Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "John Doe",
  "email": "john@gmail.com",
  "threshold": 150
}
```

**Field Validation:**

- `name`: Required, 2-100 characters
- `email`: Required, valid email format
- `threshold`: Required, integer between 0-500

**Success Response:** `201 Created`

```json
{
  "success": true,
  "message": "Subscription created successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@gmail.com",
    "threshold": 150,
    "message": "Successfully subscribed to air quality alerts!"
  },
  "timestamp": 1708438421000
}
```

**Error Response:** `409 Conflict` (Duplicate Email)

```json
{
  "success": false,
  "message": "Email john@gmail.com is already subscribed",
  "data": null,
  "timestamp": 1708438421000
}
```

**Error Response:** `400 Bad Request` (Validation Error)

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Invalid email format",
    "threshold": "Threshold must be at least 0"
  },
  "timestamp": 1708438421000
}
```

---

### 4. Unsubscribe from Alerts

**Endpoint:** `DELETE /api/unsubscribe/{email}`

**Description:** Unsubscribe from air quality alerts.

**Path Parameters:**

- `email`: Email address to unsubscribe

**Success Response:** `200 OK`

```json
{
  "success": true,
  "message": "Successfully unsubscribed from alerts",
  "data": "john@gmail.com",
  "timestamp": 1708438421000
}
```

---

### 5. Health Check

**Endpoint:** `GET /api/air-quality/health`

**Description:** Check if the API is running.

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Air Quality API is running",
  "data": null,
  "timestamp": 1708438421000
}
```

---

## 🧪 Testing Endpoints

### Test: Generate Pollution Spike

**Endpoint:** `POST /api/test/pollution-spike`

**Description:** Force generation of high AQI values for testing alert system.

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Pollution spike generated. Next reading will show high AQI.",
  "data": null,
  "timestamp": 1708438421000
}
```

---

### Test: Reset to Good Quality

**Endpoint:** `POST /api/test/reset-quality`

**Description:** Reset simulated data to good air quality levels.

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Air quality reset to good levels.",
  "data": null,
  "timestamp": 1708438421000
}
```

---

### Test: Generate Data Immediately

**Endpoint:** `POST /api/test/generate-now`

**Description:** Force immediate data generation (instead of waiting for scheduler).

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "New air quality data generated immediately.",
  "data": null,
  "timestamp": 1708438421000
}
```

---

## 📊 AQI Categories

| AQI Range | Category                       | Health Message                          |
| --------- | ------------------------------ | --------------------------------------- |
| 0-50      | Good                           | Air quality is satisfactory             |
| 51-100    | Moderate                       | Acceptable for most people              |
| 101-150   | Unhealthy for Sensitive Groups | Sensitive groups may experience effects |
| 151-200   | Unhealthy                      | Everyone may experience health effects  |
| 201-300   | Very Unhealthy                 | Health alert: avoid outdoor activities  |
| 301+      | Hazardous                      | Health warning: stay indoors            |

---

## 🔐 Future Authentication (Not Yet Implemented)

When JWT authentication is added:

**Request Headers:**

```
Authorization: Bearer <jwt_token>
```

---

## 📡 Postman Collection

### Import these requests into Postman:

#### 1. Get Current Air Quality

```
GET http://localhost:8080/api/air-quality/current
```

#### 2. Get Recent Readings

```
GET http://localhost:8080/api/air-quality/recent
```

#### 3. Subscribe

```
POST http://localhost:8080/api/subscribe
Content-Type: application/json

{
  "name": "Test User",
  "email": "test@example.com",
  "threshold": 100
}
```

#### 4. Generate Test Data

```
POST http://localhost:8080/api/test/generate-now
```

#### 5. Generate Pollution Spike

```
POST http://localhost:8080/api/test/pollution-spike
```

---

## 🐛 Error Codes

| Status Code | Meaning                        |
| ----------- | ------------------------------ |
| 200         | Success                        |
| 201         | Created (subscription)         |
| 400         | Bad Request (validation error) |
| 404         | Not Found (no data available)  |
| 409         | Conflict (duplicate email)     |
| 500         | Internal Server Error          |

---

## 🔄 Data Refresh Rate

- **New data generated:** Every 30 seconds
- **Frontend should poll:** Every 30 seconds
- **Alert checks:** Every 30 seconds
- **Database cleanup:** Automatic (keeps last 100 records)

---

## 📝 Notes

1. **Time Format:** All times are in `HH:mm:ss` format (24-hour)
2. **Decimal Precision:**
   - PM2.5, PM10, NO2, O3: 1 decimal place
   - CO: 2 decimal places
   - Temperature, Humidity: 1 decimal place
3. **CORS:** Pre-configured for localhost development
4. **Database:** MySQL with automatic schema creation

---

## 🚀 Quick Test Workflow

1. Start backend: `mvn spring-boot:run`
2. Wait 30 seconds for first data generation
3. Test GET current: `curl http://localhost:8080/api/air-quality/current`
4. Test subscription: Use Postman or curl
5. Generate spike: `curl -X POST http://localhost:8080/api/test/pollution-spike`
6. Verify alert logged in database

---

## 📞 Support

For issues or questions, check:

- Backend logs in console
- Database records
- Browser console (CORS errors)
- Postman for raw API testing
