package com.maqc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyScoreDTO {
    private Long propertyId;
    private Double recommendationScore; // Overall score out of 10
    private Double priceReasonablenessScore; // 0-3 points
    private Double rentalPerformanceScore; // 0-2 points
    private Double sellerMotivationScore; // 0-2 points
    private Double propertyConditionScore; // 0-1.5 points
    private Double transactionComplexityScore; // 0-1.5 points
    private String notes;
}