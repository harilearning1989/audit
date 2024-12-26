package com.web.audit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLIENT_AUDIT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAudit {

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private String id;

    @Column(name = "EXTERNAL_API_URL", nullable = false)
    private String externalApiUrl;

    @Column(name = "REQUEST_TIMESTAMP", nullable = true)
    private LocalDateTime requestTimestamp;

    @Column(name = "RESPONSE_TIMESTAMP", nullable = true)
    private LocalDateTime responseTimestamp;

    @Column(name = "METHOD", nullable = false)
    private String method;

    @Lob
    @Column(name = "REQUEST_HEADERS")
    private String requestHeaders;

    @Lob
    @Column(name = "REQUEST_BODY")
    private String requestBody;

    @Lob
    @Column(name = "RESPONSE")
    private String response;

    @Column(name = "RESPONSE_STATUS")
    private Integer responseStatus;

    @Column(name = "DURATION")
    private Integer duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_AUDIT_ID", nullable = false)
    private ServiceAudit serviceAudit;
}
