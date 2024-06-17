package com.roze.appointment_service.service;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.enums.RoleName;
import com.roze.appointment_service.exception.AppointmentExistsException;
import com.roze.appointment_service.exception.NoChangesMadeException;
import com.roze.appointment_service.exception.NotFoundException;
import com.roze.appointment_service.feign.UserClient;
import com.roze.appointment_service.mapper.AppointmentMapper;
import com.roze.appointment_service.persistance.AppointmentRepository;
import com.roze.appointment_service.persistance.model.AppointmentEntity;
import com.roze.appointment_service.service.impl.AppointmentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static Long validId = 1L;
    public static Long nonExistentId = 999L;

    @Mock
    private AppointmentRepository appointmentRepositoryMock;
    @Mock
    private AppointmentMapper appointmentMapperMock;
    @Mock
    private UserClient userClientMock;
    @InjectMocks
    private AppointmentServiceImpl appointmentServiceMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private AppointmentEntity setUpAppointmentEntity() {
        return AppointmentEntity.builder()
                .ownerId(validId)
                .vetId(2L)
                .appointmentDateTime(LocalDateTime
                        .of(2024, 8, 8, 10, 30, 0))
                .reason("Regular checkup")
                .build();
    }

    private AppointmentRequest setUpAppointmentRequest() {
        return AppointmentRequest.builder()
                .ownerId(validId)
                .vetId(2L)
                .appointmentDateTime(LocalDateTime
                        .of(2024, 8, 8, 10, 30, 0))
                .reason("Regular checkup")
                .build();
    }

    private AppointmentResponse setUpAppointmentResponse() {
        return AppointmentResponse.builder()
                .appointmentId(validId)
                .ownerName("John")
                .ownerSurname("Doe")
                .vetName("Jin")
                .vetSurname("Din")
                .appointmentDateTime(LocalDateTime
                        .of(2024, 8, 8, 10, 30, 0))
                .reason("Regular checkup")
                .build();
    }

    private UserResponse setUpOwnerUserResponse() {
        return UserResponse.builder()
                .id(validId)
                .name("John")
                .surname("Doe")
                .email("john@doe.com")
                .phoneNumber("1234567890")
                .roleNames(List.of(RoleName.OWNER))
                .build();
    }

    private UserResponse setUpVetUserResponse() {
        return UserResponse.builder()
                .id(2L)
                .name("Jin")
                .surname("Din")
                .email("jinn@din.com")
                .phoneNumber("1234567890")
                .roleNames(List.of(RoleName.VET))
                .build();
    }

    @Test
    void saveAppointment_WhenSuccessful() {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        AppointmentEntity appointmentEntity = setUpAppointmentEntity();
        AppointmentResponse appointmentResponse = setUpAppointmentResponse();
        UserResponse ownerResponse = setUpOwnerUserResponse();
        UserResponse vetResponse = setUpVetUserResponse();

        when(userClientMock.getUserById(appointmentRequest.getOwnerId())).thenReturn(ownerResponse);
        when(userClientMock.getUserById(appointmentRequest.getVetId())).thenReturn(vetResponse);
        when(appointmentMapperMock.requestToEntity(appointmentRequest)).thenReturn(appointmentEntity);
        when(appointmentRepositoryMock.existsByVetIdAndAppointmentDateTime(
                appointmentEntity.getVetId(), appointmentEntity.getAppointmentDateTime())).thenReturn(false);
        when(appointmentRepositoryMock.save(appointmentEntity)).thenReturn(appointmentEntity);
        when(appointmentMapperMock.entityToResponse(appointmentEntity, ownerResponse, vetResponse))
                .thenReturn(appointmentResponse);

        AppointmentResponse result = appointmentServiceMock.saveAppointment(appointmentRequest);

        assertEquals(appointmentResponse, result);
        verify(appointmentRepositoryMock, times(1)).save(appointmentEntity);
    }

    @Test
    void saveAppointment_WhenOwnerNotFound_ThrowsNotFoundException() {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        AppointmentEntity appointmentEntity = setUpAppointmentEntity();

        when(userClientMock.getUserById(appointmentRequest.getOwnerId()))
                .thenThrow(new NotFoundException("User not found with id: " + validId));

        NotFoundException thrownException = assertThrows(NotFoundException.class, () ->
                appointmentServiceMock.saveAppointment(appointmentRequest));

        assertEquals("User not found with id: " + validId, thrownException.getMessage());
        verify(appointmentRepositoryMock, times(0)).save(appointmentEntity);
    }

    @Test
    void saveAppointment_WhenVetAppointmentExists_ThrowsAppointmentExistsException() {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        AppointmentEntity appointmentEntity = setUpAppointmentEntity();
        UserResponse ownerResponse = setUpOwnerUserResponse();
        UserResponse vetResponse = setUpVetUserResponse();

        when(userClientMock.getUserById(appointmentRequest.getOwnerId())).thenReturn(ownerResponse);
        when(userClientMock.getUserById(appointmentRequest.getVetId())).thenReturn(vetResponse);
        when(appointmentMapperMock.requestToEntity(appointmentRequest)).thenReturn(appointmentEntity);
        when(appointmentRepositoryMock.existsByVetIdAndAppointmentDateTime(
                appointmentEntity.getVetId(), appointmentEntity.getAppointmentDateTime())).thenReturn(true);

        AppointmentExistsException thrownException = assertThrows(AppointmentExistsException.class, () ->
                appointmentServiceMock.saveAppointment(appointmentRequest));

        assertEquals("Appointment for given veterinarian, date and time already exists",
                thrownException.getMessage());
        verify(appointmentRepositoryMock, times(0)).save(appointmentEntity);
    }

    @Test
    void findAppointmentById_WhenSuccessful() {
        AppointmentResponse appointmentResponse = setUpAppointmentResponse();
        AppointmentEntity appointmentEntity = setUpAppointmentEntity();
        UserResponse ownerResponse = setUpOwnerUserResponse();
        UserResponse vetResponse = setUpVetUserResponse();

        when(appointmentRepositoryMock.findById(validId)).thenReturn(Optional.of(appointmentEntity));
        when(userClientMock.getUserById(eq(appointmentEntity.getOwnerId()))).thenReturn(ownerResponse);
        when(userClientMock.getUserById(eq(appointmentEntity.getVetId()))).thenReturn(vetResponse);
        when(appointmentMapperMock.entityToResponse(appointmentEntity, ownerResponse, vetResponse))
                .thenReturn(appointmentResponse);

        AppointmentResponse result = appointmentServiceMock.findAppointmentById(validId);

        assertEquals(appointmentResponse, result);
        verify(appointmentRepositoryMock, times(1)).findById(validId);
    }

    @Test
    void findAppointmentById_WhenNotFound() {
        when(appointmentRepositoryMock.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException thrownException = assertThrows(EntityNotFoundException.class, () ->
                appointmentServiceMock.findAppointmentById(nonExistentId));

        assertEquals("Appointment object not found for the given ID: " + nonExistentId,
                thrownException.getMessage());
        verify(appointmentRepositoryMock, times(1)).findById(nonExistentId);
    }

    @Test
    void updateAppointmentById_WhenSuccessful() {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        appointmentRequest.setReason("New reason for appointment");
        AppointmentEntity appointmentEntity = setUpAppointmentEntity();
        AppointmentResponse appointmentResponse = setUpAppointmentResponse();
        UserResponse ownerResponse = setUpOwnerUserResponse();
        UserResponse vetResponse = setUpVetUserResponse();

        when(appointmentRepositoryMock.findById(validId)).thenReturn(Optional.of(appointmentEntity));
        when(userClientMock.getUserById(appointmentRequest.getOwnerId())).thenReturn(ownerResponse);
        when(userClientMock.getUserById(appointmentRequest.getVetId())).thenReturn(vetResponse);
        when(appointmentMapperMock.requestToEntity(appointmentRequest)).thenReturn(appointmentEntity);
        when(appointmentRepositoryMock.existsByVetIdAndAppointmentDateTime(
                appointmentEntity.getVetId(), appointmentEntity.getAppointmentDateTime())).thenReturn(false);
        when(appointmentRepositoryMock.save(appointmentEntity)).thenReturn(appointmentEntity);
        when(appointmentMapperMock.entityToResponse(appointmentEntity, ownerResponse, vetResponse))
                .thenReturn(appointmentResponse);

        AppointmentResponse result = appointmentServiceMock.updateAppointmentById(validId, appointmentRequest);

        assertEquals(appointmentResponse, result);
        verify(appointmentRepositoryMock, times(1)).save(appointmentEntity);
    }

    @Test
    void updateAppointmentById_WhenNoChangesMade_ThrowsNoChangesMadeException() {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        AppointmentEntity appointmentEntity = setUpAppointmentEntity();

        when(appointmentRepositoryMock.findById(validId)).thenReturn(Optional.of(appointmentEntity));

        NoChangesMadeException thrownException = assertThrows(NoChangesMadeException.class, () ->
                appointmentServiceMock.updateAppointmentById(validId, appointmentRequest));

        assertEquals("Appointment object was not updated as no changes of object were made.",
                thrownException.getMessage());
        verify(appointmentRepositoryMock, times(0)).save(appointmentEntity);
    }

    @Test
    void deleteAppointmentById_WhenSuccessful() {
        AppointmentEntity appointmentEntity = setUpAppointmentEntity();

        when(appointmentRepositoryMock.findById(validId)).thenReturn(Optional.of(appointmentEntity));

        appointmentServiceMock.deleteAppointmentById(validId);

        verify(appointmentRepositoryMock, times(1)).deleteById(validId);
    }

    @Test
    void deleteAppointmentById_WhenNotFound_ThrowsEntityNotFoundException() {
        when(appointmentRepositoryMock.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException thrownException = assertThrows(EntityNotFoundException.class, () ->
                appointmentServiceMock.deleteAppointmentById(nonExistentId));

        assertEquals("Appointment object not found for the given ID: " + nonExistentId,
                thrownException.getMessage());
        verify(appointmentRepositoryMock, times(0)).deleteById(nonExistentId);
    }
}
