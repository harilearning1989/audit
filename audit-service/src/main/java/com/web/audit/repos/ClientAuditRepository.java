package com.web.audit.repos;

import com.web.audit.entities.ClientAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAuditRepository extends JpaRepository<ClientAudit, Long> {
}
