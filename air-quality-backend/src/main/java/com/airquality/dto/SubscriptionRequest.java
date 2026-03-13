package com.airquality.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Threshold is required")
    @Min(value = 0, message = "Threshold must be at least 0")
    @Max(value = 500, message = "Threshold cannot exceed 500")
    private Integer threshold;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;
}
