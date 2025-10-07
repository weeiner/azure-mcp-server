package com.example.azuredevopsmcpserver.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListToolsResponse {
    @JsonProperty("tools")
    private List<Tool> tools;

    public ListToolsResponse() {
    }

    public ListToolsResponse(List<Tool> tools) {
        this.tools = tools;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }
}