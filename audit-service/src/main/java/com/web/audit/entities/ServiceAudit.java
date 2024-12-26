package com.web.audit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "SERVICE_AUDIT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SERVICE_NAME", nullable = false)
    private String serviceName;

    @Column(name = "HOST_NAME", nullable = false)
    private String hostName;

    @Column(name = "REQUEST_URI", nullable = false)
    private String requestUri;

    @Column(name = "REQUEST_METHOD", nullable = false)
    private String requestMethod;

    @Lob
    @Column(name = "REQUEST_HEADERS",nullable = true)
    private String requestHeaders;

    @Lob
    @Column(name = "REQUEST_BODY",nullable = true)
    private String requestBody;

    @Column(name = "REQUEST_TIMESTAMP", nullable = true)
    private LocalDateTime requestTimestamp;

    @Column(name = "RESPONSE_TIMESTAMP", nullable = true)
    private LocalDateTime responseTimestamp;

    @Column(name = "RESPONSE_STATUS")
    private Integer responseStatus;

    @Lob
    @Column(name = "RESPONSE",nullable = true)
    private String response;

    @Column(name = "DURATION")
    private Long duration;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    //@OneToMany(mappedBy = "serviceAudit", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<ClientAudit> clientAudits;

}
