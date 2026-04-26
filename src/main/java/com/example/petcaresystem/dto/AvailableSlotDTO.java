package com.example.petcaresystem.dto;

import java.time.LocalDateTime;

public class AvailableSlotDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long serviceProviderId;
    private String serviceProviderName;
    private String serviceType;

    public AvailableSlotDTO() {}

    public AvailableSlotDTO(LocalDateTime startTime, LocalDateTime endTime,
                            Long serviceProviderId, String serviceProviderName, String serviceType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceProviderId = serviceProviderId;
        this.serviceProviderName = serviceProviderName;
        this.serviceType = serviceType;
    }

    // Getters and Setters
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Long getServiceProviderId() { return serviceProviderId; }
    public void setServiceProviderId(Long serviceProviderId) { this.serviceProviderId = serviceProviderId; }

    public String getServiceProviderName() { return serviceProviderName; }
    public void setServiceProviderName(String serviceProviderName) { this.serviceProviderName = serviceProviderName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
}