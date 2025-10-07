package com.example.azuredevopsmcpserver.tools;

import com.example.azuredevopsmcpserver.azuredevops.AzureDevOpsService;
import com.example.azuredevopsmcpserver.azuredevops.model.AzureDevOpsProject;
import com.example.azuredevopsmcpserver.azuredevops.model.WorkItem;
import com.example.azuredevopsmcpserver.mcp.*;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AzureDevOpsTools {

    private final ToolRegistry toolRegistry;
    private final AzureDevOpsService azureDevOpsService;

    public AzureDevOpsTools(ToolRegistry toolRegistry, AzureDevOpsService azureDevOpsService) {
        this.toolRegistry = toolRegistry;
        this.azureDevOpsService = azureDevOpsService;
    }

    @PostConstruct
    public void registerTools() {
        registerListProjectsTool();
        registerGetProjectTool();
        registerGetWorkItemsTool();
        registerGetWorkItemTool();
        registerGetRepositoriesTool();
        registerGetBuildsTool();
    }

    private void registerListProjectsTool() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(),
                "required", List.of());

        Tool tool = new Tool("list_projects", "List all Azure DevOps projects in the organization", schema);

        toolRegistry.registerTool(tool, arguments -> {
            try {
                List<AzureDevOpsProject> projects = azureDevOpsService.getProjects();

                StringBuilder result = new StringBuilder();
                result.append("Azure DevOps Projects:\n\n");

                for (AzureDevOpsProject project : projects) {
                    result.append(String.format("• **%s** (ID: %s)\n", project.getName(), project.getId()));
                    if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                        result.append(String.format("  Description: %s\n", project.getDescription()));
                    }
                    result.append(String.format("  State: %s | Visibility: %s\n",
                            project.getState(), project.getVisibility()));
                    result.append("\n");
                }

                if (projects.isEmpty()) {
                    result.append("No projects found or access denied.");
                }

                return new CallToolResponse(List.of(CallToolResponse.Content.text(result.toString())));
            } catch (Exception e) {
                return new CallToolResponse(
                        List.of(CallToolResponse.Content.text("Error listing projects: " + e.getMessage())),
                        true);
            }
        });
    }

    private void registerGetProjectTool() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "project", Map.of(
                                "type", "string",
                                "description", "Project name or ID")),
                "required", List.of("project"));

        Tool tool = new Tool("get_project", "Get detailed information about a specific Azure DevOps project", schema);

        toolRegistry.registerTool(tool, arguments -> {
            try {
                String project = (String) arguments.get("project");
                if (project == null || project.trim().isEmpty()) {
                    return new CallToolResponse(
                            List.of(CallToolResponse.Content.text("Project parameter is required")),
                            true);
                }

                AzureDevOpsProject projectInfo = azureDevOpsService.getProject(project);

                StringBuilder result = new StringBuilder();
                result.append(String.format("**%s**\n\n", projectInfo.getName()));
                result.append(String.format("**ID:** %s\n", projectInfo.getId()));

                if (projectInfo.getDescription() != null && !projectInfo.getDescription().isEmpty()) {
                    result.append(String.format("**Description:** %s\n", projectInfo.getDescription()));
                }

                result.append(String.format("**State:** %s\n", projectInfo.getState()));
                result.append(String.format("**Visibility:** %s\n", projectInfo.getVisibility()));

                if (projectInfo.getUrl() != null) {
                    result.append(String.format("**URL:** %s\n", projectInfo.getUrl()));
                }

                return new CallToolResponse(List.of(CallToolResponse.Content.text(result.toString())));
            } catch (Exception e) {
                return new CallToolResponse(
                        List.of(CallToolResponse.Content.text("Error getting project: " + e.getMessage())),
                        true);
            }
        });
    }

    private void registerGetWorkItemsTool() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "project", Map.of(
                                "type", "string",
                                "description", "Project name or ID"),
                        "wiql", Map.of(
                                "type", "string",
                                "description",
                                "Work Item Query Language (WIQL) query. If not provided, gets recent work items.")),
                "required", List.of("project"));

        Tool tool = new Tool("get_work_items", "Query work items from an Azure DevOps project using WIQL", schema);

        toolRegistry.registerTool(tool, arguments -> {
            try {
                String project = (String) arguments.get("project");
                String wiql = (String) arguments.get("wiql");

                if (project == null || project.trim().isEmpty()) {
                    return new CallToolResponse(
                            List.of(CallToolResponse.Content.text("Project parameter is required")),
                            true);
                }

                if (wiql == null || wiql.trim().isEmpty()) {
                    wiql = "SELECT [System.Id], [System.Title], [System.State], [System.WorkItemType], [System.AssignedTo] FROM WorkItems WHERE [System.TeamProject] = '"
                            + project + "' ORDER BY [System.ChangedDate] DESC";
                }

                List<WorkItem> workItems = azureDevOpsService.getWorkItems(project, wiql);

                StringBuilder result = new StringBuilder();
                result.append(String.format("Work Items in **%s**:\n\n", project));

                for (WorkItem workItem : workItems) {
                    result.append(String.format("• **#%d** %s\n", workItem.getId(), workItem.getTitle()));
                    result.append(String.format("  Type: %s | State: %s\n",
                            workItem.getWorkItemType(), workItem.getState()));
                    if (workItem.getAssignedTo() != null) {
                        result.append(String.format("  Assigned to: %s\n", workItem.getAssignedTo()));
                    }
                    result.append("\n");
                }

                if (workItems.isEmpty()) {
                    result.append("No work items found matching the query.");
                }

                return new CallToolResponse(List.of(CallToolResponse.Content.text(result.toString())));
            } catch (Exception e) {
                return new CallToolResponse(
                        List.of(CallToolResponse.Content.text("Error querying work items: " + e.getMessage())),
                        true);
            }
        });
    }

    private void registerGetWorkItemTool() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "project", Map.of(
                                "type", "string",
                                "description", "Project name or ID"),
                        "id", Map.of(
                                "type", "integer",
                                "description", "Work item ID")),
                "required", List.of("project", "id"));

        Tool tool = new Tool("get_work_item", "Get detailed information about a specific work item", schema);

        toolRegistry.registerTool(tool, arguments -> {
            try {
                String project = (String) arguments.get("project");
                Object idObj = arguments.get("id");

                if (project == null || project.trim().isEmpty()) {
                    return new CallToolResponse(
                            List.of(CallToolResponse.Content.text("Project parameter is required")),
                            true);
                }

                if (idObj == null) {
                    return new CallToolResponse(
                            List.of(CallToolResponse.Content.text("Work item ID parameter is required")),
                            true);
                }

                int workItemId = (idObj instanceof Integer) ? (Integer) idObj : Integer.parseInt(idObj.toString());

                WorkItem workItem = azureDevOpsService.getWorkItem(project, workItemId);

                StringBuilder result = new StringBuilder();
                result.append(String.format("**Work Item #%d**\n\n", workItem.getId()));
                result.append(String.format("**Title:** %s\n", workItem.getTitle()));
                result.append(String.format("**Type:** %s\n", workItem.getWorkItemType()));
                result.append(String.format("**State:** %s\n", workItem.getState()));

                if (workItem.getAssignedTo() != null) {
                    result.append(String.format("**Assigned To:** %s\n", workItem.getAssignedTo()));
                }

                result.append(String.format("**Revision:** %d\n", workItem.getRev()));

                if (workItem.getUrl() != null) {
                    result.append(String.format("**URL:** %s\n", workItem.getUrl()));
                }

                return new CallToolResponse(List.of(CallToolResponse.Content.text(result.toString())));
            } catch (Exception e) {
                return new CallToolResponse(
                        List.of(CallToolResponse.Content.text("Error getting work item: " + e.getMessage())),
                        true);
            }
        });
    }

    private void registerGetRepositoriesTool() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "project", Map.of(
                                "type", "string",
                                "description", "Project name or ID")),
                "required", List.of("project"));

        Tool tool = new Tool("get_repositories", "List Git repositories in an Azure DevOps project", schema);

        toolRegistry.registerTool(tool, arguments -> {
            try {
                String project = (String) arguments.get("project");
                if (project == null || project.trim().isEmpty()) {
                    return new CallToolResponse(
                            List.of(CallToolResponse.Content.text("Project parameter is required")),
                            true);
                }

                List<Object> repositories = azureDevOpsService.getRepositories(project);

                StringBuilder result = new StringBuilder();
                result.append(String.format("Git Repositories in **%s**:\n\n", project));

                for (Object repoObj : repositories) {
                    if (repoObj instanceof Map) {
                        Map<?, ?> repo = (Map<?, ?>) repoObj;
                        String name = (String) repo.get("name");
                        String id = (String) repo.get("id");
                        String webUrl = (String) repo.get("webUrl");
                        String defaultBranch = (String) repo.get("defaultBranch");

                        result.append(String.format("• **%s**\n", name));
                        result.append(String.format("  ID: %s\n", id));
                        if (defaultBranch != null) {
                            result.append(String.format("  Default Branch: %s\n", defaultBranch));
                        }
                        if (webUrl != null) {
                            result.append(String.format("  URL: %s\n", webUrl));
                        }
                        result.append("\n");
                    }
                }

                if (repositories.isEmpty()) {
                    result.append("No repositories found.");
                }

                return new CallToolResponse(List.of(CallToolResponse.Content.text(result.toString())));
            } catch (Exception e) {
                return new CallToolResponse(
                        List.of(CallToolResponse.Content.text("Error getting repositories: " + e.getMessage())),
                        true);
            }
        });
    }

    private void registerGetBuildsTool() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "project", Map.of(
                                "type", "string",
                                "description", "Project name or ID"),
                        "top", Map.of(
                                "type", "integer",
                                "description", "Maximum number of builds to return (default: 10)")),
                "required", List.of("project"));

        Tool tool = new Tool("get_builds", "Get recent builds from an Azure DevOps project", schema);

        toolRegistry.registerTool(tool, arguments -> {
            try {
                String project = (String) arguments.get("project");
                Object topObj = arguments.get("top");

                if (project == null || project.trim().isEmpty()) {
                    return new CallToolResponse(
                            List.of(CallToolResponse.Content.text("Project parameter is required")),
                            true);
                }

                Integer top = 10; // default
                if (topObj != null) {
                    top = (topObj instanceof Integer) ? (Integer) topObj : Integer.parseInt(topObj.toString());
                }

                List<Object> builds = azureDevOpsService.getBuilds(project, top);

                StringBuilder result = new StringBuilder();
                result.append(String.format("Recent Builds in **%s**:\n\n", project));

                for (Object buildObj : builds) {
                    if (buildObj instanceof Map) {
                        Map<?, ?> build = (Map<?, ?>) buildObj;
                        String buildNumber = (String) build.get("buildNumber");
                        String status = (String) build.get("status");
                        String result_field = (String) build.get("result");
                        String sourceBranch = (String) build.get("sourceBranch");
                        String queueTime = (String) build.get("queueTime");

                        result.append(String.format("• **Build %s**\n", buildNumber));
                        result.append(String.format("  Status: %s", status));
                        if (result_field != null) {
                            result.append(String.format(" | Result: %s", result_field));
                        }
                        result.append("\n");

                        if (sourceBranch != null) {
                            result.append(String.format("  Branch: %s\n", sourceBranch));
                        }
                        if (queueTime != null) {
                            result.append(String.format("  Queued: %s\n", queueTime));
                        }
                        result.append("\n");
                    }
                }

                if (builds.isEmpty()) {
                    result.append("No builds found.");
                }

                return new CallToolResponse(List.of(CallToolResponse.Content.text(result.toString())));
            } catch (Exception e) {
                return new CallToolResponse(
                        List.of(CallToolResponse.Content.text("Error getting builds: " + e.getMessage())),
                        true);
            }
        });
    }
}