package com.example.azuredevopsmcpserver.mcp;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class ToolRegistry {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    private final Map<String, Function<Map<String, Object>, CallToolResponse>> toolHandlers = new ConcurrentHashMap<>();

    public void registerTool(Tool tool, Function<Map<String, Object>, CallToolResponse> handler) {
        tools.put(tool.getName(), tool);
        toolHandlers.put(tool.getName(), handler);
    }

    public List<Tool> getAllTools() {
        return new ArrayList<>(tools.values());
    }

    public CallToolResponse callTool(String name, Map<String, Object> arguments) {
        Function<Map<String, Object>, CallToolResponse> handler = toolHandlers.get(name);
        if (handler == null) {
            return new CallToolResponse(
                    List.of(CallToolResponse.Content.text("Tool not found: " + name)),
                    true);
        }

        try {
            return handler.apply(arguments);
        } catch (Exception e) {
            return new CallToolResponse(
                    List.of(CallToolResponse.Content.text("Error executing tool '" + name + "': " + e.getMessage())),
                    true);
        }
    }

    public boolean hasTool(String name) {
        return tools.containsKey(name);
    }

    public Tool getTool(String name) {
        return tools.get(name);
    }
}