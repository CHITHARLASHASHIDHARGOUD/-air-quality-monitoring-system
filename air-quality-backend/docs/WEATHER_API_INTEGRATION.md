# Weather API Integration Guide

## 🌤️ Overview

This guide covers the weather API integration for the Urban Air Quality Monitoring System. The system now includes real-time weather data from OpenWeatherMap API to provide comprehensive environmental information alongside air quality metrics.

---

## 🎯 Features

- ✅ **Current Weather Data** - Real-time weather information
- ✅ **City-based Search** - Get weather for any city worldwide
- ✅ **Coordinate-based Search** - Get weather by latitude/longitude
- ✅ **Combined Data Endpoint** - Air quality + weather in one call
- ✅ **Automatic Fallback** - Graceful handling of API failures
- ✅ **Comprehensive Metrics** - Temperature, humidity, wind, pressure, visibility

---

## 📡 API Endpoints

### 1. Get Current Weather (Default City)

```http
GET /api/weather/current
```

**Response:**

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

### 2. Get Weather by City

```http
GET /api/weather/city/{cityName}
```

**Example:**

```http
GET /api/weather/city/Paris
GET /api/weather/city/New York
GET /api/weather/city/Tokyo
```

### 3. Get Weather by Coordinates

```http
GET /api/weather/coordinates?lat={latitude}&lon={longitude}
```

**Example:**

```http
GET /api/weather/coordinates?lat=51.5074&lon=-0.1278
```

### 4. Get Combined Air Quality + Weather

```http
GET /api/weather/combined
```

**Response:**

```json
{
  "success": true,
  "message": "Combined data retrieved successfully",
  "data": {
    "airQuality": {
      "aqi": 85,
      "category": "Moderate",
      "advice": "Unusually sensitive people should consider reducing prolonged outdoor exertion",
      "pm25": 25.5,
      "pm10": 50.2,
      "co": 0.8,
      "no2": 35.0,
      "o3": 60.0,
      "timestamp": "2026-02-20T10:30:00"
    },
    "weather": {
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
    },
    "location": "London, GB",
    "timestamp": "2026-02-20T10:30:00"
  }
}
```

### 5. Get Combined Data by City

```http
GET /api/weather/combined/city/{cityName}
```

**Example:**

```http
GET /api/weather/combined/city/Mumbai
```

---

## 🔧 Configuration

The weather API is configured in `application.properties`:

```properties
# Weather API Configuration (OpenWeatherMap)
weather.api.key=da40a01545734ca7e86b2574509f30f1
weather.api.url=https://api.openweathermap.org/data/2.5/weather
weather.api.city=London
weather.api.units=metric
weather.api.timeout=5000
```

### Configuration Options:

| Property              | Description                      | Default                                           |
| --------------------- | -------------------------------- | ------------------------------------------------- |
| `weather.api.key`     | OpenWeatherMap API key           | Required                                          |
| `weather.api.url`     | API base URL                     | `https://api.openweathermap.org/data/2.5/weather` |
| `weather.api.city`    | Default city for weather data    | `London`                                          |
| `weather.api.units`   | Units (metric/imperial/standard) | `metric`                                          |
| `weather.api.timeout` | API timeout in milliseconds      | `5000`                                            |

### Units:

- **metric**: Temperature in Celsius, wind in m/s
- **imperial**: Temperature in Fahrenheit, wind in mph
- **standard**: Temperature in Kelvin, wind in m/s

---

## 🌍 Supported Cities

You can query weather for any city worldwide. Examples:

- **Major Cities**: London, Paris, New York, Tokyo, Mumbai, Sydney
- **Multi-word Cities**: "New York", "Los Angeles", "Hong Kong"
- **With Country Code**: "London,GB", "Paris,FR" (for disambiguation)

---

## 📊 Weather Data Fields

| Field         | Type     | Description             | Unit (Metric) |
| ------------- | -------- | ----------------------- | ------------- |
| `city`        | String   | City name               | -             |
| `country`     | String   | Country code (ISO 3166) | -             |
| `temperature` | Double   | Current temperature     | °C            |
| `feelsLike`   | Double   | Feels like temperature  | °C            |
| `humidity`    | Integer  | Humidity percentage     | %             |
| `pressure`    | Double   | Atmospheric pressure    | hPa           |
| `windSpeed`   | Double   | Wind speed              | m/s           |
| `windDegree`  | Integer  | Wind direction          | degrees       |
| `description` | String   | Weather description     | -             |
| `icon`        | String   | Weather icon code       | -             |
| `visibility`  | Integer  | Visibility distance     | meters        |
| `clouds`      | Integer  | Cloudiness              | %             |
| `timestamp`   | DateTime | Data timestamp          | -             |

---

## 🎨 Weather Icons

OpenWeatherMap provides weather icons that can be displayed:

**Icon URL Format:**

```
https://openweathermap.org/img/wn/{icon}@2x.png
```

**Example:**

```html
<img src="https://openweathermap.org/img/wn/04d@2x.png" alt="Weather" />
```

**Icon Codes:**

- `01d/01n` - Clear sky
- `02d/02n` - Few clouds
- `03d/03n` - Scattered clouds
- `04d/04n` - Broken clouds
- `09d/09n` - Shower rain
- `10d/10n` - Rain
- `11d/11n` - Thunderstorm
- `13d/13n` - Snow
- `50d/50n` - Mist

