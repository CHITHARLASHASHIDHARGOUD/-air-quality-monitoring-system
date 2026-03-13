package com.airquality.service;

import org.springframework.stereotype.Service;

/**
 * Service for calculating AQI category and health messages
 * Based on standard AQI breakpoints
 */
@Service
public class AQICategoryService {

    /**
     * Determine category based on AQI value
     */
    public String getCategory(int aqi) {
        if (aqi <= 50) {
            return "Good";
        } else if (aqi <= 100) {
            return "Moderate";
        } else if (aqi <= 150) {
            return "Unhealthy for Sensitive Groups";
        } else if (aqi <= 200) {
            return "Unhealthy";
        } else if (aqi <= 300) {
            return "Very Unhealthy";
        } else {
            return "Hazardous";
        }
    }

    /**
     * Get health message based on category
     */
    public String getHealthMessage(String category) {
        switch (category) {
            case "Good":
                return "Air quality is satisfactory, and air pollution poses little or no risk.";
            
            case "Moderate":
                return "Air quality is acceptable. However, there may be a risk for some people, particularly those who are unusually sensitive to air pollution.";
            
            case "Unhealthy for Sensitive Groups":
                return "Members of sensitive groups may experience health effects. The general public is less likely to be affected.";
            
            case "Unhealthy":
                return "Some members of the general public may experience health effects; members of sensitive groups may experience more serious health effects.";
            
            case "Very Unhealthy":
                return "Health alert: The risk of health effects is increased for everyone. Avoid outdoor activities.";
            
            case "Hazardous":
                return "Health warning of emergency conditions: everyone is more likely to be affected. Stay indoors and keep activity levels low.";
            
            default:
                return "Unknown air quality level.";
        }
    }

    /**
     * Get health message directly from AQI value
     */
    public String getHealthMessage(int aqi) {
        String category = getCategory(aqi);
        return getHealthMessage(category);
    }

    /**
     * Calculate AQI from PM2.5 concentration
     * Simplified calculation using linear interpolation
     */
    public int calculateAQIFromPM25(double pm25) {
        // PM2.5 breakpoints and corresponding AQI values
        double[][] breakpoints = {
            {0.0, 12.0, 0, 50},
            {12.1, 35.4, 51, 100},
            {35.5, 55.4, 101, 150},
            {55.5, 150.4, 151, 200},
            {150.5, 250.4, 201, 300},
            {250.5, 500.4, 301, 500}
        };

        for (double[] bp : breakpoints) {
            if (pm25 >= bp[0] && pm25 <= bp[1]) {
                double cLow = bp[0];
                double cHigh = bp[1];
                double aqiLow = bp[2];
                double aqiHigh = bp[3];
                
                return (int) Math.round(
                    ((aqiHigh - aqiLow) / (cHigh - cLow)) * (pm25 - cLow) + aqiLow
                );
            }
        }

        return pm25 > 500 ? 500 : 0;
    }

    /**
     * Get color code for frontend (optional utility)
     */
    public String getCategoryColor(String category) {
        switch (category) {
            case "Good": return "#00E400";
            case "Moderate": return "#FFFF00";
            case "Unhealthy for Sensitive Groups": return "#FF7E00";
            case "Unhealthy": return "#FF0000";
            case "Very Unhealthy": return "#8F3F97";
            case "Hazardous": return "#7E0023";
            default: return "#808080";
        }
    }
}
