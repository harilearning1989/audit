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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REQUEST_URI")
    private String requestUrl;

    @Column(name = "REQUEST_TIMESTAMP")
    private LocalDateTime requestTimestamp;

    @Column(name = "RESPONSE_TIMESTAMP")
    private LocalDateTime responseTimestamp;

    @Column(name = "METHOD")
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

    @Column(name = "HOST_NAME")
    private String hostname;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    //private Map<String, String> requestHeadersMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_AUDIT_ID")
    private ServiceAudit serviceAudit;
}
