package com.web.audit.config.client;

import com.web.audit.filters.AuditExchangeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Map;

@Configuration
public class CommonWebClient {

    @Autowired
    private AuditExchangeFilter auditExchangeFilter; // Inject the audit filter

    public WebClient creataWebClient(String baseUrl, Map<String, String> headers) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(auditExchangeFilter);
        headers.forEach(builder::defaultHeader);
        return builder.build();
    }

    public <T> T httpServiceProxyFactory(String baseUrl, Map<String, String> headers, Class<T> clientClass) {
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(creataWebClient(baseUrl, headers)))
                .build()
                .createClient(clientClass);
    }

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
