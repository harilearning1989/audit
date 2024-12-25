package com.web.rest.audit.repos;

import com.web.rest.audit.entities.ServiceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAuditRepo extends JpaRepository<ServiceAudit, Long> {
}
