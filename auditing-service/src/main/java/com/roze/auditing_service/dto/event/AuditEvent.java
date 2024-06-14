package com.roze.auditing_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent {
    private String serviceName;
    private String eventType;
    private Long initiatorUserId;
    private String message;
    private Timestamp timestamp;
}
