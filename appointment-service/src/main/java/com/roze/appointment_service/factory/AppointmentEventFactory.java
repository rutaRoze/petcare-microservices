package com.roze.appointment_service.factory;

import com.roze.appointment_service.dto.event.AppointmentEvent;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.enums.EventType;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventFactory {

    public AppointmentEvent createAppointmentEvent(EventType eventType,
                                                   AppointmentResponse appointmentResponse) {
        return AppointmentEvent.builder()
                .eventType(eventType)
                .appointmentResponse(appointmentResponse)
                .build();
    }
}
