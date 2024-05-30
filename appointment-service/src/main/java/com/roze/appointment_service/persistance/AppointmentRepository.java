package com.roze.appointment_service.persistance;

import com.roze.appointment_service.persistance.model.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    boolean existsByVetIdAndAppointmentDateTime(
            Long vetId,
            LocalDateTime appointmentDateTime);
}
