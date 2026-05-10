package com.maqc.backend.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private String proType; // "notary", "inspector", or "agent"
    private Long proId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String date;
    private String time;
    private String notes;
}
