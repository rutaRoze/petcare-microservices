package com.roze.appointment_service.service.impl;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.exception.AppointmentExistsException;
import com.roze.appointment_service.exception.NoChangesMadeException;
import com.roze.appointment_service.exception.NotFoundException;
import com.roze.appointment_service.feign.UserClient;
import com.roze.appointment_service.mapper.AppointmentMapper;
import com.roze.appointment_service.persistance.AppointmentRepository;
import com.roze.appointment_service.persistance.model.AppointmentEntity;
import com.roze.appointment_service.service.AppointmentService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    UserClient userClient;

    @Override
    public AppointmentResponse saveAppointment(AppointmentRequest appointmentRequest) {
        UserResponse ownerResponse = getUserByIdOrThrow(appointmentRequest.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentRequest.getVetId());

        AppointmentEntity appointmentToSave = appointmentMapper.requestToEntity(appointmentRequest);

        if (doesAppointmentAlreadyExists(appointmentToSave)) {
            throw new AppointmentExistsException("Appointment for given veterinarian, date and time already exists");
        }

        AppointmentEntity savedAppointment = appointmentRepository.save(appointmentToSave);

        return appointmentMapper.entityToResponse(savedAppointment, ownerResponse, vetResponse);
    }

    @Override
    public AppointmentResponse findAppointmentById(Long id) {
        AppointmentEntity appointmentEntity = getAppointmentByIdOrThrow(id);

        UserResponse ownerResponse = getUserByIdOrThrow(appointmentEntity.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentEntity.getVetId());

        return appointmentMapper.entityToResponse(appointmentEntity, ownerResponse, vetResponse);
    }

    @Override
    public AppointmentResponse updateAppointmentById(Long id, AppointmentRequest appointmentRequest) {
        AppointmentEntity existingAppointment = getAppointmentByIdOrThrow(id);

        if (isAppointmentEqual(existingAppointment, appointmentRequest)) {
            throw new NoChangesMadeException("Appointment object was not updated as no changes of object were made.");
        }

        AppointmentEntity appointmentToUpdate = appointmentMapper.requestToEntity(appointmentRequest);

        UserResponse ownerResponse = getUserByIdOrThrow(appointmentToUpdate.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentToUpdate.getVetId());

        if (doesAppointmentAlreadyExists(appointmentToUpdate)) {
            throw new AppointmentExistsException("Appointment for given veterinarian, date and time already exists");
        }

        existingAppointment.setOwnerId(appointmentToUpdate.getOwnerId());
        existingAppointment.setVetId(appointmentToUpdate.getVetId());
        existingAppointment.setAppointmentDateTime(appointmentToUpdate.getAppointmentDateTime());
        existingAppointment.setReason(appointmentToUpdate.getReason());

        AppointmentEntity updatedAppointment = appointmentRepository.save(existingAppointment);

        return appointmentMapper.entityToResponse(updatedAppointment, ownerResponse, vetResponse);
    }

    @Override
    public void deleteAppointmentById(Long id) {
        getAppointmentByIdOrThrow(id);
        appointmentRepository.deleteById(id);
    }

    private AppointmentEntity getAppointmentByIdOrThrow(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment object not found for the given ID: " + appointmentId));
    }

    private boolean isAppointmentEqual(AppointmentEntity existingAppointment, AppointmentRequest appointmentRequest) {
        return existingAppointment.getOwnerId().equals(appointmentRequest.getOwnerId()) &&
                existingAppointment.getVetId().equals(appointmentRequest.getVetId()) &&
                existingAppointment.getAppointmentDateTime().equals(appointmentRequest.getAppointmentDateTime()) &&
                existingAppointment.getReason().equals(appointmentRequest.getReason());
    }

    private boolean doesAppointmentAlreadyExists(AppointmentEntity appointmentToSave) {
        return appointmentRepository.existsByVetIdAndAppointmentDateTime(
                appointmentToSave.getVetId(),
                appointmentToSave.getAppointmentDateTime()
        );
    }

    private UserResponse getUserByIdOrThrow(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User not found with id: " + userId);
        }
    }
}
