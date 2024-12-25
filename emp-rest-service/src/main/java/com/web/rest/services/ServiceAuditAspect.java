package com.web.rest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.rest.audit.entities.ServiceAudit;
import com.web.rest.audit.repos.ServiceAuditRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Aspect
@Component
public class ServiceAuditAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAuditAspect.class);
    @Autowired
    private ServiceAuditRepo serviceAuditRepository;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logApiDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.info("=========logApiDetails starting===========");
        // Capture start time
        LocalDateTime startTime = LocalDateTime.now();
        long startMillis = System.currentTimeMillis();

        // Get request details
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestMethod = request.getMethod();
        String requestPayload = new ObjectMapper().writeValueAsString(joinPoint.getArgs());
        String requestHeaders = new ObjectMapper().writeValueAsString(Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader)));

        String hostname = InetAddress.getLocalHost().getHostName();
        String requestUri = request.getRequestURI();

        // Proceed with the API call
        Object response;
        try {
            response = joinPoint.proceed();
        } catch (Throwable e) {
            saveAudit(requestMethod, requestPayload, requestHeaders, startTime,hostname,requestUri,
                    500, e.getMessage(), System.currentTimeMillis() - startMillis);
            throw e;
        }

        // Capture end time and response
        LocalDateTime endTime = LocalDateTime.now();
        long endMillis = System.currentTimeMillis();
        int statusCode = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse().getStatus();

        String responseData = new ObjectMapper().writeValueAsString(response);

        // Save audit
        saveAudit(requestMethod, requestPayload, requestHeaders, startTime,hostname,requestUri,
                statusCode, responseData, endMillis - startMillis);
        LOGGER.info("=========logApiDetails ending===========");
        return response;
    }

    private void saveAudit(String requestMethod, String requestPayload, String requestHeaders,
                           LocalDateTime startTime,String hostname,String requestUri, int statusCode, String responseData, long durationMs) {
        ServiceAudit audit = new ServiceAudit();
        audit.setRequestMethod(requestMethod);
        audit.setRequestBody(requestPayload);
        audit.setRequestHeaders(requestHeaders);
        audit.setRequestTimestamp(startTime);
        audit.setResponseStatus(statusCode);
        //audit.setResponseData(responseData);
        audit.setResponseTimestamp(startTime.plusNanos(durationMs * 1_000_000));
        audit.setDuration(durationMs);
        audit.setHostName(hostname);
        audit.setRequestUri(requestUri);
        audit.setServiceName(requestUri);

        LOGGER.info("=========saveAudit before save===========");
        serviceAuditRepository.save(audit);
    }
}