---

## 🚦 Error Handling

The weather service includes automatic fallback:

1. **API Success**: Returns real weather data
2. **API Failure**: Returns default/safe values
3. **Invalid City**: Returns data unavailable message

**Default Response Structure:**

```json
{
  "city": "RequestedCity",
  "country": "N/A",
  "temperature": 20.0,
  "feelsLike": 20.0,
  "humidity": 50,
  "pressure": 1013.0,
  "windSpeed": 5.0,
  "windDegree": 0,
  "description": "Data unavailable",
  "icon": "01d",
  "visibility": 10000,
  "clouds": 0,
  "timestamp": "current-time"
}
```

---

## 🧪 Testing

### Using cURL:

```bash
# Get current weather
curl http://localhost:8080/api/weather/current

# Get weather for Paris
curl http://localhost:8080/api/weather/city/Paris

# Get weather by coordinates
curl "http://localhost:8080/api/weather/coordinates?lat=48.8566&lon=2.3522"

# Get combined data
curl http://localhost:8080/api/weather/combined

# Get combined data for Mumbai
curl http://localhost:8080/api/weather/combined/city/Mumbai
```

### Using Postman:

Import the endpoints with the following base URL:

```
http://localhost:8080
```

---

## 🔄 Integration with Frontend

### JavaScript Example:

```javascript
// Fetch combined air quality and weather data
async function fetchEnvironmentalData(city = null) {
  const url = city
    ? `http://localhost:8080/api/weather/combined/city/${city}`
    : "http://localhost:8080/api/weather/combined";

  try {
    const response = await fetch(url);
    const data = await response.json();

    if (data.success) {
      const { airQuality, weather, location } = data.data;

      // Display air quality
      console.log(`AQI: ${airQuality.aqi} (${airQuality.category})`);

      // Display weather
      console.log(`Temperature: ${weather.temperature}°C`);
      console.log(`Condition: ${weather.description}`);
      console.log(`Humidity: ${weather.humidity}%`);

      return data.data;
    }
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// Usage
fetchEnvironmentalData("London");
```

### React Example:

```jsx
import { useState, useEffect } from "react";

function EnvironmentalDashboard() {
  const [data, setData] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/weather/combined")
      .then((res) => res.json())
      .then((result) => {
        if (result.success) {
          setData(result.data);
        }
      });
  }, []);

  if (!data) return <div>Loading...</div>;

  return (
    <div>
      <h2>Air Quality: {data.airQuality.aqi}</h2>
      <p>{data.airQuality.category}</p>

      <h2>Weather in {data.location}</h2>
      <img
        src={`https://openweathermap.org/img/wn/${data.weather.icon}@2x.png`}
        alt="Weather"
      />
      <p>Temperature: {data.weather.temperature}°C</p>
      <p>Condition: {data.weather.description}</p>
      <p>Humidity: {data.weather.humidity}%</p>
    </div>
  );
}
```

---

## 🔐 API Key Management

**Current API Key:** `da40a01545734ca7e86b2574509f30f1`

### Getting Your Own API Key:

1. Visit [OpenWeatherMap](https://openweathermap.org/api)
2. Sign up for a free account
3. Generate API key (free tier: 60 calls/minute, 1M calls/month)
4. Replace the key in `application.properties`

### API Rate Limits:

- **Free Tier**: 60 calls/minute, 1,000,000 calls/month
- **Paid Plans**: Higher limits available

---

## 📝 Best Practices

1. **Caching**: Consider caching weather data for 10-15 minutes
2. **Error Handling**: Frontend should handle "Data unavailable" gracefully
3. **Rate Limiting**: Don't exceed API rate limits
4. **City Names**: Use proper formatting (capitalize, spaces for multi-word)
5. **Coordinates**: Use valid lat/lon ranges (-90 to 90, -180 to 180)

---

## 🚀 Next Steps

1. **Cache Implementation**: Add Redis for weather data caching
2. **Weather Forecast**: Add 5-day forecast endpoint
3. **Historical Data**: Store weather data in database
4. **Weather Alerts**: Integrate severe weather alerts
5. **Air Quality Correlation**: Analyze weather's impact on air quality

---

## 📚 Related Documentation

- [API Documentation](API_DOCUMENTATION.md)
- [Frontend Integration](FRONTEND_INTEGRATION.md)
- [Deployment Guide](DEPLOYMENT_GUIDE.md)
- [Real API Integration](REAL_API_INTEGRATION.md)

---

## 🆘 Troubleshooting

### Issue: "API key not found"

**Solution**: Check `application.properties` has `weather.api.key` set

### Issue: "City not found"

**Solution**: Check city name spelling, try with country code

### Issue: "Connection timeout"

**Solution**: Check internet connection, increase timeout value

### Issue: "Rate limit exceeded"

**Solution**: Implement caching or upgrade API plan

---

## 📞 Support

For issues or questions:

- Check logs: Look for "WeatherService" entries
- Enable debug logging: `logging.level.com.airquality=DEBUG`
- Review OpenWeatherMap docs: https://openweathermap.org/api

---

**Last Updated:** February 20, 2026
