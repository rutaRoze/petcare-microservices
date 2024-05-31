package com.roze.appointment_service.mapper;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.feign.UserClient;
import com.roze.appointment_service.persistance.model.AppointmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    @Autowired
    private UserClient userClient;

    public AppointmentEntity requestToEntity(AppointmentRequest appointmentRequest) {

        return AppointmentEntity.builder()
                .ownerId(appointmentRequest.getOwnerId())
                .vetId(appointmentRequest.getVetId())
                .appointmentDateTime(appointmentRequest.getAppointmentDateTime())
                .reason(appointmentRequest.getReason())
                .build();
    }

    public AppointmentResponse entityToResponse(AppointmentEntity appointmentEntity,
                                                UserResponse ownerResponse, UserResponse vetResponse) {

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
