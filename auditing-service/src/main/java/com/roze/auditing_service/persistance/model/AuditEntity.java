package com.roze.auditing_service.persistance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auditEntries")
public class AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "initiator_user_id")
    private Long initiatorUserId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "time_stamp", nullable = false)
    private Timestamp timestamp;
}
