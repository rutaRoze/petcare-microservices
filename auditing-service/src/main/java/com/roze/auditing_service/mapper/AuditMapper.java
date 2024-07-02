package com.roze.auditing_service.mapper;

import com.roze.auditing_service.dto.event.AuditEvent;
import com.roze.auditing_service.persistance.model.AuditEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditMapper {
    AuditEntity auditEventToAuditEntity(AuditEvent auditEvent);

    AuditEvent auditEntityToAuditEvent(AuditEntity auditEntity);
}
