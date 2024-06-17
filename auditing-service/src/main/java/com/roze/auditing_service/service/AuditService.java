package com.roze.auditing_service.service;


import com.roze.auditing_service.dto.event.AuditEvent;

public interface AuditService {

    void saveAuditEvent(AuditEvent auditEvent);
}
