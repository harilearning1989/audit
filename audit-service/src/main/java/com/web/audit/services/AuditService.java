package com.web.audit.services;

import com.web.audit.entities.ClientAudit;
import com.web.audit.entities.ServiceAudit;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface AuditService {
    ServiceAudit saveAudit(ServiceAudit serviceAudit);

    List<ServiceAudit> findAll();

    Optional<ServiceAudit> findById(Long auditLogId);

    ClientAudit saveClientAudit(ClientAudit audit);

    Optional<ClientAudit> findClientAuditById(Long auditId);

    Mono<ClientAudit> updateAuditLog(String uuid, Consumer<ClientAudit> updateLogic);

}
