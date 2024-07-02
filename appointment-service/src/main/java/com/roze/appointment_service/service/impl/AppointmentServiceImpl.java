package com.roze.appointment_service.service.impl;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.enums.EventType;
import com.roze.appointment_service.exception.AppointmentExistsException;
import com.roze.appointment_service.exception.NoChangesMadeException;
import com.roze.appointment_service.exception.NotFoundException;
import com.roze.appointment_service.factory.AppointmentEventFactory;
import com.roze.appointment_service.factory.AuditEventFactory;
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

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    @Autowired
    private AppointmentEventFactory appointmentEventFactory;

    @Autowired
    private AuditEventFactory auditEventFactory;

    @Override
    public AppointmentResponse saveAppointment(AppointmentRequest appointmentRequest) {
        UserResponse ownerResponse = getUserByIdOrThrow(appointmentRequest.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentRequest.getVetId());

        AppointmentEntity appointmentToSave = appointmentMapper.requestToEntity(appointmentRequest);

        checkDoesAppointmentForVetAlreadyExists(appointmentToSave);

        AppointmentEntity savedAppointment = appointmentRepository.save(appointmentToSave);

        AppointmentResponse response = appointmentMapper.entityToResponse(savedAppointment, ownerResponse, vetResponse);

        kafkaEventProducer.sendEventToKafkaServer(auditEventFactory
                .createAuditEvent(EventType.CREATED, null, savedAppointment));
        kafkaEventProducer.sendEventToKafkaServer(appointmentEventFactory
                .createAppointmentEvent(EventType.CREATED, response));

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
        String existingAppointmentForAuditEntry = existingAppointment.toString();

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

        kafkaEventProducer.sendEventToKafkaServer(auditEventFactory
                .createAuditEvent(EventType.UPDATED,
                        existingAppointmentForAuditEntry,
                        updatedAppointment));
        kafkaEventProducer.sendEventToKafkaServer(appointmentEventFactory
                .createAppointmentEvent(EventType.UPDATED, response));

        return response;
    }

    @Override
    public void deleteAppointmentById(Long id) {
        AppointmentEntity appointmentToDelete = getAppointmentByIdOrThrow(id);
        UserResponse ownerResponse = getUserByIdOrThrow(appointmentToDelete.getOwnerId());
        UserResponse vetResponse = getUserByIdOrThrow(appointmentToDelete.getVetId());
        AppointmentResponse response = appointmentMapper.entityToResponse(appointmentToDelete, ownerResponse, vetResponse);

        kafkaEventProducer.sendEventToKafkaServer(auditEventFactory
                .createAuditEvent(EventType.CANCELED, null, appointmentToDelete));
        kafkaEventProducer.sendEventToKafkaServer(appointmentEventFactory
                .createAppointmentEvent(EventType.CANCELED, response));

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
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }
}
