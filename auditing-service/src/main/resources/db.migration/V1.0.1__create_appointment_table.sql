USE petcareauditdb;

DROP TABLE IF EXISTS auditEntries;

CREATE TABLE appointments (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR NOT NULL,
    event_type VARCHAR NOT NULL,
    initiator_user_id BIGINT,
    message VARCHAR(2000) NOT NULL
    event_timestamp TIMESTAMP NOT NULL,
);