package com.roze.appointment_service.service;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;

public interface AppointmentService {
    AppointmentResponse saveAppointment(AppointmentRequest appointmentRequest);

    AppointmentResponse findAppointmentById(Long id);

    AppointmentResponse updateAppointmentById(Long id, AppointmentRequest appointmentRequest);

    void deleteAppointmentById(Long id);
}
