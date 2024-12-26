package com.web.audit.filters;

import com.web.audit.entities.ClientAudit;
import com.web.audit.entities.ServiceAudit;
import com.web.audit.repos.ClientAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class AuditExchangeFilter implements ExchangeFilterFunction {

    @Autowired
    private ClientAuditRepository clientAuditRepository;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        LocalDateTime startTime = LocalDateTime.now();
        return next.exchange(request)
                .doOnNext(response -> {
                    // Here we need to log the client audit details in emp-service
                    logClientAudit(request, response, startTime);
                })
                .doOnError(error -> {
                    logClientError(request, startTime, error);
                });
    }

    private void logClientAudit(ClientRequest request, ClientResponse response, LocalDateTime startTime) {
        // Retrieve the ServiceAudit object from AuditContext to link the client audit
        ServiceAudit serviceAudit = new ServiceAudit();//AuditContext.getServiceAudit();

        if (serviceAudit != null) {
            ClientAudit clientAudit = new ClientAudit();
            clientAudit.setExternalApiUrl(request.url().toString());
            clientAudit.setMethod(request.method().toString());
            clientAudit.setRequestTimestamp(startTime);
            clientAudit.setResponseTimestamp(LocalDateTime.now());
            clientAudit.setResponse(response.bodyToMono(String.class).block());
            clientAudit.setResponseStatus(response.statusCode().value());

           /* Long startTime = (Long) request.getAttribute(START_TIME);
            long duration = System.currentTimeMillis() - startTime;
            clientAudit.setDuration(java.time.Duration.between(startTime, LocalDateTime.now()).toMillis());*/

            // Link this client audit with the service audit
            //serviceAudit.addClientAudit(clientAudit);

            // You should save this client audit
            clientAuditRepository.save(clientAudit);
        }
    }

    private void logClientError(ClientRequest request, LocalDateTime startTime, Throwable error) {
        // Log error details if external API call fails
        /*ServiceAudit serviceAudit = AuditContext.getServiceAudit();
        if (serviceAudit != null) {
            ClientAudit clientAudit = new ClientAudit();
            clientAudit.setExternalApiUrl(request.url().toString());
            clientAudit.setMethod(request.method().toString());
            clientAudit.setRequestTimestamp(startTime);
            clientAudit.setResponse(error.getMessage());
            clientAudit.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            serviceAudit.addClientAudit(clientAudit);

            clientAuditRepository.save(clientAudit);
        }*/
    }
}

