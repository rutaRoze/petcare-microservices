package com.roze.appointment_service.service.impl;

import com.roze.appointment_service.dto.event.AppointmentEvent;
import com.roze.appointment_service.dto.event.AuditEvent;
import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.enums.EventType;
import com.roze.appointment_service.exception.AppointmentExistsException;
import com.roze.appointment_service.exception.NoChangesMadeException;
import com.roze.appointment_service.exception.NotFoundException;
import com.roze.appointment_service.feign.UserClient;
import com.roze.appointment_service.kafka.producer.KafkaEventProducer;
import com.roze.appointment_service.mapper.AppointmentMapper;
import com.roze.appointment_service.persistance.AppointmentRepository;
import com.roze.appointment_service.persistance.model.AppointmentEntity;
import com.roze.appointment_service.service.AppointmentService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    UserClient userClient;

    @Autowired
    KafkaEventProducer kafkaEventProducer;

    @Override
    public AppointmentResponse saveAppointment(AppointmentRequest appointmentRequest) {
        UserResponse ownerResponse = getUserByIdOrThrow(appointmentRequest.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentRequest.getVetId());

        AppointmentEntity appointmentToSave = appointmentMapper.requestToEntity(appointmentRequest);

        checkDoesAppointmentForVetAlreadyExists(appointmentToSave);

        AppointmentEntity savedAppointment = appointmentRepository.save(appointmentToSave);

        AppointmentResponse response = appointmentMapper.entityToResponse(savedAppointment, ownerResponse, vetResponse);

        kafkaEventProducer.sendEventToKafkaServer(createAuditEvent(EventType.CREATED, savedAppointment, savedAppointment));
        kafkaEventProducer.sendEventToKafkaServer(createAppointmentEvent(EventType.CREATED, response));

        return response;
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

        checkIsAppointmentRequestHasAnyUpdates(existingAppointment, appointmentRequest);

        AppointmentEntity appointmentToUpdate = appointmentMapper.requestToEntity(appointmentRequest);

        UserResponse ownerResponse = getUserByIdOrThrow(appointmentToUpdate.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentToUpdate.getVetId());

        checkDoesAppointmentForVetAlreadyExists(appointmentToUpdate);

        existingAppointment.setOwnerId(appointmentToUpdate.getOwnerId());
        existingAppointment.setVetId(appointmentToUpdate.getVetId());
        existingAppointment.setAppointmentDateTime(appointmentToUpdate.getAppointmentDateTime());
        existingAppointment.setReason(appointmentToUpdate.getReason());

        AppointmentEntity updatedAppointment = appointmentRepository.save(existingAppointment);

        AppointmentResponse response = appointmentMapper.entityToResponse(updatedAppointment, ownerResponse, vetResponse);

        kafkaEventProducer.sendEventToKafkaServer(createAuditEvent(EventType.UPDATED, appointmentToUpdate, updatedAppointment));
        kafkaEventProducer.sendEventToKafkaServer(createAppointmentEvent(EventType.UPDATED, response));

        return response;
    }

    @Override
    public void deleteAppointmentById(Long id) {
        AppointmentEntity appointmentToDelete = getAppointmentByIdOrThrow(id);
        UserResponse ownerResponse = getUserByIdOrThrow(appointmentToDelete.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentToDelete.getVetId());
        AppointmentResponse response = appointmentMapper.entityToResponse(appointmentToDelete, ownerResponse, vetResponse);

        kafkaEventProducer.sendEventToKafkaServer(createAuditEvent(EventType.CANCELED, appointmentToDelete, appointmentToDelete));
        kafkaEventProducer.sendEventToKafkaServer(createAppointmentEvent(EventType.CANCELED, response));

        appointmentRepository.deleteById(id);
    }

    private AppointmentEntity getAppointmentByIdOrThrow(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment object not found for the given ID: " + appointmentId));
    }

    private void checkIsAppointmentRequestHasAnyUpdates(AppointmentEntity existingAppointment, AppointmentRequest appointmentRequest) {
        boolean isAppointmentsAreEqual = existingAppointment.getOwnerId().equals(appointmentRequest.getOwnerId()) &&
                existingAppointment.getVetId().equals(appointmentRequest.getVetId()) &&
                existingAppointment.getAppointmentDateTime().equals(appointmentRequest.getAppointmentDateTime()) &&
                existingAppointment.getReason().equals(appointmentRequest.getReason());

        if (isAppointmentsAreEqual) {
            throw new NoChangesMadeException("Appointment object was not updated as no changes of object were made.");
        }
    }

    private void checkDoesAppointmentForVetAlreadyExists(AppointmentEntity appointmentToSave) {

        boolean vetAppointmentExists = appointmentRepository.existsByVetIdAndAppointmentDateTime(
                appointmentToSave.getVetId(),
                appointmentToSave.getAppointmentDateTime()
        );

        if (vetAppointmentExists) {
            throw new AppointmentExistsException("Appointment for given veterinarian, date and time already exists");
        }
    }

    private UserResponse getUserByIdOrThrow(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User not found with id: " + userId);
        }
    }

    private AppointmentEvent createAppointmentEvent(EventType eventType, AppointmentResponse appointmentResponse) {
        return AppointmentEvent.builder()
                .eventType(eventType)
                .appointmentResponse(appointmentResponse)
                .build();
    }

    private AuditEvent createAuditEvent(EventType eventType,
                                        AppointmentEntity appointmentToUpdate,
                                        AppointmentEntity updatedAppointment) {
        return AuditEvent.builder()
                .serviceName("Appointment-service")
                .eventType(String.format("Appointment %s", eventType.toString()))
                .initiatorUserId(appointmentToUpdate.getVetId())
                .message(buildAuditEventMessage(eventType, appointmentToUpdate, updatedAppointment))
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    private String buildAuditEventMessage(EventType eventType,
                                          AppointmentEntity appointmentToUpdate,
                                          AppointmentEntity updatedAppointment) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("Appointment with Id %d was %s: ",
                appointmentToUpdate.getAppointmentId(),
                eventType.toString().toLowerCase(Locale.ROOT)));

        switch (eventType) {
            case UPDATED -> {
                String changes = getUpdateChanges(appointmentToUpdate, updatedAppointment);
                message.append(" from ");
                message.append(changes);
            }
            case CREATED, CANCELED -> {
                String value = getCreateAndCancelMessage(updatedAppointment);
                message.append(value);
            }
            default -> throw new IllegalArgumentException("Unsupported event type");
        }

        return message.toString();
    }

    private String getUpdateChanges(AppointmentEntity oldAppointment, AppointmentEntity newAppointment) {
        StringBuilder changes = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (!oldAppointment.getOwnerId().equals(newAppointment.getOwnerId())) {
            changes.append(String.format("ownerId: %d to ownerId: %d", oldAppointment.getOwnerId(), newAppointment.getOwnerId()));
        }

        if (!oldAppointment.getVetId().equals(newAppointment.getVetId())) {
            if (!changes.isEmpty()) {
                changes.append(", ");
            }
            changes.append(String.format("vetId: %d to vetId: %d", oldAppointment.getVetId(), newAppointment.getVetId()));
        }

        if (!oldAppointment.getAppointmentDateTime().equals(newAppointment.getAppointmentDateTime())) {
            if (!changes.isEmpty()) {
                changes.append(", ");
            }
            changes.append(String.format("appointmentDateTime: %s to appointmentDateTime: %s",
                    oldAppointment.getAppointmentDateTime().format(formatter),
                    newAppointment.getAppointmentDateTime().format(formatter)));
        }

        if (!oldAppointment.getReason().equals(newAppointment.getReason())) {
            if (!changes.isEmpty()) {
                changes.append(", ");
            }
            changes.append(String.format("reason: %s to reason: %s", oldAppointment.getReason(), newAppointment.getReason()));
        }

        return changes.toString();
    }

    private String getCreateAndCancelMessage(AppointmentEntity appointmentEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return String.format("ownerId: %d, vetId: %d, appointmentDateTime: %s, reason: %s",
                appointmentEntity.getOwnerId(),
                appointmentEntity.getVetId(),
                appointmentEntity.getAppointmentDateTime().format(formatter),
                appointmentEntity.getReason());
    }
}
