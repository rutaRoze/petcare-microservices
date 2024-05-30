package com.roze.appointment_service.mapper;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.exception.NotFoundException;
import com.roze.appointment_service.feign.UserClient;
import com.roze.appointment_service.persistance.model.AppointmentEntity;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    @Autowired
    private UserClient userClient;

    public AppointmentEntity requestToEntity(AppointmentRequest appointmentRequest) {

        UserResponse ownerResponse = getUserByIdOrThrow(appointmentRequest.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentRequest.getVetId());

        return AppointmentEntity.builder()
                .ownerId(ownerResponse.getId())
                .vetId(vetResponse.getId())
                .appointmentDateTime(appointmentRequest.getAppointmentDateTime())
                .reason(appointmentRequest.getReason())
                .build();
    }

    public AppointmentResponse entityToResponse(AppointmentEntity appointmentEntity) {

        UserResponse ownerResponse = getUserByIdOrThrow(appointmentEntity.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentEntity.getVetId());

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

    private UserResponse getUserByIdOrThrow(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User not found with id: " + userId);
        }
    }
}
