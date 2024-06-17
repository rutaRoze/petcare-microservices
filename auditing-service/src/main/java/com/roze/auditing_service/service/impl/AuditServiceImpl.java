package com.roze.auditing_service.service.impl;

import com.roze.auditing_service.dto.event.AuditEvent;
import com.roze.auditing_service.mapper.AuditMapper;
import com.roze.auditing_service.persistance.AuditRepository;
import com.roze.auditing_service.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private AuditMapper auditMapper;

    @Override
    public void saveAuditEvent(AuditEvent auditEvent) {
        auditRepository.save(auditMapper.auditEventToAuditEntity(auditEvent));
        log.info("Audit event saved to the database: {}", auditEvent);
    }
}
