package com.web.audit.context;

import com.web.audit.entities.ServiceAudit;

public class AuditContext {

    private static final ThreadLocal<ServiceAudit> serviceAuditThreadLocal = new ThreadLocal<>();

    public static void setServiceAudit(ServiceAudit serviceAudit) {
        serviceAuditThreadLocal.set(serviceAudit);
    }

    public static ServiceAudit getServiceAudit() {
        return serviceAuditThreadLocal.get();
    }

    public static void clear() {
        serviceAuditThreadLocal.remove();
    }
}

