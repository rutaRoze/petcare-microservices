USE petcareappointmentdb;

DROP TABLE IF EXISTS appointments;

CREATE TABLE appointments (
    appointment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    vet_id BIGINT NOT NULL,
    appointment_datetime DATETIME NOT NULL,
    reason VARCHAR(500) NOT NULL
);