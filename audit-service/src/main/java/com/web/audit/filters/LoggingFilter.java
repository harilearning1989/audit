package com.web.audit.filters;

import com.web.audit.entities.ClientAudit;
import com.web.audit.repos.ClientAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class LoggingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    private final ClientAuditRepository logDetailsRepository;

    public LoggingFilter(ClientAuditRepository logDetailsRepository) {
        this.logDetailsRepository = logDetailsRepository;
    }

    public ExchangeFilterFunction logRequestAndResponse() {
        return (request, next) -> {
            ClientAudit logDetails = createLogDetailsFromRequest(request);
            ClientAudit logDetailsTmp = logDetailsRepository.save(logDetails);
            logDetails.setRequestTimestamp(LocalDateTime.now());
            logDetails.setId(logDetailsTmp.getId());
            LOGGER.info("ClientAudit logDetails::{}", logDetails);
            return next.exchange(request)
                    .flatMap(response -> handleResponse(response, logDetails))
                    .doOnError(error -> handleError(logDetails, error));
        };
    }

    private ClientAudit createLogDetailsFromRequest(ClientRequest request) {
        ClientAudit logDetails = new ClientAudit();
        logDetails.setMethod(request.method().name());
        logDetails.setRequestUrl(request.url().toString());
        logDetails.setHostname(request.url().toString());
        logDetails.setRequestTimestamp(LocalDateTime.now());
        /*logDetails.setRequestHeadersMap(request.headers()
                .toSingleValueMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));*/
        return logDetails;
    }

    private Mono<ClientResponse> handleResponse(ClientResponse response, ClientAudit logDetails) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(responseBody -> {
                    logDetails.setResponse(responseBody);
                    logDetails.setResponseTimestamp(LocalDateTime.now());
                    logDetails.setResponseStatus(response.statusCode().value());
                    if (response.statusCode().isError()) {
                        logDetails.setErrorMessage(responseBody);
                    }
                    logDetails(logDetails);
                    logDetailsRepository.save(logDetails);

                    // Create a new ClientResponse with the buffered body
                    return Mono.just(
                            ClientResponse.create(response.statusCode())
                                    .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                                    .body(responseBody)
                                    .build()
                    );
                });
    }

    private void handleError(ClientAudit logDetails, Throwable error) {
        //logDetails.setError(true);
        logDetails.setErrorMessage(error.getMessage());
        logDetails.setResponseTimestamp(LocalDateTime.now());
        logDetails(logDetails);
        logDetailsRepository.save(logDetails);
    }

    private void logDetails(ClientAudit logDetails) {
        LOGGER.info("ClientAudit Complete logDetails::{}", logDetails);
        //if (logDetails.isError()) {
            //System.err.println("Error Message: " + logDetails.getErrorMessage());
       // }
    }

}
