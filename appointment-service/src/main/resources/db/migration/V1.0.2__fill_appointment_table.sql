USE petcareappointmentdb;

INSERT INTO appointments (owner_id, vet_id, appointment_datetime, reason)
VALUES
    (2, 1, STR_TO_DATE('2024,06,01 09,00,00', '%Y,%m,%d %H,%i,%s'), 'Regular checkup'),
    (3, 5, STR_TO_DATE('2024,06,05 14,00,00', '%Y,%m,%d %H,%i,%s'), 'Vaccination'),
    (7, 1, STR_TO_DATE('2024,06,15 10,30,00', '%Y,%m,%d %H,%i,%s'), 'Surgery');