package com.roze.appointment_service.factory;

import com.roze.appointment_service.dto.event.AuditEvent;
import com.roze.appointment_service.enums.EventType;
import com.roze.appointment_service.persistance.model.AppointmentEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;

@Component
public class AuditEventFactory {

    public AuditEvent createAuditEvent(EventType eventType,
                                       String existingAppointment,
                                       AppointmentEntity updatedAppointment) {
        return AuditEvent.builder()
                .serviceName("Appointment-service")
                .eventType(String.format("Appointment %s", eventType.toString()))
                .initiatorUserId(updatedAppointment.getVetId())
                .message(buildAuditEventMessage(eventType, existingAppointment, updatedAppointment))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String buildAuditEventMessage(EventType eventType,
                                          String existingAppointment,
                                          AppointmentEntity updatedAppointment) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("Appointment with Id %d was %s: ",
                updatedAppointment.getAppointmentId(),
                eventType.toString().toLowerCase(Locale.ROOT)));

        switch (eventType) {
            case UPDATED -> {
                message.append(String.format("FROM %s TO %s",
                        existingAppointment,
                        updatedAppointment));
            }
            case CREATED, CANCELED -> {
                message.append(String.format(updatedAppointment.toString()));
            }
            default -> throw new IllegalArgumentException("Unsupported event type");
        }

        return message.toString();
    }
}
