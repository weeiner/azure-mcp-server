package com.example.azuredevopsmcpserver.azuredevops;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class AzureDevOpsConfig {

    @Value("${azure.devops.organization:}")
    private String organization;

    @Value("${azure.devops.pat:}")
    private String personalAccessToken;

    @Bean
    public WebClient azureDevOpsWebClient() {
        WebClient.Builder builder = WebClient.builder();

        if (organization != null && !organization.isEmpty()) {
            builder.baseUrl("https://dev.azure.com/" + organization);
        }

        if (personalAccessToken != null && !personalAccessToken.isEmpty()) {
            String encodedToken = Base64.getEncoder()
                    .encodeToString((":" + personalAccessToken).getBytes(StandardCharsets.UTF_8));

            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedToken);
        }

        builder.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        builder.defaultHeader(HttpHeaders.ACCEPT, "application/json");

        return builder.build();
    }
}