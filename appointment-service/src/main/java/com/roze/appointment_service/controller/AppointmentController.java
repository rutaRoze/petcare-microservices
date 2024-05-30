package com.roze.appointment_service.controller;

import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("api/v1/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping()
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest appointmentRequest) {
        AppointmentResponse appointmentResponse = appointmentService.saveAppointment(appointmentRequest);

        return new ResponseEntity<>(appointmentResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @Min(1) @PathVariable Long id) {
        AppointmentResponse appointmentResponse = appointmentService.findAppointmentById(id);

        return ResponseEntity.ok(appointmentResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointmentById(
            @Min(1) @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest appointmentRequest) {
        AppointmentResponse appointmentResponse = appointmentService.updateAppointmentById(id, appointmentRequest);

        return new ResponseEntity<>(appointmentResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointmentById(
            @Min(1) @PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);

        return ResponseEntity.noContent().build();
    }
}
