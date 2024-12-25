package com.web.audit.services;

import com.web.audit.entities.ServiceAudit;

import java.util.List;
import java.util.Optional;

public interface AuditService {
    ServiceAudit saveAudit(ServiceAudit serviceAudit);

    List<ServiceAudit> findAll();

    Optional<ServiceAudit> findById(Long auditLogId);
}
