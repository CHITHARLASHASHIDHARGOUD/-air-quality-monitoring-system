# Weather API Quick Start

## 🚀 Quick Test Guide

Your weather API integration is now complete! Here's how to test it:

### 1. Start the Application

```bash
mvn spring-boot:run
```

Or run from your IDE: `AirQualityApplication.java`

### 2. Test Weather Endpoints

**Get Current Weather (Default: London)**

```bash
curl http://localhost:8080/api/weather/current
```

**Get Weather for a Specific City**

```bash
curl http://localhost:8080/api/weather/city/Paris
curl http://localhost:8080/api/weather/city/Mumbai
curl http://localhost:8080/api/weather/city/NewYork
```

**Get Weather by Coordinates**

```bash
# London coordinates
curl "http://localhost:8080/api/weather/coordinates?lat=51.5074&lon=-0.1278"

# Mumbai coordinates
curl "http://localhost:8080/api/weather/coordinates?lat=19.0760&lon=72.8777"
```

**Get Combined Air Quality + Weather Data**

```bash
curl http://localhost:8080/api/weather/combined
curl http://localhost:8080/api/weather/combined/city/Tokyo
```

### 3. Sample Response

```json
{
  "success": true,
  "message": "Weather data retrieved successfully",
  "data": {
    "city": "London",
    "country": "GB",
    "temperature": 15.5,
    "feelsLike": 14.2,
    "humidity": 72,
    "pressure": 1013.0,
    "windSpeed": 5.5,
    "windDegree": 180,
    "description": "broken clouds",
    "icon": "04d",
    "visibility": 10000,
    "clouds": 75,
    "timestamp": "2026-02-20T10:30:00"
  }
}
```

### 4. Browser Testing

Simply open these URLs in your browser:

- http://localhost:8080/api/weather/current
- http://localhost:8080/api/weather/city/Paris
- http://localhost:8080/api/weather/combined

### 5. Configuration

Your API key is already configured in `application.properties`:

```properties
weather.api.key=da40a01545734ca7e86b2574509f30f1
weather.api.city=London
weather.api.units=metric
```

**Change default city:**

```properties
weather.api.city=Mumbai
```

**Change temperature units:**

```properties
# metric = Celsius, imperial = Fahrenheit, standard = Kelvin
weather.api.units=imperial
```

### 6. Integration with Frontend

```html
<!DOCTYPE html>
<html>
  <head>
    <title>Weather & Air Quality Dashboard</title>
  </head>
  <body>
    <div id="dashboard"></div>

    <script>
      // Fetch combined data
      fetch("http://localhost:8080/api/weather/combined")
        .then((response) => response.json())
        .then((data) => {
          if (data.success) {
            const { airQuality, weather } = data.data;

            document.getElementById("dashboard").innerHTML = `
                        <h2>📍 ${weather.city}, ${weather.country}</h2>
                        
                        <div class="weather">
                            <h3>🌤️ Weather</h3>
                            <img src="https://openweathermap.org/img/wn/${weather.icon}@2x.png" />
                            <p>Temperature: ${weather.temperature}°C</p>
                            <p>${weather.description}</p>
                            <p>Humidity: ${weather.humidity}%</p>
                            <p>Wind: ${weather.windSpeed} m/s</p>
                        </div>
                        
                        <div class="air-quality">
                            <h3>💨 Air Quality</h3>
                            <p>AQI: ${airQuality.aqi} (${airQuality.category})</p>
                            <p>${airQuality.advice}</p>
                        </div>
                    `;
          }
        })
        .catch((error) => console.error("Error:", error));
    </script>
  </body>
</html>
```

### 7. Available Endpoints

| Method | Endpoint                                 | Description                   |
| ------ | ---------------------------------------- | ----------------------------- |
| GET    | `/api/weather/current`                   | Get weather for default city  |
| GET    | `/api/weather/city/{cityName}`           | Get weather for specific city |
| GET    | `/api/weather/coordinates?lat={}&lon={}` | Get weather by coordinates    |
| GET    | `/api/weather/combined`                  | Get air quality + weather     |
| GET    | `/api/weather/combined/city/{cityName}`  | Get combined data for city    |

### 8. Weather Icon URLs

Display weather icons using:

```
https://openweathermap.org/img/wn/{icon}@2x.png
```

Example:

```html
<img src="https://openweathermap.org/img/wn/04d@2x.png" alt="Weather" />
```

### 9. Popular Cities to Test

- London, GB
- Paris, FR
- New York, US
- Tokyo, JP
- Mumbai, IN
- Sydney, AU
- Berlin, DE
- Singapore, SG
- Dubai, AE
- Toronto, CA

### 10. Troubleshooting

**Issue: API key not working**

- Check `application.properties` has correct key
- Restart the application after changes

**Issue: City not found**

- Check spelling
- Try adding country code: "London,GB"

**Issue: Connection timeout**

- Check internet connection
- API might be rate-limited (60 calls/min on free tier)

**Issue: Data unavailable**

- API fallback is working
- Check logs for actual error

### 11. Next Steps

✅ **Weather API is fully integrated!**

You now have:

- Real-time weather data
- City-based search
- Coordinate-based search
- Combined air quality + weather endpoints
- Automatic error handling
- Comprehensive documentation

**Explore full documentation:**

- [WEATHER_API_INTEGRATION.md](WEATHER_API_INTEGRATION.md) - Complete guide
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - All API endpoints
- [FRONTEND_INTEGRATION.md](FRONTEND_INTEGRATION.md) - Frontend integration guide

---

**API Key:** da40a01545734ca7e86b2574509f30f1  
**Default City:** London  
**Units:** Metric (Celsius)  
**Rate Limit:** 60 calls/minute (free tier)

**Need help?** Check the logs or full documentation.
