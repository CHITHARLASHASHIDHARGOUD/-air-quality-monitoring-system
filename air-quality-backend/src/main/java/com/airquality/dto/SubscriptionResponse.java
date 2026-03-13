package com.airquality.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    
    private Long id;
    private String name;
    private String email;
    private Integer threshold;
    private String city;
    private String message;
}
