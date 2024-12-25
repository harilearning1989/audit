package com.web.audit.services;

import com.web.audit.entities.ServiceAudit;
import com.web.audit.repos.ServiceAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private ServiceAuditRepository serviceAuditRepository;

    @Override
    public ServiceAudit saveAudit(ServiceAudit serviceAudit) {
        return serviceAuditRepository.save(serviceAudit);
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
