package com.example.azuredevopsmcpserver.azuredevops;

import com.example.azuredevopsmcpserver.azuredevops.model.AzureDevOpsProject;
import com.example.azuredevopsmcpserver.azuredevops.model.WorkItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class AzureDevOpsService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AzureDevOpsService(WebClient azureDevOpsWebClient, ObjectMapper objectMapper) {
        this.webClient = azureDevOpsWebClient;
        this.objectMapper = objectMapper;
    }

    public List<AzureDevOpsProject> getProjects() {
        try {
            JsonNode response = webClient.get()
                    .uri("/_apis/projects?api-version=7.1-preview.4")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("value")) {
                return objectMapper.convertValue(
                        response.get("value"),
                        new TypeReference<List<AzureDevOpsProject>>() {
                        });
            }

            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch projects: " + e.getMessage(), e);
        }
    }

    public AzureDevOpsProject getProject(String projectIdOrName) {
        try {
            return webClient.get()
                    .uri("/_apis/projects/{project}?api-version=7.1-preview.4", projectIdOrName)
                    .retrieve()
                    .bodyToMono(AzureDevOpsProject.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch project '" + projectIdOrName + "': " + e.getMessage(), e);
        }
    }

    public List<WorkItem> getWorkItems(String projectIdOrName, String wiql) {
        try {
            // First, execute the WIQL query to get work item IDs
            Map<String, String> queryBody = Map.of("query", wiql);

            JsonNode queryResponse = webClient.post()
                    .uri("/{project}/_apis/wit/wiql?api-version=7.1-preview.2", projectIdOrName)
                    .bodyValue(queryBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (queryResponse == null || !queryResponse.has("workItems")) {
                return List.of();
            }

            JsonNode workItemsNode = queryResponse.get("workItems");
            if (workItemsNode.size() == 0) {
                return List.of();
            }

            // Extract IDs
            StringBuilder idsBuilder = new StringBuilder();
            for (int i = 0; i < workItemsNode.size(); i++) {
                if (i > 0)
                    idsBuilder.append(",");
                idsBuilder.append(workItemsNode.get(i).get("id").asText());
            }

            // Fetch full work item details
            JsonNode workItemsResponse = webClient.get()
                    .uri("/{project}/_apis/wit/workitems?ids={ids}&api-version=7.1-preview.3",
                            projectIdOrName, idsBuilder.toString())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (workItemsResponse != null && workItemsResponse.has("value")) {
                return objectMapper.convertValue(
                        workItemsResponse.get("value"),
                        new TypeReference<List<WorkItem>>() {
                        });
            }

            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch work items: " + e.getMessage(), e);
        }
    }

    public WorkItem getWorkItem(String projectIdOrName, int workItemId) {
        try {
            return webClient.get()
                    .uri("/{project}/_apis/wit/workitems/{id}?api-version=7.1-preview.3", projectIdOrName, workItemId)
                    .retrieve()
                    .bodyToMono(WorkItem.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch work item " + workItemId + ": " + e.getMessage(), e);
        }
    }

    public List<Object> getRepositories(String projectIdOrName) {
        try {
            JsonNode response = webClient.get()
                    .uri("/{project}/_apis/git/repositories?api-version=7.1-preview.1", projectIdOrName)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("value")) {
                return objectMapper.convertValue(
                        response.get("value"),
                        new TypeReference<List<Object>>() {
                        });
            }

            return List.of();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch repositories for project '" + projectIdOrName + "': " + e.getMessage(), e);
        }
    }

    public List<Object> getBuilds(String projectIdOrName, Integer top) {
        try {
            String uri = "/{project}/_apis/build/builds?api-version=7.1-preview.7";
            if (top != null && top > 0) {
                uri += "&$top=" + top;
            }

            JsonNode response = webClient.get()
                    .uri(uri, projectIdOrName)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("value")) {
                return objectMapper.convertValue(
                        response.get("value"),
                        new TypeReference<List<Object>>() {
                        });
            }

            return List.of();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch builds for project '" + projectIdOrName + "': " + e.getMessage(), e);
        }
    }
}