package com.web.audit.controls;

import com.web.audit.entities.ServiceAudit;
import com.web.audit.services.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("audit")
public class AuditRestController {

    private static final Logger logger = LoggerFactory.getLogger(AuditRestController.class);
    @Autowired
    private AuditService auditService;

    @GetMapping("findAll")
    public List<ServiceAudit> findAll(){
        return auditService.findAll();
    }
}
