package com.roze.appointment_service.mapper;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.feign.UserClient;
import com.roze.appointment_service.persistance.model.AppointmentEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    @Autowired
    private UserClient userClient;

    public AppointmentEntity requestToEntity(AppointmentRequest appointmentRequest) {

        UserResponse ownerResponse = userClient.getUserById(appointmentRequest.getOwnerId());
        if (ownerResponse == null) {
            throw new EntityNotFoundException("User not found by id: " + appointmentRequest.getOwnerId());
        }

        UserResponse vetResponse = userClient.getUserById(appointmentRequest.getVetId());
        if (vetResponse == null) {
            throw new EntityNotFoundException("User not found by id: " + appointmentRequest.getOwnerId());
        }

        return AppointmentEntity.builder()
                .ownerId(ownerResponse.getId())
                .vetId(vetResponse.getId())
                .appointmentDateTime(appointmentRequest.getAppointmentDateTime())
                .reason(appointmentRequest.getReason())
                .build();
    }

    public AppointmentResponse entityToResponse(AppointmentEntity appointmentEntity) {

        UserResponse ownerResponse = userClient.getUserById(appointmentEntity.getOwnerId());
        if (ownerResponse == null) {
            throw new EntityNotFoundException("User not found by id: " + appointmentEntity.getOwnerId());
        }

        UserResponse vetResponse = userClient.getUserById(appointmentEntity.getVetId());
        if (vetResponse == null) {
            throw new EntityNotFoundException("User not found by id: " + appointmentEntity.getOwnerId());
        }

        return AppointmentResponse.builder()
                .appointmentId(appointmentEntity.getAppointmentId())
                .ownerName(ownerResponse.getName())
                .ownerSurname(ownerResponse.getSurname())
                .vetName(vetResponse.getName())
                .vetSurname(vetResponse.getSurname())
                .appointmentDateTime(appointmentEntity.getAppointmentDateTime())
                .reason(appointmentEntity.getReason())
                .build();
    }
}
