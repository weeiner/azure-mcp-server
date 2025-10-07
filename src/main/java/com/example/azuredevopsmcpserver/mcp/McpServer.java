package com.example.azuredevopsmcpserver.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class McpServer {

    private final ObjectMapper objectMapper;
    private final ToolRegistry toolRegistry;

    @Autowired
    public McpServer(ObjectMapper objectMapper, ToolRegistry toolRegistry) {
        this.objectMapper = objectMapper;
        this.toolRegistry = toolRegistry;
    }

    public String handleMessage(String message) {
        try {
            JsonNode messageNode = objectMapper.readTree(message);

            // Check if it's a request or notification
            if (messageNode.has("method")) {
                String method = messageNode.get("method").asText();
                Object id = messageNode.has("id") ? getIdValue(messageNode.get("id")) : null;
                JsonNode params = messageNode.get("params");

                return handleRequest(method, params, id);
            } else {
                return handleNotification(messageNode);
            }

        } catch (Exception e) {
            return createErrorResponse(null, -32700, "Parse error: " + e.getMessage());
        }
    }

    private String handleRequest(String method, JsonNode params, Object id) {
        try {
            switch (method) {
                case "initialize":
                    return handleInitialize(params, id);
                case "tools/list":
                    return handleListTools(id);
                case "tools/call":
                    return handleCallTool(params, id);
                default:
                    return createErrorResponse(id, -32601, "Method not found: " + method);
            }
        } catch (Exception e) {
            return createErrorResponse(id, -32603, "Internal error: " + e.getMessage());
        }
    }

    private String handleNotification(JsonNode messageNode) {
        try {
            String method = messageNode.get("method").asText();
            if ("initialized".equals(method)) {
                // Just acknowledge the initialized notification
                return ""; // No response for notifications
            }
            return "";
        } catch (Exception e) {
            // Notifications don't send error responses
            return "";
        }
    }

    private String handleInitialize(JsonNode params, Object id) throws IOException {
        InitializeRequest request = objectMapper.treeToValue(params, InitializeRequest.class);
        InitializeResponse response = new InitializeResponse();

        JsonRpcResponse jsonRpcResponse = new JsonRpcResponse(id, response);
        return objectMapper.writeValueAsString(jsonRpcResponse);
    }

    private String handleListTools(Object id) throws IOException {
        List<Tool> tools = toolRegistry.getAllTools();
        ListToolsResponse response = new ListToolsResponse(tools);

        JsonRpcResponse jsonRpcResponse = new JsonRpcResponse(id, response);
        return objectMapper.writeValueAsString(jsonRpcResponse);
    }

    private String handleCallTool(JsonNode params, Object id) throws IOException {
        CallToolRequest request = objectMapper.treeToValue(params, CallToolRequest.class);

        try {
            CallToolResponse response = toolRegistry.callTool(request.getName(), request.getArguments());
            JsonRpcResponse jsonRpcResponse = new JsonRpcResponse(id, response);
            return objectMapper.writeValueAsString(jsonRpcResponse);
        } catch (Exception e) {
            CallToolResponse errorResponse = new CallToolResponse(
                    List.of(CallToolResponse.Content.text("Error executing tool: " + e.getMessage())),
                    true);
            JsonRpcResponse jsonRpcResponse = new JsonRpcResponse(id, errorResponse);
            return objectMapper.writeValueAsString(jsonRpcResponse);
        }
    }

    private String createErrorResponse(Object id, int code, String message) {
        try {
            JsonRpcError error = new JsonRpcError(code, message);
            JsonRpcResponse response = new JsonRpcResponse(id, error);
            return objectMapper.writeValueAsString(response);
        } catch (IOException e) {
            return "{\"jsonrpc\":\"2.0\",\"id\":" + id + ",\"error\":{\"code\":-32603,\"message\":\"Internal error\"}}";
        }
    }

    private Object getIdValue(JsonNode idNode) {
        if (idNode.isNumber()) {
            return idNode.asInt();
        } else if (idNode.isTextual()) {
            return idNode.asText();
        } else {
            return null;
        }
    }
}