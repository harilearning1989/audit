package com.web.audit.repos;

import com.web.audit.entities.ServiceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAuditRepository extends JpaRepository<ServiceAudit,Long> {
}
