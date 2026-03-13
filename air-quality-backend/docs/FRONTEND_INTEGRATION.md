# Frontend Integration Guide

## Connecting Your HTML UI to Spring Boot Backend

---

## 🚀 Quick Start

Your existing HTML UI is **already structured correctly** and expects the exact data format our backend provides. You just need to replace the simulated fetch calls with real API calls.

---

## 📡 API Endpoints

### Base URL

```
Local Development: http://localhost:8080/api
Production: https://your-domain.com/api
```

### Available Endpoints

#### 1. **Get Current Air Quality**

```
GET /api/air-quality/current
```

**Response:**

```json
{
  "aqi": 120,
  "category": "Unhealthy",
  "healthMessage": "Reduce outdoor activities",
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

#### 2. **Get Recent Readings**

```
GET /api/air-quality/recent
```

**Response:**

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
  ...
]
```

#### 3. **Subscribe to Alerts**

```
POST /api/subscribe
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

**Response:**

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

---

## 🔧 Integration Steps

### Step 1: Update JavaScript in your HTML file

Find the section in your `air2.html` where data is fetched (likely using `fetch()` or simulated data).

Replace simulated code with:

```javascript
// Configuration
const API_BASE_URL = "http://localhost:8080/api";

// Fetch current air quality data
async function fetchCurrentAirQuality() {
  try {
    const response = await fetch(`${API_BASE_URL}/air-quality/current`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    updateUI(data);
  } catch (error) {
    console.error("Error fetching air quality data:", error);
    showError("Unable to fetch air quality data. Please try again.");
  }
}

// Fetch recent readings for table
async function fetchRecentReadings() {
  try {
    const response = await fetch(`${API_BASE_URL}/air-quality/recent`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const readings = await response.json();
    updateTable(readings);
  } catch (error) {
    console.error("Error fetching recent readings:", error);
  }
}

// Subscribe to alerts
async function subscribeToAlerts(name, email, threshold) {
  try {
    const response = await fetch(`${API_BASE_URL}/subscribe`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ name, email, threshold }),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Subscription failed");
    }

    const result = await response.json();
    showSuccess(result.data.message);
  } catch (error) {
    console.error("Error subscribing:", error);
    showError(error.message || "Subscription failed. Please try again.");
  }
}

// Auto-refresh every 30 seconds to match backend generation
setInterval(() => {
  fetchCurrentAirQuality();
  fetchRecentReadings();
}, 30000);

// Initial fetch
fetchCurrentAirQuality();
fetchRecentReadings();
```

### Step 2: Update UI Functions

Make sure your UI update functions match the data structure:

```javascript
function updateUI(data) {
  // Update AQI value
  document.getElementById("aqi-value").textContent = data.aqi;

  // Update category
  document.getElementById("category").textContent = data.category;

  // Update health message
  document.getElementById("health-message").textContent = data.healthMessage;

  // Update pollutants
  document.getElementById("pm25").textContent = data.pm25.toFixed(1);
  document.getElementById("pm10").textContent = data.pm10.toFixed(1);
  document.getElementById("co").textContent = data.co.toFixed(2);
  document.getElementById("no2").textContent = data.no2.toFixed(1);
  document.getElementById("o3").textContent = data.o3.toFixed(1);

  // Update weather
  document.getElementById("temperature").textContent =
    data.temperature.toFixed(1);
  document.getElementById("humidity").textContent = data.humidity.toFixed(1);

  // Update time
  document.getElementById("time").textContent = data.time;

  // Update category styling (Good, Moderate, Unhealthy, etc.)
  updateCategoryStyle(data.category);
}

