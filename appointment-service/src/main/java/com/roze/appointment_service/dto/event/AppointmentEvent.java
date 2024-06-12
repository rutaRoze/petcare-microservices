package com.roze.appointment_service.dto.event;

import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEvent {
    private String eventType;
//    private AppointmentResponse appointmentResponse;
}
