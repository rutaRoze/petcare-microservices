package com.roze.appointment_service.exception;

public class AppointmentExistsException extends RuntimeException {
    public AppointmentExistsException(String message) {
        super(message);
    }
}
