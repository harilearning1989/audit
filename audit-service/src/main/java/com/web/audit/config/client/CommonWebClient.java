package com.web.audit.config.client;

import com.web.audit.entities.ClientAudit;
import com.web.audit.filters.AuditExchangeFilter;
import com.web.audit.filters.LoggingFilter;
import com.web.audit.repos.ClientAuditRepository;
import com.web.audit.services.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CommonWebClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonWebClient.class);

    private final ClientAuditRepository logDetailsRepository;

    @Autowired
    private AuditExchangeFilter auditExchangeFilter;

    Map<String, ClientAudit> map = new HashMap<>();

    /*@Autowired
    private ClientAuditRepository clientAuditRepository;*/

    @Autowired
    private AuditService auditService;

    public CommonWebClient(ClientAuditRepository logDetailsRepository) {
        this.logDetailsRepository = logDetailsRepository;
    }

    public <T> T httpServiceProxyFactory(String baseUrl, Map<String, String> headers, Class<T> clientClass) {
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(createWebClient(baseUrl, headers, auditService)))
                .build()
                .createClient(clientClass);
    }

    public WebClient createWebClient(String baseUrl, Map<String, String> headers, AuditService auditService) {
        //ContextPropagationOperator.install();
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                        .filter(new LoggingFilter(logDetailsRepository).logRequestAndResponse());
                //.filter(logAndSaveRequestDetails(auditService))
                //.filter(logAndSaveResponseDetails(auditService))
                //.filter(logAndSaveErrorDetails(auditService));

        //.filter(auditExchangeFilter);
        //.filter(logRequest())
        //.filter(logResponse());
        //.filter(auditFilter());
        headers.forEach(builder::defaultHeader);
        return builder.build();
    }

    private ExchangeFilterFunction logAndSaveRequestDetails(AuditService auditService) {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            String clientAuditId = UUID.randomUUID().toString();
            LOGGER.info("ExchangeFilterFunction logAndSaveRequestDetails entry point auditService::{} clientAuditId::{}", auditService, clientAuditId);
            ClientAudit audit = new ClientAudit();
            audit.setMethod(request.method().name());
            audit.setRequestHeaders(request.headers().toString());
            audit.setHostname(request.url().getHost());
            audit.setRequestBody(getBody(request));
            audit.setRequestTimestamp(LocalDateTime.now());
            //audit.setEndPoint(request.url().toString());
            audit.setRequestUrl(request.url().toString());
           /* long id;
            if (auditService != null) {
                audit = auditService.saveClientAudit(audit);
                id = audit.getId();
            } else {
                id = 0;
            }
            return id;*/

            //clientAuditService.saveAuditLog(audit).subscribe();
            LOGGER.info("ExchangeFilterFunction logAndSaveRequestDetails audit::{}", audit);
            map.put(clientAuditId, audit);
            //return Mono.just(request);
            // Add UUID as a header to propagate it through the request
            /*return Mono.just(ClientRequest.from(request)
                    .header("X-Request-UUID", clientAuditId)
                    .build());*/
            // Return the client request and store the UUID in Reactor Context
            return Mono.deferContextual(context -> Mono.just(ClientRequest.from(request)
                            .build())
                    .contextWrite(ctx -> ctx.put("X-Request-UUID", clientAuditId)));
        });
    }

    private ExchangeFilterFunction logAndSaveResponseDetails(AuditService auditService) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> Mono.deferContextual(context -> {
            String uuid = context.getOrDefault("X-Request-UUID", null);
            LOGGER.info("ExchangeFilterFunction logAndSaveResponseDetails uuid::{}", uuid);
            if (uuid != null) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(responseBody -> {
                            /*return auditService.updateAuditLog(uuid, audit -> {
                                audit.setResponseDetails("Status Code: " + clientResponse.statusCode() + ", Body: " + responseBody);
                            });*/
                            ClientAudit clientAudit = map.get(uuid);
                            clientAudit.setResponseStatus(clientResponse.statusCode().value());
                            clientAudit.setResponse(responseBody);
                            LOGGER.info("ExchangeFilterFunction logAndSaveResponseDetails clientAudit::{}", clientAudit);
                            return Mono.just(clientAudit);
                        });
            }
            return Mono.just(clientResponse);
        }).then(Mono.just(clientResponse)));
    }

    private ExchangeFilterFunction logAndSaveResponseDetailsTemp(AuditService auditService) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(responseBody -> {
                    ClientResponse.Headers headers = clientResponse.headers();
                    LOGGER.info("ExchangeFilterFunction logAndSaveResponseDetails entry point headers::{}", headers.toString());
                    String clientAuditId = clientResponse.headers()
                            .asHttpHeaders()
                            .getFirst("X-Request-UUID");
                    LOGGER.info("ExchangeFilterFunction logAndSaveResponseDetails entry point clientAuditId::{}", clientAuditId);
                    if (clientAuditId != null) {
                        ClientAudit clientAudit = map.get(clientAuditId);
                        clientAudit.setResponseStatus(clientResponse.statusCode().value());
                        clientAudit.setResponseTimestamp(LocalDateTime.now());
                        clientAudit.setResponse(responseBody);
                        /*return auditService.updateAuditLog(clientAuditId, audit -> {
                            audit.setResponseStatus(clientResponse.statusCode().value());
                            audit.setResponseTimestamp(LocalDateTime.now());
                            audit.setResponse(responseBody);
                        });*/
                        return Mono.just(clientAudit);
                    }
                    return Mono.empty();
                })
                .then(Mono.just(clientResponse)));
    }

    private ExchangeFilterFunction logAndSaveErrorDetails(AuditService auditService) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            String clientAuditId = clientResponse.headers()
                                    .asHttpHeaders()
                                    .getFirst("X-Request-UUID");

                            LOGGER.info("ExchangeFilterFunction logAndSaveErrorDetails entry point clientAuditId::{}", clientAuditId);

                            if (clientAuditId != null) {
                                return auditService.updateAuditLog(clientAuditId, audit -> {
                                    audit.setResponseStatus(clientResponse.statusCode().value());
                                    audit.setResponseTimestamp(LocalDateTime.now());
                                    audit.setErrorMessage(errorBody);
                                });
                            }
                            return Mono.empty();
                        })
                        .then(Mono.just(clientResponse));
            }
            return Mono.just(clientResponse);
        });
    }

    public ExchangeFilterFunction auditFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request ->
                Mono.fromCallable(() -> {
                            try {
                                LOGGER.info("======auditFilter fromCallable entry point======");
                                ClientAudit audit = new ClientAudit();
                                audit.setMethod(request.method().name());
                                audit.setRequestHeaders(request.headers().toString());
                                audit.setHostname(request.url().getHost());
                                audit.setRequestBody(getBody(request)); // Optional if you log request bodies
                                audit.setRequestTimestamp(LocalDateTime.now());
                                //audit.setEndPoint(request.url().toString());
                                long id;
                                if (auditService != null) {
                                    audit = auditService.saveClientAudit(audit);
                                    id = audit.getId();
                                } else {
                                    id = 0;
                                }
                                return id;
                            } catch (Exception e) {
                                System.err.println("Audit save failed: " + e.getMessage());
                                return -1L;
                            }
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(id ->
                                Mono.just(request)
                                        .contextWrite(context -> context.put("auditId", id))
                        )
        ).andThen((request, next) -> {
            LocalDateTime startTime = LocalDateTime.now();
            LOGGER.info("======auditFilter andThen entry point======");
            return next.exchange(request)
                    .flatMap(response -> processResponse(response, startTime));
            //.onErrorResume(e -> handleError(e, startTime));
        });
    }

    private Mono<ClientResponse> processResponse(ClientResponse response, LocalDateTime startTime) {
        return response.bodyToMono(String.class) // Reactive reading of the body
                .flatMap(body -> {
                    LOGGER.info("======processResponse flatMap entry point======");
                    Long auditId = retrieveAuditId();
                    //ClientAudit audit = clientAuditRepository.findById(auditId).orElseThrow();
                    if (auditService != null) {
                        ClientAudit audit = auditService.findClientAuditById(auditId).orElseThrow();
                        audit.setResponseStatus(response.statusCode().value());
                        audit.setResponse(body);
                        audit.setResponseTimestamp(LocalDateTime.now());
                        auditService.saveClientAudit(audit);
                    }
                    LOGGER.info("======response======{}", response);
                    return Mono.just(response);
                });
    }

    private Mono<ClientResponse> handleError(Throwable e, LocalDateTime startTime) {
        /*LocalDateTime endTime = LocalDateTime.now();
        System.err.println("Error occurred: " + e.getMessage());
        System.err.println("Time taken: " + Duration.between(startTime, endTime));
        return Mono.error(e);*/
        LOGGER.info("======handleError entry point======");
        Long auditId = 10L;//retrieveAuditId();
        if (auditService != null) {
            ClientAudit audit = auditService.findClientAuditById(auditId).orElseThrow();
            audit.setErrorMessage(e.getMessage());
            audit.setResponseTimestamp(LocalDateTime.now());
            auditService.saveClientAudit(audit); // Update audit record
        }
        return Mono.error(e);
    }

    private Long retrieveAuditId() {
        return (Long) Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get("auditId")))
                .subscribeOn(Schedulers.boundedElastic())
                .block();
    }

    /*
    public ExchangeFilterFunction auditFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            ClientAudit audit = new ClientAudit();
            audit.setMethod(request.method().name());
            audit.setRequestHeaders(request.headers().toString());
            audit.setHostname(request.url().getHost());
            audit.setRequestBody(getBody(request)); // Optional if you log request bodies
            audit.setRequestTimestamp(LocalDateTime.now());
            audit.setEndPoint(request.url().toString());

            // Save to DB and add the saved audit ID to the context
            //ClientAudit savedAudit = clientAuditRepository.save(audit);
            long id;
            if(auditService != null){
                audit = auditService.saveClientAudit(audit);
                id = audit.getId();
            } else {
                id = 0;
            }
            return Mono.just(request)
                    .contextWrite(context -> context.put("auditId", id));
        }).andThen((request, next) -> {
            LocalDateTime startTime = LocalDateTime.now();
            return next.exchange(request)
                    .flatMap(response -> processResponse(response, startTime))
                    .onErrorResume(e -> handleError(e, startTime));
        });
    }
     */

    /*public ExchangeFilterFunction auditFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request ->
                Mono.fromCallable(() -> {
                            // Save the audit record
                            try {
                                ClientAudit audit = new ClientAudit();
                                audit.setMethod(request.method().name());
                                ClientAudit savedAudit = auditService.saveClientAuditBlocking(audit); // Blocking save
                                return savedAudit.getId(); // Return the saved audit ID
                            } catch (Exception e) {
                                // Log the error and proceed with a fallback ID
                                System.err.println("Audit save failed: " + e.getMessage());
                                return -1L; // Default value if saving fails
                            }
                        })
                        .subscribeOn(Schedulers.boundedElastic()) // Offload to a separate thread
                        .flatMap(id ->
                                Mono.just(request)
                                        .contextWrite(context -> context.put("auditId", id)) // Add auditId to context
                        )
        );
    }*/

    private Mono<ClientResponse> processResponseTmp(ClientResponse response, LocalDateTime startTime) {
        return response.bodyToMono(String.class).flatMap(body -> {
            Long auditId = retrieveAuditId();
            //ClientAudit audit = clientAuditRepository.findById(auditId).orElseThrow();
            if (auditService != null) {
                ClientAudit audit = auditService.findClientAuditById(auditId).orElseThrow();
                audit.setResponseStatus(response.statusCode().value());
                audit.setResponse(body);
                audit.setResponseTimestamp(LocalDateTime.now());
                auditService.saveClientAudit(audit);
            }
            return Mono.just(response);
        });
    }

    private Mono<ClientResponse> handleErrorTmp(Throwable e, LocalDateTime startTime) {
        Long auditId = retrieveAuditId();
        if (auditService != null) {
            ClientAudit audit = auditService.findClientAuditById(auditId).orElseThrow();
            audit.setErrorMessage(e.getMessage());
            audit.setResponseTimestamp(LocalDateTime.now());
            auditService.saveClientAudit(audit); // Update audit record
        }
        return Mono.error(e);
    }

    private Long retrieveAuditIdTmp() {
        return (Long) Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get("auditId")))
                .block(); // Retrieve audit ID from context
    }

    private String getBody(ClientRequest request) {
        // Implement request body logging if needed
        return null;
    }

    /*private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("Response status: " + clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }*/

    // Generic method to create a WebClient for any service client
    /*
    public <T> T createClient(Class<T> clientClass, String baseUrl, Map<String, String> headersMap) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl) // set the base URL for the WebClient
                .defaultHeaders(headers -> headersMap.forEach(headers::add)) // add headers to the WebClient
                .build();

        // Create a proxy client for the provided clientClass (service interface)
        return webClient.get()
                .uri(baseUrl) // Specify the uri here or pass a specific endpoint if needed
                .retrieve() // Perform the GET request (you can customize this further for other HTTP methods)
                .bodyToMono(clientClass) // Map the response body to the class type
                .block(); // Wait for the response to complete (block to get the result)
    }*/

    /*private WebClient createAuditWebClient(String url, Map<String, String> headers) {
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeaders(header -> headers.forEach(header::add))
                .build();
    }

    private WebClient createWebClient(String url, Map<String, String> headers) {
        return WebClient.builder()
                .baseUrl(url)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(new AuditClientFilter());
                })
                .defaultHeaders(header -> headers.forEach(header::add))
                .build();
    }
*/
    /*private WebClient createWebClient(String url, Map<String, String> headers) {
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeaders(header -> headers.forEach(header::add))
                .build();
    }

    public <S> S createClient(Class<S> serviceType, String url, Map<String, String> headersMap) {
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(createWebClient(url, headersMap)))
                .build()
                .createClient(serviceType);
    }*/

    /*public <S> S createAuditClient(Class<S> serviceType, String url, Map<String, String> headersMap) {
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(createAuditWebClient(url, headersMap)))
                .build()
                .createClient(serviceType);
    }*/
}
