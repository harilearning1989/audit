package com.web.audit.services;

import com.web.audit.entities.ServiceAudit;
import com.web.audit.repos.ServiceAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    private ServiceAuditRepository serviceAuditRepository;

    @Override
    public ServiceAudit saveAudit(ServiceAudit serviceAudit) {
        try {
            return serviceAuditRepository.save(serviceAudit);
        } catch (Exception e) {
            // Check if the exception is due to ORA-12541
            if (e.getMessage() != null && e.getMessage().contains("ORA-12541")) {
                LOGGER.warn("Unable to save audit data: {}", e.getMessage());
            } else {
                LOGGER.error("Unexpected error while saving audit data", e);
            }
            return null;
        }
    }

    @Override
    public List<ServiceAudit> findAll() {
        return serviceAuditRepository.findAll();
    }

    @Override
    public Optional<ServiceAudit> findById(Long auditLogId) {
        return serviceAuditRepository.findById(auditLogId);
    }
}