function updateTable(readings) {
  const tbody = document.getElementById("readings-table-body");
  tbody.innerHTML = "";

  readings.forEach((reading) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${reading.time}</td>
      <td>${reading.aqi}</td>
      <td>${reading.pm25.toFixed(1)}</td>
      <td>${reading.pm10.toFixed(1)}</td>
      <td>${reading.temperature.toFixed(1)}°C</td>
      <td>${reading.humidity.toFixed(1)}%</td>
    `;
    tbody.appendChild(row);
  });
}
```

### Step 3: Handle Subscription Form

```javascript
document
  .getElementById("subscription-form")
  .addEventListener("submit", async (e) => {
    e.preventDefault();

    const name = document.getElementById("name-input").value.trim();
    const email = document.getElementById("email-input").value.trim();
    const threshold = parseInt(
      document.getElementById("threshold-input").value,
    );

    await subscribeToAlerts(name, email, threshold);

    // Clear form on success
    e.target.reset();
  });
```

---

## 🌐 CORS Configuration

The backend is already configured to accept requests from:

- `http://localhost:5500` (Live Server)
- `http://127.0.0.1:5500`
- `http://localhost:3000`

To add more origins, edit `application.properties`:

```properties
airquality.cors.allowed-origins=http://localhost:5500,http://yourfrontend.com
```

---

## 🧪 Testing the Connection

### 1. Start the Backend

```bash
cd air-quality-backend
mvn spring-boot:run
```

### 2. Open Browser Console

Open `air2.html` in your browser and check the console:

- You should see successful API calls
- Data should update every 30 seconds
- No CORS errors

### 3. Test Subscription

- Fill out the subscription form
- Check browser console for success message
- Verify in database: `SELECT * FROM subscribers;`

---

## 🔍 Debugging

### Problem: CORS Error

```
Access to fetch at 'http://localhost:8080/api/air-quality/current'
from origin 'http://localhost:5500' has been blocked by CORS policy
```

**Solution:** Check that:

1. Backend is running
2. Your frontend origin is in `application.properties` allowed origins
3. Browser cache is cleared

### Problem: 404 Not Found

```
GET http://localhost:8080/api/air-quality/current 404
```

**Solution:**

1. Verify backend is running on port 8080
2. Check endpoint URL is correct
3. View backend logs for errors

### Problem: No Data Returned

```
Response: {"success":false,"message":"No air quality data available"}
```

**Solution:**

- Wait 30 seconds for scheduler to generate first data
- Or call test endpoint: `POST http://localhost:8080/api/test/generate-now`

---

## 📱 Production Deployment

### Update API Base URL

For production, use environment variable or config:

```javascript
const API_BASE_URL =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api"
    : "https://your-backend-url.com/api";
```

Or use relative URLs if frontend and backend are on same domain:

```javascript
const API_BASE_URL = "/api"; // Works if frontend at example.com and backend at example.com/api
```

---

## ✅ Complete Frontend Template

Here's a complete working example you can copy:

```html
<script>
  const API_BASE_URL = "http://localhost:8080/api";

  // Fetch and update current air quality
  async function refreshAirQuality() {
    try {
      const response = await fetch(`${API_BASE_URL}/air-quality/current`);
      const data = await response.json();

      // Update main display
      document.querySelector(".aqi-value").textContent = data.aqi;
      document.querySelector(".category").textContent = data.category;
      document.querySelector(".health-msg").textContent = data.healthMessage;
      document.querySelector(".pm25-val").textContent = data.pm25;
      document.querySelector(".pm10-val").textContent = data.pm10;
      document.querySelector(".co-val").textContent = data.co;
      document.querySelector(".no2-val").textContent = data.no2;
      document.querySelector(".o3-val").textContent = data.o3;
      document.querySelector(".temp-val").textContent = data.temperature;
      document.querySelector(".humidity-val").textContent = data.humidity;
      document.querySelector(".time-val").textContent = data.time;

      // Fetch recent readings
      const recentResponse = await fetch(`${API_BASE_URL}/air-quality/recent`);
      const readings = await recentResponse.json();
      updateTable(readings);
    } catch (error) {
      console.error("Error:", error);
    }
  }

  // Update table
  function updateTable(readings) {
    const tbody = document.querySelector("#recent-table tbody");
    tbody.innerHTML = readings
      .map(
        (r) => `
      <tr>
        <td>${r.time}</td>
        <td>${r.aqi}</td>
        <td>${r.pm25}</td>
        <td>${r.pm10}</td>
        <td>${r.temperature}°</td>
        <td>${r.humidity}%</td>
      </tr>
    `,
      )
      .join("");
  }

  // Subscribe
  async function subscribe(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    try {
      const response = await fetch(`${API_BASE_URL}/subscribe`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: formData.get("name"),
          email: formData.get("email"),
          threshold: parseInt(formData.get("threshold")),
        }),
      });

      const result = await response.json();

      if (result.success) {
        alert("✅ " + result.data.message);
        event.target.reset();
      } else {
        alert("❌ " + result.message);
      }
    } catch (error) {
      alert("❌ Subscription failed. Please try again.");
    }
  }

  // Initialize
  refreshAirQuality();
  setInterval(refreshAirQuality, 30000);
</script>
```

---

## 🎯 Summary

1. **Replace simulated fetch calls** with real API calls to `http://localhost:8080/api`
2. **Use the exact data structure** from API responses
3. **Auto-refresh every 30 seconds** to match backend data generation
4. **Handle errors gracefully** with try-catch blocks
5. **Test locally first** before deploying to production

Your frontend should now display **live, real-time air quality data** from the Spring Boot backend! 🎉
