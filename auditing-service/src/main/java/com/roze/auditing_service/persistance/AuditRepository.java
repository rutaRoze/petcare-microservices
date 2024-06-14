package com.roze.auditing_service.persistance;

import com.roze.auditing_service.persistance.model.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, Long> {

}
