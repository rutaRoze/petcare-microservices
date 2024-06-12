package com.roze.notification_service.dto.event;

import com.roze.notification_service.dto.response.AppointmentResponse;
import com.roze.notification_service.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEvent {
    private EventType eventType;
    private AppointmentResponse appointmentResponse;
}
